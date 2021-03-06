package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.route.module.ModuleHandler;
import com.xueersi.common.route.module.entity.Module;
import com.xueersi.common.route.module.entity.ModuleData;
import com.xueersi.common.route.module.moduleInterface.AbsDispatcher;
import com.xueersi.common.route.module.startParam.ParamKey;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.share.business.biglive.config.BigLiveCfg;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.business.EnglishNameBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BigLivePlayBackEntity;
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
    private long offset;
    /** 讲座类型 0未知，1普通讲座 2大班讲座 3轻直播*/
    private int liveRoomType;
    private DispatcherBll dispatcherBll;
    /**
     * 讲座是否是大班 灰度状态
     **/
//    private int big_live_type = -1;
    /**
     * 直播 是否是大班 灰度状态
     */
    private int planVersion = DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_DEFAULT;
    private String paramsJson;

    public interface LiveNewStatus {
        int LIVE_UNBEGIN = 1;//待开始
        int LIVE_LIVING = 2;//进行中
        int LIVE_WAIT_PLAYBACK = 3;//等待回放
        int LIVE_CAN_PLAYBACK = 4;//未完成
        int LIVE_CAN_PLAYBACK_PLUS = 5;//已完成
    }

    private Activity activity;
    DataLoadEntity dataLoadEntity;
    EnglishNameBusiness englishNameBll;

    @Override
    public void dispatch(final Activity srcActivity, final Bundle bundle, final int requestCode) {
        if (bundle == null) {
            return;
        }
        boolean have = XesPermission.checkPermission(srcActivity,
                new LiveActivityPermissionCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        LiveMainHandler.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                dispatchInfo(srcActivity, bundle, requestCode);
                            }
                        });

                    }
                }, PermissionConfig.PERMISSION_CODE_STORAGE);
        if (have) {
            dispatchInfo(srcActivity, bundle, requestCode);
        }
    }

    private void dispatchInfo(Activity srcActivity, Bundle bundle, int requestCode) {
        englishNameBll = new EnglishNameBusiness(srcActivity);
        try {
            boolean supportName = englishNameBll.checkOverName();
            ShareDataManager.getInstance().put(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK, supportName, ShareDataManager.SHAREDATA_USER);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (bundle.containsKey(ParamKey.EXTRAKEY_JSONPARAM)) {
            activity = srcActivity;
            dispatcherBll = new DispatcherBll(srcActivity);
            paramsJson = bundle.getString(ParamKey.EXTRAKEY_JSONPARAM);
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
                //视频标记点位置
                offset = jsonObject.optLong("offset");
                // 大班灰度状态
//                big_live_type = jsonObject.optInt("bigLiveStatus", big_live_type);
                planVersion = jsonObject.optInt("planVersion",
                        DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_DEFAULT);
                liveRoomType = jsonObject.optInt("liveRoomType",DispatcherConfig.PUBLIC_TYPE_UNKNOW);
                if(type == TYPE_LECTURE  && (liveRoomType == DispatcherConfig.PUBLIC_TYPE_COMMON || liveRoomType == DispatcherConfig.PUBLIC_TYPE_LIGHTLIVE)){
                    Module m = new Module();
                    m.moduleName = "livepublic";
                    m.param = paramsJson;
                    m.moduleType = 0;
                    ModuleHandler.start(srcActivity, m);
                    return;
                }
                if (type == TYPE_LIVE) {
                    //startLive();
                    enterLive();
                } else if (type == TYPE_RECORD) {
                    startRecord();
                } else if (type == TYPE_EXP) {
                    startExp();
                } else if (type == TYPE_AUDIT) {
                    startAudit();
                } else if (type == TYPE_LECTURE) {
                    enterLecture();
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
            int liveType = bundle.getInt("type", 0);
            if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE){
                Module m = new Module();
                m.moduleName = "livepublic";
                ModuleHandler.start(srcActivity, new ModuleData(m, bundle));
                return;
            }
            Intent intent = new Intent(srcActivity, LiveVideoLoadActivity.class);
            intent.putExtras(bundle);
            srcActivity.startActivityForResult(intent, requestCode);
        }
    }

    private void enterLive() {
        //startLive(true);
        if (planVersion == DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_DEFAULT) {
            dataLoadEntity = new DataLoadEntity(activity);
            dispatcherBll.bigLivePlanVersion(Integer.parseInt(planId), 3,
                    new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            BigLiveGrayEntity entity = (BigLiveGrayEntity) objData[0];
                            int planVersion = entity.getPlanVersion();
                            if (planVersion != DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_DEFAULT) {
                                if (DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_COMMON == planVersion) {
                                    startLive(false);
                                } else {
                                    startLive(true);
                                }
                            } else {
                                XESToastUtils.showToast(activity, "未知直播类型");
                            }
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            XESToastUtils.showToast(activity, failMsg);
                        }
                    }, dataLoadEntity);

        } else if (planVersion == DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_COMMON) {
            startLive(false);
        } else {
            startLive(true);
        }
    }

    private void startAudit() {
        LiveVideoEnter.intentToAuditClassActivity(activity, vStuCourseId, planId,
                planVersion != DispatcherConfig.BIGLIVE_GRAY_CONTROL_PLANVERSION_COMMON);
    }

    private void startExp() {
        VideoSectionEntity sectionEntity = new VideoSectionEntity();
        sectionEntity.setvSectionName(chapterName);
        sectionEntity.setvChapterName(chapterName);
        dispatcherBll.deductStuGolds(sectionEntity, planId, termId);
    }



    private void startLive(boolean isBiglive) {
        switch (rstatus) {
            case LiveNewStatus.LIVE_UNBEGIN://未开始
                break;
            case LiveNewStatus.LIVE_LIVING://进行中
                startLivePlay(vStuCourseId, courseId, planId, isBiglive);
                break;
            case LiveNewStatus.LIVE_WAIT_PLAYBACK://等待回放
            case LiveNewStatus.LIVE_CAN_PLAYBACK: //未完成
            case LiveNewStatus.LIVE_CAN_PLAYBACK_PLUS: { //已完成
                if (isBiglive) {
                    //大班整合回放
                    dispatcherBll.bigLivePlayBack(planId, BigLiveCfg.BIGLIVE_BIZID_LIVE,
                            vStuCourseId, new AbstractBusinessDataCallBack() {
                                @Override
                                public void onDataSucess(Object... objData) {
                                    BigLivePlayBackEntity entity =
                                            (BigLivePlayBackEntity) objData[0];
                                    if (entity != null) {
                                        enterBigLivePlayBack(entity,LocalCourseConfig.LIVETYPE_LIVE);
                                    }
                                }
                            }, dataLoadEntity);

                } else {
                    //普通直播回放
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
            }
            break;
            default:
                break;
        }
    }


    private void startLivePlay(final String vStuCourseID, final String courseId,
                               final String sectionId,
                               final boolean isBigLive) {
        if (!AppBll.getInstance(activity).canSeeVideo(new AppBll.OnSelectListener() {

            @Override
            public void onSelect(boolean goon) {
                if (goon) {
                    startLivePlayActivity(vStuCourseID, courseId, sectionId, isBigLive);
                }
            }
        })) {
            AppBll.getInstance(activity.getApplicationContext());
            return;
        }
        AppBll.getInstance(activity.getApplicationContext());
        startLivePlayActivity(vStuCourseID, courseId, sectionId, isBigLive);
    }


    private void startLivePlayActivity(String vStuCourseID, String courseId, String sectionId,
                                       boolean isBigLive) {
        LiveVideoEnter.intentToLiveVideoActivity(activity, vStuCourseID, courseId, sectionId,
                LiveVideoBusinessConfig.ENTER_FROM_2, isBigLive);
    }


    private void startLecture(final boolean isBigLive) {
        boolean isLive = rstatus == 1;
        if (isLive) {
            final String liveId = planId;
            if (!AppBll.getInstance(activity).canSeeVideo(new AppBll.OnSelectListener() {
                public void onSelect(boolean goon) {
                    if (goon) {
                        startLivePlayActivity(liveId, isBigLive);
                    }
                }
            })) {
                AppBll.getInstance(activity.getApplicationContext());
                return;
            }
            AppBll.getInstance(activity);
            startLivePlayActivity(liveId, isBigLive);
        } else {
            if (isBigLive) {
                dispatcherBll.getBigLivePublic(planId, BigLiveCfg.BIGLIVE_BIZID_LECTURE,
                        "0", new AbstractBusinessDataCallBack() {
                            @Override
                            public void onDataSucess(Object... objData) {
                                BigLivePlayBackEntity entity = (BigLivePlayBackEntity) objData[0];
                                if (entity != null) {
                                    //enterBigLiveLecturePlayBack(entity);
                                    enterBigLivePlayBack(entity,LocalCourseConfig.LIVETYPE_LECTURE);
                                }
                            }
                        }, dataLoadEntity);

            }
//            else {
//                dispatcherBll.getPublic(chapterName, planId, teacherId, gotoClassTime,
//                        new AbstractBusinessDataCallBack() {
//                            @Override
//                            public void onDataSucess(Object... objData) {
//                                PublicEntity publicEntity = (PublicEntity) objData[0];
//                                if (publicEntity != null) {
//                                    playLivePlayBackVideo(publicEntity);
//                                }
//                            }
//                        }, dataLoadEntity);
//            }

        }
    }

    AbstractBusinessDataCallBack publicGrayControlCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            PublicLiveGrayEntity mPublicLiveGrayEntity = (PublicLiveGrayEntity) objData[0];
            if (mPublicLiveGrayEntity.getStatus() != DispatcherConfig.PUBLIC_TYPE_BIGLIVE){
                Module m = new Module();
                m.moduleName = "livepublic";
                m.param = paramsJson;
                m.moduleType = 0;
                ModuleHandler.start(activity, m);
                return;
            }
            startLecture(mPublicLiveGrayEntity.getStatus() == DispatcherConfig.PUBLIC_TYPE_BIGLIVE);
        }

        @Override
        public void onDataFail(int errStatus, String failMsg) {
            super.onDataFail(errStatus, failMsg);
            XESToastUtils.showToast(activity, failMsg);
        }
    };


    /**
     * 讲座，直播/回放 入口
     *
     * @return
     */
    private void enterLecture() {
        if (liveRoomType == DispatcherConfig.PUBLIC_TYPE_BIGLIVE) {
            startLecture(true);
        }  else if (liveRoomType == DispatcherConfig.PUBLIC_TYPE_UNKNOW) {
            dataLoadEntity = new DataLoadEntity(activity);
            dispatcherBll.publicLiveIsGrayLecture(planId, true, publicGrayControlCallBack,
                    dataLoadEntity);
        }
    }


