package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveTransferHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveTransferHttpResponseParser;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 课程中心到直播页面的中转页面
 */
public class LiveVideoTransferActivity extends BaseActivity {
    private static final String TAG = "LiveVideoTransferActivity";

    private LiveTransferHttpManager mCourseHttpManager;
    private LiveTransferHttpResponseParser mCourseHttpResponseParser;
    private JSONObject sectionEntityJson;

    // 跳转来源
    private String from;

    private String vCoursseID;
    private String vChapterID;
    private String vSectionName;
    private String vTradeId;
    private String vSectionID;
    private String vStuCourseID;
    private String VisitTimeKey;

    protected LogToFile mLogtf;

    DataLoadEntity dataLoadEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        init();
        Intent intent = getIntent();
        String sectionEntity = intent.getStringExtra("sectionEntity");
        String stuCouId = intent.getStringExtra("stuCouId");
        from = intent.getStringExtra("where");
        try {
            sectionEntityJson = new JSONObject(sectionEntity);
            vCoursseID = sectionEntityJson.optString("vCoursseID");
            vChapterID = sectionEntityJson.optString("vChapterID");
            vSectionName = sectionEntityJson.optString("vSectionName");
            vTradeId = sectionEntityJson.optString("vTradeId");
            vSectionID = sectionEntityJson.optString("vSectionID");
            vStuCourseID = sectionEntityJson.optString("vStuCourseID");
            VisitTimeKey = sectionEntityJson.optString("VisitTimeKey");
        } catch (JSONException e) {
            Loger.e(TAG, e.getMessage());
        }
        mLogtf = new LogToFile(this, TAG);
        mLogtf.d("sectionEntity=" + sectionEntity + "  stuCouId=" + stuCouId + "  from=" + from);
        deductStuGold(stuCouId);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        postDataLoadEvent(dataLoadEntity.webDataSuccess());
        EventBus.getDefault().unregister(this);

    }

    private void init() {
        mCourseHttpManager = new LiveTransferHttpManager(this);
        mCourseHttpResponseParser = new LiveTransferHttpResponseParser();
    }

    /**
     * 观看视频扣除金币
     *
     * @param stuCouId
     */
    public void deductStuGold(final String stuCouId) {
        dataLoadEntity = new DataLoadEntity(mContext);
        postDataLoadEvent(dataLoadEntity.beginLoading());
        // 网络加载数据
        mCourseHttpManager.deductStuGold(stuCouId, vCoursseID,
                vSectionID, 0, new HttpCallBack(dataLoadEntity) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = mCourseHttpResponseParser
                                .deductStuGoldParser(vSectionID, stuCouId, responseEntity);
                        if (entity != null && entity.getIsArts() == 1) {
                            artscoursewarenewpoint(stuCouId, entity);
                        } else {
                            intentToPlayBack(entity);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logger.i("onPmFailure");
                        finish();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.i("onPmError");
                        finish();
                    }
                });
    }

    public void artscoursewarenewpoint(final String stuCouId, final
    VideoResultEntity entitys) {
        //   DataLoadEntity dataLoadEntity = new DataLoadEntity(mContext);
        postDataLoadEvent(dataLoadEntity.beginLoading());
        // 网络加载数据
        mCourseHttpManager.artscoursewarenewpoint(vSectionID, new HttpCallBack(dataLoadEntity) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                Loger.e(TAG, "responseEntity:" + responseEntity);
                VideoResultEntity entity = mCourseHttpResponseParser
                        .parseNewArtsEvent(stuCouId, vSectionID, entitys, responseEntity);

                intentToPlayBack(entity);

            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                Loger.e(TAG, "onPmFailureresponseEntity:" + msg);
                finish();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                Loger.e(TAG, "onPmErrorresponseEntity:" + responseEntity.getErrorMsg());
                finish();
            }
        });
    }

    public void intentToPlayBack(VideoResultEntity result) {
        VideoSectionEntity sectionEntity = result.getMapVideoSectionEntity().get(vSectionID);
        // 播放数据设定
        VideoLivePlayBackEntity videoEntity = videoLivePlayBackFromVideoSection(result,
                vSectionID);
        VideoLivePlayBackEntity tutorEntity = videoLivePlayBackFromVideoSection(result,
                vSectionID + LiveVideoConfig.LIVE_PLAY_BACK_TUTOR_FLAGE);

        Bundle bundle = new Bundle();
        bundle.putSerializable("videoliveplayback", videoEntity);
        bundle.putSerializable("videoTutorEntity", tutorEntity);
        if (tutorEntity != null) {
            bundle.putInt("teacherVideoStatus", MediaPlayer.VIDEO_TEACHER_MAIN);
        }

        bundle.putInt("type", 3);
        bundle.putInt("isArts", result.getIsArts());
        bundle.putInt("pattern", result.getPattern());
        if (!StringUtils.isSpace(sectionEntity.getExamPaperUrl())) {
            if (result.getIsArts() == 1) {
                ShareDataManager.getInstance().put(ShareBusinessConfig.SP_LIVE_EXAM_URL_LIBARTS, sectionEntity
                                .getExamPaperUrl(),
                        ShareDataManager.SHAREDATA_USER);
            } else if (result.getIsArts() == 2) {
                ShareDataManager.getInstance().put(ShareBusinessConfig.SP_LIVE_EXAM_URL_CHS, sectionEntity
                        .getExamPaperUrl(), ShareDataManager.SHAREDATA_USER);
            } else {
                ShareDataManager.getInstance().put(ShareBusinessConfig.SP_LIVE_EXAM_URL_SCIENCE, sectionEntity
                                .getExamPaperUrl(),
                        ShareDataManager.SHAREDATA_USER);
            }
            ShareDataManager.getInstance().put(ShareBusinessConfig.SP_LIVE_EXAM_COMMON_URL + result.getIsArts(),
                    sectionEntity
                            .getExamPaperUrl(), ShareDataManager.SHAREDATA_USER);
        }
        if (!StringUtils.isSpace(sectionEntity.getSpeechEvalUrl())) {
            ShareDataManager.getInstance().put(ShareBusinessConfig.SP_SPEECH_URL, sectionEntity.getSpeechEvalUrl(),
                    ShareDataManager.SHAREDATA_USER);
        }
        LiveVideoEnter.intentTo(this, bundle, from);
        // finish();
        // OtherModuleEnter.intentTo((Activity) mContext, bundle, CourseDetailActivity.class.getSimpleName());
    }

    public VideoLivePlayBackEntity videoLivePlayBackFromVideoSection(VideoResultEntity
                                                                             result, String sectionId) {
        VideoSectionEntity sectionEntity = result.getMapVideoSectionEntity().get(sectionId);
        if (sectionEntity == null) {
            return null;
        }

        // 播放数据设定
        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
        videoEntity.setLstPoint(result.getLstPoint());
        videoEntity.setCourseId(vCoursseID);
        videoEntity.setChapterId(vChapterID);
        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVE_PLAY_LIVE);
        videoEntity.setPlayVideoName(vSectionName);
        videoEntity.setVideoCacheKey(vTradeId);
        videoEntity.setLiveId(vSectionID);
        videoEntity.setStuCourseId(vStuCourseID);
        videoEntity.setVisitTimeKey(VisitTimeKey);
        videoEntity.setvCourseSendPlayVideoTime(getSendPlayVideoTimeStatic(LocalCourseConfig.SENDPLAYVIDEOTIME));
        // 互动题数据
        videoEntity.setPlayVideoId(sectionEntity.getvSectionID());
        videoEntity.setLstVideoQuestion(sectionEntity.getLstVideoQuestion());
        videoEntity.setVideoPath(sectionEntity.getVideoWebPath());
        videoEntity.setHostPath(sectionEntity.getHostPath());
        videoEntity.setVideoPathNoHost(sectionEntity.getVideoPath());
        videoEntity.setIsAllowMarkpoint(result.getIsAllowMarkpoint());
        videoEntity.setLearning_stage(result.getLearning_stage());
        videoEntity.setClassId(sectionEntity.getClassId());
        videoEntity.setGetInfoStr(result.getGetInfoStr());
        videoEntity.setPattern(result.getPattern());
        // 回放一发多题数据的绑定
        if (result.getIsMul() == 1) {
            videoEntity.setMul(true);
        } else {
            videoEntity.setMul(false);
        }
        videoEntity.setEdustage(sectionEntity.getEducationStage());
        videoEntity.setClassId(sectionEntity.getClassId());
        videoEntity.setTeamId(sectionEntity.getTeamId());
        videoEntity.setStuCoulId(sectionEntity.getStuCouId());
        // 评价老师
        videoEntity.setEvaluateIsOpen(sectionEntity.getEvaluateIsOpen());
        videoEntity.setEvaluateTimePer(sectionEntity.getEvaluateTimePer());
        videoEntity.setMainTeacherId(sectionEntity.getMainTeacherId());
        videoEntity.setMainTeacherName(sectionEntity.getMainTeacherName());
        videoEntity.setMainTeacherImg(sectionEntity.getMainTeacherImg());
        videoEntity.setTutorTeacherId(sectionEntity.getTutorTeacherId());
        videoEntity.setTutorTeacherName(sectionEntity.getTutorTeacherName());
        videoEntity.setTutorTeacherImg(sectionEntity.getTutorTeacherImg());
        return videoEntity;
    }

    /**
     * 获取观看视频统计间隔时间
     *
     * @param sendPlayVideoTimeKey
     */
    public static int getSendPlayVideoTimeStatic(String sendPlayVideoTimeKey) {
        return ShareDataManager.getInstance().getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180, ShareDataManager
                .SHAREDATA_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
