package com.example.thriftr.utils.components

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


//Random Fxn found while fixing Status bar issue
@Composable
fun SystembarColorChanger(
    statusBarColor: Color?,
    isLightIcons: Boolean
){
    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    SideEffect {
        changeSystemBarsColor(window = window,
            view = view,
            statusBarColor = statusBarColor,
            isLightIcons = isLightIcons)
    }
}
fun changeSystemBarsColor(
    window: Window,
    view: View,
    statusBarColor: Color?,
    isLightIcons: Boolean
){
    statusBarColor?.let{
        window.statusBarColor = it.toArgb()
    }
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isLightIcons
}