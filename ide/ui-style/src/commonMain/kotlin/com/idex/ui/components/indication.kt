package com.idex.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.ContentDrawScope


val IdexIndication: @Composable () -> Indication = {
	val color = AmbientContentColor.current
	
	object : Indication {
		override fun createInstance() = object : IndicationInstance {
			override fun ContentDrawScope.drawIndication(interactionState: InteractionState) {
				when(interactionState.value.first()) {
					Interaction.Pressed -> drawRect(color.copy(alpha = .15f))
					Interaction.Dragged -> drawRect(color.copy(alpha = .18f))
					Interaction.Focused -> drawRect(color.copy(alpha = .05f))
					// Interaction.Hovered -> drawRect(color.copy(alpha = .1f))
				}
			}
		}
	}
}
