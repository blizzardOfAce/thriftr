package com.example.thriftr.utils.components.editprofilescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.data.Address

@Composable
fun AddressSection(
    defaultAddress: Address?,
    otherAddresses: List<Address>,
    onEditAddress: (Address) -> Unit,
    onDeleteAddress: (Address) -> Unit,
    onAddAddress: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Addresses", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
            //style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Default Address",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        defaultAddress?.let { address ->
            AddressItem(
                address = address,
                onEdit = { onEditAddress(address) },
                onDelete = { onDeleteAddress(address) })
        } ?: run {
            Text("No default address set", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Other Addresses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (otherAddresses.isEmpty()) {
            Text("No other addresses saved", color = Color.Gray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                otherAddresses.forEach { address ->
                    AddressItem(
                        address = address,
                        onEdit = { onEditAddress(address) },
                        onDelete = { onDeleteAddress(address) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onAddAddress, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Address")
        }
    }
}

@Composable
fun AddressItem(
    address: Address, onEdit: () -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = address.street,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "${address.city}, ${address.state} ${address.postalCode}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = address.country, style = MaterialTheme.typography.bodyMedium
                )
            }

            // Edit button (top-right)
            IconButton(
                onClick = onEdit, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, contentDescription = "Edit Address"
                )
            }

            // Delete button (bottom-right)
            IconButton(
                onClick = onDelete, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Address",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
