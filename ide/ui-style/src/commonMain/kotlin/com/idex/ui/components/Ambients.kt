package com.idex.ui.components

import androidx.compose.runtime.ambientOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle


val AmbientContentColor = ambientOf { Color.Black }

val AmbientContentAlpha = ambientOf { 1f }

val AmbientTextStyle = ambientOf { TextStyle() }
