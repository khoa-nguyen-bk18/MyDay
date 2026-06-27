package com.devindie.myday

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
