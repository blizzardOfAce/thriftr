package com.example.thriftr.utils.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

//Alternate ForgotPasswordScreen instead of Modal Sheet
@Composable
fun ForgotPassword(){
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }


    Column(modifier=Modifier.fillMaxSize()) {
        Column(modifier =Modifier.fillMaxWidth()) {
            Text(text = "Forgot\nPassword?", fontWeight = FontWeight.SemiBold,
                fontSize = 48.sp, modifier = Modifier.padding(top = 36.dp, start = 12.dp))
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your email address") },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "User") },
                colors = TextFieldDefaults.colors(unfocusedIndicatorColor = Color.Gray),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            Text(text = "** We will send you a message to set or reset your new password",
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Start)

            Spacer(modifier = Modifier.padding(vertical = 12.dp))

            Button(shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(56.dp),
                onClick = {
                    coroutineScope.launch {
                        try {
                          //  Appwrite.onLogin(email, password)

                           // user = email
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            ) {
                Text("Submit", fontSize = 20.sp)
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview(){
    ForgotPassword()
}