package com.devindie.myday.feature.main.api

import com.devindie.myday.core.navigation.MainRoute
import kotlin.test.Test
import kotlin.test.assertEquals

class MainRouteTest {
    @Test
    fun mainDestinationRoutesAreUnique() {
        val routes = MainDestination.entries.map { it.route }

        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun startDestination_isReflect() {
        assertEquals(MainRoute.Reflect, MainDestination.Start.route)
    }
}
