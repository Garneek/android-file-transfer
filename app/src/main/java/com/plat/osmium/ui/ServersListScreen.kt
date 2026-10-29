package com.plat.osmium.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.material.divider.MaterialDivider
import com.plat.osmium.ui.theme.TonalIconButton

data class Directory(
  val name: String = "Default directory"
)

data class RichServer(
  val serverId: Int = 0,
  val name: String = "Default server",
  val host: String = "localhost",
  val port: Int = 0,
  val password: String = "",
  val downloadDirectory: String = "",
  val isOnline: Boolean = true,
)

class ServersListViewModel : ViewModel() {
  private val _servers =
    listOf(RichServer(name = "Server 1"), RichServer(name = "Server 2", isOnline = false))
  val servers: List<RichServer>
    get() = _servers

  private val _directories = listOf(
    Directory(),
    Directory("Dir2"),
    Directory("Dir3"),
    Directory("Dir4"),
    Directory("Dir5"),
    Directory("Dir6"),
    Directory("Dir7"),
    Directory("Dir8")
  )
  val directories: List<Directory>
    get() = _directories
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersListScreen(
  modifier: Modifier = Modifier,
  serversListViewModel: ServersListViewModel = viewModel(),
) {
  Scaffold(
    modifier.fillMaxSize(), topBar = {
      TopAppBar(title = {
        Text(
          text = "Profile name placeholder",
          modifier = Modifier.padding(PaddingValues(horizontal = 8.dp))
        )
      }, navigationIcon = {
        IconButton(onClick = hapticConfirm {}) {
          Icon(
            painter = painterResource(R.drawable.icon_back),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.default_icon_button_description),
          )
        }
      })
    }) { padding ->
    LazyColumn(Modifier.padding(padding)) {
      item {
        SectionHeader(
          stringResource(R.string.servers_header_text),
          onIconPress = hapticConfirm {},
          iconResourceId = R.drawable.icon_add,
        )
      }
      items(serversListViewModel.servers.size) { idx ->
        ServerListEntry(
          serversListViewModel.servers[idx],
          onClick = hapticClick {},
          onLongClick = hapticLongPress {})
      }
      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
      item {
        SectionHeader(
          stringResource(R.string.directories_header_text),
          onIconPress = hapticConfirm {},
          iconResourceId = R.drawable.icon_add,
        )
      }
      items(serversListViewModel.directories.size) { idx ->
        DirectoryListEntry(
          serversListViewModel.directories[idx],
          onClick = hapticClick {},
          onLongPress = hapticLongPress {},
          onUploadClick = hapticConfirm {})
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
    leadingContent = {
      Icon(
        painter = painterResource(id = if (serverData.isOnline) R.drawable.icon_link else R.drawable.icon_link_off),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(R.string.default_icon_button_description),
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
    headlineContent = {
      Text(
        serverData.name,
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge,
      )
    },
    modifier = modifier.combinedClickable(true, onClick = onClick, onLongClick = onLongClick),
  )
}

@Composable
fun DirectoryListEntry(
  dirData: Directory,
  onClick: () -> Unit,
  onLongPress: () -> Unit,
  onUploadClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptics = LocalHapticFeedback.current
  ListItem(
    leadingContent = {
      Icon(
        painter = painterResource(id = R.drawable.icon_folder),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(R.string.default_icon_button_description),
      )
    },
    headlineContent = {
      Text(
        dirData.name,
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge,
      )
    },
    trailingContent = {
      IconButton(
        onClick = onUploadClick, colors = TonalIconButton(), shape = ShapeDefaults.Medium
      ) {
        Icon(
          painter = painterResource(id = R.drawable.icon_upload),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.default_icon_button_description),
        )
      }
    },
    modifier = modifier.combinedClickable(true, onClick = onClick, onLongClick = onLongPress),
  )
}
