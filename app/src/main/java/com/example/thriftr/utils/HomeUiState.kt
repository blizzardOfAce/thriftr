package com.example.thriftr.utils

import com.example.thriftr.data.Product

data class HomeScreenData(
    val products: List<Product>,
    val bestDeals: List<Product>
)
