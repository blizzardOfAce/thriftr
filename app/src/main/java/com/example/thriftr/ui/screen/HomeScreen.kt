package com.example.thriftr.ui.screen

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thriftr.R
import com.example.thriftr.data.Product
import com.example.thriftr.utils.HomeScreenData
import com.example.thriftr.utils.UiState
import com.example.thriftr.utils.components.CustomBottomNavBar
import com.example.thriftr.utils.components.ErrorContent
import com.example.thriftr.utils.components.NavigationDrawer
import com.example.thriftr.utils.components.homescreen.AnimatedBestDealsCarousel
import com.example.thriftr.utils.components.homescreen.AnimatedProductCard
import com.example.thriftr.utils.components.homescreen.CategoryRowWithPlaceholder
import com.example.thriftr.utils.components.homescreen.DealsHeader
import com.example.thriftr.utils.components.homescreen.ProductCard
import com.example.thriftr.utils.components.homescreen.SearchBar
import com.example.thriftr.utils.components.homescreen.ShimmerPlaceholder
import com.example.thriftr.utils.components.homescreen.SortFilterDialog
import com.example.thriftr.utils.components.homescreen.SortFilterMode
import com.example.thriftr.utils.components.homescreen.TopSection
import com.example.thriftr.viewModel.HomeViewModel
import com.example.thriftr.viewModel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navController: NavHostController,
    onClickProductItem: (Product) -> Unit,
    onButtonClick: (Product) -> Unit,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel
) {
    val userData by profileViewModel.userData.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val searchState by homeViewModel.searchResults.collectAsStateWithLifecycle()
    val paginationState by homeViewModel.paginationState.collectAsStateWithLifecycle()

    val selectedCategory by homeViewModel.selectedCategoryIndex.collectAsStateWithLifecycle()
    val categories = homeViewModel.categories

    var showSortFilterDialog by remember { mutableStateOf<SortFilterMode?>(null) }
    val currentSort by homeViewModel.sortState.collectAsStateWithLifecycle()
    val currentFilters by homeViewModel.filterState.collectAsStateWithLifecycle()

    var isFirstLoad = homeViewModel.isFirstLoad

    LaunchedEffect(Unit) {
        homeViewModel.fetchDataIfNeeded()
        profileViewModel.fetchProfileIfNeeded()
        if (!homeViewModel.hasShownLoadingAnimation) {
            isFirstLoad = true
            homeViewModel.hasShownLoadingAnimation = true
        } else {
            isFirstLoad = false
        }
    }

    val gridState = rememberLazyStaggeredGridState()
    val isSearchActive by remember {
        derivedStateOf { searchState !is UiState.Idle }
    }

    if (showSortFilterDialog != null) {
        SortFilterDialog(
            mode = showSortFilterDialog!!,
            currentSort = currentSort,
            currentFilters = currentFilters,
            onDismiss = { showSortFilterDialog = null },
            onSortSelected = homeViewModel::setSortOption,
            onFilterToggled = homeViewModel::toggleFilterOption
        )
    }

    var backPressedTime by remember { mutableLongStateOf(0L) }
    val backPressThreshold = 2000L  // 2 seconds
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    BackHandler {
        if (isSearchActive) {
            // First back press clears search and closes keyboard
            focusManager.clearFocus()
            homeViewModel.clearSearchResults()
        } else {
            // Double back press to exit
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < backPressThreshold) {
                // Exit the app
                val activity = (context as? ComponentActivity)
                activity?.finish()
            } else {
                // Show toast and update time
                backPressedTime = currentTime
                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fix best deals display issue
    val shouldShowBestDeals = remember {
        derivedStateOf {
            uiState is UiState.Success && (uiState as? UiState.Success<HomeScreenData>)?.data?.bestDeals?.isNotEmpty() == true
        }
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collectLatest { lastVisibleIndex ->
                val totalItems = gridState.layoutInfo.totalItemsCount
                val state = paginationState[selectedCategory]

                if (lastVisibleIndex != null && totalItems > 0 && lastVisibleIndex >= totalItems - 2) {
                    if (state?.hasMoreData == true && !state.isLoading) {
                        homeViewModel.loadMoreProducts()
                    }
                }
            }
    }

    //Not used
    val customFlingBehavior = remember {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                var remainingVelocity = initialVelocity
                while (remainingVelocity > 1f) {
                    val delta = remainingVelocity * 0.1f // Control the deceleration rate
                    scrollBy(delta)
                    remainingVelocity *= 0.9f // Reduce velocity gradually
                }
                return remainingVelocity
            }
        }
    }

    NavigationDrawer(
        onNavigate = { navController.navigate(it) },
        profileData = userData,
        onClose = { scope.launch { drawerState.close() } },
        drawerState = drawerState
    ) {

//      Will be handled later(i guess):
//      val showBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route == Screens.HomeScreen.route

        Scaffold(
            bottomBar = {
//            AnimatedVisibility(
//                visible = showBottomBar,
//                enter = slideInVertically { it / 2 } + fadeIn(
//                    animationSpec = tween(
//                        100
//                    )
//                ),
//                exit = fadeOut(animationSpec = tween(100)) + slideOutVertically { it / 2 }
//            ) {
                CustomBottomNavBar(navController)
                //           }
            }) { paddingValues ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(200.dp),
                state = gridState,
                userScrollEnabled = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                flingBehavior = ScrollableDefaults.flingBehavior()

            ) {

                item(span = StaggeredGridItemSpan.FullLine) {
                    TopSection(
                        navController = navController,
                        imageUrl = userData.imagePath ?: "NULL",
                        toggleNavDrawer = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        })
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    SearchBar(
                        onSearchQueryChange = homeViewModel::updateSearchQuery,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        viewModel = homeViewModel

                    )
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    DealsHeader(
                        title = when {
                        isSearchActive -> when (searchState) {
                            is UiState.Success -> "Search Results (${(searchState as UiState.Success<List<Product>>).data.size})"
                            is UiState.Loading -> "Searching..."
                            else -> "Search"
                        }

                        else -> categories[selectedCategory]
                    },
                        onSortClick = { showSortFilterDialog = SortFilterMode.SORT },
                        onFilterClick = { showSortFilterDialog = SortFilterMode.FILTER },
                        sortOption = currentSort,
                        filterOptions = currentFilters
                    )
                }

                // Dynamic Content based on search or category
                if (isSearchActive) {
                    handleSearchContent(
                        state = searchState,
                        homeViewModel = homeViewModel,
                        onClick = onClickProductItem
                    )
                } else {
                    handleCategoryContent(
                        uiState = uiState,
                        selectedCategory = selectedCategory,
                        categories = categories,
                        homeViewModel = homeViewModel,
                        isFirstLoad = isFirstLoad,
                        isVisible = shouldShowBestDeals.value,
                        onClickDealsButton = onButtonClick,
                        onClickProductItem = onClickProductItem
                    )
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    val currentPaginationState = paginationState[selectedCategory]

                    // Only show pagination loading indicator if we're not in initial loading state
                    if (currentPaginationState?.isLoading == true && uiState !is UiState.Loading) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                // Make sure this isn't covered by animations
                                .zIndex(1f)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    } else if (currentPaginationState?.hasMoreData == true && !isSearchActive) {
                        Spacer(modifier = Modifier.height(80.dp))
                    } else if (!isSearchActive && uiState is UiState.Success<*> && (uiState as? UiState.Success<HomeScreenData>)?.data?.products?.isNotEmpty() == true) {
                        // If we have products but no more to load, show an end of list indicator
                        Text(
                            "End of results",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

            }
        }
    }
}


