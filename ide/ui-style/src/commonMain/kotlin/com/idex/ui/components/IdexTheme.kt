package com.idex.ui.components

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable


object IdexTheme {
	val colors: Colors
		@Composable
		@ReadOnlyComposable
		get() = MaterialTheme.colors
	
	val typography: Typography
		@Composable
		@ReadOnlyComposable
		get() = MaterialTheme.typography
	
	val shapes: Shapes
		@Composable
		@ReadOnlyComposable
		get() = MaterialTheme.shapes
}


@Composable
fun IdexTheme(
	colors: Colors = IdexTheme.colors,
	typography: Typography = IdexTheme.typography,
	shapes: Shapes = IdexTheme.shapes,
	content: @Composable () -> Unit
) {
	PlatformTheme(colors, typography, shapes, content)
}

@Composable
expect fun PlatformTheme(colors: Colors, typography: Typography, shapes: Shapes, content: @Composable () -> Unit)
