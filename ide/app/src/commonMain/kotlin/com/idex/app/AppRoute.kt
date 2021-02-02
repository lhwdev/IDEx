package com.idex.app

import androidx.compose.runtime.Composable
import com.idex.ui.navigation.ComposableRoute
import com.idex.ui.navigation.RouteId


class AppRouteId : RouteId


data class AppRoute(override val id: AppRouteId, val content: @Composable () -> Unit) : ComposableRoute<AppRouteId> {
	override fun content() {
		content.invoke()
	}
}
