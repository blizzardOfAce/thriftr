package com.example.thriftr.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _darkThemeEnabled = MutableStateFlow<Boolean?>(null) // `null` means follow system
    val darkThemeEnabled = _darkThemeEnabled.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(false) // `null` means follow system
    val notificationEnabled = _notificationEnabled.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        _darkThemeEnabled.value = enabled // User manually toggled
    }

    fun resetToSystemTheme() {
        _darkThemeEnabled.value = null // Follow system again
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationEnabled.value = enabled
    }

}
