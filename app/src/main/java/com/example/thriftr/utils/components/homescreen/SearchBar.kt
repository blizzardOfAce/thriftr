package com.example.thriftr.utils.components.homescreen

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thriftr.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier, onSearchQueryChange: (String) -> Unit, viewModel: HomeViewModel
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    LaunchedEffect(query) {
        onSearchQueryChange(query) // Immediate feedback while typing
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isError = false
    val enabled = true
    val singleLine = true
    val colors = TextFieldDefaults.colors().copy(
        unfocusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.onSurface
    )

    BasicTextField(
        value = query, onValueChange = onSearchQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 0.5.dp,
                color = if (interactionSource.collectIsFocusedAsState().value) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ), singleLine = singleLine, textStyle = TextStyle(
            fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface
        ), interactionSource = interactionSource, decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = query,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = isError,
                placeholder = {
                    Text(
                        text = "Search products", fontWeight = FontWeight.Light, fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.MicNone,
                        contentDescription = "Voice Search",
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                },
                colors = colors,
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 0.dp
                ),
                shape = RoundedCornerShape(12.dp)
            )
        })
}
