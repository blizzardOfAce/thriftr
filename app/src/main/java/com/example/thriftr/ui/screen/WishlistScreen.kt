package com.example.thriftr.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thriftr.utils.WishlistState
import com.example.thriftr.utils.components.ErrorContent
import com.example.thriftr.utils.components.wishlistscreen.ShimmerWishlistItemCard
import com.example.thriftr.utils.components.wishlistscreen.WishlistGridItemCard
import com.example.thriftr.utils.components.wishlistscreen.WishlistItemCardWithAnimation
import com.example.thriftr.utils.components.wishlistscreen.WishlistToggleViewBar
import com.example.thriftr.viewModel.WishlistViewModel
import kotlinx.coroutines.launch

@Composable
fun WishlistScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    wishlistViewModel: WishlistViewModel,
    snackbarHostState: SnackbarHostState
) {
    val state by wishlistViewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    var isGridView by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun showSnackbar(message: String, actionLabel: String, onAction: suspend () -> Unit) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message, actionLabel = actionLabel, duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onAction()
            }
        }
    }

    LaunchedEffect(Unit) {
        wishlistViewModel.fetchWishlistIfNeeded()
    }

    when (state) {
        is WishlistState.Loading -> {
            LazyColumn(
                state = listState, modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

            ) {
                items(5) { ShimmerWishlistItemCard() }
            }
        }

        is WishlistState.Success -> {
            val products = (state as WishlistState.Success).items

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .animateContentSize()
            ) {
                WishlistToggleViewBar(isGridView = isGridView) { isGridView = it }


                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        contentPadding = PaddingValues(2.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            WishlistGridItemCard(
                                product = product,
                                navController = navController,
                                viewModel = wishlistViewModel,
                                showSnackbar = { message, actionLabel, action ->
                                    showSnackbar(message, actionLabel, action)
                                })
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            WishlistItemCardWithAnimation(
                                product = product,
                                navController = navController,
                                viewModel = wishlistViewModel,
                                showSnackbar = { message, actionLabel, action ->
                                    showSnackbar(message, actionLabel, action)
                                })
                        }
                    }
                }
            }
        }

        is WishlistState.Error -> {
            ErrorContent(message = (state as WishlistState.Error).message)
        }
    }
}








