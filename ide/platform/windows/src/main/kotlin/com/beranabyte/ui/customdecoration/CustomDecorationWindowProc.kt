// Copyright 2020 Kalkidan Betre Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:Suppress("SpellCheckingInspection")

package com.beranabyte.ui.customdecoration

import com.beranabyte.ui.customdecoration.User32Ex.Companion.WM_NCCALCSIZE
import com.beranabyte.ui.customdecoration.User32Ex.Companion.WM_NCHITTEST
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Structure
import com.sun.jna.platform.win32.BaseTSD
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.platform.win32.WinUser.*
import com.sun.jna.win32.W32APIOptions


internal fun is64Bit(): Boolean {
	val model = System.getProperty(
		"sun.arch.data.model",
		System.getProperty("com.ibm.vm.bitmode")
	)
	return if(model != null) {
		"64" == model
	} else false
}

internal val INSTANCEEx = Native.load("user32", User32Ex::class.java, W32APIOptions.DEFAULT_OPTIONS)
internal val sDWM = NativeLibrary.getInstance("dwmapi")
internal val sDwmExtendFrameIntoClientArea = sDWM.getFunction("DwmExtendFrameIntoClientArea")

internal data class MARGINS(
	@JvmField
	val cxLeftWidth: Int,
	@JvmField
	val cxRightWidth: Int,
	@JvmField
	val cyTopHeight: Int,
	@JvmField
	val cyBottomHeight: Int
) : Structure() {
	override fun getFieldOrder() = listOf("cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight")
}

internal fun DwmExtendFrameIntoClientArea(hwnd: HWND, pMarInset: MARGINS): HRESULT =
	sDwmExtendFrameIntoClientArea(HRESULT::class.java, arrayOf(hwnd, pMarInset)) as HRESULT


fun CustomDecorationParameters.hitTest(hWnd: HWND): LRESULT {
	val borderOffset = maximizedWindowFrameThickness
	val borderThickness = frameResizeBorderThickness
	val ptMouse = POINT()
	val rcWindow = RECT()
	
	User32.INSTANCE.GetCursorPos(ptMouse)
	User32.INSTANCE.GetWindowRect(hWnd, rcWindow)
	
	var uRow = 1
	var uCol = 1
	var fOnResizeBorder = false
	var fOnFrameDrag = false
	val topOffset =
		if(titleBarHeight == 0) borderThickness else titleBarHeight
	
	if(ptMouse.y >= rcWindow.top && ptMouse.y < rcWindow.top + topOffset + borderOffset) {
		fOnResizeBorder = ptMouse.y < rcWindow.top + borderThickness // Top Resizing
		if(!fOnResizeBorder) {
			fOnFrameDrag =
				(ptMouse.y <= rcWindow.top + titleBarHeight + borderOffset
					&& ptMouse.x < rcWindow.right - (controlBoxWidth
					+ borderOffset + extraRightReservedWidth)
					&& ptMouse.x > (rcWindow.left + iconWidth
					+ borderOffset + extraLeftReservedWidth))
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
	// val HTSYSMENU = 3
	val hitTests = arrayOf(
		intArrayOf(
			HTTOPLEFT,
			if(fOnResizeBorder) HTTOP else if(fOnFrameDrag) HTCAPTION else HTNOWHERE,
			HTTOPRIGHT
		), intArrayOf(HTLEFT, HTNOWHERE, HTRIGHT), intArrayOf(HTBOTTOMLEFT, HTBOTTOM, HTBOTTOMRIGHT)
	)
	return LRESULT(hitTests[uRow][uCol].toLong())
}


class CustomDecorationWindowProc(hwnd: HWND, val params: CustomDecorationParameters) : WindowProc {
	val defWndProc: BaseTSD.LONG_PTR =
		if(is64Bit()) INSTANCEEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, this)
		else INSTANCEEx.SetWindowLong(hwnd, User32Ex.GWLP_WNDPROC, this)
	
	
	init {
		DwmExtendFrameIntoClientArea(hwnd, MARGINS(0, 0, 0, -50))
		INSTANCEEx.SetWindowPos(
			hwnd, hwnd, 0, 0, 0, 0,
			SWP_NOMOVE or SWP_NOSIZE or SWP_NOZORDER or SWP_NOOWNERZORDER or SWP_FRAMECHANGED
		)
	}
	
	override fun callback(hwnd: HWND, uMsg: Int, wparam: WPARAM, lparam: LPARAM): LRESULT {
		return when(uMsg) {
			WM_NCCALCSIZE -> LRESULT(0)
			WM_NCHITTEST -> {
				val lresult = params.hitTest(hwnd)
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
}
