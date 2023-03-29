package com.adance.module_rtm.rtm

import android.app.Application
import android.util.Log
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mCallInvitationAcceptedCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mCallInvitationCancelledCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mCallInvitationFailureCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mCallInvitationReceivedCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mCallInviteesAnsweredFailureCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mLocalInvitationRefusedCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mLoginConnectCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mLoginStateCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mReceiveRoomMessageCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mRoomMemberJoinedCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mRoomMemberLeftCallback
import com.adance.module_rtm.rtm.RTMSDKManager.Companion.mTokenWillExpireCallback
import im.zego.zim.ZIM
import im.zego.zim.callback.ZIMEventHandler
import im.zego.zim.callback.ZIMMessageSentCallback
import im.zego.zim.entity.*
import im.zego.zim.enums.*
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * zego rtm 管理类
 * Created by wenhao 2022/12/13
 */
class ZegoRtmSdkManager {

    companion object {
        private const val TAG = "RtmSdkManager"
        private var zegoRtmSdkManager: ZegoRtmSdkManager? = null
        fun instance(): ZegoRtmSdkManager? {
            if (zegoRtmSdkManager == null) {
                synchronized(ZegoRtmSdkManager::class.java) {
                    if (zegoRtmSdkManager == null) {
                        zegoRtmSdkManager = ZegoRtmSdkManager()
                    }
                }
            }
            return zegoRtmSdkManager
        }
    }

    private var mZim: ZIM? = null

