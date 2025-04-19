package com.example.thriftr

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thriftr.ui.screen.AdminScreen
import com.example.thriftr.ui.screen.BillingScreen
import com.example.thriftr.ui.screen.CartScreen
import com.example.thriftr.ui.screen.DealsScreen
import com.example.thriftr.ui.screen.EditProfileScreen
import com.example.thriftr.ui.screen.HomeScreen
import com.example.thriftr.ui.screen.LoginScreen
import com.example.thriftr.ui.screen.OrderScreen
import com.example.thriftr.ui.screen.ProductDetailsScreen
import com.example.thriftr.ui.screen.ProfileScreen
import com.example.thriftr.ui.screen.RegisterScreen
import com.example.thriftr.ui.screen.SettingsScreen
import com.example.thriftr.ui.screen.StartScreen
import com.example.thriftr.ui.screen.WishlistScreen
import com.example.thriftr.viewModel.AuthViewModel
import com.example.thriftr.viewModel.CartViewModel
import com.example.thriftr.viewModel.HomeViewModel
import com.example.thriftr.viewModel.OrderViewModel
import com.example.thriftr.viewModel.ProfileViewModel
import com.example.thriftr.viewModel.SettingsViewModel
import com.example.thriftr.viewModel.WishlistViewModel


/*Todo: Implement Nested navigation Graph and a generic parceable data class to manage different data classes*/

@Composable
fun AppNavigation(
    startDestination: String,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    onThemeToggle: (Boolean) -> Unit,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val wishlistViewModel: WishlistViewModel = hiltViewModel()
    val cartViewModel: CartViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,

        //Ios Style:
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -100 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -100 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(300))
        }

    ) {
        // Start Screen (Login/SignUp)
        composable(Screens.StartScreen.route) {
            StartScreen(
                onClickLogin = {
                    navController.navigate(Screens.LoginScreen.route)
                    {
                        popUpTo(Screens.StartScreen.route) { inclusive = true }
                    }
                },
                onClickSignUp = {
                    navController.navigate(Screens.RegisterScreen.route)
                    {
                        popUpTo(Screens.StartScreen.route) { inclusive = true }
                    }
                },
                onClickSeller = {
                    navController.navigate(Screens.AdminScreen.route)
                },
                onClickSkip = {/* Implement later*/ }
            )
        }

        // Home Screen
        composable(Screens.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                onClickProductItem = { product ->
                    navController.navigate("${Screens.ProductDetailsScreen.route}/${product.id}")
                },
                onButtonClick = { product ->
                    navController.navigate("${Screens.DealsScreen.route}/${product.id}")
                    {
                        popUpTo(Screens.StartScreen.route) { inclusive = true }
                    }
                },
                paddingValues = paddingValues
            )
        }

        // Login and SignUp Screens
        composable(
            route = Screens.LoginScreen.route,
            enterTransition = {
                val slideIn = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeIn = fadeIn(animationSpec = tween(500))
                slideIn + fadeIn

            },
            exitTransition = {
                val slideOut = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeOut = fadeOut(animationSpec = tween(500))
                slideOut + fadeOut
            },
            popEnterTransition = {
                val slideIn = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeIn = fadeIn(animationSpec = tween(500))
                slideIn + fadeIn
            },

            popExitTransition = {
                val slideOut = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeOut = fadeOut(animationSpec = tween(500))
                slideOut + fadeOut
            }
        ) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel
            )
        }
        composable(
            route = Screens.RegisterScreen.route,
            enterTransition = {
                val slideIn = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeIn = fadeIn(animationSpec = tween(300))
                slideIn + fadeIn
            },
            exitTransition = {
                val slideOut = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeOut = fadeOut(animationSpec = tween(500))
                slideOut + fadeOut
            },
            popEnterTransition = {
                val slideIn = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeIn = fadeIn(animationSpec = tween(500))
                slideIn + fadeIn
            },

            popExitTransition = {
                val slideOut = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
                val fadeOut = fadeOut(animationSpec = tween(500))
                slideOut + fadeOut
            }
        ) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }

        //ProductDetailsScreen
        composable(
            route = "${Screens.ProductDetailsScreen.route}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailsScreen(
                productId = productId,
                navController = navController,
                homeViewModel = homeViewModel,
                cartViewModel = cartViewModel,
                wishlistViewModel = wishlistViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        //ProfileScreen
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                paddingValues = paddingValues,
                navController = navController
            )
        }

        //CartScreen
        composable(Screens.CartScreen.route) {
            CartScreen(
                viewModel = cartViewModel,
                navController = navController,
                paddingValues = paddingValues
            )
        }

        //BillingScreen
        composable(
            route = Screens.BillingScreen.route
        ) {
            BillingScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                cartViewModel = cartViewModel,
                padding = paddingValues
            )
        }

        //WishlistScreen
        composable(Screens.WishlistScreen.route) {
            WishlistScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                wishlistViewModel = wishlistViewModel,
                paddingValues = paddingValues
            )
        }

        //SettingsScreen
        composable(route = Screens.SettingsScreen.route) {
            SettingsScreen(
                navController = navController, authViewModel = authViewModel,
                settingsViewModel = settingsViewModel,
                paddingValues = paddingValues,
                onThemeToggle = onThemeToggle
            )
        }

        //EditProfileScreen
        composable(
            route = "${Screens.EditProfileScreen.route}?editAddress={editAddress}",
            arguments = listOf(
                navArgument("editAddress") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )) {
            EditProfileScreen(
                viewModel = profileViewModel,
                paddingValues = paddingValues,
                snackbarHostState = snackbarHostState,
                navController = navController
            )
        }

        //DealsScreen
        composable(
            route = "${Screens.DealsScreen.route}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            DealsScreen(productId = productId, homeViewModel = homeViewModel, onBackPress = {
                navController.popBackStack()
            })
        }

        //AdminScreen
        composable(route = Screens.AdminScreen.route) {
            val context =  LocalContext.current
            AdminScreen(
                onClickBack = { navController.popBackStack() },
                onProductSaved = {
                    Toast.makeText(context, "Product Added", Toast.LENGTH_SHORT).show()
                }
            )
        }
        //OrderScreen
        composable(route = Screens.OrderScreen.route) {
            val orderViewModel: OrderViewModel = hiltViewModel()
            OrderScreen(
                paddingValues = paddingValues,
                viewModel = orderViewModel
            )
        }
    }
}

sealed class Screens(val route: String) {
    data object AdminScreen : Screens("AdminScreen")
    data object StartScreen : Screens("StartScreen")
    data object HomeScreen : Screens("HomeScreen")
    data object SettingsScreen : Screens("SettingsScreen")
    data object DealsScreen : Screens("DealsScreen")
    data object LoginScreen : Screens("LoginScreen")
    data object RegisterScreen : Screens("RegisterScreen")
    data object CartScreen : Screens("CartScreen")
    data object WishlistScreen : Screens("WishlistScreen")
    data object BillingScreen : Screens("BillingScreen")
    data object OrderScreen : Screens("OrderScreen")
    data object ProfileScreen : Screens("ProfileScreen")
    data object EditProfileScreen : Screens("EditProfileScreen")
    data object ProductDetailsScreen : Screens("ProductDetailsScreen")
}
