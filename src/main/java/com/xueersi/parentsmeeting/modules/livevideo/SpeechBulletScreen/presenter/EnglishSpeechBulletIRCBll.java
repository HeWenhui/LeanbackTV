package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.presenter;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.EnglishSpeechBulletContract;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.view.EnglishSpeechBulletPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private int voiceBarrageCount;
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
//                    data = new JSONObject("{\"open\":true}");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                onNotice("", "", data, 1005);
//            }
//        }, 20000);
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject data = null;
//                try {
//                    data = new JSONObject("{\"open\":false}");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                onNotice("", "", data, 1005);
//            }
//        }, 10000);

    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        if (englishSpeechBulletView != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    englishSpeechBulletView.closeSpeechBullet(false);
                }
            });
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.i("onTopic: jsonObject= " + jsonObject.toString());
        this.liveTopic = liveTopic;
        if (liveTopic.getMainRoomstatus().isOpenVoiceBarrage()) {
            voiceBarrageCount = liveTopic.getMainRoomstatus().getVoiceBarrageCount();
            if (englishSpeechBulletView != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        englishSpeechBulletView.showSpeechBullet(mRootView);
                    }
                });
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.i("onNotice: jsonObject= " + data.toString());
        switch (type) {
            case XESCODE.XCR_ROOM_OPEN_VOICEBARRAGE: {
                //开启/关闭弹幕
                String open = data.optString("open");
                voiceBarrageCount = data.optInt("voiceBarrageCount");
                if ("true".equals(open)) {
                    if (englishSpeechBulletView != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                englishSpeechBulletView.showSpeechBullet(mRootView);
                            }
                        });
                    }
                } else if ("false".equals(open)) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            englishSpeechBulletView.closeSpeechBullet(true);
                        }
                    });
                }
                break;
            }
            case XESCODE.XCR_ROOM_VOICEBARRAGE: {
                //弹幕消息
                String headImg = data.optString("headImg");
                String context = data.optString("context");
                String name = data.optString("name");
                String teamId = data.optString("teamId");
                englishSpeechBulletView.receiveDanmakuMsg(name, context, headImg, mRootView);
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
        JSONObject requestJson = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            if (mGetInfo.getSubjectIds().length != 0) {
                data.put("subjectIds", Integer.valueOf(mGetInfo.getSubjectIds()[0]).intValue());
            }
            if (mGetInfo.getMode().equals(LiveTopic.MODE_CLASS)) {
                data.put("teaSenderId", mLiveBll.getMainTeacherStr());
            } else {
                data.put("teaSenderId", mLiveBll.getCounTeacherStr());
            }
            data.put("studentId", mGetInfo.getStuId());
            data.put("courseId", mGetInfo.getStudentLiveInfo().getCourseId());
            data.put("classId", mGetInfo.getStudentLiveInfo().getClassId());
            data.put("liveId", mLiveBll.getLiveId());
            data.put("liveType", mGetInfo.getLiveType());
            data.put("teamId", mGetInfo.getStudentLiveInfo().getTeamId());
            data.put("bulletId", mLiveBll.getLiveId() + "_" + voiceBarrageCount);
            String[] strings = msg.split(" ");
            JSONArray keywords = new JSONArray();
            for (int i = 0; i < strings.length; i++) {
                keywords.put(strings[i]);
            }
            data.put("keywords", keywords);

            JSONObject content = new JSONObject();
            content.put("type", XESCODE.XCR_ROOM_VOICEBARRAGE + "");
            content.put("senderId", mLiveBll.getConnectNickname());
            content.put("context", msg);
            content.put("name", mGetInfo.getStandLiveName());
            content.put("headImg", mGetInfo.getHeadImgPath());
            data.put("content", content);

            requestJson.put("type", 5);
            requestJson.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getHttpManager().pushSpeechBullet(requestJson.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.i("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.i("onPmSuccess: responseEntity = " + response.body().string().toString());
            }
        });
    }

    @Override
    public String getHeadImgUrl() {
        return mGetInfo.getHeadImgPath();
    }
}
