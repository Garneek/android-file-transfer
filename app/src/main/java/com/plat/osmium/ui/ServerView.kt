package com.plat.osmium.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.plat.osmium.R
import com.plat.osmium.domain.ServerDetailsViewModel
import com.plat.osmium.ui.components.OsmiumTopBar
import com.plat.osmium.ui.components.hapticClick

@Composable
fun ServerView(
  serverViewModel: ServerDetailsViewModel,
  modifier: Modifier = Modifier,
  canNavBack: Boolean = true,
  navBack: () -> Unit = {},
  navUpload: () -> Unit = {},
  navServerSettings: () -> Unit = {},
  bottomNavbar: @Composable () -> Unit = {},
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  val server = serverViewModel.server.collectAsState(null).value
  Scaffold(
    modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      val subtitle = (server?.host ?: "Loading") + ":" + (server?.port ?: "?")
      OsmiumTopBar(
        navBack = navBack,
        title = server?.name ?: "Loading",
        subtitle = subtitle,
        canNavBack = canNavBack,
        scrollBehavior = scrollBehavior,
        actions = {
          IconButton(onClick = hapticClick ( navUpload )) {
            Icon(
              painter = painterResource(R.drawable.icon_upload),
              tint = MaterialTheme.colorScheme.primary,
              contentDescription = stringResource(R.string.upload_icon_desc),
            )
          }
          IconButton(onClick = hapticClick(navServerSettings)) {
            Icon(
              painter = painterResource(R.drawable.icon_settings),
              tint = MaterialTheme.colorScheme.primary,
              contentDescription = stringResource(R.string.server_settings),
            )
          }
        }
      )
    },
    bottomBar = bottomNavbar
  ) { padding ->


  }

}
