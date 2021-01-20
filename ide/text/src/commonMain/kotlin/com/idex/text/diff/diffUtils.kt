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

import com.idex.diff.DiffAlgorithmListener
import com.idex.text.diff.algoriithm.myers.MyersTextDiff


/*
 * This package is a modification of :ide:diff module that is optimized for text use case.
 */


/**
 * Computes the difference between the original and revised text
 *
 * @param includeEqualParts Include equal data parts into the patch.
 * @return The patch describing the difference between the original and revised sequences.
 */
fun <T> diffText(
	original: CharSequence, revised: CharSequence,
	algorithm: TextDiffAlgorithm = MyersTextDiff(), progress: DiffAlgorithmListener? = null,
	includeEqualParts: Boolean = false
) = generateTextPatch(
	original,
	revised,
	algorithm.computeDiff(original, revised, progress),
	includeEqualParts
)
