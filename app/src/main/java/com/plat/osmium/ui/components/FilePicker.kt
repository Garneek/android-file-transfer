package com.plat.osmium.ui.components

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.R
import com.plat.osmium.domain.FilePickerViewModel
import com.plat.osmium.domain.getFileSize
import com.plat.osmium.utils.PermissionRequester
import com.plat.osmium.utils.fileSizeFormatter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.google.android.material.transition.MaterialContainerTransform
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FilePicker(
  onDismissRequest: () -> Unit,
  onConfirm: (List<File>) -> Unit = { _ -> onDismissRequest() },
  filePickerViewModel: FilePickerViewModel = viewModel(),
) {
  PermissionRequester(PermissionRequester.RequestedValues.Any).apply {
    add(Manifest.permission.READ_MEDIA_VIDEO)
    add(Manifest.permission.READ_MEDIA_AUDIO)
    add(Manifest.permission.READ_MEDIA_IMAGES)
  }.request()

  val odr = {
    filePickerViewModel.resetDirectory()
    onDismissRequest()
  }

  Dialog(
    onDismissRequest = odr,
    properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
  ) {
    val selectMode = remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
      Column(
        modifier = Modifier.fillMaxWidth()
      ) {
        FileTopBar(
          target = filePickerViewModel.target,
          directoryName = filePickerViewModel.getDirectoryName(),
          selectMode = selectMode.value,
          nSelected =
            if (filePickerViewModel.target == FilePickerViewModel.Target.MultiFile)
              filePickerViewModel.fileList.collectAsState().value.current().fold(0)
              { n, pair ->
                if (pair.second) n + 1
                else n
              }
            else 0,
          onCloseClick = {
            selectMode.value = false
            filePickerViewModel.clearSelections()
          },
          onMoveUpClick = { filePickerViewModel.moveUp() },
          onDismissRequest = odr,
          onConfirm = {
            onConfirm(filePickerViewModel.getResult())
            odr()
          }
        )
      }
    }
    ) { paddingValues ->
      val fileList = filePickerViewModel.fileList.collectAsState().value
      val files1 = fileList.list1()
      val files2 = fileList.list2()
      val useFile1 = fileList.currentList1

      Box() {
        FileList(
          useFile1,
          filePickerViewModel,
          modifier = Modifier.padding(paddingValues),
          selectMode = selectMode.value,
          enableSelectMode = { selectMode.value = true },
          files = files1,
        )
        FileList(
          !useFile1,
          filePickerViewModel,
          modifier = Modifier.padding(paddingValues),
          selectMode = selectMode.value,
          enableSelectMode = { selectMode.value = true },
          files = files2,
        )
      }
    }
  }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FileList(
  visible: Boolean,
  filePickerViewModel: FilePickerViewModel,
  modifier: Modifier = Modifier,
  selectMode: Boolean = false,
  enableSelectMode: () -> Unit = {},
  files: List<Pair<File, Boolean>> = listOf(),
) {
  AnimatedVisibility(
    visible,
    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
    exit = fadeOut(),
  ) {
    LazyColumn(
      modifier = modifier
    ) {
      items(files.size) { idx ->
        val file = files[idx]
        val selectable = filePickerViewModel.checkFileMatches(file.first)
        FileListItem(
          file = file.first,
          selected = filePickerViewModel.isSelected(
            idx,
            filePickerViewModel.selectedIdx.collectAsState().value
          ),
          selectable = selectable,
          selectMode = selectMode,
          target = filePickerViewModel.target,
          onFileSelect = { filePickerViewModel.selectFile(idx) },
          navigateTo = { filePickerViewModel.goToDirectory(file.first) },
          onLongClick = {
            if (!selectMode && selectable) {
              enableSelectMode()
              filePickerViewModel.selectFile(idx)
            }
          },
          onSetSelection = { v -> filePickerViewModel.setSelectionFile(idx, v) }
        )
        HorizontalDivider(thickness = 1.dp)
      }
    }
  }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun FileListItem(
  file: File,
  selected: Boolean,
  selectable: Boolean,
  selectMode: Boolean,
  target: FilePickerViewModel.Target,
  onFileSelect: () -> Unit,
  navigateTo: () -> Unit,
  onLongClick: () -> Unit,
  onSetSelection: (Boolean) -> Unit
) {
  val colors = if (selected && selectMode)
    ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
  else
    ListItemDefaults.colors()

  ListItem(
    leadingContent = {
      ImageLoader(
        file,
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(16.dp)),
      )
    },
    headlineContent = {
      Text(text = file.name, maxLines = 1)
    },
    supportingContent = {
      if (file.isFile)
        Text(text = fileSizeFormatter(getFileSize(file)))
    },
    modifier = Modifier
      .combinedClickable(
        true,
        onClick = hapticClick {
          if (selectMode && selectable) {
            when (target) {
              FilePickerViewModel.Target.Directory,
              FilePickerViewModel.Target.File -> onFileSelect()

              FilePickerViewModel.Target.MultiFile -> onSetSelection(!selected)
            }
          } else if (!selectMode) navigateTo()
        },
        onLongClick = hapticLongPress {
          onLongClick()
        }
      ),
    colors = colors,
    trailingContent = {
      val haptics = LocalHapticFeedback.current
      val audioManager =
        LocalContext.current.getSystemService(Context.AUDIO_SERVICE) as AudioManager

      if (selectMode && selectable) {
        when (target) {
          FilePickerViewModel.Target.Directory, FilePickerViewModel.Target.File -> {
            RadioButton(
              selected = selected,
              onClick = hapticClick { onFileSelect() }
            )
          }

          FilePickerViewModel.Target.MultiFile -> {
            Checkbox(
              checked = selected,
              onCheckedChange = { v ->
                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
                haptics.performHapticFeedback(
                  if (selected) HapticFeedbackType.ToggleOn
                  else HapticFeedbackType.ToggleOff
                )
                onSetSelection(v)
              }
            )
          }
        }
      }
    })
}

