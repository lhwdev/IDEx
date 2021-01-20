package com.idex.text


fun CharSequence.iterator(
	startIndex: Int, endIndex: Int = length
): CharIterator = if(endIndex == -1) object : CharIterator() {
	private var index = startIndex
	override fun nextChar(): Char = get(index++)
	override fun hasNext(): Boolean = index < length
} else object : CharIterator() {
	private var index = startIndex
	override fun nextChar(): Char {
		check(hasNext()) { "iterating text: bound out of buffer" }
		return get(index++)
	}
	
	override fun hasNext(): Boolean = index < endIndex
}


class SimpleImmutableTextBuffer(override val text: CharSequence) : ImmutableTextBuffer() {
	override val length get() = text.length
	
	override fun limit(startIndex: Int, endIndex: Int, isFixed: Boolean): ImmutableTextBuffer =
		SimpleImmutableTextBufferRanged(text, startIndex, endIndex, isFixed)
	
	override fun iterate(startIndex: Int, endIndex: Int) = text.iterator(startIndex, endIndex)
	
	override fun cache(startIndex: Int, endIndex: Int) = text.limitOffset(startIndex, endIndex)
	
	override fun cacheAll() = text
}

class SimpleImmutableTextBufferRanged(
	private val originalText: CharSequence,
	private val startIndex: Int,
	private val endIndex: Int,
	private val isFixed: Boolean
) : ImmutableTextBuffer() {
	private fun checkBound(index: Int) {
		check(index in startIndex until endIndex) {
			"index($index) out of bound $startIndex until $endIndex"
		}
	}
	
	private fun checkBound(startIndex: Int, endIndex: Int) {
		check(startIndex >= this.startIndex) {
			"startIndex($startIndex) out of bound ${this.startIndex} until ${this.endIndex}"
		}
		check(endIndex <= this.endIndex) {
			"endIndex($endIndex) out of bound ${this.startIndex} until ${this.endIndex}"
		}
		check(startIndex <= endIndex) { "startIndex($startIndex) > endIndex($endIndex)" }
	}
	
	override val length get() = endIndex - startIndex
	
	override val text = object : CharSequence {
		override val length = originalText.length
		override fun get(index: Int): Char {
			checkBound(index)
			return originalText[index]
		}
		
		override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
			checkBound(startIndex, endIndex)
			return originalText.subSequence(startIndex, endIndex)
		}
	}
	
	override fun limit(startIndex: Int, endIndex: Int, isFixed: Boolean): ImmutableTextBuffer {
		if(this.isFixed) checkBound(startIndex, endIndex)
		return SimpleImmutableTextBufferRanged(text, startIndex, endIndex, isFixed)
	}
	
	override fun iterate(startIndex: Int, endIndex: Int): CharIterator {
		if(isFixed) checkBound(startIndex, endIndex)
		return text.iterator(startIndex, endIndex)
	}
	
	override fun cache(startIndex: Int, endIndex: Int) = text.limitOffset(startIndex, endIndex)
	
	override fun cacheAll() = text
}
