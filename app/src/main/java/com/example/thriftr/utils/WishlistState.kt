package com.example.thriftr.utils

import com.example.thriftr.data.Product

sealed class WishlistState {
    data object Loading : WishlistState()
    data class Success(val items: List<Product>) : WishlistState()
    data class Error(val message: String) : WishlistState()
}
