package com.example.thriftr.data

import android.util.Log

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val imagePath: String?,
    val savedAddresses: List<String>
) {
    constructor() : this("", "", "", "", null, emptyList())

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "Id" to id,
            "FirstName" to firstName,
            "LastName" to lastName,
            "Email" to email,
            "SavedAddresses" to savedAddresses
        )
        imagePath?.let { map["ImagePath"] = it }

        return map
    }

    fun getAddresses(): List<Address> = savedAddresses.mapNotNull { Address.fromJson(it) }

    fun getDefaultAddress(): Address? = getAddresses().firstOrNull { it.isDefault }

    companion object {
        fun fromMap(map: Map<String, Any>): User? {
            return try {
                User(
                    id = map["Id"] as String,
                    firstName = map["FirstName"] as String,
                    lastName = map["LastName"] as String,
                    email = map["Email"] as String,
                    imagePath = map["ImagePath"] as? String,
                    savedAddresses = (map["SavedAddresses"] as? List<String>) ?: emptyList()
                )
            } catch (e: Exception) {
                Log.e("User.fromMap", "Error mapping user: ${e.message}")
                null
            }
        }
    }
}

