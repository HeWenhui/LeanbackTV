package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.ScienceSpeechBullletContract;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.view.ChineseSpeechBulletPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
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
 * Created by Zhang Yuansun on 2019/1/29.
 */

public class ChineseSpeechBulletScreenIRCBll extends LiveBaseBll implements TopicAction, NoticeAction, MessageAction, ScienceSpeechBullletContract.ScienceSpeechBulletPresenter {
    private ScienceSpeechBullletContract.ScienceSpeechBulletView speechBulletView;
    private LiveTopic mLiveTopic;
    private String open;
    private String voiceId;
    private String from;
    /**
     * 是不是有分组
     */
    private boolean haveTeam = false;
    /** 主讲老师前缀 */
    public static final String TEACHER_PREFIX = "t_";
    /** 辅导老师前缀 */
    public static final String COUNTTEACHER_PREFIX = "f_";

    public ChineseSpeechBulletScreenIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        this.speechBulletView = new ChineseSpeechBulletPager(context, false);
        speechBulletView.setPresenter(this);
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
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        if (speechBulletView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    speechBulletView.closeSpeechBullet(false);
                }
            });
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.i("onNotice:type=" + type + "|data=" + data);
        switch (type) {
            case XESCODE.XCR_ROOM_CHINESE_DANMU_OPEN: {
                open = data.optString("open");
                voiceId = data.optString("voiceId");
                from = data.optString("from");
                logger.i("open=" + open + "|voiceId=" + voiceId + "|from=" + from);
                //voice不能为空，并且发送notice老师类型的与当前直播的老师类型一致
                if ((!"".equals(voiceId)) &&
                        (LiveTopic.MODE_CLASS.equals(mGetInfo.getMode()) && "t".equals(from) || LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) && "f".equals(from))) {
                    if ("true".equals(open)) {
                        if (speechBulletView != null) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    speechBulletView.showSpeechBullet(mRootView);
                                }
                            });
                        }
                    } else if ("false".equals(open)) {
                        if (speechBulletView != null) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    speechBulletView.closeSpeechBullet(true);
                                }
                            });
                        }
                    }
                }
               /* else if ("".equals(voiceId)) {
                    // 教师端退出情况：如果收到的260消息中的voiceId字段为空，学生退出弹幕但不要弹出提示窗口。
                    if (speechBulletView != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                speechBulletView.closeSpeechBullet(false);
                            }
                        });
                    }
                }*/
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.XCR_ROOM_CHINESE_DANMU_OPEN, XESCODE.XCR_ROOM_CHINESE_DANMU_SEND
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

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(message);
            int type = jsonObject.getInt("type");
            if (type != XESCODE.XCR_ROOM_CHINESE_DANMU_SEND) {
                return;
            }
        } catch (JSONException e) {
            return;
        }

        //不同组的学生互相不能看弹幕
//        if (haveTeam) {
//            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
//            String teamId = studentLiveInfo.getTeamId();
//            try {
//                jsonObject = new JSONObject(message);
//                String to = jsonObject.optString("to");
//                if (!teamId.equals(to)) {
//                    return;
//                }
//            } catch (JSONException e) {
//                return;
//            }
//        }
        try {
            jsonObject = new JSONObject(message);
            final String name = jsonObject.optString("name");
            final String headImgUrl = jsonObject.optString("headImg");
            final String msg = jsonObject.optString("msg");
            if (speechBulletView != null) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        speechBulletView.receiveDanmakuMsg(name, msg, headImgUrl, true, mRootView);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
//        if (sourceNick.startsWith(TEACHER_PREFIX)) {
//            logger.i("onQuit:mainTeacher quit");
//            if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && speechBulletView != null) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        speechBulletView.closeSpeechBullet(false);
//                    }
//                });
//            }
//        } else if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
//            logger.i("onQuit:Counteacher quit");
//            if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && speechBulletView != null) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        speechBulletView.closeSpeechBullet(false);
//                    }
//                });
//            }
//        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }

    @Override
    public void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack) {
        logger.i("uploadSpeechBulletScreen()");
        getHttpManager().uploadVoiceBarrage(mGetInfo.getId(), mGetInfo.getStuId(), voiceId, msg, requestCallBack);
    }

    @Override
    public void sendDanmakuMessage(String msg) {
        logger.i("sendDanmakuMessage()");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_CHINESE_DANMU_SEND);
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
            logger.e("sendDanmakuMessage:", e);
        }
    }

    @Override
    public String getVoiceId() {
        return voiceId;
    }

    @Override
    public String getHeadImgUrl() {
        return mGetInfo.getHeadImgPath();
    }

    @Override
    public String getStuSex() {
        return mGetInfo.getStuSex();
    }

    @Override
    public void onStop() {
        speechBulletView.onStop();
    }
}