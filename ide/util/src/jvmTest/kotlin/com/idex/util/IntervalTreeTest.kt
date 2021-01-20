package com.idex.util

import kotlin.test.Test


data class Hi(val text: String, override val start: Int, override val end: Int) : Interval


class IntervalTreeTest {
	val tree = IntervalTree<Hi>()
	
	@Test
	fun main() {
		tree.addAll(
			arrayOf(
				Hi("world", 0, 1),
				Hi("hello", 1, 2),
				Hi("Ho4", 1, 4),
				Hi("hey", 1, 27),
				Hi("o", 2, 3),
				Hi("Ho", 2, 9),
				Hi("Ho2", 3, 5),
				Hi("Ho3", 4, 5),
				Hi("!!", 4, 6),
				Hi("Ho5", 5, 8),
				Hi("wow", 13, 18),
				Hi("jake", 28, 30),
				Hi("paul", 22, 35),
			)
		)
		
		println(tree.dump())
		println(tree.overlappers(Interval(1, 10)).joinToString { it.text })
	}
}
