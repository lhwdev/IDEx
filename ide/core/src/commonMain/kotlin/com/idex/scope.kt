package com.idex

import androidx.compose.runtime.ProvidableAmbient
import androidx.compose.runtime.ambientOf


data class ScopeInfo(val name: String)

interface Scope {
	val scopeInfo: ScopeInfo
}

val AmbientScope: ProvidableAmbient<Scope?> = ambientOf { null }
