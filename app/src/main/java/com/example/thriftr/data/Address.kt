package com.example.thriftr.data

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class Address(
    val id: String = UUID.randomUUID().toString(),
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean = false
) {
    fun toJson(): String = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String): Address? {
            return try {
                Json.decodeFromString<Address>(json)
            } catch (e: Exception) {
                Log.e("Address.fromJson", "Error mapping Address: ${e.message}")
                null
            }
        }
    }
}
