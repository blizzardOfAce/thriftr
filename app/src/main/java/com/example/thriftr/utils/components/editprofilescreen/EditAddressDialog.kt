package com.example.thriftr.utils.components.editprofilescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.thriftr.data.Address

@Composable
fun AddressEditorDialog(
    address: Address? = null,
    onDismiss: () -> Unit,
    onSave: (Address) -> Unit
) {
    var street by remember { mutableStateOf(address?.street ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var state by remember { mutableStateOf(address?.state ?: "") }
    var postalCode by remember { mutableStateOf(address?.postalCode ?: "") }
    var country by remember { mutableStateOf(address?.country ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (address == null) "Add Address" else "Edit Address") },
        text = {
            Column {
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Add similar fields for city, state, postalCode, country
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                    Text("Default Address")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Address(
                        street = street,
                        city = city,
                        state = state,
                        postalCode = postalCode,
                        country = country,
                        isDefault = isDefault
                    )
                )
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}