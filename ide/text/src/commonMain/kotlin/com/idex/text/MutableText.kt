@file:Suppress("NOTHING_TO_INLINE")

package com.idex.text


const val EMPTY_TEXT = ""


/**
 * A text representing a sequence of characters that can be modified and observed.
 * [MutableText] also provides 'marker' which can be used to track the position of a text fragment.
 *
 * It's better not to implement this class directly, instead use [TextEditor] that is fit for most
 * purposes of text editor, or [ArrayMutableText] which is actually a wrapper of a char array.
 *
 * This class does not guarantee synchronization.
 */
interface MutableText : CharSequence {
	/**
	 * The character at the specified index is set to [char]. This text is altered to represent a
	 * new character sequence that is identical to the old character sequence except that it
	 * contains the character [char] at position [index].
	 *
	 * The index argument must be greater than or equal to `0`, and less than the length of this
	 * text.
	 *
	 * @param index the index of the character to modify.
	 * @param char the new character.
	 * @throws IndexOutOfBoundsException if [index] is negative or greater than or equal to
	 * [length].
	 *
	 * @see get
	 */
	operator fun set(index: Int, char: Char)
	
	
	fun append(text: CharSequence, startIndex: Int = 0, endIndex: Int = text.length) {
		insert(length, text, startIndex, endIndex)
	}
	
	fun append(text: CharArray, startIndex: Int = 0, endIndex: Int = text.size) {
		append(text.toCharSequence(startIndex, endIndex))
	}
	
	fun append(char: Char) {
		append("$char")
	}
	
	fun insert(where: Int, text: CharSequence, startIndex: Int = 0, endIndex: Int = text.length) {
		replace(where, where, text, startIndex, endIndex)
	}
	
	fun replace(
		startIndex: Int, endIndex: Int,
		replacement: CharSequence,
		replacementStartIndex: Int = 0, replacementEndIndex: Int = replacement.length
	)
	
	fun remove(startIndex: Int, endIndex: Int) {
		replace(startIndex, endIndex, EMPTY_TEXT)
	}
	
	
	fun set(startIndex: Int, endIndex: Int, text: CharSequence, textIndex: Int = 0) {
		for(i in 0 until endIndex - startIndex)
			this[i + startIndex] = text[i + textIndex]
	}
	
	
	/**
	 * Returns a mutable text that is a synchronized subsequence of this text, starting at the
	 * specified [startIndex] and ending right before the specified [endIndex].
	 *
	 * Using the returned one is synchronized with the original text(`this`), so `text.part(1, 5)[2]`
	 * is identical to `text[3]`. All index is modified to fit the original text and if it exceeds
	 * length(= `endIndex - startIndex`), it may throw [IndexOutOfBoundsException].
	 *
	 * All operations to the returned one is not guaranteed to be performed at once or to be
	 * synchronized.
	 *
	 * @see offset
	 */
	fun limitOffset(startIndex: Int = 0, endIndex: Int = length): MutableText = object : MutableText {
		private val translation = startIndex
		
		
		override fun set(index: Int, char: Char) {
			checkBound(index, length)
			this@MutableText[index + translation] = char
		}
		
		override val length = endIndex - startIndex
		
		override fun get(index: Int): Char {
			checkBound(index, length)
			return this@MutableText[index + translation]
		}
		
		override fun set(startIndex: Int, endIndex: Int, text: CharSequence, textIndex: Int) {
			checkBound(startIndex, endIndex, length)
			this@MutableText.set(startIndex + translation, endIndex + translation, text, textIndex)
		}
		
		override fun replace(
			startIndex: Int,
			endIndex: Int,
			replacement: CharSequence,
			replacementStartIndex: Int,
			replacementEndIndex: Int
		) {
			checkBound(startIndex, endIndex, length)
			this@MutableText.replace(
				startIndex + translation,
				endIndex + translation,
				replacement,
				replacementStartIndex,
				replacementEndIndex
			)
		}
		
		override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
			checkBound(startIndex, endIndex, length)
			return this@MutableText.subSequence(startIndex + translation, endIndex + translation)
		}
	}
	
	/**
	 * Returns a mutable text that is a synchronized subsequence of this text, translated by [index].
	 *
	 * Using the returned one is synchronized with the original text(`this`), so `text.part(1, 5)[2]`
	 * is identical to `text[3]`. This function do not add additional bound limitations to returned
	 * one, so for instance, you can even do `text.partUnbounded(3)[-1] = 'h'` (of course text[2] is
	 * in the bound of the text)
	 *
	 * For addition, the length of returned one is `length - index`.
	 *
	 * All operations to the returned one is not guaranteed to be performed at once or to be
	 * synchronized.
	 *
	 * @see limitOffset
	 */
	fun offset(index: Int): MutableText = object : MutableText {
		val translation = index
		
		override fun set(index: Int, char: Char) {
			this@MutableText[index + translation] = char
		}
		
		override val length: Int get() = this@MutableText.length - translation
		
		override fun set(startIndex: Int, endIndex: Int, text: CharSequence, textIndex: Int) {
			this@MutableText.set(startIndex + translation, endIndex + translation, text, textIndex)
		}
		
		override fun replace(
			startIndex: Int,
			endIndex: Int,
			replacement: CharSequence,
			replacementStartIndex: Int,
			replacementEndIndex: Int
		) {
			this@MutableText.replace(
				startIndex + translation,
				endIndex + translation,
				replacement,
				replacementStartIndex,
				replacementEndIndex
			)
		}
		
		override fun get(index: Int) = this@MutableText[index + translation]
		
		override fun subSequence(startIndex: Int, endIndex: Int) =
			this@MutableText.subSequence(startIndex + translation, endIndex + translation)
	}
}

/**
 * Set the characters in the [range] to the [text].
 */
inline operator fun MutableText.set(range: IntRange, text: CharSequence) {
	set(range.first, range.last, text)
}


// extensions

inline operator fun MutableText.plusAssign(text: CharSequence) {
	append(text)
}

inline operator fun MutableText.plusAssign(text: CharArray) {
	append(text)
}

inline operator fun MutableText.plusAssign(char: Char) {
	append(char)
}

inline fun MutableText.remove(range: IntRange) {
	remove(range.first, range.last + 1)
}


inline fun MutableText.replaceChar(
	where: Int,
	replacement: CharSequence,
	replacementStartIndex: Int = 0, replacementEndIndex: Int = replacement.length
) {
	replace(where, where + 1, replacement, replacementStartIndex, replacementEndIndex)
}

