package com.example.thriftr.utils.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thriftr.R

@Composable
fun SignUpOptions(
    text1: String,
    text2: String,
    destination: String,
    navController: NavController
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "- or continue with -",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Google Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(60.dp))
                        .padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { /* Handle Google login */ }
                    )
                }

                // Facebook Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(60.dp))
                        .padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook_logo),
                        contentDescription = "Facebook Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { /* Handle Facebook login */ }
                    )
                }


                // Apple Button
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(60.dp))
                        .padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.apple_logo),
                        contentDescription = "Apple Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp)
                            .clickable { /* Handle Apple login */ }
                    )
                }
            }

            Row {
                Text(
                    text = text1,
                    fontSize = 16.sp,
                )
                Text(
                    text = text2,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate(destination)
                    }
                )
            }
        }
    }
}
