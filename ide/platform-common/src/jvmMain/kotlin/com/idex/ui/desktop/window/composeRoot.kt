package com.idex.ui.desktop.window

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.WindowEvents
import androidx.compose.runtime.*
import androidx.compose.ui.window.MenuBar
import com.idex.ui.util.isChanged
import java.awt.image.BufferedImage
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalComposeApi::class)
suspend fun composeRoot(parent: CompositionContext? = null, content: @Composable () -> Unit): Composition {
	val composition = Composition(EmptyApplier, parent ?: Recomposer(coroutineContext))
	composition.setContent(content)
	return composition
}

object EmptyApplier : AbstractApplier<Unit>(Unit) {
	override fun onClear() {}
	override fun insertBottomUp(index: Int, instance: Unit) {}
	override fun insertTopDown(index: Int, instance: Unit) {}
	override fun move(from: Int, to: Int, count: Int) {}
	override fun remove(index: Int, count: Int) {}
}



actual typealias DesktopBufferedImage = BufferedImage
actual typealias DesktopMenuBar = MenuBar
actual typealias DesktopWindowEvents = WindowEvents
actual typealias DesktopAppWindow = AppWindow

@Composable
actual fun ComposableWindowInternal(
	info: WindowInfo,
	undecorated: Boolean,
	resizable: Boolean,
	events: WindowEvents,
	onDismissRequest: (() -> Unit)?,
	initWindow: ((AppWindow) -> Unit)?,
	content: @Composable () -> Unit
) {
	val platformDecoration = LocalWindowDecoration.current
	
	lateinit var window: AppWindow
	
	fun wrapEvents() = WindowEvents(
		onOpen = {
			platformDecoration?.onInit?.invoke(window)
			events.onOpen?.invoke()
		},
		onClose = events.onClose,
		onMinimize = events.onMinimize,
		onMaximize = events.onMaximize,
		onRestore = events.onRestore,
		onFocusGet = events.onFocusGet,
		onFocusLost = events.onFocusLost,
		onResize = events.onResize,
		onRelocate = events.onRelocate
	)
	
	window = remember {
		AppWindow(
			info.title,
			info.size, info.location, info.centered,
			info.icon, info.menuBar,
			undecorated, resizable, wrapEvents(), onDismissRequest
		)
	}
	
	remember { initWindow?.invoke(window) } // need to remember the result of initWindow(): or it will be GCed
	
	val w = window.window
	
	
	// update
	
	if(window.title != info.title) window.setTitle(info.title)
	
	if(w.size.width != info.size.width || w.size.height != info.size.height)
		window.setSize(info.size.width, info.size.height)
	
	if(isChanged(info.centered)) {
		if(info.centered) window.setWindowCentered()
		else if(w.location.x != info.location.x || w.location.y != info.location.y)
			window.setLocation(info.location.x, info.location.y)
	}
	
	if(window.icon != info.icon) window.setIcon(info.icon)
	
	val menuBar = info.menuBar
	remember(menuBar) {
		if(menuBar == null) window.removeMenuBar() else window.setMenuBar(menuBar)
	}
	
	
	val compositionContext = rememberCompositionContext()
	
	DisposableEffect(null) {
		window.show(compositionContext) {
			content()
		}
		onDispose { window.close() }
	}
}
