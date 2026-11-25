package com.plat.osmium.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.plat.osmium.utils.defaultDir
import java.util.Date

@Entity
data class Server(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "host") val host: String = "localhost",
  @ColumnInfo(name = "port") val port: Int = 0,
  @ColumnInfo(name = "password") val password: String = "",
  @ColumnInfo(name = "directory") val directory: String? = defaultDir().path + "/Download",
  @ColumnInfo(name = "accepts_uploads") val acceptsUploads: Boolean? = true,
  @ColumnInfo(name = "last_sync") val lastSync: Date? = null,
  @ColumnInfo(name = "last_ping") val lastPing: Date? = null
)
