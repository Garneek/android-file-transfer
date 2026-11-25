package com.plat.osmium.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plat.osmium.R
import com.plat.osmium.db.OsmiumRepository
import com.plat.osmium.db.Server
import com.plat.osmium.utils.defaultDir
import com.plat.osmium.utils.formatPath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AddServerViewModel(private val repository: OsmiumRepository) : ViewModel() {

  private val _server: MutableStateFlow<Server> = MutableStateFlow(Server())
  val server: StateFlow<Server> = _server.asStateFlow()

  private val _passwordHidden: MutableStateFlow<Boolean> = MutableStateFlow(true)
  val passwordHidden: StateFlow<Boolean> = _passwordHidden.asStateFlow()

  private val _folderPickerOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val folderPickerOpen: StateFlow<Boolean> = _folderPickerOpen.asStateFlow()

  private val _directory: MutableStateFlow<String> =
    MutableStateFlow(server.value.directory ?: (defaultDir().path + "/Download"))
  val directory: StateFlow<String> = _directory.asStateFlow()

  private val _saving: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val saving: StateFlow<Boolean> = _saving.asStateFlow()

  private val _error: MutableStateFlow<Int?> =
    MutableStateFlow(null)
  val error: StateFlow<Int?> = _error.asStateFlow()

  val nameTfState: TextFieldState = TextFieldState(server.value.name)
  val ipTfState: TextFieldState = TextFieldState(server.value.host)
  val portTfState: TextFieldState = TextFieldState(server.value.port.toString())
  val passwordTfState: TextFieldState = TextFieldState(server.value.password)
  val directoryTfState: TextFieldState = TextFieldState(directory.value.formatPath())


  fun save(navBack: () -> Unit) = viewModelScope.launch {
    _saving.value = true
    var save = true

    fun makeError(code: Int) {
      save = false
      _error.value = code
    }

    val name = nameTfState.text.toString()
    if (name.length < 3)
      makeError(R.string.error_short_server_name)

    val host = ipTfState.text.toString()
    if (host.length < 3)
      makeError(R.string.error_host_not_valid)

    var port: Int = 0
    try {
      port = portTfState.text.toString().toInt()
    } catch (e: Exception) {
      makeError(R.string.error_port_number)
    }
    if (port !in 1..<65536)
      makeError(R.string.error_port_not_valid)

    val password = passwordTfState.text.toString()
    val directory = directory.value
    if (!File(directory).exists())
      makeError(R.string.error_directory_doesnt_exist)

    if (save) {
      val server = Server(
        name = name,
        host = host,
        port = port,
        password = password,
        directory = directory,
      )
      repository.insertServer(server)
      _saving.value = false
      navBack()
    } else
      _saving.value = false
  }

  fun clearName() {
    nameTfState.clearText()
  }

  fun clearIp() {
    ipTfState.clearText()
  }

  fun clearPort() {
    ipTfState.clearText()
  }

  fun clearPassword() {
    passwordTfState.clearText()
  }

  fun swapHiddenState() {
    _passwordHidden.value = !passwordHidden.value
  }

  fun openFolderPicker() {
    _folderPickerOpen.value = true
  }

  fun closeFolderPicker() {
    _folderPickerOpen.value = false
  }

  fun setDirPath(dir: String) {
    _directory.value = dir
    directoryTfState.setTextAndPlaceCursorAtEnd(directory.value.formatPath())
  }

  fun resetError() {
    _error.value = null
  }

  class Factory(private val repository: OsmiumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(AddServerViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return AddServerViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}