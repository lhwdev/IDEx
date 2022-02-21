package com.idex.app

import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.idex.ui.components.IdexTheme


@Composable
fun AppTheme(content: @Composable () -> Unit) {
	IdexTheme(
		colors = Colors(
			primary = Color(0xff455a64),
			primaryVariant = Color(0xff37474f),
			onPrimary = Color(0xffffffff),
			secondary = Color(0xffbdbdbd),
			secondaryVariant = Color(0xff929292),
			onSecondary = Color(0xff000000),
			background = Color(0xfffafafa),
			onBackground = Color(0xff000000),
			surface = Color(0xffffffff),
			onSurface = Color(0xff000000),
			error = Color(0xfff44336),
			onError = Color(0xff000000),
			isLight = true
		),
		typography = Typography(),
		content = content
	)
}
