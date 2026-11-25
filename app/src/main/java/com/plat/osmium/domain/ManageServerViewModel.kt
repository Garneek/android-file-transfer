package com.plat.osmium.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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

class ManageServerViewModel(
  private val repository: OsmiumRepository,
  private val serverId: Int?
) : ViewModel() {
  private val _editMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val editMode: StateFlow<Boolean> = _editMode.asStateFlow()

  private val _deleteConfirm: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val deleteConfirm: StateFlow<Boolean> = _deleteConfirm.asStateFlow()

  private val _startingSubtitle: MutableStateFlow<String> = MutableStateFlow("")
  val startingSubtitle: StateFlow<String> = _startingSubtitle.asStateFlow()

  private val _directory: MutableStateFlow<String> =
    MutableStateFlow(defaultDir().path + "/Download")
  val directory: StateFlow<String> = _directory.asStateFlow()

  val nameTfState: TextFieldState = TextFieldState("Default server")
  val ipTfState: TextFieldState = TextFieldState("localhost")
  val portTfState: TextFieldState = TextFieldState("6767")
  val passwordTfState: TextFieldState = TextFieldState("")
  val directoryTfState: TextFieldState = TextFieldState(directory.value.formatPath())

  private val _folderPickerOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val folderPickerOpen: StateFlow<Boolean> = _folderPickerOpen.asStateFlow()

  private val _saving: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val saving: StateFlow<Boolean> = _saving.asStateFlow()

  private val _error: MutableStateFlow<Int?> =
    MutableStateFlow(null)
  val error: StateFlow<Int?> = _error.asStateFlow()

  private val _passwordHidden: MutableStateFlow<Boolean> = MutableStateFlow(true)
  val passwordHidden: StateFlow<Boolean> = _passwordHidden.asStateFlow()

  private fun initFromId() = viewModelScope.launch {
    if (serverId != null) {
      _saving.value = true
      _editMode.value = true
      val server = repository.serverById(serverId)
      if (server != null) {
        _directory.value = server.directory ?: directory.value
        nameTfState.setTextAndPlaceCursorAtEnd(server.name)
        ipTfState.setTextAndPlaceCursorAtEnd(server.host)
        portTfState.setTextAndPlaceCursorAtEnd(server.port.toString())
        passwordTfState.setTextAndPlaceCursorAtEnd(server.password)
        directoryTfState.setTextAndPlaceCursorAtEnd(directory.value.formatPath())

        _startingSubtitle.value = server.host + ":" + server.port
      } else {
        _error.value = R.string.error_no_server_chosen
      }
      _saving.value = false
    }
  }

  fun delete(navHome: () -> Unit) = viewModelScope.launch {
    cancelDeletion()
    _saving.value = true
    val server = repository.serverById(serverId!!)
    repository.deleteServer(server!!)
    _saving.value = false
    navHome()
  }

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
        id = if (editMode.value) serverId!! else 0,
        name = name,
        host = host,
        port = port,
        password = password,
        directory = directory,
      )
      if (editMode.value)
        repository.updateServer(server)
      else
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

  fun askForDeletion() {
    _deleteConfirm.value = true
  }

  fun cancelDeletion() {
    _deleteConfirm.value = false
  }

  class Factory(private val repository: OsmiumRepository, private val serverId: Int? = null) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ManageServerViewModel::class.java)) {
        val vm = ManageServerViewModel(repository, serverId = serverId)
        vm.initFromId()
        @Suppress("UNCHECKED_CAST")
        return vm as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}