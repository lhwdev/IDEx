@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package com.idex.util


@RequiresOptIn("UnsafeArray utility can be used only for performance and checking is user's responsibility.")
@Retention(AnnotationRetention.SOURCE)
annotation class UnsafeArray


@UnsafeArray
inline fun <T> unsafeArrayOf(size: Int): Array<T> = arrayOfNulls<Any?>(size) as Array<T>

@UnsafeArray
inline fun <T> Array<T>.copyUnsafeOf(newSize: Int): Array<T> = copyOf(newSize) as Array<T>

@UnsafeArray
inline fun <T> Array<T>.emptyAt(index: Int) = (this as Array<Any?>).swap(null, index) as T

@UnsafeArray
inline fun <T> Array<T>.emptyRange(startIndex: Int, endIndex: Int) {
	(this as Array<Any?>).fill(null, startIndex, endIndex)
}
