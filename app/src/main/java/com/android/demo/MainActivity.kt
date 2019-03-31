package com.android.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mHandler = Handler(Handler.Callback { msg ->
        if (msg.what == 1) {

            Log.e("收到消息", "1")
        }
        false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initdata()
    }

    fun initdata() {
        send_msg.setOnClickListener {
            Log.e("发送消息", "1")
            var mMessage = Message()
            mMessage.what = 1
            mHandler.sendMessageDelayed(mMessage, 5000)
        }
        dis_msg.setOnClickListener {
            Log.e("移除消息", "1")
            mHandler.removeMessages(1)
        }
    }
}
