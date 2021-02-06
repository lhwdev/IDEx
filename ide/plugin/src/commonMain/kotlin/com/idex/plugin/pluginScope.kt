package com.idex.plugin

import androidx.compose.runtime.*
import com.idex.AmbientScope


private val sAmbientPlugin: ProvidableAmbient<Plugin?> = ambientOf { null }

/**
 * This is exposed as [Ambient] type, not [ProvidableAmbient], as providing via AmbientPlugin does not also provide
 * [AmbientScope].
 *
 * If you need, you can cast this [AmbientPlugin] into `ProvidableAmbient<Plugin?>`.
 */
val AmbientPlugin: Ambient<Plugin?> = sAmbientPlugin

@Composable
fun ProvidePlugin(plugin: Plugin, content: @Composable () -> Unit) {
	Providers(
		AmbientScope provides plugin,
		sAmbientPlugin provides plugin,
		content = content
	)
}
