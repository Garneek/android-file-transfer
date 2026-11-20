package com.plat.osmium.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
//  (
//    foreignKeys = [ForeignKey(
//        entity = Server::class,
//        parentColumns = arrayOf("id"),
//        childColumns = arrayOf("server_id"),
//        onDelete = ForeignKey.SET_NULL
//    )]
//)
data class Directory(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "server_id") val serverId: Int? = null,
  @ColumnInfo(name = "path") val path: String,
)
