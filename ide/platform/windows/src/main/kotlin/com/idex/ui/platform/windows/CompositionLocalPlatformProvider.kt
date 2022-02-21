package com.idex.ui.platform.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.idex.ui.desktop.window.AppWindowFrame
import com.idex.ui.desktop.window.LocalWindowDecoration
import com.idex.ui.desktop.window.WindowDecoration


@Composable
fun ProvideWindows(content: @Composable () -> Unit) {
	AppWindowFrame()
	CompositionLocalProvider(
		LocalWindowDecoration provides WindowDecoration(onInit = ::initCustomDecoration),
		content = content
	)
}
