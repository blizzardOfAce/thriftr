package com.example.thriftr.utils

import com.example.thriftr.BuildConfig

object AppWriteConstants {
    const val END_POINT = "https://cloud.appwrite.io/v1"
    const val PROJECT_ID = BuildConfig.APPWRITE_PROJECT_ID
    const val DATABASE_ID = BuildConfig.APPWRITE_DATABASE_ID
    const val USER_COLLECTION_ID = BuildConfig.APPWRITE_USER_COLLECTION_ID
    const val PRODUCT_COLLECTION_ID = BuildConfig.APPWRITE_PRODUCT_COLLECTION_ID
    const val CART_COLLECTION_ID = BuildConfig.APPWRITE_CART_COLLECTION_ID
    const val WISHLIST_COLLECTION_ID = BuildConfig.APPWRITE_WISHLIST_COLLECTION_ID
    const val ORDER_COLLECTION_ID = BuildConfig.APPWRITE_ORDER_COLLECTION_ID
    const val PRODUCT_IMAGE_BUCKET_ID = BuildConfig.APPWRITE_PRODUCT_IMAGE_BUCKET_ID
    const val PROFILE_IMAGE_BUCKET_ID = BuildConfig.APPWRITE_PROFILE_IMAGE_BUCKET_ID
}


