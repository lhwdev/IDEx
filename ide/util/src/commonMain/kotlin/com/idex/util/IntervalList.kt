package com.idex.util


/**
 * A set keyed by Interval objects.
 */
interface IntervalList<out T> : Set<T> {
	/**
	 * The element in this list that contains the given Interval.
	 * This method returns null if the Interval cannot be found.
	 */
	operator fun get(interval: Interval): T?
	
	/**
	 * An Iterator which traverses the tree in ascending order.
	 */
	override fun iterator(): Iterator<T>
	
	/**
	 * Whether or not this list contains the given Interval.
	 */
	fun containsInterval(interval: Interval): Boolean
	
	/**
	 * Whether or not any of the Intervals in this list overlap the given
	 * Interval
	 * @param interval - the potentially overlapping Interval
	 */
	fun overlaps(interval: Interval): Boolean
	
	/**
	 * An Iterator over the Intervals in this list that overlap the
	 * given Interval
	 * @param interval - the overlapping Interval
	 */
	fun overlappers(interval: Interval): Iterable<T>
	
	/**
	 * The number of Intervals in this list that overlap the given
	 * Interval
	 * @param interval - the overlapping Interval
	 */
	fun overlapCount(interval: Interval): Int {
		var count = 0
		
		overlappers(interval).forEach { _ ->
			count++
		}
		
		return count
	}
}


interface Ref<T> {
	var value: T
}


interface MutableIntervalList<T> : IntervalList<T>, MutableSet<T> {
	/**
	 * Optimized way to get the location of the element that contains the given Interval.
	 * This method returns null if the Interval cannot be found.
	 */
	fun getNode(interval: Interval): Ref<T>?
	
	/**
	 * Adds the given value into the IntervalTree.
	 *
	 * This method constructs a new Node containing the given value and places
	 * it into the tree. If the value already exists within the tree, the tree
	 * remains unchanged.
	 * @param element - the value to place into the tree
	 * @return the reference to the node, as the same way with [getNode]
	 */
	fun addNode(element: T): Ref<T>
	
	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * @return the element previously at the specified position, or null if couldn't replace the element
	 */
	fun replace(element: T): T?
	
	/**
	 * Replaces the specified exact element with the specified element.
	 * @return if replaced the element
	 */
	fun replace(old: T, new: T): Boolean
	
	/**
	 * Deletes the given interval from this list.
	 * If the interval does not exist, this list remains unchanged.
	 * @return whether or not an Interval was removed from this IntervalTree
	 */
	fun removeAt(interval: Interval): Boolean
}
