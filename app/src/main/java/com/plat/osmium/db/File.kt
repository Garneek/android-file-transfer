package com.plat.osmium.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity
data class OsmiumFile(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "thumbnail_uri") val thumbnailUri: Boolean,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "chunk_n") val chunkN: Int,
    @ColumnInfo(name = "chunk_size") val chunkSize: Long,
    @ColumnInfo(name = "is_ready") val isReady: Boolean,
    @ColumnInfo(name = "date_ready") val dateReady: String
)
