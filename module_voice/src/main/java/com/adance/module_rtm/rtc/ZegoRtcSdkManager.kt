package com.adance.module_rtm.rtc

import android.app.Application
import android.util.Log
import android.view.TextureView
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mClientRoleChangedCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mConnectionLostCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mErrorCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mMeJoinedCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mRejoinChannelSuccessCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mRoomStreamUpdateCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mUserJoinedCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mUserLeftCallback
import com.adance.module_rtm.rtc.RTCSDKManager.Companion.mUserOfflineCallback
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.ZegoMediaPlayer
import im.zego.zegoexpress.callback.IZegoEventHandler
import im.zego.zegoexpress.constants.*
import im.zego.zegoexpress.entity.*
import org.json.JSONObject
import java.io.File
import java.util.ArrayList


/**
 * 即构 rtc 管理类
 * Created by wenhao 2022/12/14
 */
class ZegoRtcSdkManager {
    companion object {
        private var zegoRtcSdkManager: ZegoRtcSdkManager? = null
        fun instance(): ZegoRtcSdkManager? {
            if (zegoRtcSdkManager == null) {
                synchronized(ZegoRtcSdkManager::class.java) {
                    if (zegoRtcSdkManager == null) {
                        zegoRtcSdkManager = ZegoRtcSdkManager()
                    }
                }
            }
            return zegoRtcSdkManager
        }
    }

    private var mEngine: ZegoExpressEngine? = null
    private var zegoMediaPlayer: ZegoMediaPlayer? = null

    /**
     * 创建引擎
     * type 0 标准音视频通话场景 1 默认（通用）场景
     */
    fun createEngine(application: Application, appID: String, logFile: String,type:Int) {
        val profile = ZegoEngineProfile()
        profile.appID = appID.toLong()
        profile.scenario =if (type == 0) ZegoScenario.STANDARD_VIDEO_CALL else ZegoScenario.DEFAULT //标准音视频通话场景，适用于 1v1 视频或语音通话场景。
        profile.application = application

        setLogFile(logFile)

        mEngine = ZegoExpressEngine.createEngine(profile, object : IZegoEventHandler() {
            override fun onRoomStateChanged(
                roomID: String?,
                reason: ZegoRoomStateChangedReason?,
                errorCode: Int,
                extendedData: JSONObject?,
            ) {
                super.onRoomStateChanged(roomID, reason, errorCode, extendedData)
                when (reason) {
                    ZegoRoomStateChangedReason.LOGINED -> { //登录成功
                        mMeJoinedCallback?.onMeJoined()
                    }
                    ZegoRoomStateChangedReason.RECONNECTING -> {//连接临时中断
                        mConnectionLostCallback?.onConnectionLost()
                    }
                    ZegoRoomStateChangedReason.RECONNECTED -> { //重连成功
                        mRejoinChannelSuccessCallback?.onRejoinSuccess()
                    }
                    ZegoRoomStateChangedReason.RECONNECT_FAILED -> { //0 代表自己
                        mUserOfflineCallback?.onUserOffline("0")
                    }
                    ZegoRoomStateChangedReason.LOGIN_FAILED,
                    ZegoRoomStateChangedReason.KICK_OUT,
                    ZegoRoomStateChangedReason.LOGOUT_FAILED,
                    -> {
                        mErrorCallback?.onError(errorCode)
                    }

                    else -> {

                    }
                }
            }

            /**
             * 推流状态回调
             */
            override fun onPublisherStateUpdate(
                streamID: String?,
                state: ZegoPublisherState?,
                errorCode: Int,
                extendedData: JSONObject?,
            ) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData)
                if (errorCode == 0) {
                    if (state == ZegoPublisherState.PUBLISHING) {
                        mClientRoleChangedCallback?.onClientRoleChangedAnchor()
                    }
                    if (state == ZegoPublisherState.NO_PUBLISH) {
                        mClientRoleChangedCallback?.onClientRoleChangedAudience()
                    }
                } else {
                    mClientRoleChangedCallback?.onClientRoleChangedFail(errorCode)
                }
            }

            /**
             * 相同房间内其他用户推的流增加或减少的通知
             */
            override fun onRoomStreamUpdate(
                roomID: String?,
                updateType: ZegoUpdateType?,
                streamList: ArrayList<ZegoStream>?,
                extendedData: JSONObject?,
            ) {
                super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData)
                if (updateType == ZegoUpdateType.ADD) {
                    for (zegoStream in streamList!!) {
                        mRoomStreamUpdateCallback?.onRoomStreamAdd(zegoStream.streamID)
                    }
                } else if (updateType == ZegoUpdateType.DELETE) {
                    for (zegoStream in streamList!!) {
                        mRoomStreamUpdateCallback?.onRoomStreamDelete(zegoStream.streamID)
                    }
                }
            }

            /**
             * 房间内其他用户增加或减少的回调通知
             */
            override fun onRoomUserUpdate(
                roomID: String?,
                updateType: ZegoUpdateType?,
                userList: ArrayList<ZegoUser>?,
            ) {
                super.onRoomUserUpdate(roomID, updateType, userList)
                if (updateType == ZegoUpdateType.ADD) {
                    for (user in userList!!) {
                        mUserJoinedCallback?.onUserJoined(user.userID)
                    }
                } else if (updateType == ZegoUpdateType.DELETE) {
                    for (user in userList!!) {
                        mUserLeftCallback?.onUserLeft(user.userID)
                    }
                }
            }

