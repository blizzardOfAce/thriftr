package com.example.thriftr.repository

import android.util.Log
import com.example.thriftr.data.Product
import com.example.thriftr.data.toProduct
import com.example.thriftr.utils.AppWriteConstants.DATABASE_ID
import com.example.thriftr.utils.AppWriteConstants.PRODUCT_COLLECTION_ID
import com.example.thriftr.utils.AppWriteConstants.WISHLIST_COLLECTION_ID
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.services.Databases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor(
    client: Client,
    private val authRepository: AuthRepository
) {
    private val database = Databases(client)

    // Caching layer
    private val productsCache = mutableMapOf<String, Product>()
    private val wishlistCache = mutableSetOf<String>()

    private val _operationInProgress = MutableStateFlow(false)
    val operationInProgress: StateFlow<Boolean> = _operationInProgress.asStateFlow()

    val userId: String?
        get() = authRepository.authState.value?.id

    suspend fun getWishlistItems(): List<Product> {
        val userId = userId ?: return emptyList()
        try {
            val wishlistDocs = database.listDocuments(
                databaseId = DATABASE_ID,
                collectionId = WISHLIST_COLLECTION_ID,
                queries = listOf(Query.equal("userId", userId))
            )

            val productIds = wishlistDocs.documents
                .mapNotNull { it.data["productId"] as? String }
                .distinct()

            wishlistCache.clear()
            wishlistCache.addAll(productIds)

            return productIds.mapNotNull { id ->
                productsCache[id] ?: fetchProductDetailsFromId(id)?.also {
                    productsCache[id] = it
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to load wishlist: ${e.message}")
        }
    }

    suspend fun fetchProductDetailsFromId(productId: String): Product? {
        return try {
            val response = database.getDocument(
                databaseId = DATABASE_ID,
                collectionId = PRODUCT_COLLECTION_ID,
                documentId = productId
            )
            response.toProduct().also { productsCache[productId] = it }
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Fetch Product details from Id Error: ${e.message}", e)
            null
        }
    }

    suspend fun addToWishlist(product: Product): Boolean {
        val userId = userId ?: return false
        if (_operationInProgress.value || wishlistCache.contains(product.id)) return false
        _operationInProgress.value = true

        return try {
            val existingDocs = database.listDocuments(
                databaseId = DATABASE_ID,
                collectionId = WISHLIST_COLLECTION_ID,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.equal("productId", product.id)
                )
            )

            if (existingDocs.documents.isEmpty()) {
                database.createDocument(
                    databaseId = DATABASE_ID,
                    collectionId = WISHLIST_COLLECTION_ID,
                    documentId = ID.unique(),
                    data = mapOf("userId" to userId, "productId" to product.id)
                )
                productsCache[product.id] = product
                wishlistCache.add(product.id)
            }
            true
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Add to Wishlist Error: ${e.message}", e)
            false
        } finally {
            _operationInProgress.value = false
        }
    }

    suspend fun removeFromWishlist(productId: String): Boolean {
        val userId = userId ?: return false
        if (_operationInProgress.value) return false
        _operationInProgress.value = true

        return try {
            val existingDocs = database.listDocuments(
                databaseId = DATABASE_ID,
                collectionId = WISHLIST_COLLECTION_ID,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.equal("productId", productId)
                )
            )

            if (existingDocs.documents.isNotEmpty()) {
                val documentId = existingDocs.documents.first().id
                database.deleteDocument(
                    databaseId = DATABASE_ID,
                    collectionId = WISHLIST_COLLECTION_ID,
                    documentId = documentId
                )
                wishlistCache.remove(productId)
                productsCache.remove(productId)
            }
            true
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Remove from Wishlist Error: ${e.message}", e)
        false
        } finally {
            _operationInProgress.value = false
        }
    }

    suspend fun clearWishlist(): Boolean {
        val userId = userId ?: return false
        return try {
            val existingDocs = database.listDocuments(
                databaseId = DATABASE_ID,
                collectionId = WISHLIST_COLLECTION_ID,
                queries = listOf(Query.equal("userId", userId))
            )

            existingDocs.documents.forEach { doc ->
                database.deleteDocument(
                    databaseId = DATABASE_ID,
                    collectionId = WISHLIST_COLLECTION_ID,
                    documentId = doc.id
                )
            }
            productsCache.clear()
            wishlistCache.clear()
            true
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Clear Wishlist Error: ${e.message}", e)
            false
        }
    }

    fun clearCache() {
        productsCache.clear()
        wishlistCache.clear()
    }
}


