package com.idex.plugin

import com.idex.Scope
import com.idex.ScopeInfo


/**
 * A class which contains information about a plugin.
 */
class Plugin(val config: PluginConfig) : Scope {
	override val scopeInfo: ScopeInfo = ScopeInfo(config.name)
}
