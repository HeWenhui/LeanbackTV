package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ScienceStaticConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.CoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

/**
 * Created by linyuqiang on 2018/7/26.
 */
public class LiveBaseEnglishH5CoursewareCreat implements BaseEnglishH5CoursewareCreat {
    private AnswerRankIRCBll mAnswerRankIRCBll;
    private int isArts;
    private boolean allowTeamPk;
    private LiveGetInfo liveGetInfo;
    private Logger logger;

    LivePagerBack livePagerBack;

    public LiveBaseEnglishH5CoursewareCreat() {
        logger = LiveLoggerFactory.getLogger("LiveBaseEnglishH5CoursewareCreat");
    }

    public void setmAnswerRankBll(AnswerRankIRCBll mAnswerRankBll) {
        this.mAnswerRankIRCBll = mAnswerRankBll;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setArts(int isArts) {
        this.isArts = isArts;
    }

    public void setAllowTeamPk(boolean allowTeamPk) {
        this.allowTeamPk = allowTeamPk;
    }

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

    @Override
    public BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID) {
        AnswerRankBll mAnswerRankBll = null;
        if (mAnswerRankIRCBll != null) {
            mAnswerRankBll = mAnswerRankIRCBll.getAnswerRankBll();
        }
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        //应该是没有为null的时候
        if (liveGetInfo != null) {
            if (liveGetInfo.isNewCourse()) {
                if (isArts == LiveVideoSAConfig.ART_SEC) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                        if (englishH5Entity.getNewEnglishH5()) {
                            CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, mAnswerRankBll == null ? "0"
                                    : mAnswerRankBll.getIsShow(), isArts, allowTeamPk);
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
                        englishH5Entity.setDynamicurl("https://live.xueersi.com/science/LiveExam/getCourseWareTestHtml");
                        if (englishH5Entity.getNewEnglishH5()) {
                            CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, mAnswerRankBll == null ? "0"
                                    : mAnswerRankBll.getIsShow(), isArts, allowTeamPk);
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
                        CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, mAnswerRankBll == null ? "0"
                                : mAnswerRankBll.getIsShow(), isArts, allowTeamPk);
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                } else if (isArts == LiveVideoSAConfig.ART_EN) {
                    // 英语
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                    if (videoQuestionH5Entity.isNewArtsH5Courseware() && !LiveQueConfig.EN_COURSE_TYPE_NEW_GAME.equals(videoQuestionH5Entity.type)) {
                        long before = System.currentTimeMillis();
                        CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
                        logger.d("CoursewareNativePagerCreat:time=" + (System.currentTimeMillis() - before));
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                }
            } else {
                if (isArts == 0) {
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
                }
            }
        }
        EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, mAnswerRankBll == null ? "0"
                : mAnswerRankBll.getIsShow(), isArts, allowTeamPk);
        h5CoursewarePager.setLivePagerBack(livePagerBack);
        return h5CoursewarePager;
    }
}
