package com.example.thriftr.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thriftr.R
import com.example.thriftr.data.CartProduct
import com.example.thriftr.utils.components.ErrorContent
import com.example.thriftr.utils.components.ShimmerContent
import com.example.thriftr.utils.components.cartscreen.AnimatedCartList
import com.example.thriftr.utils.components.cartscreen.QuantitySelector
import com.example.thriftr.viewModel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

/* Todo: Add haptic feedback for quantity changes
*/


@Composable
fun CartScreen(
    paddingValues: PaddingValues,
    viewModel: CartViewModel,
    navController: NavController
) {
    val cartState by viewModel.state.collectAsState()
    val localUpdates by viewModel.localUpdates.collectAsState()
    val visible by remember { derivedStateOf { viewModel.visible } }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(50)
        viewModel.visible = true
    }

        when (val state = cartState) {
            is CartViewModel.CartState.Loading -> {
                ShimmerContent()
            }

            is CartViewModel.CartState.Error -> {
                ErrorContent(message = state.message)
            }

            is CartViewModel.CartState.Success -> {
                val totalAmount = state.items.sumOf { it.quantity * (it.product.price).toDouble() }
                if (state.items.isEmpty()) {
                    EmptyCartView()
                } else {

                    AnimatedCartList(
                        totalAmount = totalAmount,
                        onClickCheckout = { navController.navigate("BillingScreen") },
                        visible = visible,
                        items = state.items,
                        localUpdates = localUpdates,

                        onUpdateQuantity = { productId, qty, size, color ->
                            if (qty > 0) {
                                scope.launch {
                                    viewModel.updateQuantity(
                                        productId, qty, size, color
                                    )
                                }
                            } else {
                                viewModel.removeFromCart(productId, size, color)
                            }
                        },
                        onItemClick = { productId ->
                            navController.navigate("ProductDetailsScreen/$productId")

                        },
                        paddingValues = paddingValues)
                }
            }
        }
    }

@Composable
fun EmptyCartView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp), contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            painter = painterResource(R.drawable.hungry_cat),
            contentDescription = "Empty cart"
        )
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            color = MaterialTheme.colorScheme.error,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


//CHECK THIS LATER:

//fun Modifier.dashedBorder(
//    color: Color,
//    strokeWidth: Float,
//    dashLength: Float = 10f,
//    gapLength: Float = 10f
//) = this.then(
//    Modifier.border(
//        width = strokeWidth.dp,
//        color = color,
//        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
//        border = CustomDashedBorder(color, strokeWidth, dashLength, gapLength)
//    )
//)
//
//class CustomDashedBorder(
//    private val color: Color,
//    private val strokeWidth: Float,
//    private val dashLength: Float,
//    private val gapLength: Float
//) : androidx.compose.ui.graphics.drawscope.DrawModifier {
//    override fun DrawScope.draw() {
//        drawLine(
//            color = color,
//            strokeWidth = strokeWidth,
//            start = androidx.compose.ui.geometry.Offset.Zero,
//            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
//            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
//                intervals = floatArrayOf(dashLength, gapLength),
//                phase = 0f
//            )
//        )
//        drawLine(
//            color = color,
//            strokeWidth = strokeWidth,
//            start = androidx.compose.ui.geometry.Offset(0f, size.height),
//            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
//            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
//                intervals = floatArrayOf(dashLength, gapLength),
//                phase = 0f
//            )
//        )
//        drawLine(
//            color = color,
//            strokeWidth = strokeWidth,
//            start = androidx.compose.ui.geometry.Offset(size.width, size.height),
//            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
//            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
//                intervals = floatArrayOf(dashLength, gapLength),
//                phase = 0f
//            )
//        )
//        drawLine(
//            color = color,
//            strokeWidth = strokeWidth,
//            start = androidx.compose.ui.geometry.Offset(0f, 0f),
//            end = androidx.compose.ui.geometry.Offset(0f, size.height),
//            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
//                intervals = floatArrayOf(dashLength, gapLength),
//                phase = 0f
//            )
//        )
//    }
//}
