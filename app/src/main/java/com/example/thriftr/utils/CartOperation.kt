package com.example.thriftr.utils

import com.example.thriftr.data.CartItem

sealed class CartOperation {
    data object Loading : CartOperation()
    data class Success(val items: List<CartItem>) : CartOperation()
    data class Error(val message: String) : CartOperation()
}