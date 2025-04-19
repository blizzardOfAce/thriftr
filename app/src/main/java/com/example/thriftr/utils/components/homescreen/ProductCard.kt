package com.example.thriftr.utils.components.homescreen

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thriftr.data.Product
import com.example.thriftr.viewModel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun AnimatedProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (Product) -> Unit,
    homeViewModel: HomeViewModel,
    index: Int
) {
    val productId = product.id
    val wasAnimated = homeViewModel.isProductAnimated(productId)
    var animated by rememberSaveable { mutableStateOf(wasAnimated) } // ✅ Remember across recompositions

    val screenWidthDp = with(LocalDensity.current) { 400.dp.toPx() }
    val initialOffsetX = screenWidthDp * 0.3f

    val offsetX by animateFloatAsState(
        targetValue = if (animated) 0f else initialOffsetX,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseOut
        )
    )

    val alpha by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(productId) {
        if (!wasAnimated) {
            val delayTime = index * 30L
            delay(delayTime)
            animated = true
            homeViewModel.markProductAnimated(productId) // ✅ Mark as animated
        }
    }

    ProductCard(
        modifier = modifier
            .graphicsLayer(
                translationX = offsetX,
                alpha = alpha
            ),
        product = product,
        onClick = onClick
    )
}

@Composable
fun ProductCard(
    modifier: Modifier,
    product: Product,
    onClick: (Product) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick(product) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = product.name,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description ?: "",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!product.description.isNullOrEmpty()) Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${product.price}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



