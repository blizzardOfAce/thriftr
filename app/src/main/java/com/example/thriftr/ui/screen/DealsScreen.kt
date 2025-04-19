package com.example.thriftr.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.thriftr.utils.HomeScreenData
import com.example.thriftr.utils.UiState
import com.example.thriftr.utils.components.CustomTopBar
import com.example.thriftr.utils.components.dealsscreen.DealHighlights
import com.example.thriftr.utils.components.dealsscreen.DealTermsCard
import com.example.thriftr.utils.components.dealsscreen.DealTimerCard
import com.example.thriftr.viewModel.HomeViewModel

@Composable
fun DealsScreen(
    productId: String,
    homeViewModel: HomeViewModel,
    onBackPress: () -> Unit
) {
    val scrollState = rememberScrollState()

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val product =
        (uiState as UiState.Success<HomeScreenData>).data.bestDeals.firstOrNull { it.id == productId }


    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            CustomTopBar(
                title = "Best Deals",
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Go back")
                    }
                })
        },
        content = { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Deal Image
                    AsyncImage(
                        model = product?.images?.firstOrNull(),
                        contentDescription = product?.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.4f)
                                    )
                                )
                            )
                    )

                    // Discount Badge
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${(product?.discount ?: 0f).toInt()}% OFF",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Deal Information Section
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Deal Title
                    product?.let {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time Remaining
                    DealTimerCard()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Deal Highlights
                    Text(
                        text = "Deal Highlights",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DealHighlights()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Terms and Conditions
                    DealTermsCard()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Claim Deal Button
                    Button(
                        onClick = { /* Handle claim deal */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Claim Deal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}






