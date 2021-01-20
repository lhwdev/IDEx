@file:Suppress("NOTHING_TO_INLINE")

package com.idex.util


expect inline fun assert(block: () -> Unit)

inline fun assert(condition: Boolean) {
	assert { if(!condition) throw AssertionError() }
}
