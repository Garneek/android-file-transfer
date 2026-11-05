package com.plat.osmium.domain

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.plat.osmium.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import kotlin.io.path.fileSize
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.plat.osmium.utils.ProductionDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private fun defaultDir(): File = File(Environment.getExternalStorageDirectory().absolutePath)

@RequiresApi(Build.VERSION_CODES.O)
fun getFileSize(file: File): Long = file.toPath().fileSize()

class FilePickerViewModel(
  val target: Target = Target.MultiFile
) : ViewModel() {
  class SwappableList<T> {
    private var list1: List<T> = listOf()
    private var list2: List<T> = listOf()
    var currentList1 = true

    fun current(): List<T> = if (currentList1) list1 else list2
    fun list1(): List<T> = list1
    fun list2(): List<T> = list2
    fun setCurrent(n: List<T>): SwappableList<T> {
      val tmp = SwappableList<T>()
      tmp.currentList1 = currentList1
      if (tmp.currentList1) {
        tmp.list1 = n
        tmp.list2 = list2
      } else {
        tmp.list1 = list1
        tmp.list2 = n
      }
      return tmp
    }

    fun swapCurrent(n: List<T>): SwappableList<T> {
      val tmp = SwappableList<T>()
      tmp.currentList1 = !currentList1
      if (tmp.currentList1) {
        tmp.list1 = n
        tmp.list2 = list2
      } else {
        tmp.list1 = list1
        tmp.list2 = n
      }
      return tmp
    }
  }

  enum class Target(val resId: Int) {
    Directory(R.string.directory_picker_title),
    File(R.string.file_picker_title),
    MultiFile(R.string.multifile_picker_title)
  }

  private val _currentDirectory: MutableStateFlow<File> = MutableStateFlow(defaultDir())
  val currentDirectory: StateFlow<File> = _currentDirectory.asStateFlow()

  private val _fileList: MutableStateFlow<SwappableList<Pair<File, Boolean>>> =
    MutableStateFlow(SwappableList())
  val fileList: StateFlow<SwappableList<Pair<File, Boolean>>> = _fileList.asStateFlow()


  private val _selectedIdx: MutableStateFlow<Int> = MutableStateFlow(0)
  val selectedIdx: StateFlow<Int> = _selectedIdx.asStateFlow()


  private fun refreshFileList() {
    if (currentDirectory.value.isDirectory) {
      CoroutineScope(context = ProductionDispatcherProvider.provideIOContext()).launch {
        try {
          var files = currentDirectory.value.listFiles()!!.asList()
          if (target == Target.Directory)
            files = files.filter { f -> f.isDirectory }

          _fileList.value = _fileList.value.swapCurrent(files.map { file ->
            Pair(file, false)
          })
        } catch (_: Exception) {
        }
      }
    }
  }

  fun goToDirectory(directory: File) {
    if (directory.isDirectory) {
      _currentDirectory.value = directory
      refreshFileList()
    }
  }

  fun resetDirectory() {
    _currentDirectory.value = defaultDir()
    _fileList.value = SwappableList()
    refreshFileList()
    _selectedIdx.value = 0
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun moveUp() {
    if (currentDirectory.value != defaultDir()) {
      try {
        _currentDirectory.value = _currentDirectory.value.toPath().parent.toFile()
        refreshFileList()
      } catch (_: Exception) {
        // Expected if it cannot move up, no point logging
      }
    }
  }

  @Composable
  fun getDirectoryName(): String =
    if (currentDirectory.collectAsState().value == defaultDir())
      stringResource(R.string.root_directory)
    else
      currentDirectory.collectAsState().value.name

  fun setSelectionFile(idx: Int, state: Boolean) {
    if (idx < fileList.value.current().size && idx >= 0) {
      _fileList.value =
        _fileList.value.setCurrent(_fileList.value.current().mapIndexed { index, pair ->
          if (idx == index)
            Pair(pair.first, state)
          else
            pair
        })
    }
  }

  fun selectFile(idx: Int) =
    if (target != Target.MultiFile)
      _selectedIdx.value = idx
    else
      setSelectionFile(idx, true)

  fun isSelected(idx: Int, selected: Int): Boolean =
    when (target) {
      Target.Directory,
      Target.File -> idx == selected

      Target.MultiFile -> fileList.value.current()[idx].second
    }

  fun checkFileMatches(file: File): Boolean =
    when (target) {
      Target.Directory -> file.isDirectory
      Target.File, Target.MultiFile -> file.isFile
    }

  fun clearSelections() {
    when (target) {
      Target.Directory,
      Target.File -> _selectedIdx.value = 0

      Target.MultiFile ->
        _fileList.value = _fileList.value.setCurrent(_fileList.value.current().map { pair ->
          Pair(pair.first, false)
        })
    }
  }

  fun getResult(): List<File> =
    when (target) {
      Target.Directory,
      Target.File ->
        if (selectedIdx.value < fileList.value.current().size)
          listOf(fileList.value.current()[selectedIdx.value].first)
        else listOf()

      Target.MultiFile ->
        fileList.value.current().filter { pair -> pair.second }.map { pair -> pair.first }
    }

  companion object {
    val TARGET_KEY = CreationExtras.Key<Target>()

    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val targetArg = (this[TARGET_KEY] as Target)
        val vm = FilePickerViewModel(target = targetArg)
        vm.resetDirectory()
        vm
      }
    }
  }
}