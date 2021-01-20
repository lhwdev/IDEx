package com.idex.text

import kotlin.math.min


private const val DEFAULT_LENGTH = 16

private const val DEFAULT_GROW_FACTOR = 2f

private const val MAX_SIZE = Int.MAX_VALUE / 2


// TODO: implement limit, offset
open class ArrayMutableText private constructor(
	override var length: Int = 0,
	var growFactor: Float = DEFAULT_GROW_FACTOR,
	override var array: CharArray
) : MutableText, ArrayText {
	constructor(growFactor: Float = DEFAULT_GROW_FACTOR, capacity: Int = (DEFAULT_LENGTH * growFactor).toInt()) :
		this(growFactor = growFactor, array = CharArray(capacity))
	
	constructor(text: CharArray, startIndex: Int = 0, endIndex: Int = text.size) :
		this(array = text.copyOfRange(startIndex, endIndex))
	
	constructor(text: CharSequence, startIndex: Int = 0, endIndex: Int = text.length) : this(
		length = endIndex - startIndex,
		array = CharArray(text.length + 16).also {
			text.copyInto(it, startIndex = startIndex, endIndex = endIndex)
		})
	
	
	inline val capacity get() = array.size
	
	
	inline fun ensureCapacity(
		newLength: Int,
		preserveSize: Int = length,
		extraCopy: (old: CharArray, new: CharArray) -> Unit
	) {
		val oldArray: CharArray
		val newArray: CharArray
		
		if(newLength > array.size) {
			oldArray = array
			newArray = newArray(newLength)
			oldArray.copyInto(newArray, endIndex = preserveSize)
			array = newArray
		} else {
			oldArray = array
			newArray = oldArray
		}
		
		extraCopy(oldArray, newArray)
	}
	
	fun ensureCapacity(newLength: Int, preserveSize: Int = length) = if(newLength > array.size) {
		val oldArray = array
		val newArray = newArray(newLength)
		
		oldArray.copyInto(newArray, endIndex = preserveSize)
		array = newArray
		oldArray
	} else array
	
	fun newArray(newLength: Int) = CharArray(min((newLength * growFactor).toInt() + 16, MAX_SIZE))
	
	override fun set(index: Int, char: Char) {
		checkBound(index, length)
		
		array[index] = char
	}
	
	override fun append(text: CharSequence, startIndex: Int, endIndex: Int) {
		val len = endIndex - startIndex
		
		ensureCapacity(length + len)
		
		text.copyInto(array, destinationOffset = length, startIndex = startIndex, endIndex = endIndex)
		length += len
	}
	
	override fun append(text: CharArray, startIndex: Int, endIndex: Int) {
		val len = endIndex - startIndex
		
		ensureCapacity(length + len)
		
		text.copyInto(array, destinationOffset = length, startIndex = startIndex, endIndex = endIndex)
		length += len
	}
	
	override fun append(char: Char) {
		ensureCapacity(length + 1)
		
		array[length] = char
		length++
	}
	
	override fun insert(where: Int, text: CharSequence, startIndex: Int, endIndex: Int) {
		checkBound(where, length)
		
		val len = endIndex - startIndex
		
		// ###########|@@@@@@@@@@@@
		// ###########|++++++|@@@@@@@@@@@@
		//            ^      ^
		//         where   where + len
		
		ensureCapacity(length + len, preserveSize = where) { old, new ->
			old.copyInto(new, destinationOffset = where + len, startIndex = where, endIndex = length)
		}
		text.copyInto(array, destinationOffset = where, startIndex = startIndex, endIndex = endIndex)
		
		length += len
	}
	
	override fun remove(startIndex: Int, endIndex: Int) {
		checkBound(startIndex, endIndex, length)
		
		val len = endIndex - startIndex
		
		// ###########|~~~~~~~~|@@@@@@@@@@@@
		// ###########|@@@@@@@@@@@@
		//            ^        ^
		//          start     end
		array.copyInto(array, destinationOffset = startIndex, startIndex = endIndex, endIndex = length)
		
		length -= len
	}
	
	override fun set(startIndex: Int, endIndex: Int, text: CharSequence, textIndex: Int) {
		checkBound(startIndex, endIndex, length)
		
		val len = endIndex - startIndex
		
		text.copyInto(array, destinationOffset = startIndex, startIndex = textIndex, endIndex = textIndex + len)
	}
	
	override fun replace(
		startIndex: Int, endIndex: Int,
		replacement: CharSequence,
		replacementStartIndex: Int, replacementEndIndex: Int
	) {
		checkBound(startIndex, endIndex, length)
		checkBound(replacementStartIndex, replacementEndIndex, replacement.length)
		
		val oldLen = endIndex - startIndex
		val replacedLen = replacementEndIndex - replacementStartIndex
		val newLength = length - oldLen + replacedLen
		
		// ###########|-------------------------|@@@@@@@@@@@@@@@@@@
		// ###########|++++++++++++|@@@@@@@@@@@@@@@@@@
		//            ^            ^            ^
		//         start  start + replacedLen  end
		
		ensureCapacity(newLength, preserveSize = startIndex) { old, new ->
			old.copyInto(
				new,
				destinationOffset = startIndex + replacedLen,
				startIndex = endIndex,
				endIndex = length
			)
		}
		
		replacement.copyInto(
			array,
			destinationOffset = startIndex,
			startIndex = replacementStartIndex,
			endIndex = replacementEndIndex
		)
		
		length = newLength
	}
	
	override fun get(index: Int): Char {
		checkBound(index, length)
		return array[index]
	}
	
	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		checkBound(startIndex, endIndex, length)
		return array.toCharSequence(startIndex, endIndex)
	}
}
