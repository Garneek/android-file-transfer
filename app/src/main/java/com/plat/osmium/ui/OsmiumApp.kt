package com.plat.osmium.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plat.osmium.ui.components.OsmiumBottomNavbar

import com.plat.osmium.ui.theme.OsmiumTheme

enum class OsmiumScreen {
    ServerList,
    ServerDetails,
    Other,
}

fun getDestinationNavbarIndex(screen: String?): Int {
    return when (screen) {
        OsmiumScreen.ServerList.name -> 0
        OsmiumScreen.ServerDetails.name -> 0
        OsmiumScreen.Other.name -> 1
        else -> 2
    }
}

@Composable
fun OsmiumApp(navController: NavHostController = rememberNavController()) {
    OsmiumTheme {
        NavHost(
            navController = navController,
            startDestination = OsmiumScreen.ServerList.name,
        ) {
            @Composable
            fun BottomNavbar() {
                OsmiumBottomNavbar(
                    getDestinationNavbarIndex(navController.currentDestination?.route),
                    {
                        navController.navigate(OsmiumScreen.ServerList.name)
                        while (navController.previousBackStackEntry != null)
                            navController.popBackStack()
                    }, {}, {})
            }

            composable(route = OsmiumScreen.ServerList.name) {
                ServersListScreen(
                    navBack = { navController.navigateUp() },
                    canNavBack = navController.previousBackStackEntry != null,
                    navAddServer = { navController.navigate(OsmiumScreen.Other.name) },
                    bottomNavbar = { BottomNavbar() }
                )
            }
            composable(route = OsmiumScreen.Other.name) {
                OtherScreen(
                    navBack = { navController.navigateUp() },
                    canNavBack = navController.previousBackStackEntry != null,
                    bottomNavbar = { BottomNavbar() }
                )
            }
        }
    }
}