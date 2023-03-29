package com.adance.voice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.adance.module_rtm.rtm.RTMSDKManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RTMSDKManager.mLoginStateCallback = object : RTMSDKManager.OnLoginStateCallback{

            override fun onConnectionStateChanged(state: Int, reason: Int) {
                Log.e("TAG",state.toString()+"----------"+reason)
            }

            override fun onConnectSuccess() {
                Log.e("TAG","onConnectSuccess")
            }

            override fun onConnectTimeOut() {
                Log.e("TAG","onConnectTimeOut")
            }

            override fun onConnectFail() {
                Log.e("TAG","onConnectFail")
            }

            override fun onConnectInterrupted() {
                Log.e("TAG","onConnectInterrupted")
            }

            override fun onConnectLogout() {
                Log.e("TAG","onConnectLogout")
            }

            override fun onConnectBanned() {
                Log.e("TAG","onConnectBanned")
            }

            override fun onConnectRemoteLogin() {
                Log.e("TAG","onConnectRemoteLogin")
            }

            override fun onConnectTokenExpired() {
                Log.e("TAG","onConnectTokenExpired")
            }

            override fun onConnectAborted() {
                Log.e("TAG","onConnectAborted")
            }

        }
    }
}