    /**
     * 创建 ZIM 实例
     */
    fun createInstance(application: Application, appID: String, logFile: String) {
        val appConfig = ZIMAppConfig()
        appConfig.appID = appID.toLong() //替换为您申请到的 AppID
        setLogFile(logFile)
        if (mZim == null) {
            mZim = ZIM.create(appConfig, application)
        }

        mZim?.setEventHandler(object : ZIMEventHandler() {
            override fun onConnectionStateChanged(
                zim: ZIM?,
                state: ZIMConnectionState?,
                event: ZIMConnectionEvent?,
                extendedData: JSONObject?,
            ) {
                super.onConnectionStateChanged(zim, state, event, extendedData)
                if (event != null) {
                    if (state != null) {
                        mLoginStateCallback?.onConnectionStateChanged(state.value(), event.value())
                    }
                }
                when (state) {
                    ZIMConnectionState.DISCONNECTED -> { //未连接状态，在登录前和登出之后进入该状态。
                        when (event) {
                            ZIMConnectionEvent.LOGIN_TIMEOUT -> { //连接超时。
                                mLoginStateCallback?.onConnectTimeOut()
                            }
                            ZIMConnectionEvent.LOGIN_INTERRUPTED -> { //网络连接临时中断。
                                mLoginStateCallback?.onConnectInterrupted()
                            }
                            ZIMConnectionEvent.KICKED_OUT -> { //被踢下线。
                                mLoginStateCallback?.onConnectAborted()
                                mLoginConnectCallback?.onConnectAborted()
                            }
                            ZIMConnectionEvent.TOKEN_EXPIRED -> { //因登录 Token 过期而断开连接。
                                mLoginStateCallback?.onConnectTokenExpired()
                            }
                            else -> {

                            }
                        }
                    }
                    ZIMConnectionState.CONNECTED -> {
                        mLoginStateCallback?.onConnectSuccess()
                        mLoginConnectCallback?.onConnected()

                    }
                    ZIMConnectionState.RECONNECTING -> {
                        mLoginConnectCallback?.onReconnecting()
                    }
                    else -> {}
                }
            }

            override fun onReceiveRoomMessage(
                zim: ZIM?,
                messageList: ArrayList<ZIMMessage>?,
                fromRoomID: String?,
            ) {
                super.onReceiveRoomMessage(zim, messageList, fromRoomID)
                for (zimMessage in messageList!!) {
                    if (zimMessage is ZIMCommandMessage) {
                        if (zimMessage.message.isNotEmpty()) {
                            mReceiveRoomMessageCallback?.onReceiveRoomMessage(zimMessage.message.decodeToString())
                        }
                    }
                }
            }

            override fun onTokenWillExpire(zim: ZIM?, second: Int) {
                super.onTokenWillExpire(zim, second)
                mTokenWillExpireCallback?.onTokenWillExpire()
            }

            /**
             * 邀请者收到接受邀请回调
             */
            override fun onCallInvitationAccepted(
                zim: ZIM?,
                info: ZIMCallInvitationAcceptedInfo?,
                callID: String?,
            ) {
                super.onCallInvitationAccepted(zim, info, callID)
                mCallInvitationAcceptedCallback?.onCallInvitationAccepted()
            }

            /**
             * 邀请者收到拒绝邀请回调
             */
            override fun onCallInvitationRejected(
                zim: ZIM?,
                info: ZIMCallInvitationRejectedInfo?,
                callID: String?,
            ) {
                super.onCallInvitationRejected(zim, info, callID)
                mLocalInvitationRefusedCallback?.onCallInvitationRejected()
            }

            /**
             * 邀请者收到呼叫邀请超时的回调
             */
            override fun onCallInviteesAnsweredTimeout(
                zim: ZIM?,
                invitees: java.util.ArrayList<String>?,
                callID: String?,
            ) {
                super.onCallInviteesAnsweredTimeout(zim, invitees, callID)
                mCallInviteesAnsweredFailureCallback?.onCallInviteesAnsweredFailure(2)
            }

            /**
             * 被邀请者收到呼叫邀请回调
             */
            override fun onCallInvitationReceived(
                zim: ZIM?,
                info: ZIMCallInvitationReceivedInfo?,
                callID: String?,
            ) {
                super.onCallInvitationReceived(zim, info, callID)
                if (info != null) {
                    val jsonObject = JSONObject(info.extendedData)
                    val callerId = jsonObject.optString("callerUid")
                    val channelId = jsonObject.optString("channel")
                    mCallInvitationReceivedCallback?.onCallInvitationReceived(
                        callerId,
                        channelId,
                        info.extendedData
                    )
                }
            }

            /**
             * 被邀请者收到取消邀请回调
             */
            override fun onCallInvitationCancelled(
                zim: ZIM?,
                info: ZIMCallInvitationCancelledInfo?,
                callID: String?,
            ) {
                super.onCallInvitationCancelled(zim, info, callID)
                mCallInvitationCancelledCallback?.onCallInvitationCancelled()
            }

            /**
             * 被邀请者收到呼叫邀请超时的回调
             */
            override fun onCallInvitationTimeout(zim: ZIM?, callID: String?) {
                super.onCallInvitationTimeout(zim, callID)
                mCallInvitationFailureCallback?.onCallInvitationFailure(3)
            }

            override fun onRoomMemberJoined(
                zim: ZIM?,
                memberList: java.util.ArrayList<ZIMUserInfo>?,
                roomID: String?,
            ) {
                super.onRoomMemberJoined(zim, memberList, roomID)
                if (memberList != null) {
                    for (user in memberList) {
                        mRoomMemberJoinedCallback?.onRoomMemberJoined(user.userID)
                    }
                }
            }

            /**
             * 其他成员离开房间的回调
             */
            override fun onRoomMemberLeft(
                zim: ZIM?,
                memberList: java.util.ArrayList<ZIMUserInfo>?,
                roomID: String?,
            ) {
                super.onRoomMemberLeft(zim, memberList, roomID)
                if (memberList != null) {
                    for (user in memberList) {
                        mRoomMemberLeftCallback?.onRoomMemberLeft(user.userID)
                    }
                }
            }

        })
    }

    /**
     * 设置日志存储路径
     */
    private fun setLogFile(logFile: String) {
        val logConfig = ZIMLogConfig()
        val file = File(logFile)
        if (!file.exists()) {
            file.mkdirs()
        }
        logConfig.logPath = file.absolutePath
        ZIM.setLogConfig(logConfig)
    }


