package com.idex.text

import com.idex.util.Interval


data class Marker<T>(
	val data: T,
	override var start: Int,
	override var end: Int,
	val isExclusive: Boolean = true
) : Interval
