package com.xueersi.parentsmeeting.modules.livevideo.notice;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by lyqai on 2018/7/5.
 */

public class LiveAutoNoticeIRCBll extends LiveBaseBll {

    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    private long blockTime;

    public LiveAutoNoticeIRCBll(Activity context, LiveBll2 liveBll, ViewGroup rootView) {
        super(context, liveBll, rootView);
        putInstance(LiveAutoNoticeIRCBll.class, this);
    }

    public LiveAutoNoticeBll getLiveAutoNoticeBll() {
        return mLiveAutoNoticeBll;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mGetInfo.getStudentLiveInfo() != null
                && "1".equals(mGetInfo.getIsShowCounselorWhisper())) {
            LiveAutoNoticeBll liveAutoNoticeBll = new LiveAutoNoticeBll(activity, mLiveBll, (RelativeLayout) mRootView);
            liveAutoNoticeBll.setGrade(mGetInfo.getGrade());
            liveAutoNoticeBll.setClassId(mGetInfo.getStudentLiveInfo().getClassId());
            liveAutoNoticeBll.setTeacherImg(mGetInfo.getTeacherIMG());
            liveAutoNoticeBll.setTeacherName(mGetInfo.getTeacherName());
            liveAutoNoticeBll.setLiveBll(mLiveBll);
            liveAutoNoticeBll.setHttpManager(mLiveBll.getHttpManager());
            liveAutoNoticeBll.setLiveId(mLiveId);
            //if (mQuestionAction instanceof QuestionBll) {
            QuestionIRCBll questionBll = ProxUtil.getProxUtil().get(activity, QuestionIRCBll.class);
            if (questionBll != null) {
                questionBll.setLiveAutoNoticeBll(liveAutoNoticeBll);
            }
            EnglishH5CoursewareBll englishH5CoursewareBll = ProxUtil.getProxUtil().get(activity, EnglishH5CoursewareBll.class);
            if (englishH5CoursewareBll != null) {
                englishH5CoursewareBll.setLiveAutoNoticeBll(liveAutoNoticeBll);
            }
            mLiveAutoNoticeBll = liveAutoNoticeBll;
        }
    }

    public void onUnknown(String line) {
        if (mLiveAutoNoticeBll != null) {
            if (line.contains("BLOCK")) {//发送了敏感词
                if (System.currentTimeMillis() - blockTime > 2 * 60 * 1000) {
                    blockTime = System.currentTimeMillis();
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            mLiveAutoNoticeBll.showNotice(mGetInfo.getTeacherName(), mGetInfo.getTeacherIMG());
                        }
                    }, 10000);
                }
            }
        }
    }

    public void showNotice(String teacherName, String teacherIMG) {
        if (mLiveAutoNoticeBll != null) {
            mLiveAutoNoticeBll.showNotice(teacherName, teacherIMG);
        }
    }

    public void setTestId(String testId) {
        if (mLiveAutoNoticeBll != null) {
            mLiveAutoNoticeBll.setTestId(testId);
        }
    }

    public void setSrcType(String srcType) {
        if (mLiveAutoNoticeBll != null) {
            mLiveAutoNoticeBll.setSrcType(srcType);
        }
    }
}
