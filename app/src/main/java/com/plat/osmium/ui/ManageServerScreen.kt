package com.plat.osmium.ui

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.byValue
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.plat.osmium.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plat.osmium.domain.ManageServerViewModel
import com.plat.osmium.domain.FilePickerViewModel
import com.plat.osmium.ui.components.ConfirmDeletionDialog
import com.plat.osmium.ui.components.ErrorDialog
import com.plat.osmium.ui.components.FilePicker
import com.plat.osmium.ui.components.OsmiumTopBar
import com.plat.osmium.ui.components.hapticClick
import com.plat.osmium.ui.components.hapticConfirm
import com.plat.osmium.utils.Common

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ManageServerScreen(
  modifier: Modifier = Modifier,
  manageServerViewModel: ManageServerViewModel = viewModel(
    factory = ManageServerViewModel.Factory(Common.application.repository)
  ),
  canNavBack: Boolean = true,
  navBack: () -> Unit = {},
  navHome: () -> Unit = {},
  bottomNavbar: @Composable () -> Unit = {},
) {
  val editMode = manageServerViewModel.editMode.collectAsState().value
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  Scaffold(
    modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
      OsmiumTopBar(
        navBack = navBack,
        title = stringResource(if (editMode) R.string.edit_server_header else R.string.add_server_header),
        subtitle = if (editMode) manageServerViewModel.startingSubtitle.collectAsState().value else null,
        canNavBack = canNavBack,
        actions = {
          if (editMode)
            IconButton(onClick = hapticClick { manageServerViewModel.askForDeletion() }) {
              Icon(
                painter = painterResource(id = R.drawable.icon_delete),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.delete),
              )
            }

          FilledTonalIconButton(onClick = hapticConfirm { manageServerViewModel.save(navBack) }) {
            Icon(
              painter = painterResource(id = R.drawable.icon_check),
              tint = MaterialTheme.colorScheme.primary,
              contentDescription = stringResource(R.string.confirm),
            )
          }
        })
    }, bottomBar = bottomNavbar
  ) { padding ->
    val error = manageServerViewModel.error.collectAsState().value
    if (manageServerViewModel.folderPickerOpen.collectAsState().value) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        FilePicker(
          onDismissRequest = {
            manageServerViewModel.closeFolderPicker()
          },
          onConfirm = { fileList ->
            if (fileList.isNotEmpty())
              manageServerViewModel.setDirPath(fileList[0].path)
          },
          filePickerViewModel = viewModel(
            factory = FilePickerViewModel.Factory(FilePickerViewModel.Target.Directory)
          )
        )
      }
    } else if (manageServerViewModel.saving.collectAsState().value)
      Dialog(onDismissRequest = {}) {
        ContainedLoadingIndicator(
          modifier = Modifier
            .width(128.dp)
            .height(128.dp)
        )
      }
    else {
      if (error != null)
        ErrorDialog(onDismissRequest = { manageServerViewModel.resetError() }, stringResource(error))
      else if (manageServerViewModel.deleteConfirm.collectAsState().value) {
        ConfirmDeletionDialog(
          onDismissRequest = { manageServerViewModel.cancelDeletion() },
          onConfirmDelete = { manageServerViewModel.delete(navHome) },
          title = stringResource(R.string.dialog_delete_confirm_title),
          text = stringResource(R.string.dialog_delete_confirm_text),
        )
      }
      LazyColumn(
        Modifier
          .padding(padding)
          .padding(vertical = 4.dp, horizontal = 32.dp)
      ) {
        item {
          EditTextField(
            manageServerViewModel.nameTfState,
            stringResource(R.string.add_server_name),
            onClear = { manageServerViewModel.clearName() },
            modifier = Modifier.padding(top = 16.dp)
          )
        }
        item {
          EditTextField(
            manageServerViewModel.ipTfState,
            stringResource(R.string.add_server_ip),
            onClear = { manageServerViewModel.clearIp() },
            keyboardType = KeyboardType.Uri
          )
        }
        item {
          EditTextField(
            manageServerViewModel.portTfState,
            stringResource(R.string.add_server_port),
            onClear = { manageServerViewModel.clearPort() },
            keyboardType = KeyboardType.Number
          )
        }
        item {
          SecureEditTextField(
            manageServerViewModel.passwordTfState,
            stringResource(R.string.add_server_password),
            onClear = { manageServerViewModel.clearPassword() },
            last = true,
            passwordHidden = manageServerViewModel.passwordHidden.collectAsState().value,
            swapPasswordHidden = { manageServerViewModel.swapHiddenState() }
          )
        }

        item {
          OutlinedTextField(
            modifier = modifier
              .fillMaxWidth()
              .padding(bottom = 16.dp),
            readOnly = true,
            state = manageServerViewModel.directoryTfState,
            label = { Text(stringResource(R.string.add_server_directory)) },
            trailingIcon = {
              IconButton(onClick = {
                manageServerViewModel.openFolderPicker()
              }) {
                Icon(
                  painter = painterResource(id = R.drawable.icon_edit),
                  tint = MaterialTheme.colorScheme.primary,
                  contentDescription = stringResource(R.string.edit),
                )
              }
            },
            shape = OutlinedTextFieldDefaults.roundedShape,
            lineLimits = TextFieldLineLimits.SingleLine
          )
        }
      }
    }

  }
}

