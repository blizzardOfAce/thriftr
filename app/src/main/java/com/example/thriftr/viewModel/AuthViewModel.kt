package com.example.thriftr.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thriftr.repository.AuthRepository
import com.example.thriftr.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appwrite.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    val authState: StateFlow<User<Map<String, Any>>?> = authRepository.authState

    val isCheckingSession: StateFlow<Boolean> = authRepository.isCheckingSession

    init {
        checkSessionIfNeeded()
    }

    private fun checkSessionIfNeeded() {
       // if (isCheckingSession.value) return // Prevent multiple calls
        viewModelScope.launch {
            authRepository.checkCurrentSession()
        }
    }

    fun login(email: String, password: String) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            _uiState.value = UiState.Error("Invalid credentials")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.login(email, password)
                .onSuccess { _uiState.value = UiState.Success(Unit)
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(email: String, password: String) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            _uiState.value = UiState.Error("Invalid credentials")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.register(email, password)
                .onSuccess { _uiState.value = UiState.Success(Unit) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Registration failed") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.logout()
                .onSuccess {
                    _uiState.value = UiState.Idle
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Logout failed") }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun isValidPassword(password: String) = password.length >= 8

    fun resetPasswordWithEmail(email: String) {
        if (!isValidEmail(email)) {
            _uiState.value = UiState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = UiState.Success(Unit)
                    // Optional: Show a specific message for password reset
                    _uiState.value = UiState.Error("Password reset link sent to your email")
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.message ?: "Failed to send reset link")
                }
        }
    }
}
