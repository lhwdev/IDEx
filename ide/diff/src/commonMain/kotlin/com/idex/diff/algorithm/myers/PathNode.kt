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
package com.idex.diff.algorithm.myers


/**
 * A node in a diff path.
 *
 * @author [Juanco Anez](mailto:juanco@suigeneris.org)
 */
class PathNode(
	/**
	 * Position in the original sequence.
	 */
	val i: Int,
	
	/**
	 * Position in the revised sequence.
	 */
	val j: Int,
	
	val isSnake: Boolean,
	/**
	 * Is this a bootstrap node?
	 *
	 *
	 * In bootstrap nodes one of the two coordinates is less than zero.
	 *
	 * @return tru if this is a bootstrap node.
	 */
	val isBootstrap: Boolean,
	
	prev: PathNode? = null
) {
	/**
	 * The previous node in the path.
	 */
	val prev: PathNode? = if(isSnake) prev else prev?.previousSnake
	
	/**
	 * Skips sequences of [PathNodes][PathNode] until a snake or bootstrap node is found, or the end of the
	 * path is reached.
	 *
	 * @return The next first [PathNode] or bootstrap node in the path, or `null` if none found.
	 */
	val previousSnake: PathNode?
		get() = when {
			isBootstrap -> null
			!isSnake && prev != null -> prev.previousSnake
			else -> this
		}
	
	override fun toString(): String {
		val buf = StringBuilder("[")
		var node: PathNode? = this
		while(node != null) {
			buf.append("(")
			buf.append(node.i)
			buf.append(",")
			buf.append(node.j)
			buf.append(")")
			node = node.prev
		}
		buf.append("]")
		return buf.toString()
	}
}
