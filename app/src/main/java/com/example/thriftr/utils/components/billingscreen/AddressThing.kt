package com.example.thriftr.utils.components.billingscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.data.Address

@Composable
fun AddressThing(address:Address, onClickAdd:() -> Unit,
                 onClickModify: () -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)){
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Address:  ", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "${address.street}, ", fontSize = 14.sp, maxLines = 2)
                Text(
                    text = "${address.city}, ${address.postalCode}, ${address.state}, ${address.country} ",
                    fontSize = 14.sp,
                    maxLines = 2
                )
            }
        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Card(
                shape = RoundedCornerShape(topEnd = 12.dp)
            ) {
                IconButton(onClick = onClickAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Add address")
                }
            }

            Card(
                shape = RoundedCornerShape(bottomEnd = 12.dp)
            ) {
                IconButton(onClick = onClickModify) {
                    Icon(Icons.Default.Mode, contentDescription = "Edit address")
                }
            }
        }
    }
}


@Composable
fun AddAddressPlaceholder(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No default Address found", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(modifier =  Modifier.fillMaxWidth(0.8f), onClick = onClick) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Add address")
                    Text("Add Address")
                }
            }
        }
    }
}