// Copyright 2020 Kalkidan Betre Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:Suppress("SpellCheckingInspection")

package com.beranabyte.ui.customdecoration

import com.sun.jna.Native
import com.sun.jna.platform.win32.BaseTSD
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import com.sun.jna.win32.W32APIOptions


private fun is64Bit(): Boolean {
	val model = System.getProperty(
		"sun.arch.data.model",
		System.getProperty("com.ibm.vm.bitmode")
	)
	return if(model != null) {
		"64" == model
	} else false
}

private val INSTANCEEx = Native.load("user32", User32Ex::class.java, W32APIOptions.DEFAULT_OPTIONS)
private const val WM_NCCALCSIZE = 0x0083
private const val WM_NCHITTEST = 0x0084


class CustomDecorationWindowProc(hwnd: HWND, val params: CustomDecorationParameters) : WindowProc {
	val defWndProc: BaseTSD.LONG_PTR =
		if(is64Bit()) INSTANCEEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, this)
		else INSTANCEEx.SetWindowLong(hwnd, User32Ex.GWLP_WNDPROC, this)
	
	
	init {
		INSTANCEEx.SetWindowPos(
			hwnd, hwnd, 0, 0, 0, 0,
			SWP_NOMOVE or SWP_NOSIZE or SWP_NOZORDER or SWP_FRAMECHANGED
		)
	}
	
	override fun callback(hwnd: HWND, uMsg: Int, wparam: WPARAM, lparam: LPARAM): LRESULT {
		return when(uMsg) {
			WM_NCCALCSIZE -> LRESULT(0)
			WM_NCHITTEST -> {
				val lresult = hitTest(hwnd, uMsg, wparam, lparam)
				if(lresult.toInt() == LRESULT(0).toInt()) {
					INSTANCEEx.CallWindowProc(defWndProc, hwnd, uMsg, wparam, lparam)
				} else lresult
			}
			WM_DESTROY -> {
				if(is64Bit()) INSTANCEEx.SetWindowLongPtr(
					hwnd,
					User32Ex.GWLP_WNDPROC,
					defWndProc
				) else INSTANCEEx.SetWindowLong(hwnd, User32Ex.GWLP_WNDPROC, defWndProc)
				LRESULT(0)
			}
			else -> INSTANCEEx.CallWindowProc(defWndProc, hwnd, uMsg, wparam, lparam)
		}
	}
	
	@Suppress("UNUSED_PARAMETER")
	private fun hitTest(hWnd: HWND, message: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
		val borderOffset = params.maximizedWindowFrameThickness
		val borderThickness = params.frameResizeBorderThickness
		val ptMouse = POINT()
		val rcWindow = RECT()
		
		User32.INSTANCE.GetCursorPos(ptMouse)
		User32.INSTANCE.GetWindowRect(hWnd, rcWindow)
		
		var uRow = 1
		var uCol = 1
		var fOnResizeBorder = false
		var fOnFrameDrag = false
		val topOffset =
			if(params.titleBarHeight == 0) borderThickness else params.titleBarHeight
		
		if(ptMouse.y >= rcWindow.top && ptMouse.y < rcWindow.top + topOffset + borderOffset) {
			fOnResizeBorder = ptMouse.y < rcWindow.top + borderThickness // Top Resizing
			if(!fOnResizeBorder) {
				fOnFrameDrag =
					(ptMouse.y <= rcWindow.top + params.titleBarHeight + borderOffset
						&& ptMouse.x < rcWindow.right - (params.controlBoxWidth
						+ borderOffset + params.extraRightReservedWidth)
						&& ptMouse.x > (rcWindow.left + params.iconWidth
						+ borderOffset + params.extraLeftReservedWidth))
			}
			uRow = 0 // Top Resizing or Caption Moving
		} else if(ptMouse.y < rcWindow.bottom && ptMouse.y >= rcWindow.bottom - borderThickness) uRow =
			2 // Bottom Resizing
		if(ptMouse.x >= rcWindow.left && ptMouse.x < rcWindow.left + borderThickness) uCol = 0 // Left Resizing
		else if(ptMouse.x < rcWindow.right && ptMouse.x >= rcWindow.right - borderThickness) uCol = 2 // Right Resizing
		
		val HTTOPLEFT = 13
		val HTTOP = 12
		val HTCAPTION = 2
		val HTTOPRIGHT = 14
		val HTLEFT = 10
		val HTNOWHERE = 0
		val HTRIGHT = 11
		val HTBOTTOMLEFT = 16
		val HTBOTTOM = 15
		val HTBOTTOMRIGHT = 17
		val HTSYSMENU = 3
		val hitTests = arrayOf(
			intArrayOf(
				HTTOPLEFT,
				if(fOnResizeBorder) HTTOP else if(fOnFrameDrag) HTCAPTION else HTNOWHERE,
				HTTOPRIGHT
			), intArrayOf(HTLEFT, HTNOWHERE, HTRIGHT), intArrayOf(HTBOTTOMLEFT, HTBOTTOM, HTBOTTOMRIGHT)
		)
		return LRESULT(hitTests[uRow][uCol].toLong())
	}
	
}
