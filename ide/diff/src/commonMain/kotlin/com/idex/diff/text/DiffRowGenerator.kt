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
package com.idex.diff.text

import com.idex.diff.Delta
import com.idex.diff.DeltaType
import com.idex.diff.Patch
import com.idex.diff.diff
import kotlin.math.max


/**
 * Splitting lines by character to achieve char by char diff checking.
 */
val SPLITTER_BY_CHARACTER = { line: String ->
	val list = ArrayList<String>(line.length)
	for(character in line.toCharArray()) {
		list.add(character.toString())
	}
	list
}

/**
 * Splitting lines by word to achieve word by word diff checking.
 */
val SPLITTER_BY_WORD = { line: String -> splitStringPreserveDelimiter(line, SPLIT_BY_WORD_PATTERN) }
val SPLIT_BY_WORD_PATTERN = Regex("\\s+|[,.\\[\\](){}/\\\\*+\\-#]")

fun splitStringPreserveDelimiter(str: String, SPLIT_PATTERN: Regex): List<String> {
	val list = mutableListOf<String>()
	val matcher = SPLIT_PATTERN.findAll(str)
	var pos = 0
	matcher.forEach {
		if(pos < it.range.first) {
			list.add(str.substring(pos, it.range.start))
		}
		list.add(it.value)
		pos = it.range.endInclusive
	}
	if(pos < str.length) {
		list.add(str.substring(pos))
	}
	return list
}


val WHITESPACE_PATTERN = Regex("\\s+")

private fun adjustWhitespace(raw: String): String {
	return WHITESPACE_PATTERN.replace(raw.trim { it <= ' ' }, " ")
}

private val DEFAULT_EQUALIZER = { a: String, b: String -> a == b }


private val IGNORE_WHITESPACE_EQUALIZER =
	{ original: String, revised: String -> adjustWhitespace(original) == adjustWhitespace(revised) }

private val LINE_NORMALIZER_FOR_HTML = { str: String -> normalize(str) }


/**
 * This class for generating DiffRows for side-by-side view. You can customize the way of
 * generating. For example, show inline diffs on not, ignoring white spaces or/and blank lines and
 * so on. All parameters for generating are optional. If you do not specify them, the class will use
 * the default values.
 *
 * These values are: showInlineDiffs = false; ignoreWhiteSpaces = true; ignoreBlankLines = true; ...
 *
 * For instantiating the DiffRowGenerator you should use the its builder. Like in example  `
 * DiffRowGenerator generator = new DiffRowGenerator.Builder().showInlineDiffs(true).
 * ignoreWhiteSpaces(true).columnWidth(100).build();
` *
 */
