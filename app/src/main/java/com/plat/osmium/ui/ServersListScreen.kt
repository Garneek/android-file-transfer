@file:Suppress("FunctionName")

package com.plat.osmium.ui

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plat.osmium.db.Directory
import com.plat.osmium.domain.RichServer
import com.plat.osmium.domain.ServersListDirectoriesViewModel
import com.plat.osmium.domain.ServersListServersViewModel
import com.plat.osmium.ui.components.ManageDirectoryDialog
import com.plat.osmium.ui.components.OsmiumTopBar
import com.plat.osmium.ui.components.SectionHeader
import com.plat.osmium.ui.components.hapticClick
import com.plat.osmium.ui.components.hapticConfirm
import com.plat.osmium.ui.components.hapticLongPress
import com.plat.osmium.utils.Common
import com.plat.osmium.utils.defaultDir
import com.plat.osmium.utils.pathFormatter
import kotlin.random.Random


@Composable
fun ServersListScreen(
  modifier: Modifier = Modifier,
  serversViewModel: ServersListServersViewModel = viewModel(),
  directoriesViewModel: ServersListDirectoriesViewModel =
    viewModel(
      factory = ServersListDirectoriesViewModel.Factory(Common.application.repository)
    ),
  canNavBack: Boolean = true,
  navBack: () -> Unit = {},
  navServer: (Int) -> Unit = {},
  navAddServer: () -> Unit = {},
  bottomNavbar: @Composable () -> Unit = {},
) {
  val dialogState = directoriesViewModel.managementDialog.collectAsState().value
  if (dialogState != ServersListDirectoriesViewModel.ManagementDialogState.Closed) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ManageDirectoryDialog(
        directoriesViewModel,
        onDismissRequest = {
          directoriesViewModel.setDialogState(ServersListDirectoriesViewModel.ManagementDialogState.Closed)
        }
      )
    }
  }

  // Screen body
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  Scaffold(
    modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      OsmiumTopBar(
        navBack = navBack,
        title = stringResource(R.string.servers_header_text),
        canNavBack = canNavBack,
        scrollBehavior = scrollBehavior
      )
    },
    bottomBar = bottomNavbar
  ) { padding ->

    val directories = directoriesViewModel.directories.collectAsState(listOf()).value
    LazyColumn(
      Modifier
        .padding(padding)
        .padding(vertical = 4.dp)
    ) {
      item(key = 9999) {
        SectionHeader(
          stringResource(R.string.servers_header_text),
          onIconPress = hapticConfirm(navAddServer),
          iconResourceId = R.drawable.icon_add,
        )
      }
      items(serversViewModel.servers.size, key = { it + 5000 }) { idx ->
        ServerListEntry(
          serversViewModel.servers[idx],
          onClick = hapticClick { navServer(serversViewModel.servers[idx].serverId) },
          onLongClick = hapticLongPress { directoriesViewModel.uploadFiles(idx) })

        Spacer(Modifier.height(1.dp))
      }
      item(key = 9998) {
        Spacer(modifier = Modifier.height(20.dp))
      }
      item(key = 9997) {
        SectionHeader(
          stringResource(R.string.directories_header_text),
          onIconPress = hapticConfirm {directoriesViewModel.addDirectory(Directory(path = defaultDir().path + "/" + Random.nextInt().toString()))}, // ----------------------------------
          iconResourceId = R.drawable.icon_add,
        )
      }
      items(directories.size, { it }) { idx ->
        DirectoryListEntry(
          directories[idx],
          onClick = hapticClick {
            directoriesViewModel.setDialogState(ServersListDirectoriesViewModel.ManagementDialogState.Start)
            directoriesViewModel.setDialogEntry(directories[idx])
          },
          onUploadClick = hapticConfirm { directoriesViewModel.uploadFiles(idx) })
        Spacer(Modifier.height(1.dp))
      }
    }
  }
}

@Composable
fun ServerListEntry(
  serverData: RichServer,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ListItem(
    elevation = ListItemDefaults.elevation(2.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .padding(horizontal = 8.dp)
      .combinedClickable(true, onClick = onClick, onLongClick = onLongClick),
    leadingContent = {
      Icon(
        painter = painterResource(id = if (serverData.isOnline) R.drawable.icon_link else R.drawable.icon_link_off),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(if (serverData.isOnline) R.string.server_online_desc else R.string.server_offline_desc),
      )
    },
    content = {
      Text(
        serverData.name,
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge,
      )
    },
    supportingContent = {
      Text(
        if (serverData.isOnline) stringResource(R.string.supporting_server_online) else stringResource(
          R.string.supporting_server_offline
        ),
        maxLines = 1,
        style = MaterialTheme.typography.bodyMedium,
      )
    },
  )
}

@Composable
fun DirectoryListEntry(
  dirData: Directory,
  onClick: () -> Unit,
  onUploadClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ListItem(
    elevation = ListItemDefaults.elevation(2.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier =
      modifier
        .padding(horizontal = 8.dp)
        .clickable(true, onClick = onClick),
    leadingContent = {
      Icon(
        painter = painterResource(id = R.drawable.icon_folder),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(R.string.directory_icon_desc),
      )
    },
    content = {
      Text(
        pathFormatter(dirData.path),
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge,
        softWrap = false
      )
    },
    trailingContent = {
      FilledTonalIconButton(
        onClick = onUploadClick, shape = ShapeDefaults.Medium
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_upload),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.upload_icon_desc),
        )
      }
    },
  )
}
