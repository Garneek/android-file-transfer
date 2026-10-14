package com.example.file_transfer.network_protocol

import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class TcpClient(listener: OnMessageReceived?) {
    private var mServerMessage: String? = null
    private var mMessageListener: OnMessageReceived? = null
    private var mRun = false
    private var mBufferOut: PrintWriter? = null
    private var mBufferIn: BufferedReader? = null



    init {
        mMessageListener = listener
    }

    fun sendMessage(message: String?) {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (mBufferOut != null) {
                    mBufferOut!!.println(message)
                    mBufferOut!!.flush()
                }
            }
        }
        val thread = Thread(runnable)
        thread.start()
    }

    fun stopClient() {
        mRun = false

        if (mBufferOut != null) {
            mBufferOut!!.flush()
            mBufferOut!!.close()
        }

        mMessageListener = null
        mBufferIn = null
        mBufferOut = null
        mServerMessage = null
    }

    fun run() {
        mRun = true

        try {
            //here you must put your computer's IP address.
            val serverAddr = InetAddress.getByName("localhost")

            Log.d("TCP Client", "C: Connecting...")

            //create a socket to make the connection with the server
            val socket = Socket(serverAddr, 6767)

            try {
                //sends the message to the server

                mBufferOut =
                    PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)

                //receives the message which the server sends back
                mBufferIn = BufferedReader(InputStreamReader(socket.getInputStream()))


                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    mServerMessage = mBufferIn!!.readLine()

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener!!.messageReceived(mServerMessage)
                    }
                }

                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'")
            } catch (e: Exception) {
                Log.e("TCP", "S: Error", e)
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close()
            }
        } catch (e: Exception) {
            Log.e("TCP", "C: Error", e)
        }
    }

    interface OnMessageReceived {
        fun messageReceived(message: String?)
    }

    companion object {
        val TAG: String = TcpClient::class.java.getSimpleName()
    }
}