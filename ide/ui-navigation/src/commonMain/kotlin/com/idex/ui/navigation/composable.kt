package com.idex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.idex.util.firstInstanceOf


val LocalNavigationState = compositionLocalOf<NavigationState> { error("not provided") }


interface ComposableRoute<out Id : RouteId> : Route<Id> {
	@Composable
	fun content()
}


@Composable
fun NavigationRoot(state: NavigationState, content: @Composable () -> Unit) {
	CompositionLocalProvider(LocalNavigationState provides state, content = content)
}

@Composable
fun RouteScope(currentRoute: Route<*>, content: @Composable () -> Unit) {
	CompositionLocalProvider(
		LocalNavigationState provides LocalNavigationState.current.copy(currentRoute = currentRoute),
		content = content
	)
}


@Composable
fun ComposableRouteHost() {
	val state = LocalNavigationState.current
	
	// TODO: preserve states for routes in back stack
	val topRoute = state.routes.firstInstanceOf<ComposableRoute<*>>()
	
	RouteScope(topRoute) {
		topRoute.content()
	}
}
