package com.idex.util


inline fun <T> List<T>.forEachReversed(block: (T) -> Unit) {
	val iterator = listIterator(size)
	while(iterator.hasPrevious())
		block(iterator.previous())
}

inline fun <T> List<T>.forEachReversedIndexed(block: (index: Int, T) -> Unit) {
	val size = size
	val iterator = listIterator(size)
	var index = size
	while(iterator.hasPrevious())
		block(index--, iterator.previous())
}

inline fun <reified T> List<*>.lastInstanceOfOrNull(): T? {
	forEachReversed { if(it is T) return it }
	return null
}

inline fun <reified T> List<*>.lastInstanceOf(): T {
	forEachReversed { if(it is T) return it }
	throw NoSuchElementException("Collection contains no element matching the predicate.")
}

inline fun <reified T> List<*>.firstInstanceOfOrNull(): T? {
	forEach { if(it is T) return it }
	return null
}

inline fun <reified T> List<*>.firstInstanceOf(): T {
	forEach { if(it is T) return it }
	throw NoSuchElementException("Collection contains no element matching the predicate.")
}


