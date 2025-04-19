package com.example.thriftr.utils.components.productdetailsscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SizeChips(
    sizes: List<String>,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit
) {
    val sizesList = sizes.flatMap { it.split(",")
        .map { it.trim() } }
        .filter { it.isNotEmpty() }

    if (sizesList.isEmpty()) {
        Text("Not Available")
        return
    }

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(sizesList) { size ->
            SizeChip(
                size = size,
                onSizeSelected = onSizeSelected,
                isSelected = size == selectedSize
            )
        }
    }
}

@Composable
fun SizeChip(
    size: String,
    onSizeSelected: (String) -> Unit,
    isSelected: Boolean
) {
    Surface(
        modifier = Modifier
            .padding(1.dp)
            .clickable { onSizeSelected(size) },
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = size,
                fontSize = 12.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}