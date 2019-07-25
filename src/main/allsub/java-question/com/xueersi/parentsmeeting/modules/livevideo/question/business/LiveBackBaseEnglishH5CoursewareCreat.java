package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.text.TextUtils;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager.GroupGameNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ScienceStaticConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ChineseAiSubjectiveCoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.CoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeakChineseCoursewarePager;

/**
 * Created by linyuqiang on 2018/7/26.
 * 直播回放的英语课件创建
 */
public class LiveBackBaseEnglishH5CoursewareCreat implements BaseEnglishH5CoursewareCreat {
    private WrapOnH5ResultClose wrapOnH5ResultClose;
    LivePagerBack livePagerBack;
    private LiveGetInfo liveGetInfo;
    private int isArts;

    private boolean isFirst = true;

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setArts(int arts) {
        this.isArts = arts;
    }

    public void setWrapOnH5ResultClose(WrapOnH5ResultClose wrapOnH5ResultClose) {
        this.wrapOnH5ResultClose = wrapOnH5ResultClose;
    }

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

//    @Override
//    public BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID) {
//        wrapOnH5ResultClose.setOnH5ResultClose(onH5ResultClose);
//        wrapOnH5ResultClose.setVideoQuestionH5Entity(videoQuestionH5Entity);
//        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
//
//        // 如果辅导态
//        if (videoQuestionH5Entity.isTUtor()) {
//            String tutorHttp=  UmsAgentTrayPreference.getInstance().getString(AppConfig.XES_LIVE_VIDEO_TUTOR_RESULT_HTML, "");
//            if(TextUtils.isEmpty(tutorHttp)){
//                tutorHttp = "https://live.xueersi.com/scistatic/outDoorTest/index.html";
//            }
//              englishH5Entity.setDynamicurl(tutorHttp);
//            EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", LiveVideoSAConfig.ART_SEC, false);
//            h5CoursewarePager.setLivePagerBack(livePagerBack);
//        return h5CoursewarePager;
//        }
//
//        if (isArts == 0) {
//            String educationstage = liveGetInfo.getEducationStage();
//            if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
//                englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
////                if (englishH5Entity.getNewEnglishH5()) {
////                    CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
////                            videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
////                    h5CoursewarePager.setLivePagerBack(livePagerBack);
////                    return h5CoursewarePager;
////                }
//            } else {
//                // TODO 理科小学
////                englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew() + ScienceStaticConfig.THIS_VERSION_HTML + "/index.html");
//                englishH5Entity.setDynamicurl("https://live.xueersi.com/science/LiveExam/getCourseWareTestHtml");
////                if (englishH5Entity.getNewEnglishH5()) {
////                    CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
////                            videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
////                    h5CoursewarePager.setLivePagerBack(livePagerBack);
////                    return h5CoursewarePager;
////                }
//                if (englishH5Entity.getNewEnglishH5()) {
//                    if (LiveQueConfig.CHI_COURESWARE_TYPE_SPEAKING_CHINESE.equals(videoQuestionH5Entity.englishH5Entity.getPackageAttr())
//                            && LiveVideoConfig.LIVE_TYPE_HALFBODY == liveGetInfo.getPattern()) {
//                        SpeakChineseCoursewarePager h5CoursewarePager = new SpeakChineseCoursewarePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", isArts, false);
//                        h5CoursewarePager.setLivePagerBack(livePagerBack);
//                        return h5CoursewarePager;
//                    }
//                }
//            }
//        } else if (isArts == LiveVideoSAConfig.ART_CH) {
//            englishH5Entity.setDynamicurl("https://live.chs.xueersi.com/LiveExam/getCourseWareTestHtml");
//            if( LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(englishH5Entity.getPackageAttr())){
//                englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew());
//                videoQuestionH5Entity.setSubjectiveItem2AIUrl(liveGetInfo.getSubjectiveItem2AIUrl());
//            }
//            String educationstage = liveGetInfo.getEducationStage();
//            videoQuestionH5Entity.setEducationstage(educationstage);
//        } else if (isArts == LiveVideoSAConfig.ART_EN){
//            String type = videoQuestionH5Entity.type;
//            if (LiveQueConfig.isGroupGame(type)) {
//                GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, true, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
//                groupGameMultNativePager.setLivePagerBack(livePagerBack);
//                return groupGameMultNativePager;
//            }
//        }
//
//        EnglishH5CoursewareX5Pager    h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", isArts, false);
//
//        h5CoursewarePager.setLivePagerBack(livePagerBack);
//        return h5CoursewarePager;
//    }

