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

import com.idex.diff.PatchFailedException

/**
 * Holds the information about the part of text involved in the diff process
 */
data class TextChunk(val position: Int, var replacement: CharSequence, val changePosition: List<Int>? = null) {
	constructor(start: Int, end: Int, lines: CharSequence) : this(start, lines.subSequence(start, end))
	
	val size get() = replacement.length
	
	val lastPosition get() = position + size - 1
	
	
	/**
	 * Verifies that this chunk's saved text matches the corresponding text in the given sequence.
	 *
	 * @param target the sequence to verify against.
	 * @throws PatchFailedException
	 */
	fun verify(target: CharSequence) {
		if(position > target.length || lastPosition > target.length)
			throw PatchFailedException("Incorrect Chunk: the position of chunk > target size")
		
		for(i in 0 until size) {
			if(target[position + i] != replacement[i]) {
				throw PatchFailedException("Incorrect Chunk: the chunk content doesn't match the target")
			}
		}
	}
}
