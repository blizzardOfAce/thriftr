package com.example.thriftr.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.thriftr.utils.UiState
import com.example.thriftr.viewModel.AdminViewModel
import kotlinx.coroutines.launch
import com.example.thriftr.utils.components.CustomTopBar
import com.example.thriftr.utils.components.adminscreen.CategoryDropdown
import com.example.thriftr.utils.components.adminscreen.ColorSelectionSection
import com.example.thriftr.utils.components.adminscreen.GlassCard
import com.example.thriftr.utils.components.adminscreen.ProductImagesCard
import com.example.thriftr.utils.components.adminscreen.SizeInputField
import com.example.thriftr.utils.components.adminscreen.SlimOutlinedTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
    onProductSaved: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { viewModel.addImage(it) }
    }

    LaunchedEffect(viewModel.uiState) {
        when (val state = viewModel.uiState) {
            is UiState.Success -> {
                onProductSaved()
                viewModel.resetState()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Add Product",
                navigationIcon =
                    {
                        IconButton(onClick = { onClickBack() }) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = "Go back"
                            )
                        }
                } ,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showConfirmationDialog = true },
                icon = {
                    if (viewModel.uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(Icons.Default.Save, "Save")
                    }
                },
                text = { Text("Save Product") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                expanded = viewModel.uiState !is UiState.Loading
            )
        }
    ) { padding ->
        if (viewModel.uiState is UiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Basic Information Card
            ProductBasicInfoCard(viewModel)

            // Pricing and Inventory Card
            ProductPricingCard(viewModel)

            // Description Card
            ProductDescriptionCard(viewModel)

            // Images Card
            ProductImagesCard(
                selectedImages = viewModel.selectedImages,
                onAddImage = { imagePickerLauncher.launch("image/*") },
                onRemoveImage = viewModel::removeImage
            )
        }
    }

    if (showConfirmationDialog) {
        SaveProductDialog(
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                scope.launch {
                    showConfirmationDialog = false
                    viewModel.saveProduct()
                }
            }
        )
    }
}

@Composable
private fun ProductBasicInfoCard(viewModel: AdminViewModel) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            SlimOutlinedTextField(
                value = viewModel.productName,
                onValueChange = viewModel::updateProductName,
                label = "Product Name",
                //modifier = Modifier.fillMaxWidth().heightIn(48.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default
            )

            CategoryDropdown(
                selectedCategory = viewModel.category,
                onCategorySelected = viewModel::updateCategory
            )
        }
    }
}

@Composable
private fun ProductPricingCard(viewModel: AdminViewModel) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Pricing & Inventory",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold

            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SlimOutlinedTextField(
                    value = viewModel.price,
                    onValueChange = viewModel::updatePrice,
                    label = "Price",
                    modifier = Modifier.weight(1f),
                    //.padding(vertical = 2.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )

                SlimOutlinedTextField(
                    value = viewModel.discount,
                    onValueChange = viewModel::updateDiscount,
                    label = "Discount",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SlimOutlinedTextField(
                    value = viewModel.stock.toString(),
                    onValueChange = { viewModel.updateStock(it.toIntOrNull() ?: 1) },
                    label = "Stock",
                    modifier = Modifier.weight(1f).heightIn(48.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

               // Row(
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Switch(
                        checked = viewModel.freeShipping,
                        onCheckedChange = viewModel::updateFreeShipping
                    )
                    Text("Free Shipping", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun ProductDescriptionCard(viewModel: AdminViewModel) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Description & Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold

            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = viewModel.details,
                onValueChange = viewModel::updateDetails,
                label = { Text("Details") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            SizeInputField(
                sizes = viewModel.sizes.value,
                onSizesChanged = { viewModel.updateSizes(it) },
            )

            ColorSelectionSection(selectedColors = viewModel.selectedColors,
                onColorRemoved = viewModel::removeColor,
                onColorAdded = viewModel::addColor)
        }
    }
}

@Composable
private fun SaveProductDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Product") },
        text = { Text("Are you sure you want to save this product?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}









