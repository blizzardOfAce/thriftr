package com.example.thriftr.utils.components.editprofilescreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.thriftr.R

@Composable
fun ProfileImageWithUploadFxn(
    imageUrl: String?, onImageSelected: (Uri) -> Unit
) {
    var showImagePicker by remember { mutableStateOf(false) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = { uri ->
            uri?.let {
                onImageSelected(it)
            }
        })

    Box {
        AsyncImage(
            model = imageUrl ?: R.drawable.ic_profile,
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(144.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )

        IconButton(
            onClick = { showImagePicker = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.White, CircleShape)
        ) {
            Icon(Icons.Default.Edit, "Edit Profile Image")
        }
    }

    if (showImagePicker) {
        imagePicker.launch("image/*")
    }
}