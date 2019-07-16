package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.route.module.moduleInterface.AbsDispatcher;
import com.xueersi.common.route.module.startParam.ParamKey;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by dqq on 2019/7/11.
 */
public class LiveVideoDispatcher extends AbsDispatcher {

    //1234分别代表 直播录播讲座精品课
    /***直播**/
    public static final int TYPE_LIVE = 1;
    /***录播*/
    public static final int TYPE_RECORD = 2;
    /***讲座*/
    public static final int TYPE_LECTURE = 3;
    /***体验课*/
    public static final int TYPE_EXP = 4;
    //TODO 旁听
    public static final int TYPE_AUDIT = 5;
    //TODO 心法
    public static final int TYPE_HEART = 6;

    private String vStuCourseId;
    private String courseId;
    private String planId;
    private String chapterName;
    private String termId;
    private int type;
    private int status;
    private int variety;
    private DispatcherBll dispatcherBll;

    public interface LiveNewStatus {
        int LIVE_UNBEGIN = 1;//待开始
        int LIVE_LIVING = 2;//进行中
        int LIVE_WAIT_PLAYBACK = 3;//等待回放
        int LIVE_CAN_PLAYBACK = 4;//未完成
        int LIVE_CAN_PLAYBACK_PLUS = 5;//已完成
    }

    private Activity activity;


    @Override
    public void dispatch(Activity srcActivity, Bundle bundle, int requestCode) {
        if (bundle == null) {
            return;
        }

        if (bundle.containsKey(ParamKey.EXTRAKEY_JSONPARAM)) {
            activity = srcActivity;
            dispatcherBll = new DispatcherBll(srcActivity);
            String paramsJson = bundle.getString(ParamKey.EXTRAKEY_JSONPARAM);
            if (TextUtils.isEmpty(paramsJson)) {
                XESToastUtils.showToast(activity, "数据异常");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(paramsJson);
                status = jsonObject.optInt("status");
                vStuCourseId = jsonObject.optString("stuCouId");
                courseId = jsonObject.optString("courseId");
                planId = jsonObject.optString("planId");
                chapterName = jsonObject.optString("planName");
                termId = jsonObject.optString("termId");
                type = jsonObject.optInt("type");
                type = jsonObject.optInt("variety");
                if (type == TYPE_LIVE) {
                    startLive();
                } else if (type == TYPE_RECORD) {
                    startRecord();
                } else if (type == TYPE_EXP) {
                    startExp();
                } else if (type == TYPE_AUDIT) {
                    startAudit();
                } else if (type == TYPE_LECTURE) {
                    startLecture();
                } else if (type == TYPE_HEART) {
                    startHeart();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XESToastUtils.showToast(activity, "数据异常");
            }
        } else {
            Intent intent = new Intent(srcActivity, LiveVideoLoadActivity.class);
            intent.putExtras(bundle);
            srcActivity.startActivityForResult(intent, requestCode);
        }
    }

    private void startAudit() {
        LiveVideoEnter.intentToAuditClassActivity(activity, vStuCourseId, planId);
    }

    private void startExp() {
        VideoSectionEntity sectionEntity = new VideoSectionEntity();
        sectionEntity.setvSectionName(chapterName);
        sectionEntity.setvChapterName(chapterName);
        dispatcherBll.deductStuGolds(sectionEntity, planId, termId);
    }

    private void startLive() {
        switch (status) {
            case LiveNewStatus.LIVE_UNBEGIN://未开始
                break;
            case LiveNewStatus.LIVE_LIVING://进行中
                startLivePlay(vStuCourseId, courseId, planId);
                break;
            case LiveNewStatus.LIVE_WAIT_PLAYBACK://等待回放
            case LiveNewStatus.LIVE_CAN_PLAYBACK: //未完成
            case LiveNewStatus.LIVE_CAN_PLAYBACK_PLUS: { //已完成
                VideoSectionEntity sectionEntity = new VideoSectionEntity();
                sectionEntity.setvSectionName(chapterName);
                sectionEntity.setvChapterName(chapterName);
                sectionEntity.setvChapterID(planId);
                sectionEntity.setvSectionID(planId);
                sectionEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LIVE + "-" + sectionEntity
                        .getvSectionID());
                sectionEntity.setvCoursseID(courseId);
                sectionEntity.setvStuCourseID(vStuCourseId);
                // 扣除金币
                dispatcherBll.deductStuGold(sectionEntity, vStuCourseId);
            }
            break;
        }
    }

