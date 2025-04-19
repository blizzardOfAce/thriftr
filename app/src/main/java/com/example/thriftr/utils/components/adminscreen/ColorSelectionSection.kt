package com.example.thriftr.utils.components.adminscreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.thriftr.viewModel.AdminViewModel
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlin.collections.forEach

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSelectionSection(
    selectedColors: List<AdminViewModel.ColorInfo>,
    onColorAdded: (AdminViewModel.ColorInfo) -> Unit,
    onColorRemoved: (AdminViewModel.ColorInfo) -> Unit
) {
    var showColorDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Available Colors", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedColors.forEach { colorInfo ->
                    ColorChip(colorInfo, onRemove = { onColorRemoved(colorInfo) })
                }
            }
            Button(onClick = { showColorDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Color")
            }
        }
    }
    if (showColorDialog) {
        ColorPickerDialog(
            onColorSelected = {
                onColorAdded(it)
                showColorDialog = false
            },
            onDismiss = { showColorDialog = false }
        )
    }
}

@Composable
fun ColorChip(
    colorInfo: AdminViewModel.ColorInfo,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = try {
                Color(colorInfo.hexCode.toColorInt())
            } catch (e: IllegalArgumentException) {
                Log.e("Color Chip", "Error: ${e.message}", e)
                Color.Black // Default to Black if parsing fails
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color, shape = CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(colorInfo.name)
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove color",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ColorPickerDialog(
    onColorSelected: (AdminViewModel.ColorInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf("") }
    var colorName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a Color") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        if (colorEnvelope.fromUser) {
                            selectedColor = colorEnvelope.hexCode
                        }
                    }
                )
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).height(35.dp),
                    controller = controller
                )
                AlphaTile(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).height(60.dp),
                    controller = controller
                )
                OutlinedTextField(
                    value = colorName,
                    singleLine = true,
                    onValueChange = { colorName = it },
                    label = { Text("Color Name") },
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedColor.isNotEmpty() && colorName.isNotBlank()) {
                        val hexCode = if (selectedColor.startsWith("#")) selectedColor else "#$selectedColor"
                        onColorSelected(AdminViewModel.ColorInfo(colorName, hexCode))
                    }
                    onDismiss()
                }
            )           {
                Text("Select")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
