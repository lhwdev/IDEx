@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE") // inevitable; cannot suppress with something like --add-exports
// see https://discuss.kotlinlang.org/t/using-sun-awt-image-with-java-11/15127/4

package com.idex.ui.platform.windows

import sun.java2d.pipe.Region
import java.awt.*
import java.awt.BufferCapabilities.FlipContents
import java.awt.event.FocusEvent
import java.awt.event.PaintEvent
import java.awt.image.ColorModel
import java.awt.image.ImageObserver
import java.awt.image.ImageProducer
import java.awt.peer.*


class NullComponentPeer : LightweightPeer, CanvasPeer, PanelPeer {
	override fun isObscured() = false
	
	override fun canDetermineObscurity() = false
	
	override fun isFocusable(): Boolean = false
	
	override fun setVisible(b: Boolean) {}
	fun show() {}
	fun hide() {}
	
	override fun setEnabled(b: Boolean) {}
	fun enable() {}
	fun disable() {}
	
	override fun paint(g: Graphics) {}
	override fun print(g: Graphics) {}
	override fun setBounds(x: Int, y: Int, width: Int, height: Int, op: Int) {}
	override fun coalescePaintEvent(e: PaintEvent) {}
	
	override fun handleEvent(arg0: AWTEvent) {}
	override fun getPreferredSize() = Dimension(1, 1)
	
	override fun getMinimumSize(): Dimension = Dimension(1, 1)
	
	override fun getColorModel(): ColorModel? = null
	
	override fun getGraphics() = null
	
	override fun getGraphicsConfiguration() = null
	
	override fun getFontMetrics(font: Font) = null
	
	override fun dispose() {}
	
	override fun setForeground(c: Color) {}
	override fun setBackground(c: Color) {}
	override fun setFont(f: Font) {}
	override fun updateCursorImmediately() {}
	
	override fun requestFocus(
		lightweightChild: Component, temporary: Boolean,
		focusedWindowChangeAllowed: Boolean, time: Long, cause: FocusEvent.Cause
	) = false
	
	override fun createImage(producer: ImageProducer) = null
	
	override fun createImage(width: Int, height: Int) = null
	
	override fun prepareImage(img: Image, w: Int, h: Int, o: ImageObserver) = false
	
	override fun checkImage(img: Image, w: Int, h: Int, o: ImageObserver) = 0
	
	override fun getLocationOnScreen() = Point(0, 0)
	
	override fun getInsets() = Insets(0, 0, 0, 0)
	
	override fun beginValidate() {}
	override fun endValidate() {}
	
	
	override fun handlesWheelScrolling() = false
	
	override fun createVolatileImage(width: Int, height: Int) = null
	
	override fun beginLayout() {}
	override fun endLayout() {}
	
	override fun createBuffers(numBuffers: Int, caps: BufferCapabilities) {
		throw AWTException(
			"Page-flipping is not allowed on a lightweight component"
		)
	}
	
	override fun getBackBuffer(): Image = throw IllegalStateException(
		"Page-flipping is not allowed on a lightweight component"
	)
	
	override fun flip(
		x1: Int, y1: Int, x2: Int, y2: Int,
		flipAction: FlipContents
	) {
		throw IllegalStateException(
			"Page-flipping is not allowed on a lightweight component"
		)
	}
	
	override fun destroyBuffers() {}
	
	override fun isReparentSupported() = false
	
	override fun reparent(newNativeParent: ContainerPeer) {
		throw UnsupportedOperationException()
	}
	
	override fun layout() {}
	
	override fun applyShape(shape: Region) {}
	
	override fun setZOrder(above: ComponentPeer) {}
	override fun updateGraphicsData(gc: GraphicsConfiguration) = false
	
	override fun getAppropriateGraphicsConfiguration(gc: GraphicsConfiguration) = gc
}
