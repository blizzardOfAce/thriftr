package com.example.thriftr.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.R
import com.example.thriftr.ui.theme.ThriftrTheme
import com.example.thriftr.ui.theme.primaryLight

/* Todo: Add a first launch flow after fresh install using sharedPreferences or something */

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    onClickSignUp: () -> Unit,
    onClickLogin: () -> Unit,
    onClickSkip: () -> Unit, // Add a new callback for the skip button
    onClickSeller: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF8E6CEF), // Top color (8E6CEF)
            Color(0xFFDAD3D3)  // Bottom color (DAD3D3)
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Image(
            modifier = Modifier
                .padding(top = 64.dp)
                .fillMaxWidth()
                .height(600.dp),
            painter = painterResource(id = R.drawable.ss_content),
            contentDescription = "Start Image",
        )
        // Skip Button
        TextButton(
            onClick = onClickSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Text(text = "SKIP", color = Color.White, fontSize = 16.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, bottom = 48.dp, top = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp),
                    onClick = onClickSignUp
                ) {
                    Text(text = "REGISTER", fontSize = 20.sp, color = Color(0xFF8E6CEF))
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp),
                    onClick = onClickLogin
                ) {
                    Text(text = "LOGIN", fontSize = 20.sp, color = Color(0xFF8E6CEF))
                }
                Spacer(modifier = Modifier.height(4.dp))

                val annotatedString = buildAnnotatedString {
                    withStyle(SpanStyle(color = primaryLight)) {
                        append("-continue as seller instead-")
                    }
                }

                BasicText(
                    text = annotatedString, modifier = Modifier.clickable(
                        onClick = onClickSeller
                    )
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    ThriftrTheme {
        StartScreen(
            modifier = Modifier,
            onClickLogin = {},
            onClickSkip = {},
            onClickSignUp = {},
            onClickSeller = {}
        )
    }
}