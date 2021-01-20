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
package com.idex.text.diff.algoriithm.myers

import com.idex.diff.Change
import com.idex.diff.DeltaType
import com.idex.diff.DiffAlgorithmListener
import com.idex.diff.algorithm.myers.PathNode
import com.idex.text.diff.TextDiffAlgorithm


/**
 * A clean-room implementation of Eugene Myers greedy differencing algorithm.
 */
class MyersTextDiff : TextDiffAlgorithm {
	/**
	 * Return empty diff if get the error while procession the difference.
	 */
	override fun computeDiff(
		source: CharSequence, target: CharSequence, progress: DiffAlgorithmListener?
	): List<Change> {
		progress?.diffStart()
		val path = buildPath(source, target, progress)
		val result = buildRevision(path)
		progress?.diffEnd()
		return result
	}
	
	/**
	 * Computes the minimum diff path that expresses de differences between the original and revised
	 * sequences, according to Gene Myers differencing algorithm.
	 *
	 * @param orig The original sequence.
	 * @param rev The revised sequence.
	 * @return A minimum [Path][PathNode] across the differences graph.
	 */
	private fun buildPath(orig: CharSequence, rev: CharSequence, progress: DiffAlgorithmListener?): PathNode? {
		// these are local constants
		val N = orig.length
		val M = rev.length
		val MAX = N + M + 1
		
		val size = 1 + 2 * MAX
		val middle = size / 2
		val diagonal = arrayOfNulls<PathNode>(size)
		diagonal[middle + 1] = PathNode(0, -1, isSnake = true, isBootstrap = true, prev = null)
		
		for(d in 0 until MAX) {
			progress?.diffStep(d, MAX)
			var k = -d
			while(k <= d) {
				val kMiddle = middle + k
				val kPlus = kMiddle + 1
				val kMinus = kMiddle - 1
				var prev: PathNode?
				var i: Int
				
				if(k == -d || k != d && diagonal[kMinus]!!.i < diagonal[kPlus]!!.i) {
					i = diagonal[kPlus]!!.i
					prev = diagonal[kPlus]
				} else {
					i = diagonal[kMinus]!!.i + 1
					prev = diagonal[kMinus]
				}
				diagonal[kMinus] = null // no longer used
				var j = i - k
				var node = PathNode(i, j, isSnake = false, isBootstrap = false, prev = prev)
				while(i < N && j < M && orig[i] == rev[j]) {
					i++
					j++
				}
				if(i != node.i) {
					node = PathNode(i, j, isSnake = true, isBootstrap = false, prev = node)
				}
				diagonal[kMiddle] = node
				if(i >= N && j >= M) {
					return diagonal[kMiddle]
				}
				k += 2
			}
			diagonal[middle + d - 1] = null
		}
		
		throw IllegalStateException("could not find a diff path")
	}
	
	/**
	 * Constructs a [com.idex.text.diff.TextPatch] from a difference path.
	 *
	 * @param actualPath The path.
	 * @return A [com.idex.text.diff.TextPatch] script corresponding to the path.
	 */
	private fun buildRevision(actualPath: PathNode?): List<Change> {
		var path = actualPath
		val changes = mutableListOf<Change>()
		if(path!!.isSnake) {
			path = path.prev
		}
		while(path?.prev?.let { it.j >= 0 } == true) {
			val prev = path.prev!!
			
			check(!path.isSnake) { "bad diff path: found snake when looking for diff" }
			val i = path.i
			val j = path.j
			val iAnchor = prev.i
			val jAnchor = prev.j
			when {
				iAnchor == i && jAnchor != j -> changes.add(Change(DeltaType.insert, iAnchor, i, jAnchor, j))
				iAnchor != i && jAnchor == j -> changes.add(Change(DeltaType.delete, iAnchor, i, jAnchor, j))
				else -> changes.add(Change(DeltaType.change, iAnchor, i, jAnchor, j))
			}
			
			path = prev
			if(path.isSnake) path = path.prev
		}
		return changes
	}
}
