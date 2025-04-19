package com.example.thriftr.utils.components.productdetailsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.data.Product


@Composable
fun ProductInformation(productData: Product) {
    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
        // Product Name
        Text(
            text = productData.name,
            fontSize = 24.sp,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Product Description
        Text(
            text = productData.description ?: "No description available",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Price and Discount Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                // Current Price
                Text(
                    text = "$${productData.price} USD",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Original Price (Strikethrough)
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "$${productData.price + (productData.discount!! / 100) * productData.price}",
                    fontSize = 22.sp,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Discount Percentage
            productData.discount?.let {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "${it.toInt()}% OFF",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}