package com.example.thriftr.services

import android.content.Context
import com.example.thriftr.BuildConfig
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases


object AppwriteSingleton {
    private const val END_POINT = "https://cloud.appwrite.io/v1"

    lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint(END_POINT)
            .setProject(BuildConfig.APPWRITE_PROJECT_ID)

        account = Account(client)
        databases = Databases(client)
    }
}





