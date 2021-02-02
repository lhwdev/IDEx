package com.idex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun Surface(backgroundColor: Color? = null, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
	Box(
		modifier = modifier.let { if(backgroundColor == null) it else it.background(backgroundColor) }
	) {
		content()
	}
}


val Colors.primarySurface: Color get() = if(isLight) primary else surface
