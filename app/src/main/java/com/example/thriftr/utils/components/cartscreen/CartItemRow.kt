package com.example.thriftr.utils.components.cartscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thriftr.data.CartProduct

@Composable
internal fun CartItemRow(
    cartProduct: CartProduct,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentQuantity by remember { mutableIntStateOf(quantity) }


    fun updateQuantity(newQuantity: Int) {
        if (newQuantity > 0) {
            currentQuantity = newQuantity
            onQuantityChange(newQuantity)
        } else {
            onQuantityChange(0) // Signal removal
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = cartProduct.product.images.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text(
                    text = cartProduct.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(0.8f),
                        text = "₹${cartProduct.product.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    QuantitySelector(
                        modifier = Modifier.weight(1f),
                        currentQuantity = currentQuantity,
                        onQuantityChange = ::updateQuantity
                    )

                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Size: ${cartProduct.selectedSize.takeUnless { it.isNullOrEmpty() } ?: "N/A"}",
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f))

                    Text(text = "Color: ${cartProduct.selectedColor.takeUnless { it.isNullOrEmpty() } ?: "N/A"}",
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f))

                }
            }
        }
    }
}