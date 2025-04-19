package com.example.thriftr.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.thriftr.data.User
import com.example.thriftr.utils.ProfileState
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    onNavigate: (String) -> Unit,
    profileData: User,
    onClose: () -> Unit,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 1.dp
            ) {
                // User Profile Section
                DrawerHeader(
                    userData = profileData,
                    onProfileClick = { onNavigate("ProfileScreen") }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Navigation Items
                DrawerBody(
                    onNavigate = { route ->
                        scope.launch {
                            drawerState.close()
                            onNavigate(route)
                        }
                    }
                )
            }
        },
        content = content
    )
}

@Composable
private fun DrawerHeader(
    userData: User,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // User Avatar
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(onClick = onProfileClick)
        ) {
            if (userData.imagePath != null) {
                AsyncImage(
                    model = userData.imagePath,
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Text(
            text = "${userData.firstName} ${userData.lastName}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = userData.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DrawerBody(
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Primary Navigation Items
        NavigationDrawerItems(
            items = primaryNavigationItems,
            onNavigate = onNavigate
        )

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Secondary Navigation Items
        NavigationDrawerItems(
            items = secondaryNavigationItems,
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun NavigationDrawerItems(
    items: List<NavigationItem>,
    onNavigate: (String) -> Unit
) {
    items.forEach { item ->
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            badge = item.badgeCount?.let { count ->
                {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = count.toString(),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            },
            selected = false,
            onClick = { onNavigate(item.route) },
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent
            )
        )
    }
}

private data class UserData(
    val name: String,
    val email: String,
    val avatarUrl: String?
)

private data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int? = null
)

private val primaryNavigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Default.Home,
        route = "HomeScreen"
    ),
    NavigationItem(
        title = "Categories",
        icon = Icons.Default.Category,
        route = "categories"
    ),
    NavigationItem(
        title = "Deals",
        icon = Icons.Default.LocalOffer,
        route = "DealScreen"
    ),
    NavigationItem(
        title = "Orders",
        icon = Icons.Default.ShoppingBag,
        route = "orders",
        badgeCount = 2
    ),
    NavigationItem(
        title = "Wishlist",
        icon = Icons.Default.Favorite,
        route = "WishlistScreen"
    )
)

private val secondaryNavigationItems = listOf(
    NavigationItem(
        title = "Settings",
        icon = Icons.Default.Settings,
        route = "SettingsScreen"
    ),
    NavigationItem(
        title = "Help & Support",
        icon = Icons.Default.Help,
        route = "support"
    )
)