package com.idex.util


@Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // `b!!::class`, https://youtrack.jetbrains.com/issue/KT-37878
inline fun <reified A : Any, B> equals(a: A, b: B, compare: (A) -> Boolean) = when {
	a === b -> true
	b !is A -> false
	a::class != b!!::class -> false
	else -> compare(b)
}

