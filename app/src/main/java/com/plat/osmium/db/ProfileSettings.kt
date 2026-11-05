package com.plat.osmium.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Server::class,
        parentColumns = arrayOf("serverId"),
        childColumns = arrayOf("default_server_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProfileSettings(
    @PrimaryKey val profileId: Int,
    @ColumnInfo(name = "night_mode") val nightMode: Boolean,
    @ColumnInfo(name = "preview_images") val previewImages: Boolean,
    @ColumnInfo(name = "preview_other_media") val previewOtherMedia: Boolean,
    @ColumnInfo(name = "device_name") val deviceName: String,
    @ColumnInfo(name = "default_server_id") val defaultServerId: Int,
    @ColumnInfo(name = "default_server_password") val defaultServerPassword: String,
    @ColumnInfo(name = "default_downloads_dir") val defaultDownloadsDir: String
)
