package com.devindie.myday.feature.main.api

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector
import com.devindie.myday.core.navigation.MainRoute

/** Bottom-nav destinations from Stitch screen "Empty Nav Screen" (project 17128375841121903851). */
enum class MainDestination(
    val route: MainRoute,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Browse(
        route = MainRoute.Browse,
        label = "Browse",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
    ),
    Cart(
        route = MainRoute.Cart,
        label = "Cart",
        selectedIcon = Icons.Filled.ShoppingBasket,
        unselectedIcon = Icons.Outlined.ShoppingBasket,
    ),
    Reflect(
        route = MainRoute.Reflect,
        label = "Reflect",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
    ),
    Profile(
        route = MainRoute.Profile,
        label = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
    ),
    ;

    companion object {
        val Start = Browse
    }
}
