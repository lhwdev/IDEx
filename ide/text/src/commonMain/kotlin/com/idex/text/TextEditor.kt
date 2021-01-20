package com.idex.text

import com.idex.util.IntervalSetList
import com.idex.util.MutableIntervalSetList
import kotlinx.coroutines.flow.MutableSharedFlow


abstract class TextEditor : MutableTextBuffer(), ObservableText {
	override val observation = MutableSharedFlow<TextMutation>()
	
	
	/// Markers
	
	// Requirements:
	// 1. identifiable via interval
	// 2. can exist multiple nodes with same interval
	
	abstract val markers: IntervalSetList<*>
	
	abstract fun beginMutateMarkers(): MutableIntervalSetList<*>
	
	abstract fun endMutateMarkers(markers: MutableIntervalSetList<*>)
	
	inline fun <R> mutateMarkers(block: MutableIntervalSetList<*>.() -> R): R {
		val markers = beginMutateMarkers()
		return try {
			markers.block()
		} finally {
			endMutateMarkers(markers)
		}
	}
}
