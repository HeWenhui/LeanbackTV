package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.XSAsykTask;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoCourseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExPerienceLiveMessage;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageGroupEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.OnSpeechEval;
import com.xueersi.ui.dataload.DataLoadEntity;

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

                // 网络加载数据
                mCourseHttpManager.getRedPacket(operateId, liveId,
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
        // 网络加载数据
        mCourseHttpManager.saveTestRecord(questionEntity.isNewArtsH5Courseware(), questionEntity.getSrcType(), questionEntity.getvQuestionID(), result, questionEntity.getAnswerDay(),
                liveId, livePlayType, voice, isRight, new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        logger.d("saveQuestionResult:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
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
        // 网络加载数据
        mCourseHttpManager.sumitCourseWareH5(questionEntity.isNewArtsH5Courseware(), questionEntity.getSrcType(), questionEntity.getvQuestionID(), result, questionEntity.getAnswerDay(),
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

//    /**
//     * 上传视频互动题答案
//     *
//     * @param dataLoadEntity
//     * @param sectionId
//     */
//    public void saveQuestionResult(final DataLoadEntity dataLoadEntity, final String srcType, final String sectionId,
//                                   final String result,
//                                   final String testDay, final String liveId, final int livePlayType) {
//        // 从网络更新数据库数据
//        if (!NetWorkHelper.isNetworkAvailable(mContext)) {
//            postDataLoadEvent(dataLoadEntity.webDataError());
//            EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(result));
//            return;
//        }
//        MyUserInfoEntity myUserInfoEntity = LiveAppUserInfo.getInstance();
//        // 网络加载数据
//        mCourseHttpManager.saveTestRecord(myUserInfoEntity.getEnstuId(), srcType, sectionId, result, testDay,
//                liveId, livePlayType, false, false, new HttpCallBack(dataLoadEntity) {
//
//                    @Override
//                    public void onPmSuccess(ResponseEntity responseEntity) {
//                        VideoResultEntity entity = mCourseHttpResponseParser
//                                .parseQuestionAnswer(responseEntity, false);
//                        isEmpty(entity, dataLoadEntity);
//                        EventBus.getDefault().post(new PlaybackVideoEvent.OnAnswerReslut(entity));
//                    }
//
//                    @Override
//                    public void onPmFailure(Throwable error, String msg) {
//                        XESToastUtils.showToast(mContext, msg);
//                    }
//
//                    @Override
//                    public void onPmError(ResponseEntity responseEntity) {
//                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
//                    }
//                });
    // }

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

                // 网络加载数据
                mCourseHttpManager.getLivePlayRedPacket(operateId, liveId,
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

    public void getLivePlayRedPacket(final DataLoadEntity dataLoadEntity, final String liveId, final String operateId, final AbstractBusinessDataCallBack callBack) {
        // 网络加载数据
        mCourseHttpManager.getLivePlayRedPacket(operateId, liveId,
                new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = mCourseHttpResponseParser
                                .redPacketParseParser(responseEntity);
                        callBack.onDataSucess(entity);
//                        EventBus.getDefault().post(new PlaybackVideoEvent.OnGetRedPacket(entity));
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

                // 网络加载数据
                mCourseHttpManager.getLivePlayRedPackets(operateId, termId, liveId,
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
                    logger.i("getLiveLectureMsgsFromFile:file=" + file.getName() + ",liveMessageEntities=" +
                            liveMessageEntities.size());
                    return liveMessageGroupEntity;
                }
            } catch (Exception e) {
                logger.i("getLiveLectureMsgsFromFile:file=" + file.getName() + ",delete");
                file.delete();
            }
        }
        return null;
    }

//    /**
//     * 请求并保存当前的聊天，并回调
//     *
//     * @param dir                保存的文件
//     * @param channel            房间名
//     * @param start              其实时间
//     * @param getLiveLectureMsgs 回调接口
//     */
//    public void getLiveLectureMsgs(final File dir, String channel, final String start, final
//    ArrayList<VideoQuestionEntity> timeEntities, final LectureLivePlayBackVideoActivity.GetLiveLectureMsgs
//                                           getLiveLectureMsgs) {
//        MyUserInfoEntity myUserInfoEntity = LiveAppUserInfo.getInstance();
//        logger.i( "getLiveLectureMsgs:start=" + start);
//        File file = new File(dir, start);
//        LiveMessageGroupEntity liveMessageGroupEntity = getLiveLectureMsgsFromFile(file);
//        if (liveMessageGroupEntity != null && liveMessageGroupEntity.count > 0) {
//            ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
//            logger.i( "getLiveLectureMsgs:count=" + liveMessageGroupEntity.count + ",liveMessageEntities=" +
//                    liveMessageEntities.size());
//            getLiveLectureMsgs.getLiveLectureMsgs(liveMessageGroupEntity);
//            return;
//        }
//        mCourseHttpManager.getLiveLectureMsgs(myUserInfoEntity.getEnstuId(), channel, 50, start, 1, new
//                HttpCallBack(false) {
//
//                    @Override
//                    public void onPmSuccess(ResponseEntity responseEntity) {
//                        JSONArray array = (JSONArray) responseEntity.getJsonObject();
//                        LiveMessageGroupEntity liveMessageGroupEntity = mCourseHttpResponseParser.liveMessagesParser(array);
//                        ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
//                        if (liveMessageGroupEntity.count > 0) {
//                            saveMsgToFile(dir, start, array, timeEntities);
//                        }
//                        File[] files = dir.listFiles();
//                        if (files != null) {
//                            for (int i = 0; i < files.length; i++) {
//                                File file = files[i];
//                                long time = Long.parseLong(file.getName());
//                                if (Long.parseLong(start) < time && time <= liveMessageGroupEntity.lastid) {
//                                    boolean delete = file.delete();
//                                    logger.i( "getLiveLectureMsgs:onPmSuccess:delete=" + delete);
//                                }
//                            }
//                        }
//                        logger.i( "getLiveLectureMsgs:onPmSuccess:liveMessageGroupEntity=" + liveMessageEntities.size()
//                                + "," + liveMessageGroupEntity.otherMessageEntities.size());
//                        getLiveLectureMsgs.getLiveLectureMsgs(liveMessageGroupEntity);
//                    }
//
//                    @Override
//                    public void onPmFailure(Throwable error, String msg) {
//                        logger.i( "getLiveLectureMsgs:onPmFailure:msg=" + msg);
//                        getLiveLectureMsgs.onPmFailure();
//                    }
//
//                    @Override
//                    public void onPmError(ResponseEntity responseEntity) {
//                        logger.i( "getLiveLectureMsgs:onPmError:ErrorMsg=" + responseEntity.getErrorMsg());
//                    }
//                });
//    }

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
//                                        logger.i( "saveMsgToFile:continue");
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
        logger.i("saveMsgToFile:array=" + array.length() + ",array2=" + array2.length());
        FileUtils.writeFileFromString(dir.getPath() + File.separator + start, array2.toString(), false);
    }

    public void sendRecordInteract(String url, String termId, int times) {
        mCourseHttpManager.sendExpeRecordInteract(url, termId, times, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("sendRecordInteract : Success");
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.i("sendRecordInteract : Failure,msg: " + msg);
                super.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.i("sendRecordInteract : Error");
                super.onPmError(responseEntity);
            }
        });
    }

    // 18.04.11 获取讲座直播回放中的更多课程的广告信息
    public void getMoreCourseChoices(String liveId, final AbstractBusinessDataCallBack getDataCallBack) {
        mCourseHttpManager.getMoreCourseChoices(liveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Log.e("Duncan", "playbackresponseEntity:" + responseEntity);
                MoreChoice choiceEntity = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString(), MoreChoice.class);
                if (choiceEntity != null) {
                    getDataCallBack.onDataSucess(choiceEntity);
                }
            }
        });

    }

    // 获取体验学习报告
    public void getExperienceResult(String termId, String liveId, final AbstractBusinessDataCallBack getDataCallBack) {
        mCourseHttpManager.getExperienceResult(termId, liveId, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                ExperienceResult learn = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString(), ExperienceResult.class);
                if (learn != null) {
                    getDataCallBack.onDataSucess(learn);
                }
                Log.e("Duncan", "playbackresponseEntity:" + responseEntity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                Log.e("Duncan", "playbackerrorEntity:" + error);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Log.e("Duncan", "playbackerrorEntity:" + responseEntity);
            }


        });
    }

    public void sendExperienceFeedback(String user_id, String plan_id, String subject_id, String grade_id, String order_id, String suggest, JSONArray jsonOption, HttpCallBack requestCallBack) {
        mCourseHttpManager.sendExperienceFeedback(user_id, plan_id, subject_id, grade_id, order_id, suggest, jsonOption, requestCallBack);
    }

    /**
     * 上传视频互动题答案
     * <p>
     * //     * @param dataLoadEntity
     * //     * @param sectionId
     */
