package com.example.thriftr.utils.components.cartscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantitySelector(
    currentQuantity: Int, onQuantityChange: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(0.5.dp, Color.Gray),
        modifier = modifier.wrapContentSize()
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp), contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onQuantityChange(currentQuantity - 1) },
                    modifier = Modifier.size(20.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "$currentQuantity",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                )

                IconButton(
                    onClick = { onQuantityChange(currentQuantity + 1) },
                    modifier = Modifier.size(20.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}