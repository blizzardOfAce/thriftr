package com.example.thriftr.repository

import android.util.Log
import com.example.thriftr.data.Order
import com.example.thriftr.utils.AppWriteConstants.DATABASE_ID
import com.example.thriftr.utils.AppWriteConstants.ORDER_COLLECTION_ID
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    client: Client
) {
    private val database = Databases(client)

    suspend fun fetchOrders(userId: String): List<Order> {
        return try {
            val response = database.getDocument(
                databaseId = DATABASE_ID,
                collectionId = ORDER_COLLECTION_ID,
                documentId = userId
            )
            val orderJsonList = (response.data["orders"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

            orderJsonList.map { Json.decodeFromString(it) }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error fetching orders: ${e.message}")
            emptyList()
        }
    }

    suspend fun saveOrder(order: Order, userId: String) {
        val newOrderJson = Json.encodeToString(order)

        try {
            // First check if document exists
            try {
                val existingDoc = database.getDocument(
                    databaseId = DATABASE_ID,
                    collectionId = ORDER_COLLECTION_ID,
                    documentId = userId
                )

                // Document exists, update it
                val existingOrders = (existingDoc.data["orders"] as? List<*>)
                    ?.filterIsInstance<String>() ?: emptyList()
                val updatedOrders = existingOrders + newOrderJson

                database.updateDocument(
                    databaseId = DATABASE_ID,
                    collectionId = ORDER_COLLECTION_ID,
                    documentId = userId,
                    data = mapOf("orders" to updatedOrders)
                )

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error saving order: ${e.message}")

                // Create document with initial order
                database.createDocument(
                    databaseId = DATABASE_ID,
                    collectionId = ORDER_COLLECTION_ID,
                    documentId = userId,
                    data = mapOf("orders" to listOf(newOrderJson))
                )

            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Failed to save order: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateOrder(updatedOrder: Order, userId: String) {
        try {
            // Get the existing document
            val existingDoc = database.getDocument(
                databaseId = DATABASE_ID,
                collectionId = ORDER_COLLECTION_ID,
                documentId = userId
            )

            // Get and parse existing orders
            val existingOrdersJson = (existingDoc.data["orders"] as? List<*>)
                ?.filterIsInstance<String>() ?: emptyList()
            val existingOrders = existingOrdersJson.map { Json.decodeFromString<Order>(it) }

            // Find the order to update by its ID
            val updatedOrders = existingOrders.map {
                if (it.id == updatedOrder.id) updatedOrder else it
            }

            // Convert back to JSON strings
            val updatedOrdersJson = updatedOrders.map { Json.encodeToString(it) }

            // Update the document
            database.updateDocument(
                databaseId = DATABASE_ID,
                collectionId = ORDER_COLLECTION_ID,
                documentId = userId,
                data = mapOf("orders" to updatedOrdersJson)
            )

        } catch (e: Exception) {
            Log.e("OrderRepository", "Failed to update order: ${e.message}")
            throw e
        }
    }
}
