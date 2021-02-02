package com.idex.ui.util

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.idex.util.MutableDeque
import com.idex.util.MutableQueue
import com.idex.util.MutableStack


fun <T> mutableStateStackOf(): MutableStack<T> = SnapshotStateStack(mutableStateListOf())
fun <T> mutableStateStackOf(vararg elements: T): MutableStack<T> =
	SnapshotStateStack(mutableStateListOf(*elements))

fun <T> mutableStateQueueOf(): MutableQueue<T> = SnapshotStateDeque(mutableStateListOf())
fun <T> mutableStateQueueOf(vararg elements: T): MutableQueue<T> =
	SnapshotStateDeque(mutableStateListOf(*elements))

fun <T> mutableStateDequeOf(): MutableDeque<T> = SnapshotStateDeque(mutableStateListOf())
fun <T> mutableStateDequeOf(vararg elements: T): MutableDeque<T> =
	SnapshotStateDeque(mutableStateListOf(*elements))


// index is inverted internally; PersistentList implementation is optimized for tail add
// so this exists separated from SnapshotStateDeque
@Stable
class SnapshotStateStack<T> internal constructor(private val original: SnapshotStateList<T>) : MutableStack<T> {
	override val size get() = original.size
	override fun contains(element: T) = original.contains(element)
	override fun containsAll(elements: Collection<T>) = original.containsAll(elements)
	override fun isEmpty() = original.isEmpty()
	override fun iterator() = original.iterator()
	override fun peekFirst() = original.last()
	override fun pushFirst(element: T) {
		original.add(element)
	}
	
	override fun popFirst() = original.removeLast()
	override fun replaceFirst(element: T) {
		original[original.lastIndex] = element
	}
	
	override fun removeAll(elements: Collection<T>) = original.removeAll(elements)
	override fun retainAll(elements: Collection<T>) = original.retainAll(elements)
	override fun add(element: T): Boolean {
		original.add(0, element) // originally appending at the tail
		return true
	}
	
	override fun addAll(elements: Collection<T>) = original.addAll(0, elements)
	override fun clear() {
		original.clear()
	}
	
	override fun remove(element: T) = original.remove(element)
}

@Stable
class SnapshotStateDeque<T> internal constructor(private val original: SnapshotStateList<T>) : MutableDeque<T> {
	override val size get() = original.size
	override fun contains(element: T) = original.contains(element)
	override fun containsAll(elements: Collection<T>) = original.containsAll(elements)
	override fun isEmpty() = original.isEmpty()
	override fun iterator() = original.iterator()
	override fun peekFirst(): T = original.last()
	override fun removeAll(elements: Collection<T>) = original.removeAll(elements)
	override fun retainAll(elements: Collection<T>) = original.retainAll(elements)
	override fun pushFirst(element: T) {
		original.add(0, element)
	}
	
	override fun popFirst() = original.removeAt(0)
	override fun replaceFirst(element: T) {
		original[0] = element
	}
	
	override fun pushLast(element: T) {
		original.add(element)
	}
	
	override fun add(element: T) = original.add(element)
	override fun addAll(elements: Collection<T>) = original.addAll(elements)
	override fun clear() {
		original.clear()
	}
	
	override fun remove(element: T) = original.remove(element)
}
