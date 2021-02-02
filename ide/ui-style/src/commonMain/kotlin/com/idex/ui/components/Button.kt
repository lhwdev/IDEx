package com.idex.ui.components

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp


@Composable
fun Button(
	onClick: () -> Unit,
	enabled: Boolean = true,
	colors: ButtonColors = buttonColors(),
	contentPadding: PaddingValues = DefaultButtonPadding,
	interactionState: InteractionState = remember { InteractionState() },
	content: @Composable () -> Unit
) {
	val backgroundColor = colors.backgroundColor(enabled)
	val contentColor = colors.contentColor(enabled)
	
	Providers(
		AmbientContentColor provides contentColor
	) {
		Surface(
			modifier = Modifier
				.clickable(
					enabled = enabled,
					interactionState = interactionState,
					onClick = onClick
				)
				.padding(contentPadding)
				.background(backgroundColor)
		) {
			content()
		}
	}
}


private val DefaultButtonPadding = PaddingValues(16.dp, 8.dp, 16.dp, 8.dp)

data class ButtonColors(
	val backgroundColor: Color,
	val disabledBackgroundColor: Color,
	val contentColor: Color,
	val disabledContentColor: Color
)

fun ButtonColors.backgroundColor(enabled: Boolean) = if(enabled) backgroundColor else disabledBackgroundColor
fun ButtonColors.contentColor(enabled: Boolean) = if(enabled) contentColor else disabledContentColor


@Composable
fun buttonColors(
	backgroundColor: Color = IdexTheme.colors.primary,
	disabledBackgroundColor: Color = IdexTheme.colors.primary.copy(alpha = 0.12f)
		.compositeOver(IdexTheme.colors.surface),
	contentColor: Color = contentColorFor(backgroundColor),
	disabledContentColor: Color = IdexTheme.colors.onSurface.copy(alpha = 0.4f)
) = ButtonColors(backgroundColor, disabledBackgroundColor, contentColor, disabledContentColor)

