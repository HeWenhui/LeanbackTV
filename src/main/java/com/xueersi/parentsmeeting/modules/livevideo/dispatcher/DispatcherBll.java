package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpAutoLive;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.LiveExperienceEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BigLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveTransferHttpManager;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import static com.xueersi.parentsmeeting.modules.livevideo.dispatcher.DispatcherConfig.LIVE_PLAY_BACK_TUTOR_FLAGE;

/**
 * Created by dqq on 2019/7/11.
 */
public class DispatcherBll extends BaseBll {

    private DispatcherHttpManager dispatcherHttpManager;
    private LiveTransferHttpManager liveTransferHttpManager;
    private DispatcherHttpResponseParser dispatcherHttpResponseParser;

    public DispatcherBll(Context context) {
        super(context);
        dispatcherHttpManager = new DispatcherHttpManager(context);
        liveTransferHttpManager = new LiveTransferHttpManager(mContext);
        dispatcherHttpResponseParser = new DispatcherHttpResponseParser();
    }


    public void deductStuGold(final VideoSectionEntity sectionEntity, final String stuCouId) {
        final DataLoadEntity dataLoadEntity = new DataLoadEntity(mContext);
        postDataLoadEvent(dataLoadEntity.beginLoading());
        // 网络加载数据
        liveTransferHttpManager.deductStuGold(stuCouId, sectionEntity.getvCoursseID(),
                sectionEntity.getvSectionID(), 0, new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        VideoResultEntity entity = dispatcherHttpResponseParser
                                .deductStuGoldParser(sectionEntity.getvSectionID(), stuCouId, responseEntity);
                        if (entity != null && entity.getIsArts() == 1) {
                            artscoursewarenewpoint(sectionEntity, stuCouId, entity, dataLoadEntity);
                        } else {
                            EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(dataLoadEntity
                                    .webDataSuccess()));
                            intentToPlayBack(sectionEntity, entity);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logger.i("onPmFailure");
                        EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(dataLoadEntity.webDataError
                                (ContextManager
                                        .getContext().getResources().getString(com.xueersi.parentsmeeting.base.R.string.net_request_error))));
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.i("onPmError");
                        EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(dataLoadEntity.webDataError
                                (responseEntity.getErrorMsg())));
                    }
                });
    }


    public void artscoursewarenewpoint(final VideoSectionEntity sectionEntity, final String stuCouId, final VideoResultEntity entitys, DataLoadEntity dataLoadEntity) {
//        DataLoadEntity dataLoadEntity = new DataLoadEntity(mContext);
//        postDataLoadEvent(dataLoadEntity.beginLoading());
        // 网络加载数据
        liveTransferHttpManager.artscoursewarenewpoint(sectionEntity.getvSectionID(), new HttpCallBack(dataLoadEntity) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                Loger.e("Duncan", "responseEntity:" + responseEntity);
                VideoResultEntity entity = dispatcherHttpResponseParser.parseNewArtsEvent(stuCouId, sectionEntity.getvSectionID(), entitys, responseEntity);
                intentToPlayBack(sectionEntity, entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                Loger.e("Duncan", "onPmFailureresponseEntity:" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                Loger.e("Duncan", "onPmErrorresponseEntity:" + responseEntity);
            }
        });
    }


    public void intentToPlayBack(VideoSectionEntity sectionEntit, VideoResultEntity result) {
        VideoSectionEntity sectionEntity = result.getMapVideoSectionEntity().get(sectionEntit.getvSectionID());
        // 播放数据设定
        VideoLivePlayBackEntity videoEntity = videoLivePlayBackFromVideoSection(sectionEntit, result,
                sectionEntit.getvSectionID());
        VideoLivePlayBackEntity tutorEntity = videoLivePlayBackFromVideoSection(sectionEntit, result,
                sectionEntit.getvSectionID() + LIVE_PLAY_BACK_TUTOR_FLAGE);

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
        LiveVideoEnter.intentTo((Activity) mContext, bundle, mContext.getClass().getSimpleName());
    }


    public VideoLivePlayBackEntity videoLivePlayBackFromVideoSection(VideoSectionEntity
                                                                             section, VideoResultEntity
                                                                             result, String sectionId) {
        VideoSectionEntity sectionEntity = result.getMapVideoSectionEntity().get(sectionId);
        if (sectionEntity == null) {
            return null;
        }

        // 播放数据设定
        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
        videoEntity.setLstPoint(result.getLstPoint());
        videoEntity.setCourseId(section.getvCoursseID());
        videoEntity.setChapterId(section.getvChapterID());
        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVE_PLAY_LIVE);
        videoEntity.setPlayVideoName(section.getvSectionName());
        videoEntity.setVideoCacheKey(section.getvTradeId());
        videoEntity.setLiveId(section.getvSectionID());
        videoEntity.setStuCourseId(section.getvStuCourseID());
        videoEntity.setVisitTimeKey(section.getVisitTimeKey());
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

    private int getSendPlayVideoTimeStatic(String sendPlayVideoTimeKey) {
        return ShareDataManager.getInstance().getInt(LocalCourseConfig.SENDPLAYVIDEOTIME, 180, ShareDataManager
                .SHAREDATA_USER);
    }

    public void deductStuGolds(final VideoSectionEntity sectionEntity, final String liveId, final String termId) {
        DataLoadEntity dataLoadEntity = new DataLoadEntity(mContext);
        postDataLoadEvent(dataLoadEntity.beginLoading());
        // 网络加载数据
        dispatcherHttpManager.deductStuGolds(liveId, termId,
                new HttpCallBack(dataLoadEntity) {


                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        LiveExperienceEntity entity = dispatcherHttpResponseParser
                                .deductStuGoldParsers(responseEntity);

                        entity.setTermId(termId);
                        entity.setLiveId(liveId);

                        // 播放数据设定
                        VideoLivePlayBackEntity videoEntity = new VideoLivePlayBackEntity();
                        videoEntity.setExpLiveType(entity.getExpLiveType());
                        videoEntity.setHbTime(entity.getHbTime());
                        videoEntity.setVisitTimeUrl(entity.getVisitTimeUrl());
                        videoEntity.setCourseId(entity.getClassId()); // classId
                        videoEntity.setChapterId(entity.getTermId()); // termId

                        videoEntity.setHalfBodyH5Url(entity.getHalfBodyH5Url());

                        videoEntity.setGradId(entity.getAutoLive().getGradId());
                        videoEntity.setSubjectId(entity.getAutoLive().getSubjectId());
                        videoEntity.setPattern(entity.getPattern());

                        videoEntity.setSpeechEvalUrl(entity.getSpeechEvalUrl());
                        videoEntity.setSpeechEvalSubmitUrl(entity.getSpeechEvalSubmitUrl());
                        videoEntity.setSubmitCourseWareH5AnswerUseVoiceUrl(entity
                                .getSubmitCourseWareH5AnswerUseVoiceUrl());
                        videoEntity.setInteractUrl(entity.getInteractUrl());
                        videoEntity.setSubjectiveSubmitUrl(entity.getSubjectiveSubmitUrl());
                        videoEntity.setCoursewareH5Url(entity.getCoursewareH5Url());
                        videoEntity.setExamUrl(entity.getExamUrl());
                        videoEntity.setPrek(entity.isPreK());
                        videoEntity.setNoviceGuide(entity.isNoviceGuide());

                        videoEntity.setvLivePlayBackType(LocalCourseConfig.LIVE_PLAY_LIVE);
                        videoEntity.setPlayVideoName(sectionEntity.getvSectionName());
//            videoEntity.setVideoCacheKey(reslut.getAutoLive().getNowTime().toString());
                        videoEntity.setLiveId(entity.getLiveId());
                        videoEntity.setStuCourseId(DispatcherConfig.stuId);

                        videoEntity.setVisitTimeKey(Long.toString(entity.getAutoLive().getNowTime() - entity
                                .getAutoLive().getStartTime()));
                        // 互动题数据
//            videoEntity.setPlayVideoId(sectionEntity.getvSectionID());
                        videoEntity.setLstVideoQuestion(entity.getEvent());
                        videoEntity.setVideoPath(entity.getVideoPath());
                        videoEntity.setVideoPaths(entity.getVideoPaths());

                        videoEntity.setExpChatId(entity.getExpChatId());

                        videoEntity.setLearnFeedback(entity.getLearnFeedback());
                        videoEntity.setPaidBannerInfoUrl(entity.getPaidBannerInfoUrl());
                        videoEntity.setRecommendClassUrl(entity.getRecommendClassUrl());
                        videoEntity.setSubmitUnderStandUrl(entity.getSubmitUnderStandUrl());
                        videoEntity.setTeacherId(entity.getLiveInfo().getTeacherId());
                        videoEntity.setClassId(entity.getClassId());

                        videoEntity.setRoomChatCfgServerList(entity.getRoomChatCfgServerList());
                        videoEntity.setSciAiEvent(entity.getSciAiEvent());

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("videoliveplayback", videoEntity);
                        bundle.putInt("isArts", entity.getIsArts());
                        bundle.putStringArrayList("roomChatCfgServerList", entity.getRoomChatCfgServerList());
                        bundle.putString("expChatId", entity.getExpChatId());
                        bundle.putString("sex", entity.getSex());
                        bundle.putInt("pattern", videoEntity.getPattern());
                        bundle.putBoolean("isExperience", true);

                        if (!"".equals(entity.getExamPaperUrl())) {
                            if (entity.getIsArts() == 1) {
                                mShareDataManager.put(ShareBusinessConfig.SP_LIVE_EXAM_URL_LIBARTS, entity
                                        .getExamPaperUrl(), ShareDataManager.SHAREDATA_USER);
                            } else {
                                mShareDataManager.put(ShareBusinessConfig.SP_LIVE_EXAM_URL_SCIENCE, entity
                                        .getExamPaperUrl(), ShareDataManager.SHAREDATA_USER);
                            }
                        }
                        if (!"".equals(entity.getSpeechEvalUrl())) {
                            mShareDataManager.put(ShareBusinessConfig.SP_SPEECH_URL, entity.getSpeechEvalUrl(),
                                    ShareDataManager.SHAREDATA_USER);
                        }

                        if (videoEntity.getPattern() == LiveVideoConfig.LIVE_PATTERN_COMMON) {//三分屏体验课
                            if (videoEntity.getExpLiveType() == 2) { // 录直播体验课
                                ExpLiveInfo expLiveInfo = DispatcherHttpResponseParser.parserExliveInfo(responseEntity);
                                if (expLiveInfo != null) {
                                    videoEntity.setTutorTeacherId(expLiveInfo.getCoachTeacherId() + "");
                                    expLiveInfo.setLiveType(entity.getLiveType());
                                    bundle.putSerializable("expLiveInfo", expLiveInfo);
                                }

                                long startTime = entity.getAutoLive().getStartTime();
                                long endTime = entity.getAutoLive().getEndTime();
                                long nowTime = entity.getAutoLive().getNowTime();
                                String gradId = entity.getAutoLive().getGradId();
                                String termId = entity.getAutoLive().getTermId();

                                ExpAutoLive expAutoLive = new ExpAutoLive(startTime, endTime, nowTime, gradId, termId);
                                bundle.putSerializable("expAutoLive", expAutoLive);

                                bundle.putSerializable("entity", entity.getAutoLive());
                                LiveVideoEnter.intentToLiveBackExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            } else if (entity.isExpSciAi()) {
                                LiveVideoEnter.intentToAIExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            } else {
                                LiveVideoEnter.intentToExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            }

                        } else if (videoEntity.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {//全身直播体验课
                            if (videoEntity.getExpLiveType() == 2) { // 录直播体验课
                                ExpLiveInfo expLiveInfo = DispatcherHttpResponseParser.parserExliveInfo(responseEntity);
                                if (expLiveInfo != null) {
                                    videoEntity.setTutorTeacherId(expLiveInfo.getCoachTeacherId() + "");
                                    expLiveInfo.setLiveType(entity.getLiveType());
                                    bundle.putSerializable("expLiveInfo", expLiveInfo);
                                }

                                long startTime = entity.getAutoLive().getStartTime();
                                long endTime = entity.getAutoLive().getEndTime();
                                long nowTime = entity.getAutoLive().getNowTime();
                                String gradId = entity.getAutoLive().getGradId();
                                String termId = entity.getAutoLive().getTermId();

                                ExpAutoLive expAutoLive = new ExpAutoLive(startTime, endTime, nowTime, gradId, termId);
                                bundle.putSerializable("expAutoLive", expAutoLive);

                                bundle.putSerializable("entity", entity.getAutoLive());
                                LiveVideoEnter.intentToLiveBackExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            } else {
                                LiveVideoEnter.intentToStandExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            }
                        } else if (videoEntity.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY) {//半身直播体验课
                            if (videoEntity.getExpLiveType() == 2) { // 录直播体验课
                                ExpLiveInfo expLiveInfo = DispatcherHttpResponseParser.parserExliveInfo(responseEntity);
                                if (expLiveInfo != null) {
                                    videoEntity.setTutorTeacherId(expLiveInfo.getCoachTeacherId() + "");
                                    expLiveInfo.setLiveType(entity.getLiveType());
                                    bundle.putSerializable("expLiveInfo", expLiveInfo);
                                }

                                long startTime = entity.getAutoLive().getStartTime();
                                long endTime = entity.getAutoLive().getEndTime();
                                long nowTime = entity.getAutoLive().getNowTime();
                                String gradId = entity.getAutoLive().getGradId();
                                String termId = entity.getAutoLive().getTermId();

                                ExpAutoLive expAutoLive = new ExpAutoLive(startTime, endTime, nowTime, gradId, termId);
                                bundle.putSerializable("expAutoLive", expAutoLive);

                                bundle.putSerializable("entity", entity.getAutoLive());
                                LiveVideoEnter.intentToLiveBackExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            } else {
                                LiveVideoEnter.intentToHalfBodyExperience((Activity) mContext, bundle,
                                        mContext.getClass().getSimpleName());
                            }
                        }

                    }

                    @Override
                    public void onPmFailure(Throwable error, final String msg) {
                        logger.e(error.toString() + " " + msg);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                Map<String, String> map = new HashMap<>();
                                String eventId = LogerTag.DEBUG_EXPERIENCE_GETINFO;
                                map.put("logtype", "responseCode");
                                map.put("os", "Android");
                                map.put("liveid", liveId);
                                map.put("orderid", termId);
                                map.put("loglevel", "Error");
                                String errMsg = "";
                                if (!TextUtils.isEmpty(msg)) {
                                    errMsg = msg;
                                }
                                map.put("ip", IpAddressUtil.USER_IP);
                                //拿到serverip的ip地址
//                            String hostIP = ;
                                map.put("serverip", getHost(AppConfig.HTTP_HOST));
                                map.put("errmsg", errMsg);
                                map.put("getInfoUrl", ShareBusinessConfig.URL_EXPERIENCE_LIVE_INFO);
                                map.put("errmsg", "直播未开始");
                                UmsAgentManager.umsAgentDebug(mContext, eventId, map);
                            }
                        }.start();

                    }

                    @Override
                    public void onPmError(final ResponseEntity responseEntity) {
                        //上传日志 ，wiki地址：https://wiki.xesv5.com/pages/viewpage.action?pageId=13842928

                        logger.e(responseEntity.toString());
                        new Thread() {
                            @Override
                            public void run() {
//                                super.run();
                                Map<String, String> map = new HashMap<>();
                                String eventId = LogerTag.DEBUG_EXPERIENCE_GETINFO;
//                        map.put("userid", UserBll.getInstance().getMyUserInfoEntity().getStuId());
                                if (responseEntity.isJsonError()) {
                                    map.put("logtype", "jsonerror");
                                } else {
                                    map.put("logtype", "businesserror");
                                }
                                map.put("loglevel", "Error");
                                map.put("os", "Android");
                                map.put("liveid", liveId);
                                String errMsg = "";
                                if (!TextUtils.isEmpty(responseEntity.getErrorMsg())) {
                                    errMsg = responseEntity.getErrorMsg();
                                }
                                map.put("ip", IpAddressUtil.USER_IP);
                                //拿到serverip的ip地址
//                                    String hostIP = getHost(AppConfig.HTTP_HOST);
//                                    InetAddress serverAddress = InetAddress.getByName(hostIP);
                                map.put("serverip", getHost(AppConfig.HTTP_HOST));

                                map.put("errmsg", errMsg);
                                map.put("orderid", termId);
                                map.put("getInfoUrl", ShareBusinessConfig.URL_EXPERIENCE_LIVE_INFO);
                                map.put("errmsg", "直播未开始");

                                UmsAgentManager.umsAgentDebug(mContext, eventId, map);
                            }
                        }.start();
                    }

                    /**
                     * 根据url获取服务器的ip地址
                     *
                     * @param ip
                     * @return
                     */
                    private String getHost(String ip) {
                        try {
                            int len = ip.length();
                            int pos = ip.indexOf("//");
                            int i = pos + 2;
                            while (i < len) {
                                if (ip.charAt(i) == '/') {
                                    break;
                                }
                                i++;
                            }
                            String url = ip.substring(pos <= 0 ? 0 : pos + 2, i);
                            return InetAddress.getByName(url).getHostAddress();
                        } catch (Exception e) {
                            return "";
                        }
                    }
                });
    }

    public void getPublic(final String courseName, final String courseId, final String teacherId,
                          final String gotoClassTime,
                          final AbstractBusinessDataCallBack callBack,DataLoadEntity dataLoadEntity) {
        if (dataLoadEntity == null) {
            dataLoadEntity = new DataLoadEntity(mContext);
        }
        postDataLoadEvent(dataLoadEntity.beginLoading());
        dispatcherHttpManager.publicLiveCourseQuestion(courseId, teacherId, gotoClassTime,
                new HttpCallBack(dataLoadEntity) {
            public void onPmSuccess(ResponseEntity responseEntity) {
                PublicEntity publicLiveCourseEntity =
                        dispatcherHttpResponseParser.publicLiveCourseQuestionParser(responseEntity);
                if (publicLiveCourseEntity != null) {
                    publicLiveCourseEntity.setCourseId(courseId);
                    publicLiveCourseEntity.setCourseName(courseName);
                    publicLiveCourseEntity.setTeacherId(teacherId);
                    if (!TextUtils.isEmpty(gotoClassTime) && TextUtils.isDigitsOnly(gotoClassTime)) {
                        publicLiveCourseEntity.setGotoClassTime(Long.parseLong(gotoClassTime));
                    }
                }
                callBack.onDataSucess(publicLiveCourseEntity);
            }
        });
    }





    /**
     * 大班整合讲座-回放入口
     *
     * @param planId
     * @param bizeId
     * @param stuCouId
     */
    public void getBigLivePublic(String planId, String bizeId, String
            stuCouId, final AbstractBusinessDataCallBack callBack,DataLoadEntity dataLoadEntity) {
        if(dataLoadEntity ==null) {
            dataLoadEntity = new DataLoadEntity(mContext);
        }
        postDataLoadEvent(dataLoadEntity.beginLoading());

        int iPlanId = Integer.parseInt(planId);
        int iBizeId = Integer.parseInt(bizeId);
        int iStuCouId = Integer.parseInt(stuCouId);

        dispatcherHttpManager.publicBigLivePlayBackEnter(iPlanId, iBizeId, iStuCouId,
                new HttpCallBack(dataLoadEntity) {
            public void onPmSuccess(ResponseEntity responseEntity) {

                BigLivePlayBackEntity bigLivePlayBackEntity = dispatcherHttpResponseParser
                        .praseBigLiveEnterPlayBack(responseEntity);

                if(bigLivePlayBackEntity != null){
                    callBack.onDataSucess(bigLivePlayBackEntity);
                }else{
                    callBack.onDataFail(0,"数据解析失败");
                }

            }
        });
    }

    /**
     * 直播灰度场次
     * @param liveId
     * @param callBack
     */
    public void publicLiveIsGrayLecture(final String liveId , final boolean isLive,
                                        final AbstractBusinessDataCallBack callBack,final DataLoadEntity   dataLoadEntity) {
            postDataLoadEvent(dataLoadEntity.beginLoading());
        //请求查询数据
        dispatcherHttpManager.publicLiveIsGrayLecture( liveId,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        PublicLiveGrayEntity entity = new PublicLiveGrayEntity();
                        int status =  dispatcherHttpResponseParser.parserPublicResult(responseEntity);
                        entity.setStatus(status);
                        entity.setLive(isLive);
                        callBack.onDataSucess(entity);
                        if(isLive) {
                            EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(dataLoadEntity.webDataSuccess()));
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        callBack.onDataFail(-1,msg);
                            EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(dataLoadEntity.webDataSuccess()));
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        callBack.onDataFail(-1,responseEntity.getErrorMsg());
                            EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(dataLoadEntity.webDataSuccess()));
                    }
                });

    }
}
