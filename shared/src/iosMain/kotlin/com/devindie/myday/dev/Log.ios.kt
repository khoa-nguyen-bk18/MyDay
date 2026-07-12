package com.devindie.myday.dev

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform as NativePlatform
import platform.Foundation.NSThread

@OptIn(ExperimentalNativeApi::class)
internal actual fun isDevBuild(): Boolean = NativePlatform.isDebugBinary

internal actual fun currentThreadName(): String {
    val name = NSThread.currentThread.name
    return if (!name.isNullOrBlank()) name else "thread-${NSThread.currentThread.hashCode()}"
}
