package com.idex.util


inline fun <reified A : Any, B> equals(a: A, b: B, compare: (A) -> Boolean) = when {
	a === b -> true
	b !is A -> false
	@Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // https://youtrack.jetbrains.com/issue/KT-37878
	a::class != b::class -> false
	else -> compare(b)
}

