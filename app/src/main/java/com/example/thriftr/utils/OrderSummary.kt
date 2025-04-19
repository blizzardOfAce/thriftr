package com.example.thriftr.utils

import com.example.thriftr.data.CartProduct

data class OrderSummary(
    val subtotal: Double,
    val tax: Double,
    val shipping: Double,
    val total: Double
)

fun calculateOrderSummary(cartItems: List<CartProduct>): OrderSummary {
    val subtotal = cartItems.sumOf { it.product.price.toDouble() * it.quantity }
    val tax = subtotal * 0.18
    val shipping = 50.0
    val total = subtotal + tax + shipping
    return OrderSummary(subtotal, tax, shipping, total)
}

