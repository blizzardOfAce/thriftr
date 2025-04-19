package com.example.thriftr.utils.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.thriftr.R


sealed class BottomNavItem(
    val route: String,
    val title: String,
    val iconRes: Int,
    val hasNews: Boolean
) {
    data object Home : BottomNavItem(
        route = "HomeScreen",
        title = "Home",
        iconRes = R.drawable.home_36dp,
        hasNews = false
    )

    data object Wishlist : BottomNavItem(
        route = "WishlistScreen",
        title = "Wishlist",
        iconRes = R.drawable.favorite_36dp,
        hasNews = false
    )

    data object Cart : BottomNavItem(
        route = "CartScreen",
        title = "Cart",
        iconRes = R.drawable.shopping_cart_36dp,
        hasNews = false
    )

    data object Profile : BottomNavItem(
        route = "ProfileScreen",
        title = "Profile",
        iconRes = R.drawable.account_circle_36dp,
        hasNews = false
    )

    data object Settings : BottomNavItem(
        route = "SettingsScreen",
        title = "Settings",
        iconRes = R.drawable.settings_36dp,
        hasNews = false
    )
}

@Composable
fun CustomBottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Wishlist,
        BottomNavItem.Cart,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column {
        // **Top Border** - Matches TopBar Divider
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.background) // ✅ Same as TopBar
        ) {
            // **Raised BottomAppBar (DAB)**
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RectangleShape), // ✅ Adds shadow
                containerColor = MaterialTheme.colorScheme.background, // ✅ Matches TopBar
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(32.dp),
                                    tint = if (currentRoute == item.route)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // ✅ Fixes Pressed Color
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = Color.Transparent
                            ),
                            alwaysShowLabel = false
                        )
                    }
                }
            }

            // **Floating Action Button (Cart)**
            FloatingActionButton(
                onClick = {
                    if (currentRoute != BottomNavItem.Cart.route) {
                        navController.navigate(BottomNavItem.Cart.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(70.dp)
                    .offset(y = (-20).dp) // ✅ Keeps FAB elevated above navbar
                    .border(
                        width = 1.dp, // ✅ Border for elevated effect
                        color = MaterialTheme.colorScheme.outline, shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.shopping_cart_36dp),
                    contentDescription = "Cart",
                    tint = if (currentRoute == BottomNavItem.Cart.route)
                        MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

//Working
//@Composable
//fun CustomBottomNavBar(navController: NavController) {
//    val items = listOf(
//        BottomNavItem.Home,
//        BottomNavItem.Wishlist,
//        BottomNavItem.Cart,
//        BottomNavItem.Profile,
//        BottomNavItem.Settings
//    )
//
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//
//    Column {
//        // **Top Border**
//        HorizontalDivider(
//            thickness = 0.5.dp,
//            color = MaterialTheme.colorScheme.onBackground
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(80.dp)
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//            BottomAppBar(
//                modifier = Modifier.fillMaxWidth(),
//                containerColor = MaterialTheme.colorScheme.background
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    items.forEach { item ->
//                        NavigationBarItem(
//                            selected = currentRoute == item.route,
//                            onClick = {
//                                if (currentRoute != item.route) {
//                                    navController.navigate(item.route) {
//                                        launchSingleTop = true
//                                        restoreState = true
//                                    }
//                                }
//                            },
//                            icon = {
//                                Icon(
//                                    painter = painterResource(id = item.iconRes),
//                                    contentDescription = item.title,
//                                    modifier = Modifier.size(32.dp),
//                                    tint = if (currentRoute == item.route) MaterialTheme.colorScheme.primary
//                                    else Color.LightGray
//                                )
//                            },
//                            colors = NavigationBarItemDefaults.colors().copy(
//                                selectedIconColor = MaterialTheme.colorScheme.primary,
//                                unselectedIconColor = MaterialTheme.colorScheme.surface,
//                                selectedIndicatorColor = Color.Transparent
//                            ),
//                            alwaysShowLabel = false
//                        )
//                    }
//                }
//            }
//
//            FloatingActionButton(
//                onClick = {
//                    if (currentRoute != BottomNavItem.Cart.route) {
//                        navController.navigate(BottomNavItem.Cart.route) {
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                },
//                shape = CircleShape,
//                containerColor = MaterialTheme.colorScheme.primary,
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .size(70.dp)
//                    .offset(y = (-20).dp) // Keeps FAB elevated above navbar
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.shopping_cart_36dp),
//                    contentDescription = "Cart",
//                    tint = if (currentRoute == BottomNavItem.Cart.route)
//                        MaterialTheme.colorScheme.onPrimaryContainer
//                    else MaterialTheme.colorScheme.outline,
//                    modifier = Modifier.size(32.dp)
//                )
//            }
//        }
//    }
//}





