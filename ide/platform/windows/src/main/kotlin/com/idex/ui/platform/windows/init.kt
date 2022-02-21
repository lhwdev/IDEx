package com.idex.ui.platform.windows

import javax.swing.UIManager


fun initWindows() {
	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
}
