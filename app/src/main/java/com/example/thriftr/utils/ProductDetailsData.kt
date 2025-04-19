package com.example.thriftr.utils

import com.example.thriftr.data.Product

data class ProductDetailsData(
    val product: Product,
    val isAddedToCart: Boolean,
    val selectedSize: String?,
    val selectedColor: String?
)