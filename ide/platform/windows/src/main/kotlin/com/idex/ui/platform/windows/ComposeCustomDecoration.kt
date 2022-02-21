package com.idex.ui.platform.windows

import androidx.compose.desktop.AppWindow
import com.beranabyte.ui.customdecoration.CustomDecorationParameters
import com.beranabyte.ui.customdecoration.CustomDecorationWindowProc
import com.beranabyte.ui.customdecoration.TransparentDecorationWindowProc
import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.Component
import java.awt.Container
import java.awt.event.WindowEvent
import java.awt.event.WindowListener


fun Component.asHWND(): HWND {
	val hwnd = HWND()
	hwnd.pointer = Native.getComponentPointer(this)
	return hwnd
}

fun initCustomDecoration(window: AppWindow): Any? {
	val windowHWND = window.window.asHWND()
	val composeHWND =
		((window.window.contentPane.getComponent(0) as Container).getComponent(0) as Container).getComponent(0).asHWND()
	
	window.window.addWindowListener(object : WindowListener {
		private var windowDecoration: Any? = null
		
		override fun windowOpened(e: WindowEvent) {
			val params = CustomDecorationParameters()
			windowDecoration = CustomDecorationWindowProc(windowHWND, params) to
				TransparentDecorationWindowProc(composeHWND, params)
			// windowDecoration = CustomDecorationWindowProc(windowHWND, params)
		}
		
		override fun windowClosing(e: WindowEvent) {
		}
		
		override fun windowClosed(e: WindowEvent) {
		}
		
		override fun windowIconified(e: WindowEvent) {
		}
		
		override fun windowDeiconified(e: WindowEvent) {
		}
		
		override fun windowActivated(e: WindowEvent) {
		}
		
		override fun windowDeactivated(e: WindowEvent) {
		}
	})
	
	return null
}

