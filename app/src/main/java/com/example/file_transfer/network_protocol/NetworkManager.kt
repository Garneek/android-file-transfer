package com.example.file_transfer.network_protocol

interface NetworkManager {
    fun discoverLocalHosts(): Array<Pair<String, Short>>;
    fun connectTo(hostname: String, port: Short);

    fun sendPacket()

}