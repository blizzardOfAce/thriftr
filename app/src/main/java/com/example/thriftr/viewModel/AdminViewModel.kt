package com.example.thriftr.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.thriftr.data.Product
import com.example.thriftr.repository.ProductRepository
import com.example.thriftr.utils.AppWriteConstants.PRODUCT_IMAGE_BUCKET_ID
import com.example.thriftr.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.appwrite.Client
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: ProductRepository,
    @ApplicationContext private val context: Context,
    private val client: Client
) : ViewModel() {

    var productName by mutableStateOf("")
        private set
    var price by mutableStateOf("")
        private set
    var stock by mutableIntStateOf(1)
        private set
    var freeShipping by mutableStateOf(false)
        private set
    var discount by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var details by mutableStateOf("")
        private set
    var category by mutableStateOf("")
        private set
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())
        private set

    private val _selectedColors = mutableStateListOf<ColorInfo>()
    val selectedColors: List<ColorInfo> = _selectedColors

    private val _sizes = mutableStateOf("")
    val sizes: State<String> = _sizes

    data class ColorInfo(
        val name: String,
        val hexCode: String
    )

    var uiState by mutableStateOf<UiState<Unit>>(UiState.Idle)
        private set

    fun addColor(colorInfo: ColorInfo) {
        if (!_selectedColors.contains(colorInfo)) {
            _selectedColors.add(colorInfo)
        }
    }

    fun removeColor(colorInfo: ColorInfo) {
        _selectedColors.remove(colorInfo)
    }


    fun updateSizes(newSizes: String) {
        _sizes.value = newSizes
    }

    fun updateProductName(name: String) {
        productName = name
    }

    fun updatePrice(newPrice: String) {
        if (newPrice.isEmpty() || newPrice.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
            price = newPrice
        }
    }

    fun updateStock(newStock: Int) {
        stock = maxOf(1, newStock)
    }

    fun updateFreeShipping(enabled: Boolean) {
        freeShipping = enabled
    }

    fun updateDiscount(newDiscount: String) {
        if (newDiscount.isEmpty() || newDiscount.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
            discount = newDiscount
        }
    }

    fun updateDescription(desc: String) {
        description = desc
    }

    fun updateDetails(newDetails: String) {
        details = newDetails
    }

    fun updateCategory(newCategory: String) {
        category = newCategory
    }

    fun addImage(uri: Uri) {
        selectedImages = selectedImages + uri
    }

    fun removeImage(uri: Uri) {
        selectedImages = selectedImages - uri
    }

    private fun isValidProduct(): Boolean {
        return productName.isNotBlank() &&
                price.isNotBlank() &&
                category.isNotBlank() &&
                selectedImages.isNotEmpty()
    }

    suspend fun saveProduct(): Result<Unit> {
        if (!isValidProduct()) {
            return Result.failure(IllegalStateException("Please fill in all required fields"))
        }

        return try {
            uiState = UiState.Loading

            // First compress and upload images
            val compressedImages = repository.compressImages(context, selectedImages)
            val imageUrls = repository.uploadImagesToAppwrite(
                client = client,
                bucketId = PRODUCT_IMAGE_BUCKET_ID,
                imageByteArrays = compressedImages,
                productName = productName
            )

            if (imageUrls.isEmpty()) {
                uiState = UiState.Error("Failed to upload images")
                return Result.failure(Exception("Failed to upload images"))
            }

            val sizesArray = _sizes.value.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toList()

            // Create product with uploaded image URLs
            val product = Product(
                id = UUID.randomUUID().toString(),
                name = productName,
                price = price.toFloatOrNull() ?: 0f,
                stock = stock,
                colors = selectedColors.map { "${it.name}:${it.hexCode}" }.toList(),
                freeShipping = freeShipping,
                sizes = sizesArray,
                discount = discount.toFloatOrNull(),
                details = details.takeIf { it.isNotBlank() },
                description = description.takeIf { it.isNotBlank() },
                category = category,
                images = imageUrls
            )

            // Save product to database
            repository.saveProductToCollection(product = product)
            uiState = UiState.Success(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            uiState = UiState.Error(e.message ?: "Unknown error occurred")
            Result.failure(e)
        }
    }

    fun resetState() {
        productName = ""
        price = ""
        stock = 1
        freeShipping = false
        discount = ""
        description = ""
        details = ""
        category = ""
        selectedImages = emptyList()
        uiState = UiState.Idle
    }
}

