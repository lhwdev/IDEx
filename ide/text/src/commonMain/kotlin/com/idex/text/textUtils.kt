@file:Suppress("NOTHING_TO_INLINE")

package com.idex.text


fun CharSequence.toCharArray(startIndex: Int = 0, endIndex: Int = length) =
	CharArray(endIndex - startIndex).also {
		copyInto(
			it,
			startIndex = startIndex,
			endIndex = endIndex
		) // some optimizations are already done in [copyInto] so doesn't need to check
	}

fun CharSequence.copyInto(
	destination: CharArray, destinationOffset: Int = 0,
	startIndex: Int = 0, endIndex: Int = length
) {
	when(this) {
		// avoid double delegating
		// but not for CharSequenceWrapper + String...
		is ArrayTextBase -> when(this) {
			is ArrayTextBounded -> {
				checkBound(startIndex, endIndex, length)
				val thisStart = this.startIndex
				array.copyInto(destination, destinationOffset, thisStart + startIndex, thisStart + endIndex)
			}
			is ArrayText -> {
				checkBound(startIndex, endIndex, array.size)
				array.copyInto(destination, destinationOffset, startIndex, endIndex)
			}
			else -> for(i in 0 until endIndex - startIndex)
				destination[destinationOffset + i] = this[startIndex + i]
		}
		is String -> stringCopyInto(destination, destinationOffset, startIndex, endIndex)
		else -> for(i in 0 until endIndex - startIndex)
			destination[destinationOffset + i] = this[startIndex + i]
	}
}

expect fun String.stringCopyInto(
	destination: CharArray, destinationOffset: Int = 0,
	startIndex: Int = 0, endIndex: Int = length
)

inline fun CharSequence.forEach(block: (Char) -> Unit) {
	for(i in 0 until length) block(get(i))
}

inline fun CharSequence.forEachIndexed(block: (index: Int, c: Char) -> Unit) {
	for(i in 0 until length) block(i, get(i))
}

operator fun CharSequence.plus(text: CharSequence): CharSequence =
	CharArray(length + text.length).let {
		copyInto(it)
		text.copyInto(it, destinationOffset = length)
		CharArraySequence(it)
	}

operator fun CharSequence.plus(char: Char): CharSequence =
	CharArray(length + 1).let {
		copyInto(it)
		it[it.lastIndex] = char
		CharArraySequence(it)
	}


fun Char.isLetterOrDigit() = isLetter() || isDigit()

expect fun Char.isDigit(): Boolean

expect fun Char.isLetter(): Boolean


// START from kotlin stdlib

/**
 * Returns the index within this char sequence of the first occurrence of the specified [text],
 * starting from the specified [startIndex].
 *
 * @param ignoreCase `true` to ignore character case when matching a string. By default `false`.
 * @return An index of the first occurrence of [text] or `-1` if none is found.
 */
fun CharSequence.indexOf(text: CharSequence, startIndex: Int = 0, ignoreCase: Boolean = false): Int {
	return if(text !is String)
		indexOf(text, startIndex, length, ignoreCase)
	else indexOf(string = text, startIndex = startIndex, ignoreCase = ignoreCase)
}

/**
 * Returns the index within this char sequence of the last occurrence of the specified [text],
 * starting from the specified [startIndex].
 *
 * @param startIndex The index of character to start searching at. The search proceeds backward toward the beginning of the string.
 * @param ignoreCase `true` to ignore character case when matching a string. By default `false`.
 * @return An index of the last occurrence of [text] or -1 if none is found.
 */
fun CharSequence.lastIndexOf(text: CharSequence, startIndex: Int = lastIndex, ignoreCase: Boolean = false): Int {
	return if(text !is String) {
		indexOf(text, startIndex, 0, ignoreCase, last = true)
	} else {
		lastIndexOf(string = text, startIndex = startIndex)
	}
}

private fun CharSequence.indexOf(
	other: CharSequence,
	startIndex: Int,
	endIndex: Int,
	ignoreCase: Boolean,
	last: Boolean = false
): Int {
	val indices =
		if(!last) startIndex.coerceAtLeast(0)..endIndex.coerceAtMost(length)
		else startIndex.coerceAtMost(lastIndex) downTo endIndex.coerceAtLeast(0)
	
	for(index in indices) {
		if(other.regionMatches(0, this, index, other.length, ignoreCase))
			return index
	}
	
	return -1
}

// END from kotlin stdlib


