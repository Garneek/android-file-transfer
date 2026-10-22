package com.example.file_transfer.network_protocol

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.net.InetAddress
import java.net.Socket
import kotlinx.coroutines.*

class TcpClient(hostname: String, port: Int, listener: MessageListener) {
    companion object {
        val TAG: String = TcpClient::class.java.getSimpleName()
    }
    interface MessageListener {
        fun messageReceived(message: ByteArray)
    }

    var clientListener: MessageListener?
    var running = false
    var outputStream: BufferedOutputStream? = null
    var inputStream: BufferedInputStream? = null

    var serverHostname: String? = null
    var serverPort: Int? = null

    init {
        clientListener = listener
        serverHostname = hostname
        serverPort = port
    }

    suspend fun runClient() {
        running = true

        try {
            val serverAddr = InetAddress.getByName(serverHostname!!)
            val socket = Socket(serverAddr, serverPort!!)

            try {
                outputStream = BufferedOutputStream(socket.getOutputStream())
                inputStream = BufferedInputStream(socket.getInputStream())


                while (running) {
                    inputStream!!.readBytes().let {
                        clientListener?.messageReceived(it)
                    }
                }

            } catch (e: Exception) {
                Log.e("TCP", "S: Error", e)
            } finally {
                socket.close()
            }
        } catch (e: Exception) {
            Log.e("TCP", "C: Error", e)
        }
    }

    suspend fun stopClient() {
        running = false

        if (outputStream != null) {
            outputStream!!.flush()
            outputStream!!.close()
        }

        clientListener = null
        inputStream = null
        outputStream = null
    }


    suspend fun sendMessage(message: ByteArray?) {
        outputStream?.write(message)
        outputStream?.flush()
    }
}