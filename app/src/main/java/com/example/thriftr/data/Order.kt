package com.example.thriftr.data

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Order(
    val id: String = UUID.randomUUID().toString(),
    val total: Double,
    val status: String,
    val items: List<OrderItem>,
    val createdAt: Long,
    val shippingAddress: String
)

@Serializable
data class OrderItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int
)