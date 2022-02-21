package com.idex.ui.platform.windows

import androidx.compose.runtime.Composable
import com.idex.ui.desktop.window.composeRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


fun mainComposable(content: @Composable () -> Unit) {
	runBlocking {
		withContext(Dispatchers.Main) {
			composeRoot(content = content)
			// val frame = JFrame()
			// frame.isVisible = true
			// CustomDecorationWindowProc(frame.asHWND(), CustomDecorationParameters())
		}
	}
}
