package com.example.thriftr.viewModel

import androidx.lifecycle.ViewModel
import com.example.thriftr.utils.ProductDetailsData
import com.example.thriftr.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProductDetailsData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize

    private val _selectedColor = MutableStateFlow<String?>(null)
    val selectedColor: StateFlow<String?> = _selectedColor

    fun updateSelectedSize(size: String) {
        _selectedSize.value = size
    }

    fun updateSelectedColor(color: String) {
        _selectedColor.value = color
    }
}
