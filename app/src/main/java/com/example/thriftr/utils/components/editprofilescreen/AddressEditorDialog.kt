package com.example.thriftr.utils.components.editprofilescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thriftr.data.Address

@Composable
fun AddressEditorDialog(
    address: Address, onSave: (Address) -> Unit, onDismiss: () -> Unit
) {
    var street by remember { mutableStateOf(address.street) }
    var city by remember { mutableStateOf(address.city) }
    var state by remember { mutableStateOf(address.state) }
    var postalCode by remember { mutableStateOf(address.postalCode) }
    var country by remember { mutableStateOf(address.country) }
    var isDefault by remember { mutableStateOf(address.isDefault) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (address.id.isEmpty()) "Add Address" else "Edit Address") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Postal Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(
                        checked = isDefault, onCheckedChange = { isDefault = it })
                    Text("Set as default address")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        address.copy(
                            street = street,
                            city = city,
                            state = state,
                            postalCode = postalCode,
                            country = country,
                            isDefault = isDefault
                        )
                    )
                }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })
}