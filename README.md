# voice
语聊sdk封装

#添加混淆文件
-keep class **.zego.**{*;}
-keep class io.agora.**{*;}

#sdk用到的权限
<!-- SDK 必须使用的权限 -->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

 <!-- 对于 Android 12.0 及以上且集成 v4.1.0 以下 SDK 的设备，还需要添加以下权限 -->
 <uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
 <!-- 对于 Android 12.0 及以上设备，还需要添加以下权限 -->
 <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 <uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>
