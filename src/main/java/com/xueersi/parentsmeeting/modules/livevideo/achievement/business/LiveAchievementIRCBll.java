package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tal.speech.language.TalLanguage;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by linyuqiang on 2018/7/5.
 * 本场成就和语音能量条
 */
public class LiveAchievementIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, LiveAchievementHttp,
        EnglishSpeekHttp, AudioRequest {
    StarInteractAction starAction;
    EnglishSpeekAction englishSpeekAction;
    boolean audioRequest = false;
    EnglishSpeekMode englishSpeekMode;

    public LiveAchievementIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        putInstance(LiveAchievementIRCBll.class, this);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        String mode = mGetInfo.getMode();
        if (1 == getInfo.getIsAllowStar()) {
            putInstance(AudioRequest.class, this);
            if (mGetInfo.getPattern() == 2) {
                englishSpeekMode = new EnglishSpeekModeStand();
            } else {
                englishSpeekMode = new EnglishSpeekModeNomal();
            }
            initAchievement(mode);
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
        }
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
                starBll.initView(mRootView);
                LiveAchievementIRCBll.this.starAction = starBll;
                //能量条
                EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                boolean initView = englishSpeekBll.initView(mRootView, mGetInfo.getMode(), null);
                if (initView) {
                    englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    englishSpeekBll.setLiveAndBackDebug(mLiveBll);
                    englishSpeekBll.setmShareDataManager(mShareDataManager);
                    LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekBll;
                }
            }
            if (LiveAchievementIRCBll.this.englishSpeekAction != null) {
                LiveAchievementIRCBll.this.englishSpeekAction.onModeChange(mode, audioRequest);
            }
        }
    }

    private class EnglishSpeekModeStand implements EnglishSpeekMode {

        @Override
        public void initAchievement(String mode) {
            StarInteractAction starAction;
            EnglishSpeekAction englishSpeekAction = null;
            TalLanguage talLanguage = null;
            if (englishSpeekAction != null) {
                englishSpeekAction.stop(null);
                talLanguage = englishSpeekAction.getTalLanguage();
            }
            if (LiveTopic.MODE_CLASS.equals(mode)) {
                LiveStandAchievementBll starBll = new LiveStandAchievementBll(activity, mLiveType, mGetInfo
                        .getStarCount(), mGetInfo.getGoldCount(), true);
                starBll.setLiveBll(LiveAchievementIRCBll.this);
                starBll.setLiveAndBackDebug(mLiveBll);
                starBll.initView(mRootView);
                starAction = starBll;

                //能量条
                EnglishStandSpeekBll englishSpeekBll = new EnglishStandSpeekBll(activity);
                boolean initView = englishSpeekBll.initView(mRootView, mGetInfo.getMode(), talLanguage);
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
                starBll.initView(mRootView);
                starAction = starBll;

                //能量条
                EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                boolean initView = englishSpeekBll.initView(mRootView, mGetInfo.getMode(), talLanguage);
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
    }

    @Override
    public void onModeChange(String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        initAchievement(mode);
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
                        Loger.d(TAG, "setTotalOpeningLength:onPmSuccess" + responseEntity.getJsonObject());
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
                        Loger.d(TAG, "setTotalOpeningLength:onFailure");
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
                        Loger.d(TAG, "setTotalOpeningLength:onPmError" + responseEntity.getErrorMsg());
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
                Loger.d(TAG, "setNotOpeningNum:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.e(TAG, "setNotOpeningNum:onFailure", e);
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                Loger.d(TAG, "setNotOpeningNum:onPmError" + responseEntity.getErrorMsg());
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
                // Loger.e(TAG, "understand", e);
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
                // Loger.e(TAG, "understand", e);
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
                Loger.d(TAG, "start:englishSpeekBll.start");
            }
        }
    };

    @Override
    public void request(OnAudioRequest onAudioRequest) {
        audioRequest = true;
        Loger.d(TAG, "request:englishSpeekBll=" + (englishSpeekAction == null));
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
        audioRequest = false;
        Loger.d(TAG, "release:englishSpeekBll=" + (englishSpeekAction == null));
        if (englishSpeekAction != null) {
            handler.sendEmptyMessageDelayed(1, 2000);
        }
    }
}
