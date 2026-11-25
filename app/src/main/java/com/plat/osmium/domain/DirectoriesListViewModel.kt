package com.plat.osmium.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plat.osmium.db.Directory
import com.plat.osmium.db.OsmiumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
class DirectoriesListViewModel(private val repository: OsmiumRepository) : ViewModel() {
  val directories: Flow<List<Directory>> = repository.allDirs

  enum class ManagementDialogState {
    Start,
    Edit,
    ConfirmDelete,
    ConfirmEdit,
    Closed,
  }

  private val _managementDialog: MutableStateFlow<ManagementDialogState> =
    MutableStateFlow(ManagementDialogState.Closed)
  val managementDialog: StateFlow<ManagementDialogState> = _managementDialog.asStateFlow()

  private val _dialogEntry: MutableStateFlow<Directory?> = MutableStateFlow(null)
  val dialogEntry: StateFlow<Directory?> = _dialogEntry.asStateFlow()

  fun setDialogEntry(dir: Directory) {
    _dialogEntry.value = dir
  }

  fun setDialogState(state: ManagementDialogState) {
    _managementDialog.value = state
  }

  fun addDirectory(dir: Directory) = viewModelScope.launch {
    repository.insertDir(dir)
  }

  fun deleteCurrentDirectory() = viewModelScope.launch {
    if (dialogEntry.value != null)
      repository.deleteDir(dialogEntry.value!!)
  }

  fun updateDirectory(directory: File? = null, serverId: Int? = null) = viewModelScope.launch {
    val dr = dialogEntry.value
    if (dr != null) {
      repository.updateDir(
        Directory(
          id = dr.id,
          path = directory?.path ?: dr.path,
          serverId = serverId ?: dr.serverId,
        )
      )
    }
  }

  fun uploadFiles(idx: Int) {}

  class Factory(private val repository: OsmiumRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(DirectoriesListViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return DirectoriesListViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }

}

