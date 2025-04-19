package com.example.thriftr.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.thriftr.data.Product
import com.example.thriftr.data.toProduct
import com.example.thriftr.utils.AppWriteConstants
import com.example.thriftr.utils.AppWriteConstants.DATABASE_ID
import com.example.thriftr.utils.AppWriteConstants.END_POINT
import com.example.thriftr.utils.AppWriteConstants.PRODUCT_COLLECTION_ID
import com.example.thriftr.utils.AppWriteConstants.PRODUCT_IMAGE_BUCKET_ID
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val databases: Databases
) {

    companion object {
        private const val PAGE_SIZE = 4
        private const val BEST_DEALS_CATEGORY = "Best Deals"
    }


    suspend fun fetchBestDeals(page: Int): Result<List<Product>> = runCatching {
        queryProducts(
            category = BEST_DEALS_CATEGORY,
            page = page,
            excludeCategory = null
        )
    }

    suspend fun fetchProductsByCategory(
        category: String?,
        page: Int,
        excludeCategory: String? = BEST_DEALS_CATEGORY
    ): Result<List<Product>> = runCatching {
        queryProducts(category, page, excludeCategory)
    }


    suspend fun searchProducts(query: String): List<Product> = runCatching {
        databases.listDocuments(
            databaseId = DATABASE_ID,
            collectionId = PRODUCT_COLLECTION_ID,
            queries = listOf(
                Query.or(
                    listOf(
                        Query.search("name", query),
                        Query.search("category", query)
                    )
                )
            )
        ).documents.map { it.toProduct() }
    }.getOrElse { emptyList() }

    private suspend fun queryProducts(
        category: String?,
        page: Int,
        excludeCategory: String?
    ): List<Product> {
        val queries = buildList {
            category?.let { add(Query.equal("category", it)) }
            excludeCategory?.let { add(Query.notEqual("category", it)) }
            add(Query.limit(PAGE_SIZE))
            add(Query.offset(maxOf(0, (page - 1) * PAGE_SIZE)))

        }

        return databases.listDocuments(
            databaseId = DATABASE_ID,
            collectionId = PRODUCT_COLLECTION_ID,
            queries = queries
        ).documents.map { it.toProduct() }
    }


    fun compressImages(context: Context, imageUris: List<Uri>): List<ByteArray> {
        return imageUris.mapNotNull { uri ->
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                70,
                outputStream
            ) // Adjust compression quality if needed
            outputStream.toByteArray()
        }
    }

    //Function that takes list of byte arrays, uploads them to the appwrite storage
    // and returns their url as a list of strings:
    suspend fun uploadImagesToAppwrite(
        client: Client,
        bucketId: String,
        imageByteArrays: List<ByteArray>,
        productName: String,
    ): List<String> {
        val storage = Storage(client)
        val imageUrls = mutableListOf<String>()

        for ((index, imageData) in imageByteArrays.withIndex()) {
            try {
                // Create a filename based on the product name and image index
                val filename = "$productName-$index.jpg"

                // Create an InputFile object from the byte array
                val inputFile = InputFile.fromBytes(
                    bytes = imageData,
                    filename = filename,
                    mimeType = "image/jpeg"
                )

                // Upload the file to Appwrite Storage
                val result = withContext(Dispatchers.IO) {
                    storage.createFile(
                        bucketId = bucketId,
                        fileId = ID.unique(),
                        file = inputFile,
//                        permissions = listOf(
//                            Permission.read(Role.any()),
//                            Permission.create(Role.user(userId)),
//                            Permission.update(Role.user(userId)),
//                            Permission.delete(Role.user(userId))
//                        )
                    )
                }
                // Construct the download URL
                val imageUrl =
                    "$END_POINT/storage/buckets/$PRODUCT_IMAGE_BUCKET_ID/files/${result.id}/view?project=${AppWriteConstants.PROJECT_ID}&mode=admin"
                imageUrls.add(imageUrl)
            } catch (e: AppwriteException) {
                e.printStackTrace()
            }
        }

        return imageUrls
    }

    suspend fun saveProductToCollection(product: Product) {
        val randomId = ID.unique()

        val documentData = mapOf(
            "id" to randomId,
            "name" to product.name,
            "category" to product.category,
            "price" to product.price,
            "discount" to (product.discount ?: 0.0f),
            "description" to (product.description ?: ""),
            "details" to (product.details ?: ""),
            "colors" to (product.colors ?: emptyList()),
            "sizes" to (product.sizes ?: emptyList()),
            "freeShipping" to product.freeShipping,
            "stock" to product.stock,
            "images" to (product.images)
        )

        try {
            databases.createDocument(
                collectionId = PRODUCT_COLLECTION_ID,
                documentId = randomId,
                databaseId = DATABASE_ID,
                data = documentData,
//                permissions = listOf(
//                    Permission.read(Role.any()),
//                    Permission.create(Role.user(userId)),
//                    Permission.update(Role.user(userId)),
//                    Permission.delete(Role.user(userId))
//                )
            )
        } catch (e: AppwriteException) {
            e.printStackTrace()
        }
    }
}
