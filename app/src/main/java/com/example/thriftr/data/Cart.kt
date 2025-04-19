package com.example.thriftr.data

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: String,
    val quantity: Int,
    val selectedSize: String?,
    val selectedColor: String?
)

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedSize: String? = null,
    val selectedColor: String? = null
)


