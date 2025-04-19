package com.example.thriftr.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thriftr.data.Address
import com.example.thriftr.data.User
import com.example.thriftr.repository.AuthRepository
import com.example.thriftr.repository.ProfileRepository
import com.example.thriftr.utils.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _userData = MutableStateFlow(User())
    val userData: StateFlow<User> = _userData.asStateFlow()

   val userId: String?
       get() = authRepository.authState.value?.id

    private val _animationState = MutableStateFlow(emptySet<Int>())
    val animationState: StateFlow<Set<Int>> = _animationState.asStateFlow()

    fun markAnimated(index: Int) {
        _animationState.update { it + index }
    }


    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var addresses by mutableStateOf<List<Address>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun fetchProfileIfNeeded() {
        val currentState = _state.value
        if (currentState !is ProfileState.Success) {  // Fetch only if not already loaded
            loadUserData()
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            try {
                val user = repo.getUser(userId?:"NULL") ?: throw Exception("User not found")
                _userData.value = user
                firstName = user.firstName
                lastName = user.lastName
                imageUrl = user.imagePath ?: "NULL"
                addresses = user.getAddresses()
                _state.value = ProfileState.Success(user)
            } catch (e: Exception) {
                _state.value = ProfileState.Error(e.message ?: "Error loading profile")
            }
        }
    }

    fun updateAddress(updated: Address) {

        // If addresses is empty, treat it as an add operation
        if (addresses.isEmpty()) {
            addAddress(updated)
            return
        }

        // If the address ID doesn't exist in current addresses, add it
        if (addresses.none { it.id == updated.id }) {
            addAddress(updated)
            return
        }

        // Otherwise, update existing address
        val newAddresses = addresses.map {
            when {
                it.id == updated.id -> {
                    updated
                }

                updated.isDefault -> {
                    it.copy(isDefault = false)
                }

                else -> it
            }
        }
        addresses = newAddresses
    }

    fun addAddress(newAddress: Address) {

        val addressToAdd = if (newAddress.id.isEmpty()) {
            newAddress.copy()
        } else {
            newAddress
        }

        addresses = if (addressToAdd.isDefault) {
            val updatedAddresses = addresses.map { it.copy(isDefault = false) } + addressToAdd
            updatedAddresses
        } else {
            val updatedAddresses = addresses + addressToAdd
            updatedAddresses
        }
    }


    fun deleteAddress(addressId: String) {
        addresses = addresses.filter { it.id != addressId }
    }

    fun saveProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val currentUser = (_state.value as? ProfileState.Success)?.user
                    ?: throw Exception("User not loaded")

                if (addresses.isEmpty()) {

                                   }

                val addressesJson = addresses.map {
                    it.toJson().also { json ->
                    }
                }

                val updatedUser = currentUser.copy(
                    firstName = firstName,
                    lastName = lastName,
                    savedAddresses = addressesJson
                )

                val result = repo.updateUser(updatedUser)
                if (result.isSuccess) {
                    loadUserData() // Refresh data
                } else {
                    _state.value = ProfileState.Error("Failed to save profile")
                }
            } catch (e: Exception) {
                _state.value = ProfileState.Error(e.message ?: "Save failed")
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = repo.uploadProfileImage(uri = uri, context = context)
                if (result.isSuccess) {
                    val currentUser = (_state.value as? ProfileState.Success)?.user
                        ?: return@launch
                    val updatedUser = currentUser.copy(imagePath = result.getOrNull())
                    repo.updateUser(updatedUser)
                    loadUserData()
                }
            } catch (e: Exception) {
                _state.value = ProfileState.Error("Upload failed: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
