package com.example.thriftr.utils.components.profilescreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.thriftr.R

@Composable
fun HologramProfileImage(
    imageUrl: String?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    innerImageSize: Dp = size - 20.dp,
    hologramColors: List<Color> = listOf(
        Color.Cyan.copy(alpha = 0.5f),
        Color.Magenta.copy(alpha = 0.5f),
        Color.Yellow.copy(alpha = 0.5f),
        Color.Cyan.copy(alpha = 0.5f)
    )
) {
    var showPicker by remember { mutableStateOf(false) }
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let(onImageSelected) })

    val infiniteTransition = rememberInfiniteTransition(label = "hologram_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = "hologram_angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clickable { pickImage.launch("image/*") }) {
        // Hologram effect
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = angle
                }) {
            val brush = Brush.sweepGradient(
                colors = hologramColors, center = center
            )
            drawCircle(
                brush = brush, radius = size.toPx() / 2, center = center, alpha = 0.5f
            )
        }

        // Profile image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: R.drawable.ic_profile).crossfade(true).build(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(innerImageSize)
                .clip(CircleShape)
                .border(
                    width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape
                )
        )
    }

    if (showPicker) {
        pickImage.launch("image/*")
    }
}