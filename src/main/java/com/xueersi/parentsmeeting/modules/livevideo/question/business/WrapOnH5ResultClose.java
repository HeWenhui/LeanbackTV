package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;

/**
 * Created by lyqai on 2018/7/26.
 */

public class WrapOnH5ResultClose implements EnglishH5CoursewareBll.OnH5ResultClose {
    EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose;
    VideoQuestionLiveEntity videoQuestionH5Entity;

    public void setOnH5ResultClose(EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose) {
        this.onH5ResultClose = onH5ResultClose;
    }

    public void setVideoQuestionH5Entity(VideoQuestionLiveEntity videoQuestionH5Entity) {
        this.videoQuestionH5Entity = videoQuestionH5Entity;
    }

    @Override
    public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager) {
        onH5ResultClose.onH5ResultClose(baseEnglishH5CoursewarePager);
    }
}