fun CharSequence.limitOffset(startIndex: Int, endIndex: Int = length): CharSequence = when(this) {
	// avoid double wrapping
	is CharSequenceWrapper -> {
		checkBound(startIndex, endIndex, length)
		CharSequenceWrapper(text, this.startIndex + startIndex, this.startIndex + endIndex)
	}
	is ArrayTextBase -> when(this) {
		is ArrayTextBounded -> {
			checkBound(startIndex, endIndex, this.startIndex, this.endIndex)
			CharArraySequence(array, startIndex, endIndex)
		}
		is ArrayText -> {
			checkBound(startIndex, endIndex, array.size)
			CharArraySequence(array, startIndex, endIndex)
		}
		else -> CharSequenceWrapper(this, startIndex, endIndex)
	}
	else -> CharSequenceWrapper(this, startIndex, endIndex)
}

fun CharSequence.limit(startIndex: Int, endIndex: Int = length): CharSequence = when(this) {
	// avoid double wrapping
	is CharSequenceWrapper -> {
		checkBound(startIndex, endIndex, 0, length)
		CharSequenceWrapper(text, startIndex, endIndex)
	}
	is ArrayTextBase -> when(this) {
		is ArrayTextBounded -> {
			checkBound(startIndex, endIndex, length)
			CharArraySequence(array, startIndex, endIndex)
		}
		is ArrayText -> {
			checkBound(startIndex, endIndex, array.size)
			CharArraySequence(array, startIndex, endIndex)
		}
		else -> CharSequenceWrapper(this, startIndex, endIndex)
	}
	else -> CharSequenceWrapper(this, startIndex, endIndex)
}


fun CharArray.toCharSequence(startIndex: Int = 0, endIndex: Int = size): CharSequence =
	CharArraySequence(copyOfRange(startIndex, endIndex)) // would rather String(...) ?

fun CharArray.wrap(startIndex: Int = 0, endIndex: Int = size): CharSequence =
	CharArraySequence(this, startIndex, endIndex)


inline fun checkBound(index: Int, length: Int) {
	if(index !in 0 until length) throw StringIndexOutOfBoundsException("index($index) is negative or exceeds length($length)")
}


inline fun checkBound(startIndex: Int, endIndex: Int, length: Int) {
	if(startIndex < 0) throw StringIndexOutOfBoundsException("startIndex($startIndex) < 0")
	if(endIndex > length) throw StringIndexOutOfBoundsException("endIndex($endIndex) > length($length)")
	if(startIndex > endIndex) throw StringIndexOutOfBoundsException("startIndex($startIndex) > endIndex($endIndex)")
}

inline fun checkBound(startIndex: Int, endIndex: Int, originalstartIndex: Int, originalendIndex: Int) {
	if(startIndex < originalstartIndex)
		throw StringIndexOutOfBoundsException("startIndex($startIndex) out of bound $originalstartIndex until $originalendIndex")
	if(endIndex > originalendIndex)
		throw StringIndexOutOfBoundsException("endIndex($endIndex) out of bound $originalstartIndex until $originalendIndex")
	if(startIndex > endIndex)
		throw StringIndexOutOfBoundsException("startIndex($startIndex) > endIndex($endIndex)")
}


abstract class CharSequenceBase : CharSequence {
	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
		CharArraySequence(toCharArray(startIndex, endIndex))
	
	override fun equals(other: Any?) = when {
		this === other -> true
		other !is CharSequence -> false
		else -> {
			forEachIndexed { i, c -> if(other[i] != c) return false }
			true
		}
	}
	
	override fun hashCode(): Int {
		var sum = 0
		forEach { sum = sum * 31 + it.toInt() }
		return sum
	}
	
	override fun toString() = toCharArray().concatToString()
}

private class CharSequenceWrapper(val text: CharSequence, val startIndex: Int = 0, endIndex: Int = text.length) :
	CharSequenceBase() {
	override val length = endIndex - startIndex
	
	override fun get(index: Int): Char {
		checkBound(index, length)
		return text[index + startIndex]
	}
	
	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		checkBound(startIndex, endIndex, length)
		return CharArraySequence(text.toCharArray(this.startIndex + startIndex, this.startIndex + endIndex))
	}
	
	override fun toString() = text.toCharArray(startIndex, startIndex + length).concatToString()
}

private class CharArraySequence(
	override val array: CharArray,
	override val startIndex: Int = 0,
	override val endIndex: Int = array.size
) : CharSequenceBase(), ArrayTextBounded {
	override val length = endIndex - startIndex
	
	override fun get(index: Int): Char {
		checkBound(index, length)
		return array[startIndex + index]
	}
	
	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		checkBound(startIndex, endIndex, length)
		return CharArraySequence(array.copyOfRange(this.startIndex + startIndex, this.startIndex + endIndex))
	}
	
	override fun toString() = array.concatToString(startIndex = startIndex, endIndex = startIndex + length)
}
