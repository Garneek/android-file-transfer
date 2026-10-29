package com.plat.osmium.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.plat.osmium.R

import com.plat.osmium.ui.theme.OsmiumTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OsmiumApp() {
  OsmiumTheme {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = ::OsmiumBottomNavbar
    ) { _ ->
      Box(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.background)
      ) {
        ServersListScreen()
      }
    }
  }

}

@Composable
private fun OsmiumBottomNavbar(modifier: Modifier = Modifier) {
  var chosen by rememberSaveable { mutableIntStateOf(0) }

  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surfaceVariant,
    modifier = modifier
  ) {
    NavigationBarItem(
      icon = {
        Icon(
          painter = painterResource(id = R.drawable.icon_list),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.navbar_servers_desc),
        )
      },
      label = {
        Text(stringResource(R.string.navbar_servers))
      },
      selected = chosen == 0,
      onClick = hapticConfirm { chosen = 0 }
    )
    NavigationBarItem(
      icon = {
        Icon(
          painter = painterResource(id = R.drawable.icon_upload),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.navbar_transfers_desc),
        )
      },
      label = {
        Text(stringResource(R.string.navbar_transfers))
      },
      selected = chosen == 1,
      onClick = hapticConfirm { chosen = 1 }
    )
    NavigationBarItem(
      icon = {
        Icon(
          painter = painterResource(id = R.drawable.icon_list),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.navbar_settings_desc),
        )
      },
      label = {
        Text(stringResource(R.string.navbar_settings))
      },
      selected = chosen == 2,
      onClick = hapticConfirm { chosen = 2 }
    )
  }
}