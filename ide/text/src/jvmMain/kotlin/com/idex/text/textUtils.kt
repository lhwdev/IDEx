@file:JvmName("TextUtilsJvm")

package com.idex.text


actual fun Char.isDigit() = Character.isDigit(this)

actual fun Char.isLetter() = Character.isLetter(this)

actual fun String.stringCopyInto(
	destination: CharArray, destinationOffset: Int,
	startIndex: Int, endIndex: Int
) {
	@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
	(this as java.lang.String).getChars(startIndex, endIndex, destination, destinationOffset)
}
