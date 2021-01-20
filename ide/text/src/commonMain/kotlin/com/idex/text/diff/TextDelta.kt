/*
 * Copyright 2018~2020 java-diff-utils.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.idex.text.diff

import com.idex.diff.DeltaType
import com.idex.text.MutableText


/**
 * Abstract delta between a source and a target.
 * @author Tobias Warneke (t.warneke@gmx.net)
 */
sealed class TextDelta {
	abstract val type: DeltaType
	abstract val source: TextChunk
	abstract val target: TextChunk
	
	protected fun verifyChunk(target: CharSequence) {
		source.verify(target)
	}
	
	abstract fun applyTo(target: MutableText)
	
	abstract fun restore(target: MutableText)
	
	/**
	 * Create a new delta of the actual instance with customized chunk data.
	 */
	abstract fun withChunks(original: TextChunk, revised: TextChunk): TextDelta
}


data class EqualTextDelta(val data: TextChunk) : TextDelta() {
	constructor(source: TextChunk, target: TextChunk) : this(source) {
		check(source == target)
	}
	
	override val type get() = DeltaType.equal
	
	override val source get() = data
	override val target get() = data
	
	override fun applyTo(target: MutableText) {
		verifyChunk(target)
	}
	
	override fun restore(target: MutableText) {}
	
	override fun toString() = "[EqualDelta, position: ${source.position}, lines: ${source.replacement}]"
	
	override fun withChunks(original: TextChunk, revised: TextChunk) = EqualTextDelta(original, revised)
}

/**
 * Describes the add-delta between original and revised texts.
 * `` is the type of the compared elements in the 'lines'.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
data class InsertTextDelta(override val source: TextChunk, override val target: TextChunk) : TextDelta() {
	override val type get() = DeltaType.insert
	
	override fun applyTo(target: MutableText) {
		verifyChunk(target)
		target.insert(source.position, this.target.replacement)
	}
	
	override fun restore(target: MutableText) {
		val position = this.target.position
		target.remove(position, position + this.target.size)
	}
	
	override fun withChunks(original: TextChunk, revised: TextChunk) = InsertTextDelta(original, revised)
	
	override fun toString() = "[InsertDelta, position: ${source.position}, lines: ${target.replacement}]"
}

/**
 * Describes the delete-delta between original and revised texts.
 * `` is the type of the compared elements in the 'lines'.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
data class DeleteTextDelta(override val source: TextChunk, override val target: TextChunk) : TextDelta() {
	override val type get() = DeltaType.delete
	
	override fun applyTo(target: MutableText) {
		verifyChunk(target)
		val position = source.position
		target.remove(position, position + source.size)
	}
	
	override fun restore(target: MutableText) {
		target.insert(this.target.position, source.replacement)
	}
	
	override fun withChunks(original: TextChunk, revised: TextChunk) = DeleteTextDelta(original, revised)
	
	override fun toString() = "[DeleteDelta, position: ${source.position}, lines: ${source.replacement}]"
}


/**
 * Describes the change-delta between original and revised texts.
 * `` is the type of the compared elements in the data 'lines'.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
data class ChangeTextDelta(override val source: TextChunk, override val target: TextChunk) : TextDelta() {
	override val type = DeltaType.change
	
	override fun applyTo(target: MutableText) {
		verifyChunk(target)
		val position = source.position
		target.replace(startIndex = position, endIndex = position + source.size, replacement = this.target.replacement)
	}
	
	override fun restore(target: MutableText) {
		val position = this.target.position
		target.replace(position, position + this.target.size, this.source.replacement)
	}
	
	override fun withChunks(original: TextChunk, revised: TextChunk) = ChangeTextDelta(original, revised)
	
	override fun toString() =
		"[ChangeDelta, position: ${source.position}, lines: ${source.replacement} to ${target.replacement}]"
}
