package com.adance.module_rtm.rtm

import android.app.Application

/**
 * rtm管理类
 * Created by wenhao 2022/12/13
 */
class RTMSDKManager {

    companion object {
        private var sdkManager: RTMSDKManager? = null
        const val RTM_AGORA = "agora" //声网
        const val RTM_ZEGO = "zego" //即构
        var rtm: String? = null
        fun instance(): RTMSDKManager? {
            if (sdkManager == null) {
                synchronized(RTMSDKManager::class.java) {
                    if (sdkManager == null) {
                        sdkManager = RTMSDKManager()
                    }
                }
            }
            return sdkManager
        }

        var mLoginConnectCallback: OnLoginConnectCallback? = null

        //login state callback
        var mLoginStateCallback: OnLoginStateCallback? = null

        //room message callback
        var mReceiveRoomMessageCallback: OnReceiveRoomMessageCallback? = null

        //token Expire callback
        var mTokenWillExpireCallback: OnTokenWillExpireCallback? = null

        //call accept callback
        var mCallInvitationAcceptedCallback: OnCallInvitationAcceptedCallback? = null

        //refused callback
        var mLocalInvitationRefusedCallback: OnLocalInvitationRefusedCallback? = null

        //cancel callback
        var mLocalInvitationCanceledCallback: OnLocalInvitationCanceledCallback? = null

        //received callback
        var mCallInvitationReceivedCallback: OnCallInvitationReceivedCallback? = null

        //Cancelled callback
        var mCallInvitationCancelledCallback: OnCallInvitationCancelledCallback? = null

        //call invitees Failure callback
        var mCallInviteesAnsweredFailureCallback: OnCallInviteesAnsweredFailureCallback? = null

        //call Failure callback
        var mCallInvitationFailureCallback: OnCallInvitationFailureCallback? = null

        //member joined callback
        var mRoomMemberJoinedCallback: OnRoomMemberJoinedCallback? = null

        //member left callback
        var mRoomMemberLeftCallback: OnRoomMemberLeftCallback? = null
    }

    /**
     * 根据不同的rtm创建对应的实例
     */
    fun create(application: Application, appID: String, logFile: String) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.createInstance(application, appID, logFile)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.createInstance(application, appID, logFile)
        }
    }

    /**
     * 根据不同的rtm登录对应的信令
     */
    fun login(
        userID: String,
        userName: String,
        token: String,
        callback: OnLoggedInCallback,
    ) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.login(token, userID, callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.login(userID, userName, token, callback)
        }
    }

    fun createRoom(channel: String, callback: OnRoomEnteredCallback) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.createRoom(channel, callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.createRoom(channel, callback)
        }
    }

    /**
     * 呼叫邀请
     * userID 被邀请id
     */
    fun callInvite(
        userID: String?,
        channel: String?,
        paramsContent: String,
        callback: OnCallInvitationSentCallback,
    ) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.callInvite(userID, channel, paramsContent, callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.callInvite(userID, paramsContent, callback)
        }
    }

    /**
     * 接受呼叫邀请
     * callID 呼叫人id
     */
    fun callAccept(
        callID: String,
        callback: OnCallAcceptanceSentCallback,
    ) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.callAccept(callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.callAccept(callID, callback)
        }
    }

    /**
     * 取消呼叫邀请
     * userID 被邀请id
     * callID 呼叫人id
     */
    fun callCancel(
        userID: String? = null,
        callID: String? = null,
        callback: OnCallCancelSentCallback,
    ) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.callCancel(callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.callCancel(userID, callID, callback)
        }
    }

    /**
     * 拒绝呼叫邀请
     */
    fun callReject(
        callID: String,
        callback: OnCallRejectionSentCallback,
    ) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.callReject(callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.callReject(callID, callback)
        }
    }

    /**
     * 发送自定义消息
     */
    fun sendChannelMessage(message: String, channel: String, callback: OnMessageSentCallback) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.sendChannelMessage(message, callback)
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.sendChannelMessage(message, channel, callback)
        }
    }

    /**
     * 离开房间
     */
    fun leaveRoom(roomID: String) {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.leaveRoom()
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.leaveRoom(roomID)
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        if (rtm.isNullOrEmpty()) {
            return
        }
        if (RTM_AGORA == rtm) {
            AgoraRtmSdkManager.instance()?.logout()
        } else if (RTM_ZEGO == rtm) {
            ZegoRtmSdkManager.instance()?.logout()
        }
    }

    interface OnLoggedInCallback {
        fun onLoggedSuccess()
        fun onLoggedFail(errorCode: Int, errorDesc: String)
    }

    interface OnLoginConnectCallback {
        //被踢掉线
        fun onConnectAborted()
        //重连中
        fun onReconnecting()
        //连接成功
        fun onConnected()
    }

    interface OnLoginStateCallback {
        fun onConnectionStateChanged(state: Int, reason: Int)

        //登录成功
        fun onConnectSuccess()

        //登录超时
        fun onConnectTimeOut()

        //登录失败
        fun onConnectFail()

        //登录中断
        fun onConnectInterrupted()

        //退出登录
        fun onConnectLogout()

        //禁止登录
        fun onConnectBanned()

        //多个id登录
        fun onConnectRemoteLogin()

        //登录token过期
        fun onConnectTokenExpired()

        //被踢掉线
        fun onConnectAborted()
    }

    interface OnReceiveRoomMessageCallback {
        fun onReceiveRoomMessage(msgStr: String)
    }

    interface OnTokenWillExpireCallback {
        fun onTokenWillExpire()
    }

    interface OnCallInvitationAcceptedCallback {
        fun onCallInvitationAccepted()
    }

    interface OnLocalInvitationRefusedCallback {
        fun onCallInvitationRejected()
    }

    interface OnLocalInvitationCanceledCallback {
        fun onCallInvitationCancel()
    }

    interface OnCallInvitationReceivedCallback {
        fun onCallInvitationReceived(callerId: String, channelId: String, extendedData: String)
    }

    interface OnCallInvitationCancelledCallback {
        fun onCallInvitationCancelled()
    }

    interface OnCallInviteesAnsweredFailureCallback {
        fun onCallInviteesAnsweredFailure(errorCode: Int)
    }

    interface OnCallInvitationFailureCallback {
        fun onCallInvitationFailure(errorCode: Int)
    }

    interface OnRoomMemberJoinedCallback {
        fun onRoomMemberJoined(userID: String)
    }

    interface OnRoomMemberLeftCallback {
        fun onRoomMemberLeft(userID: String)
    }

    interface OnCallInvitationSentCallback {
        fun onCallInvitationSentSuccess(callID: String)
        fun onCallInvitationSentFail(errorCode: Int)
    }

    interface OnCallAcceptanceSentCallback {
        fun onCallAcceptanceSentSuccess()
        fun onCallAcceptanceSentFail(errorCode: Int)
    }

    interface OnCallCancelSentCallback {
        fun onCallCancelSentSuccess()
        fun onCallCancelSentFail(errorCode: Int)
    }

    interface OnCallRejectionSentCallback {
        fun onCallRejectionSentSuccess()
        fun onCallRejectionSentFail(errorCode: Int)
    }

    interface OnRoomEnteredCallback {
        fun onSuccess()
        fun onFailure(errorCode: Int)
    }

    interface OnMessageSentCallback {
        fun onMessageSentSuccess()
        fun onMessageSentFail(errorCode: Int)
    }

}