@Composable
private fun FileTopBar(
  target: FilePickerViewModel.Target,
  directoryName: String,
  selectMode: Boolean,
  nSelected: Int,
  onCloseClick: () -> Unit,
  onMoveUpClick: () -> Unit,
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit
) {
  TopAppBar(
    title = {
      MaterialContainerTransform()
      Column {
        Text(
          text = stringResource(target.resId) + ": " + directoryName,
          style = MaterialTheme.typography.titleLarge,
          maxLines = 1,
          softWrap = false,
          modifier = Modifier.padding(end = 16.dp)
        )
        if (selectMode && target == FilePickerViewModel.Target.MultiFile)
          Text(
            text = stringResource(R.string.n_selected) + nSelected,
            style = MaterialTheme.typography.labelMedium,
          )
      }
    },
    navigationIcon = {
      if (directoryName != "Root" || selectMode)
        IconButton(
          onClick = hapticClick {
            if (selectMode)
              onCloseClick()
            else
              onMoveUpClick()
          },
        ) {
          Icon(
            painter = painterResource(
              if (selectMode) R.drawable.icon_close else R.drawable.icon_up
            ),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.nav_back),
          )
        }
    },
    actions = {
      if (selectMode)
        FilledIconButton(
          onClick = hapticConfirm { onConfirm() },
          modifier = Modifier.padding(4.dp)
        ) {
          Icon(
            painterResource(R.drawable.icon_check),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = stringResource(R.string.confirm)
          )
        }
      else IconButton(
        onClick = hapticReject(onDismissRequest),
        modifier = Modifier.padding(4.dp)
      ) {
        Icon(
          painterResource(R.drawable.icon_close),
          tint = MaterialTheme.colorScheme.onSurface,
          contentDescription = stringResource(R.string.close)
        )
      }
    }
  )
}