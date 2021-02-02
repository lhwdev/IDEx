package com.idex.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idex.ui.ComposableWindow
import com.idex.ui.WindowInfo
import com.idex.ui.components.IdexTheme
import com.idex.ui.components.Surface
import com.idex.ui.components.Text
import com.idex.ui.components.primarySurface
import com.idex.ui.navigation.AmbientNavigationState
import com.idex.ui.navigation.replaceRoute


val SplashRoute = AppRoute(AppRouteId()) { Splash() }


class IdexContext

@Suppress("unused", "RedundantSuspendModifier")
suspend fun IdexContext.idexInit() {
	// TODO
}


@Composable
fun Splash() = ComposableWindow(WindowInfo("IDEx")) {
	val navigation = AmbientNavigationState.current
	val context = remember { IdexContext() }
	
	LaunchedEffect(Unit) {
		context.idexInit()
		
		// complete
		navigation.replaceRoute(MainIdeRoute)
	}
	
	Surface(
		backgroundColor = IdexTheme.colors.primarySurface,
		modifier = Modifier.fillMaxSize()
	) {
		Box(Modifier.fillMaxSize().padding(64.dp)) {
			Text("IDEx", style = IdexTheme.typography.h1, modifier = Modifier.align(Alignment.CenterStart))
		}
	}
}
