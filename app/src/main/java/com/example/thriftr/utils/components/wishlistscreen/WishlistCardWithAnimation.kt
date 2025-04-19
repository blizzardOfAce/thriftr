package com.example.thriftr.utils.components.wishlistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thriftr.R
import com.example.thriftr.data.Product
import com.example.thriftr.viewModel.WishlistViewModel
import kotlinx.coroutines.launch

@Composable
fun WishlistItemCardWithAnimation(
    product: Product,
    showSnackbar: (String, String, suspend () -> Unit) -> Unit,
    navController: NavController,
    viewModel: WishlistViewModel
) {
    val isVisible = viewModel.itemVisibility[product.id] != false

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { it })
    ) {
        WishlistItemCard(
            product = product,
            navController = navController,
            viewModel = viewModel,
            showSnackbar = showSnackbar,
            onMoveToCart = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistItemCard(
    product: Product,
    showSnackbar:  (String, String, suspend () -> Unit) -> Unit,
    onMoveToCart: () -> Unit,
    viewModel: WishlistViewModel,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { with(density) { 150.dp.toPx() } },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                scope.launch {
                    viewModel.removeFromWishlist(product.id, showSnackbar)
                }
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        }
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .clickable { navController.navigate("ProductDetailsScreen/${product.id}") }
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop,
                        model = product.images.firstOrNull(),
                        contentDescription = "product image"
                    )
                    Column {
                        Text(
                            text = product.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Price: ₹${product.price}", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onMoveToCart,
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.shopping_cart_checkout_36dp),
                                contentDescription = "add to cart",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Move to Cart",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.removeFromWishlist(product.id, showSnackbar)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "remove from wishlist",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Remove",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}