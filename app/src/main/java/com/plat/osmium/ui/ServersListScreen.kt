package com.plat.osmium.ui

import android.os.Build
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.plat.osmium.domain.FilePickerViewModel
import com.plat.osmium.ui.components.FilePicker
import com.plat.osmium.ui.components.ManageDirectoryDialog
import com.plat.osmium.ui.components.OsmiumTopBar
import com.plat.osmium.ui.components.SectionHeader
import com.plat.osmium.ui.components.hapticClick
import com.plat.osmium.ui.components.hapticConfirm
import com.plat.osmium.ui.components.hapticLongPress
import com.plat.osmium.ui.theme.TonalIconButton
import kotlin.apply

data class Directory(
  val name: String = "Default directory",
  val id: Int = 0
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

class ServersListServersViewModel : ViewModel() {
  private val _servers =
    listOf(
      RichServer(name = "Server 1"),
      RichServer(name = "Server 2", isOnline = false),
      RichServer(name = "Server 1"),
      RichServer(name = "Server 2", isOnline = false),
      RichServer(name = "Server 1"),
    )
  val servers: List<RichServer>
    get() = _servers
}

class ServersListDirectoriesViewModel : ViewModel() {
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

  enum class ManagementDialogState {
    Start,
    Edit,
    ConfirmDelete,
    ConfirmEdit,
    Closed,
  }

  val managementDialog = mutableStateOf(ManagementDialogState.Start)
  val dialogWhichDirectoryId = mutableIntStateOf(0)

  fun closeManagementDialog() {
    managementDialog.value = ManagementDialogState.Closed
  }

  fun openManagementDialog() {
    managementDialog.value = ManagementDialogState.Start
  }

  fun deleteCurrentDirectory() {
    val dir_id = dialogWhichDirectoryId
  }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServersListScreen(
  modifier: Modifier = Modifier,
  serversViewModel: ServersListServersViewModel = viewModel(),
  directoriesViewModel: ServersListDirectoriesViewModel = viewModel(),
  canNavBack: Boolean = true,
  navBack: () -> Unit = {},
  navServer: (Int) -> Unit = {},
  navDirectory: (Int) -> Unit = {},
  navAddServer: () -> Unit = {},
  navAddDirectory: () -> Unit = {},
) {
  val tmp: MutableState<String> = remember { mutableStateOf("") }

  // Directories management dialog
  when (directoriesViewModel.managementDialog.value) {
    ServersListDirectoriesViewModel.ManagementDialogState.Closed -> {
    }

    ServersListDirectoriesViewModel.ManagementDialogState.Start -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        FilePicker(
          onDismissRequest = { directoriesViewModel.closeManagementDialog() },
          onConfirm = { fileList -> if (fileList.isNotEmpty()) tmp.value = fileList[0].path },
          filePickerViewModel = viewModel(
            factory = FilePickerViewModel.Factory,
            extras = MutableCreationExtras().apply {
              set(
                FilePickerViewModel.TARGET_KEY,
                FilePickerViewModel.Target.MultiFile
              )
            })
        )
      }
    }

    else ->
      ManageDirectoryDialog(
        state = directoriesViewModel.managementDialog.value,
        onDismissRequest = {
          directoriesViewModel.closeManagementDialog()
        },
        onEdit = {
          directoriesViewModel.managementDialog.value =
            ServersListDirectoriesViewModel.ManagementDialogState.Edit
        },
        onDelete = {
          directoriesViewModel.managementDialog.value =
            ServersListDirectoriesViewModel.ManagementDialogState.ConfirmDelete
        },
        onDeleteConfirm = {
          directoriesViewModel.closeManagementDialog()
          directoriesViewModel.deleteCurrentDirectory()
        }
      )

  }


  // Screen body
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  Scaffold(
    modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
      OsmiumTopBar(
        navBack = navBack,
        title = stringResource(R.string.servers_header_text),
        canNavBack = canNavBack,
        scrollBehavior = scrollBehavior
      )
    }) { padding ->
    LazyColumn(Modifier.padding(padding)) {
      item(key = 10000) {
        Text(tmp.value)
      }

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
          onLongClick = hapticLongPress {}) // HERE <------------------------------------------
      }
      item(key = 9998) {
        Spacer(modifier = Modifier.height(20.dp))
      }
      item(key = 9997) {
        SectionHeader(
          stringResource(R.string.directories_header_text),
          onIconPress = hapticConfirm(navAddDirectory),
          iconResourceId = R.drawable.icon_add,
        )
      }
      items(directoriesViewModel.directories.size, { it }) { idx ->
        DirectoryListEntry(
          directoriesViewModel.directories[idx],
          onClick = hapticClick { navDirectory(directoriesViewModel.directories[idx].id) },
          onLongPress = hapticLongPress {
            directoriesViewModel.openManagementDialog()
            directoriesViewModel.dialogWhichDirectoryId.intValue =
              directoriesViewModel.directories[idx].id
          },
          onUploadClick = hapticConfirm {}) // HERE <------------------------------------------
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
  // DEPRECATED EDIT LATER <-------------------------------------------------
  ListItem(
    leadingContent = {
      Icon(
        painter = painterResource(id = if (serverData.isOnline) R.drawable.icon_link else R.drawable.icon_link_off),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(if (serverData.isOnline) R.string.server_online_desc else R.string.server_offline_desc),
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
        contentDescription = stringResource(R.string.directory_icon_desc),
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
          contentDescription = stringResource(R.string.upload_icon_desc),
        )
      }
    },
    modifier = modifier.combinedClickable(true, onClick = onClick, onLongClick = onLongPress),
  )
}
