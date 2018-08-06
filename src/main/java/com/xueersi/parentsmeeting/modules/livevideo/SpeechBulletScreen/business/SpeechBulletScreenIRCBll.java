package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
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
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Zhang Yuansun on 2018/7/12.
 */

public class SpeechBulletScreenIRCBll extends LiveBaseBll implements TopicAction, NoticeAction, MessageAction{
    SpeechBulletScreenBll mSpeechBulletScreenAction;
    private LiveTopic mLiveTopic = new LiveTopic();
    /** 是不是有分组 */
    private boolean haveTeam = false;
    public SpeechBulletScreenIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mLiveTopic = mLiveBll.getLiveTopic();
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
        if (mSpeechBulletScreenAction == null) {
            SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
            speechBulletScreenBll.initView(mRootView);
            mSpeechBulletScreenAction = speechBulletScreenBll;
            mSpeechBulletScreenAction.setSpeechBulletScreenHttp(new LiveSpeechBulletScreenHttp());
        }
        mSpeechBulletScreenAction.onStartSpeechBulletScreen();

    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.XCR_ROOM_DANMU_OPEN: {
                if (mSpeechBulletScreenAction == null) {
                    SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
                    speechBulletScreenBll.initView(mRootView);
                    mSpeechBulletScreenAction = speechBulletScreenBll;
                    mSpeechBulletScreenAction.setSpeechBulletScreenHttp(new LiveSpeechBulletScreenHttp());
                }
                mSpeechBulletScreenAction.onStartSpeechBulletScreen();
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
        Loger.i("LiveBll", "=====> onPrivateMessage:" + sender + ":" + login + ":" + hostname + ":" + target + ":" +
                message);
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

    class LiveSpeechBulletScreenHttp implements  SpeechBulletScreenHttp {

        @Override
        public boolean sendMessage(String msg, String name) {
            boolean sendMessage = false;
            if (mLiveTopic.isDisable()) {
                return false;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "" + XESCODE.TEACHER_MESSAGE);
                    if (StringUtils.isEmpty(name)) {
                        name = mGetInfo.getStuName();
                    }
                    jsonObject.put("name", name);
                    jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
                    jsonObject.put("version", "" + mGetInfo.getHeadImgVersion());
                    jsonObject.put("msg", msg);
                    if (haveTeam) {
                        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                        String teamId = studentLiveInfo.getTeamId();
                        jsonObject.put("from", "android_" + teamId);
                        jsonObject.put("to", teamId);
                    }
                    sendMessage = mLiveBll.sendMessage(jsonObject);
                } catch (Exception e) {
                    // Loger.e(TAG, "understand", e);
                    UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
                    mLogtf.e("sendMessage", e);
                }
            }
            return sendMessage;
        }


        /**
         * 发生弹幕消息
         */
        public void sendDanmakuMessage(int ftype) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.XCR_ROOM_DANMU_SEND);
                jsonObject.put("name", mGetInfo.getStuName());
                jsonObject.put("ftype", ftype);
                mLiveBll.sendMessage(jsonObject);
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
            } catch (Exception e) {
//            Loger.e(TAG, "understand", e);
                mLogtf.e("sendDanmakuMessage", e);
            }
        }
    }

}
