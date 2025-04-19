package com.example.thriftr.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thriftr.data.Order
import com.example.thriftr.repository.AuthRepository
import com.example.thriftr.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val _userId = MutableStateFlow(authRepository.authState.value?.id ?: "")
    private val userId: String get() = _userId.value

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() = _orders

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _orderStatus = MutableStateFlow<OrderStatus>(OrderStatus.Idle)
    val orderStatus: StateFlow<OrderStatus> get() = _orderStatus


    fun fetchOrdersIfNeeded() {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _orders.value = repository.fetchOrders(userId)
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error fetching orders: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun placeOrder(order: Order) {
        if (userId.isEmpty()) {
            Log.e("OrderViewModel", "Cannot place order: User ID is empty")
            _orderStatus.value = OrderStatus.Error("User not authenticated")
            return
        }

        viewModelScope.launch {
            _orderStatus.value = OrderStatus.Loading
            try {
                repository.saveOrder(order, userId)
                _orderStatus.value = OrderStatus.Success
                fetchOrdersIfNeeded() // Refresh the orders
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error placing order: ${e.message}")
                _orderStatus.value = OrderStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun cancelOrder(order: Order) {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Create a new order with cancelled status
                val cancelledOrder = order.copy(status = "Cancelled")

                // Update the order in the repository
                repository.updateOrder(cancelledOrder, userId)

                // Refresh orders
                fetchOrdersIfNeeded()
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error cancelling order: ${e.message}")
            }
        }
    }

    sealed class OrderStatus {
        data object Idle : OrderStatus()
        data object Loading : OrderStatus()
        data object Success : OrderStatus()
        data class Error(val message: String) : OrderStatus()
    }
}