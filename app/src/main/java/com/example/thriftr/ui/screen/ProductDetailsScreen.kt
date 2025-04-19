package com.example.thriftr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.thriftr.data.Product
import com.example.thriftr.utils.WishlistState
import com.example.thriftr.utils.components.ErrorContent
import com.example.thriftr.utils.components.productdetailsscreen.ColorCircles
import com.example.thriftr.utils.components.productdetailsscreen.ProductDetailsTopBar
import com.example.thriftr.utils.components.productdetailsscreen.ProductImageCarousel
import com.example.thriftr.utils.components.productdetailsscreen.ProductInformation
import com.example.thriftr.utils.components.productdetailsscreen.SizeChips
import com.example.thriftr.utils.components.productdetailsscreen.SkeletonProductDetails
import com.example.thriftr.viewModel.CartViewModel
import com.example.thriftr.viewModel.HomeViewModel
import com.example.thriftr.viewModel.ProductDetailsViewModel
import com.example.thriftr.viewModel.WishlistViewModel
import kotlinx.coroutines.launch


@Composable
fun ProductDetailsScreen(
    productId: String,
    navController: NavController,
    homeViewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    productDetailsViewModel: ProductDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val initialProduct by remember { mutableStateOf(homeViewModel.getCachedProduct(productId)) }
    val fetchedProduct by wishlistViewModel.state.collectAsState()
    val selectedSize by productDetailsViewModel.selectedSize.collectAsState()
    val selectedColor by productDetailsViewModel.selectedColor.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val currentProduct = when {
        initialProduct != null -> initialProduct
        fetchedProduct is WishlistState.Success -> {
            (fetchedProduct as WishlistState.Success).items.firstOrNull { it.id == productId }
        }
        else -> null
    }

    val cartState by cartViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (cartState is CartViewModel.CartState.Loading) {
            cartViewModel.loadCart()
        }
    }

    LaunchedEffect(productId) {
        if (initialProduct == null) {
            wishlistViewModel.fetchProductDetailsFromId(productId)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            ProductDetailsTopBar(
                onBack = onBack,
                onCartClick = { navController.navigate("CartScreen") },
                onProfileClick = { navController.navigate("ProfileScreen") },
                text = ""
            )
        }
    ) { innerPadding ->
            when {
                currentProduct != null -> {
                    ProductDetailsContent(
                        innerPadding = innerPadding,
                        productData = currentProduct,
                        wishlistViewModel = wishlistViewModel,
                        onAddToCart = {
                            // Check if product has sizes and colors
                            val needsSize = currentProduct.sizes?.filter { it.isNotBlank() }?.isNotEmpty() == true
                            val needsColor = currentProduct.colors?.filter { it.isNotBlank() }?.isNotEmpty() == true

                            // Only use selected options if needed
                            val selectedSizeOrNull = selectedSize?.takeIf { needsSize }
                            val selectedColorOrNull = selectedColor?.takeIf { needsColor }

                            // Validate selections if needed
                            if (needsSize && selectedSizeOrNull == null) {
                                Toast.makeText(context, "Please select a size", Toast.LENGTH_SHORT).show()
                                return@ProductDetailsContent
                            }
                            if (needsColor && selectedColorOrNull == null) {
                                Toast.makeText(context, "Please select a color", Toast.LENGTH_SHORT).show()
                                return@ProductDetailsContent
                            }

                            val isAlreadyInCart = cartViewModel.isItemInCart(productId, selectedSizeOrNull, selectedColorOrNull)

                            if (isAlreadyInCart) {
                                Toast.makeText(context, "Already in Cart", Toast.LENGTH_SHORT).show()
                            } else {
                                // Add to cart
                                scope.launch{
                                    cartViewModel.updateQuantity(
                                        productId = productId,
                                        newQty = 1,
                                        selectedSize = selectedSizeOrNull,
                                        selectedColor = selectedColorOrNull
                                    )
                                }
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBuyNow = {
                            val needsSize = !currentProduct.sizes.isNullOrEmpty()
                            val needsColor = !currentProduct.colors.isNullOrEmpty()

                            if (needsSize && selectedSize == null) {
                                Toast.makeText(context, "Please select a size", Toast.LENGTH_SHORT).show()
                                return@ProductDetailsContent
                            }
                            if (needsColor && selectedColor == null) {
                                Toast.makeText(context, "Please select a color", Toast.LENGTH_SHORT).show()
                                return@ProductDetailsContent
                            }

                            navController.navigate("BillingScreen")
                        },
                        selectedSize = selectedSize,
                        selectedColor = selectedColor,
                        onSizeSelected = { productDetailsViewModel.updateSelectedSize(it) },
                        onColorSelected = { productDetailsViewModel.updateSelectedColor(it) }
                    )
                }

                fetchedProduct is WishlistState.Loading -> {
                    SkeletonProductDetails()
                }
                fetchedProduct is WishlistState.Error -> {
                    ErrorContent(
                        message = (fetchedProduct as WishlistState.Error).message
                    )
                }
            }
        }
}

@Composable
fun ProductDetailsContent(
    innerPadding: PaddingValues,
    productData: Product,
    selectedSize: String?,
    selectedColor: String?,
    wishlistViewModel: WishlistViewModel,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    onSizeSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit
) {
    val isInWishlist by remember(productData.id) {
        derivedStateOf {
            when (val state = wishlistViewModel.state.value) {
                is WishlistState.Success -> state.items.any { it.id == productData.id }
                else -> false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()
        .padding(paddingValues = innerPadding)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ProductImageCarousel(
                    product = productData,
                    isInWishlist = isInWishlist,
                    onWishlistToggle = { shouldAdd ->
                        if (shouldAdd) {
                            wishlistViewModel.addToWishlist(
                                product = productData
                            )
                            wishlistViewModel.fetchWishlistItems()
                        } else {
                            wishlistViewModel.removeFromWishlist(productData.id) { _, _, _ -> }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                ProductInformation(productData = productData)
                Spacer(modifier = Modifier.height(8.dp))

                // Size Selection
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Sizes:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    SizeChips(
                        sizes = productData.sizes ?: emptyList(),
                        selectedSize = selectedSize,
                        onSizeSelected = onSizeSelected
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Color Selection
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Colors:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    ColorCircles(
                        colors = productData.colors ?: emptyList(),
                        selectedColor = selectedColor,
                        onColorSelected = onColorSelected
                    )
                }

                Spacer(modifier = Modifier.height(72.dp))

                ProductDetails(details = productData.details)
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        BottomButtons(
            onAddToCart = onAddToCart,
            onBuyNow = onBuyNow,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProductDetails(details: String?) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Product Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = details ?: "Not Available",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun BottomButtons(
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 0.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedButton(
            onClick = onAddToCart,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        ) {
            Text("Add to Cart")
        }

        Button(
            onClick = onBuyNow,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        ) {
            Text("Buy Now")
        }
    }
}











