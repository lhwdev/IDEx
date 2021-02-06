package com.idex.app

import androidx.compose.runtime.Composable
import com.idex.editor.rememberCodeEditor


val MainIdeRoute = AppRoute(AppRouteId()) {
	MainIde()
}


@Composable
fun MainIde() {
	val editor = rememberCodeEditor()
	
	editor()
}
