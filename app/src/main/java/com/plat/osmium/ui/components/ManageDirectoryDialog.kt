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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.R
import com.plat.osmium.domain.FilePickerViewModel
import com.plat.osmium.domain.DirectoriesListViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ManageDirectoryDialog(
  directoryViewModel: DirectoriesListViewModel,
  onDismissRequest: () -> Unit,
) {
  when (val dialogState = directoryViewModel.managementDialog.collectAsState().value) {
    DirectoriesListViewModel.ManagementDialogState.Start, DirectoriesListViewModel.ManagementDialogState.ConfirmDelete -> DialogBase(
      onDismissRequest
    ) {
      AnimatedVisibility(
        dialogState == DirectoriesListViewModel.ManagementDialogState.Start,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        StartDialog(onEdit = {
          directoryViewModel.setDialogState(DirectoriesListViewModel.ManagementDialogState.Edit)
        }, onDelete = {
          directoryViewModel.setDialogState(DirectoriesListViewModel.ManagementDialogState.ConfirmDelete)
        })
      }
      AnimatedVisibility(
        dialogState != DirectoriesListViewModel.ManagementDialogState.Start,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        ConfirmDeletionDialog(
          onDismissRequest,
          onConfirmDelete = {
            directoryViewModel.deleteCurrentDirectory()
            onDismissRequest()
          },
          title = stringResource(R.string.popup_dir_confirm_delete_title),
          text = stringResource(R.string.popup_dir_confirm_delete_text),
          )
      }
    }


    DirectoriesListViewModel.ManagementDialogState.Edit -> FilePicker(
      onDismissRequest = {
        directoryViewModel.setDialogState(DirectoriesListViewModel.ManagementDialogState.Start)
      },
      onConfirm = { fileList ->
        if (fileList.isNotEmpty()) directoryViewModel.updateDirectory(
          fileList[0]
        )
      },
      filePickerViewModel = viewModel(
        factory = FilePickerViewModel.Factory(FilePickerViewModel.Target.Directory)
      ),
    )


    else -> {}
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
        stringResource(R.string.edit), color = MaterialTheme.colorScheme.secondary, maxLines = 1
      )
    }
    Spacer(Modifier.width(16.dp))
    TextButton(onClick = hapticClick(onDelete)) {
      Text(
        stringResource(R.string.delete), maxLines = 1
      )
    }
  }
}

