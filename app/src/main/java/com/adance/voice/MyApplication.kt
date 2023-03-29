package com.adance.voice

import android.app.Application
import android.util.Log
import com.adance.module_rtm.rtm.RTMSDKManager

/**
 * Created by wenhao 2022/11/21
 */
class MyApplication : Application(){

    companion object{
        @JvmStatic
        lateinit var instance: MyApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this

        RTMSDKManager.rtm = "zego"
        RTMSDKManager.instance()?.create(this,"1355862958",filesDir.absolutePath+"/voice")

        Log.e("TAG",filesDir.absolutePath+"/voice")

        RTMSDKManager.instance()?.login("23104749","23104749","04AAAAAGOuS5QAEDd0dnpna3RrNnBoZzYyMWgAgL8wa04quPQHV5O5AR4c5k9W12yuc9Y6fgRBFYzGSU5q1wKPdmCJJXcx95DUe75ncGWAsNFptGDoAsl7m3UU3C8NP7jbHTTk36sybt1hWB6e2QH+xEPhX\\/uC8ym4L16XPqyHNuAAaa4QVDI14n\\/riHUOUXnjf4\\/0iFK\\/fvnOQ+H4",object : RTMSDKManager.OnLoggedInCallback{
            override fun onLoggedSuccess() {
                Log.e("TAG","===================onLoggedSuccess")
            }

            override fun onLoggedFail(errorCode: Int, errorDesc: String) {
                Log.e("TAG","===================onLoggedFail---->"+errorCode+"-----"+errorDesc)
            }

        })
    }
}