package com.devindie.myday.dev

import android.app.Application
import android.content.pm.ApplicationInfo

internal actual fun isDevBuild(): Boolean {
    val app = runCatching {
        Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication")
            .invoke(null) as? Application
    }.getOrNull() ?: return true
    return app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}

internal actual fun currentThreadName(): String = Thread.currentThread().name
