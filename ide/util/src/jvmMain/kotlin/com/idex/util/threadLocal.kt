package com.idex.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import java.lang.ThreadLocal as JThreadLocal


actual class ThreadLocal<T> actual constructor() : ReadWriteProperty<Any?, T?> {
	private val local = JThreadLocal<T?>()
	
	actual override fun getValue(thisRef: Any?, property: KProperty<*>) = local.get()
	
	actual override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
		local.set(value)
	}
}

actual class ThreadLocalDefault<T> actual constructor(val defaultValue: T) : ReadWriteProperty<Any?, T> {
	private val local = object : JThreadLocal<T>() {
		override fun initialValue() = defaultValue
	}
	
	actual override fun getValue(thisRef: Any?, property: KProperty<*>): T = local.get()
	
	actual override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		local.set(value)
	}
}
