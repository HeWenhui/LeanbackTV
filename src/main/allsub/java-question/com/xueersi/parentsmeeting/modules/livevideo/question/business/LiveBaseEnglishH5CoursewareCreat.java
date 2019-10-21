package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.GetStuActiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager.GroupGameEmptyPager;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager.GroupGameNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ScienceStaticConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ChineseAiSubjectiveCoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.CoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeakChineseCoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VPlayerListenerReg;

import java.util.List;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionContract.INTELLIGENT_RECOGNITION_STOP_ONCE;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionContract.PROCESS_RECORD_SIGN;

/**
 * Created by linyuqiang on 2018/7/26.
 */
public class LiveBaseEnglishH5CoursewareCreat implements BaseEnglishH5CoursewareCreat {
    private String TAG = "LiveBaseEnglishH5CoursewareCreat";
    private AnswerRankIRCBll mAnswerRankIRCBll;
    private int mIsArts;
    private boolean allowTeamPk;
    private LiveGetInfo liveGetInfo;
    private Logger logger;
    private LivePagerBack livePagerBack;
    private boolean isFirst = true;
    private LogToFile mLogtf;

    public LiveBaseEnglishH5CoursewareCreat(Context context) {
        logger = LiveLoggerFactory.getLogger(TAG);
        mLogtf = new LogToFile(context, TAG);
    }

    public void setmAnswerRankBll(AnswerRankIRCBll mAnswerRankBll) {
        this.mAnswerRankIRCBll = mAnswerRankBll;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
        mIsArts = liveGetInfo.getIsArts();
    }

    public void setAllowTeamPk(boolean allowTeamPk) {
        this.allowTeamPk = allowTeamPk;
    }

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

