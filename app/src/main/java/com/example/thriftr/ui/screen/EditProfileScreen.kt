package com.example.thriftr.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thriftr.data.Address
import com.example.thriftr.utils.ProfileState
import com.example.thriftr.utils.components.editprofilescreen.AddressEditorDialog
import com.example.thriftr.utils.components.editprofilescreen.AddressSection
import com.example.thriftr.utils.components.editprofilescreen.ProfileImageWithUploadFxn
import com.example.thriftr.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    var showImageConfirmDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }
    val scope = rememberCoroutineScope()

    val userState by viewModel.state.collectAsState()
    val defaultAddress =
        remember { derivedStateOf { viewModel.addresses.firstOrNull { it.isDefault } } }
    val otherAddresses =
        remember { derivedStateOf { viewModel.addresses.filterNot { it.isDefault } } }

    LaunchedEffect(Unit) {  // Run only once when the screen is first opened
        val editAddressId = navController.currentBackStackEntry?.arguments?.getString("editAddress")
        if (!editAddressId.isNullOrEmpty() && editAddressId != "null") {
            val addressToEdit = viewModel.addresses.find { it.id == editAddressId }
            if (addressToEdit != null) {
                editingAddress = addressToEdit
            }
        }
    }

    // Then show your address edit form conditionally based on whether editingAddress is set
    if (editingAddress != null) {
        // Show address edit form with pre-populated fields from editingAddress
        AddressEditorDialog(address = editingAddress!!, onSave = { updated ->
            if (editingAddress!!.id.isEmpty()) {
                viewModel.addAddress(updated)
            } else {
                viewModel.updateAddress(updated)
            }
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (editingAddress!!.id.isEmpty()) "Address added successfully"
                    else "Address updated successfully"
                )
            }
            editingAddress = null
        }, onDismiss = { editingAddress = null })

    }


    if (showImageConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showImageConfirmDialog = false },
            title = { Text("Confirm Profile Picture") },
            text = {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(150.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImageConfirmDialog = false
                        selectedImageUri?.let { viewModel.uploadProfileImage(it, context) }
                    }, enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) CircularProgressIndicator()
                    else Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showImageConfirmDialog = false }, enabled = !viewModel.isLoading
                ) {
                    Text("Cancel")
                }
            })
    }

        when (val state = userState) {
            is ProfileState.Loading -> FullScreenLoader(
                modifier = Modifier,
                paddingValues = paddingValues
            )

            is ProfileState.Error -> ErrorScreen(state.message)
            is ProfileState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile Image Section
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        ProfileImageWithUploadFxn(
                            imageUrl = state.user.imagePath, onImageSelected = { uri ->
                                selectedImageUri = uri
                                showImageConfirmDialog = true
                            })
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Personal Info Section
                    Text(
                        "Personal Information", fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.firstName,
                        onValueChange = { viewModel.firstName = it },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        label = { Text("First Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(52.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.lastName,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        onValueChange = { viewModel.lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(52.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AddressSection(
                        defaultAddress = defaultAddress.value,
                        otherAddresses = otherAddresses.value,
                        onEditAddress = { editingAddress = it },
                        onDeleteAddress = { address ->
                            viewModel.deleteAddress(address.id)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Address deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.addAddress(address)
                                }
                            }
                        },
                        onAddAddress = {
                            editingAddress = Address(
                                street = "", state = "", city = "", country = "", postalCode = ""
                            )
                        })
                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            viewModel.saveProfile()
                            scope.launch {
                                snackbarHostState.showSnackbar("Profile updated successfully")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) CircularProgressIndicator()
                        else Text("Save Changes")
                    }
                }
            }
        }

    editingAddress?.let { address ->
        AddressEditorDialog(address = address, onSave = { updated ->
            // Always use addAddress for new addresses
            if (address.id.isEmpty()) {
                viewModel.addAddress(updated)
            } else {
                viewModel.updateAddress(updated)
            }
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (address.id.isEmpty()) "Address added successfully"
                    else "Address updated successfully"
                )
            }
            editingAddress = null
        }, onDismiss = { editingAddress = null })
    }
}