class DiffRowGenerator(
	/**
	 * Show inline diffs in generating diff rows or not.
	 */
	private var showInlineDiffs: Boolean = false,
	
	/**
	 * Ignore white spaces in generating diff rows or not.
	 */
	private var ignoreWhiteSpaces: Boolean = false,
	
	/**
	 * Generator for Old-Text-Tags.
	 */
	private var oldTag: (tag: DiffRow.Tag, open: Boolean) -> String =
		{ _, open -> if(open) "<span class=\"editOldInline\">" else "</span>" },
	
	/**
	 * Generator for New-Text-Tags.
	 */
	private var newTag: (tag: DiffRow.Tag, open: Boolean) -> String =
		{ _: DiffRow.Tag?, open: Boolean -> if(open) "<span class=\"editNewInline\">" else "</span>" },
	
	/**
	 * Set the column width of generated lines of original and revised texts.
	 */
	private var columnWidth: Int = 0,
	
	/**
	 * Merge the complete result within the original text. This makes sense for one line display.
	 */
	private var mergeOriginalRevised: Boolean = false,
	
	/**
	 * Give the original old and new text lines to DiffRow without any additional processing
	 * and without any tags to highlight the change.
	 */
	private var reportLinesUnchanged: Boolean = false,
	private var inlineDiffSplitter: (String) -> List<String> = SPLITTER_BY_CHARACTER,
	private var lineNormalizer: (String) -> String = LINE_NORMALIZER_FOR_HTML,
	
	/**
	 * Processor for diffed text parts. Here e.g. white characters could be replaced by something visible.
	 */
	private var processDiffs: ((String) -> String)? = null,
	private var equalizer: (String, String) -> Boolean =
		if(ignoreWhiteSpaces) IGNORE_WHITESPACE_EQUALIZER else DEFAULT_EQUALIZER,
	private var replaceOriginalLinefeedInChangesWithSpaces: Boolean = false
) {
	/**
	 * Get the DiffRows describing the difference between original and revised texts using the given
	 * patch. Useful for displaying side-by-side diff.
	 *
	 * @param original the original text
	 * @param revised the revised text
	 * @return the DiffRows between original and revised texts
	 */
	fun generateDiffRows(original: List<String>, revised: List<String>): List<DiffRow> {
		return generateDiffRows(original, diff(original, revised, equalizer))
	}
	
	/**
	 * Generates the DiffRows describing the difference between original and revised texts using the
	 * given patch. Useful for displaying side-by-side diff.
	 *
	 * @param original the original text
	 * @param patch the given patch
	 * @return the DiffRows between original and revised texts
	 */
	fun generateDiffRows(original: List<String>, patch: Patch<String>): List<DiffRow> {
		val diffRows: MutableList<DiffRow> = ArrayList()
		var endPos = 0
		val deltaList = patch.deltas
		for(delta in deltaList) {
			val orig = delta.source
			val rev = delta.target
			for(line in original.subList(endPos, orig.position)) {
				diffRows.add(buildDiffRow(DiffRow.Tag.equal, line, line))
			}
			
			// Inserted DiffRow
			if(delta.type == DeltaType.insert) {
				endPos = orig.lastPosition + 1
				for(line in rev.lines) {
					diffRows.add(buildDiffRow(DiffRow.Tag.insert, "", line))
				}
				continue
			}
			
			// Deleted DiffRow
			if(delta.type == DeltaType.delete) {
				endPos = orig.lastPosition + 1
				for(line in orig.lines) {
					diffRows.add(buildDiffRow(DiffRow.Tag.delete, line, ""))
				}
				continue
			}
			if(showInlineDiffs) {
				diffRows.addAll(generateInlineDiffs(delta))
			} else {
				for(j in 0 until max(orig.size, rev.size)) {
					diffRows.add(
						buildDiffRow(
							DiffRow.Tag.change,
							if(orig.lines.size > j) orig.lines[j] else "",
							if(rev.lines.size > j) rev.lines[j] else ""
						)
					)
				}
			}
			endPos = orig.lastPosition + 1
		}
		
		// Copy the final matching chunk if any.
		for(line in original.subList(endPos, original.size)) {
			diffRows.add(buildDiffRow(DiffRow.Tag.equal, line, line))
		}
		return diffRows
	}
	
	private fun buildDiffRow(type: DiffRow.Tag, originalLine: String, newLine: String): DiffRow {
		return if(reportLinesUnchanged) {
			DiffRow(type, originalLine, newLine)
		} else {
			var wrapOrg = preprocessLine(originalLine)
			if(DiffRow.Tag.delete == type) {
				if(mergeOriginalRevised || showInlineDiffs) {
					wrapOrg = oldTag(type, true) + wrapOrg + oldTag(type, false)
				}
			}
			var wrapNew = preprocessLine(newLine)
			if(DiffRow.Tag.insert == type) {
				if(mergeOriginalRevised) {
					wrapOrg = newTag(type, true) + wrapNew + newTag(type, false)
				} else if(showInlineDiffs) {
					wrapNew = newTag(type, true) + wrapNew + newTag(type, false)
				}
			}
			DiffRow(type, wrapOrg, wrapNew)
		}
	}
	
	private fun buildDiffRowWithoutNormalizing(type: DiffRow.Tag, originalLine: String, newLine: String): DiffRow {
		return DiffRow(
			type,
			wrapText(originalLine, columnWidth),
			wrapText(newLine, columnWidth)
		)
	}
	
	fun normalizeLines(list: List<String>): List<String> =
		if(reportLinesUnchanged) list else list.map { lineNormalizer(it) }
	
	/**
	 * Add the inline diffs for given delta
	 *
	 * @param delta the given delta
	 */
	private fun generateInlineDiffs(delta: Delta<String>): List<DiffRow> {
		val orig = normalizeLines(delta.source.lines)
		// val rev = normalizeLines(delta.target.lines)
		
		val joinedOrig = orig.joinToString("\n")
		val joinedRev = orig.joinToString("\n")
		val origList = inlineDiffSplitter(joinedOrig).toMutableList()
		val revList = inlineDiffSplitter(joinedRev).toMutableList()
		
		val inlineDeltas: List<Delta<String>> = diff(origList, revList, equalizer).deltas.reversed()
		
		for(inlineDelta in inlineDeltas) {
			val inlineOrig = inlineDelta.source
			val inlineRev = inlineDelta.target
			if(inlineDelta.type == DeltaType.delete) {
				wrapInTag(
					origList,
					inlineOrig.position,
					inlineOrig
						.position
						+ inlineOrig.size,
					DiffRow.Tag.delete,
					oldTag,
					processDiffs,
					replaceOriginalLinefeedInChangesWithSpaces && mergeOriginalRevised
				)
			} else if(inlineDelta.type == DeltaType.insert) {
				if(mergeOriginalRevised) {
					origList.addAll(
						inlineOrig.position,
						revList.subList(
							inlineRev.position,
							inlineRev.position + inlineRev.size
						)
					)
					wrapInTag(
						origList, inlineOrig.position,
						inlineOrig.position + inlineRev.size,
						DiffRow.Tag.insert, newTag, processDiffs, false
					)
				} else {
					wrapInTag(
						revList, inlineRev.position,
						inlineRev.position + inlineRev.size,
						DiffRow.Tag.insert, newTag, processDiffs, false
					)
				}
			} else if(inlineDelta.type == DeltaType.change) {
				if(mergeOriginalRevised) {
					origList.addAll(
						inlineOrig.position + inlineOrig.size,
						revList.subList(
							inlineRev.position,
							inlineRev.position + inlineRev.size
						)
					)
					wrapInTag(
						origList, inlineOrig.position + inlineOrig.size,
						inlineOrig.position + inlineOrig.size + inlineRev.size,
						DiffRow.Tag.change, newTag, processDiffs, false
					)
				} else {
					wrapInTag(
						revList, inlineRev.position,
						inlineRev.position + inlineRev.size,
						DiffRow.Tag.change, newTag, processDiffs, false
					)
				}
				wrapInTag(
					origList,
					inlineOrig.position,
					inlineOrig.position + inlineOrig.size,
					DiffRow.Tag.change,
					oldTag,
					processDiffs,
					replaceOriginalLinefeedInChangesWithSpaces && mergeOriginalRevised
				)
			}
		}
		val origResult = StringBuilder()
		val revResult = StringBuilder()
		for(character in origList) {
			origResult.append(character)
		}
		for(character in revList) {
			revResult.append(character)
		}
		val original = origResult.toString().split("\n")
		val revised = revResult.toString().split("\n")
		val diffRows = mutableListOf<DiffRow>()
		
		for(j in 0 until max(original.size, revised.size)) {
			diffRows.add(
				buildDiffRowWithoutNormalizing(
					DiffRow.Tag.change,
					if(original.size > j) original[j] else "",
					if(revised.size > j) revised[j] else ""
				)
			)
		}
		return diffRows
	}
	
	private fun preprocessLine(line: String): String = if(columnWidth == 0) {
		lineNormalizer(line)
	} else {
		wrapText(lineNormalizer(line), columnWidth)
	}
	
	companion object {
		/**
		 * Wrap the elements in the sequence with the given tag
		 *
		 * @param startPosition the position from which tag should start. The counting start from a
		 * zero.
		 * @param endPosition the position before which tag should should be closed.
		 * @param tagGenerator the tag generator
		 */
		fun wrapInTag(
			sequence: MutableList<String>, startPosition: Int,
			endPosition: Int, tag: DiffRow.Tag, tagGenerator: (DiffRow.Tag, Boolean) -> String,
			processDiffs: ((String) -> String)?, replaceLinefeedWithSpace: Boolean
		) {
			var endPos = endPosition
			while(endPos >= startPosition) {
				
				//search position for end tag
				while(endPos > startPosition) {
					if("\n" != sequence[endPos - 1]) {
						break
					} else if(replaceLinefeedWithSpace) {
						sequence[endPos - 1] = " "
						break
					}
					endPos--
				}
				if(endPos == startPosition) {
					break
				}
				sequence.add(endPos, tagGenerator(tag, false))
				if(processDiffs != null) {
					sequence[endPos - 1] = processDiffs(sequence[endPos - 1])
				}
				endPos--
				
				//search position for end tag
				while(endPos > startPosition) {
					if("\n" == sequence[endPos - 1]) {
						if(replaceLinefeedWithSpace) {
							sequence[endPos - 1] = " "
						} else {
							break
						}
					}
					if(processDiffs != null) {
						sequence[endPos - 1] = processDiffs(sequence[endPos - 1])
					}
					endPos--
				}
				sequence.add(endPos, tagGenerator(tag, true))
				endPos--
			}
		}
	}
}
