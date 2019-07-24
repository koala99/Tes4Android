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
            getDeviceInfo()
//            var mMessage = Message()
//            mMessage.what = 1
//            mHandler.sendMessageDelayed(mMessage, 5000)
//            mHandler.obtainMessage(0, "hello world").sendToTarget();

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

    fun getDeviceInfo() {
        var deviceId = SystemInfoUtils.getDeviceId(this@MainActivity)
        var imeiId = SystemInfoUtils.getImei(this@MainActivity)
        var androidId = SystemInfoUtils.getAndroidId(this@MainActivity)
        var product = android.os.Build.PRODUCT
        var brand = android.os.Build.BRAND
        var broad = android.os.Build.BOARD
        var cupAbi = android.os.Build.CPU_ABI
        var cupAbi2 = android.os.Build.CPU_ABI2
        var tags = android.os.Build.TAGS
        var model = android.os.Build.MODEL
        var VERSION_CODES_base = android.os.Build.VERSION_CODES.BASE
        var SDK = android.os.Build.VERSION.SDK
        var VERSION_RELEASE = android.os.Build.VERSION.RELEASE
        var DEVICE = android.os.Build.DEVICE
        var sdk_int = android.os.Build.VERSION.SDK_INT
        var CODENAME = android.os.Build.VERSION.CODENAME
        var INCREMENTAL = android.os.Build.VERSION.INCREMENTAL
        var DISPLAY = android.os.Build.DISPLAY
        var HARDWARE = android.os.Build.HARDWARE
        var BOOTLOADER = android.os.Build.BOOTLOADER
        var USER = android.os.Build.USER
        var MANUFACTURER = android.os.Build.MANUFACTURER
        var FINGERPRINT = android.os.Build.FINGERPRINT
        var id = android.os.Build.ID
        var user = android.os.Build.USER
        var type = android.os.Build.TYPE
        var radio = android.os.Build.RADIO
        var time = android.os.Build.TIME
        var serial = android.os.Build.SERIAL
        var host = android.os.Build.HOST
//        vnwl3:google:742574626528451:588088167614103
        Log.e("status", product + ":" + brand + ":" + deviceId + ":" + imeiId)
    }

}
