package com.example.thriftr.ui.screen


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.thriftr.utils.components.SignUpOptions
import com.example.thriftr.utils.UiState
import com.example.thriftr.utils.components.ResetPasswordBottomSheet
import com.example.thriftr.viewModel.AuthViewModel
import com.example.thriftr.viewModel.ProfileViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by remember { authViewModel.uiState }.collectAsStateWithLifecycle()
    val authState by remember { authViewModel.authState }.collectAsStateWithLifecycle()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showResetPasswordSheet by remember { mutableStateOf(false) }

    // Navigate when user is authenticated
    LaunchedEffect(authState) {
        if (authState != null) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            navController.navigate("HomeScreen") {
                popUpTo("StartScreen") { inclusive = true }
            }
        }
    }

    // Handle UI state changes (errors)
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Error -> {
                Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT)
                    .show()
                authViewModel.resetUiState()
            }
            else -> {} // No action for other states
        }
    }
    if (showResetPasswordSheet) {
        ResetPasswordBottomSheet(
            isActive = showResetPasswordSheet,
            onDismissRequest = { showResetPasswordSheet = false },
            loginViewModel = authViewModel
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
       Column (verticalArrangement = Arrangement.spacedBy(40.dp)) {
            Text(
                text = "Welcome\nBack!",
                fontSize = 48.sp,
                lineHeight = 56.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email") },
                    leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = "email") },
                    trailingIcon = {
                        if (email.isNotEmpty() && !authViewModel.isValidEmail(email)) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    isError = email.isNotEmpty() && !authViewModel.isValidEmail(email),
                    supportingText = {
                        if (email.isNotEmpty() && !authViewModel.isValidEmail(email)) {
                            Text("Invalid email format", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Password, contentDescription = "password") },
                    singleLine = true,
                    supportingText = {
                        if (password.isNotEmpty() && !authViewModel.isValidPassword(password)) {
                            Text(
                                "Password must be of at least 8 characters",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                // Add the "Forgot Password?" text here
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Forgot Password?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { showResetPasswordSheet = true }
                            .padding(vertical = 4.dp)
                    )
                }

                Button(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = { authViewModel.login(email, password)
                              profileViewModel.loadUserData()},
                    enabled = authViewModel.isValidEmail(email) && authViewModel.isValidPassword(
                        password
                    )
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Login", fontSize = 18.sp, textAlign = TextAlign.Center)
                    }
                }

            }
            SignUpOptions(
                text1 = "No account? ",
                text2 = "Sign Up",
                destination = "RegisterScreen",
                navController = navController
            )
        }
    }
}



