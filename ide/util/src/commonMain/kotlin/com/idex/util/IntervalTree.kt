package com.idex.util


/**
 * A balanced binary-search tree keyed by Interval objects.
 *
 * The underlying data-structure is a red-black tree largely implemented from
 * CLRS (Introduction to Algorithms, 2nd edition) with the interval-tree
 * extensions mentioned in section 14.3
 */
class IntervalTree<T : Interval> private constructor(rootElement: T?, size: Int) : MutableIntervalList<T>,
	AbstractMutableCollection<T>() {
	
	fun dump(): String {
		val lines = mutableMapOf<Int, String>()
		root.dump(lines)
		return lines.toList().sortedBy { it.first }.joinToString("\n") { it.second }
	}
	
	private fun Node.dump(lines: MutableMap<Int, String>, depth: Int = 0) {
		if(isNil) return
		var value = lines.getOrElse(depth) { "" }
		value += interval
		lines[depth] = value
		left.dump(lines, depth + 1)
		lines[depth] = lines[depth] + " "
		right.dump(lines, depth + 1)
	}
	
	private val nil = Node() // The sentinel Node to represent the absence of a node.
	private var root: Node = if(rootElement == null) nil else Node(rootElement) // The root Node.
	
	/**
	 * The number of intervals stored in this IntervalTree.
	 */
	override var size: Int = size // Size of the tree. Updated by insert() and Node#delete()
		private set
	
	/**
	 * Constructs an empty IntervalTree.
	 */
	constructor() : this(null, 0)
	
	/**
	 * Constructs an IntervalTree containing a single node corresponding to
	 * the given interval.
	 * @param rootElement - the interval to add to the tree
	 */
	constructor(rootElement: T) : this(rootElement, 1) {
		root.blacken()
	}
	
	
	///////////////////////////////////
	// Tree -- General query methods //
	///////////////////////////////////
	override fun isEmpty(): Boolean = root.isNil
	
	/**
	 * The Node in this IntervalTree that contains the given Interval.
	 * This method returns the nil Node if the Interval cannot be found.
	 * @param interval - the Interval to search for.
	 */
	private fun search(interval: Interval) = root.search(interval)
	
	override fun get(interval: Interval): T? = search(interval).interval
	
	override fun getNode(interval: Interval): Ref<T>? = search(interval).takeUnless { it.isNil }
	
	/**
	 * Whether or not this IntervalTree contains the given Interval.
	 * @param element - the Interval to search for
	 */
	override fun contains(element: T): Boolean {
		return !search(element).isNil
	}
	
	/**
	 * Whether or not this IntervalTree contains the given Interval.
	 * @param interval - the Interval to search for
	 */
	override fun containsInterval(interval: Interval): Boolean {
		return !search(interval).isNil
	}
	
	/**
	 * The minimum value in this IntervalTree
	 * @return the minimum value in this IntervalTree; maybe null
	 */
	val minimum: T?
		get() {
			val n: Node = root.minimumNode
			return if(n.isNil) null else n.interval
		}
	
	/**
	 * The maximum value in this IntervalTree
	 * @return the maximum value in this IntervalTree; maybe null.
	 */
	val maximum: T?
		get() {
			val n = root.maximumNode
			return if(n.isNil) null else n.interval
		}
	
	/**
	 * The next Interval in this IntervalTree
	 * @param interval - the Interval to search for
	 * @return the next Interval in this IntervalTree; maybe null.
	 */
	fun successor(interval: Interval): T? {
		var n = search(interval)
		if(n.isNil) {
			return null
		}
		n = n.successor
		return if(n.isNil) null else n.interval
	}
	
	/**
	 * The previous Interval in this IntervalTree
	 * @param interval - the Interval to search for
	 * @return the previous Interval in this IntervalTree; maybe null.
	 */
	fun predecessor(interval: Interval): T? {
		var n = search(interval)
		if(n.isNil) {
			return null
		}
		n = n.predecessor
		return if(n.isNil) null else n.interval
	}
	
	/**
	 * An Iterator which traverses the tree in ascending order.
	 */
	override fun iterator(): MutableIterator<T> {
		return TreeIterator(root)
	}
	
	/**
	 * An Iterator over the Intervals in this IntervalTree that overlap the
	 * given Interval
	 * @param interval - the overlapping Interval
	 */
	override fun overlappers(interval: Interval): Iterable<T> {
		return root.overlappers(interval)
	}
	
	/**
	 * Whether or not any of the Intervals in this IntervalTree overlap the given
	 * Interval
	 * @param interval - the potentially overlapping Interval
	 */
	override fun overlaps(interval: Interval): Boolean {
		return !root.anyOverlappingNode(interval).isNil
	}
	
	/**
	 * The number of Intervals in this IntervalTree that overlap the given
	 * Interval
	 * @param interval - the overlapping Interval
	 */
	override fun overlapCount(interval: Interval): Int {
		return root.numOverlappingNodes(interval)
	}
	
	/**
	 * The least Interval in this IntervalTree that overlaps the given Interval
	 * @param interval - the overlapping Interval
	 * @return an Optional containing, if it exists, the least Interval in this
	 * IntervalTree that overlaps the given Interval; otherwise (i.e., if there
	 * is no overlap), an empty Optional
	 */
	fun minimumOverlapper(interval: Interval): T? {
		val n = root.minimumOverlappingNode(interval)
		return if(n.isNil) null else n.interval
	}
	///////////////////////////////
	// Tree -- Insertion methods //
	///////////////////////////////
	
	
	private fun addInternal(node: Node): Node {
		var y = nil
		var x = root
		while(!x.isNil) {                         // Traverse the tree down to a leaf.
			y = x
			x.maxEnd = x.maxEnd.coerceAtLeast(node.maxEnd) // Update maxEnd on the way down.
			val cmp = node.compareTo(x)
			if(cmp == 0) {
				return x // Value already in tree. Do nothing.
			}
			x = if(cmp == -1) x.left else x.right
		}
		node.parent = y
		if(y.isNil) {
			root = node
			root.blacken()
		} else {                      // Set the parent of n.
			val cmp = node.compareTo(y)
			if(cmp == -1) {
				y.left = node
			} else {
				assert(cmp == 1)
				y.right = node
			}
			node.left = nil
			node.right = nil
			node.redden()
			node.insertFixup()
		}
		size++
		return node
	}
	
	override fun add(element: T): Boolean {
		val newNode = Node(element)
		return addInternal(newNode) === newNode
	}
	
	override fun addNode(element: T): Ref<T> = addInternal(Node(element))
	
	
	override fun replace(element: T): T? {
		val previous = search(element)
		val last = previous.interval
		if(last != null) previous.interval = element // here all other properties are identical(so caught by search)
		
		return last
	}
	
	override fun replace(old: T, new: T) = if(old.compareTo(new) == 0) {
		// only replace value
		
		val previous = search(old)
		val last = previous.interval
		val match = last == old
		if(match) previous.interval = new // here all other properties are identical(so caught by search)
		match
	} else {
		remove(old) and add(new)
	}
	
	
	//////////////////////////////
	// Tree -- Deletion methods //
	//////////////////////////////
	
	
	/**
	 * Deletes the given interval from this IntervalTree.
	 *
	 *
	 * If the interval does not exist, this IntervalTree remains unchanged.
	 * @param interval - the Interval to delete from the tree
	 * @return whether or not an Interval was removed from this IntervalTree
	 */
	override fun removeAt(interval: Interval): Boolean {    // Node#delete does nothing and returns
		return search(interval).delete() // false if element.isNil
	}
	
	override fun remove(element: T): Boolean {
		val node = search(element)
		return if(node.interval == element) node.delete() else false
	}
	
	/**
	 * Deletes the smallest Interval from this IntervalTree.
	 *
	 *
	 * If there is no smallest Interval (that is, if the tree is empty), this
	 * IntervalTree remains unchanged.
	 * @return whether or not an Interval was removed from this IntervalTree
	 */
	fun removeMin(): Boolean {            // Node#delete does nothing and
		return root.minimumNode.delete() // returns false if element.isNil
	}
	
	/**
	 * Deletes the greatest Interval from this IntervalTree.
	 *
	 *
	 * If there is no greatest Interval (that is, if the tree is empty), this
	 * IntervalTree remains unchanged.
	 * @return whether or not an Interval was removed from this IntervalTree
	 */
	fun removeMax(): Boolean {            // Node#delete does nothing and
		return root.maximumNode.delete() // returns false if elemen.isNil
	}
	
	/**
	 * Deletes all Intervals that overlap the given Interval from this
	 * IntervalTree.
	 *
	 *
	 * If there are no overlapping Intervals, this IntervalTree remains
	 * unchanged.
	 * @param element - the overlapping Interval
	 * @return whether or not an Interval was removed from this IntervalTree
	 */
	fun removeOverlappers(element: T): Boolean {
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
		val iter = OverlappingNodeIterator(root, element)
		iter.forEach { s.add(it) }
		return s.map { remove(it.interval) }
			.fold(false) { a, b -> a || b }
	}
	
	override fun clear() {
		val lastRoot = root.interval
		root = if(lastRoot == null) Node() else Node(lastRoot)
	}
	
	/**
	 * A representation of a node in an interval tree.
	 */
	private inner class Node : Interval, Ref<T> {
		/* Most of the "guts" of the interval tree are actually methods called
         * by nodes. For example, IntervalTree#delete(val) searches up the Node
         * containing val; then that Node deletes itself with Node#delete().
         */
		var interval: T?
		var parent: Node
		var left: Node
		var right: Node
		var isBlack = false
		var maxEnd = 0
		
		override var value: T
			get() = interval!!
			set(value) {
				interval = value
			}
		
		/**
		 * Constructs a Node with no data.
		 *
		 *
		 * This Node has a null interval field, is black, and has all pointers
		 * pointing at itself. This is intended to be used as the sentinel
		 * node in the tree ("nil" in CLRS).
		 */
		@Suppress("UNCHECKED_CAST")
		constructor() {
			interval = null
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
			this.interval = interval
			parent = nil
			left = nil
			right = nil
			maxEnd = interval.end
			redden()
		}
		
		/**
		 * The start of the Interval in this Node
		 */
		override val start: Int
			get() {
				return interval!!.start
			}
		
		/**
		 * The end of the Interval in this Node
		 */
		override val end: Int
			get() {
				return interval!!.end
			}
		///////////////////////////////////
		// Node -- General query methods //
		///////////////////////////////////
		/**
		 * Searches the subtree rooted at this Node for the given Interval.
		 * @param interval - the Interval to search for
		 * @return the Node with the given Interval, if it exists; otherwise,
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
		 * Searches the subtree rooted at this Node for its minimum Interval.
		 * @return the Node with the minimum Interval, if it exists; otherwise,
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
		 * Searches the subtree rooted at this Node for its maximum Interval.
		 * @return the Node with the maximum Interval, if it exists; otherwise,
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
		 * the Interval. This method is meant to be a quick helper method to
		 * determine if any overlap exists between an Interval and any of an
		 * IntervalTree's Intervals. The returned Node will be the first
		 * overlapping one found.
		 * @param interval - the given Interval
		 * @return an overlapping Node from this Node's subtree, if one exists;
		 * otherwise the sentinel Node
		 */
		fun anyOverlappingNode(interval: Interval): Node {
			var x = this
			while(!x.isNil && !interval.overlap(x.interval!!)) {
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
							if(n.start >= interval.end) {
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
		 * Interval.
		 * @param interval - the overlapping Interval
		 * @return the next Node that overlaps the Interval, if one exists;
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
		 * The number of Nodes in this Node's subtree that overlap the given
		 * Interval.
		 *
		 *
		 * This number includes this Node if this Node overlaps interval. This method
		 * iterates over all overlapping Nodes, so if you ultimately need to
		 * inspect the Nodes, it will be more efficient to simply create the
		 * Iterator yourself.
		 * @param interval - the overlapping Interval
		 * @return the number of overlapping Nodes
		 */
		fun numOverlappingNodes(interval: Interval): Int {
			var count = 0
			val iter = OverlappingNodeIterator(this, interval)
			while(iter.hasNext()) {
				iter.next()
				count++
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
				y.isRoot -> root = x
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
			size--
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
			get() = left.isNil && right.isNil
		
		/**
		 * Whether or not this Node has two children, i.e., neither of its
		 * children are leaves.
		 */
		val hasTwoChildren: Boolean
			get() = !left.isNil && !right.isNil
		
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
		private fun grandparent(): Node {
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
			var value = interval!!.end
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
			interval = o.interval
		}
		
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
						z.grandparent().redden()
						z = z.grandparent()
					} else {
						if(z.isRightChild) {
							z = z.parent
							z.leftRotate()
						}
						z.parent.blacken()
						z.grandparent().redden()
						z.grandparent().rightRotate()
					}
				} else {
					val y = z.grandparent().left
					if(y.isRed) {
						z.parent.blacken()
						y.blacken()
						z.grandparent().redden()
						z = z.grandparent()
					} else {
						if(z.isLeftChild) {
							z = z.parent
							z.rightRotate()
						}
						z.parent.blacken()
						z.grandparent().redden()
						z.grandparent().leftRotate()
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
				isNil -> true
				isBlack -> left.hasValidRedColoring &&
					right.hasValidRedColoring
				else -> left.isBlack && right.isBlack &&
					left.hasValidRedColoring &&
					right.hasValidRedColoring
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
				isNil -> true                                 // 1. sentinel node
				hasNoChildren -> {                            // 2. leaf node
					maxEnd == end
				}
				else -> {
					val consistent = maxEnd >= end
					when {
						// 3. two children
						hasTwoChildren -> consistent && maxEnd >= left.maxEnd && maxEnd >= right.maxEnd &&
							left.hasConsistentMaxEnds &&
							right.hasConsistentMaxEnds
						// 4. one child -- right
						left.isNil -> consistent && maxEnd >= right.maxEnd &&
							right.hasConsistentMaxEnds
						// 5. one child -- left
						else -> consistent &&
							maxEnd >= left.maxEnd &&
							left.hasConsistentMaxEnds
					}
				}
			}
	}
	///////////////////////
	// Tree -- Iterators //
	///////////////////////
	/**
	 * An Iterator which walks along this IntervalTree's Nodes in ascending order.
	 */
	private inner class TreeNodeIterator(root: Node) : MutableIterator<Node> {
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
		
		override fun remove() {
			next.delete()
		}
		
		init {
			next = root.minimumNode
		}
	}
	
	/**
	 * An Iterator which walks along this IntervalTree's Intervals in ascending
	 * order.
	 *
	 *
	 * This class just wraps a TreeNodeIterator and extracts each Node's Interval.
	 */
	private inner class TreeIterator(root: Node) : MutableIterator<T> {
		private val nodeIter = TreeNodeIterator(root)
		
		override fun hasNext(): Boolean {
			return nodeIter.hasNext()
		}
		
		override fun next(): T {
			return nodeIter.next().interval!!
		}
		
		override fun remove() {
			nodeIter.remove()
		}
	}
	
	/**
	 * An Iterator which walks along this IntervalTree's Nodes that overlap
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
			val rtrn = next
			next = rtrn.nextOverlappingNode(interval)
			return rtrn
		}
		
		init {
			next = root.minimumOverlappingNode(interval)
		}
	}
	
	/**
	 * An Iterator which walks along this IntervalTree's Intervals that overlap
	 * a given Interval in ascending order.
	 *
	 *
	 * This class just wraps an OverlappingNodeIterator and extracts each Node's
	 * Interval.
	 */
	private inner class OverlapperIterator(root: Node, interval: Interval) : Iterator<T> {
		private val nodeIter: OverlappingNodeIterator = OverlappingNodeIterator(root, interval)
		override fun hasNext(): Boolean {
			return nodeIter.hasNext()
		}
		
		override fun next(): T {
			return nodeIter.next().interval!!
		}
		
	}
	///////////////////////////////
	// Tree -- Debugging methods //
	///////////////////////////////
	/**
	 * Whether or not this IntervalTree is a valid binary-search tree.
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
	 * Whether or not this IntervalTree is balanced.
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
	 * Whether or not this IntervalTree has a valid red coloring.
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
		get() = root.hasValidRedColoring
	
	/**
	 * Whether or not this IntervalTree has consistent maxEnd values.
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
		get() = root.hasConsistentMaxEnds
}
