// Copyright 2020 Kalkidan Betre Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:Suppress("SpellCheckingInspection")

package com.beranabyte.ui.customdecoration

import com.beranabyte.ui.customdecoration.User32Ex.Companion.WM_NCCALCSIZE
import com.beranabyte.ui.customdecoration.User32Ex.Companion.WM_NCHITTEST
import com.sun.jna.platform.win32.BaseTSD
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*


class TransparentDecorationWindowProc(hwnd: HWND, val params: CustomDecorationParameters) : WindowProc {
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
				val lresult = params.hitTest(hwnd)
				when(lresult.toInt()) {
					LRESULT(0).toInt() ->
						INSTANCEEx.CallWindowProc(defWndProc, hwnd, uMsg, wparam, lparam)
					
					LRESULT(1).toInt() -> /* HTCLIENT */
						lresult
					
					else -> LRESULT(-1) /* HTTRANSPARENT */
				}
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
