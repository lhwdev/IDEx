package com.idex.util


interface Stack<out T> : Collection<T> {
	fun peekFirst(): T
}

interface MutableStack<T> : Stack<T>, MutableCollection<T> {
	fun pushFirst(element: T)
	fun popFirst(): T
	fun replaceFirst(element: T)
}

interface Queue<out T> : Collection<T> {
	fun peekFirst(): T
}

interface MutableQueue<T> : Queue<T>, MutableCollection<T> {
	fun pushLast(element: T)
	fun popFirst(): T
	fun replaceFirst(element: T)
}

interface Deque<out T> : Stack<T>, Queue<T>
interface MutableDeque<T> : Stack<T>, Deque<T>, MutableStack<T>, MutableQueue<T>

private class DequeImpl<T>(
	array: Array<@UnsafeVariance T>,
	growFactor: Float = sDefaultGrowFactor
) : CircularArrayList<T>(array, growFactor), MutableDeque<T> {
	
	constructor(capacity: Int = sDefaultCapacity, growFactor: Float = sDefaultGrowFactor) :
		this(@OptIn(UnsafeArray::class) unsafeArrayOf(capacity), growFactor)
	
	@OptIn(UnsafeArray::class)
	constructor(collection: Collection<T>, capacity: Int = collection.size) : this(collection.toArrayUnsafe(capacity)) {
		size = collection.size
	}
	
	override fun pushFirst(element: T) {
		add(0, element)
	}
	
	override fun pushLast(element: T) {
		add(element)
	}
	
	override fun popFirst() = removeAt(0)
	override fun peekFirst() = get(0)
	
	override fun replaceFirst(element: T) {
		set(0, element)
	}
}

fun <T> stackOf(vararg elements: T): Stack<T> = DequeImpl(elements.copyOf())

@Suppress("UNCHECKED_CAST")
@OptIn(UnsafeArray::class)
fun <T> mutableStackOf(vararg elements: T): MutableStack<T> =
	DequeImpl(elements.copyUnsafeOf(elements.size + 32) as Array<T>)

fun <T> mutableStackOf(capacity: Int = sDefaultCapacity, growFactor: Float = sDefaultGrowFactor): MutableStack<T> =
	DequeImpl(capacity, growFactor)


fun <T> queueOf(vararg elements: T): Queue<T> = DequeImpl(elements.copyOf())

@Suppress("UNCHECKED_CAST")
@OptIn(UnsafeArray::class)
fun <T> mutableQueueOf(vararg elements: T): MutableQueue<T> =
	DequeImpl(elements.copyUnsafeOf(elements.size + 32) as Array<T>)

fun <T> mutableQueueOf(capacity: Int = sDefaultCapacity, growFactor: Float = sDefaultGrowFactor): MutableQueue<T> =
	DequeImpl(capacity, growFactor)


fun <T> dequeOf(vararg elements: T): Deque<T> = DequeImpl(elements.copyOf())

@Suppress("UNCHECKED_CAST")
@OptIn(UnsafeArray::class)
fun <T> mutableDequeOf(vararg elements: T): MutableDeque<T> =
	DequeImpl(elements.copyUnsafeOf(elements.size + 32) as Array<T>)

fun <T> mutableDequeOf(capacity: Int = sDefaultCapacity, growFactor: Float = sDefaultGrowFactor): MutableDeque<T> =
	DequeImpl(capacity, growFactor)

