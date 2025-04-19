package com.example.thriftr.utils.components.profilescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thriftr.utils.components.MenuItem
import com.example.thriftr.viewModel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun MenuSection(navController: NavController, viewModel: ProfileViewModel) {

    val menuItems = remember {
        listOf(
            Icons.Default.ManageAccounts to "Edit Profile",
            Icons.Default.ShoppingBag to "My Orders",
            Icons.Default.Favorite to "Wishlist"
        )
    }

    val animatedItems by viewModel.animationState.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        menuItems.forEachIndexed { index, (icon, label) ->
            val slideAnim = remember { Animatable(if (index in animatedItems) 0f else 300f) }

            LaunchedEffect(Unit) {
                if (index !in animatedItems) { // Animate only if not already animated
                    delay(index * 80L)
                    slideAnim.animateTo(0f, animationSpec = tween(500))
                    viewModel.markAnimated(index) // Store animation completion
                }
            }

            MenuItem(
                icon = icon,
                label = label,
                modifier = Modifier.offset(x = slideAnim.value.dp)
            ) {
                when (label) {
                    "Edit Profile" -> navController.navigate("EditProfileScreen")
                    "My Orders" -> navController.navigate("OrderScreen")
                    "Wishlist" -> navController.navigate("WishlistScreen")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}