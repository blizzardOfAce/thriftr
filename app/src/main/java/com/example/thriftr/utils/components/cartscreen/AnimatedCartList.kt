package com.example.thriftr.utils.components.cartscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.data.CartProduct
import com.example.thriftr.utils.components.billingscreen.CartItemRow
import kotlin.math.min

@Composable
fun AnimatedCartList(
    paddingValues: PaddingValues,
    onClickCheckout: () -> Unit,
    totalAmount: Double,
    visible: Boolean,
    items: List<CartProduct>,
    localUpdates: Map<String, Int>,
    onUpdateQuantity: (String, Int, String?, String?) -> Unit,
    onItemClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .padding(horizontal = 8.dp)
            .animateContentSize() // Smooth transition when items load
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(items = items, key = { _, item ->
            "${item.product.id}-${item.selectedSize ?: "default"}-${item.selectedColor ?: "default"}"
        }) { index, item ->

            val itemKey =
                "${item.product.id}-${item.selectedSize ?: "default"}-${item.selectedColor ?: "default"}"
            val currentQty = remember { mutableIntStateOf(localUpdates[itemKey] ?: item.quantity) }

            LaunchedEffect(localUpdates[itemKey]) {
                currentQty.intValue = localUpdates[itemKey] ?: item.quantity
            }

            AnimatedVisibility(
                visible = visible, enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth * 2 },
                    // Cap the delay at a reasonable maximum
                    animationSpec = tween(
                        delayMillis = min(index * 50, 300), durationMillis = 300
                    )
                ) + fadeIn(), exit = slideOutHorizontally() + fadeOut()
            ) {
                Column {
                    CartItemRow(
                        cartProduct = item,
                        quantity = currentQty.intValue,
                        onQuantityChange = { newQty ->
                            onUpdateQuantity(
                                item.product.id, newQty, item.selectedSize, item.selectedColor
                            )
                        },
                        onClick = { onItemClick(item.product.id) },
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }

        item {
            AnimatedVisibility(visible = visible && items.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
                    ) {
                        Text("Total: ", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "₹%.2f".format(totalAmount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onClickCheckout,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(52.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Go to checkout", fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}