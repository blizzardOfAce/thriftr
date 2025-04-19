package com.example.thriftr.utils.components.billingscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OrderSummarySection(
    subtotal: Double,
    tax: Double,
    shipping: Double,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OrderSummaryRow("Subtotal", subtotal)
            OrderSummaryRow("Tax (18%)", tax)
            OrderSummaryRow("Shipping", shipping)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface)
            OrderSummaryRow("Total", total, isTotal = true)
        }
    }
}

@Composable
fun OrderSummaryRow(label: String, value: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if(isTotal) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = "₹%.2f".format(value),
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
    }
}