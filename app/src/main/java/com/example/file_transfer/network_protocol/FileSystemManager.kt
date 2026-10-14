package com.example.file_transfer.network_protocol

interface FileSystemManager {
    fun readFileBlock(pathname: String, offset: Int, size: Int = 524288): ByteArray;
    fun writeFileBlock(pathname: String, offset: Int, data: ByteArray): Unit;
}