            override fun onNetworkModeChanged(mode: ZegoNetworkMode?) {
                super.onNetworkModeChanged(mode)
                if (mode?.value() == 0 || mode?.value() == 1 || mode?.value() == 2) {//网络连接中断
                    mConnectionLostCallback?.onNetworkDisconnect()
                } else {
                    mode?.value()?.let { mConnectionLostCallback?.onNetworkRestore(it) }
                }
            }
        })
    }

    /**
     * 设置日志存储路径
     */
    private fun setLogFile(logFile: String) {
        val logConfig = ZegoLogConfig()
        val file = File(logFile)
        if (!file.exists()) {
            file.mkdirs()
        }
        logConfig.logPath = file.absolutePath
        ZegoExpressEngine.setLogConfig(logConfig)
    }

    /**
     * 登录房间
     */
    fun loginRoom(
        userID: Int,
        channel: String,
        token: String,
    ) {
        val zegoUser = ZegoUser(userID.toString())
        val roomConfig = ZegoRoomConfig()
        roomConfig.isUserStatusNotify = true //是否开启用户进出房间回调通知 [onRoomUserUpdate]，默认关闭
        roomConfig.token = token
        mEngine?.enableCamera(false)
        mEngine?.loginRoom(
            channel, zegoUser, roomConfig
        ) { _, _ -> } //0 成功
    }

    /**
     * 离开房间
     */
    fun leaveRoom() {
        mEngine?.logoutRoom()
    }

    /**
     * 切换前置后置摄像头
     */
    fun setSwitchCamera(enabled: Boolean) {
        mEngine?.useFrontCamera(enabled)
    }


    /**
     * 开启视频
     */
    fun startEnableVideo() {
        mEngine?.enableCamera(true)
    }

    //预览并推流
    fun startPublish(userID: Int, localView: TextureView?) {
        // 设置本地预览视图并启动预览，视图模式采用 SDK 默认的模式，等比缩放填充整个 View
        if (localView != null) {
            val previewCanvas = ZegoCanvas(localView)
            mEngine?.enableCamera(true)
            mEngine?.startPreview(previewCanvas)
        }
        // 开始推流
        // 用户调用 loginRoom 之后再调用此接口进行推流
        // 在同一个 AppID 下，开发者需要保证“streamID” 全局唯一，如果不同用户各推了一条 “streamID” 相同的流，后推流的用户会推流失败。
        mEngine?.startPublishingStream(userID.toString())
    }

    //停止推流
    fun stopPublish() {
        mEngine?.stopPublishingStream()
    }

    //拉流
    fun startPlaying(streamID: String, remoteView: TextureView?) {
        if (remoteView != null) {
            val playCanvas = ZegoCanvas(remoteView)
            playCanvas.viewMode = ZegoViewMode.ASPECT_FILL
            mEngine?.enableCamera(true)
            mEngine?.startPlayingStream(streamID, playCanvas)
            return
        }
        mEngine?.startPlayingStream(streamID)
    }

    //停止拉流
    fun stopPlaying(streamID: String) {
        mEngine?.stopPlayingStream(streamID)
    }

    //设置是否静音
    fun setMuteLocalAudio(muted: Boolean): Int {
        mEngine?.mutePublishStreamAudio(muted)
        return 0
    }

    //设置是否外放
    fun setEnableSpeakerphone(enabled: Boolean): Int {
        mEngine?.setAudioRouteToSpeaker(enabled)
        return 0
    }

    //设置预设的变声效果
    fun setVoiceConversionPreset(preset: Int): Int {
        val zegoVoiceChangerPreset = when (preset) {
            1 -> {
                ZegoVoiceChangerPreset.WOMEN_TO_MEN
            }
            2 -> {
                ZegoVoiceChangerPreset.FEMALE_FRESH
            }
            3 -> {
                ZegoVoiceChangerPreset.MALE_MAGNETIC
            }
            4 -> {
                ZegoVoiceChangerPreset.OPTIMUS_PRIME
            }
            else -> {
                ZegoVoiceChangerPreset.NONE
            }
        }
        mEngine?.setVoiceChangerPreset(zegoVoiceChangerPreset)
        return 0
    }

    fun createMediaPlayer() {
        if (zegoMediaPlayer == null){
            zegoMediaPlayer = mEngine?.createMediaPlayer()
            zegoMediaPlayer?.enableAux(true)
        }
    }

    fun playMusic(filepath: String?) {
        zegoMediaPlayer?.loadResource(filepath) {
            if (it == 0){
                zegoMediaPlayer?.start()
            }
        }
    }

    fun stopMusic() {
        zegoMediaPlayer?.stop()
    }

    fun pauseMusic() {
        zegoMediaPlayer?.pause()
    }

    fun resumeMusic() {
        zegoMediaPlayer?.resume()
    }

    fun setMusicVolumeRtc(volume: Int) {
        zegoMediaPlayer?.setVolume(volume)
    }

    fun getMusicDuration(): Float {
        return zegoMediaPlayer?.totalDuration?.toFloat() ?: 0f
    }

    fun getMusicCurrentPosition(): Float {
        return zegoMediaPlayer?.currentProgress?.toFloat() ?: 0f
    }

    fun setMusicPosition(millisecond: Long) {
        zegoMediaPlayer?.seekTo(millisecond) {}
    }


    /**
     * 销毁RtcEngine
     */
    fun destroy() {
        ZegoExpressEngine.destroyEngine { }
    }

}