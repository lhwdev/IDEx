/*
 *
 */
package com.idex.util


fun Interval(start: Int, end: Int): Interval = object : Interval {
	override val start = start
	override val end = end
}


/**
 * Closed-open, [), interval on the integer number line.
 */
interface Interval : Comparable<Interval> {
	/**
	 * Returns the starting point of this.
	 */
	val start: Int
	
	/**
	 * Returns the ending point of this.
	 */
	val end: Int
	
	
	override fun compareTo(other: Interval) = when {
		start > other.start -> 1
		start < other.start -> -1
		end > other.end -> 1
		end < other.end -> -1
		else -> 0
	}
}

/**
 * Returns the length of this.
 */
val Interval.length: Int
	get() {
		return end - start
	}

fun Interval.overlap(o: Interval): Boolean {
	return end > o.start && o.end > start
}

/**
 * Returns if this interval is adjacent to the specified interval.
 *
 * Two intervals are adjacent if either one ends where the other starts.
 * @param other - the interval to compare this one to
 * @return if this interval is adjacent to the specified interval.
 */
fun Interval.isAdjacent(other: Interval): Boolean {
	return start == other.end || end == other.start
}
