package com.plat.osmium.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plat.osmium.ui.components.OsmiumBottomNavbar

import com.plat.osmium.ui.theme.OsmiumTheme

enum class OsmiumScreen() {
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
      @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
      @Composable
      fun Route(screen: @Composable () -> Unit) {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            OsmiumBottomNavbar(
              getDestinationNavbarIndex(navController.currentDestination?.route),
              {
                navController.navigate(OsmiumScreen.ServerList.name)
                while (navController.previousBackStackEntry != null)
                  navController.popBackStack()
              }, {}, {})
          }
        ) { _ ->
          Box(
            modifier = Modifier
              .background(MaterialTheme.colorScheme.background)
          ) {
            screen()
          }
        }
      }

      composable(route = OsmiumScreen.ServerList.name) {
        Route {
          ServersListScreen(
            navBack = { navController.navigateUp() },
            canNavBack = navController.previousBackStackEntry != null,
            navAddServer = { navController.navigate(OsmiumScreen.Other.name) },
          )
        }
      }
      composable(route = OsmiumScreen.Other.name) {
        Route {
          OtherScreen(
            navBack = { navController.navigateUp() },
            canNavBack = navController.previousBackStackEntry != null
          )
        }
      }
    }
  }
}