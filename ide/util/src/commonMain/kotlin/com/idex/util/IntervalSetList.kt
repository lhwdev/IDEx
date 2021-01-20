package com.idex.util


/**
 * This set does not store exact duplicates, but will store Intervals that
 * have identical coordinates but differ in some other aspect (e.g., a name
 * field). Each Node of this tree stores its "identical" Intervals in a
 * HashSet; hence the name "IntervalSetList".
 *
 * Two Intervals, i and j, will be stored as distinct Intervals in the same
 * Node if and only if:
 *
 * - i.compareTo(j) == 0
 * - i.equals(j) == false
 */
interface IntervalSetList<T> : IntervalList<T> {
	/**
	 * All elements in this IntervalTree that contains the given Interval.
	 * This method returns empty set if the Interval cannot be found.
	 */
	fun getAll(interval: Interval): Set<T>
}


// this do not work well when size != 1
private fun <T> MutableSet<T>.asRef(): Ref<T>? = if(isEmpty()) null else object : Ref<T> {
	override var value: T
		get() = first()
		set(value) {
			remove(first())
			add(value)
		}
}


interface MutableIntervalSetList<T> : IntervalSetList<T>, MutableIntervalList<T> {
	/**
	 * All elements in this IntervalTree that contains the given Interval.
	 * This method returns empty set if the Interval cannot be found.
	 *
	 * You can only put the element with the same interval with [interval] into the returned set. Otherwise, the
	 * behavior is undefined.
	 */
	override fun getAll(interval: Interval): MutableSet<T>
	
	override fun getNode(interval: Interval): Ref<T>? = getAll(interval).asRef()
	
	/**
	 * Adds the given value into the IntervalTree.
	 *
	 * This method constructs a new Node containing the given value and places
	 * it into the tree. If the same interval already exists within the tree, the value
	 * is inserted in the existing set.
	 *
	 * @param element - the value to place into the tree
	 * @return if the value is inserted, false if the identical element(satisfies compareTo and equals) already exists
	 */
	override fun add(element: T): Boolean
	
	/**
	 * Adds the given value into the IntervalTree.
	 *
	 * This method constructs a new Node containing the given value and places
	 * it into the tree. If the same interval already exists within the tree, the value
	 * is inserted in the existing set.
	 *
	 * @param element - the value to place into the tree
	 * @return the reference to the node, as the same way with [getNode]
	 */
	fun addNodeSet(element: T): MutableSet<T>
	
	override fun addNode(element: T) = addNodeSet(element).asRef()!! // never empty
	
	/**
	 * Deletes **all elements** with the given interval from this list.
	 * If the interval does not exist, this list remains unchanged.
	 * @return whether or not an Interval was removed from this IntervalTree
	 */
	override fun removeAt(interval: Interval): Boolean
}
