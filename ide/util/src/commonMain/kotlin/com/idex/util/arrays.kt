package com.idex.util


@Suppress("NOTHING_TO_INLINE")
inline fun <T> Array<T>.swap(newElement: T, index: Int): T {
	val last = this[index]
	this[index] = newElement
	return last
}

@Suppress("NOTHING_TO_INLINE")
inline fun IntArray.swap(newElement: Int, index: Int): Int {
	val last = this[index]
	this[index] = newElement
	return last
}
