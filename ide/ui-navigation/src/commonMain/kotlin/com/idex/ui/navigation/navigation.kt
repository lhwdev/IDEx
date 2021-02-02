package com.idex.ui.navigation

import androidx.compose.runtime.Stable
import com.idex.ui.util.mutableStateStackOf
import com.idex.util.MutableStack


interface RouteId

interface Route<out Id : RouteId> {
	val id: Id
}

interface RouteLifecycleListener {
	fun onPush() {}
	fun onPop(popTarget: Route<*>): Boolean = true
}

@Stable
data class NavigationState(
	val currentRoute: Route<*>,
	internal val routes: MutableStack<Route<*>> = mutableStateStackOf()
)


fun NavigationState.pushRoute(route: Route<*>) {
	routes.pushFirst(route)
}

fun NavigationState.popRoute(route: Route<*> = routes.peekFirst()) {
	while(routes.isNotEmpty()) {
		val first = routes.peekFirst()
		if(first == route) break
		else {
			val result = (first as? RouteLifecycleListener)?.onPop(route) ?: true
			if(!result) break // a route refused to pop
			routes.popFirst()
		}
	}
}

fun NavigationState.replaceRoute(route: Route<*>) {
	popRoute(currentRoute)
	pushRoute(route)
}
