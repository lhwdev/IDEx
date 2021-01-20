package com.idex.util


/**
 * A balanced binary-search tree keyed by Interval objects.
 *
 * This tree does not store exact duplicates, but will store Intervals that
 * have identical coordinates but differ in some other aspect (e.g., a name
 * field). Each Node of this tree stores its "identical" Intervals in a
 * HashSet; hence the name "IntervalSetTree".
 *
 *
 * Two Intervals, i and j, will be stored as distinct Intervals in the same
 * Node if and only if:
 *
 *  * i.compareTo(j) == 0
 *  * i.equals(j) == false
 *
 * The underlying data-structure is a red-black tree largely implemented from
 * CLRS (Introduction to Algorithms, 2nd edition) with the interval-tree
 * extensions mentioned in section 14.3
</I> */
class IntervalSetTree<T : Interval> private constructor(rootItem: T?, size: Int) : MutableIntervalSetList<T>,
	AbstractMutableCollection<T>() {
	private val nil = Node() // The sentinel Node to represent the absence of a node.
	private var root = if(rootItem == null) Node() else Node(rootItem) // The root Node.
	
	/**
	 * The number of intervals stored in this IntervalSetTree.
	 */
	override var size: Int = size // Size of the tree. Updated by insert() and delete()
		private set
	
	/**
	 * Constructs an empty IntervalSetTree.
	 */
	constructor() : this(null, 0)
	
	/**
	 * Constructs an IntervalSetTree with a single node containing the given
	 * Interval.
	 * @param rootElement - the Interval to add to this IntervalSetTree
	 */
	constructor(rootElement: T) : this(rootElement, 1) {
		root.blacken()
	}
	
	
	///////////////////////////////////
	// Tree -- General query methods //
	///////////////////////////////////
	/**
	 * Whether this IntervalSetTree is empty or not.
	 */
	override fun isEmpty() = root.isNil
	
	/**
	 * The Node in this IntervalSetTree that has the same start and end
	 * coordinates of the given Interval.
	 *
	 *
	 * It is not necessarily the case that the Node contains the Interval,
	 * just that the boundary coordinates are the same. This method returns
	 * the nil Node if no valid Node can be found.
	 * @param interval - the Interval to search for
	 */
	private fun search(interval: Interval): Node {
		return root.search(interval)
	}
	
	override fun get(interval: Interval): T? = search(interval).intervals.firstOrNull()
	
	override fun getAll(interval: Interval): MutableSet<T> = search(interval).intervals
	
	/**
	 * Whether or not this IntervalSetTree contains the given Interval.
	 * @param element - the Interval to search for
	 */
	override operator fun contains(element: T): Boolean {
		return search(element).intervals.contains(element)
	}
	
	override fun containsInterval(interval: Interval): Boolean {
		return search(interval).intervals.contains(interval)
	}
	
	/**
	 * The Intervals in the minimum Node of this IntervalSetTree.
	 * @return an Iterator, possibly empty, over all minimum Intervals
	 */
	val minimum: Iterator<T>
		get() {
			val n = root.minimumNode
			return if(n.isNil) emptyList<T>().iterator() else n.intervals.iterator()
		}
	
	/**
	 * The Intervals in the maximum Node of this IntervalSetTree.
	 * @return an Iterator, possibly empty, over all maximum Intervals
	 */
	val maximum: Iterator<T>
		get() {
			val n = root.maximumNode
			return if(n.isNil) emptyList<T>().iterator() else n.intervals.iterator()
		}
	
	/**
	 * The Intervals in the following Node of this IntervalSetTree.
	 *
	 *
	 * The "following" Node is the next Node in this tree relative to the Node
	 * corresponding to the passed Interval
	 * @param interval - the Interval to search for
	 * @return an Iterator over the Intervals in the next Node, possibly empty
	 * if this Node is the maximum Node.
	 */
	fun successors(interval: Interval): Iterator<T> {
		var n = search(interval)
		if(n.isNil) {
			return emptyList<T>().iterator()
		}
		n = n.successor
		return if(n.isNil) emptyList<T>().iterator() else n.intervals.iterator()
	}
	
	/**
	 * The Intervals in the preceding Node of this IntervalSetTree.
	 *
	 *
	 * The "preceding" Node is the previous Node in this tree relative to the
	 * Node corresponding to the passed Interval
	 * @param interval - the Interval to search for
	 * @return an Iterator over the Intervals in the next Node, possibly empty
	 * if this Node is the minimum Node.
	 */
	fun predecessors(interval: Interval): Iterator<T> {
		var n = search(interval)
		if(n.isNil) {
			return emptyList<T>().iterator()
		}
		n = n.predecessor
		return if(n.isNil) {
			emptyList<T>().iterator()
		} else n.intervals.iterator()
	}
	
	/**
	 * An Iterator which traverses the tree in ascending order.
	 */
	override fun iterator(): MutableIterator<T> {
		return TreeIterator(root)
	}
	
	/**
	 * An Iterator over the Intervals in this IntervalSetTree that overlap the
	 * given Interval
	 * @param interval - the overlapping Interval
	 */
	override fun overlappers(interval: Interval): Iterable<T> {
		return root.overlappers(interval)
	}
	
	/**
	 * Whether or not any of the Intervals in this IntervalSetTree overlap the
	 * given Interval
	 * @param interval - the potentially overlapping Interval
	 */
	override fun overlaps(interval: Interval): Boolean {
		return !root.anyOverlappingNode(interval).isNil
	}
	
	/**
	 * The number of Intervals in this IntervalSetTree that overlap the given
	 * Interval
	 * @param interval - the overlapping Interval
	 */
	override fun overlapCount(interval: Interval): Int {
		return root.numOverlappingIntervals(interval)
	}
	
	/**
	 * The minimum Intervals in this IntervalSetTree that overlap the given
	 * Interval
	 *
	 *
	 * There may be more than one minimum Interval if two Intervals have the same
	 * start and end coordinates
	 * @param interval - the overlapping Interval
	 * @return a Iterator over the minimum Intervals that overlap the given
	 * Interval; an empty Iterator if no such Interval exists.
	 */
	fun minimumOverlappers(interval: Interval): Iterator<T> {
		val n = root.minimumOverlappingNode(interval)
		return if(n.isNil) emptyList<T>().iterator() else n.intervals.iterator()
	}
	///////////////////////////////
	// Tree -- Insertion methods //
	///////////////////////////////
	
	
	private fun addInternal(element: T): Pair<Boolean, Node> {
		var y = nil
		var x = root
		
		// Traverse the tree down to a leaf
		while(!x.isNil) {
			y = x
			
			// Update maxEnd on the way down.
			x.maxEnd = x.maxEnd.coerceAtLeast(element.end)
			
			// If the Node for this Interval already exists, add the Interval
			// to its Set and increment size if successful.
			val cmp = element.compareTo(x)
			if(cmp == 0) {
				return if(x.intervals.add(element)) {
					size++
					true to x
				} else {
					false to x
				}
			}
			x = if(cmp == -1) x.left else x.right
		}
		
		// Didn't find the correct Node on the way down, so make a new Node
		// containing the Interval.
		val z = Node(element)
		z.parent = y
		if(y.isNil) {                // Three cases:
			root = z
			root.blacken() // 1) New node is root
		} else {
			val cmp = z.compareTo(y)
			if(cmp == -1) {
				y.left = z // 2) New node is left-child leaf
			} else {
				assert(cmp == 1)
				y.right = z // 3) New node is right-child leaf
			}
			z.left = nil
			z.right = nil
			z.redden()
			z.insertFixup()
		}
		size++
		return true to z
	}
	
	override fun add(element: T) = addInternal(element).first
	
	override fun addNodeSet(element: T) = addInternal(element).second.intervals
	
	override fun replace(element: T): T? {
		val previous = search(element)
		return if(previous.isNil) null else { // here all other properties are identical(so caught by search)
			val last = previous.intervals
			val lastValue = last.first()
			last.clear()
			last.add(element)
			lastValue
		}
	}
	
	override fun replace(old: T, new: T) = if(old.compareTo(new) == 0) {
		// only replace value
		val previous = search(old)
		val last = previous.intervals
		if(old in last) {
			last.remove(old)
			last.add(new)
			true
		} else false
	} else {
		remove(old) and add(new)
	}
	
	
	//////////////////////////////
	// Tree -- Deletion methods //
	//////////////////////////////
	/**
	 * Deletes all elements with the given Interval from this IntervalSetTree.
	 *
	 * If the Interval does not exist, this IntervalTree remains unchanged.
	 * @param interval - the Interval to delete from the tree
	 * @return whether or not an Interval was removed from this tree
	 */
	override fun removeAt(interval: Interval): Boolean {
		return search(interval).delete() // delete() handles size change
	}
	
	override fun remove(element: T): Boolean {
		val n = search(element)
		val removed = n.intervals.remove(element)
		if(removed) {
			size--
		}
		if(n.intervals.isEmpty()) {
			n.delete() // Node#delete does nothing if n.isNil
		}
		return removed
	}
	
	/**
	 * Deletes the smallest Intervals from this IntervalSetTree.
	 *
	 *
	 * If there is no smallest Interval (that is, if the tree is empty), this
	 * IntervalSetTree remains unchanged. If multiple Intervals share the same
	 * start and end value, all are removed.
	 * @return whether or not any Intervals were removed from this tree
	 */
	fun removeMin(): Boolean {            // Node#delete does nothing and
		return root.minimumNode.delete() // returns false if t.isNil
	}
	
	/**
	 * Deletes the greatest Intervals from this IntervalSetTree.
	 *
	 *
	 * If there is no greatest Interval (that is, if the tree is empty), this
	 * IntervalSetTree remains unchanged. If multiple Intervals share the same
	 * start and end value, all are removed.
	 * @return whether or not any Intervals were removed from this tree
	 */
	fun removeMax(): Boolean {            // Node#delete does nothing and
		return root.maximumNode.delete() // returns false if t.isNil
	}
	
	/**
	 * Deletes all Intervals that overlap the given Interval from this
	 * IntervalSetTree.
	 *
	 *
	 * If there are no overlapping Intervals, this IntervalSetTree remains
	 * unchanged.
	 * @param t - the overlapping Interval
	 * @return whether or not any Interval were removed from this tree
	 */
	fun removeOverlappers(t: T): Boolean {
		// TODO 
		// Replacing the line
		//    s.forEach(n -> delete(n.interval()))
		// with
		//    s.forEach(n -> n.delete())
		// causes a NullPointerException in resetMaxEnd(). Why?!
		//
		// As it stands, every deletion operation causes the tree
		// to be searched. Fix this, please.
		val s = HashSet<Node>()
		val iter = OverlappingNodeIterator(root, t)
		iter.forEach { s.add(it) }
		return s.map { removeAt(it.intervals.iterator().next()) }.fold(false) { a, b -> a || b }
	}
	
	/**
	 * A representation of a node in an interval tree.
	 */
	private inner class Node : Interval, Iterable<T> {
		/* Most of the "guts" of the interval tree are actually methods called
         * by nodes. For example, IntervalTree#delete(val) searches up the Node
         * containing val; then that Node deletes itself with Node#delete().
         */
		var intervals: MutableSet<T>
		override var start = 0
			private set
		override var end = 0
			private set
		var parent: Node
		var left: Node
		var right: Node
		var isBlack = false
		var maxEnd = 0
		
		/**
		 * Constructs a Node with no data.
		 *
		 *
		 * This Node contains no Intervals, is black, and has all pointers
		 * pointing at itself. This is intended to be used as the sentinel
		 * node in the tree ("nil" in CLRS).
		 */
		constructor() {
			intervals = mutableSetOf()
			parent = this
			left = this
			right = this
			blacken()
		}
		
		/**
		 * Constructs a Node containing the given Interval.
		 * @param interval - the Interval to be contained within this Node
		 */
		constructor(interval: T) {
			intervals = HashSet()
			intervals.add(interval)
			parent = nil
			left = nil
			right = nil
			start = interval.start
			end = interval.end
			maxEnd = end
			redden()
		}
		
		override fun iterator(): MutableIterator<T> {
			return intervals.iterator()
		}
		
		///////////////////////////////////
		// Node -- General query methods //
		///////////////////////////////////
		/**
		 * Searches the subtree rooted at this Node for the Node with the
		 * coordinates represented by this Interval.
		 *
		 *
		 * The Interval does not need to be contained within the Node, just
		 * have the same coordinates, to be returned.
		 * @param interval - the Interval to search for
		 * @return the Node with the corresponding coordinates, if it exists;
		 * otherwise,
		 * the sentinel Node
		 */
		fun search(interval: Interval): Node {
			var n = this
			while(!n.isNil && interval.compareTo(n) != 0) {
				n = if(interval.compareTo(n) == -1) n.left else n.right
			}
			return n
		}
		
		/**
		 * Searches the subtree rooted at this Node for its minimum Intervals.
		 * @return the Node with the minimum Intervals, if it exists; otherwise,
		 * the sentinel Node
		 */
		val minimumNode: Node
			get() {
				var n = this
				while(!n.left.isNil) {
					n = n.left
				}
				return n
			}
		
		/**
		 * Searches the subtree rooted at this Node for its maximum Intervals.
		 * @return the Node with the maximum Intervals, if it exists; otherwise,
		 * the sentinel Node
		 */
		val maximumNode: Node
			get() {
				var n = this
				while(!n.right.isNil) {
					n = n.right
				}
				return n
			}
		
		/**
		 * The successor of this Node.
		 * @return the Node following this Node, if it exists; otherwise the
		 * sentinel Node
		 */
		val successor: Node
			get() {
				if(!right.isNil) {
					return right.minimumNode
				}
				var x = this
				var y = parent
				while(!y.isNil && x === y.right) {
					x = y
					y = y.parent
				}
				return y
			}
		
		/**
		 * The predecessor of this Node.
		 * @return the Node preceding this Node, if it exists; otherwise the
		 * sentinel Node
		 */
		val predecessor: Node
			get() {
				if(!left.isNil) {
					return left.maximumNode
				}
				var x = this
				var y = parent
				while(!y.isNil && x === y.left) {
					x = y
					y = y.parent
				}
				return y
			}
		///////////////////////////////////////
		// Node -- Overlapping query methods //
		///////////////////////////////////////
		/**
		 * Returns a Node from this Node's subtree that overlaps the given
		 * Interval.
		 *
		 *
		 * The only guarantee of this method is that the returned Node overlaps
		 * the Interval t. This method is meant to be a quick helper method to
		 * determine if any overlap exists between an Interval and any of an
		 * IntervalSetTree's Intervals. The returned Node will be the first
		 * overlapping one found.
		 * @param interval - the given Interval
		 * @return an overlapping Node from this Node's subtree, if one exists;
		 * otherwise the sentinel Node
		 */
		fun anyOverlappingNode(interval: Interval): Node {
			var x = this
			while(!x.isNil && !interval.overlap(x)) {
				x = if(!x.left.isNil && x.left.maxEnd > interval.start) x.left else x.right
			}
			return x
		}
		
		/**
		 * Returns the minimum Node from this Node's subtree that overlaps the
		 * given Interval.
		 * @param interval - the given Interval
		 * @return the minimum Node from this Node's subtree that overlaps the
		 * Interval, if one exists; otherwise, the sentinel Node
		 */
		fun minimumOverlappingNode(interval: Interval): Node {
			var result = nil
			var n = this
			if(!n.isNil && n.maxEnd > interval.start) {
				while(true) {
					if(n.overlap(interval)) {
						
						// This node overlaps. There may be a lesser overlapper
						// down the left subtree. No need to consider the right
						// as all overlappers there will be greater.
						result = n
						n = n.left
						if(n.isNil || n.maxEnd <= interval.start) {
							// Either no left subtree, or nodes can't overlap.
							break
						}
					} else {
						
						// This node doesn't overlap.
						// Check the left subtree if an overlapper may be there
						val left = n.left
						if(!left.isNil && left.maxEnd > interval.start) {
							n = left
						} else {
							
							// Left subtree cannot contain an overlapper. Check the
							// right sub-tree.
							if(n.start > interval.end) {
								// Nothing in the right subtree can overlap
								break
							}
							n = n.right
							if(n.isNil || n.maxEnd <= interval.start) {
								// No right subtree, or nodes can't overlap.
								break
							}
						}
					}
				}
			}
			return result
		}
		
		/**
		 * An Iterator over all values in this Node's subtree that overlap the
		 * given Interval.
		 * @param interval - the overlapping Interval
		 */
		fun overlappers(interval: Interval) = iterable { OverlapperIterator(this, interval) }
		
		/**
		 * The next Node (relative to this Node) which overlaps the given
		 * Interval
		 * @param interval - the overlapping Interval
		 * @return the next Node that overlaps the Interval t, if one exists;
		 * otherwise, the sentinel Node
		 */
		fun nextOverlappingNode(interval: Interval): Node {
			var x = this
			var rtrn = nil
			
			// First, check the right subtree for its minimum overlapper.
			if(!right.isNil) {
				rtrn = x.right.minimumOverlappingNode(interval)
			}
			
			// If we didn't find it in the right subtree, walk up the tree and
			// check the parents of left-children as well as their right subtrees.
			while(!x.parent.isNil && rtrn.isNil) {
				if(x.isLeftChild) {
					rtrn = if(x.parent.overlap(interval)) x.parent else x.parent.right.minimumOverlappingNode(interval)
				}
				x = x.parent
			}
			return rtrn
		}
		
		/**
		 * The number of Intervals in this Node's subtree that overlap the given
		 * Interval.
		 *
		 * This number includes this Node's Intervals if they overlap t. This
		 * method iterates over all overlapping Nodes, so if you ultimately
		 * need to inspect the Intervals, it will be more efficient to simply
		 * create the Iterator yourself.
		 * @param interval - the overlapping Interval
		 * @return the number of overlapping Nodes
		 */
		fun numOverlappingIntervals(interval: Interval): Int {
			var count = 0
			val iter = OverlappingNodeIterator(this, interval)
			while(iter.hasNext()) {
				count += iter.next().intervals.size
			}
			return count
		}
		//////////////////////////////
		// Node -- Deletion methods //
		//////////////////////////////
		//TODO: Should we rewire the Nodes rather than copying data?
		//      I suspect this method causes some code which seems like it
		//      should work to fail.
		/**
		 * Deletes this Node from its tree.
		 *
		 *
		 * More specifically, removes the data held within this Node from the
		 * tree. Depending on the structure of the tree at this Node, this
		 * particular Node instance may not be removed; rather, a different
		 * Node may be deleted and that Node's contents copied into this one,
		 * overwriting the previous contents.
		 */
		fun delete(): Boolean {
			if(isNil) {  // Can't delete the sentinel node.
				return false
			}
			size -= intervals.size
			var y = this
			if(hasTwoChildren) { // If the node to remove has two children,
				y = successor // copy the successor's data into it and
				copyData(y) // remove the successor. The successor is
				maxEndFixup() // guaranteed to both exist and have at most
			} // one child, so we've converted the two-
			// child case to a one- or no-child case.
			val x = if(y.left.isNil) y.right else y.left
			x.parent = y.parent
			when {
				y.isRoot -> {
					root = x
				}
				y.isLeftChild -> {
					y.parent.left = x
					y.maxEndFixup()
				}
				else -> {
					y.parent.right = x
					y.maxEndFixup()
				}
			}
			if(y.isBlack) {
				x.deleteFixup()
			}
			return true
		}
		////////////////////////////////////////////////
		// Node -- Tree-invariant maintenance methods //
		////////////////////////////////////////////////
		/**
		 * Whether or not this Node is the root of its tree.
		 */
		val isRoot: Boolean
			get() = !isNil && parent.isNil
		
		/**
		 * Whether or not this Node is the sentinel node.
		 */
		val isNil: Boolean
			get() = this === nil
		
		/**
		 * Whether or not this Node is the left child of its parent.
		 */
		val isLeftChild: Boolean
			get() = this === parent.left
		
		/**
		 * Whether or not this Node is the right child of its parent.
		 */
		val isRightChild: Boolean
			get() = this === parent.right
		
		/**
		 * Whether or not this Node has no children, i.e., is a leaf.
		 */
		val hasNoChildren: Boolean
			get() {
				return left.isNil && right.isNil
			}
		
		/**
		 * Whether or not this Node has two children, i.e., neither of its
		 * children are leaves.
		 */
		val hasTwoChildren: Boolean
			get() {
				return !left.isNil && !right.isNil
			}
		
		/**
		 * Sets this Node's color to black.
		 */
		fun blacken() {
			isBlack = true
		}
		
		/**
		 * Sets this Node's color to red.
		 */
		fun redden() {
			isBlack = false
		}
		
		/**
		 * Whether or not this Node's color is red.
		 */
		val isRed: Boolean
			get() = !isBlack
		
		/**
		 * A pointer to the grandparent of this Node.
		 */
		private val grandparent: Node
			get() {
				return parent.parent
			}
		
		/**
		 * Sets the maxEnd value for this Node.
		 *
		 *
		 * The maxEnd value should be the highest of:
		 *
		 *  * the end value of this node's data
		 *  * the maxEnd value of this node's left child, if not null
		 *  * the maxEnd value of this node's right child, if not null
		 *
		 *
		 * This method will be correct only if the left and right children have
		 * correct maxEnd values.
		 */
		private fun resetMaxEnd() {
			var value = end
			if(!left.isNil) {
				value = value.coerceAtLeast(left.maxEnd)
			}
			if(!right.isNil) {
				value = value.coerceAtLeast(right.maxEnd)
			}
			maxEnd = value
		}
		
		/**
		 * Sets the maxEnd value for this Node, and all Nodes up to the root of
		 * the tree.
		 */
		private fun maxEndFixup() {
			var n = this
			n.resetMaxEnd()
			while(!n.parent.isNil) {
				n = n.parent
				n.resetMaxEnd()
			}
		}
		
		/**
		 * Performs a left-rotation on this Node.
		 * @see - Cormen et al. "Introduction to Algorithms", 2nd ed, pp. 277-279.
		 */
		private fun leftRotate() {
			val y = right
			right = y.left
			if(!y.left.isNil) {
				y.left.parent = this
			}
			y.parent = parent
			when {
				parent.isNil -> root = y
				isLeftChild -> parent.left = y
				else -> parent.right = y
			}
			y.left = this
			parent = y
			resetMaxEnd()
			y.resetMaxEnd()
		}
		
		/**
		 * Performs a right-rotation on this Node.
		 * @see - Cormen et al. "Introduction to Algorithms", 2nd ed, pp. 277-279.
		 */
		private fun rightRotate() {
			val y = left
			left = y.right
			if(!y.right.isNil) {
				y.right.parent = this
			}
			y.parent = parent
			when {
				parent.isNil -> root = y
				isLeftChild -> parent.left = y
				else -> parent.right = y
			}
			y.right = this
			parent = y
			resetMaxEnd()
			y.resetMaxEnd()
		}
		
		/**
		 * Copies the data from a Node into this Node.
		 * @param o - the other Node containing the data to be copied
		 */
		private fun copyData(o: Node) {
			intervals = o.intervals
			start = o.start
			end = o.end
		}
		
		/**
		 * Returns a String representation of this Node.
		 *
		 *
		 * This representation will display the start and end coordinates, the
		 * color, and the max-end value of this Node. Useful for quick
		 * debugging outside of the debugger.
		 */
		override fun toString(): String {
			return if(isNil) {
				"nil"
			} else {
				val color = if(isBlack) "black" else "red"
				"""
					start = $start
					end = $end
					maxEnd = $maxEnd
					color = $color
				""".trimIndent()
			}
		}
		
		/**
		 * Ensures that red-black constraints and interval-tree constraints are
		 * maintained after an insertion.
		 */
		fun insertFixup() {
			var z = this
			while(z.parent.isRed) {
				if(z.parent.isLeftChild) {
					val y = z.parent.parent.right
					if(y.isRed) {
						z.parent.blacken()
						y.blacken()
						z.grandparent.redden()
						z = z.grandparent
					} else {
						if(z.isRightChild) {
							z = z.parent
							z.leftRotate()
						}
						z.parent.blacken()
						z.grandparent.redden()
						z.grandparent.rightRotate()
					}
				} else {
					val y = z.grandparent.left
					if(y.isRed) {
						z.parent.blacken()
						y.blacken()
						z.grandparent.redden()
						z = z.grandparent
					} else {
						if(z.isLeftChild) {
							z = z.parent
							z.rightRotate()
						}
						z.parent.blacken()
						z.grandparent.redden()
						z.grandparent.leftRotate()
					}
				}
			}
			root.blacken()
		}
		
		/**
		 * Ensures that red-black constraints and interval-tree constraints are
		 * maintained after deletion.
		 */
		private fun deleteFixup() {
			var x = this
			while(!x.isRoot && x.isBlack) {
				if(x.isLeftChild) {
					var w = x.parent.right
					if(w.isRed) {
						w.blacken()
						x.parent.redden()
						x.parent.leftRotate()
						w = x.parent.right
					}
					if(w.left.isBlack && w.right.isBlack) {
						w.redden()
						x = x.parent
					} else {
						if(w.right.isBlack) {
							w.left.blacken()
							w.redden()
							w.rightRotate()
							w = x.parent.right
						}
						w.isBlack = x.parent.isBlack
						x.parent.blacken()
						w.right.blacken()
						x.parent.leftRotate()
						x = root
					}
				} else {
					var w = x.parent.left
					if(w.isRed) {
						w.blacken()
						x.parent.redden()
						x.parent.rightRotate()
						w = x.parent.left
					}
					if(w.left.isBlack && w.right.isBlack) {
						w.redden()
						x = x.parent
					} else {
						if(w.left.isBlack) {
							w.right.blacken()
							w.redden()
							w.leftRotate()
							w = x.parent.left
						}
						w.isBlack = x.parent.isBlack
						x.parent.blacken()
						w.left.blacken()
						x.parent.rightRotate()
						x = root
					}
				}
			}
			x.blacken()
		}
		///////////////////////////////
		// Node -- Debugging methods //
		///////////////////////////////
		/**
		 * Whether or not the subtree rooted at this Node is a valid
		 * binary-search tree.
		 * @param min - a lower-bound Node
		 * @param max - an upper-bound Node
		 */
		fun isBST(min: Node?, max: Node?): Boolean {
			if(isNil) {
				return true // Leaves are a valid BST, trivially.
			}
			if(min != null && compareTo(min) <= 0) {
				return false // This Node must be greater than min
			}
			return if(max != null && compareTo(max) >= 0) {
				false // and less than max.
			} else left.isBST(min, this) && right.isBST(this, max)
			
			// Children recursively call method with updated min/max.
		}
		
		/**
		 * Whether or not the subtree rooted at this Node is balanced.
		 *
		 *
		 * Balance determination is done by calculating the black-height.
		 * @param black - the expected black-height of this subtree
		 */
		fun isBalanced(black: Int): Boolean {
			var b = black
			if(isNil) {
				return b == 0 // Leaves have a black-height of zero,
			} // even though they are black.
			if(isBlack) {
				b--
			}
			return left.isBalanced(b) && right.isBalanced(b)
		}
		
		/**
		 * Whether or not the subtree rooted at this Node has a valid
		 * red-coloring.
		 *
		 *
		 * A red-black tree has a valid red-coloring if every red node has two
		 * black children.
		 */
		val hasValidRedColoring: Boolean
			get() = when {
				isNil -> {
					true
				}
				isBlack -> {
					left.hasValidRedColoring &&
						right.hasValidRedColoring
				}
				else -> {
					left.isBlack && right.isBlack &&
						left.hasValidRedColoring &&
						right.hasValidRedColoring
				}
			}
		
		/**
		 * Whether or not the subtree rooted at this Node has consistent maxEnd
		 * values.
		 *
		 *
		 * The maxEnd value of an interval-tree Node is equal to the maximum of
		 * the end-values of all intervals contained in the Node's subtree.
		 */
		val hasConsistentMaxEnds: Boolean
			get() = when {
				isNil -> {                                    // 1. sentinel node
					true
				}
				hasNoChildren -> {                            // 2. leaf node
					maxEnd == end
				}
				else -> {
					val consistent = maxEnd > end
					when {
						hasTwoChildren -> {                       // 3. two children
							consistent && maxEnd >= left.maxEnd && maxEnd >= right.maxEnd &&
								left.hasConsistentMaxEnds &&
								right.hasConsistentMaxEnds
						}
						left.isNil -> {                    // 4. one child -- right
							consistent && maxEnd >= right.maxEnd &&
								right.hasConsistentMaxEnds
						}
						else -> {
							consistent && // 5. one child -- left
								maxEnd >= left.maxEnd &&
								left.hasConsistentMaxEnds
						}
					}
				}
			}
	}
	///////////////////////
	// Tree -- Iterators //
	///////////////////////
	/**
	 * An Iterator which walks along this IntervalSetTree's Nodes in ascending order.
	 */
	private inner class TreeNodeIterator private constructor(root: Node) : Iterator<Node> {
		private var next: Node
		override fun hasNext(): Boolean {
			return !next.isNil
		}
		
		override fun next(): Node {
			if(!hasNext()) {
				throw NoSuchElementException("Interval tree has no more elements.")
			}
			val rtrn = next
			next = rtrn.successor
			return rtrn
		}
		
		init {
			next = root.minimumNode
		}
	}
	
	/**
	 * An Iterator which walks along this IntervalSetTree's Intervals in
	 * ascending order.
	 */
	private inner class TreeIterator(root: Node) : MutableIterator<T> {
		private var currentNode = root.minimumNode
		private var nextNode = currentNode.successor
		private var iter = currentNode.iterator()
		override fun hasNext(): Boolean {
			return iter.hasNext() || !nextNode.isNil
		}
		
		override fun next(): T {
			return if(iter.hasNext()) {
				iter.next()
			} else {
				currentNode = nextNode
				nextNode = currentNode.successor
				iter = currentNode.iterator()
				iter.next()
			}
		}
		
		override fun remove() {
			iter.remove()
		}
	}
	
	/**
	 * An Iterator which walks along this IntervalSetTree's Nodes that overlap
	 * a given Interval in ascending order.
	 */
	private inner class OverlappingNodeIterator(root: Node, private val interval: Interval) : Iterator<Node> {
		private var next: Node
		override fun hasNext(): Boolean {
			return !next.isNil
		}
		
		override fun next(): Node {
			if(!hasNext()) {
				throw NoSuchElementException("Interval tree has no more overlapping elements.")
			}
			val rtrn: Node = next
			next = rtrn.nextOverlappingNode(interval)
			return rtrn
		}
		
		init {
			next = root.minimumOverlappingNode(interval)
		}
	}
	
	/**
	 * An Iterator which walks along this IntervalSetTree's Intervals that overlap
	 * a given Interval in ascending order.
	 */
	private inner class OverlapperIterator(root: Node, private val interval: Interval) : Iterator<T> {
		private var iter: Iterator<T>
		private var currentNode: Node
		private var nextNode: Node
		override fun hasNext(): Boolean {
			return iter.hasNext() || !nextNode.isNil
		}
		
		override fun next(): T {
			return if(iter.hasNext()) {
				iter.next()
			} else {
				currentNode = nextNode
				nextNode = currentNode.nextOverlappingNode(interval)
				iter = currentNode.iterator()
				iter.next()
			}
		}
		
		init {
			currentNode = root.minimumOverlappingNode(interval)
			nextNode = currentNode.nextOverlappingNode(interval)
			iter = currentNode.iterator()
		}
	}
	///////////////////////////////
	// Tree -- Debugging methods //
	///////////////////////////////
	/**
	 * Whether or not this IntervalSetTree is a valid binary-search tree.
	 *
	 *
	 * This method will return false if any Node is less than its left child
	 * or greater than its right child.
	 *
	 *
	 * This method is used for debugging only, and its access is changed in
	 * testing.
	 */
	private val isBST: Boolean
		get() = root.isBST(null, null)
	
	/**
	 * Whether or not this IntervalSetTree is balanced.
	 *
	 *
	 * This method will return false if all of the branches (from root to leaf)
	 * do not contain the same number of black nodes. (Specifically, the
	 * black-number of each branch is compared against the black-number of the
	 * left-most branch.)
	 *
	 *
	 * This method is used for debugging only, and its access is changed in
	 * testing.
	 */
	private val isBalanced: Boolean
		get() {
			var black = 0
			var x = root
			while(!x.isNil) {
				if(x.isBlack) {
					black++
				}
				x = x.left
			}
			return root.isBalanced(black)
		}
	
	/**
	 * Whether or not this IntervalSetTree has a valid red coloring.
	 *
	 *
	 * This method will return false if all of the branches (from root to leaf)
	 * do not contain the same number of black nodes. (Specifically, the
	 * black-number of each branch is compared against the black-number of the
	 * left-most branch.)
	 *
	 *
	 * This method is used for debugging only, and its access is changed in
	 * testing.
	 */
	private val hasValidRedColoring: Boolean
		get() {
			return root.hasValidRedColoring
		}
	
	/**
	 * Whether or not this IntervalSetTree has consistent maxEnd values.
	 *
	 *
	 * This method will only return true if each Node has a maxEnd value equal
	 * to the highest interval end value of all the intervals in its subtree.
	 *
	 *
	 * This method is used for debugging only, and its access is changed in
	 * testing.
	 */
	private val hasConsistentMaxEnds: Boolean
		get() {
			return root.hasConsistentMaxEnds
		}
}
