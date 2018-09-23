package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;
import android.util.Log;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Zhang Yuansun on 2018/7/12.
 */

public class SpeechBulletScreenIRCBll extends LiveBaseBll implements TopicAction, NoticeAction, MessageAction{
    SpeechBulletScreenBll mSpeechBulletScreenAction;
    String open;
    String voiceId;
    String from;
    LiveTopic liveTopic;
    /** 是不是有分组 */
    private boolean haveTeam = false;
    public SpeechBulletScreenIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);

        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
                haveTeam = true;
            }
        }

//        JSONObject data = null;
//        try {
//            data = new JSONObject("{\"from\":\"f\",\"open\":true,\"type\":\"260\",\"voiceId\":\"2567_1533872215382\"}");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        onNotice("","",data,260);
//
//        final JSONObject finalData = data;
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onNotice("","", finalData,260);
//            }
//        },5000);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        this.liveTopic = liveTopic;
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        if ( mSpeechBulletScreenAction!=null) {
            mSpeechBulletScreenAction.onCloseSpeechBulletScreen(false);
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        Log.i(TAG,"onNotice:type="+type+"|data="+data);
        switch (type) {
            case XESCODE.XCR_ROOM_DANMU_OPEN: {
                open = data.optString("open");
                voiceId = data.optString("voiceId");
                from = data.optString("from");
                Log.i(TAG,"open="+open+"|voiceId="+voiceId+"|from="+from);
                //voice不能为空，并且发送notice老师类型的与当前直播的老师类型一致
                if ((!"".equals(voiceId)) &&
                        (LiveTopic.MODE_CLASS.equals(mGetInfo.getMode())&&"t".equals(from) || LiveTopic.MODE_TRANING.equals(mGetInfo.getMode())&&"f".equals(from))) {
                    if ("true".equals(open) ){
                        if (mSpeechBulletScreenAction == null) {
                            SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
                            speechBulletScreenBll.initView(mRootView);
                            mSpeechBulletScreenAction = speechBulletScreenBll;
                            mSpeechBulletScreenAction.setSpeechBulletScreenHttp(new LiveSpeechBulletScreenHttp());
                        }
                        if (mSpeechBulletScreenAction != null) {
                            mSpeechBulletScreenAction.onShowSpeechBulletScreen();
                        }
                    } else if ("false".equals(open)) {
                        mSpeechBulletScreenAction.onCloseSpeechBulletScreen(true);
                    }
                } else if ("".equals(voiceId)) {
                // 教师端退出情况：如果收到的260消息中的voiceId字段为空，学生退出弹幕但不要弹出提示窗口。
                    if (mSpeechBulletScreenAction != null) {
                        mSpeechBulletScreenAction.onCloseSpeechBulletScreen(false);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[] {
                XESCODE.XCR_ROOM_DANMU_OPEN, XESCODE.XCR_ROOM_DANMU_SEND
        };
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {
        if (mSpeechBulletScreenAction != null) {
            mSpeechBulletScreenAction.onMessage(target, sender, login, hostname, text, "");
        }
    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
        Log.i("LiveBll", "=====> onPrivateMessage:" + sender + ":" + login + ":" + hostname + ":" + target + ":" +
                message);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            int type = jsonObject.getInt("type");
            if (type != XESCODE.XCR_ROOM_DANMU_SEND) {
                return;
            }
        } catch (JSONException e) {
            return;
        }

        //不同组的学生互相不能看弹幕
        if (haveTeam) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            String teamId = studentLiveInfo.getTeamId();
            try {
                jsonObject = new JSONObject(message);
                String to = jsonObject.optString("to");
                if (!teamId.equals(to)) {
                    return;
                }
            } catch (JSONException e) {
                return;
            }
        }

        if (mSpeechBulletScreenAction == null) {
            SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
            speechBulletScreenBll.initView(mRootView);
            mSpeechBulletScreenAction = speechBulletScreenBll;
            mSpeechBulletScreenAction.setSpeechBulletScreenHttp(new LiveSpeechBulletScreenHttp());
        }

        if (mSpeechBulletScreenAction != null) {
            mSpeechBulletScreenAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
        }
    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    @Override
    public void onUserList(String channel, User[] users) {

    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }

    class LiveSpeechBulletScreenHttp implements SpeechBulletScreenHttp {
        /**
         * 发送弹幕消息
         */
        @Override
        public void sendDanmakuMessage(String msg) {
            Log.i(TAG,"sendDanmakuMessage()");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.XCR_ROOM_DANMU_SEND);
                jsonObject.put("name", mGetInfo.getStuName());
                jsonObject.put("headImg", mGetInfo.getHeadImgPath());
                jsonObject.put("msg", msg);
                //不同组的学生互相不能看弹幕
                if (haveTeam) {
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                    String teamId = studentLiveInfo.getTeamId();
                    jsonObject.put("from", "android_" + teamId);
                    jsonObject.put("to", teamId);
                    //如果teamId小于0，说明该生是临调生
//                    if (!StringUtils.isEmpty(teamId) && teamId.startsWith("-")) {
//                        jsonObject.put("temporary", "1");
//                    }
                }

                mLiveBll.sendMessage(jsonObject);
            } catch (Exception e) {
//            logger.e( "understand", e);
                mLogtf.e("sendDanmakuMessage", e);
            }
        }
        /**
         * http post:上传发言语句
         */
        @Override
        public void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack){
            Log.i(TAG,"uploadSpeechBulletScreen()");
            getHttpManager().uploadVoiceBarrage(mGetInfo.getId(), mGetInfo.getStuId(), voiceId, msg , requestCallBack);
        }

        @Override
        public String getHeadImgUrl() {
            return mGetInfo.getHeadImgPath();
        }

        @Override
        public String getVoiceId() {
            return voiceId;
        }
    }

    @Override
    public void onDestory() {
        if (mSpeechBulletScreenAction != null) {
            mSpeechBulletScreenAction.onDestory();
        }
    }
}
