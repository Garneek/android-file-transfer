package com.plat.osmium.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plat.osmium.R
import com.plat.osmium.domain.ManageServerViewModel
import com.plat.osmium.domain.ServerDetailsViewModel
import com.plat.osmium.ui.components.ErrorDialog
import com.plat.osmium.ui.components.OsmiumBottomNavbar

import com.plat.osmium.ui.theme.OsmiumTheme
import com.plat.osmium.utils.Common
import kotlinx.coroutines.flow.MutableStateFlow

enum class OsmiumScreen {
  ServerList,
  AddServer,
  ServerDetails,
  ServerSettings,
  Other,
}

// Which bottom navbar item does the screen belong to
// 0 - Servers
// 1 - Transfers
// 2 - Settings
fun getDestinationNavbarIndex(screen: String?): Int {
  return when (screen) {
    OsmiumScreen.ServerList.name -> 0
    OsmiumScreen.AddServer.name -> 0
    OsmiumScreen.ServerDetails.name -> 0
    OsmiumScreen.ServerSettings.name -> 0
    OsmiumScreen.Other.name -> 2
    else -> 0
  }
}

@Composable
fun OsmiumApp(navController: NavHostController = rememberNavController()) {
  OsmiumTheme {
    NavHost(
      navController = navController,
      startDestination = OsmiumScreen.ServerList.name,
    ) {
      // possibly sus
      val serverId: MutableStateFlow<Int?> = MutableStateFlow(null)

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
          navServer = { id ->
            serverId.value = id
            navController.navigate(OsmiumScreen.ServerDetails.name)
          },
          navAddServer = { navController.navigate(OsmiumScreen.AddServer.name) },
          bottomNavbar = { BottomNavbar() }
        )
      }

      composable(route = OsmiumScreen.AddServer.name) {
        ManageServerScreen(
          navBack = { navController.navigateUp() },
          canNavBack = navController.previousBackStackEntry != null,
          bottomNavbar = { BottomNavbar() }
        )
      }

      composable(route = OsmiumScreen.ServerDetails.name) {
        val sid = serverId.collectAsState().value
        if (sid != null)
          ServerView(
            serverViewModel = viewModel(
              factory = ServerDetailsViewModel.Factory(
                serverId = sid,
                repository = Common.application.repository
              )
            ),
            navBack = {
              navController.navigateUp()
            },
            navServerSettings = { navController.navigate(OsmiumScreen.ServerSettings.name) },
            canNavBack = navController.previousBackStackEntry != null,
            bottomNavbar = { BottomNavbar() }
          )
        else
          ErrorDialog(
            { navController.navigateUp() },
            error = stringResource(R.string.error_no_server_chosen)
          )
      }

      composable(route = OsmiumScreen.ServerSettings.name) {
        val sid = serverId.collectAsState().value
        if (sid != null)
          ManageServerScreen(
            navBack = { navController.navigateUp() },
            canNavBack = navController.previousBackStackEntry != null,
            bottomNavbar = { BottomNavbar() },
            manageServerViewModel = viewModel(
              factory = ManageServerViewModel.Factory(Common.application.repository, sid)
            ),
            navHome = {
              navController.navigate(OsmiumScreen.ServerList.name)
              while (navController.previousBackStackEntry != null)
                navController.popBackStack()
            }
          )
        else
          ErrorDialog(
            { navController.navigateUp() },
            error = stringResource(R.string.error_no_server_chosen)
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