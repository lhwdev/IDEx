package com.idex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import com.idex.util.firstInstanceOf


val AmbientNavigationState = ambientOf<NavigationState>()


interface ComposableRoute<out Id : RouteId> : Route<Id> {
	@Composable
	fun content()
}


@Composable
fun NavigationRoot(state: NavigationState, content: @Composable () -> Unit) {
	Providers(AmbientNavigationState provides state, content = content)
}

@Composable
fun RouteScope(currentRoute: Route<*>, content: @Composable () -> Unit) {
	Providers(
		AmbientNavigationState provides AmbientNavigationState.current.copy(currentRoute = currentRoute),
		content = content
	)
}


@Composable
fun ComposableRouteHost() {
	val state = AmbientNavigationState.current
	
	// TODO: preserve states for routes in back stack
	val topRoute = state.routes.firstInstanceOf<ComposableRoute<*>>()
	
	RouteScope(topRoute) {
		topRoute.content()
	}
}
