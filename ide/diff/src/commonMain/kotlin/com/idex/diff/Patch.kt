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

import io.github.mrasterisco.sortedlist.BinaryTreeMutableList
import io.github.mrasterisco.sortedlist.SortedMutableList


fun <T> generatePatch(
	original: List<T>,
	revised: List<T>,
	changes: List<Change>,
	includeEquals: Boolean = false
): Patch<T> {
	val patch = Patch<T>()
	var startOriginal = 0
	var startRevised = 0
	var changeList = changes
	if(includeEquals) {
		changeList = changes.sortedBy { it.startOriginal }
	}
	for(change in changeList) {
		if(includeEquals && startOriginal < change.startOriginal) {
			patch.addDelta(
				EqualDelta(
					Chunk(startOriginal, change.startOriginal, original),
					Chunk(startRevised, change.startRevised, revised)
				)
			)
		}
		val orgChunk: Chunk<T> = Chunk(
			change.startOriginal, change.endOriginal, original
		)
		val revChunk: Chunk<T> = Chunk(
			change.startRevised, change.endRevised, revised
		)
		when(change.deltaType) {
			DeltaType.delete -> patch.addDelta(DeleteDelta(orgChunk, revChunk))
			DeltaType.insert -> patch.addDelta(InsertDelta(orgChunk, revChunk))
			DeltaType.change -> patch.addDelta(ChangeDelta(orgChunk, revChunk))
			else -> Unit
		}
		startOriginal = change.endOriginal
		startRevised = change.endRevised
	}
	if(includeEquals && startOriginal < original.size) {
		patch.addDelta(
			EqualDelta(
				Chunk(startOriginal, original.size, original),
				Chunk(startRevised, revised.size, revised)
			)
		)
	}
	return patch
}

/**
 * Describes the patch holding all deltas between the original and revised texts.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 * @param <T> The type of the compared elements in the 'lines'.
 */
class Patch<T> private constructor(val deltas: SortedMutableList<Delta<T>>) {
	constructor() : this(BinaryTreeMutableList(deltaComparator()))
	
	constructor(deltas: List<Delta<T>>) : this(BinaryTreeMutableList<Delta<T>>(deltaComparator()).also {
		it += deltas
	})
	
	/**
	 * Apply this patch to the given target
	 *
	 * @return the patched text
	 * @throws PatchFailedException if can't apply patch
	 */
	fun applyTo(target: List<T>): List<T> {
		val result = ArrayList(target)
		val iterator = deltas.listIterator(deltas.size)
		while(iterator.hasPrevious()) {
			val delta = iterator.previous()
			delta.applyTo(result)
		}
		
		return result
	}
	
	
	/**
	 * Restore the text to original. Opposite to applyTo() method.
	 *
	 * @param target the given target
	 * @return the restored text
	 */
	fun restore(target: List<T>): List<T> {
		val result = ArrayList(target)
		val it = deltas.listIterator(deltas.size)
		while(it.hasPrevious()) {
			val delta = it.previous()
			delta.restore(result)
		}
		return result
	}
	
	/**
	 * Add the given delta to this patch
	 *
	 * @param delta the given delta
	 */
	fun addDelta(delta: Delta<T>) {
		deltas += delta
	}
	
	override fun toString() = "Patch{deltas=$deltas}"
}
