package com.idex.plugin

import androidx.compose.runtime.*
import com.idex.LocalScope


private val sLocalPlugin: ProvidableCompositionLocal<Plugin?> = compositionLocalOf { null }

/**
 * This is exposed as [CompositionLocal] type, not [ProvidableCompositionLocal], as providing via LocalPlugin does not
 * also provide [LocalScope].
 *
 * If you need, you can cast this [LocalPlugin] into `ProvidableCompositionLocal<Plugin?>`.
 */
public val LocalPlugin: CompositionLocal<Plugin?> = sLocalPlugin

@Composable
public fun ProvidePlugin(plugin: Plugin, content: @Composable () -> Unit) {
	CompositionLocalProvider(
		LocalScope provides plugin,
		sLocalPlugin provides plugin,
		content = content
	)
}
