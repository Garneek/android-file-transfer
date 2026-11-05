package com.plat.osmium.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity
data class Server(
    @PrimaryKey val serverId: Int = 0,
    @ColumnInfo(name = "name") val name: String = "Default server",
    @ColumnInfo(name = "host") val host: String = "localhost",
    @ColumnInfo(name = "port") val port: Int = 0,
    @ColumnInfo(name = "password") val password: String = "",
    @ColumnInfo(name = "download_directory") val downloadDirectory: String = "",
)
