@file:Suppress("NOTHING_TO_INLINE")

package com.idex.util


const val sDefaultCapacity = 64
const val sDefaultGrowFactor = 1.25f


@UnsafeArray
internal fun <T> Collection<T>.toArrayUnsafe(capacity: Int) = unsafeArrayOf<T>(capacity).also {
	forEachIndexed { index, element -> it[index] = element }
}


fun <T> circularArrayListOf(capacity: Int = sDefaultCapacity, growFactor: Float = sDefaultGrowFactor): MutableList<T> =
	CircularArrayList(capacity, growFactor)

@Suppress("UNCHECKED_CAST")
fun <T> circularArrayListOf(vararg list: T): MutableList<T> = CircularArrayList(list.asList())


private fun <T> Array<T>.copyIntoUnchecked(
	circulationStartIndex: Int,
	destination: Array<T>, destinationOffset: Int = 0,
	startIndex: Int = 0, endIndex: Int = size
) {
	if(startIndex == endIndex) return
	
	val actualStart = (circulationStartIndex + startIndex) % size
	val actualEnd = (circulationStartIndex + endIndex) % size
	
	if(actualStart < actualEnd) { // normal ordering, no circular
		copyInto(destination, destinationOffset, actualStart, actualEnd)
	} else { // circular order
		copyInto(destination, destinationOffset, actualStart, size) // from actualStart to end
		copyInto( // from start to actualEnd
			destination, destinationOffset + (size - actualStart),
			0, actualEnd
		)
	}
}


open class CircularArrayList<T> protected constructor(
	private var array: Array<@UnsafeVariance T>,
	val growFactor: Float = sDefaultGrowFactor
) : AbstractMutableList<T>(), RandomAccess {
	
	constructor(capacity: Int = sDefaultCapacity, growFactor: Float = sDefaultGrowFactor) :
		this(@OptIn(UnsafeArray::class) unsafeArrayOf(capacity), growFactor)
	
	@OptIn(UnsafeArray::class)
	constructor(collection: Collection<T>, capacity: Int = collection.size) : this(collection.toArrayUnsafe(capacity)) {
		size = collection.size
	}
	
	private var startIndex = 0
	
	final override var size = 0
		protected set
	
	val capacity get() = array.size
	
	private inline fun realIndex(index: Int) = (startIndex + index) % capacity
	
	private inline fun check(index: Int): Int {
		if(index !in 0 until size) throw IndexOutOfBoundsException("index=$index size=$size")
		return index
	}
	
	private inline fun check(index: Int, name: String) {
		if(index !in 0 until size) throw IndexOutOfBoundsException("$name index=$index size=$size")
	}
	
	private fun ensureCapacity(newCapacity: Int, retainCount: Int = size): Boolean {
		if(newCapacity > capacity) { // grow
			growTo(newCapacity, retainCount)
			return true
		}
		return false
	}
	
	private fun growTo(newCapacity: Int, retainCount: Int = size) {
		val newArray = @OptIn(UnsafeArray::class) unsafeArrayOf<T>((newCapacity * growFactor).toInt() + 16)
		// reorder circular ordering
		array.copyIntoUnchecked(startIndex, newArray, endIndex = retainCount)
		array = newArray
		startIndex = 0
	}
	
	fun copyInto(destination: Array<T>, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size) {
		check(startIndex, "startIndex")
		check(endIndex)
		if(startIndex > endIndex) throw IndexOutOfBoundsException("startIndex($startIndex) > endIndex($endIndex)")
		if(destinationOffset !in destination.indices)
			throw IndexOutOfBoundsException("destination index=$destinationOffset size=${destination.size}")
		
		array.copyIntoUnchecked(this.startIndex, destination, destinationOffset, startIndex, endIndex)
	}
	
	override fun get(index: Int) = array[realIndex(check(index))]
	
	override fun add(index: Int, element: T) {
		val oldSize = size
		// not 0 until size; 0..size because add(size, ??) means appending an element
		if(index !in 0..oldSize) throw IndexOutOfBoundsException("index=$index size=$size")
		
		val newSize = oldSize + 1
		size = newSize
		
		val spare = capacity >= newSize
		
		if(spare) when(index) {
			0 -> {
				val newStart = startIndex - 1
				startIndex = newStart
				array[newStart] = element
				return
			}
			
			oldSize -> {
				array[realIndex(oldSize)] = element
				return
			}
		}
		
		// fallback
		val last = array
		val lastStart = startIndex
		growTo(newSize, retainCount = index)
		array[index] = element
		last.copyIntoUnchecked(lastStart, array, destinationOffset = index + 1, startIndex = index, endIndex = oldSize)
	}
	
	override fun removeAt(index: Int): T { // TODO: also remove first
		check(index)
		size--
		
		return when(index) {
			0 -> {
				val oldStart = startIndex
				val last = @OptIn(UnsafeArray::class) array.emptyAt(oldStart)
				startIndex = (oldStart + 1) % capacity
				return last
			}
			lastIndex -> {
				@OptIn(UnsafeArray::class)
				return array.emptyAt(realIndex(lastIndex))
			}
			else -> {
				// fallback
				val realIndex = realIndex(index)
				val realEnd = realIndex(size)
				println("wow $realIndex $realEnd")
				
				if(realIndex < realEnd) { // normal ordering
					val value = array[realIndex]
					array.copyInto(array, destinationOffset = realIndex, startIndex = realIndex + 1, endIndex = realEnd)
					value
				} else {
					array.copyInto(
						array, destinationOffset = realIndex,
						startIndex = realIndex + 1, endIndex = capacity
					)
					val value = array.swap(array[0], array.lastIndex)
					array.copyInto(array, destinationOffset = 0, startIndex = 1, endIndex = realEnd + 1)
					@OptIn(UnsafeArray::class) array.emptyAt(realEnd)
					value
				}
			}
		}
	}
	
	override fun set(index: Int, element: T): T {
		check(index)
		
		val realIndex = realIndex(index)
		return array.swap(element, realIndex)
	}
	
	// TODO: change this: this is only for test
	fun dumpToString() = "startIndex=$startIndex " + array.contentToString()
	override fun toString() = joinToString(prefix = "[", postfix = "]") { it.toString() }
}