@Composable
fun EditTextField(
  state: TextFieldState,
  label: String,
  onClear: () -> Unit,
  modifier: Modifier = Modifier,
  keyboardType: KeyboardType = KeyboardType.Text,
  last: Boolean = false,
) {
  OutlinedTextField(
    modifier = modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp),
    state = state,
    label = { Text(text = label) },
    trailingIcon = {
      IconButton(onClick = onClear, modifier = Modifier.visible(state.text.isNotBlank())) {
        Icon(
          painter = painterResource(id = R.drawable.icon_close),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.clear),
        )
      }
    },
    keyboardOptions = KeyboardOptions(
      imeAction = if (!last) ImeAction.Next else ImeAction.Done,
      keyboardType = keyboardType,
      capitalization = if (keyboardType == KeyboardType.Text) KeyboardCapitalization.Words
      else KeyboardCapitalization.None
    ),
    inputTransformation = if (keyboardType == KeyboardType.Number) InputTransformation.byValue { _, proposed -> proposed.filter { it.isDigit() } } else null,
    lineLimits = TextFieldLineLimits.SingleLine,
    shape = OutlinedTextFieldDefaults.roundedShape,
  )
}

@Composable
fun SecureEditTextField(
  state: TextFieldState,
  label: String,
  onClear: () -> Unit,
  modifier: Modifier = Modifier,
  passwordHidden: Boolean = true,
  swapPasswordHidden: () -> Unit = {},
  last: Boolean = false,
) {
  OutlinedSecureTextField(
    modifier = modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp),
    state = state,
    label = { Text(text = label) },
    trailingIcon = {
      IconButton(onClick = onClear, modifier = Modifier.visible(state.text.isNotBlank())) {
        Icon(
          painter = painterResource(id = R.drawable.icon_close),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.clear),
        )
      }
    },
    keyboardOptions = KeyboardOptions(
      imeAction = if (!last) ImeAction.Next else ImeAction.Done,
      keyboardType = if (passwordHidden) KeyboardType.Password else KeyboardType.PasswordVisible,
      capitalization = KeyboardCapitalization.None,
      autoCorrectEnabled = false
    ),
    leadingIcon = {
      IconButton(onClick = hapticClick(swapPasswordHidden)) {
        Icon(
          painter = painterResource(if (passwordHidden) R.drawable.icon_vis else R.drawable.icon_vis_off),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(R.string.clear),
        )
      }
    },
    textObfuscationMode = if (passwordHidden) TextObfuscationMode.System else TextObfuscationMode.Visible,
    textObfuscationCharacter = '●',
    shape = OutlinedTextFieldDefaults.roundedShape,
  )
}