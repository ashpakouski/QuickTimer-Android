package com.shpak.quicktimer.util

import kotlin.reflect.KProperty

class LazyTryOrNull<out T>(
    private val initializer: () -> T?
) {
    companion object {
        private val UNINITIALIZED = Any()
    }

    @Volatile
    private var _value: Any? = UNINITIALIZED

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (_value === UNINITIALIZED) {
            synchronized(this) {
                if (_value === UNINITIALIZED) {
                    _value = try {
                        initializer()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return _value as? T?
    }
}

fun <T> lazyTryOrNull(initializer: () -> T?): LazyTryOrNull<T> = LazyTryOrNull(initializer)