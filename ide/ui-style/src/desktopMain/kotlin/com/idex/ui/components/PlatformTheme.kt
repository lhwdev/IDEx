package com.idex.ui.components

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.Colors
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable


@Composable
actual fun PlatformTheme(colors: Colors, typography: Typography, shapes: Shapes, content: @Composable () -> Unit) {
	DesktopMaterialTheme(colors, typography, shapes, content)
}
