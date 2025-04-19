package com.example.thriftr.utils.components.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thriftr.viewModel.HomeViewModel
import kotlinx.coroutines.delay


@Composable
fun CategoryRowWithPlaceholder(
    isLoading: Boolean,
    images: List<Int>,
    categories: List<String>,
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Crossfade(targetState = isLoading, label = "CategoryRowTransition") { loading ->
            if (loading) {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Optional: padding on sides
                ) {
                    items(5) {
                        ShimmerPlaceholder(
                            shape = CircleShape,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            } else {
                CategoryRowWithAnimation(
                    images = images,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    homeViewModel = homeViewModel
                )

            }

        }
    }
}


@Composable
fun CategoryRowWithAnimation(
    images: List<Int>,
    categories: List<String>,
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // Read from ViewModel - has the animation been completed already?
    val animationCompleted by homeViewModel.categoryAnimationCompleted.collectAsState()

    // Create visibility states for each category
    val visibilityStates = remember {
        List(categories.size) { mutableStateOf(animationCompleted) }
    }


    // Only animate if not already done
    LaunchedEffect(Unit) {
        if (!animationCompleted) {
            // Animate sequentially
            categories.forEachIndexed { index, _ ->
                delay(index * 50L)
                visibilityStates[index].value = true
            }
            // Mark as completed in ViewModel
            homeViewModel.markCategoryAnimationComplete()
        } else {
            // Make all visible immediately
            visibilityStates.forEach { it.value = true }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { index, category ->
            AnimatedVisibility(
                visible = visibilityStates[index].value,
                enter = slideInHorizontally(
                    initialOffsetX = { 200 }, // Start 200dp to the right
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(300)
                )
            ) {
                CategoryButton(
                    imageRes = images[index],
                    category = category,
                    isSelected = index == selectedCategory,
                    onClick = { onCategorySelected(index) }
                )
            }
        }
    }
}

@Composable
fun CategoryButton(
    category: String,
    imageRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick, interactionSource = interactionSource, indication = null)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = category,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFFEEEEEE) else Color.Transparent,
                    CircleShape
                )
                .border(
                    if (isSelected) 1.5.dp else 0.5.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    CircleShape
                )
        )
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