    @Override
    public BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID) {

        wrapOnH5ResultClose.setOnH5ResultClose(onH5ResultClose);
        wrapOnH5ResultClose.setVideoQuestionH5Entity(videoQuestionH5Entity);
        if (videoQuestionH5Entity.isTUtor()) {
            setArts(LiveVideoSAConfig.ART_SEC);
        }
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        //应该是没有为null的时候
        if (liveGetInfo != null) {
            if (isArts == LiveVideoSAConfig.ART_CH) {
//                String educationstage = liveGetInfo.getEducationStage();
//                videoQuestionH5Entity.setEducationstage(educationstage);
//                //语文
//                if (englishH5Entity.getNewEnglishH5()) {
//                    if (LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(videoQuestionH5Entity.englishH5Entity.getPackageAttr())) {
//                        ChineseAiSubjectiveCoursewarePager h5CoursewarePager = new ChineseAiSubjectiveCoursewarePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0"
//                                , isArts, false, liveGetInfo.getSubjectiveItem2AIUrl());
//                        h5CoursewarePager.setLivePagerBack(livePagerBack);
//                        return h5CoursewarePager;
//                    }
//                }
            } else if (isArts == LiveVideoSAConfig.ART_SEC) {
                if (englishH5Entity.getNewEnglishH5()) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    englishH5Entity.setDynamicurl(LiveHttpConfig.LIVE_HOST + "/science/LiveExam/getCourseWareTestHtml");
                    if (LiveQueConfig.CHI_COURESWARE_TYPE_SPEAKING_CHINESE.equals(videoQuestionH5Entity.englishH5Entity.getPackageAttr())
                            && LiveVideoConfig.LIVE_TYPE_HALFBODY == liveGetInfo.getPattern()) {
                        SpeakChineseCoursewarePager h5CoursewarePager = new SpeakChineseCoursewarePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0"
                                , isArts, false);
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                }
            }
            if (liveGetInfo.isNewCourse()) {
                if (isArts == LiveVideoSAConfig.ART_SEC) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                        if (englishH5Entity.getNewEnglishH5()) {
                            CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", isArts, false);
                            h5CoursewarePager.setLivePagerBack(livePagerBack);
                            return h5CoursewarePager;
                        }
                    } else {
                        // TODO 理科小学
//                    ScienceStaticConfig scienceStaticConfig = liveGetInfo.getScienceStaticConfig();
//                    String localfile = null;
//                    if (scienceStaticConfig != null) {
//                        ScienceStaticConfig.Version version = scienceStaticConfig.stringVersionHashMap.get(ScienceStaticConfig.THIS_VERSION);
//                        if (version != null) {
//                            localfile = version.localfile;
//                        }
//                    }
//                    if (localfile != null) {
//                        localfile = "file://" + localfile;
//                        englishH5Entity.setDynamicurl(localfile);
//                    } else {
//                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew() + ScienceStaticConfig.THIS_VERSION_HTML + "/index.html");
//                    }
                        englishH5Entity.setDynamicurl(LiveHttpConfig.LIVE_HOST + "/science/LiveExam/getCourseWareTestHtml");
                        if (englishH5Entity.getNewEnglishH5()) {
                            CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0"
                                    , isArts, false);
                            h5CoursewarePager.setLivePagerBack(livePagerBack);
                            return h5CoursewarePager;
                        }
                    }
                    //语文
                } else if (isArts == LiveVideoSAConfig.ART_CH) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(educationstage);
                    //语文
                    if (englishH5Entity.getNewEnglishH5()) {
                        if (LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(videoQuestionH5Entity.englishH5Entity.getPackageAttr())) {
                            ChineseAiSubjectiveCoursewarePager h5CoursewarePager = new ChineseAiSubjectiveCoursewarePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0"
                                    , isArts, false, liveGetInfo.getSubjectiveItem2AIUrl());
                            h5CoursewarePager.setLivePagerBack(livePagerBack);
                            return h5CoursewarePager;
                        }
                        CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0"
                                , isArts, false);
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                } else if (isArts == LiveVideoSAConfig.ART_EN) {
                    // 英语
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                    if (videoQuestionH5Entity.isNewArtsH5Courseware() && !LiveQueConfig.EN_COURSE_TYPE_NEW_GAME.equals(videoQuestionH5Entity.type)) {
                        long before = System.currentTimeMillis();
                        BaseEnglishH5CoursewarePager h5CoursewarePager = null;
                        String type = videoQuestionH5Entity.type;
                        if (LiveQueConfig.isGroupGame(type)) {
                            GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, true, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
                            groupGameMultNativePager.setLivePagerBack(livePagerBack);
                            return groupGameMultNativePager;

                        } else {
                            CoursewareNativePager coursewareNativePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", isArts, false);
                            coursewareNativePager.setLivePagerBack(livePagerBack);
                            h5CoursewarePager = coursewareNativePager;
                        }
//                        CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
                        // logger.d("CoursewareNativePagerCreat:time=" + (System.currentTimeMillis() - before));
                        return h5CoursewarePager;
                    }
                }
            } else {
                if (isArts == LiveVideoSAConfig.ART_SEC) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                    } else {
                        // TODO 理科小学
//                    ScienceStaticConfig scienceStaticConfig = liveGetInfo.getScienceStaticConfig();
//                    String localfile = null;
//                    if (scienceStaticConfig != null) {
//                        ScienceStaticConfig.Version version = scienceStaticConfig.stringVersionHashMap.get(ScienceStaticConfig.THIS_VERSION);
//                        if (version != null) {
//                            localfile = version.localfile;
//                        }
//                    }
//                    if (localfile != null) {
//                        localfile = "file://" + localfile;
//                        englishH5Entity.setDynamicurl(localfile);
//                    } else {
//                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew() + ScienceStaticConfig.THIS_VERSION_HTML + "/index.html");
//                    }
                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew() + ScienceStaticConfig.THIS_VERSION_HTML + "/index.html");
                    }
                } else if (isArts == LiveVideoSAConfig.ART_CH) {
                    englishH5Entity.setDynamicurl("https://live.chs.xueersi.com/LiveExam/getCourseWareTestHtml");
                    if (LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(englishH5Entity.getPackageAttr())) {
                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew());
                        videoQuestionH5Entity.setSubjectiveItem2AIUrl(liveGetInfo.getSubjectiveItem2AIUrl());
                    }
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(educationstage);
                } else if (isArts == LiveVideoSAConfig.ART_EN) {
                    String type = videoQuestionH5Entity.type;
                    if (LiveQueConfig.isGroupGame(type)) {
                        GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, true, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
                        groupGameMultNativePager.setLivePagerBack(livePagerBack);
                        return groupGameMultNativePager;

                    }
                }
            }
        }
        if (videoQuestionH5Entity.isTUtor()) {
            String tutorHttp = UmsAgentTrayPreference.getInstance().getString(AppConfig.XES_LIVE_VIDEO_TUTOR_RESULT_HTML, "");
            if (TextUtils.isEmpty(tutorHttp)) {
                tutorHttp = LiveQueHttpConfig.TUTOR_COURSE_URL;
            }
            englishH5Entity.setDynamicurl(tutorHttp);
            EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", LiveVideoSAConfig.ART_SEC, false);
            h5CoursewarePager.setLivePagerBack(livePagerBack);
            return h5CoursewarePager;
        }
        EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0"
                , isArts, false);
        h5CoursewarePager.setLivePagerBack(livePagerBack);
        return h5CoursewarePager;
    }
}
