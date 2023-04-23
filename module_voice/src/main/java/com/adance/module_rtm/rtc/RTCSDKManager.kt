package com.adance.module_rtm.rtc

import android.app.Application
import android.view.TextureView
import android.widget.FrameLayout

/**
 * rtc管理类
 * Created by wenhao 2022/12/14
 */
class RTCSDKManager {
    companion object {
        private var sdkManager: RTCSDKManager? = null
        const val RTC_AGORA = "agora" //声网
        const val RTC_ZEGO = "zego" //即构
        var rtc: String? = null
        fun instance(): RTCSDKManager? {
            if (sdkManager == null) {
                synchronized(RTCSDKManager::class.java) {
                    if (sdkManager == null) {
                        sdkManager = RTCSDKManager()
                    }
                }
            }
            return sdkManager
        }

        //error callback
        var mErrorCallback: OnErrorCallback? = null

        //room login callback
        var mMeJoinedCallback: OnMeJoinedCallback? = null

        //rejoin channel success callback
        var mRejoinChannelSuccessCallback: OnRejoinChannelSuccessCallback? = null

        //client role changed callback
        var mClientRoleChangedCallback: OnClientRoleChangedCallback? = null

        //room stream update callback
        var mRoomStreamUpdateCallback: OnRoomStreamUpdateCallback? = null

        //user joined callback
        var mUserJoinedCallback: OnUserJoinedCallback? = null

        //user left callback
        var mUserLeftCallback: OnUserLeftCallback? = null

        //user offline callback
        var mUserOfflineCallback: OnUserOfflineCallback? = null

        //connection lost callback
        var mConnectionLostCallback: OnConnectionLostCallback? = null
    }

