package com.idex.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idex.ui.components.IdexTheme
import com.idex.ui.desktop.window.ComposableWindow
import com.idex.ui.desktop.window.WindowInfo
import com.idex.ui.navigation.LocalNavigationState


val SplashRoute = AppRoute(AppRouteId()) { Splash() }


class IdexContext

@Suppress("unused", "RedundantSuspendModifier")
suspend fun IdexContext.idexInit() {
	// TODO
}


@Composable
fun Splash() = ComposableWindow(WindowInfo("IDEx")) {
	val navigation = LocalNavigationState.current
	val context = remember { IdexContext() }
	
	LaunchedEffect(Unit) {
		context.idexInit()
		
		// complete
		// navigation.replaceRoute(MainIdeRoute)
	}
	
	Surface(
		color = IdexTheme.colors.primarySurface,
		modifier = Modifier.fillMaxSize()
	) {
		Row {
			// Button(onClick = {}) {
			// 	Box(modifier = Modifier.drawBehind {
			// 		drawRect(Color.Red)
			// 	})
			// 	Text("HO", style = IdexTheme.typography.h3, modifier = Modifier.background(Color.Red).fillMaxSize())
			// }
			Box(Modifier.fillMaxSize().padding(64.dp)) {
				Text("IDEx", style = IdexTheme.typography.h1, modifier = Modifier.align(Alignment.CenterStart))
			}
		}
	}
}
