package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.tal.speech.language.TalLanguage;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.AppInfoEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.util.LoadSoCallBack;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.speakerrecognition.SpeakerRecognitionerInterface;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

/**
 * Created by linyuqiang on 2018/7/5.
 * 本场成就和语音能量条
 */
public class LiveAchievementIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, LiveAchievementHttp,
        EnglishSpeekHttp, AudioRequest {
    StarInteractAction starAction;
    EnglishSpeekAction englishSpeekAction;
    AtomicBoolean audioRequest = new AtomicBoolean(false);
    EnglishSpeekMode englishSpeekMode;
    SpeakerRecognitioner speakerRecognitioner;
    private VerifyCancelAlertDialog recognizeDialog;

    public LiveAchievementIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        putInstance(LiveAchievementIRCBll.class, this);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        final long sTime = mGetInfo.getsTime();
        if (1 == getInfo.getIsAllowStar()) {
            initRecognizeDialog();
            putInstance(AudioRequest.class, this);
            putInstance(UpdateAchievement.class, new UpdateAchievement() {
                @Override
                public void getStuGoldCount() {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            String liveid = mGetInfo.getId();
                            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
                            getHttpManager().getStuGoldCount(enstuId, liveid, new HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    StarAndGoldEntity starAndGoldEntity = getHttpResponseParser().parseStuGoldCount
                                            (responseEntity);
                                    mGetInfo.setGoldCount(starAndGoldEntity.getGoldCount());
                                    mGetInfo.setStarCount(starAndGoldEntity.getStarCount());
                                    if (starAction != null) {
                                        starAction.onGetStar(starAndGoldEntity);
                                    }
                                }
                            });
                        }
                    }, 500);
                }
            });
            AppInfoEntity appInfoEntity = AppBll.getInstance().getAppInfoEntity();
            boolean voiceRecognSwitchOn = mShareDataManager.getBoolean(ShareBusinessConfig.SP_VOICE_RECOGNI_SWITCH,
                    true,
                    ShareDataManager.SHAREDATA_USER);
            if (voiceRecognSwitchOn) {
                SpeakerRecognitionerInterface.checkResoureDownload(mContext, new LoadSoCallBack() {
                    @Override
                    public void start() {
                    }

                    @Override
                    public void success() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SpeakerRecognitionerInterface speakerRecognitionerInterface =
                                        SpeakerRecognitionerInterface
                                                .getInstance();
                                boolean result = speakerRecognitionerInterface.init();
                                if (result) {
                                    String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                                    byte[] pcmdata = new byte[10];
                                    int enrollIvector = speakerRecognitionerInterface.
                                            enrollIvector(pcmdata, pcmdata.length, 0, stuId, false);
                                    if (enrollIvector != 0) {
                                        long interval = sTime * 1000 - System.currentTimeMillis();
                                        boolean allow = true;
                                        if (!LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) ||
                                                interval <= 60 * 1000) {
                                            allow = false;
                                        }
//                                        handler.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if (recognizeDialog != null && !recognizeDialog.isDialogShow()) {
//                                                    recognizeDialog.showDialog();
//                                                }
//                                            }
//                                        });
                                        if (allow) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (recognizeDialog != null && !recognizeDialog.isDialogShow()) {
                                                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext
                                                                .getResources().getString(R.string.personal_1701001));
                                                        recognizeDialog.showDialog();
                                                    }
                                                }
                                            });
                                        } else {
                                            if (mGetInfo.getPattern() == 2) {
                                                englishSpeekMode = new EnglishSpeekModeStand();
                                            } else {
                                                englishSpeekMode = new EnglishSpeekModeNomal();
                                            }
                                            initAchievement(mGetInfo.getMode());
                                        }
                                    } else {
                                        speakerRecognitioner = new SpeakerRecognitioner(activity);
                                        if (englishSpeekAction != null) {
                                            englishSpeekAction.setSpeakerRecognitioner(speakerRecognitioner);
                                        }
                                        startAchievement();
                                    }
                                } else {
                                    startAchievement();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void progress(float progress, int type) {

                    }

                    @Override
                    public void fail(int errorCode, String errorMsg) {
                        startAchievement();
                    }
                });
            } else {
                startAchievement();
            }
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGotoRecogniz) {
            isGotoRecogniz = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SpeakerRecognitionerInterface speakerRecognitionerInterface = SpeakerRecognitionerInterface
                            .getInstance();
                    boolean result = speakerRecognitionerInterface.init();
                    if (result) {
                        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                        byte[] pcmdata = new byte[10];
                        int enrollIvector = speakerRecognitionerInterface.
                                enrollIvector(pcmdata, pcmdata.length, 0, stuId, false);
                        if (enrollIvector != 0) {
                            startAchievement();
                        } else {
                            speakerRecognitioner = new SpeakerRecognitioner(activity);
                            if (englishSpeekAction != null) {
                                englishSpeekAction.setSpeakerRecognitioner(speakerRecognitioner);
                            }
                            startAchievement();
                        }
                    } else {
                        startAchievement();
                    }
                }
            }).start();
        }
    }

    boolean isGotoRecogniz = false;

    private void initRecognizeDialog() {
        recognizeDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
        recognizeDialog.initInfo("为了让开口数据更为准确，请进行声纹认证");
        recognizeDialog.setVerifyBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .personal_1701002));
                isGotoRecogniz = true;
