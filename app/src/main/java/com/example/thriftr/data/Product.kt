package com.example.thriftr.data

import android.os.Parcelable
import io.appwrite.models.Document
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val freeShipping: Boolean = false,
    val stock: Int = 1,
    val discount: Float? = null,
    val description: String? = null,
    val details: String? = null,
    val colors: List<String>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
) : Parcelable

fun Document<Map<String, Any>>.toProduct(): Product {
    return Product(
        id = this.id,
        name = this.data["name"] as? String ?: "Unknown",
        description = this.data["description"] as? String ?: "No description",
        price = (this.data["price"] as? Number)?.toFloat() ?: 0f,
        discount = (this.data["discount"] as? Number)?.toFloat(),  // Allow null
        freeShipping = (this.data["freeShipping"] as? Boolean) ?: false, // Safe cast with default
        stock = (this.data["stock"] as? Number)?.toInt() ?: 1, // Safe cast with default
        images = (this.data["images"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        sizes = (this.data["sizes"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        colors = (this.data["colors"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        category = this.data["category"] as? String ?: "Uncategorized",
        details = this.data["details"] as? String ?: "No details available"
    )
}