    @Override
    public BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID) {
        String isShowRanks = "0";
        if (mAnswerRankIRCBll != null) {
            AnswerRankBll mAnswerRankBll = mAnswerRankIRCBll.getAnswerRankBll();
            if (mAnswerRankBll != null) {
                isShowRanks = mAnswerRankBll.getIsShow();
            }
        }
        int isArts = mIsArts;
        if (videoQuestionH5Entity.isTUtor()) {
            isArts = LiveVideoSAConfig.ART_SEC;
        }
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        //应该是没有为null的时候
        if (liveGetInfo != null) {
            mLogtf.d("creat:isArts=" + isArts + ",isNewCourse=" + liveGetInfo.isNewCourse() + ",mode=" + liveGetInfo.getMode() + ",ne=" + englishH5Entity.getNewEnglishH5()
                    + ",ane=" + englishH5Entity.isArtsNewH5Courseware());
            if (isArts == LiveVideoSAConfig.ART_CH && LiveTopic.MODE_CLASS.equals(liveGetInfo.getMode())) {
                String educationstage = liveGetInfo.getEducationStage();
                videoQuestionH5Entity.setEducationstage(educationstage);
                //语文
                if (englishH5Entity.getNewEnglishH5()) {
                    if (LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(videoQuestionH5Entity.englishH5Entity.getPackageAttr())) {
                        ChineseAiSubjectiveCoursewarePager h5CoursewarePager = new ChineseAiSubjectiveCoursewarePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk, liveGetInfo.getSubjectiveItem2AIUrl());
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                }
            } else if (isArts == LiveVideoSAConfig.ART_SEC) {
                if (englishH5Entity.getNewEnglishH5()) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    englishH5Entity.setDynamicurl(LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/getCourseWareTestHtml");
                    if (LiveQueConfig.CHI_COURESWARE_TYPE_SPEAKING_CHINESE.equals(videoQuestionH5Entity.englishH5Entity.getPackageAttr())
                            && LiveVideoConfig.LIVE_TYPE_HALFBODY == liveGetInfo.getPattern()) {
                        SpeakChineseCoursewarePager h5CoursewarePager = new SpeakChineseCoursewarePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk);
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                }
            }
            if (liveGetInfo.isNewCourse() || LiveTopic.MODE_TRANING.equals(liveGetInfo.getMode())) {
                if (isArts == LiveVideoSAConfig.ART_SEC) {
                    String educationstage = liveGetInfo.getEducationStage();
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
                        englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                        if (englishH5Entity.getNewEnglishH5()) {
                            CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk);
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
                        englishH5Entity.setDynamicurl(LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/getCourseWareTestHtml");
                        if (englishH5Entity.getNewEnglishH5()) {
                            CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk);
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
                            ChineseAiSubjectiveCoursewarePager h5CoursewarePager = new ChineseAiSubjectiveCoursewarePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk, liveGetInfo.getSubjectiveItem2AIUrl());
                            h5CoursewarePager.setLivePagerBack(livePagerBack);
                            return h5CoursewarePager;
                        }
                        CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk);
                        h5CoursewarePager.setLivePagerBack(livePagerBack);
                        return h5CoursewarePager;
                    }
                } else if (isArts == LiveVideoSAConfig.ART_EN) {
                    // 英语
                    videoQuestionH5Entity.setEducationstage(liveGetInfo.getEducationStage());
                    englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
                    if (videoQuestionH5Entity.isNewArtsH5Courseware() && !LiveQueConfig.EN_COURSE_TYPE_NEW_GAME.equals(videoQuestionH5Entity.type)) {
                        long before = System.currentTimeMillis();
                        BaseEnglishH5CoursewarePager h5CoursewarePager;
                        String type = videoQuestionH5Entity.type;
                        if (LiveQueConfig.isGroupGame(type)) {
                            h5CoursewarePager = createGame(context, videoQuestionH5Entity, onH5ResultClose);
                        } else if (LiveQueConfig.EN_INTELLIGENT_EVALUTION.equals(type)) {
                            gotoIntelligentEvaluation(context, videoQuestionH5Entity);
                            return null;
                        } else {
                            CoursewareNativePager coursewareNativePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                    videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
                            coursewareNativePager.setLivePagerBack(livePagerBack);
                            h5CoursewarePager = coursewareNativePager;
                        }
//                        CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
                        logger.d("CoursewareNativePagerCreat:time=" + (System.currentTimeMillis() - before));
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
                        BaseEnglishH5CoursewarePager h5CoursewarePager = createGame(context, videoQuestionH5Entity, onH5ResultClose);
                        return h5CoursewarePager;
                    } else if (LiveQueConfig.EN_INTELLIGENT_EVALUTION.equals(type)) {
                        gotoIntelligentEvaluation(context, videoQuestionH5Entity);
                        return null;
                    } else if (TextUtils.equals(LiveQueConfig.EN_COURSE_TYPE_21, type)) {
                        CoursewareNativePager coursewareNativePager = new CoursewareNativePager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
                        coursewareNativePager.setLivePagerBack(livePagerBack);
                        return coursewareNativePager;
                    }
                }
            }
        } else {
            mLogtf.d("creat:isArts=" + isArts + ",isNewCourse=null");
        }
        EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, isShowRanks, isArts, allowTeamPk);
        h5CoursewarePager.setLivePagerBack(livePagerBack);
        return h5CoursewarePager;
    }

    /**
     * 跳转到英语智能测评
     *
     * @param context
     * @param videoQuestionH5Entity
     */
    private void gotoIntelligentEvaluation(Context context, VideoQuestionLiveEntity videoQuestionH5Entity) {
        handleLauchIntelligentEvaluation(context, videoQuestionH5Entity);
    }

    private void handleLauchIntelligentEvaluation(Context context, VideoQuestionLiveEntity videoQuestionH5Entity) {
//        stopIntelligentOnce(context);
        if (handleStopIntelligent(context)) {
            scheduleLauchIntelligentEvaluation(context, videoQuestionH5Entity);
        } else {
            performLauchIntelligentEvaluation(context, videoQuestionH5Entity);
        }
    }


    private boolean handleStopIntelligent(Context context) {
        if (judgeIntelligentAlive(context)) {
            stopIntelligentOnce(context);
            return true;
        }
        return false;
    }

    private boolean judgeIntelligentAlive(Context context) {
        List<ActivityManager.RunningTaskInfo> list = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().
                    contains("com.xueersi.parentsmeeting.modules.aievaluation.intelligent_recognition.widget.IntelligentRecognitionActivity")) { // 说明它已经启动了
//                flag = true;
                return true;
            }
        }
        return false;
