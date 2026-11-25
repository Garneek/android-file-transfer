package com.plat.osmium.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class OsmiumRepository(
  private val directoryDao: DirectoryDao,
  private val serverDao: ServerDao
) {
  val allDirs: Flow<List<Directory>> = directoryDao.getAll()
  val allServers: Flow<List<Server>> = serverDao.getAll()

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

  @WorkerThread
  suspend fun insertServer(server: Server) {
    serverDao.insert(server)
  }

  @WorkerThread
  suspend fun updateServer(server: Server) {
    serverDao.update(server)
  }

  @WorkerThread
  suspend fun deleteServer(server: Server) {
    serverDao.delete(server)
  }
}