package com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechfeedback.business;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * 语音互动
 */
public class SpeechCollectiveIRCBll extends LiveBaseBll implements SpeechFeedBackHttp, NoticeAction, TopicAction {
    SpeechCollectiveBll speechCollectiveBll;
    private boolean isFirstCreate = true;

    public SpeechCollectiveIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        isFirstCreate = true;
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
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (speechCollectiveBll != null) {
            speechCollectiveBll.stop();
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.d("data=" + jsonObject);
        if (!isFirstCreate) {
            return;
        }
        isFirstCreate = false;
        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
        String status = mainRoomstatus.getOnGroupSpeech();
        int isVoiceInteraction = mGetInfo.getIsVoiceInteraction();
        if (isVoiceInteraction == 1 && "on".equals(status)
                && LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
            final String roomId = mainRoomstatus.getGroupSpeechRoom();
            if (speechCollectiveBll != null) {
                speechCollectiveBll.start(roomId);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是退出直播间再进来，不弹出倒计时和灰色收音球
                        ShareDataManager.getInstance().put("isOnTopic", true, ShareDataManager.SHAREDATA_USER);
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
                int isVoiceInteraction = mGetInfo.getIsVoiceInteraction();
                if (isVoiceInteraction == 1 && "on".equals(status)
                        && LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
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
            case XESCODE.SPEECH_COLLECTIVE: {
                ShareDataManager.getInstance().put("isOnTopic", false, ShareDataManager.SHAREDATA_USER);
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
                XESCODE.SPEECH_COLLECTIVE};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechCollectiveBll != null) {
            speechCollectiveBll.stop();
        }
    }
}
