package com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business;

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
 * 语音互动
 */
public class SpeechCollectiveIRCBll extends LiveBaseBll implements SpeechFeedBackHttp, NoticeAction, TopicAction {
    SpeechCollectiveBll speechCollectiveBll;

    public SpeechCollectiveIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void saveStuTalkSource(String talkSourcePath, String service) {
        getHttpManager().saveStuTalkSource(mGetInfo.getStuId(), talkSourcePath, service, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("saveStuTalkSource:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.d("saveStuTalkSource:onPmFailure" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("saveStuTalkSource:onPmError" + responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("data=" + jsonObject);
        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
        String status = mainRoomstatus.getOnGroupSpeech();
        if ("on".equals(status) && LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
            final String roomId = mainRoomstatus.getGroupSpeechRoom();
            if (speechCollectiveBll != null) {
                speechCollectiveBll.start(roomId);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createBll();
                        speechCollectiveBll.start(roomId);
                    }
                });
            }
        } else {
            if (speechCollectiveBll != null) {
                speechCollectiveBll.stop();
            }
        }
    }

    private void onStaus(String status, String roomId) {
        if (speechCollectiveBll != null) {
            try {
                if ("on".equals(status) && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                    speechCollectiveBll.start(roomId);
                } else {
                    speechCollectiveBll.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createBll() {
        if (speechCollectiveBll != null) {
            return;
        }
        SpeechCollectiveBll speechFeedBackBll = new SpeechCollectiveBll(activity, SpeechCollectiveIRCBll.this);
        speechFeedBackBll.setBottomContent(mRootView);
        speechCollectiveBll = speechFeedBackBll;
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject object, int type) {
        logger.d("data=" + object);
        switch (type) {
            case XESCODE.SPEECH_FEEDBACK: {
                final String from = object.optString("roomId");
                final String status = object.optString("status");
                if (!"voice_plan_ios".equals(from)) {
                    return;
                }
                if (speechCollectiveBll != null) {
                    onStaus(status, from);
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createBll();
                            onStaus(status, from);
                        }
                    });
                }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (speechCollectiveBll != null) {
            speechCollectiveBll.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (speechCollectiveBll != null) {
            speechCollectiveBll.onPause();
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
        if (speechCollectiveBll != null) {
            speechCollectiveBll.stop();
        }
    }
}
