package com.example.thriftr.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.thriftr.data.User
import com.example.thriftr.utils.AppWriteConstants.DATABASE_ID
import com.example.thriftr.utils.AppWriteConstants.END_POINT
import com.example.thriftr.utils.AppWriteConstants.PROFILE_IMAGE_BUCKET_ID
import com.example.thriftr.utils.AppWriteConstants.PROJECT_ID
import com.example.thriftr.utils.AppWriteConstants.USER_COLLECTION_ID
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    client: Client
) {
    private val database = Databases(client)
    private val storage = Storage(client)

    suspend fun getUser(userId: String): User? {
        return try {
            val document = database.getDocument(
                databaseId = DATABASE_ID,
                collectionId = USER_COLLECTION_ID,
                documentId = userId
            )
            User.fromMap(document.data)

        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error mapping user: ${e.message}")
            null
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val userData = user.toMap()

            database.updateDocument(
                databaseId = DATABASE_ID,
                collectionId = USER_COLLECTION_ID,
                documentId = user.id,
                data = userData
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(context: Context, uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return Result.failure(
                Exception("Invalid image")
            )
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: return Result.failure(
                Exception(
                    "Can't decode image"
                )
            )
            withContext(Dispatchers.IO) {
                inputStream.close()
            }

            // Compress image before upload
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val compressedBytes = byteArrayOutputStream.toByteArray()

            val fileId = ID.unique()
            val result = storage.createFile(
                bucketId = PROFILE_IMAGE_BUCKET_ID,
                fileId = fileId,
                file = InputFile.fromBytes(compressedBytes, "$fileId.jpg", "image/jpeg")
            )
            // Construct the correct URL
            Result.success("$END_POINT/storage/buckets/$PROFILE_IMAGE_BUCKET_ID/files/${result.id}/view?project=$PROJECT_ID&mode=admin")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
