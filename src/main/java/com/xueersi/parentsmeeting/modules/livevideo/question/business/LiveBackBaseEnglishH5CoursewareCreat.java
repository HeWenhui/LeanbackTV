package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;

/**
 * Created by linyuqiang on 2018/7/26.
 * 直播回放的英语课件创建
 */
public class LiveBackBaseEnglishH5CoursewareCreat implements BaseEnglishH5CoursewareCreat {
    private WrapOnH5ResultClose wrapOnH5ResultClose;
    LivePagerBack livePagerBack;
    private boolean IS_SCIENCE;

    public void setIS_SCIENCE(boolean IS_SCIENCE) {
        this.IS_SCIENCE = IS_SCIENCE;
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
        EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, true, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, wrapOnH5ResultClose, "0", IS_SCIENCE);
        h5CoursewarePager.setLivePagerBack(livePagerBack);
        return h5CoursewarePager;
    }
}
