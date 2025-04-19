package com.example.thriftr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.thriftr.Screens
import com.example.thriftr.data.Address
import com.example.thriftr.data.CartProduct
import com.example.thriftr.data.Order
import com.example.thriftr.data.OrderItem
import com.example.thriftr.utils.ProfileState
import com.example.thriftr.utils.calculateOrderSummary
import com.example.thriftr.utils.components.ErrorContent
import com.example.thriftr.utils.components.OrderConfirmationDialog
import com.example.thriftr.utils.components.ShimmerCartItem
import com.example.thriftr.utils.components.ShimmerContent
import com.example.thriftr.utils.components.billingscreen.AddAddressPlaceholder
import com.example.thriftr.utils.components.billingscreen.AddressThing
import com.example.thriftr.utils.components.billingscreen.CartItemRow
import com.example.thriftr.utils.components.billingscreen.OrderSummarySection
import com.example.thriftr.utils.components.profilescreen.ShimmerCard
import com.example.thriftr.viewModel.CartViewModel
import com.example.thriftr.viewModel.OrderViewModel
import com.example.thriftr.viewModel.ProfileViewModel
import java.util.UUID


@Composable
fun BillingScreen(
    padding: PaddingValues,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    cartViewModel: CartViewModel
) {
    val cartState by cartViewModel.state.collectAsState()
    val cartItems = when (cartState) {
        is CartViewModel.CartState.Success -> (cartState as CartViewModel.CartState.Success).items
        else -> emptyList()
    }
    val isLoading by cartViewModel.isLoading.collectAsState()
    val profileState by profileViewModel.state.collectAsState()

    LaunchedEffect(profileState) {
        if (profileState !is ProfileState.Success) {
            profileViewModel.fetchProfileIfNeeded()
        }
    }

        when (val state = profileState) {
            is ProfileState.Loading -> LoadingScreen(modifier = Modifier,
                padding = padding)
            is ProfileState.Error -> ErrorContent(state.message)
            is ProfileState.Success -> {
                BillingContent(
                    isLoading = isLoading,
                    cartItems = cartItems,
                    addresses = state.user.getAddresses(),
                    navController = navController,
                    padding = padding,
                    cartViewModel = cartViewModel
                )
            }
        }
}

@Composable
fun BillingContent(
    isLoading: Boolean,
    cartItems: List<CartProduct>,
    addresses: List<Address>,
    navController: NavController,
    padding: PaddingValues,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    var showOrderConfirmation by remember { mutableStateOf(false) }
    val orderStatus by orderViewModel.orderStatus.collectAsState()
    val context = LocalContext.current
    val defaultAddress = addresses.firstOrNull { it.isDefault }

    // Order Summary Calculation
    val orderSummary = calculateOrderSummary(cartItems)

    LaunchedEffect(orderStatus) {
        when (orderStatus) {
            is OrderViewModel.OrderStatus.Success -> showOrderConfirmation = true
            is OrderViewModel.OrderStatus.Error -> {
                Toast.makeText(
                    context,
                    "Failed to place order: ${(orderStatus as OrderViewModel.OrderStatus.Error).message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                if (defaultAddress != null) {
                    AddressThing(
                        address = defaultAddress,
                        onClickAdd = { navController.navigate("EditProfileScreen?editAddress=null") },
                        onClickModify = { navController.navigate("EditProfileScreen?editAddress=${defaultAddress.id}") }
                    )
                } else {
                    AddAddressPlaceholder(
                        onClick = { navController.navigate("EditProfileScreen?editAddress=null") }
                    )
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Order Summary",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (isLoading) {
                items(4) { ShimmerCartItem() }
            } else {
                items(cartItems) { item -> CartItemRow(item) }
            }

            item {
                OrderSummarySection(
                    subtotal = orderSummary.subtotal,
                    tax = orderSummary.tax,
                    shipping = orderSummary.shipping,
                    total = orderSummary.total
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (orderStatus is OrderViewModel.OrderStatus.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 52.dp)
                .height(56.dp)
                .align(Alignment.BottomCenter),
            onClick = {
                if (cartItems.isEmpty()) {
                    Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (defaultAddress == null) {
                    Toast.makeText(context, "Please add a shipping address", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Create the order
                val order = Order(
                    id = UUID.randomUUID().toString(),
                    total = orderSummary.total,
                    status = "Placed",
                    items = cartItems.map {
                        OrderItem(
                            productId = it.product.id,
                            name = it.product.name,
                            price = it.product.price.toDouble(),
                            quantity = it.quantity
                        )
                    },
                    createdAt = System.currentTimeMillis(),
                    shippingAddress = defaultAddress.toString()
                )

                // Save the order
                orderViewModel.placeOrder(order)
            },
            enabled = orderStatus !is OrderViewModel.OrderStatus.Loading && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                    ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        )
        {
            Text("Proceed to Pay", fontSize = 18.sp)
        }

        if (showOrderConfirmation) {
            OrderConfirmationDialog(
                onDismiss = {
                    showOrderConfirmation = false
                    navController.navigate(Screens.OrderScreen.route) {
                        popUpTo(Screens.HomeScreen.route) { inclusive = false }
                    }
                }
            )
            cartViewModel.clearCart()
        }
    }
}


@Composable
fun LoadingScreen(modifier: Modifier, padding: PaddingValues){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .padding(padding)){
        ShimmerCard(modifier = modifier
            .height(100.dp)
            .fillMaxWidth())
        ShimmerContent()
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerCard(modifier = modifier
            .height(200.dp)
            .fillMaxWidth())
    }
}