//    public void playLivePlayBackVideo(PublicEntity publicLiveCourseEntity) {
//        // 播放数据设定
//        ShareDataManager dataManager = ShareDataManager.getInstance();
//        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
//        videoEntity.setCourseId(publicLiveCourseEntity.getCourseId());
//        videoEntity.setCourseName(publicLiveCourseEntity.getCourseName());
//        videoEntity.setPlayVideoId(publicLiveCourseEntity.getCourseId());
//        videoEntity.setPlayVideoName(publicLiveCourseEntity.getCourseName());
//        videoEntity.setVideoPath(publicLiveCourseEntity.getPlayBackUrl());
//        videoEntity.setvCourseSendPlayVideoTime(dataManager.getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180,
//                SHAREDATA_USER));
//        videoEntity.setVideoCacheKey(publicLiveCourseEntity.getPlayBackUrl());
//        videoEntity.setLiveId(publicLiveCourseEntity.getCourseId());
//        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVETYPE_LECTURE);
//        videoEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LECTURE + "-" + publicLiveCourseEntity.getCourseId() +
//                "-" + publicLiveCourseEntity.getTeacherId());
//
//        videoEntity.setLstVideoQuestion(publicLiveCourseEntity.getLstVideoQuestion());
//        videoEntity.setvCourseSendPlayVideoTime(publicLiveCourseEntity.getSendPlayVideoTime());
//        videoEntity.setGotoClassTime(publicLiveCourseEntity.getGotoClassTime());
//        videoEntity.setOnlineNums(publicLiveCourseEntity.getOnlineNums());
//        videoEntity.setStreamTimes(publicLiveCourseEntity.getStreamTimes());
//        //测试代码
////        publicLiveCourseEntity.setGently(true);
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("videoliveplayback", videoEntity);
//        bundle.putString("planId", planId);
//        bundle.putInt("type", 2);
//        bundle.putBoolean("isGently",publicLiveCourseEntity.isGently());
//        //轻直播需要参数
//        if (publicLiveCourseEntity.isGently()){
//            bundle.putString("gentlyNotice",publicLiveCourseEntity.getGentlyNotice());
//            if (publicLiveCourseEntity.getLpWeChatEntity() != null){
//                bundle.putInt("tipType",publicLiveCourseEntity.getLpWeChatEntity().getTipType());
//            }
//        }
//        if ("720P".equals(publicLiveCourseEntity.getRadioType())) {
//            LiveVideoEnter.intentToLectureLivePlayBackVideo(activity, bundle,
//                    activity.getClass().getSimpleName());
//        } else {
//            // LiveVideoEnter.intentTo(activity, bundle, activity.getClass().getSimpleName());
//            // FIXME: 2019/9/6  线上bug 直播间有字符串比较逻辑 此次需写死 ：PublicLiveDetailActivity
//            LiveVideoEnter.intentTo(activity, bundle, "PublicLiveDetailActivity");
//        }
//    }


    /**
     * 进度大班整合讲座 回放
     *
     * @param entity
     */
    /*private void enterBigLiveLecturePlayBack(BigLivePlayBackEntity entity) {
        ShareDataManager dataManager = ShareDataManager.getInstance();
        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
        videoEntity.setCourseName(entity.getPlanInfo().getName());
        videoEntity.setPlayVideoId(entity.getPlanInfo().getId());
        videoEntity.setPlayVideoName(entity.getPlanInfo().getName());
        videoEntity.setNowTime(entity.getNowTime());


        if (entity.getConfigs() != null) {
            videoEntity.setVideoPath(entity.getConfigs().getVideoFile());
            videoEntity.setGetChatRecordUrl(entity.getConfigs().getGetChatRecordUrl());
            videoEntity.setGetMetadataUrl(entity.getConfigs().getGetMetadataUrl());
            videoEntity.setInitModuleUrl(entity.getConfigs().getInitModuleUrl());
            videoEntity.setIrcRoomsJson(entity.getConfigs().getIrcRoomsJson());
        }

        if (entity.getPlanInfo().getSubjectIds() != null && entity.getPlanInfo().getSubjectIds().size() > 0) {
            videoEntity.setSubjectId(entity.getPlanInfo().getSubjectIds().get(0));
        }
        if (entity.getPlanInfo().getGradeIds() != null && entity.getPlanInfo().getGradeIds().size() > 0) {
            videoEntity.setGradId(entity.getPlanInfo().getGradeIds().get(0));
        }

        if (entity.getStuLiveInfo() != null) {
            videoEntity.setClassId(entity.getStuLiveInfo().getClassId());
            videoEntity.setCourseId(entity.getStuLiveInfo().getCourseId());
        }

        videoEntity.setBigLive(true);
        if (entity.getPlanInfo() != null) {
            videoEntity.setsTime(entity.getPlanInfo().getsTime());
            videoEntity.seteTime(entity.getPlanInfo().geteTIme());
        }
        videoEntity.setvCourseSendPlayVideoTime(dataManager.getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180,
                SHAREDATA_USER));
        videoEntity.setVideoCacheKey(entity.getConfigs().getVideoPath());

        videoEntity.setLiveId(entity.getPlanInfo().getId());
        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVETYPE_LECTURE);
        videoEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LECTURE + "-" + entity.getPlanInfo().getId() +
                "-" + entity.getMainTeacher().getId());
        //大班整合默认 走新ijk
        MediaPlayer.setIsNewIJK(true);
        updatePsInfo(entity);
        if (entity.getConfigs() != null) {
            videoEntity.setProtocol(entity.getConfigs().getProtocol());
            videoEntity.setFileId(entity.getConfigs().getFileId());
            videoEntity.setBeforeClassFileId(entity.getConfigs().getBeforeClassFileId());
            videoEntity.setAfterClassFileId(entity.getConfigs().getAfterClassFileId());
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoliveplayback", videoEntity);
        bundle.putInt("type", 2);
        bundle.putString("planId", planId);
        bundle.putBoolean("isBigLive", true);
        // FIXME: 2019/9/6  PublicLiveDetailActivity
        //LiveVideoEnter.intentTo(activity, bundle, activity.getClass().getSimpleName());
        LiveVideoEnter.intentTo(activity, bundle, "PublicLiveDetailActivity");
    }*/


    /**
     * 进入大班整合普通回放
     * @param liveType  直播类型
     * @param entity
     */
    private void enterBigLivePlayBack(BigLivePlayBackEntity entity,int liveType) {
        ShareDataManager dataManager = ShareDataManager.getInstance();
        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
        videoEntity.setNowTime(entity.getNowTime());
        videoEntity.setOffset(offset);
        videoEntity.setSkinType(entity.getSkinType());


        if (entity.getConfigs() != null) {

            videoEntity.setVideoPath(entity.getConfigs().getVideoFile());
            videoEntity.setGetChatRecordUrl(entity.getConfigs().getGetChatRecordUrl());
            videoEntity.setGetMetadataUrl(entity.getConfigs().getGetMetadataUrl());
            videoEntity.setInitModuleUrl(entity.getConfigs().getInitModuleUrl());
            videoEntity.setIrcRoomsJson(entity.getConfigs().getIrcRoomsJson());
            videoEntity.setVideoCacheKey(entity.getConfigs().getVideoPath());

            videoEntity.setProtocol(entity.getConfigs().getProtocol());
            videoEntity.setFileId(entity.getConfigs().getFileId());
            videoEntity.setBeforeClassFileId(entity.getConfigs().getBeforeClassFileId());
            videoEntity.setAfterClassFileId(entity.getConfigs().getAfterClassFileId());
        }

        if (entity.getStuLiveInfo() != null) {
            videoEntity.setClassId(entity.getStuLiveInfo().getClassId());
            videoEntity.setTeamId(entity.getStuLiveInfo().getTeamId());
            videoEntity.setCourseId(entity.getStuLiveInfo().getCourseId());
        }

        videoEntity.setBigLive(true);
        if (entity.getPlanInfo() != null) {
            if (entity.getPlanInfo().getSubjectIds() != null && entity.getPlanInfo().getSubjectIds().size() > 0) {
                videoEntity.setSubjectId(entity.getPlanInfo().getSubjectIds().get(0));
            }
            if (entity.getPlanInfo().getGradeIds() != null && entity.getPlanInfo().getGradeIds().size() > 0) {
                videoEntity.setGradId(entity.getPlanInfo().getGradeIds().get(0));
            }

            videoEntity.setCourseName(entity.getPlanInfo().getName());
            videoEntity.setPlayVideoId(entity.getPlanInfo().getId());
            videoEntity.setPlayVideoName(entity.getPlanInfo().getName());

            videoEntity.setsTime(entity.getPlanInfo().getsTime());
            videoEntity.seteTime(entity.getPlanInfo().geteTIme());

            videoEntity.setLiveId(entity.getPlanInfo().getId());

            String visitTimeKeyStr ="";
            if(liveType == LocalCourseConfig.LIVETYPE_LECTURE){
                visitTimeKeyStr = LocalCourseConfig.LIVETYPE_LECTURE + "-" + entity.getPlanInfo().getId() +
                        "-" + entity.getMainTeacher().getId();
            }else{
                visitTimeKeyStr = liveType + "-" + entity.getPlanInfo().getId();
            }
            videoEntity.setVisitTimeKey(visitTimeKeyStr);
        }

        videoEntity.setvCourseSendPlayVideoTime(dataManager.getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180,
                SHAREDATA_USER));
        videoEntity.setvLivePlayBackType(liveType);
        //设置购课id
        videoEntity.setStuCourseId(vStuCourseId);
        //大班整合默认 走新ijk
        MediaPlayer.setIsNewIJK(true);
        updatePsInfo(entity);

        Bundle bundle = new Bundle();
        bundle.putSerializable("videoliveplayback", videoEntity);
        bundle.putInt("type", liveType);
        bundle.putString("planId", planId);
        bundle.putBoolean("isBigLive", true);
        bundle.putInt("skinType",entity.getSkinType());
        String where = liveType == LocalCourseConfig.LIVETYPE_LECTURE?"PublicLiveDetailActivity":activity.getClass().getSimpleName();
        LiveVideoEnter.intentTo(activity, bundle, where);

    }


    /**
     * 更新用户信息中的磐石信息
     **/
    private void updatePsInfo(BigLivePlayBackEntity entity) {
        try {
            if (entity != null) {
                //更新UserBll 中磐石信息
                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                if (entity.getStuInfo() != null) {
                    if (!TextUtils.isEmpty(entity.getStuInfo().getPsImId())) {
                        myUserInfoEntity.setPsimId(entity.getStuInfo().getPsImId());
                    }
                    if (!TextUtils.isEmpty(entity.getStuInfo().getPsImPwd())) {
                        myUserInfoEntity.setPsimPwd(entity.getStuInfo().getPsImPwd());
                    }
                }

                if (entity.getConfigs() != null) {
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


    private void startLivePlayActivity(String sectionId, boolean isBigLive) {
        if (isBigLive) {
            LiveVideoEnter.intentToLiveVideoActivityLecture(activity, sectionId,
                    LiveVideoBusinessConfig
                            .ENTER_FROM_22, isBigLive);
        }
//        else {
//            LiveVideoEnter.intentToLiveVideoActivityLecture(activity, sectionId,
//                    LiveVideoBusinessConfig
//                            .ENTER_FROM_22);
//        }
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
