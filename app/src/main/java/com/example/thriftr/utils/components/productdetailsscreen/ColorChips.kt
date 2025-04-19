package com.example.thriftr.utils.components.productdetailsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.thriftr.viewModel.AdminViewModel

@Composable
fun ColorCircles(
    colors: List<String>,
    selectedColor: String? = null,
    onColorSelected: (String) -> Unit = {}
) {
    // Parse the color:hexCode format
    val colorList = colors.map { colorString ->
        val parts = colorString.split(":")
        AdminViewModel.ColorInfo(
            name = parts[0],
            hexCode = parts[1]
        )
    }

    if (colorList.isEmpty()) {
        Text("Not Available")
        return
    }

    Column {
        LazyRow(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(colorList) { colorInfo ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = try {
                                    Color(colorInfo.hexCode.toColorInt())
                                } catch (e: IllegalArgumentException) {
                                    Color.Gray // Default if invalid color
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = if (selectedColor == colorInfo.name) 2.dp else 1.dp,
                                color = if (selectedColor == colorInfo.name)
                                    MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorInfo.name) }
                    )
                    Text(
                        text = colorInfo.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}