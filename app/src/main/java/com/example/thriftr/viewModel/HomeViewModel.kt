package com.example.thriftr.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thriftr.data.Product
import com.example.thriftr.repository.ProductRepository
import com.example.thriftr.utils.HomeScreenData
import com.example.thriftr.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 4
    }
    var hasShownLoadingAnimation = false

    var isFirstLoad by mutableStateOf(true)  // Track first load

    private val _animatedProductIds = MutableStateFlow(emptySet<String>())
    val animatedProductIds = _animatedProductIds.asStateFlow()

    private val _categoryAnimationCompleted = MutableStateFlow(false)
    val categoryAnimationCompleted = _categoryAnimationCompleted.asStateFlow()

    // Track product animations
    fun markProductAnimated(productId: String) {
        _animatedProductIds.value = _animatedProductIds.value + productId
    }

    fun isProductAnimated(productId: String): Boolean {
        return !isFirstLoad || productId in _animatedProductIds.value
    }

    fun markCategoryAnimationComplete() {
        _categoryAnimationCompleted.value = true
    }

    private val _uiState = MutableStateFlow<UiState<HomeScreenData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val searchResults = _searchResults.asStateFlow()

    val categories = listOf("Home", "Electronics", "Books", "Clothing", "Furniture", "Toys")
    val selectedCategoryIndex = MutableStateFlow(0)

   private val _searchQuery = MutableStateFlow(savedStateHandle["searchQuery"] ?: "")
    val searchQuery = _searchQuery.asStateFlow()

    private val _paginationState = MutableStateFlow(mapOf<Int, PaginationState>())
    val paginationState = _paginationState.asStateFlow()

    private val _productsCache = mutableMapOf<String, Product>()

    enum class SortOption(val displayName: String) {
        PRICE_LOW_TO_HIGH("Price: Low to High"),
        PRICE_HIGH_TO_LOW("Price: High to Low"),
    }

    enum class FilterOption(val displayName: String) {
        FREE_SHIPPING("Free Shipping"),
        IN_STOCK("In Stock"),
        ON_SALE("On Sale"),
    }

    private val _sortState = MutableStateFlow<SortOption?>(null)
    val sortState: StateFlow<SortOption?> = _sortState.asStateFlow()

    private val _filterState = MutableStateFlow<Set<FilterOption>>(emptySet())
    val filterState: StateFlow<Set<FilterOption>> = _filterState.asStateFlow()

    fun applySortAndFilter(products: List<Product>): List<Product> {
        return products
            .filter(::applyFilters)
            .sortedWith { p1, p2 ->
                when (sortState.value) {
                    SortOption.PRICE_LOW_TO_HIGH -> p1.price.compareTo(p2.price)
                    SortOption.PRICE_HIGH_TO_LOW -> p2.price.compareTo(p1.price)
                    null -> 0
                }
            }
    }

    private fun applyFilters(product: Product): Boolean {
        // If no filters are applied, return true to include all products
        if (filterState.value.isEmpty()) {
            return true
        }

        return filterState.value.all { filter ->
            when (filter) {
                FilterOption.FREE_SHIPPING -> product.freeShipping
                FilterOption.IN_STOCK -> product.stock > 0
                FilterOption.ON_SALE -> product.discount!! > 0
            }
        }
    }

    fun setSortOption(option: SortOption?) {
        _sortState.value = option

        // Update current UI state directly
        when {
            searchResults.value is UiState.Success -> {
                val currentResults = (searchResults.value as UiState.Success<List<Product>>).data
                val originalResults = currentResults.toList() // Preserve original list
                _searchResults.value = UiState.Success(applySortAndFilter(originalResults))
            }
            uiState.value is UiState.Success -> {
                val currentData = (uiState.value as UiState.Success<HomeScreenData>).data
                val categoryIndex = selectedCategoryIndex.value
                val originalProducts = categoryProducts[categoryIndex] ?: emptyList()
                _uiState.value = UiState.Success(
                    HomeScreenData(
                        applySortAndFilter(originalProducts),
                        currentData.bestDeals
                    )
                )
            }
        }
    }

    fun toggleFilterOption(option: FilterOption) {
        _filterState.value = if (filterState.value.contains(option)) {
            filterState.value - option
        } else {
            filterState.value + option
        }

        // Update current UI state directly - same logic as setSortOption
        when {
            searchResults.value is UiState.Success -> {
                val currentResults = (searchResults.value as UiState.Success<List<Product>>).data
                val originalResults = currentResults.toList() // Preserve original list
                _searchResults.value = UiState.Success(applySortAndFilter(originalResults))
            }
            uiState.value is UiState.Success -> {
                val currentData = (uiState.value as UiState.Success<HomeScreenData>).data
                val categoryIndex = selectedCategoryIndex.value
                val originalProducts = categoryProducts[categoryIndex] ?: emptyList()
                _uiState.value = UiState.Success(
                    HomeScreenData(
                        applySortAndFilter(originalProducts),
                        currentData.bestDeals
                    )
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = if (query.isEmpty()) UiState.Idle else _searchResults.value
    }

    private val categoryProducts = mutableMapOf<Int, List<Product>>()

    private fun loadCategoryData(index: Int) {
        if (_isLoading.value) {
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Special handling for Home category (index 0)
                // Use null for Home to get all products, otherwise use the category name
                val categoryName = if (index == 0) null else categories[index]

                val products = repo.fetchProductsByCategory(
                    categoryName,
                    1
                ).getOrNull().orEmpty()

                // Cache products individually
                products.forEach { _productsCache[it.id] = it }

                // Update with new list - ensure we don't have duplicates
                categoryProducts[index] = products.distinctBy { it.id }

                // Initialize pagination state with correct values
                updatePaginationState(index) {
                    PaginationState(
                        isLoading = false,
                        currentPage = 1,
                        hasMoreData = products.size >= PAGE_SIZE
                    )
                }

                // Update UI state
                _uiState.value = UiState.Success(
                    HomeScreenData(
                        applySortAndFilter(categoryProducts[index] ?: emptyList()),
                        if (index == 0) bestDeals else emptyList()
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load category: ${e.message}", e)
                _uiState.value = UiState.Error(e.message ?: "Failed to load category")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCachedProduct(productId: String): Product? {
        return _productsCache[productId]
    }

    fun clearSearchResults() {
        _searchQuery.value = ""
        _searchResults.value = UiState.Idle
    }

    fun cacheProducts(products: List<Product>) {
        products.forEach { _productsCache[it.id] = it }
    }

    private val _isLoading = MutableStateFlow(false)
    private var bestDeals = emptyList<Product>()
    internal var initialLoadDone = false
    private val TAG = "HomeViewModel"

    data class PaginationState(
        val isLoading: Boolean = false,
        val currentPage: Int = 0,
        val hasMoreData: Boolean = true
    )

    init {
        viewModelScope.launch {
            // Initialize pagination state for all categories
            categories.forEachIndexed { index, _ ->
                _paginationState.update { current ->
                    current.toMutableMap().apply {
                        if (!this.containsKey(index)) {
                            this[index] = PaginationState(
                                isLoading = false,
                                currentPage = 0,
                                hasMoreData = true
                            )
                        }
                    }
                }
            }

            // Handle search queries - make sure to not clear idle state
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    savedStateHandle["searchQuery"] = query
                    if (query.isEmpty()) {
                        _searchResults.value = UiState.Idle
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun fetchDataIfNeeded() {
        if (!initialLoadDone) {
            loadInitialData()
        }
    }

    private fun loadInitialData() {
        if (_isLoading.value) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (bestDeals.isEmpty()) {
                    bestDeals = repo.fetchBestDeals(1).getOrNull().orEmpty()
                }
                loadCategoryData(0)
                initialLoadDone = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load initial data: ${e.message}", e)
                _uiState.value = UiState.Error(e.message ?: "Failed to load data")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(index: Int) {
        if (selectedCategoryIndex.value == index) return

        selectedCategoryIndex.value = index

        // Reset UI state to loading when switching categories
        _uiState.value = UiState.Loading

        // Check if we have data in cache and whether the pagination is initialized correctly
        val currentPaginationState = paginationState.value[index]

        if (categoryProducts[index]?.isNotEmpty() == true && currentPaginationState?.currentPage != 0) {
            // We have data and pagination is properly initialized, just update UI
            updateUiStateWithLoadedData(index)
        } else {
            // Either no data or pagination not initialized, reload data
            loadCategoryData(index)
        }
    }

    fun loadMoreProducts() {
        val category = selectedCategoryIndex.value
        val currentState = paginationState.value[category] ?: PaginationState()

        if (currentState.isLoading || !currentState.hasMoreData) {
            return
        }

        viewModelScope.launch {
            updatePaginationState(category) { it.copy(isLoading = true) }
            try {
                val nextPage = currentState.currentPage + 1

                // Special handling for Home category
                val categoryName = if (category == 0) null else categories[category]

                val newProducts = repo.fetchProductsByCategory(
                    category = categoryName,
                    page = nextPage
                ).getOrNull().orEmpty()

                // Cache individual products
                newProducts.forEach { _productsCache[it.id] = it }

                // Create a set of existing product IDs to avoid duplicates
                val existingIds = (categoryProducts[category] ?: emptyList()).map { it.id }.toSet()

                // Only add products that don't already exist
                val uniqueNewProducts = newProducts.filter { it.id !in existingIds }

                // Merge existing and new products immutably
                val existingProducts = categoryProducts[category] ?: emptyList()
                val mergedProducts = existingProducts + uniqueNewProducts

                categoryProducts[category] = mergedProducts

                updatePaginationState(category) {
                    it.copy(
                        currentPage = nextPage,
                        hasMoreData = newProducts.size >= PAGE_SIZE,
                        isLoading = false
                    )
                }

                // Update UI with the new data
                updateUiStateWithLoadedData(category)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to load more products: ${e.message}", e)
                updatePaginationState(category) { it.copy(isLoading = false) }
            }
        }
    }

    // Update UI state with current category's products
    private fun updateUiStateWithLoadedData(categoryIndex: Int) {
        val productsForCategory = categoryProducts[categoryIndex] ?: emptyList()
        _uiState.value = UiState.Success(
            HomeScreenData(
                applySortAndFilter(productsForCategory),
                if (categoryIndex == 0) bestDeals else emptyList()
            )
        )
    }

    private fun updatePaginationState(category: Int, update: (PaginationState) -> PaginationState) {
        _paginationState.update { currentState ->
            currentState.toMutableMap().apply {
                this[category] = update(this[category] ?: PaginationState())
            }
        }
    }

    private suspend fun performSearch(query: String) {
        if (_isLoading.value) return

        _searchResults.value = UiState.Loading
        _isLoading.value = true
        try {
            val searchResults = repo.searchProducts(query)
            cacheProducts(searchResults)
            _searchResults.value = if (searchResults.isEmpty())
                UiState.Error("No results found")
            else
                UiState.Success(applySortAndFilter(searchResults))
        } catch (e: Exception) {
            _searchResults.value = UiState.Error(e.message ?: "Search failed")
        } finally {
            _isLoading.value = false
        }
    }
}