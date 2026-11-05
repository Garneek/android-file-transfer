package com.plat.osmium.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.plat.osmium.R


@Composable
fun OsmiumBottomNavbar(
  selectedN: Int,
  navServer: () -> Unit,
  navTransfers: () -> Unit,
  navSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
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
      selected = selectedN == 0,
      onClick = hapticConfirm(navServer)
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
      selected = selectedN == 1,
      onClick = hapticConfirm(navTransfers)
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
      selected = selectedN == 2,
      onClick = hapticConfirm(navSettings)
    )
  }
}