    /**
     * 创建引擎
     */
    fun createEngine(application: Application, appID: String, logFile: String,type:Int) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.createEngine(application, appID, logFile)
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.createEngine(application, appID, logFile,type)
        }
    }

    /**
     * 登录房间
     */
    fun loginRoom(
        userID: Int,
        channel: String,
        token: String,
        role: Int,
    ) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.loginRoom(userID, channel, token, role)
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.loginRoom(userID, channel, token)
        }
    }

    /**
     * 离开房间
     */
    fun leaveRoom() {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.leaveRoom()
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.leaveRoom()
        }
    }

    /**
     * 销毁引擎
     */
    fun destroy(){
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.destroy()
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.destroy()
        }
    }

    /**
     * 切换前置后置摄像头
     */
    fun setSwitchCamera(enabled: Boolean){
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.setSwitchCamera()
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.setSwitchCamera(enabled)
        }
    }

    /**
     * 开启视频
     */
    fun startEnableVideo() {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.startEnableVideo()
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.startEnableVideo()
        }
    }

    /**
     * role 1 主播 2 观众
     * 设置角色
     */
    fun setIdentity(role: Int, userID: Int){
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.setIdentity(role)
        }else if (rtc == RTC_ZEGO){
            if (role == 1) {
                ZegoRtcSdkManager.instance()?.startPublish(userID, null)
            } else {
                ZegoRtcSdkManager.instance()?.stopPublish()
            }
        }
    }


    /**
     * 开始推流
     */
    fun startPublish(application: Application, userID: Int, localView: FrameLayout?,liveView: TextureView?) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.startPublish(application, userID, localView)
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.startPublish(userID, liveView)
        }
    }

    /**
     * 停止推流
     */
    fun stopPublish() {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.stopPublish()
        }
    }

    /**
     * 开始拉流
     */
    fun startPlaying(application: Application, userID: Int, remoteView: FrameLayout?, liveView: TextureView?) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.startPlaying(application, userID, remoteView)
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.startPlaying(userID.toString(), liveView)
        }
    }

    /**
     * 停止拉流
     */
    fun stopPlaying(streamID: String) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.stopPlaying(streamID)
        }
    }

    /**
     * 设置是否静音
     */
    fun setMuteLocalAudio(muted: Boolean): Boolean {
        if (rtc.isNullOrEmpty()) {
            return false
        }
        if (rtc == RTC_AGORA) {
            return AgoraRtcSdkManager.instance()?.setMuteLocalAudio(muted) == 0
        } else if (rtc == RTC_ZEGO) {
            return ZegoRtcSdkManager.instance()?.setMuteLocalAudio(muted) == 0
        }
        return false
    }

    /**
     * 设置是否外放
     */
    fun setEnableSpeakerphone(enabled: Boolean): Boolean {
        if (rtc.isNullOrEmpty()) {
            return false
        }
        if (rtc == RTC_AGORA) {
            return AgoraRtcSdkManager.instance()?.setEnableSpeakerphone(enabled) == 0
        } else if (rtc == RTC_ZEGO) {
            return ZegoRtcSdkManager.instance()?.setEnableSpeakerphone(enabled) == 0
        }
        return false
    }

    /**
     * 设置人声
     */
    fun setVoiceConversionPreset(preset:Int){
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA) {
            AgoraRtcSdkManager.instance()?.setVoiceConversionPreset(preset)
        } else if (rtc == RTC_ZEGO) {
            ZegoRtcSdkManager.instance()?.setVoiceConversionPreset(preset)
        }
    }

    fun createMediaPlayer(){
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.createMediaPlayer()
        }
    }

    fun playMusic(filepath: String?, cycle: Int) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.playMusic(filepath,cycle)
        }else if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.playMusic(filepath)
        }
    }

    fun stopMusic() {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.stopMusic()
        }else if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.stopMusic()
        }
    }

    fun pauseMusic() {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.pauseMusic()
        }else if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.pauseMusic()
        }
    }

    fun resumeMusic() {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.resumeMusic()
        }else if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.resumeMusic()
        }
    }

    fun setMusicVolumeRtc(volume: Int) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.setMusicVolumeRtc(volume)
        }else if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.setMusicVolumeRtc(volume)
        }
    }

    fun getMusicDuration(): Float? {
        if (rtc.isNullOrEmpty()) {
            return 0f
        }
        if (rtc == RTC_AGORA){
            return AgoraRtcSdkManager.instance()?.getMusicDuration()
        }else if (rtc == RTC_ZEGO){
            return ZegoRtcSdkManager.instance()?.getMusicDuration()
        }
        return 0f
    }

    fun getMusicCurrentPosition(): Float? {
        if (rtc.isNullOrEmpty()) {
            return 0f
        }
        if (rtc == RTC_AGORA){
            return AgoraRtcSdkManager.instance()?.getMusicCurrentPosition()
        }else if (rtc == RTC_ZEGO){
            return ZegoRtcSdkManager.instance()?.getMusicCurrentPosition()
        }
        return 0f
    }

    fun setMusicPosition(startPos: Long) {
        if (rtc.isNullOrEmpty()) {
            return
        }
        if (rtc == RTC_AGORA){
            AgoraRtcSdkManager.instance()?.setMusicPosition(startPos)
        }else if (rtc == RTC_ZEGO){
            ZegoRtcSdkManager.instance()?.setMusicPosition(startPos)
        }
    }

    interface OnErrorCallback {
        fun onError(err: Int)
    }

    interface OnRejoinChannelSuccessCallback {
        fun onRejoinSuccess()
    }

    interface OnClientRoleChangedCallback {
        fun onClientRoleChangedAnchor()
        fun onClientRoleChangedAudience()
        fun onClientRoleChangedFail(errorCode: Int)
    }

    interface OnRoomStreamUpdateCallback {
        fun onRoomStreamAdd(streamID: String)
        fun onRoomStreamDelete(streamID: String)
    }

    interface OnUserJoinedCallback {
        fun onUserJoined(uid: String)
    }

    interface OnUserLeftCallback {
        fun onUserLeft(uid: String)
    }

    interface OnUserOfflineCallback {
        fun onUserOffline(uid: String)
    }

    interface OnConnectionLostCallback {
        fun onConnectionLost()
        fun onNetworkRestore(mode:Int)
        fun onNetworkDisconnect()
    }

    interface OnMeJoinedCallback {
        fun onMeJoined()
    }

}