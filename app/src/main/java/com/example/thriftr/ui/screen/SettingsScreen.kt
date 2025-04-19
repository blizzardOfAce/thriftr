package com.example.thriftr.ui.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.thriftr.Screens
import com.example.thriftr.utils.components.settingsscreen.SettingsItem
import com.example.thriftr.utils.components.settingsscreen.SettingsSwitchItem
import com.example.thriftr.viewModel.AuthViewModel
import com.example.thriftr.viewModel.SettingsViewModel

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    onThemeToggle: (Boolean) -> Unit
) {
    val systemTheme = isSystemInDarkTheme() // Get system theme
    val darkThemePref by settingsViewModel.darkThemeEnabled.collectAsStateWithLifecycle()

    // If `null`, follow system theme, else use user choice
    val darkThemeEnabled = darkThemePref ?: systemTheme

    val notificationEnabled by settingsViewModel.notificationEnabled.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { SettingsCategory(title = "Account") }

        item {
            SettingsItem(
                icon = Icons.Default.Key,
                title = "Change Password",
                onClick = { navController.navigate(Screens.EditProfileScreen.route) }
            )
        }

        item { SettingsCategory(title = "Preferences") }

        item {
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Theme",
                checked = darkThemeEnabled,
                onCheckedChange = {
                    if (darkThemePref == null) {
                        settingsViewModel.setDarkTheme(it) // User manually set it
                    } else {
                        settingsViewModel.resetToSystemTheme() // Reset to system theme
                    }
                    onThemeToggle(it) // Notify MainActivity
                }
            )
        }

        item {
            SettingsSwitchItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                checked = notificationEnabled,
                onCheckedChange = { settingsViewModel.toggleNotifications(it) }
            )
        }

        item { SettingsCategory(title = "About") }

        item {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About Us",
                onClick = { /* Handle click */ }
            )
        }

        item {
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Log Out",
                onClick = { authViewModel.logout() }
            )
        }
    }
}


@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}




