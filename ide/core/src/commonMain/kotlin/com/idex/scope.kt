package com.idex

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf


data class ScopeInfo(val name: String)

interface Scope {
	val scopeInfo: ScopeInfo
}

val LocalScope: ProvidableCompositionLocal<Scope?> = compositionLocalOf { null }
