package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.activity.ExperienceLiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExPerienceLiveMessage;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.entity.VideoCourseEntity;
import com.xueersi.parentsmeeting.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LectureLivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageGroupEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.util.JsonUtil;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.app.XSAsykTask;
import com.xueersi.xesalib.utils.file.FileUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.view.layout.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 直播回放业务层
 *
 * @author ZouHao
 */
public class LectureLivePlayBackBll extends BaseBll {

    private LivePlayBackHttpManager mCourseHttpManager;
    private LivePlayBackHttpResponseParser mCourseHttpResponseParser;

    private List<VideoCourseEntity> mLstCourseEntity = new ArrayList<>();

    public LectureLivePlayBackBll(Context context, String stuCourId) {
        super(context);
        mCourseHttpManager = new LivePlayBackHttpManager(mContext);
        mCourseHttpManager.addBodyParam("stuCouId", stuCourId);
        mCourseHttpResponseParser = new LivePlayBackHttpResponseParser();
    }

    public void setLiveVideoSAConfig(LiveVideoSAConfig liveVideoSAConfig) {
        mCourseHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
    }

    /**
     * 直播回放得到金币
     *
     * @param dataLoadEntity
     * @param liveId
     */
    public void getRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String operateId) {

        new XSAsykTask() {

            @Override
            public void preTask() {
            }

            @Override
            public void postTask() {
                // 从网络更新数据库数据
                if (!NetWorkHelper.isNetworkAvailable(mContext)) {
                    postDataLoadEvent(dataLoadEntity.webDataError());
                    EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(""));
                    return;
                }

                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                // 网络加载数据
                mCourseHttpManager.getRedPacket(myUserInfoEntity.getEnstuId(), operateId, liveId,
                        new HttpCallBack(dataLoadEntity) {

                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                VideoResultEntity entity = mCourseHttpResponseParser
                                        .redPacketParseParser(responseEntity);
                                isEmpty(entity, dataLoadEntity);
                                EventBus.getDefault().post(new PlaybackVideoEvent.OnGetRedPacket(entity));
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                XESToastUtils.showToast(mContext, msg);
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                            }
                        });
            }

            @Override
            public void doInBack() {

            }
        }.execute(true);
    }

    public void saveQuestionResult(final DataLoadEntity dataLoadEntity, final VideoQuestionEntity questionEntity,
                                   final String result, final String liveId, final int livePlayType, final boolean voice, boolean isRight, final AbstractBusinessDataCallBack callBack) {
        // 从网络更新数据库数据
        if (!voice && !NetWorkHelper.isNetworkAvailable(mContext)) {
            postDataLoadEvent(dataLoadEntity.webDataError());
            EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(result));
            return;
        }
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        // 网络加载数据
        mCourseHttpManager.saveTestRecord(myUserInfoEntity.getEnstuId(), questionEntity.getSrcType(), questionEntity.getvQuestionID(), result, questionEntity.getAnswerDay(),
                liveId, livePlayType, voice, isRight, new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        Loger.d(TAG, "saveQuestionResult:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                        VideoResultEntity entity = mCourseHttpResponseParser.parseQuestionAnswer(responseEntity, voice);
                        entity.setVoice(voice);
                        isEmpty(entity, dataLoadEntity);
                        PlaybackVideoEvent.OnAnswerReslut onAnswerReslut = new PlaybackVideoEvent.OnAnswerReslut(entity);
                        onAnswerReslut.setQuestionEntity(questionEntity);
                        callBack.onDataSucess(onAnswerReslut);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(mContext, msg);
                        callBack.onDataFail(0, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                        callBack.onDataFail(1, responseEntity.getErrorMsg());
                    }
                });
    }

    public void saveQuestionH5Result(final DataLoadEntity dataLoadEntity, final VideoQuestionEntity questionEntity,
                                     final String result, final String liveId, String isSubmit, String type,
                                     double voiceTime, boolean isRight, final AbstractBusinessDataCallBack callBack) {
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        // 网络加载数据
        mCourseHttpManager.sumitCourseWareH5(myUserInfoEntity.getEnstuId(), questionEntity.getSrcType(), questionEntity.getvQuestionID(), result, questionEntity.getAnswerDay(),
                liveId, isSubmit, type, voiceTime, isRight, new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = mCourseHttpResponseParser.parseQuestionAnswer(responseEntity, true);
                        entity.setVoice(true);
                        isEmpty(entity, dataLoadEntity);
                        PlaybackVideoEvent.OnAnswerReslut onAnswerReslut = new PlaybackVideoEvent.OnAnswerReslut(entity);
                        onAnswerReslut.setQuestionEntity(questionEntity);
                        callBack.onDataSucess(onAnswerReslut);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(mContext, msg);
                        callBack.onDataFail(0, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                        callBack.onDataFail(1, responseEntity.getErrorMsg());
                    }
                });
    }

    /**
     * 上传视频互动题答案
     *
     * @param dataLoadEntity
     * @param sectionId
     */
    public void saveQuestionResult(final DataLoadEntity dataLoadEntity, final String srcType, final String sectionId,
                                   final String result,
                                   final String testDay, final String liveId, final int livePlayType) {
        // 从网络更新数据库数据
        if (!NetWorkHelper.isNetworkAvailable(mContext)) {
            postDataLoadEvent(dataLoadEntity.webDataError());
            EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(result));
            return;
        }
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        // 网络加载数据
        mCourseHttpManager.saveTestRecord(myUserInfoEntity.getEnstuId(), srcType, sectionId, result, testDay,
                liveId, livePlayType, false, false, new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = mCourseHttpResponseParser
                                .parseQuestionAnswer(responseEntity, false);
                        isEmpty(entity, dataLoadEntity);
                        EventBus.getDefault().post(new PlaybackVideoEvent.OnAnswerReslut(entity));
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(mContext, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                    }
                });
    }

    /**
     * 直播课直播回放得到金币
     *
     * @param dataLoadEntity
     * @param liveId
     */
    public void getLivePlayRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String operateId) {

        new XSAsykTask() {

            @Override
            public void preTask() {
            }

            @Override
            public void postTask() {
                // 从网络更新数据库数据
                if (!NetWorkHelper.isNetworkAvailable(mContext)) {
                    postDataLoadEvent(dataLoadEntity.webDataError());
                    EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(""));
                    return;
                }

                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                // 网络加载数据
                mCourseHttpManager.getLivePlayRedPacket(myUserInfoEntity.getEnstuId(), operateId, liveId,
                        new HttpCallBack(dataLoadEntity) {

                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                VideoResultEntity entity = mCourseHttpResponseParser
                                        .redPacketParseParser(responseEntity);
                                isEmpty(entity, dataLoadEntity);
                                EventBus.getDefault().post(new PlaybackVideoEvent.OnGetRedPacket(entity));
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                XESToastUtils.showToast(mContext, msg);
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                            }
                        });
            }

            @Override
            public void doInBack() {

            }
        }.execute(true);
    }

    /**
     * 体验直播课直播回放得到金币
     *
     * @param dataLoadEntity
     * @param liveId
     */
    public void getLivePlayRedPackets(final DataLoadEntity dataLoadEntity, final String operateId, final String liveId, final String termId) {

        new XSAsykTask() {

            @Override
            public void preTask() {
            }

            @Override
            public void postTask() {
                // 从网络更新数据库数据
                if (!NetWorkHelper.isNetworkAvailable(mContext)) {
                    postDataLoadEvent(dataLoadEntity.webDataError());
                    EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(""));
                    return;
                }

                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                // 网络加载数据
                mCourseHttpManager.getLivePlayRedPackets(myUserInfoEntity.getEnstuId(),operateId,termId, liveId,
                        new HttpCallBack(dataLoadEntity) {

                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                VideoResultEntity entity = mCourseHttpResponseParser
                                        .redPacketParseParser(responseEntity);
                                isEmpty(entity, dataLoadEntity);
                                EventBus.getDefault().post(new PlaybackVideoEvent.OnGetRedPacket(entity));
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                XESToastUtils.showToast(mContext, msg);
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                            }
                        });
            }

            @Override
            public void doInBack() {

            }
        }.execute(true);
    }

    /** 从文件得到聊天记录 */
    public LiveMessageGroupEntity getLiveLectureMsgsFromFile(File file) {
        if (file.exists()) {

            String content = FileUtils.readFile2String(file.getPath(), "UTF-8");
            try {
                JSONArray array = new JSONArray(content);
                LiveMessageGroupEntity liveMessageGroupEntity = mCourseHttpResponseParser.liveMessagesParser(array);
                ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
                if (liveMessageGroupEntity.count > 0) {
                    Loger.i(TAG, "getLiveLectureMsgsFromFile:file=" + file.getName() + ",liveMessageEntities=" +
                            liveMessageEntities.size());
                    return liveMessageGroupEntity;
                }
            } catch (Exception e) {
                Loger.i(TAG, "getLiveLectureMsgsFromFile:file=" + file.getName() + ",delete");
                file.delete();
            }
        }
        return null;
    }

    /** 请求并保存当前的聊天，不回调 */
    public void saveLiveLectureMsgs(final File dir, String channel, final String start, final
    ArrayList<VideoQuestionEntity> timeEntities) {
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        Loger.i(TAG, "saveLiveLectureMsgs:start=" + start);
        mCourseHttpManager.getLiveLectureMsgs(myUserInfoEntity.getEnstuId(), channel, 50, start, 1, new
                HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        JSONArray array = (JSONArray) responseEntity.getJsonObject();
                        LiveMessageGroupEntity liveMessageGroupEntity = mCourseHttpResponseParser.liveMessagesParser(array);
                        ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
                        int save = 1;
                        if (liveMessageGroupEntity.count > 0) {
                            File[] files = dir.listFiles();
                            if (files != null) {
                                for (int i = 0; i < files.length; i++) {
                                    File file = files[i];
                                    long time = Long.parseLong(file.getName());
                                    if (i < files.length - 1) {
                                        if (Long.parseLong(start) >= time) {
                                            if (Long.parseLong(start) <= Long.parseLong(files[i + 1].getName())) {
                                                save = 2;
                                            }
                                        }
                                    } else {
                                        LiveMessageGroupEntity liveMessageGroupEntity2 = getLiveLectureMsgsFromFile(file);
                                        if (liveMessageGroupEntity2 != null && liveMessageGroupEntity2.count > 0) {
                                            if (Long.parseLong(start) > liveMessageGroupEntity2.lastid) {
                                                save = 3;
                                            } else {
                                                save = 4;
                                            }
                                        } else {
                                            save = 5;
                                        }
                                    }
                                }
                            }
                            if (save == 1 || save == 3) {
                                saveMsgToFile(dir, start, array, timeEntities);
                            }
                        }
                        if (save == 1 || save == 3) {
                            Loger.i(TAG, "saveLiveLectureMsgs:onPmSuccess:liveMessageGroupEntity=" + liveMessageEntities.size()
                                    + "," + liveMessageGroupEntity.otherMessageEntities.size() + ",save=" + save + ",start="
                                    + start);
                        } else {
                            Loger.i(TAG, "saveLiveLectureMsgs:onPmSuccess:liveMessageGroupEntity=" + liveMessageEntities.size()
                                    + "," + liveMessageGroupEntity.otherMessageEntities.size() + ",save=" + save);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        Loger.i(TAG, "saveLiveLectureMsgs:onPmFailure:msg=" + msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.i(TAG, "saveLiveLectureMsgs:onPmError:ErrorMsg=" + responseEntity.getErrorMsg());
                    }
                });
    }

    /**
     * 请求并保存当前的聊天，并回调
     *
     * @param dir                保存的文件
     * @param channel            房间名
     * @param start              其实时间
     * @param getLiveLectureMsgs 回调接口
     */
    public void getLiveLectureMsgs(final File dir, String channel, final String start, final
    ArrayList<VideoQuestionEntity> timeEntities, final LectureLivePlayBackVideoActivity.GetLiveLectureMsgs
                                           getLiveLectureMsgs) {
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        Loger.i(TAG, "getLiveLectureMsgs:start=" + start);
        File file = new File(dir, start);
        LiveMessageGroupEntity liveMessageGroupEntity = getLiveLectureMsgsFromFile(file);
        if (liveMessageGroupEntity != null && liveMessageGroupEntity.count > 0) {
            ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
            Loger.i(TAG, "getLiveLectureMsgs:count=" + liveMessageGroupEntity.count + ",liveMessageEntities=" +
                    liveMessageEntities.size());
            getLiveLectureMsgs.getLiveLectureMsgs(liveMessageGroupEntity);
            return;
        }
        mCourseHttpManager.getLiveLectureMsgs(myUserInfoEntity.getEnstuId(), channel, 50, start, 1, new
                HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        JSONArray array = (JSONArray) responseEntity.getJsonObject();
                        LiveMessageGroupEntity liveMessageGroupEntity = mCourseHttpResponseParser.liveMessagesParser(array);
                        ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
                        if (liveMessageGroupEntity.count > 0) {
                            saveMsgToFile(dir, start, array, timeEntities);
                        }
                        File[] files = dir.listFiles();
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                File file = files[i];
                                long time = Long.parseLong(file.getName());
                                if (Long.parseLong(start) < time && time <= liveMessageGroupEntity.lastid) {
                                    boolean delete = file.delete();
                                    Loger.i(TAG, "getLiveLectureMsgs:onPmSuccess:delete=" + delete);
                                }
                            }
                        }
                        Loger.i(TAG, "getLiveLectureMsgs:onPmSuccess:liveMessageGroupEntity=" + liveMessageEntities.size()
                                + "," + liveMessageGroupEntity.otherMessageEntities.size());
                        getLiveLectureMsgs.getLiveLectureMsgs(liveMessageGroupEntity);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        Loger.i(TAG, "getLiveLectureMsgs:onPmFailure:msg=" + msg);
                        getLiveLectureMsgs.onPmFailure();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.i(TAG, "getLiveLectureMsgs:onPmError:ErrorMsg=" + responseEntity.getErrorMsg());
                    }
                });
    }

    /**
     * 保存消息到文件，过滤中断的
     *
     * @param dir
     * @param start
     * @param array
     * @param timeEntities
     */
    private void saveMsgToFile(File dir, final String start, JSONArray array, ArrayList<VideoQuestionEntity>
            timeEntities) {
        JSONArray array2 = new JSONArray();
        a:
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                long id = object.getLong("id");
                for (int j = 0; j < timeEntities.size(); j++) {
                    VideoQuestionEntity videoQuestionEntity = timeEntities.get(j);
                    int vCategory = videoQuestionEntity.getvCategory();
                    if (vCategory == LocalCourseConfig.CATEGORY_VIDEO_END) {
                        if (id >= (long) videoQuestionEntity.getvQuestionInsretTime() * 1000000) {//大于结束时间
                            if (j < timeEntities.size() - 1) {
                                VideoQuestionEntity videoQuestionEntity2 = timeEntities.get(j + 1);
                                int vCategory2 = videoQuestionEntity2.getvCategory();
                                if (vCategory2 == LocalCourseConfig.CATEGORY_VIDEO_START) {//小于开始时间
                                    if (id <= (long) videoQuestionEntity2.getvQuestionInsretTime() * 1000000) {
//                                        Loger.i(TAG, "saveMsgToFile:continue");
                                        continue a;
                                    }
                                }
                            }
                        }
                    }
                }
                array2.put(object);
            } catch (JSONException e) {
            }
        }
        Loger.i(TAG, "saveMsgToFile:array=" + array.length() + ",array2=" + array2.length());
        FileUtils.writeFileFromString(dir.getPath() + File.separator + start, array2.toString(), false);
    }

    public void getSpeechEval(String liveid, String id, final OnSpeechEval onSpeechEval) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mCourseHttpManager.getSpeechEval(enstuId, liveid, id, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                SpeechEvalEntity speechEvalEntity = mCourseHttpResponseParser.parseSpeechEval(responseEntity);
                if (speechEvalEntity != null) {
                    onSpeechEval.onSpeechEval(speechEvalEntity);
                } else {
                    responseEntity = new ResponseEntity();
                    responseEntity.setStatus(false);
                    responseEntity.setErrorMsg("出了点意外，请稍后试试");
                    responseEntity.setJsonError(true);
                    onSpeechEval.onPmError(responseEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                onSpeechEval.onPmError(responseEntity);
            }
        });
    }

    public void sendSpeechEvalResult(String liveid, String id, String stuAnswer, String times, int entranceTime,
                                     final OnSpeechEval onSpeechEval) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mCourseHttpManager.sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime, new
                HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmSuccess=" + responseEntity.getJsonObject());
                        onSpeechEval.onSpeechEval(null);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmFailure=" + msg);
                        onSpeechEval.onPmFailure(error, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmError=" + responseEntity.getErrorMsg());
                        onSpeechEval.onPmError(responseEntity);
                    }
                });
    }

    public void sendSpeechEvalResult2(String liveid, String id, String stuAnswer,
                                      final OnSpeechEval onSpeechEval) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mCourseHttpManager.sendSpeechEvalResult2(enstuId, liveid, id, stuAnswer, new
                HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        Loger.i(TAG, "sendSpeechEvalResult:onPmSuccess=" + jsonObject);
                        onSpeechEval.onSpeechEval(jsonObject);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmFailure=" + msg);
                        onSpeechEval.onPmFailure(error, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmError=" + responseEntity.getErrorMsg());
                        onSpeechEval.onPmError(responseEntity);
                    }
                });
    }

    public void speechEval42IsAnswered(final String liveId, String num, final SpeechEvalAction.SpeechIsAnswered isAnswered) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mCourseHttpManager.speechEval42IsAnswered(enstuId, liveId, num, new
                HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        boolean isAnswer = jsonObject.optInt("isAnswer") == 1;
                        isAnswered.isAnswer(isAnswer);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmFailure=" + msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.i(TAG, "sendSpeechEvalResult:onPmError=" + responseEntity.getErrorMsg());
                    }
                });
    }

    public void getAdOnLL(String liveId, final LecAdvertEntity lecAdvertEntity, final AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mCourseHttpManager.getAdOnLL(enstuId, liveId, lecAdvertEntity.course_id, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                int isLearn = jsonObject.optInt("isLearn", 0);
                lecAdvertEntity.isLearn = isLearn;
                if (isLearn == 0) {
                    lecAdvertEntity.limit = jsonObject.optString("limit");
                    lecAdvertEntity.signUpUrl = jsonObject.optString("signUpUrl");
                    lecAdvertEntity.saleName = jsonObject.optString("saleName");
                }
                callBack.onDataSucess();
            }
        });
    }

    // 18.03.14 获取体验课的聊天记录
    public void getExperienceMsgs(String liveId,String classId,Long start,final ExperienceLiveVideoActivity.GetExperienceLiveMsgs
            getLiveLectureMsgs){
        mCourseHttpManager.getExperiencenMsgs(liveId, classId, start , new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                ExPerienceLiveMessage livebackmsg = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString(), ExPerienceLiveMessage.class);
                getLiveLectureMsgs.getLiveExperienceMsgs(livebackmsg);
            }
        });

    }

    /**
     * 上传视频互动题答案
     *
     * @param dataLoadEntity
     * @param sectionId
     */
    public void saveQuestionResults(final DataLoadEntity dataLoadEntity, final String srcType, final String sectionId,
                                    final String result,
                                    final String testDay, final String liveId, final int livePlayType) {
        // 从网络更新数据库数据
        if (!NetWorkHelper.isNetworkAvailable(mContext)) {
            postDataLoadEvent(dataLoadEntity.webDataError());
            EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(result));
            return;
        }
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        // 网络加载数据
        mCourseHttpManager.saveTestRecords(myUserInfoEntity.getEnstuId(), srcType, sectionId, result, testDay,
                liveId, livePlayType, false, false, new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = mCourseHttpResponseParser
                                .parseQuestionAnswer(responseEntity, false);
                        isEmpty(entity, dataLoadEntity);
                        EventBus.getDefault().post(new PlaybackVideoEvent.OnAnswerReslut(entity));
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(mContext, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
                    }
                });
    }
}
