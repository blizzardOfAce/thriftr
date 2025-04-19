package com.example.thriftr.viewModel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thriftr.data.Product
import com.example.thriftr.repository.WishlistRepository
import com.example.thriftr.utils.WishlistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val repository: WishlistRepository
) : ViewModel() {

    private val _state = MutableStateFlow<WishlistState>(WishlistState.Loading)
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    private val _itemVisibility = mutableStateMapOf<String, Boolean>()
    val itemVisibility: Map<String, Boolean> get() = _itemVisibility

    private val removedItems = mutableMapOf<String, Product>()
    private val deletionJobs = mutableMapOf<String, Job>()

    fun fetchWishlistIfNeeded() {
        if (_state.value !is WishlistState.Success) {
            fetchWishlistItems()
        }
    }

    fun fetchWishlistItems() {
        viewModelScope.launch {
            _state.value = WishlistState.Loading
            try {
                val items = repository.getWishlistItems()
                items.forEach { _itemVisibility[it.id] = true }
                _state.value = WishlistState.Success(items)
            } catch (e: Exception) {
                _state.value = WishlistState.Error(e.message ?: "Failed to load wishlist")
            }
        }
    }

    fun addToWishlist(product: Product) {
        viewModelScope.launch {
            if (repository.addToWishlist(product)) {
                fetchWishlistItems()
            }
        }
    }

    fun removeFromWishlist(
        productId: String,
        onShowSnackbar: (String, String, suspend () -> Unit) -> Unit
    ) {
        _itemVisibility[productId] = false // Triggers exit animation

        val currentState = _state.value
        if (currentState is WishlistState.Success) {
            val removedProduct = currentState.items.find { it.id == productId } ?: return
            removedItems[productId] = removedProduct
            val updatedList = currentState.items.filterNot { it.id == productId }
            _state.value = WishlistState.Success(updatedList)

            // Show Snackbar with Undo
            onShowSnackbar("Item removed", "Undo") {
                undoRemoveFromWishlist(productId)
            }

            // Schedule actual deletion after delay
            val job = viewModelScope.launch {
                delay(4000) // Wait for undo
                if (removedItems.containsKey(productId)) {
                    repository.removeFromWishlist(productId)
                    removedItems.remove(productId) // Finalize deletion
                }
                deletionJobs.remove(productId)
            }

            deletionJobs[productId] = job
        }
    }

    private fun undoRemoveFromWishlist(productId: String) {
        val product = removedItems[productId] ?: return

        // Cancel pending deletion
        deletionJobs[productId]?.cancel()
        deletionJobs.remove(productId)

        // Restore product
        val currentState = _state.value
        if (currentState is WishlistState.Success) {
            _state.value = WishlistState.Success(currentState.items + product)
            _itemVisibility[productId] = true
        }

        removedItems.remove(productId)
    }

    fun clearWishlist() {
        viewModelScope.launch {
            try {
                val result = repository.clearWishlist()
                if (result) {
                    _state.value = WishlistState.Success(emptyList())
                    _itemVisibility.clear()
                    removedItems.clear()
                    deletionJobs.values.forEach { it.cancel() }
                    deletionJobs.clear()
                } else {
                    _state.value = WishlistState.Error("Failed to clear wishlist")
                }
            } catch (e: Exception) {
                _state.value = WishlistState.Error(e.message ?: "Failed to clear wishlist")
            }
        }
    }

    fun fetchProductDetailsFromId(productId: String) {
        viewModelScope.launch {
            try {
                val product = repository.fetchProductDetailsFromId(productId)
                _state.value = WishlistState.Success(listOf(product ?: Product(
                    id = "Null",
                    name = "ALSO NULL",
                    category = "Electronics",
                    price = 69f,
                    images = emptyList()
                )))
            } catch (e: Exception) {
                _state.value = WishlistState.Error(e.message ?: "Failed to fetch product details")
            }
        }
    }
}




