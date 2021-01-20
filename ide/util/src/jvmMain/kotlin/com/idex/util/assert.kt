@file:JvmName("AssertJvm")

package com.idex.util


private class S

@PublishedApi
@JvmField
internal val ea = S::class.java.desiredAssertionStatus()

actual inline fun assert(block: () -> Unit) {
	if(ea) block()
}
