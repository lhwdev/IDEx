package com.idex.util


@Suppress("NOTHING_TO_INLINE")
inline fun <T> lazyNone(noinline initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
