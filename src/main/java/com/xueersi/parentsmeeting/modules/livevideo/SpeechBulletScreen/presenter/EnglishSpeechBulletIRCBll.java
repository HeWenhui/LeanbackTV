package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.EnglishSpeechBulletContract;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.view.EnglishSpeechBulletPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
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
     * 该场次语音弹幕开启次数
     */
    private String voiceBarrageCount;
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

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject data = null;
//                try {
//                    data = new JSONObject("{\"open\":true,\"type\":\"1005\"}");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                onNotice("", "", data, 260);
//            }
//        }, 1000);

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
            case XESCODE.XCR_ROOM_OPEN_VOICEBARRAGE: {
                //开启/关闭弹幕
                String open = data.optString("open");
                voiceBarrageCount = data.optString("voiceBarrageCount");
                if ("true".equals(open)) {
                    if (englishSpeechBulletView != null) {
                        englishSpeechBulletView.showSpeechBullet(mRootView);
                    }
                } else if ("false".equals(open)) {
                    englishSpeechBulletView.closeSpeechBullet(true);
                }
                break;
            }
            case XESCODE.XCR_ROOM_VOICEBARRAGE: {
                //弹幕消息
                String headImg = data.optString("headImg");
                String context = data.optString("context");
                String name = data.optString("name");
                String teamId = data.optString("teamId");
                englishSpeechBulletView.receiveDanmakuMsg(name, context, headImg);
                break;
            }

            case XESCODE.XCR_ROOM_VOICEBARRAGEPRAISE: {
                //表扬消息
                String context = data.optString("context");
                englishSpeechBulletView.receivePraiseMsg(context);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.XCR_ROOM_OPEN_VOICEBARRAGE, XESCODE.XCR_ROOM_VOICEBARRAGE, XESCODE.XCR_ROOM_VOICEBARRAGEPRAISE
        };
    }

    @Override
    public void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack) {
        JSONObject data = new JSONObject();
        try {
            data.put("subjectIds", mGetInfo.getSubjectIds());
            if (mGetInfo.getMode().equals(LiveTopic.MODE_CLASS)) {
                data.put("teaSenderId", mLiveBll.getMainTeacherStr());
            } else {
                data.put("teaSenderId", mLiveBll.getCounTeacherStr());
            }
            data.put("studentId", mGetInfo.getStuId());
            data.put("courseId", mGetInfo.getStudentLiveInfo().getCourseId());
            data.put("classId", mGetInfo.getStudentLiveInfo().getCourseId());
            data.put("liveId", mLiveBll.getLiveId());
            data.put("liveType", mGetInfo.getLiveType());
            data.put("liveType", mGetInfo.getStudentLiveInfo().getTeamId());
            data.put("bulletId", mLiveBll.getLiveId() + voiceBarrageCount);
            data.put("keywords", "");

            JSONObject content = new JSONObject();
            content.put("type", XESCODE.XCR_ROOM_VOICEBARRAGE);
            content.put("senderId", mLiveBll.getConnectNickname());
            content.put("context", msg);
            content.put("name", mGetInfo.getStuName());
            content.put("headmg", mGetInfo.getHeadImgPath());
            data.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getHttpManager().pushSpeechBullet(5, data.toString(), new HttpCallBack(false) {
            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {

            }

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

            }
        });
    }

    @Override
    public String getHeadImgUrl() {
        return mGetInfo.getHeadImgPath();
    }
}
