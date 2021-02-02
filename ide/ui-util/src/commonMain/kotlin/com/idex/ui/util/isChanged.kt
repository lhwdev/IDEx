@file:OptIn(ComposeCompilerApi::class)
@file:Suppress("NOTHING_TO_INLINE")

package com.idex.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.currentComposer


@Composable
inline fun isChanged(value: Any?) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Boolean) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Byte) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Short) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Int) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Long) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Float) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Double) = currentComposer.changed(value)

@Composable
inline fun isChanged(value: Char) = currentComposer.changed(value)
