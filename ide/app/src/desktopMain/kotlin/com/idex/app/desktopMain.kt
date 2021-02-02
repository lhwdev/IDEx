package com.idex.app

import com.idex.ui.composeRoot
import javax.swing.SwingUtilities


fun main() = SwingUtilities.invokeLater {
	composeRoot {
		App()
	}
}
