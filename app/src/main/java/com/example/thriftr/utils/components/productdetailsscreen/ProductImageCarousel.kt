package com.example.thriftr.utils.components.productdetailsscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.thriftr.data.Product
import com.example.thriftr.utils.components.homescreen.DotsIndicator
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

private data class ImageState(
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductImageCarousel(
    product: Product,
    isInWishlist: Boolean,
    onWishlistToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0)
    var isSelected by remember { mutableStateOf(isInWishlist) }

    // Separate scale and offset for each image
    val imageStates = remember {
        product.images.associateWith { url -> mutableStateOf(ImageState()) }
    }

    LaunchedEffect(isInWishlist) {
        isSelected = isInWishlist
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (product.images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                count = product.images.size,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val imageUrl = product.images[page]
                val imageState = imageStates[imageUrl]?.value ?: ImageState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                // Handle zoom only on pinch gesture (when zoom is not 1f)
                                if (zoom != 1f) {
                                    val newScale = (imageState.scale * zoom).coerceIn(1f, 3f)
                                    val newOffset = if (newScale == 1f) {
                                        Offset.Zero
                                    } else {
                                        Offset(
                                            (imageState.offset.x + pan.x).coerceIn(
                                                -size.width * (newScale - 1) / 2,
                                                size.width * (newScale - 1) / 2
                                            ),
                                            (imageState.offset.y + pan.y).coerceIn(
                                                -size.height * (newScale - 1) / 2,
                                                size.height * (newScale - 1) / 2
                                            )
                                        )
                                    }
                                    imageStates[imageUrl]?.value = ImageState(newScale, newOffset)
                                }
                            }
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Product image ${page + 1}",
                        contentScale = ContentScale.Crop, // Changed to Crop as requested
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = imageState.scale,
                                scaleY = imageState.scale,
                                translationX = imageState.offset.x,
                                translationY = imageState.offset.y
                            )
                    )
                }
            }

            // Add swipe gesture handler for the pager separately from zoom
            // This ensures swiping works reliably for navigation
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .collect { page ->
                        // Reset zoom when page changes
                        product.images.forEach { url ->
                            imageStates[url]?.value = ImageState()
                        }
                    }
            }

            // Wishlist button
            IconButton(
                onClick = {
                    isSelected = !isSelected
                    onWishlistToggle(isSelected)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isInWishlist) "Remove from Wishlist" else "Add to Wishlist",
                    tint = Color.Red
                )
            }

            // Page indicator
            DotsIndicator(
                totalDots = product.images.size,
                selectedIndex = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )
        }
    }
}