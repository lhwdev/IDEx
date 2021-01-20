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
package com.idex.diff


@PublishedApi
internal val sDeltaComparator = compareBy<Delta<*>> { it.source.position }

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> deltaComparator() = sDeltaComparator as Comparator<T>


/**
 * Abstract delta between a source and a target.
 * @author Tobias Warneke (t.warneke@gmx.net)
 */
sealed class Delta<T> {
	abstract val type: DeltaType
	abstract val source: Chunk<T>
	abstract val target: Chunk<T>
	
	protected fun verifyChunk(target: List<T>) {
		source.verify(target)
	}
	
	abstract fun applyTo(target: MutableList<T>)
	
	abstract fun restore(target: MutableList<T>)
	
	/**
	 * Create a new delta of the actual instance with customized chunk data.
	 */
	abstract fun withChunks(original: Chunk<T>, revised: Chunk<T>): Delta<T>
}


data class EqualDelta<T>(val data: Chunk<T>) : Delta<T>() {
	constructor(source: Chunk<T>, target: Chunk<T>) : this(source) {
		check(source == target)
	}
	
	override val type get() = DeltaType.equal
	
	override val source get() = data
	override val target get() = data
	
	override fun applyTo(target: MutableList<T>) {
		verifyChunk(target)
	}
	
	override fun restore(target: MutableList<T>) {}
	
	override fun toString() = "[EqualDelta, position: ${source.position}, lines: ${source.lines}]"
	
	override fun withChunks(original: Chunk<T>, revised: Chunk<T>) = EqualDelta(original, revised)
}

/**
 * Describes the add-delta between original and revised texts.
 * `<T>` is the type of the compared elements in the 'lines'.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
data class InsertDelta<T>(override val source: Chunk<T>, override val target: Chunk<T>) : Delta<T>() {
	override val type get() = DeltaType.insert
	
	override fun applyTo(target: MutableList<T>) {
		verifyChunk(target)
		val position = this.source.position
		val lines = this.target.lines
		for(i in 0 until lines.size) {
			target.add(position + i, lines[i])
		}
	}
	
	override fun restore(target: MutableList<T>) {
		val position = this.target.position
		val size = this.target.size
		
		for(i in 0 until size) {
			target.removeAt(position)
		}
	}
	
	override fun withChunks(original: Chunk<T>, revised: Chunk<T>) = InsertDelta(original, revised)
	
	override fun toString() = "[InsertDelta, position: ${source.position}, lines: ${target.lines}]"
}

/**
 * Describes the delete-delta between original and revised texts.
 * `<T>` is the type of the compared elements in the 'lines'.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
data class DeleteDelta<T>(override val source: Chunk<T>, override val target: Chunk<T>) : Delta<T>() {
	override val type get() = DeltaType.delete
	
	override fun applyTo(target: MutableList<T>) {
		verifyChunk(target)
		val position = source.position
		val size = source.size
		for(i in 0 until size) {
			target.removeAt(position)
		}
	}
	
	override fun restore(target: MutableList<T>) {
		val position = this.target.position
		val lines = source.lines
		for(i in 0 until lines.size) {
			target.add(position + i, lines[i])
		}
	}
	
	override fun withChunks(original: Chunk<T>, revised: Chunk<T>) = DeleteDelta(original, revised)
	
	override fun toString() = "[DeleteDelta, position: ${source.position}, lines: ${source.lines}]"
}


/**
 * Describes the change-delta between original and revised texts.
 * `<T>` is the type of the compared elements in the data 'lines'.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
data class ChangeDelta<T>(override val source: Chunk<T>, override val target: Chunk<T>) : Delta<T>() {
	override val type = DeltaType.change
	
	override fun applyTo(target: MutableList<T>) {
		verifyChunk(target)
		val position = source.position
		val size = source.size
		for(i in 0 until size)
			target.removeAt(position)
		
		for((i, line) in this.target.lines.withIndex())
			target.add(position + i, line)
	}
	
	override fun restore(target: MutableList<T>) {
		val position = this.target.position
		val size = this.target.size
		for(i in 0 until size) {
			target.removeAt(position)
		}
		for((i, line) in source.lines.withIndex()) {
			target.add(position + i, line)
		}
	}
	
	override fun withChunks(original: Chunk<T>, revised: Chunk<T>) = ChangeDelta(original, revised)
	
	override fun toString() = "[ChangeDelta, position: ${source.position}, lines: ${source.lines} to ${target.lines}]"
}

