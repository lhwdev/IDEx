package com.idex.app

import com.idex.ui.platform.windows.ProvideWindows
import com.idex.ui.platform.windows.initWindows
import com.idex.ui.platform.windows.mainComposable


fun main() {
	initWindows()
	
	mainComposable {
		ProvideWindows {
			App()
		}
	}
}
