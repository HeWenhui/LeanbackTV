package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ScienceStaticConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.CoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;

/**
 * Created by linyuqiang on 2018/7/26.
 * 直播回放的英语课件创建
 */
public class LiveBackBaseEnglishH5CoursewareCreat implements BaseEnglishH5CoursewareCreat {
    private WrapOnH5ResultClose wrapOnH5ResultClose;
    LivePagerBack livePagerBack;
    private LiveGetInfo liveGetInfo;
    private int isArts;

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

    @Override
    public BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID) {
        wrapOnH5ResultClose.setOnH5ResultClose(onH5ResultClose);
        wrapOnH5ResultClose.setVideoQuestionH5Entity(videoQuestionH5Entity);
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        if (isArts == 0) {
            String educationstage = liveGetInfo.getEducationStage();
            if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
                englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlZhongXueUrl());
//                if (englishH5Entity.getNewEnglishH5()) {
//                    CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                            videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
//                    h5CoursewarePager.setLivePagerBack(livePagerBack);
//                    return h5CoursewarePager;
//                }
            } else {
                // TODO 理科小学
//                englishH5Entity.setDynamicurl(liveGetInfo.getGetCourseWareHtmlNew() + ScienceStaticConfig.THIS_VERSION_HTML + "/index.html");
                englishH5Entity.setDynamicurl("https://live.xueersi.com/science/LiveExam/getCourseWareTestHtml");
//                if (englishH5Entity.getNewEnglishH5()) {
//                    CoursewareNativePager h5CoursewarePager = new CoursewareNativePager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
//                            videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, "0", isArts, false);
//                    h5CoursewarePager.setLivePagerBack(livePagerBack);
//                    return h5CoursewarePager;
//                }
            }
        } else if (isArts == 2) {
            englishH5Entity.setDynamicurl("https://live.chs.xueersi.com/LiveExam/getCourseWareTestHtml");
        }
        EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, videoQuestionH5Entity, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", isArts, false);
        h5CoursewarePager.setLivePagerBack(livePagerBack);
        return h5CoursewarePager;
    }
}
