package com.example.thriftr.ui.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.thriftr.data.User
import com.example.thriftr.utils.ProfileState
import com.example.thriftr.utils.components.ErrorContent
import com.example.thriftr.utils.components.profilescreen.HologramProfileImage
import com.example.thriftr.utils.components.profilescreen.MenuSection
import com.example.thriftr.utils.components.profilescreen.ShimmerCard
import com.example.thriftr.utils.components.profilescreen.ShimmerMenu
import com.example.thriftr.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        ProfileState.Loading -> FullScreenLoader(
            modifier = Modifier, paddingValues = paddingValues

        )

        is ProfileState.Error -> ErrorContent(message = (state as ProfileState.Error).message)
        is ProfileState.Success -> {
            ProfileContent(
                viewModel = viewModel,
                user = (state as ProfileState.Success).user,
                modifier = Modifier.padding(paddingValues),
                navController = navController
            )
        }
    }

}

@Composable
fun FullScreenLoader(modifier: Modifier, paddingValues: PaddingValues) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Box(
            modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            ShimmerCard(
                modifier = modifier
                    .clip(CircleShape)
                    .size(200.dp)
            )
        }
        Spacer(modifier = modifier.height(16.dp))
        ShimmerCard(
            modifier = modifier
                .height(40.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = modifier.height(16.dp))
        ShimmerMenu()
        Spacer(modifier = modifier.weight(1f))
        ShimmerCard(
            modifier = modifier
                .height(60.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = modifier.height(16.dp))
    }


}



@Composable
private fun ProfileContent(
    viewModel: ProfileViewModel,
    navController: NavController,
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hologram Effect Profile Image
        val context = LocalContext.current
        HologramProfileImage(
            imageUrl = user.imagePath, onImageSelected = { uri ->
                viewModel.uploadProfileImage(uri, context)
            })

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${user.firstName} ${user.lastName}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(48.dp))

        MenuSection(navController = navController, viewModel = viewModel)

        Spacer(Modifier.weight(1f))

    }
}