    /**
     * 登录 ZIM
     */
    fun login(
        userID: String,
        userName: String,
        token: String,
        callback: RTMSDKManager.OnLoggedInCallback,
    ) {
        val zimUserInfo = ZIMUserInfo()
        zimUserInfo.userID = userID
        zimUserInfo.userName = userName

        mZim?.login(zimUserInfo, token) { errorInfo ->
            if (errorInfo != null) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    callback.onLoggedSuccess()
                } else {
                    callback.onLoggedFail(
                        errorCode = errorInfo.code.value(),
                        errorDesc = errorInfo.message
                    )
                }
            }
        }
    }

    /**
     * 创建房间
     */
    fun createRoom(
        channel: String,
        callback: RTMSDKManager.OnRoomEnteredCallback,
    ) {
        val zimRoomInfo = ZIMRoomInfo()
        zimRoomInfo.roomID = channel
        zimRoomInfo.roomName = channel
        mZim?.enterRoom(
            zimRoomInfo, ZIMRoomAdvancedConfig()
        ) { _, errorInfo ->
            if (errorInfo != null) {
                when (errorInfo.code) {
                    ZIMErrorCode.SUCCESS -> {
                        callback.onSuccess()
                    }
                    else -> {
                        callback.onFailure(errorInfo.code.value())
                    }
                }
            }
        }

    }

    /**
     * 离开房间
     */
    fun leaveRoom(roomID: String) {
        if (roomID.isNotEmpty()) {
            mZim?.leaveRoom(roomID
            ) { _, errorInfo ->
                Log.e(
                    TAG,
                    "leaveRoom--->" + errorInfo?.code + "----" + errorInfo?.message
                )
            }
        }
    }

    /**
     * 发起呼叫邀请
     */
    fun callInvite(
        userID: String?,
        paramsContent: String,
        callback: RTMSDKManager.OnCallInvitationSentCallback,
    ) {
        val invitees = ArrayList<String>()
        val config = ZIMCallInviteConfig()
        config.timeout = 30
        config.extendedData = paramsContent
        userID?.let { invitees.add(it) }
        mZim?.callInvite(
            invitees, config
        ) { callID, _, errorInfo ->
            if (errorInfo != null) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    callback.onCallInvitationSentSuccess(callID)
                } else {
                    callback.onCallInvitationSentFail(errorCode = errorInfo.code.value())
                }
            }
        }
    }

    /**
     * 接受呼叫邀请
     */
    fun callAccept(
        callID: String,
        callback: RTMSDKManager.OnCallAcceptanceSentCallback,
    ) {
        mZim?.callAccept(
            callID, ZIMCallAcceptConfig()
        ) { _, errorInfo ->
            if (errorInfo?.code == ZIMErrorCode.SUCCESS) {
                callback.onCallAcceptanceSentSuccess()
            } else {
                if (errorInfo != null) {
                    callback.onCallAcceptanceSentFail(errorInfo.code.value())
                }
            }
        }
    }

    /**
     * 取消呼叫邀请
     */
    fun callCancel(
        userID: String?,
        callID: String?,
        callback: RTMSDKManager.OnCallCancelSentCallback,
    ) {
        val invitees = ArrayList<String>()
        val config = ZIMCallCancelConfig()
        if (userID != null) {
            invitees.add(userID)
        }
        mZim?.callCancel(
            invitees, callID, config
        ) { _, _, errorInfo ->
            if (errorInfo?.code == ZIMErrorCode.SUCCESS) {
                callback.onCallCancelSentSuccess()
            } else {
                if (errorInfo != null) {
                    callback.onCallCancelSentFail(errorCode = errorInfo.code.value())
                }
            }
        }
    }

    /**
     * 拒绝呼叫邀请
     */
    fun callReject(
        callID: String,
        callback: RTMSDKManager.OnCallRejectionSentCallback,
    ) {
        mZim?.callReject(
            callID, ZIMCallRejectConfig()
        ) { _, errorInfo ->
            if (errorInfo?.code == ZIMErrorCode.SUCCESS) {
                callback.onCallRejectionSentSuccess()
            } else {
                if (errorInfo != null) {
                    callback.onCallRejectionSentFail(errorCode = errorInfo.code.value())
                }
            }
        }
    }

    /**
     * 发送自定义消息
     */
    fun sendChannelMessage(
        message: String,
        channel: String,
        mMessageSentCallback: RTMSDKManager.OnMessageSentCallback,
    ) {
        val zimMessage = ZIMCommandMessage()
        zimMessage.message = message.toByteArray(StandardCharsets.UTF_8)
        mZim?.sendMessage(zimMessage,
            channel,
            ZIMConversationType.ROOM,
            ZIMMessageSendConfig(),
            object : ZIMMessageSentCallback {
                override fun onMessageAttached(message: ZIMMessage?) {

                }

                override fun onMessageSent(message: ZIMMessage?, errorInfo: ZIMError?) {
                    if (errorInfo != null) {
                        if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                            mMessageSentCallback.onMessageSentSuccess()
                        } else {
                            mMessageSentCallback.onMessageSentFail(errorInfo.code.value())
                        }
                    }
                }

            })
    }

    /**
     * 退出登录
     */
    fun logout() {
        mZim?.logout()
    }

    /**
     * 销毁 ZIM 实例
     */
    fun destroy() {
        mZim?.destroy()
    }
}