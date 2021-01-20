package com.idex.text


interface ArrayTextBase

interface ArrayText : ArrayTextBase {
	val array: CharArray
}

interface ArrayTextBounded : ArrayTextBase {
	val array: CharArray
	val startIndex: Int
	val endIndex: Int
}
