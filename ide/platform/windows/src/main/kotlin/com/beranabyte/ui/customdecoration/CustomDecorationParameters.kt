// Copyright 2020 Kalkidan Betre Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.beranabyte.ui.customdecoration

import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KProperty


operator fun AtomicInteger.getValue(receiver: Any?, property: KProperty<*>) = get()
operator fun AtomicInteger.setValue(receiver: Any?, property: KProperty<*>, value: Int) {
	set(value)
}


class CustomDecorationParameters(
	titleBarHeight: Int = 27,
	controlBoxWidth: Int = 150,
	iconWidth: Int = 40,
	extraLeftReservedWidth: Int = 0,
	extraRightReservedWidth: Int = 0,
	maximizedWindowFrameThickness: Int = 10,
	frameResizeBorderThickness: Int = 4,
	frameBorderThickness: Int = 1
) {
	var titleBarHeight by AtomicInteger(titleBarHeight)
	var controlBoxWidth by AtomicInteger(controlBoxWidth)
	var iconWidth by AtomicInteger(iconWidth)
	var extraLeftReservedWidth by AtomicInteger(extraLeftReservedWidth)
	var extraRightReservedWidth by AtomicInteger(extraRightReservedWidth)
	var maximizedWindowFrameThickness by AtomicInteger(maximizedWindowFrameThickness)
	var frameResizeBorderThickness by AtomicInteger(frameResizeBorderThickness)
	var frameBorderThickness by AtomicInteger(frameBorderThickness)
}
