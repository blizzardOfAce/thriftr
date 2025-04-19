package com.example.thriftr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thriftr.services.AppwriteSingleton
import com.example.thriftr.ui.theme.ThriftrTheme
import com.example.thriftr.utils.components.BottomNavItem
import com.example.thriftr.utils.components.CustomBottomNavBar
import com.example.thriftr.utils.components.CustomTopBar
import com.example.thriftr.viewModel.AuthViewModel
import com.example.thriftr.viewModel.CartViewModel
import com.example.thriftr.viewModel.SettingsViewModel
import com.example.thriftr.viewModel.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val wishlistViewModel: WishlistViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteSingleton.init(this)

        installSplashScreen().apply {
            setKeepOnScreenCondition { authViewModel.isCheckingSession.value }
        }

        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            val darkThemePref by settingsViewModel.darkThemeEnabled.collectAsStateWithLifecycle()
            val darkThemeEnabled = darkThemePref ?: systemDarkTheme

            ThriftrTheme(darkTheme = darkThemeEnabled) {

                val navController = rememberNavController()
                val isCheckingSession by authViewModel.isCheckingSession.collectAsStateWithLifecycle()
                val authState by authViewModel.authState.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }

                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                val sessionState = when {
                    isCheckingSession -> null
                    authState != null -> "HomeScreen"
                    else -> "StartScreen"
                }

                val useMainScaffold = currentRoute !in listOf(
                    Screens.AdminScreen.route,
                    Screens.HomeScreen.route,
                    Screens.ProductDetailsScreen.route
                )

                val showTopBar = currentRoute?.let {
                    it !in listOf(
                        Screens.StartScreen.route,
                        Screens.LoginScreen.route,
                        Screens.RegisterScreen.route,
                        Screens.HomeScreen.route
                    ) && !it.startsWith(Screens.ProductDetailsScreen.route)
                } == true

                val showBottomBar = currentRoute in listOf(
                    BottomNavItem.Wishlist.route,
                    BottomNavItem.Cart.route,
                    BottomNavItem.Profile.route
                )

                sessionState?.let { startDest ->

                    if (useMainScaffold) {
                        Scaffold(
                            contentWindowInsets = WindowInsets.systemBars,
                            snackbarHost = {
                                SnackbarHost(
                                    hostState = snackbarHostState,
                                    modifier = Modifier.padding(bottom = 40.dp)
                                )
                            },
                            topBar = {
                                if (showTopBar) {
                                    CustomTopBar(
                                        title = when {
                                            currentRoute == Screens.CartScreen.route -> "Cart"
                                            currentRoute == Screens.WishlistScreen.route -> "Wishlist"
                                            currentRoute == Screens.ProfileScreen.route -> "Profile"
                                            currentRoute == Screens.SettingsScreen.route -> "Settings"
                                            currentRoute == Screens.BillingScreen.route -> "Checkout"
                                            currentRoute == Screens.AdminScreen.route -> "Add Product"
                                            currentRoute.startsWith(Screens.DealsScreen.route) -> "Best Deals"
                                            currentRoute.startsWith(Screens.EditProfileScreen.route) -> "Edit Profile"
                                            currentRoute == Screens.OrderScreen.route -> "Orders"
                                            else -> ""
                                        },
                                        navigationIcon = if (currentRoute !in listOf(BottomNavItem.Home.route)) {
                                            {
                                                IconButton(onClick = { navController.popBackStack() }) {
                                                    Icon(
                                                        Icons.Default.ArrowBackIosNew,
                                                        contentDescription = "Go back"
                                                    )
                                                }
                                            }
                                        } else null,
                                        actions = {
                                            when (currentRoute) {
                                                BottomNavItem.Cart.route -> {
                                                    IconButton(onClick = { cartViewModel.clearCart() }) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            contentDescription = "Clear cart"
                                                        )
                                                    }
                                                }

                                                BottomNavItem.Wishlist.route -> {
                                                    IconButton(onClick = { wishlistViewModel.clearWishlist() }) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            contentDescription = "Clear wishlist"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            },
                            bottomBar = {
                                AnimatedVisibility(
                                    visible = showBottomBar,
                                    enter = slideInVertically { it / 2 } + fadeIn(
                                        animationSpec = tween(
                                            100
                                        )
                                    ),
                                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically { it / 2 }
                                ) {
                                    CustomBottomNavBar(navController)
                                }

                            },
                        ) { paddingValues ->
                            AppNavigation(
                                paddingValues = paddingValues,
                                navController = navController,
                                startDestination = startDest,
                                snackbarHostState = snackbarHostState,
                                onThemeToggle = { settingsViewModel.setDarkTheme(it) }
                            )
                        }
                    } else {

                        Scaffold(
                            bottomBar = {
                                AnimatedVisibility(
                                    visible = showBottomBar,
                                    enter = slideInVertically { it / 2 } + fadeIn(
                                        animationSpec = tween(
                                            200
                                        )
                                    ),
                                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically { it / 2 }
                                ) {
                                    CustomBottomNavBar(navController)
                                }
                            }
                        ) { paddingValues ->
                            AppNavigation(
                                paddingValues = PaddingValues(8.dp),
                                navController = navController,
                                startDestination = startDest,
                                snackbarHostState = snackbarHostState,
                                onThemeToggle = { settingsViewModel.setDarkTheme(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