//        Intent intent = new Intent();//(ActivityManager) context.getSystemService(ACTIVITY_SERVICE)
//        intent.setClassName("com.xueersi.parentsmeeting.modules.aievaluation.intelligent_recognition.widget",
//                "IntelligentRecognitionActivity");
//        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * 延迟一段时间，给stop Activity一点时间
     *
     * @param context
     * @param videoQuestionH5Entity
     */
    private void scheduleLauchIntelligentEvaluation(final Context context, final VideoQuestionLiveEntity videoQuestionH5Entity) {
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                performLauchIntelligentEvaluation(context, videoQuestionH5Entity);
            }
        }, 1000);
    }

    private void performLauchIntelligentEvaluation(Context context, VideoQuestionLiveEntity videoQuestionH5Entity) {
        //英语智能测评
        Bundle bundle = new Bundle();
        IntelligentRecognitionRecord intelligentRecognitionRecord = new IntelligentRecognitionRecord();
        intelligentRecognitionRecord.setAnswerTime(videoQuestionH5Entity.time + "");
        intelligentRecognitionRecord.setStuId(liveGetInfo.getStuId());
        intelligentRecognitionRecord.setStuCouId(liveGetInfo.getStuCouId());
        intelligentRecognitionRecord.setLiveId(liveGetInfo.getId());
        intelligentRecognitionRecord.setContent(videoQuestionH5Entity.answer);
//                        JSONArray jsonArray = data.optJSONArray("id");
//                        if (jsonArray != null && jsonArray.length() > 0) {
        intelligentRecognitionRecord.setMaterialId(videoQuestionH5Entity.id);
        logger.i("videoQuestionH5Entity.id:" + videoQuestionH5Entity.toString());
//                        }
        intelligentRecognitionRecord.setIsPlayBack("0");
        if (liveGetInfo.getStudentLiveInfo() != null) {
            intelligentRecognitionRecord.setClassId(liveGetInfo.getStudentLiveInfo().getClassId());
            intelligentRecognitionRecord.setTeamId(liveGetInfo.getStudentLiveInfo().getTeamId());
        }
        bundle.putParcelable(PROCESS_RECORD_SIGN, intelligentRecognitionRecord);
        VPlayerListenerReg reg = ProxUtil.getProxUtil().get(context, VPlayerListenerReg.class);
        if (reg != null) {
            logger.i("停止播放");
            reg.release();
        }
        XueErSiRouter.startModuleForResult((Activity) context, "/aievaluation/intelligent_recognition", XESCODE.ARTS_SEND_QUESTION, bundle);
    }

    private void stopIntelligentOnce(Context context) {
        logger.i("当前Actiivty在线，发送停止广播");
        Intent stopIntent = new Intent(INTELLIGENT_RECOGNITION_STOP_ONCE);
        stopIntent.setAction(INTELLIGENT_RECOGNITION_STOP_ONCE);
        context.sendBroadcast(stopIntent);
    }

    /**
     * 小组互动
     *
     * @param context
     * @param videoQuestionH5Entity
     * @param onH5ResultClose
     * @return
     */
    private BaseEnglishH5CoursewarePager createGame(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose) {
        String type = videoQuestionH5Entity.type;
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        if (LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON.equals(type)) {
            GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, false, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
            groupGameMultNativePager.setLivePagerBack(livePagerBack);
            return groupGameMultNativePager;
        }
        BaseEnglishH5CoursewarePager h5CoursewarePager;
        GetStuActiveTeam getStuActiveTeam = ProxUtil.getProxUtil().get(context, GetStuActiveTeam.class);
        //还没有战队
        if (getStuActiveTeam == null) {
            GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, false, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
            groupGameMultNativePager.setLivePagerBack(livePagerBack);
            h5CoursewarePager = groupGameMultNativePager;
        } else {
            InteractiveTeam interactiveTeam = getStuActiveTeam.getStuActiveTeam(false, null);
            //还没有小组,或者没有tcp
//            if (interactiveTeam == null || interactiveTeam.getEntities().size() < 2) {
//                if (interactiveTeam == null) {
//                    GroupGameEmptyPager groupGameEmptyPager = new GroupGameEmptyPager(context, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
//                    groupGameEmptyPager.setLivePagerBack(livePagerBack);
//                    h5CoursewarePager = groupGameEmptyPager;
////                    GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, false, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
////                    groupGameMultNativePager.setLivePagerBack(livePagerBack);
////                    h5CoursewarePager = groupGameMultNativePager;
//                } else {
//                    GroupGameEmptyPager groupGameEmptyPager = new GroupGameEmptyPager(context, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
//                    groupGameEmptyPager.setLivePagerBack(livePagerBack);
//                    h5CoursewarePager = groupGameEmptyPager;
////                    GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, false, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
////                    groupGameMultNativePager.setLivePagerBack(livePagerBack);
////                    h5CoursewarePager = groupGameMultNativePager;
//                }
//            } else {
//                GroupGameEmptyPager groupGameEmptyPager = new GroupGameEmptyPager(context, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
//                groupGameEmptyPager.setLivePagerBack(livePagerBack);
//                h5CoursewarePager = groupGameEmptyPager;
////                GroupGameMultNativePager groupGameMultNativePager = new GroupGameMultNativePager(context, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
////                groupGameMultNativePager.setLivePagerBack(livePagerBack);
////                h5CoursewarePager = groupGameMultNativePager;
//            }
            if (!isFirst && (interactiveTeam == null || interactiveTeam.getEntities().size() < 2)) {
                GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(context, false, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
                groupGameMultNativePager.setLivePagerBack(livePagerBack);
                h5CoursewarePager = groupGameMultNativePager;
            } else {
                GroupGameEmptyPager groupGameEmptyPager = new GroupGameEmptyPager(context, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose, isFirst);
                groupGameEmptyPager.setLivePagerBack(livePagerBack);
                h5CoursewarePager = groupGameEmptyPager;
                isFirst = false;
//                GroupGameMultNativePager groupGameMultNativePager = new GroupGameMultNativePager(context, liveGetInfo, videoQuestionH5Entity, englishH5Entity, onH5ResultClose);
//                groupGameMultNativePager.setLivePagerBack(livePagerBack);
//                h5CoursewarePager = groupGameMultNativePager;
            }
        }
        return h5CoursewarePager;
    }
}
