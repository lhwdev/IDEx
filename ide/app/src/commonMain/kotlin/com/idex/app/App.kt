package com.idex.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.idex.ui.navigation.ComposableRouteHost
import com.idex.ui.navigation.NavigationRoot
import com.idex.ui.navigation.NavigationState
import com.idex.ui.navigation.pushRoute


@Composable
fun App() {
	AppTheme {
		val navigationState = remember {
			val state = NavigationState(EntryPointRoute)
			state.pushRoute(EntryPointRoute)
			state
		}
		NavigationRoot(navigationState) {
			ComposableRouteHost()
		}
	}
}
