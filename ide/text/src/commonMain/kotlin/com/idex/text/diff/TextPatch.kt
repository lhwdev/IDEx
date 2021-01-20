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

import com.idex.diff.Change
import com.idex.diff.DeltaType
import com.idex.diff.PatchFailedException
import com.idex.diff.deltaComparator
import com.idex.text.MutableText
import io.github.mrasterisco.sortedlist.BinaryTreeMutableList
import io.github.mrasterisco.sortedlist.SortedMutableList


fun generateTextPatch(
	original: CharSequence,
	revised: CharSequence,
	changes: List<Change>,
	includeEquals: Boolean = false
): TextPatch {
	val patch = TextPatch()
	var startOriginal = 0
	var startRevised = 0
	var changeList = changes
	if(includeEquals) {
		changeList = changes.sortedBy { it.startOriginal }
	}
	for(change in changeList) {
		if(includeEquals && startOriginal < change.startOriginal) {
			patch.addDelta(
				EqualTextDelta(
					TextChunk(startOriginal, change.startOriginal, original),
					TextChunk(startRevised, change.startRevised, revised)
				)
			)
		}
		val orgChunk = TextChunk(
			change.startOriginal, change.endOriginal, original
		)
		val revChunk = TextChunk(
			change.startRevised, change.endRevised, revised
		)
		when(change.deltaType) {
			DeltaType.delete -> patch.addDelta(DeleteTextDelta(orgChunk, revChunk))
			DeltaType.insert -> patch.addDelta(InsertTextDelta(orgChunk, revChunk))
			DeltaType.change -> patch.addDelta(ChangeTextDelta(orgChunk, revChunk))
			else -> Unit
		}
		startOriginal = change.endOriginal
		startRevised = change.endRevised
	}
	if(includeEquals && startOriginal < original.length) {
		patch.addDelta(
			EqualTextDelta(
				TextChunk(startOriginal, original.length, original),
				TextChunk(startRevised, revised.length, revised)
			)
		)
	}
	return patch
}


/**
 * Describes the patch holding all deltas between the original and revised texts.
 *
 * @author [Dmitry Naumenko](dm.naumenko@gmail.com)
 */
class TextPatch private constructor(val deltas: SortedMutableList<TextDelta>) {
	constructor() : this(BinaryTreeMutableList(deltaComparator()))
	
	constructor(deltas: List<TextDelta>) : this(BinaryTreeMutableList<TextDelta>(deltaComparator()).also {
		it += deltas
	})
	
	/**
	 * Apply this patch to the given target
	 *
	 * @return the patched text
	 * @throws PatchFailedException if can't apply patch
	 */
	fun applyTo(target: MutableText) {
		val iterator = deltas.listIterator(deltas.size)
		while(iterator.hasPrevious()) {
			val delta = iterator.previous()
			delta.applyTo(target)
		}
	}
	
	
	/**
	 * Restore the text to original. Opposite to applyTo() method.
	 *
	 * @param target the given target
	 * @return the restored text
	 */
	fun restore(target: MutableText) {
		val it = deltas.listIterator(deltas.size)
		while(it.hasPrevious()) {
			val delta = it.previous()
			delta.restore(target)
		}
	}
	
	/**
	 * Add the given delta to this patch
	 *
	 * @param delta the given delta
	 */
	fun addDelta(delta: TextDelta) {
		deltas += delta
	}
	
	override fun toString() = "Patch{deltas=$deltas}"
}
