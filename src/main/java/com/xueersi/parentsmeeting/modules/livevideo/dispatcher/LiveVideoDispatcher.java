package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.route.module.moduleInterface.AbsDispatcher;
import com.xueersi.common.route.module.startParam.ParamKey;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BigLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.json.JSONObject;

import static com.xueersi.common.sharedata.ShareDataManager.SHAREDATA_USER;

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
    //TODO 试卷讲评
    public static final int TYPE_EXAM = 7;

    private String vStuCourseId;
    private String courseId;
    private String planId;
    private String chapterName;
    private String termId;
    private String mid;
    private int type;
    private int status;
    private int rstatus;
    private int variety;
    String teacherId;
    String gotoClassTime;
    private DispatcherBll dispatcherBll;
    /**是否是大班整合回放**/
    private boolean isBigLive;
    /**是否是大班 灰度状态 **/
    private int big_live_type = -1;
    public interface LiveNewStatus {
        int LIVE_UNBEGIN = 1;//待开始
        int LIVE_LIVING = 2;//进行中
        int LIVE_WAIT_PLAYBACK = 3;//等待回放
        int LIVE_CAN_PLAYBACK = 4;//未完成
        int LIVE_CAN_PLAYBACK_PLUS = 5;//已完成
    }

    private Activity activity;
    DataLoadEntity  dataLoadEntity;
    @Override
    public void dispatch(Activity srcActivity, Bundle bundle, int requestCode) {

        Log.e("ckTrac","========>dispatch:999999:"+bundle.containsKey(ParamKey.EXTRAKEY_JSONPARAM));

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
                rstatus = jsonObject.optInt("rstatus");
                vStuCourseId = jsonObject.optString("stuCouId");
                courseId = jsonObject.optString("courseId");
                planId = jsonObject.optString("planId");
                chapterName = jsonObject.optString("planName");
                termId = jsonObject.optString("termId");
                mid = jsonObject.optString("mid");
                type = jsonObject.optInt("variety");
                teacherId = jsonObject.optString("teacherId");
                gotoClassTime = jsonObject.optString("stime");
                // 是否是大班整合
                isBigLive = jsonObject.optBoolean("isBigLive",false);
                // 大班灰度状态
                big_live_type =  jsonObject.optInt("bigLiveStatus",big_live_type);

                if (type == TYPE_LIVE) {
                    startLive();
                } else if (type == TYPE_RECORD) {
                    startRecord();
                } else if (type == TYPE_EXP) {
                    startExp();
                } else if (type == TYPE_AUDIT) {
                    startAudit();
                } else if (type == TYPE_LECTURE) {
                    isBigLive();
                } else if (type == TYPE_HEART) {
                    startHeart();
                } else if (type == TYPE_EXAM) {
                    startExam();
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
        switch (rstatus) {
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



    private void startLecture(final boolean isBigLive) {
        boolean isLive = rstatus == 1;
        if (isLive) {
            final String liveId = planId;
            if (!AppBll.getInstance(activity).canSeeVideo(new AppBll.OnSelectListener() {
                public void onSelect(boolean goon) {
                    if (goon) {
                        startLivePlayActivity(liveId,isBigLive);
                    }
                }
            })) {
                AppBll.getInstance(activity.getApplicationContext());
                return;
            }
            AppBll.getInstance(activity);
            startLivePlayActivity(liveId,isBigLive);
        } else {
            if(isBigLive){
                dispatcherBll.getBigLivePublic(planId,"2","0", new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        BigLivePlayBackEntity entity = (BigLivePlayBackEntity) objData[0];
                        if(entity != null){
                            enterBigLivePlayBack(entity);
                        }
                    }
                },dataLoadEntity);

            }else {
                dispatcherBll.getPublic(chapterName, planId, teacherId, gotoClassTime, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        PublicEntity publicEntity = (PublicEntity) objData[0];
                        if (publicEntity != null) {
                            playLivePlayBackVideo(publicEntity);
                        }
                    }
                },dataLoadEntity);
            }

        }
    }

    AbstractBusinessDataCallBack  publicGrayControlCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            PublicLiveGrayEntity mPublicLiveGrayEntity = (PublicLiveGrayEntity)objData[0];
            startLecture(mPublicLiveGrayEntity.getStatus()==DispatcherConfig.PUBLIC_GRAY_CONTROL_BIG_LIVE);
        }
        @Override
        public void onDataFail(int errStatus, String failMsg) {
            super.onDataFail(errStatus, failMsg);
            XESToastUtils.showToast(activity,failMsg);
        }
    };


    /**
     * 是否是大班整合 直播
     * @return
     */
    private boolean isBigLive() {
        boolean result = isBigLive;
        if(big_live_type == DispatcherConfig.PUBLIC_GRAY_CONTROL_BIG_LIVE) {
            startLecture(true);
        } else if(big_live_type == DispatcherConfig.PUBLIC_GRAY_CONTROL_COMMON){
            startLecture(false);
        } else if (big_live_type == DispatcherConfig.PUBLIC_GRAY_CONTROL_DEFALUT){
            dataLoadEntity = new DataLoadEntity(activity);
            dispatcherBll.publicLiveIsGrayLecture(planId,true, publicGrayControlCallBack,dataLoadEntity);
        }
        return result;
    }


    public void playLivePlayBackVideo(PublicEntity publicLiveCourseEntity) {
        // 播放数据设定
        ShareDataManager dataManager = ShareDataManager.getInstance();
        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
        videoEntity.setCourseId(publicLiveCourseEntity.getCourseId());
        videoEntity.setCourseName(publicLiveCourseEntity.getCourseName());
        videoEntity.setPlayVideoId(publicLiveCourseEntity.getCourseId());
        videoEntity.setPlayVideoName(publicLiveCourseEntity.getCourseName());
        videoEntity.setVideoPath(publicLiveCourseEntity.getPlayBackUrl());
        videoEntity.setvCourseSendPlayVideoTime(dataManager.getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180,
                SHAREDATA_USER));
        videoEntity.setVideoCacheKey(publicLiveCourseEntity.getPlayBackUrl());
        videoEntity.setLiveId(publicLiveCourseEntity.getCourseId());
        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVETYPE_LECTURE);
        videoEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LECTURE + "-" + publicLiveCourseEntity.getCourseId() +
                "-" + publicLiveCourseEntity.getTeacherId());

        videoEntity.setLstVideoQuestion(publicLiveCourseEntity.getLstVideoQuestion());
        videoEntity.setvCourseSendPlayVideoTime(publicLiveCourseEntity.getSendPlayVideoTime());
        videoEntity.setGotoClassTime(publicLiveCourseEntity.getGotoClassTime());
        videoEntity.setOnlineNums(publicLiveCourseEntity.getOnlineNums());
        videoEntity.setStreamTimes(publicLiveCourseEntity.getStreamTimes());
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoliveplayback", videoEntity);
        bundle.putInt("type", 2);
        if ("720P".equals(publicLiveCourseEntity.getRadioType())) {
            LiveVideoEnter.intentToLectureLivePlayBackVideo(activity, bundle, activity.getClass()
                    .getSimpleName());
        } else {
            LiveVideoEnter.intentTo(activity, bundle, activity.getClass().getSimpleName());
        }
    }




    /**
     * 进度大班整合 回放
     * @param entity
     */
    private void enterBigLivePlayBack(BigLivePlayBackEntity entity) {

        ShareDataManager dataManager = ShareDataManager.getInstance();
        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
        videoEntity.setCourseId(entity.getPlanInfo().getId());
        videoEntity.setCourseName(entity.getPlanInfo().getName());
        videoEntity.setPlayVideoId(entity.getPlanInfo().getId());
        videoEntity.setPlayVideoName(entity.getPlanInfo().getName());

        if(entity.getConfigs() != null){
            videoEntity.setVideoPath(entity.getConfigs().getVideoFile());
            videoEntity.setGetChatRecordUrl(entity.getConfigs().getGetChatRecordUrl());
            videoEntity.setGetMetadataUrl(entity.getConfigs().getGetMetadataUrl());
            videoEntity.setInitModuleUrl(entity.getConfigs().getInitModuleUrl());
        }

        if(entity.getPlanInfo().getSubjectIds() != null && entity.getPlanInfo().getSubjectIds().size() > 0){
            videoEntity.setSubjectId(entity.getPlanInfo().getSubjectIds().get(0));
        }
        if(entity.getPlanInfo().getGradeIds() != null && entity.getPlanInfo().getGradeIds().size() >0){
            videoEntity.setGradId(entity.getPlanInfo().getGradeIds().get(0));
        }

        if(entity.getStuLiveInfo() != null){
            videoEntity.setClassId(entity.getStuLiveInfo().getClassId());
        }

        videoEntity.setBigLive(true);
        if(entity.getPlanInfo() != null){
            videoEntity.setsTime(entity.getPlanInfo().getsTime());
            videoEntity.seteTime(entity.getPlanInfo().geteTIme());
        }
        videoEntity.setvCourseSendPlayVideoTime(dataManager.getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180,
                SHAREDATA_USER));
        videoEntity.setVideoCacheKey(entity.getConfigs().getVideoPath());

        videoEntity.setLiveId(entity.getPlanInfo().getId());
        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVETYPE_LECTURE);
        videoEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LECTURE + "-" + entity.getPlanInfo().getId()+
                "-" + entity.getMainTeacher().getId());
        //大班整合默认 走新ijk
        MediaPlayer.setIsNewIJK(true);
        updatePsInfo(entity);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoliveplayback", videoEntity);
        bundle.putInt("type",2);
        bundle.putBoolean("isBigLive",true);
        LiveVideoEnter.intentTo(activity, bundle, activity.getClass().getSimpleName());
    }


    /**
     * 更新用户信息中的磐石信息
     **/
    private void updatePsInfo(BigLivePlayBackEntity entity) {
        try {
            if (entity != null) {
                //更新UserBll 中磐石信息
                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                if(entity.getStuInfo() != null){
                    if (!TextUtils.isEmpty(entity.getStuInfo().getPsImId())) {
                        myUserInfoEntity.setPsimId(entity.getStuInfo().getPsImId());
                    }
                    if (!TextUtils.isEmpty(entity.getStuInfo().getPsImPwd())) {
                        myUserInfoEntity.setPsimPwd(entity.getStuInfo().getPsImPwd());
                    }
                }

                if(entity.getConfigs() != null){
                    if (!TextUtils.isEmpty(entity.getConfigs().getAppId())) {
                        myUserInfoEntity.setPsAppId(entity.getConfigs().getAppId());
                    }
                    if (!TextUtils.isEmpty(entity.getConfigs().getAppKey())) {
                        myUserInfoEntity.setPsAppClientKey(entity.getConfigs().getAppKey());
                    }
                }
                UserBll.getInstance().saveMyUserInfo(myUserInfoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void startLivePlayActivity(String sectionId,boolean isBigLive) {
        if(isBigLive) {
            LiveVideoEnter.intentToLiveVideoActivityLecture(activity, sectionId, LiveVideoBusinessConfig
                    .ENTER_FROM_22,isBigLive);
        } else {
            LiveVideoEnter.intentToLiveVideoActivityLecture(activity, sectionId, LiveVideoBusinessConfig
                    .ENTER_FROM_22);
        }
    }

    private void startHeart() {
        VideoSectionEntity sectionEntity = new VideoSectionEntity();
        sectionEntity.setvSectionName(chapterName);
        sectionEntity.setvChapterName(chapterName);
        sectionEntity.setvChapterID(mid);
        sectionEntity.setvSectionID(mid);
        sectionEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LIVE + "-" + sectionEntity
                .getvSectionID());
        sectionEntity.setvCoursseID(courseId);
        sectionEntity.setvStuCourseID(vStuCourseId);
        // 扣除金币
        dispatcherBll.deductStuGold(sectionEntity, vStuCourseId);
    }

    private void startRecord() {

    }

    private void startExam() {
        VideoSectionEntity sectionEntity = new VideoSectionEntity();
        sectionEntity.setvSectionName(chapterName);
        sectionEntity.setvChapterName(chapterName);
        sectionEntity.setvChapterID(mid);
        sectionEntity.setvSectionID(mid);
        sectionEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LIVE + "-" + sectionEntity
                .getvSectionID());
        sectionEntity.setvCoursseID(courseId);
        sectionEntity.setvStuCourseID(vStuCourseId);
        // 扣除金币
        dispatcherBll.deductStuGold(sectionEntity, vStuCourseId);
    }
}
