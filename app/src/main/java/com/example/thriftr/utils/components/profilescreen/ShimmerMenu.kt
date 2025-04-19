package com.example.thriftr.utils.components.profilescreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerMenu() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), shape = RoundedCornerShape(12.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val shimmerColors = listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 0.6f)
            )

            val transition = rememberInfiniteTransition(label = "")
            val translateAnim = transition.animateFloat(
                initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Restart
                ), label = ""
            )

            val brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(translateAnim.value - 1000, 0f),
                end = Offset(translateAnim.value, 0f)
            )
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(24.dp)
                        .background(brush)
                ) {}
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ShimmerCard(modifier: Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim = transition.animateFloat(
        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing)
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value, 0f),
        end = Offset(translateAnim.value + 500f, 500f)
    )

    Card(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.background(brush = brush)
        )
    }
}