package com.idex.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <T> threadLocalOf() = ThreadLocal<T>()
fun <T> threadLocalOf(defaultValue: T) = ThreadLocalDefault(defaultValue)


expect class ThreadLocal<T>() : ReadWriteProperty<Any?, T?> {
	override fun getValue(thisRef: Any?, property: KProperty<*>): T?
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?)
}

expect class ThreadLocalDefault<T>(defaultValue: T) : ReadWriteProperty<Any?, T> {
	override fun getValue(thisRef: Any?, property: KProperty<*>): T
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}
