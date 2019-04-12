package com.xueersi.parentsmeeting.modules.livevideoOldIJK.notice.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/**
 * Created by lyqai on 2018/7/5.
 */

public class LiveAutoNoticeIRCBll extends LiveBaseBll {

    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    private long blockTime;

    public LiveAutoNoticeIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
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
            mLiveAutoNoticeBll = liveAutoNoticeBll;
        }
    }

    public void onUnknown(String line) {
        if (mLiveAutoNoticeBll != null) {
            if (line.contains("BLOCK")) {//发送了敏感词
                if (System.currentTimeMillis() - blockTime > 30 * 60 * 1000) {
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
