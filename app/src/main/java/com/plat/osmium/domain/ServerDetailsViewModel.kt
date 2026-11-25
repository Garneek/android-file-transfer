package com.plat.osmium.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.plat.osmium.db.OsmiumRepository
import com.plat.osmium.db.Server
import kotlinx.coroutines.flow.Flow

class ServerDetailsViewModel(serverId: Int, private val repository: OsmiumRepository) : ViewModel() {
  val server: Flow<Server?> = repository.flowServerById(serverId)

  class Factory(private val repository: OsmiumRepository, private val serverId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ServerDetailsViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return ServerDetailsViewModel(serverId, repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}