package com.plat.osmium.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class OsmiumRepository(private val directoryDao: DirectoryDao) {
  val allDirs: Flow<List<Directory>> = directoryDao.getAll()

  @WorkerThread
  suspend fun insertDir(directory: Directory) {
    directoryDao.insert(directory)
  }

  @WorkerThread
  suspend fun updateDir(directory: Directory) {
    directoryDao.update(directory)
  }

  @WorkerThread
  suspend fun deleteDir(directory: Directory) {
    directoryDao.delete(directory)
  }
}