//    public void saveQuestionResults(final DataLoadEntity dataLoadEntity, final String srcType, final String sectionId,
//                                    final String result,
//                                    final String testDay, final String liveId, final int livePlayType) {
//        // 从网络更新数据库数据
//        if (!NetWorkHelper.isNetworkAvailable(mContext)) {
//            postDataLoadEvent(dataLoadEntity.webDataError());
//            EventBus.getDefault().post(new PlaybackVideoEvent.OnPlayVideoWebError(result));
//            return;
//        }
//        MyUserInfoEntity myUserInfoEntity = LiveAppUserInfo.getInstance();
//        // 网络加载数据
//        mCourseHttpManager.saveTestRecords(myUserInfoEntity.getEnstuId(), srcType, sectionId, result, testDay,
//                liveId, livePlayType, false, false, new HttpCallBack(dataLoadEntity) {
//
//                    @Override
//                    public void onPmSuccess(ResponseEntity responseEntity) {
//                        VideoResultEntity entity = mCourseHttpResponseParser
//                                .parseQuestionAnswer(responseEntity, false);
//                        isEmpty(entity, dataLoadEntity);
//                        EventBus.getDefault().post(new PlaybackVideoEvent.OnAnswerReslut(entity));
//                    }
//
//                    @Override
//                    public void onPmFailure(Throwable error, String msg) {
//                        XESToastUtils.showToast(mContext, msg);
//                    }
//
//                    @Override
//                    public void onPmError(ResponseEntity responseEntity) {
//                        XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
//                    }
//                });
//    }
    public void sendLiveCourseVisitTime(final String stuCouId, final String liveId, final int hbTime, final Handler handler, final long delayMillis) {
        mCourseHttpManager.sendLiveCourseVisitTime(stuCouId, liveId, hbTime, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("onPmError:errorMsg=" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                if ((mContext instanceof Activity) && ((Activity) mContext).isFinishing()) {
                    return;
                }
                if (delayMillis > 12000) {
                    return;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendLiveCourseVisitTime(stuCouId, liveId, hbTime, handler, delayMillis + 2000);
                    }
                }, delayMillis);
            }
        });
    }

}