    private void startLivePlay(final String vStuCourseID, final String courseId, final String sectionId) {
        if (!AppBll.getInstance(activity).canSeeVideo(new AppBll.OnSelectListener() {

            @Override
            public void onSelect(boolean goon) {
                if (goon) {
                    startLivePlayActivity(vStuCourseID, courseId, sectionId);
                }
            }
        })) {
            AppBll.getInstance(activity.getApplicationContext());
            return;
        }
        AppBll.getInstance(activity.getApplicationContext());
        startLivePlayActivity(vStuCourseID, courseId, sectionId);
    }

    private void startLivePlayActivity(String vStuCourseID, String courseId, String sectionId) {
        LiveVideoEnter.intentToLiveVideoActivity(activity, vStuCourseID, courseId, sectionId,
                LiveVideoBusinessConfig.ENTER_FROM_2);
    }

    private void startLecture() {
        //TODO
        boolean isLive = false;
        if (isLive) {
            //TODO
            final String liveId = "";
            if (!AppBll.getInstance(activity).canSeeVideo(new AppBll.OnSelectListener() {
                public void onSelect(boolean goon) {
                    if (goon) {
                        startLivePlayActivity(liveId);
                    }
                }
            })) {
                AppBll.getInstance(activity.getApplicationContext());
                return;
            }

            AppBll.getInstance(activity);
            startLivePlayActivity(liveId);
        } else {
            //TODO
            String playBackUrl = "";
            //TODO
            String radioType = "";//publicLiveCourseEntity.getRadioType()
            //TODO
            String teacherId = "";// publicLiveCourseEntity.getTeacherId()
            //TODO
            List<VideoQuestionEntity> lstVideoQuestion = null;// publicLiveCourseEntity.getLstVideoQuestion()
            //TODO
            int sendPlayVideoTime = 0;// publicLiveCourseEntity.getSendPlayVideoTime()
            //TODO
            long gotoClassTime = 0;// publicLiveCourseEntity.getGotoClassTime()
            //TODO
            String onlineNums = "";// publicLiveCourseEntity.getOnlineNums()
            //TODO
            String streamTimes = "";//publicLiveCourseEntity.getStreamTimes()
            VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
            videoEntity.setCourseId(courseId);
            videoEntity.setCourseName(chapterName);
            videoEntity.setPlayVideoId(courseId);
            videoEntity.setPlayVideoName(chapterName);
            videoEntity.setVideoPath(playBackUrl);
            videoEntity.setvCourseSendPlayVideoTime(ShareDataManager.getInstance().getInt("send_playvideo_time", 180, ShareDataManager.SHAREDATA_USER));
            videoEntity.setVideoCacheKey(playBackUrl);
            videoEntity.setLiveId(courseId);
            videoEntity.setvLivePlayBackType(2);
            videoEntity.setVisitTimeKey("2-" + courseId + "-" + teacherId);
            videoEntity.setLstVideoQuestion(lstVideoQuestion);
            videoEntity.setvCourseSendPlayVideoTime(sendPlayVideoTime);
            videoEntity.setGotoClassTime(gotoClassTime);
            videoEntity.setOnlineNums(onlineNums);
            videoEntity.setStreamTimes(streamTimes);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoliveplayback", videoEntity);
            bundle.putInt("type", 2);
            if ("720P".equals(radioType)) {
                LiveVideoEnter.intentToLectureLivePlayBackVideo(activity, bundle, activity.getClass().getSimpleName());
            } else {
                LiveVideoEnter.intentTo(activity, bundle, activity.getClass().getSimpleName());
            }
        }
    }

    private void startLivePlayActivity(String sectionId) {
        LiveVideoEnter.intentToLiveVideoActivityLecture(activity, sectionId, LiveVideoBusinessConfig
                .ENTER_FROM_22);
    }

    private void startHeart() {
        VideoSectionEntity sectionEntity = new VideoSectionEntity();
        sectionEntity.setvSectionName(chapterName);
        sectionEntity.setvChapterName(chapterName);
        sectionEntity.setvChapterID(planId);
        sectionEntity.setvSectionID(planId);
        sectionEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LIVE + "-" + sectionEntity
                .getvSectionID());
        sectionEntity.setvCoursseID(courseId);
        sectionEntity.setvStuCourseID(vStuCourseId);
        // 扣除金币
        dispatcherBll.deductStuGold(sectionEntity, vStuCourseId);
    }

    private void startRecord() {

    }
}
