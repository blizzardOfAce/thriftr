package com.example.thriftr.utils.components.billingscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.thriftr.data.CartProduct

@Composable
fun CartItemRow(item: CartProduct) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.product.images.firstOrNull(),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.bodyMedium)
            Text("Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            Text("Price: ₹${item.product.price}", style = MaterialTheme.typography.bodySmall)
        }
    }
}