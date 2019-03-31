package com.android.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {

//    private var mHandler = Handler(Handler.Callback { msg ->
//        if (msg.what == 1) {
//
//            Log.e("收到消息", "1")
//        }
//        false
//    })

    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            // TODO Auto-generated method stub
            Toast.makeText(
                this@MainActivity, msg.obj as CharSequence,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initdata()
    }

    fun initdata() {
        send_msg.setOnClickListener {
            Log.e("发送消息", "1")
//            var mMessage = Message()
//            mMessage.what = 1
//            mHandler.sendMessageDelayed(mMessage, 5000)
            mHandler.obtainMessage(0, "hello world").sendToTarget();

        }
        dis_msg.setOnClickListener {
            Log.e("移除消息", "1")
//            mHandler.removeMessages(1)
            hookHandler()
        }
    }

//    private fun hookHandler() {
//        val field = Handler::class.java.getField("mCallback")
//
////        var field = FieldUtils.getDeclaredField(Handler.class, "mCallback",
////        true);
//        field.set(mHandler, Handler.Callback { msg ->
//            if (msg.what == 1) {
//
//                Log.e("收到消息222", "1")
//            }
//            false
//        })


    private fun hookHandler() {
        try {

            val clazz = Handler::class.java
            var field: Field? = null
            field = clazz.getDeclaredField("mCallback")

            field!!.isAccessible = true
            //        Field field = FieldUtils.getDeclaredField(Handler.class, "mCallback",
            //                true);

            field.set(mHandler, Handler.Callback { msg ->
                // TODO Auto-generated method stub
                msg.obj = "hello china"
                false
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
