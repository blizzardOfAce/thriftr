package com.example.thriftr.utils.components.adminscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SizeInputField(
    sizes: String,
    onSizesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sizeText by remember { mutableStateOf(sizes) }

    Column(modifier = modifier) {
        SlimOutlinedTextField(
            value = sizeText,
            onValueChange = { newValue ->
                sizeText = newValue
                onSizesChanged(newValue)
            },
            label = "Sizes (comma-separated)",
            singleLine = true,
            // placeholder = { Text("Enter sizes: S, M, L, XL") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            )
        )

        // Preview of entered sizes
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            sizeText.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { size ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = size,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
        }
    }
}