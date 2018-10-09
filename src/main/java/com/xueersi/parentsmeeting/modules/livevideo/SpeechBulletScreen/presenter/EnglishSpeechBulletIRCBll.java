package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.EnglishSpeechBulletContract;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenBll;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.view.EnglishSpeechBulletPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ZhangYuansun on 2018/9/14
 * 小学英语语音弹幕
 * MVP：Presenter层
 * 监听IRC消息,处理业务逻辑
 */
public class EnglishSpeechBulletIRCBll extends LiveBaseBll implements TopicAction, NoticeAction, EnglishSpeechBulletContract.EnglishSpeechBulletPresenter {
    private LiveTopic liveTopic;
    /**
     * 语音弹幕开启&关闭指令
     */
    private String open;
    /**
     * 语音弹幕场次ID
     */
    private String voiceId;
    /**
     * 语音弹幕开启老师类型："t" 主讲 "f" 辅导
     */
    private String from;
    /**
     * 学生有无分组
     */
    private boolean haveTeam = false;
    /**
     * MVP模式V层接口
     */
    private EnglishSpeechBulletContract.EnglishSpeechBulletView englishSpeechBulletView;

    public EnglishSpeechBulletIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        this.englishSpeechBulletView = new EnglishSpeechBulletPager(context);
        englishSpeechBulletView.setPresenter(this);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            // teamID不为空&不等于0，说明学生有分组
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
                haveTeam = true;
            }
        }

        JSONObject data = null;
        try {
            data = new JSONObject("{\"from\":\"f\",\"open\":true,\"type\":\"260\",\"voiceId\":\"2567_1533872215382\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onNotice("", "", data, 260);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        if (englishSpeechBulletView != null) {
            englishSpeechBulletView.closeSpeechBullet(false);
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        this.liveTopic = liveTopic;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.XCR_ROOM_DANMU_OPEN: {
                if (englishSpeechBulletView == null) {

                }
                open = data.optString("open");
                voiceId = data.optString("voiceId");
                from = data.optString("from");
                //voice不能为空，并且发送notice老师类型的与当前直播的老师类型一致
                if ((!StringUtils.isEmpty(voiceId)) &&
                        (LiveTopic.MODE_CLASS.equals(mGetInfo.getMode()) && "t".equals(from) || LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) && "f".equals(from))) {
                    if ("true".equals(open)) {
                        if (englishSpeechBulletView != null) {
                            englishSpeechBulletView.showSpeechBullet(mRootView);
                        }
                    } else if ("false".equals(open)) {
                        englishSpeechBulletView.closeSpeechBullet(true);
                    }
                } else if ("".equals(voiceId)) {
                    // 教师端退出情况：如果收到的260消息中的voiceId字段为空，学生退出弹幕但不要弹出提示窗口。
                    if (englishSpeechBulletView != null) {
                        englishSpeechBulletView.closeSpeechBullet(false);
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
        return new int[]{
                XESCODE.XCR_ROOM_DANMU_OPEN, XESCODE.XCR_ROOM_DANMU_SEND
        };
    }

    @Override
    public void sendDanmakuMessage(String msg) {
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
            logger.e("sendDanmakuMessage", e);
        }
    }

    @Override
    public void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack) {
        getHttpManager().uploadVoiceBarrage(mGetInfo.getId(), mGetInfo.getStuId(), voiceId, msg, requestCallBack);
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
