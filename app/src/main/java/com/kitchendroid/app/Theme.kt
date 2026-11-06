package com.kitchendroid.app

import androidx.compose.material3.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun KitchenDroidTheme(content: @Composable () -> Unit) {
    val colorScheme = ColorScheme(
        primary = Color(0xFF6200EE),
        primaryContainer = Color(0xFFEADDFF),
        secondary = Color(0xFF03DAC5),
        background = Color(0xFFF8F9FA),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1C1C1E),
        onSurfaceVariant = Color(0xFF49454F),
        outline = Color(0xFFC4C4C4),
        error = Color(0xFFB3261E)
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