//                SpeakerRecognitionerInterface speakerRecognitionerInterface = SpeakerRecognitionerInterface
//                        .getInstance();
//                speakerRecognitionerInterface.speakerRecognitionerFree();
                Bundle bundle = new Bundle();
                bundle.putString("from", "livevideo");
                XueErSiRouter.startModule(mContext, "/pager_personals/voicerecognize", bundle);
            }
        });
        recognizeDialog.setCancelBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .personal_1701003));
                startAchievement();
            }
        });
        recognizeDialog.setCancelShowText("取消").setVerifyShowText("去认证");

    }


    private class EnglishSpeekModeNomal implements EnglishSpeekMode {

        @Override
        public void initAchievement(String mode) {
            if (LiveAchievementIRCBll.this.starAction == null) {
                LiveAchievementBll starBll = new LiveAchievementBll(activity, mLiveType, mGetInfo,//mGetInfo
                        // .getStarCount(),
                        //mGetInfo.getGoldCount(),
                        true);
                starBll.setLiveBll(LiveAchievementIRCBll.this);
                starBll.setLiveAndBackDebug(mLiveBll);
                starBll.initView(mRootView, mContentView);
                LiveAchievementIRCBll.this.starAction = starBll;
                //能量条
                EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                if (speakerRecognitioner != null) {
                    englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                }
                boolean initView = englishSpeekBll.initView(mRootView, mGetInfo.getMode(), null, audioRequest, mContentView);
                if (initView) {
                    englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    englishSpeekBll.setLiveAndBackDebug(mLiveBll);
                    englishSpeekBll.setmShareDataManager(mShareDataManager);
                    LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekBll;
                }
            }
            if (LiveAchievementIRCBll.this.englishSpeekAction != null) {
                LiveAchievementIRCBll.this.englishSpeekAction.onModeChange(mode, audioRequest.get());
            }
        }
    }

    private class EnglishSpeekModeStand implements EnglishSpeekMode {

        @Override
        public void initAchievement(String mode) {
            EnglishSpeekAction oldEnglishSpeekAction = LiveAchievementIRCBll.this.englishSpeekAction;
            TalLanguage talLanguage = null;
            if (oldEnglishSpeekAction != null) {
                oldEnglishSpeekAction.stop(null);
                talLanguage = oldEnglishSpeekAction.getTalLanguage();
            }
            StarInteractAction starAction;
            EnglishSpeekAction englishSpeekAction = null;
            if (LiveTopic.MODE_CLASS.equals(mode)) {
                LiveStandAchievementBll starBll = new LiveStandAchievementBll(activity, mLiveType, mGetInfo
                        .getStarCount(), mGetInfo.getGoldCount(), true);
                starBll.setLiveBll(LiveAchievementIRCBll.this);
                starBll.setLiveAndBackDebug(mLiveBll);
                starBll.initView(mRootView, mContentView);
                starAction = starBll;

                //能量条
                EnglishStandSpeekBll englishSpeekBll = new EnglishStandSpeekBll(activity);
                if (speakerRecognitioner != null) {
                    englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                }
                boolean initView = englishSpeekBll.initView(mRootView, mGetInfo.getMode(), talLanguage, audioRequest, mContentView);
                if (initView) {
                    englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    starBll.setLiveAndBackDebug(mLiveBll);
                    englishSpeekBll.setmShareDataManager(mShareDataManager);
                    englishSpeekAction = englishSpeekBll;
                }
            } else {
                LiveAchievementBll starBll = new LiveAchievementBll(activity, mLiveType, mGetInfo// mGetInfo
                        // .getStarCount(),
                        //mGetInfo.getGoldCount()
                        , true);
                starBll.setLiveBll(LiveAchievementIRCBll.this);
                starBll.setLiveAndBackDebug(mLiveBll);
                starBll.initView(mRootView, mContentView);
                starAction = starBll;

                //能量条
                EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                if (speakerRecognitioner != null) {
                    englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                }
                boolean initView = englishSpeekBll.initView(mRootView, mGetInfo.getMode(), talLanguage, audioRequest, mContentView);
                if (initView) {
                    englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    englishSpeekBll.setmShareDataManager(mShareDataManager);
                    englishSpeekAction = englishSpeekBll;
                }
            }
            LiveAchievementIRCBll.this.starAction = starAction;
            LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekAction;
        }
    }

    private void startAchievement() {
        if (mGetInfo.getPattern() == 2) {
            englishSpeekMode = new EnglishSpeekModeStand();
        } else {
            englishSpeekMode = new EnglishSpeekModeNomal();
        }
        initAchievement(mGetInfo.getMode());
    }

    private void initAchievement(final String mode) {
        if (englishSpeekMode != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    englishSpeekMode.initAchievement(mode);
                }
            });
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (englishSpeekAction != null) {
            englishSpeekAction.destory();
        }
        if (speakerRecognitioner != null) {
            speakerRecognitioner.destory();
        }
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        initAchievement(mode);
        if (recognizeDialog != null && recognizeDialog.isDialogShow()) {
            recognizeDialog.cancelDialog();
        }
    }

    @Override
    public void setStuStarCount(final long reTryTime, final String starId, final AbstractBusinessDataCallBack
            callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().setStuStarCount(mLiveType, enstuId, mLiveId, starId, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                callBack.onDataSucess();
                mLogtf.d("setStuStarCount:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                callBack.onDataFail(1, msg);
                mLogtf.d("setStuStarCount:onPmFailure:msg=" + msg);
                postDelayedIfNotFinish(new Runnable() {
                    @Override
                    public void run() {
                        setStuStarCount(reTryTime + 1000, starId, callBack);
                    }
                }, reTryTime);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onDataFail(2, responseEntity.getErrorMsg());
                mLogtf.d("setStuStarCount:onPmFailure:responseEntity=" + responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void setTotalOpeningLength(final long reTryTime, final String duration, final String speakingNum, final
    String speakingLen, final float x, final float y) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = mGetInfo.getStudentLiveInfo().getClassId();
        getHttpManager().setTotalOpeningLength(enstuId, mLiveBll.getCourseId(), mLiveId, classId, duration,
                speakingNum, speakingLen,
                new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.d( "setTotalOpeningLength:onPmSuccess" + responseEntity.getJsonObject());
                        if (starAction != null) {
                            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                            int star = jsonObject.getInt("star");
                            if (star > 0) {
                                starAction.onStarAdd(star, x, y);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.d( "setTotalOpeningLength:onFailure");
                        super.onFailure(call, e);
                        postDelayedIfNotFinish(new Runnable() {
                            @Override
                            public void run() {
                                setTotalOpeningLength(reTryTime + 1000, duration, speakingNum, speakingLen, x, y);
                            }
                        }, reTryTime);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.d( "setTotalOpeningLength:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                    }
                });
    }

    @Override
    public void setNotOpeningNum() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().setNotOpeningNum(enstuId, mGetInfo.getId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d( "setNotOpeningNum:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                logger.e( "setNotOpeningNum:onFailure", e);
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d( "setNotOpeningNum:onPmError" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void sendStat(int index) {
        if (mLiveBll.getMainTeacherStr() != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.ROOM_STAR_SEND_S);
                jsonObject.put("id", "" + mGetInfo.getStuId());
                jsonObject.put("answer", index);
//            if (LiveTopic.MODE_CLASS.equals(getMode())) {
//                mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
//            } else {
//                mIRCMessage.sendNotice(mCounteacher.get_nick(), jsonObject.toString());
//            }
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
            } catch (Exception e) {
                // logger.e( "understand", e);
                mLogtf.e("sendStat", e);
            }
        }
    }

    @Override
    public void sendDBStudent(int dbDuration) {
        if (mLiveBll.getMainTeacherStr() != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.XCR_ROOM_DB_STUDENT);
                jsonObject.put("id", "" + mGetInfo.getStuId());
                jsonObject.put("duration", "" + dbDuration);
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
            } catch (Exception e) {
                // logger.e( "understand", e);
                mLogtf.e("sendDBStudent", e);
            }
        }

    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (englishSpeekAction != null) {
            LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
            boolean openDbEnergy = mainRoomstatus.isOpenDbEnergy();
            if (openDbEnergy) {
                englishSpeekAction.onDBStart();
            } else {
                englishSpeekAction.onDBStop();
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        try {
            switch (type) {
                case XESCODE.ROOM_STAR_OPEN:
                    if (starAction != null) {
                        JSONArray array = object.optJSONArray("data");
                        ArrayList<String> data = new ArrayList<>();
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                data.add(array.optString(i));
                            }
                        }
                        String nonce = object.optString("nonce");
                        String starid = object.optString("starid");
                        starAction.onStarStart(data, starid, "", nonce);
                    }
                    break;
                case XESCODE.ROOM_STAR_SEND_T:
                    if (starAction != null) {
                        JSONArray array = object.optJSONArray("data");
                        ArrayList<String> data = new ArrayList<>();
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                data.add(array.optString(i));
                            }
                        }
                        int index = object.optInt("answer", -1);
                        String answer = "";
                        if (index >= 0 && index < data.size()) {
                            answer = data.get(index);
                        }
                        String starid = object.optString("starid");
                        starAction.onStarStart(data, starid, answer, "");
                    }
                    break;
                case XESCODE.ROOM_STAR_CLOSE:
                    if (starAction != null) {
                        String id = object.getString("id");
                        Object answerObj = object.get("answer");
                        ArrayList<String> answer = new ArrayList<>();
                        if (answerObj instanceof JSONArray) {
                            JSONArray array = (JSONArray) answerObj;
                            for (int i = 0; i < array.length(); i++) {
                                answer.add(array.optString(i));
                            }
                        } else {
                            answer.add("" + answerObj);
                        }
                        String nonce = object.optString("nonce");
                        starAction.onStarStop(id, answer, nonce);
                    }
                    break;
                case XESCODE.XCR_ROOM_DB_START:
                    if (englishSpeekAction != null) {
                        englishSpeekAction.onDBStart();
                    }
                    break;
                case XESCODE.XCR_ROOM_DB_CLOSE: {
                    if (englishSpeekAction != null) {
                        englishSpeekAction.onDBStop();
                    }
                    break;
                }
                case XESCODE.XCR_ROOM_DB_PRAISE: {
                    if (englishSpeekAction != null) {
                        int answer = object.getInt("answer");
                        englishSpeekAction.praise(answer);
                    }
                    break;
                }
                case XESCODE.XCR_ROOM_DB_REMIND: {
                    if (englishSpeekAction != null) {
                        int answer = object.getInt("answer");
                        englishSpeekAction.remind(answer);
                    }
                    break;
                }
                default:
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ROOM_STAR_OPEN, XESCODE.ROOM_STAR_SEND_T,
                XESCODE.ROOM_STAR_CLOSE, XESCODE.XCR_ROOM_DB_START, XESCODE.XCR_ROOM_DB_CLOSE,
                XESCODE.XCR_ROOM_DB_PRAISE, XESCODE.XCR_ROOM_DB_REMIND};
    }

    public void onSendMsg(String msg) {
        if (starAction != null) {
            starAction.onSendMsg(msg);
        }
    }

    //语音请求和释放
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                englishSpeekAction.start();
                logger.d( "start:englishSpeekBll.start");
            }
        }
    };

    @Override
    public void request(OnAudioRequest onAudioRequest) {
        audioRequest.set(true);
        logger.d( "request:englishSpeekBll=" + (englishSpeekAction == null));
        if (englishSpeekAction != null) {
            handler.removeMessages(1);
            englishSpeekAction.stop(onAudioRequest);
        } else {
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        }
    }

    @Override
    public void release() {
        audioRequest.set(false);
        logger.d( "release:englishSpeekBll=" + (englishSpeekAction == null));
        if (englishSpeekAction != null) {
            handler.sendEmptyMessageDelayed(1, 2000);
        }
    }

}
