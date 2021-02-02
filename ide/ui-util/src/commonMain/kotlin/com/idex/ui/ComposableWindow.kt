package com.idex.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize


object WindowSize {
	val Small = IntSize(300, 480)
	val Medium = IntSize(800, 500)
	val Large = IntSize(1080, 720)
}


@Immutable
data class WindowInfo(
	val title: String,
	val size: IntSize = IntSize(800, 600),
	val location: IntOffset = IntOffset.Zero,
	val centered: Boolean = true,
	val icon: DesktopBufferedImage? = null,
	val menuBar: DesktopMenuBar? = null
)


expect class DesktopBufferedImage
expect class DesktopMenuBar
expect class DesktopWindowEvents(
	onOpen: (() -> Unit)? = null,
	onClose: (() -> Unit)? = null,
	onMinimize: (() -> Unit)? = null,
	onMaximize: (() -> Unit)? = null,
	onRestore: (() -> Unit)? = null,
	onFocusGet: (() -> Unit)? = null,
	onFocusLost: (() -> Unit)? = null,
	onResize: ((IntSize) -> Unit)? = null,
	onRelocate: ((IntOffset) -> Unit)? = null
)

expect class DesktopAppWindow


/**
 * Shows a window
 */
@Composable
fun ComposableWindow(
	info: WindowInfo,
	undecorated: Boolean = false,
	resizable: Boolean = true,
	events: DesktopWindowEvents = DesktopWindowEvents(),
	onDismissRequest: (() -> Unit)? = null,
	initWindow: ((DesktopAppWindow) -> Unit)? = null,
	content: @Composable () -> Unit
) {
	ComposableWindowInternal(info, undecorated, resizable, events, onDismissRequest, initWindow, content)
}


// workaround for https://youtrack.jetbrains.com/issue/KT-44499
@Composable
internal expect fun ComposableWindowInternal(
	info: WindowInfo,
	undecorated: Boolean,
	resizable: Boolean,
	events: DesktopWindowEvents,
	onDismissRequest: (() -> Unit)?,
	initWindow: ((DesktopAppWindow) -> Unit)?,
	content: @Composable () -> Unit
)
