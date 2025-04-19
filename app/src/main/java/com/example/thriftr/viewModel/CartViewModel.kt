package com.example.thriftr.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thriftr.data.CartProduct
import com.example.thriftr.repository.AuthRepository
import com.example.thriftr.repository.CartRepository
import com.example.thriftr.utils.CartOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repo: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class CartState {
        data object Loading : CartState()
        data class Success(val items: List<CartProduct>) : CartState()
        data class Error(val message: String) : CartState()
    }

    private val _state = MutableStateFlow<CartState>(CartState.Loading)
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _localUpdates = MutableStateFlow<Map<String, Int>>(emptyMap())
    val localUpdates: StateFlow<Map<String, Int>> = _localUpdates.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val userId: String? get() = authRepository.authState.value?.id

    var visible by mutableStateOf(false)

    init {
        loadCart()
    }

    fun loadCart() {
        userId?.let { uid ->
            viewModelScope.launch {
                _isLoading.value = true
                _state.value = CartState.Loading

                when (val result = repo.getCartWithProducts(uid)) {
                    is CartOperation.Success -> {
                        val cartItems = result.items
                        val productIds = cartItems.map { it.productId }.toSet() // Get unique product IDs

                        // Fetch all products at once
                        val productMap = repo.getProducts(productIds) // ✅ Batch fetch
                        val validCartProducts = mutableListOf<CartProduct>()

                        for (cartItem in cartItems) {
                            val product = productMap[cartItem.productId]
                            if (product == null) {
                                Log.e("CartViewModel", "Product not found for ID: ${cartItem.productId}")
                            } else {
                                validCartProducts.add(
                                    CartProduct(
                                        product = product,
                                        quantity = cartItem.quantity,
                                        selectedSize = cartItem.selectedSize,
                                        selectedColor = cartItem.selectedColor
                                    )
                                )
                            }
                        }

                        _state.value = CartState.Success(validCartProducts)

                        // ✅ Update local updates map correctly
                        _localUpdates.value = cartItems.associate {
                            generateItemKey(it.productId, it.selectedSize, it.selectedColor) to it.quantity
                        }
                    }

                    is CartOperation.Error -> {
                        _state.value = CartState.Error(result.message)
                        Log.e("CartViewModel", "Error loading cart: ${result.message}")
                    }

                    CartOperation.Loading -> _state.value = CartState.Loading
                }

                _isLoading.value = false
            }
        }
    }

    suspend fun updateQuantity(
        productId: String,
        newQty: Int,
        selectedSize: String?,
        selectedColor: String?
    ) {
        val currentState = _state.value
        if (currentState !is CartState.Success) return

        val itemKey = generateItemKey(productId, selectedSize, selectedColor)

        // 🔹 1️⃣ Ensure UI updates immediately using `localUpdates`
        _localUpdates.value = _localUpdates.value.toMutableMap().apply {
            if (newQty > 0) put(itemKey, newQty) else remove(itemKey)
        }

        val updatedItems = currentState.items.toMutableList()
        val existingItemIndex = updatedItems.indexOfFirst {
            generateItemKey(it.product.id, it.selectedSize, it.selectedColor) == itemKey
        }

        if (existingItemIndex != -1) {
            if (newQty > 0) {
                updatedItems[existingItemIndex] = updatedItems[existingItemIndex].copy(quantity = newQty)
            } else {
                updatedItems.removeAt(existingItemIndex) // 🔹 2️⃣ Remove instantly
            }
        } else if (newQty > 0) {
            repo.getProduct(productId)?.let { product ->
                updatedItems.add(CartProduct(product, newQty, selectedSize, selectedColor))
            }
        }

        // 🔹 3️⃣ Force UI recomposition
        _state.value = CartState.Success(updatedItems.toList())

        // 🔹 4️⃣ Debounced update to sync with backend
        userId?.let { uid ->
            debouncedUpdate(uid, productId, newQty, selectedSize, selectedColor)
        }
    }


    fun isItemInCart(productId: String, selectedSize: String?, selectedColor: String?): Boolean {
        val currentState = _state.value
        if (currentState !is CartState.Success) return false

        return currentState.items.any {
            it.product.id == productId &&
                    (it.selectedSize.orEmpty() == selectedSize.orEmpty()) &&
                    (it.selectedColor.orEmpty() == selectedColor.orEmpty())
        }
    }



    // Helper function to generate consistent item keys
    private fun generateItemKey(productId: String, size: String?, color: String?): String {
        return "$productId-${size.orEmpty()}-${color.orEmpty()}"
    }

    private var updateJob: Job? = null
    private fun debouncedUpdate(
        userId: String,
        productId: String,
        newQty: Int,
        selectedSize: String?,
        selectedColor: String?
    ) {
        updateJob?.cancel() // Cancel previous job if any
        updateJob = viewModelScope.launch {
            delay(500) // 500ms debounce time
            repo.updateCartItem(userId, productId, newQty, selectedSize, selectedColor)
        }
    }


    fun clearCart() {
        userId?.let { uid ->
            viewModelScope.launch {
                _state.value = CartState.Loading
                try {
                    repo.clearCart(uid)
                    _state.value = CartState.Success(emptyList())
                    _localUpdates.value = emptyMap()
                } catch (e: Exception) {
                    Log.e("CartViewModel", "Clear Cart Error: ", e)
                    _state.value = CartState.Error("Failed to clear cart")
                }
            }
        }
    }

    fun removeFromCart(productId: String, selectedSize: String?, selectedColor: String?) {
        viewModelScope.launch {
            val itemKey = generateItemKey(productId, selectedSize, selectedColor)

            // 🔹 1️⃣ Instantly update UI and remove from localUpdates
            _localUpdates.value = _localUpdates.value.toMutableMap().apply { remove(itemKey) }
            val currentState = _state.value
            if (currentState is CartState.Success) {
                val updatedItems = currentState.items.filterNot {
                    generateItemKey(it.product.id, it.selectedSize, it.selectedColor) == itemKey
                }
                _state.value = CartState.Success(updatedItems)
            }

            // 🔹 2️⃣ Sync with backend
            userId?.let { uid ->
                repo.removeFromCart(uid, productId, selectedSize, selectedColor)
            }
        }
    }

//
//    fun removeFromCart(productId: String, selectedSize: String?, selectedColor: String?) {
//        viewModelScope.launch {
//            try {
//                val result = repo.removeFromCart(userId = userId ?: "NULL", productId, selectedSize, selectedColor)
//
//                if (result is CartOperation.Success) {
//                    val cartProducts = result.items.mapNotNull { cartItem ->
//                        repo.getProduct(cartItem.productId)?.let { product ->
//                            CartProduct(
//                                product = product,
//                                quantity = cartItem.quantity,
//                                selectedSize = cartItem.selectedSize,
//                                selectedColor = cartItem.selectedColor
//                            )
//                        }
//                    }
//
//                    _state.value = CartState.Success(cartProducts)
//                } else {
//                    _state.value = CartState.Error("Failed to remove item")
//                }
//            } catch (e: Exception) {
//                _state.value = CartState.Error("Failed to remove item")
//            }
//        }
//    }
}
