package com.example.thriftr.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CustomTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
            .height(52.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                // Navigation Icon (if available)
                Box(modifier = Modifier.weight(1f)) {
                    navigationIcon?.invoke()
                }

                // Title
                Text(
                    text = title,
                    modifier = Modifier.weight(3f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center
                )

                // Actions (if available)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions?.invoke(this)
                }
            }
        }

        // Bottom Divider
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}


//@Composable
//fun CustomTopBar(
//    modifier: Modifier = Modifier,
//    title: String,
//    navigationIcon: @Composable () -> Unit,
//    actions: @Composable RowScope.() -> Unit = {},
//
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
//            .height(52.dp)
//            .zIndex(-1f) // Push it behind other content
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
//                Box(modifier = Modifier.weight(1f)) {
//                    navigationIcon()
//                }
//                Text(
//                    text = title,
//                    modifier = Modifier.weight(3f),
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.W400,
//                    textAlign = TextAlign.Center
//                )
//                Row(
//                    modifier = Modifier.weight(1f),
//                    horizontalArrangement = Arrangement.End,
//                    verticalAlignment = Alignment.CenterVertically,
//                    content = actions
//                )
//            }
//        }
//        HorizontalDivider(
//            modifier = Modifier.align(Alignment.BottomCenter),
//            thickness = 0.5.dp,
//            color = MaterialTheme.colorScheme.outline
//        )
//    }
//}
