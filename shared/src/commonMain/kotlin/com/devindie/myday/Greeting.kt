package com.devindie.myday

class Greeting {
    private val platform = getPlatform()

    fun greet(): String = sayHello(platform.name)
}
