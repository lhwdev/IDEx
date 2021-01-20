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

import com.idex.diff.algorithm.myers.MyersDiff


/**
 * Computes the difference between the original and revised text.
 */
fun diff(sourceText: String, targetText: String, progress: DiffAlgorithmListener?): Patch<String> =
	diff(sourceText.split("\n"), targetText.split("\n"), progress = progress)

/**
 * Computes the difference between the original and revised list of elements with default diff
 * algorithm
 *
 * @param source The original text. Must not be `null`.
 * @param target The revised text. Must not be `null`.
 *
 * @param equalizer the equalizer object to replace the default compare algorithm
 * (Object.equals). If `null` the default equalizer of the default algorithm is used..
 * @return The patch describing the difference between the original and revised sequences. Never
 * `null`.
 */
fun <T> diff(source: List<T>, target: List<T>, equalizer: (T, T) -> Boolean): Patch<T> =
	diff(source, target, MyersDiff(equalizer))

/**
 * Computes the difference between the original and revised list of elements with default diff
 * algorithm
 *
 * @param includeEqualParts Include equal data parts into the patch.
 * @return The patch describing the difference between the original and revised sequences.
 */
fun <T> diff(
	original: List<T>, revised: List<T>,
	algorithm: DiffAlgorithm<T> = MyersDiff(), progress: DiffAlgorithmListener? = null,
	includeEqualParts: Boolean = false
): Patch<T> {
	return generatePatch(
		original,
		revised,
		algorithm.computeDiff(original, revised, progress),
		includeEqualParts
	)
}

/**
 * Computes the difference between the given texts inline. This one uses the "trick" to make out
 * of texts lists of characters, like DiffRowGenerator does and merges those changes at the end
 * together again.
 *
 * @param original
 * @param revised
 * @return
 */
fun diffInline(original: String, revised: String): Patch<String> {
	val origList = mutableListOf<String>()
	val revList = mutableListOf<String>()
	for(character in original) {
		origList.add(character.toString())
	}
	for(character in revised) {
		revList.add(character.toString())
	}
	val patch = diff(origList, revList)
	for(delta in patch.deltas) {
		delta.source.lines = compressLines(delta.source.lines, "")
		delta.target.lines = compressLines(delta.target.lines, "")
	}
	return patch
}

private fun compressLines(lines: List<String>, separator: String): List<String> {
	return if(lines.isEmpty()) {
		emptyList()
	} else listOf(lines.joinToString(separator))
}
