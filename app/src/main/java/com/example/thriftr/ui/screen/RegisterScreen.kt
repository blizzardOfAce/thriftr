package com.example.thriftr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.thriftr.utils.components.SignUpOptions
import com.example.thriftr.utils.UiState
import com.example.thriftr.viewModel.AuthViewModel


@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val uiState by remember { authViewModel.uiState }.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT)
                    .show()
                authViewModel.resetUiState()
                navController.navigate("LoginScreen"){
                    popUpTo("RegisterScreen") { inclusive = true }
                }
            }

            is UiState.Error -> {
                Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT)
                    .show()
                authViewModel.resetUiState()
            }

            else -> {} // No action for other states
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(40.dp)

        ) {
            Text(
                text = "Create an\nAccount",
                fontSize = 48.sp,
                lineHeight = 52.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Email"
                        )
                    },
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
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    supportingText = {
                        if (password.isNotEmpty() && !authViewModel.isValidPassword(password)) {
                            Text(
                                "Password must be of at least 8 characters",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()

                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Password,
                            contentDescription = "confirm password"
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    supportingText = {
                        if (confirmPassword.isNotEmpty() && !authViewModel.isValidPassword(
                                confirmPassword
                            )
                        ) {
                            Text(
                                "Password must be of at least 8 characters",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Button(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = {
                        if (password == confirmPassword)
                            authViewModel.register(email, password)
                        else
                            Toast.makeText(context, "Password didn't match", Toast.LENGTH_SHORT)
                                .show()
                    },
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
                        Text("Register", fontSize = 18.sp, textAlign = TextAlign.Center)
                    }
                }

            }
            SignUpOptions(
                text1 = "I already have an account ", text2 = "Login",
                destination = "LoginScreen",
                navController = navController
            )
        }
    }
}


