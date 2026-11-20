package com.plat.osmium.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.R
import com.plat.osmium.domain.FilePickerViewModel
import com.plat.osmium.domain.ServersListDirectoriesViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ManageDirectoryDialog(
  directoryViewModel: ServersListDirectoriesViewModel,
  onDismissRequest: () -> Unit,
) {
  when (val dialogState = directoryViewModel.managementDialog.collectAsState().value) {
    ServersListDirectoriesViewModel.ManagementDialogState.Start,
    ServersListDirectoriesViewModel.ManagementDialogState.ConfirmDelete ->
      DialogBase(onDismissRequest) {
        AnimatedVisibility(
          dialogState == ServersListDirectoriesViewModel.ManagementDialogState.Start,
          enter = fadeIn(),
          exit = fadeOut(),
        ) {
          StartDialog(
            onEdit = {
              directoryViewModel.setDialogState(ServersListDirectoriesViewModel.ManagementDialogState.Edit)
            },
            onDelete = {
              directoryViewModel.setDialogState(ServersListDirectoriesViewModel.ManagementDialogState.ConfirmDelete)
            })
        }
        AnimatedVisibility(
          dialogState != ServersListDirectoriesViewModel.ManagementDialogState.Start,
          enter = fadeIn(),
          exit = fadeOut(),
        ) {
          ConfirmDeleteDialog(onDismissRequest, onDeleteConfirm = {
            directoryViewModel.deleteCurrentDirectory()
            onDismissRequest()
          })
        }
      }


    ServersListDirectoriesViewModel.ManagementDialogState.Edit ->
      FilePicker(
        onDismissRequest = {
          directoryViewModel.setDialogState(ServersListDirectoriesViewModel.ManagementDialogState.Start)
        },
        onConfirm = { fileList ->
          if (fileList.isNotEmpty()) directoryViewModel.updateDirectory(
            fileList[0]
          )
        },
        filePickerViewModel = viewModel(
          factory = FilePickerViewModel.Factory,
          extras = MutableCreationExtras().apply {
            set(
              FilePickerViewModel.TARGET_KEY,
              FilePickerViewModel.Target.Directory
            )
          })
      )


    else -> {}
  }
}

@Composable
fun DialogBase(
  onDismissRequest: () -> Unit,
  content: @Composable () -> Unit,
) {
  Dialog(onDismissRequest = { onDismissRequest() }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp)
    ) { content() }
  }
}

@Composable
private fun StartDialog(
  onEdit: () -> Unit = {},
  onDelete: () -> Unit = {},
) {
  DialogContent(
    stringResource(R.string.popup_dir_manage_title),
    stringResource(R.string.popup_dir_manage_text),
  ) {
    TextButton(onClick = hapticClick(onEdit)) {
      Text(
        stringResource(R.string.edit),
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 1
      )
    }
    Spacer(Modifier.width(16.dp))
    TextButton(onClick = hapticClick(onDelete)) {
      Text(
        stringResource(R.string.delete),
        maxLines = 1
      )
    }
  }
}

@Composable
private fun ConfirmDeleteDialog(
  onDismissRequest: () -> Unit = {},
  onDeleteConfirm: () -> Unit = {},
) {
  DialogContent(
    stringResource(R.string.popup_dir_confirm_delete_title),
    stringResource(R.string.popup_dir_confirm_delete_text),
  ) {
    TextButton(onClick = hapticClick(onDismissRequest)) {
      Text(
        stringResource(R.string.cancel),
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 1
      )
    }
    Spacer(Modifier.width(16.dp))
    TextButton(onClick = hapticConfirm(onDeleteConfirm)) {
      Text(
        stringResource(R.string.delete),
        maxLines = 1
      )
    }
  }
}

@Composable
private fun DialogContent(
  title: String,
  text: String,
  buttons: @Composable () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxHeight(),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column() {
      Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
      )

      Text(
        text = text,
        modifier = Modifier
          .wrapContentSize(Alignment.Center)
          .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyMedium,
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.End
    ) {
      buttons()
    }
  }
}


