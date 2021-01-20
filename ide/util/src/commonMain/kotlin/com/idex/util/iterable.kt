package com.idex.util


inline fun <T> iterable(crossinline block: () -> Iterator<T>) = object : Iterable<T> {
	override fun iterator() = block()
}
