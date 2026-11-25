package com.plat.osmium.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.plat.osmium.db.OsmiumRepository
import com.plat.osmium.db.Server
import kotlinx.coroutines.flow.Flow

class ServersListServersViewModel(private val repository: OsmiumRepository) : ViewModel() {
  val servers: Flow<List<Server>> = repository.allServers



  class Factory(private val repository: OsmiumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ServersListServersViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return ServersListServersViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}

