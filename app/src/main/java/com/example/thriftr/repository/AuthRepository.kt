package com.example.thriftr.repository

import com.example.thriftr.utils.AppWriteConstants.DATABASE_ID
import com.example.thriftr.utils.AppWriteConstants.USER_COLLECTION_ID
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton


/* Todo: Maybe optimize the authState to keep only userId instead of full user object */

@Singleton
class AuthRepository @Inject constructor(private val appwriteClient: Client) {

    private val accountService by lazy { Account(appwriteClient) }
    private val database by lazy { Databases(appwriteClient) }

    private val _authState = MutableStateFlow<User<Map<String, Any>>?>(null)
    val authState: StateFlow<User<Map<String, Any>>?> = _authState.asStateFlow()


    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    suspend fun checkCurrentSession() {
        _isCheckingSession.value = true
        val user = withContext(Dispatchers.IO) {
            runCatching {
                withTimeoutOrNull(10000) { // Timeout after 10 seconds
                    accountService.get()
                }
            }.getOrNull()
        }
        _authState.value = user
        _isCheckingSession.value = false
    }


    suspend fun login(email: String, password: String): Result<Unit> {
        return runCatching {
            accountService.createEmailPasswordSession(email, password)
            _authState.value = accountService.get()

        }
    }

    suspend fun register(email: String, password: String): Result<Unit> {
        return runCatching {

            val authUser = accountService.create(ID.unique(), email, password)

            val initialUser = com.example.thriftr.data.User(
                id = authUser.id,
                firstName = "",
                lastName = "",
                email = email,
                imagePath = null,
                savedAddresses = emptyList()
            )

            database.createDocument(
                databaseId = DATABASE_ID,
                collectionId = USER_COLLECTION_ID,
                documentId = authUser.id,
                data = initialUser.toMap(),
//                permissions = listOf(
//                    Permission.read(Role.user(authUser.id)),
//                    Permission.update(Role.user(authUser.id)),
//                    Permission.delete(Role.user(authUser.id))
//                )
            )

        }
    }

    suspend fun logout(): Result<Unit> {
        return runCatching {
            accountService.deleteSession("current")
            _authState.value = null
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return runCatching {
            // Create a recovery URL
            val redirectUrl = "https://cloud.appwrite.io"

            // Trigger password recovery
            accountService.createRecovery(
                email = email,
                url = redirectUrl
            )
        }
    }
}
