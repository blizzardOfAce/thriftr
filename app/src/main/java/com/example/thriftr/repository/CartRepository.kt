package com.example.thriftr.repository

import android.util.Log
import com.example.thriftr.data.CartItem
import com.example.thriftr.data.Product
import com.example.thriftr.data.toProduct
import com.example.thriftr.utils.AppWriteConstants.CART_COLLECTION_ID
import com.example.thriftr.utils.AppWriteConstants.DATABASE_ID
import com.example.thriftr.utils.AppWriteConstants.PRODUCT_COLLECTION_ID
import com.example.thriftr.utils.CartOperation
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import io.appwrite.services.Databases
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CartRepository @Inject constructor(
    client: Client
) {
    private val database = Databases(client)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCartWithProducts(userId: String): CartOperation {
        return try {
            val cartDoc = getCartDocument(userId)

            val cartItems = cartDoc?.getCartItems() ?: emptyList()

            CartOperation.Success(cartItems)
        } catch (e: Exception) {
            Log.e("CartRepository", "Error fetching cart: ${e.message}", e)
            CartOperation.Error(e.parseErrorMessage())
        }
    }

    suspend fun getProduct(productId: String): Product? {
        return try {
            val document = database.getDocument(DATABASE_ID, PRODUCT_COLLECTION_ID, productId)
            document.toProduct()
        } catch (e: Exception) {
            Log.e("CartRepository", "Error getting product: ${e.message}")
            null
        }
    }

    suspend fun updateCartItem(
        userId: String,
        productId: String,
        quantity: Int,
        selectedSize: String?,
        selectedColor: String?
    ): CartOperation {
        return try {
            val cartDoc = getCartDocument(userId)
            val cartItems = cartDoc?.getCartItems()?.toMutableList() ?: mutableListOf()

            val itemKey = generateItemKey(productId, selectedSize, selectedColor)
            val existingIndex = cartItems.indexOfFirst {
                generateItemKey(it.productId, it.selectedSize, it.selectedColor) == itemKey
            }

            if (existingIndex != -1) {
                if (quantity > 0) {
                    cartItems[existingIndex] = cartItems[existingIndex].copy(quantity = quantity)
                } else {
                    cartItems.removeAt(existingIndex)
                }
            } else {
                if (quantity > 0) {
                    cartItems.add(CartItem(productId, quantity, selectedSize, selectedColor)) // ✅ Add new item
                }
            }

            updateCartDocument(userId, cartItems)

            CartOperation.Success(cartItems)
        } catch (e: Exception) {
            CartOperation.Error(e.parseErrorMessage())
        }
    }

    // Helper function to generate consistent item keys
    private fun generateItemKey(productId: String, size: String?, color: String?): String {
        return "$productId-${size.orEmpty()}-${color.orEmpty()}"
    }

    private suspend fun getCartDocument(userId: String): Document<Map<String, Any>>? {
        return database.listDocuments(
            DATABASE_ID,
            CART_COLLECTION_ID,
            queries = listOf(Query.equal("userId", userId))
        ).documents.firstOrNull()
    }

    private suspend fun updateCartDocument(userId: String, items: List<CartItem>) {
        val cartDoc = getCartDocument(userId)

        val cartData = mapOf(
            "userId" to userId,
            "products" to items.map { json.encodeToString(it) } // ✅ Store as List<String>
        )

        if (items.isEmpty()) {
            cartDoc?.let {
                database.deleteDocument(DATABASE_ID, CART_COLLECTION_ID, it.id)
            }
        } else {
            cartDoc?.let {
                database.updateDocument(DATABASE_ID, CART_COLLECTION_ID, it.id, cartData)
            } ?: run {
                database.createDocument(
                    DATABASE_ID,
                    CART_COLLECTION_ID,
                    ID.unique(),
                    cartData
                )
            }
        }
    }

    suspend fun removeFromCart(userId: String, productId: String, selectedSize: String?, selectedColor: String?): CartOperation {
        return try {
            val cartDoc = getCartDocument(userId)
            val cartItems = cartDoc?.getCartItems()?.toMutableList() ?: mutableListOf()

            // Find item index based on productId + size + color
            val itemKey = generateItemKey(productId, selectedSize, selectedColor)
            val existingIndex = cartItems.indexOfFirst {
                generateItemKey(it.productId, it.selectedSize, it.selectedColor) == itemKey
            }

            if (existingIndex != -1) {
                cartItems.removeAt(existingIndex)
                updateCartDocument(userId, cartItems)
            }

            CartOperation.Success(cartItems)
        } catch (e: Exception) {
            CartOperation.Error(e.parseErrorMessage())
        }
    }

    suspend fun getProducts(productIds: Set<String>): Map<String, Product> {
        return try {
            val products = productIds.mapNotNull { productId ->
                getProduct(productId)?.let { product -> productId to product }
            }.toMap()
            products
        } catch (e: Exception) {
            Log.e("CartRepository", "Error fetching products: ${e.parseErrorMessage()}")
            emptyMap()
        }
    }

    suspend fun clearCart(userId: String) {
        val cartDoc = getCartDocument(userId)
        cartDoc?.let {
            database.deleteDocument(DATABASE_ID, CART_COLLECTION_ID, it.id)
        }
    }

    private fun Document<Map<String, Any>>.getCartItems(): List<CartItem> {
        return try {
            val rawProducts = (data["products"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()

            rawProducts.mapNotNull { jsonString ->
                try {
                    json.decodeFromString<CartItem>(jsonString) // ✅ Properly decode JSON string
                } catch (e: Exception) {
                    Log.e("CartRepository", "Error parsing cart item: ${e.message}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Error decoding cart items: ${e.message}", e)
            emptyList()
        }
    }

    private fun Exception.parseErrorMessage(): String {
        return when (this) {
            is AppwriteException -> message ?: "Appwrite error"
            else -> localizedMessage ?: "Unknown error"
        }
    }
}

