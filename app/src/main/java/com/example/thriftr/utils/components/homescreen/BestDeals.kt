package com.example.thriftr.utils.components.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thriftr.data.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@Composable
fun AnimatedBestDealsCarousel(
    products: List<Product>,
    onButtonClick: (Product) -> Unit,
    isVisible: Boolean,
    isFirstLoad: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = if (isFirstLoad) {
            expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        } else {
            EnterTransition.None
        },
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(300)
        ) + fadeOut(tween(300))
    ) {
        BestDealsCarousel(
            products = products,
            onButtonClick = onButtonClick
        )
    }
}

@Composable
fun BestDealsCarousel(products: List<Product>,
                      onButtonClick: (Product) -> Unit) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        pageCount = { products.size },
        initialPage = 0
    )
    val cornerShape = RoundedCornerShape(12.dp)
    val scope = rememberCoroutineScope()

    LaunchedEffect(products.size) {
        if (products.size > 1) {
            while (isActive) {  // Ensures coroutine stops when composable is removed
                delay(5000)
                scope.launch {
                    pagerState.animateScrollToPage(
                        page = (pagerState.currentPage + 1) % products.size,
                        animationSpec = tween(1000)
                    )
                }
            }
        }
    }

    androidx.compose.foundation.pager.HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 8.dp)
    ) { page ->
        val product = products[page]
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            // .clickable { onButtonClick(product) },
            shape = cornerShape
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0x4D000000), // Direct ARGB value for efficiency
                                    Color.Transparent
                                ),
                                startX = 0f,
                                endX = 700f
                            ),
                            shape = cornerShape
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${product.discount?.toInt()}% OFF!",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = product.name,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        OutlinedButton(
                            onClick = { onButtonClick(product) },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = "View Details", color = Color.White)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Arrow",
                                tint = Color.White,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
                // Dots Indicator with optimized recomposition
                val selectedPage by remember { derivedStateOf { pagerState.currentPage } }
                DotsIndicator(
                    totalDots = products.size,
                    selectedIndex = selectedPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                )
            }
        }
    }
}


@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (index == selectedIndex) Color.Black else Color.Gray)
            )
        }
    }
}



