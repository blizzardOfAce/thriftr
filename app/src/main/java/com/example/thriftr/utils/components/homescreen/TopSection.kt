package com.example.thriftr.utils.components.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thriftr.R

@Composable
fun TopSection(
    navController: NavController,
    imageUrl: String,
    toggleNavDrawer: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon button to implement NavBar(Just for fun):
        IconButton(onClick = toggleNavDrawer) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(32.dp)
            )
        }
        // Logo Image
        Image(
            painter = painterResource(id = R.drawable.thriftr_logo_abc),
            contentDescription = "App Logo",
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
        )
        // Profile Image
        AsyncImage(
            model = if(imageUrl == "NULL") null else imageUrl,
            placeholder = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .clickable { navController.navigate("ProfileScreen") }
        )
    }
}


