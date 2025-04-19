package com.example.thriftr.utils.components.adminscreen

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlimOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions,
    label: String,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isError = false
    val enabled = true
    val colors = TextFieldDefaults.colors().copy(
        unfocusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(52.dp)
            .padding(vertical = 2.dp)
            .border(
                width = 1.5.dp,
                color = if (interactionSource.collectIsFocusedAsState().value) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ), singleLine = singleLine, textStyle = TextStyle(
            fontSize = 15.sp, lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                //  label = {Text(text = label)},
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = isError,
                placeholder = {
                    Text(
                        text = label, fontWeight = FontWeight.Light, fontSize = 15.sp
                    )
                },
                colors = colors,
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 0.dp
                ), // Properly adjusted padding
                shape = RoundedCornerShape(8.dp)
            )
        })
}