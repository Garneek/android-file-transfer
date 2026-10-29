package com.plat.osmium.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = OsmiumFile::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("fid"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class File(
    @PrimaryKey val hash: String,
    @ColumnInfo(name = "fid") val fid: Int,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "offset") val offset: Int,
    @ColumnInfo(name = "is_downloaded") val isDownloaded: Boolean,
)
