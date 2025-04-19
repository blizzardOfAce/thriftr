package com.example.thriftr.utils

import com.example.thriftr.data.User

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}