private fun LazyStaggeredGridScope.handleSearchContent(
    state: UiState<List<Product>>, homeViewModel: HomeViewModel, onClick: (Product) -> Unit
) {
    when (state) {
        is UiState.Success -> {
            val products = state.data
            homeViewModel.cacheProducts(products)

            if (products.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        "No search results found",
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(
                    items = products, key = { _, p -> p.id }) { index, product ->
                    ProductCard(
                        modifier = Modifier, product = product, onClick = onClick
                    )
                }
            }
        }

        is UiState.Loading -> items(count = 4) {
            ShimmerPlaceholder(
                Modifier
                    .fillMaxWidth()
                    .height((250..300).random().dp)
                    .padding(4.dp)
            )
        }

        is UiState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
            ErrorContent(state.message)
        }

        UiState.Idle -> Unit
    }
}

private fun LazyStaggeredGridScope.handleCategoryContent(
    uiState: UiState<HomeScreenData>,
    selectedCategory: Int,
    categories: List<String>,
    homeViewModel: HomeViewModel,
    isFirstLoad: Boolean,
    isVisible: Boolean,
    onClickDealsButton: (Product) -> Unit,
    onClickProductItem: (Product) -> Unit,
) {
    item(span = StaggeredGridItemSpan.FullLine) {

        CategoryRowWithPlaceholder(
            isLoading = uiState is UiState.Loading,
            images = listOf(
                R.drawable.home,
                R.drawable.electronics,
                R.drawable.books,
                R.drawable.clothing,
                R.drawable.furniture,
                R.drawable.best_deals
            ),
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = homeViewModel::selectCategory,
            homeViewModel = homeViewModel
        )
    }

    item(span = StaggeredGridItemSpan.FullLine) {
        if (uiState is UiState.Loading && (selectedCategory == 0)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(8.dp)
                    .border(
                        width = 0.5.dp, color = Color.DarkGray, shape = RoundedCornerShape(12.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val bestDealsData =
                (uiState as? UiState.Success<HomeScreenData>)?.data?.bestDeals ?: emptyList()
            AnimatedBestDealsCarousel(
                products = bestDealsData,
                onButtonClick = onClickDealsButton,
                isVisible = isVisible,
                isFirstLoad = isFirstLoad
            )
        }
    }

    // Show products or loading state
    when (uiState) {
        is UiState.Success -> {
            val products = uiState.data.products

            if (products.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        "No products available",
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(products, key = { _, p -> p.id }) { index, product ->
                    AnimatedProductCard(
                        index = index,
                        modifier = Modifier,
                        product = product,
                        onClick = onClickProductItem,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }

        is UiState.Loading -> {
            items(4) {
                ShimmerPlaceholder(
                    Modifier
                        .fillMaxWidth()
                        .height((250..300).random().dp)
                        .padding(4.dp)
                )
            }
        }

        is UiState.Error -> {
            item(span = StaggeredGridItemSpan.FullLine) {
                ErrorContent(uiState.message)
            }
        }

        else -> Unit
    }
}


//Not used
object PaginationUtilsWorking {
    fun shouldLoadMore(
        gridState: LazyStaggeredGridState, isLoadingMore: Boolean, hasMoreData: Boolean
    ): Boolean {
        if (!hasMoreData || isLoadingMore) return false

        return gridState.run {
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@run false

            // Check if we can scroll at all
            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            val totalHeight = layoutInfo.viewportSize.height
            val canScroll = totalHeight > viewportHeight

            if (!canScroll) {
                // If all items fit in viewport, load more immediately
                true
            } else {
                // Original scroll-based check
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index?.let { lastIndex ->
                    lastIndex >= totalItems - 2
                } == true
            }
        }
    }
}








