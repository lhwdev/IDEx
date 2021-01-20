package com.idex.text


sealed class TextBuffer {
	abstract val length: Int
	
	abstract fun cacheAll(): CharSequence
	
	abstract fun cache(startIndex: Int, endIndex: Int): CharSequence
	
	abstract fun limit(startIndex: Int, endIndex: Int, isFixed: Boolean = true): TextBuffer
	
	abstract fun iterate(startIndex: Int, endIndex: Int = -1): CharIterator
}


abstract class ImmutableTextBuffer : TextBuffer() {
	abstract val text: CharSequence
	
	abstract override fun limit(startIndex: Int, endIndex: Int, isFixed: Boolean): ImmutableTextBuffer
}

abstract class TextSnapshot : ImmutableTextBuffer() {
	abstract val id: Any
	
	
	override fun cache(startIndex: Int, endIndex: Int) = text
	
	override fun cacheAll() = text
}


abstract class MutableTextBuffer : TextBuffer() {
	
	abstract override fun limit(startIndex: Int, endIndex: Int, isFixed: Boolean): MutableTextBuffer
	
	/// Snapshots
	
	abstract val snapshots: List<TextSnapshot>
	
	abstract val currentState: CharSequence
	
	abstract val currentSnapshot: TextSnapshot?
	
	abstract fun createSnapshot(): TextSnapshot
	
	abstract fun restoreTo(snapshot: TextSnapshot)
	
	
	/// Editing
	
	/**
	 * The resulting [MutableText] of calling this function after calling [createSnapshot] can be moved to another
	 * thread and built separately. [endEdit] should be called in the original thread that called [beginEdit].
	 */
	abstract fun beginEdit(range: IntRange? = null): MutableText
	
	abstract fun endEdit(text: MutableText)
	
	inline fun <R> edit(range: IntRange? = null, block: MutableText.() -> R): R {
		val text = beginEdit(range)
		return try {
			text.block()
		} finally {
			endEdit(text)
		}
	}
}
