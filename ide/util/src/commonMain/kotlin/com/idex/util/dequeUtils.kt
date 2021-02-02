package com.idex.util


inline fun <reified T> Stack<*>.firstInstanceOf(): T {
	forEach { if(it is T) return it }
	throw NoSuchElementException("Collection contains no element matching the predicate.")
}

inline fun <reified T> Stack<*>.firstInstanceOfOrNull(): T? {
	forEach { if(it is T) return it }
	return null
}
