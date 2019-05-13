package com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechcollective.business;

import android.app.Activity;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveHttp;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveNo2Bll;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.http.SpeechCollectiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;

import org.json.JSONObject;

/**
 * 语音互动
 */
public class SpeechCollectiveIRCBll extends LiveBaseBll implements com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechFeedBackHttp, NoticeAction, TopicAction {
    private SpeechCollectiveNo2Bll speechCollectiveBll;
    private boolean isFirstCreate = true;
    private SpeechCollectiveHttpManager speechCollectiveHttpManager;
    private SpeechCollectiveHttp collectiveHttp;
    /**
     * 是不是有分组
     */
    private boolean haveTeam = false;

    public SpeechCollectiveIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        isFirstCreate = true;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        collectiveHttp = new SpeechCollectiveHttpImpl();
        int isVoiceInteraction = mGetInfo.getIsVoiceInteraction();
        if (isVoiceInteraction == 0) {
            mLiveBll.removeBusinessBll(this);
            return;
        }
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
            haveTeam = true;
        }
//        createBll();
//        speechCollectiveBll.start("");
    }

    class SpeechCollectiveHttpImpl implements SpeechCollectiveHttp {

        @Override
        public void uploadSpeechMsg(String voiceId, String msg, AbstractBusinessDataCallBack callBack) {
            getSpeechCollectiveHttpManager().uploadSpeechMsg(voiceId, msg, callBack);
        }

        @Override
        public void sendSpeechMsg(String voiceId, String msg) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.XCR_ROOM_SPEECH_COLL);
                jsonObject.put("name", mGetInfo.getStuName());
                jsonObject.put("headImg", mGetInfo.getHeadImgPath());
                jsonObject.put("msg", msg);
                //不同组的学生互相不能看弹幕
                if (haveTeam) {
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                    String teamId = studentLiveInfo.getTeamId();
                    jsonObject.put("from", "android_" + teamId);
                    jsonObject.put("to", teamId);
                }
                mLiveBll.sendMessage(jsonObject);
            } catch (Exception e) {
                logger.e("sendSpeechMsg:", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
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
            speechCollectiveBll = null;
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
        if ("on".equals(status)) {
            final String voiceId = mainRoomstatus.getGroupSpeechRoom();
            if (speechCollectiveBll != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        speechCollectiveBll.start(voiceId);
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是退出直播间再进来，不弹出倒计时和灰色收音球
                        ShareDataManager.getInstance().put("isOnTopic", true, ShareDataManager.SHAREDATA_USER);
                        createBll();
                        speechCollectiveBll.start(voiceId);
                    }
                });
            }
        } else {
            if (speechCollectiveBll != null) {
                speechCollectiveBll.stop();
                speechCollectiveBll = null;
            }
        }
    }

    private void onStaus(String status, String voiceID) {
        if (speechCollectiveBll != null) {
            try {
                if ("on".equals(status)) {
                    speechCollectiveBll.start(voiceID);
                } else {
                    speechCollectiveBll.stop();
                    speechCollectiveBll = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    private void createBll() {
        if (speechCollectiveBll != null) {
            return;
        }
        speechCollectiveBll = new SpeechCollectiveNo2Bll(activity);
        speechCollectiveBll.setLiveGetInfo(mGetInfo);
        speechCollectiveBll.setBottomContent(mRootView);
        speechCollectiveBll.setCollectiveHttp(collectiveHttp);
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject object, int type) {
        logger.d("data=" + object);
        switch (type) {
            case XESCODE.SPEECH_COLLECTIVE: {
                ShareDataManager.getInstance().put("isOnTopic", false, ShareDataManager.SHAREDATA_USER);
                final String voiceID = object.optString("voiceId");
                final String status = object.optString("status");
                if (speechCollectiveBll != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onStaus(status, voiceID);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createBll();
                            onStaus(status, voiceID);
                        }
                    });
                }
                break;
            }
        }
    }

    public SpeechCollectiveHttpManager getSpeechCollectiveHttpManager() {
        if (speechCollectiveHttpManager == null) {
            speechCollectiveHttpManager = new SpeechCollectiveHttpManager(getHttpManager());
        }
        return speechCollectiveHttpManager;
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
    public void onDestory() {
        super.onDestory();
        if (speechCollectiveBll != null) {
            speechCollectiveBll.stop();
            speechCollectiveBll = null;
        }
    }
}
