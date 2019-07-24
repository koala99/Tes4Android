package com.android.demo.socketio

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.demo.R
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.lang.Exception




class SocketIoActivity : AppCompatActivity() {
    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socketio)
        initView()
    }

    private fun initView() {
        Handler().postDelayed({
            initSocket()
        }, 1000)

    }

    private fun initSocket() {
        try {
            val opts = IO.Options()
            opts.forceNew = true
            Log.e("status", "1")

            socket = IO.socket("http://192.168.1.116:5000",opts)

            Log.e("status", "2")

            socket.on(Socket.EVENT_CONNECT) {
                Log.e("connect", "success")
                socket.emit("my event1", "1", "2", "3")

//            socket.emit("message", "test message")
//            socket.emit('my event', {data: 'I\'m connected!'});
            }.on("event") { }.on(Socket.EVENT_DISCONNECT) {
                Log.e("disconnection", "disconnection")
            }.on(Socket.EVENT_CONNECT_ERROR){
                Log.e("connection", "error")
                Log.e( "connection", "连接 失败" + it[0] );


            }.on(Socket.EVENT_CONNECT_TIMEOUT){
                Log.e("connection", "timeout")

            }
            socket.connect()
            Log.e("status", "3")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("error", ":" + e.localizedMessage)
        }

//        try {
//            socket = IO.socket("http://172.16.20.198:5000/")
//            socket.connect()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("msg", e.localizedMessage)
//        }

//        activity_socketio!!.on("/", Emitter.Listener {
//            activity_socketio!!.disconnect()
//        })

    }


}
