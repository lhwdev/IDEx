@file:JvmName("Locals")

package com.idex.ui.desktop.window

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf


expect class PlatformWindow

@Immutable
data class WindowDecoration(val onInit: (PlatformWindow) -> Any?)

val LocalWindowDecoration = compositionLocalOf<WindowDecoration?> { null }

val hi = 3
fun ho() {
	
}

class Hi {
	init {
		LocalWindowDecoration
	}
}
