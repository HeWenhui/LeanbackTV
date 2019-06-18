package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2018/7/11.
 * 语音反馈irc
 */
public class SpeechFeedBackIRCBll extends LiveBaseBll implements com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechFeedBackHttp, NoticeAction, TopicAction {
    com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechFeedBackBll speechFeedBackAction;

    public SpeechFeedBackIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void saveStuTalkSource(String talkSourcePath, String service) {
        getHttpManager().saveStuTalkSource(mGetInfo.getStuId(), talkSourcePath, service, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d( "saveStuTalkSource:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.d( "saveStuTalkSource:onPmFailure" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d( "saveStuTalkSource:onPmError" + responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
        String status = mainRoomstatus.getOnVideoChat();
        if ("on".equals(status) && LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
            final String roomId = mainRoomstatus.getAgoraVoiceChatRoom();
            if (speechFeedBackAction != null) {
                speechFeedBackAction.start(roomId);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createBll();
                        speechFeedBackAction.start(roomId);
                    }
                });
            }
        } else {
            if (speechFeedBackAction != null) {
                speechFeedBackAction.stop();
            }
        }
    }

    private void onStaus(JSONObject object) {
        if (speechFeedBackAction != null) {
            try {
                String status = object.getString("status");
                if ("on".equals(status) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                    String roomId = object.getString("roomId");
                    speechFeedBackAction.start(roomId);
                } else {
                    speechFeedBackAction.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createBll() {
        if (speechFeedBackAction != null) {
            return;
        }
        com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechFeedBackBll speechFeedBackBll = new com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechFeedBackBll(activity, SpeechFeedBackIRCBll.this);
        speechFeedBackBll.setGetInfo(mGetInfo);
        speechFeedBackBll.setBottomContent(mRootView);
        speechFeedBackBll.setLiveAndBackDebug(contextLiveAndBackDebug);
        speechFeedBackAction = speechFeedBackBll;
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject object, int type) {
        String msg = "onNotice";
        switch (type) {
            case XESCODE.SPEECH_FEEDBACK: {
                final String from = object.optString("roomId");
                if ("voice_plan_ios".equals(from)) {
                    return;
                }
                msg += ",SPEECH_FEEDBACK";
                if (speechFeedBackAction != null) {
                    onStaus(object);
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createBll();
                            onStaus(object);
                        }
                    });
                }
                break;
            }
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.SPEECH_FEEDBACK};
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (speechFeedBackAction != null) {
            speechFeedBackAction.stop();
        }
    }
}
