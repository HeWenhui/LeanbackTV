package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;

/**
 * Created by lyqai on 2018/7/26.
 */

public class LiveBaseEnglishH5CoursewareCreat implements BaseEnglishH5CoursewareCreat {
    private AnswerRankBll mAnswerRankBll;
    private boolean IS_SCIENCE;

    public void setmAnswerRankBll(AnswerRankBll mAnswerRankBll) {
        this.mAnswerRankBll = mAnswerRankBll;
    }

    public void setIS_SCIENCE(boolean IS_SCIENCE) {
        this.IS_SCIENCE = IS_SCIENCE;
    }

    @Override
    public BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID) {
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        EnglishH5CoursewareX5Pager h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, mAnswerRankBll == null ? "0" : mAnswerRankBll.getIsShow(), IS_SCIENCE);
        return h5CoursewarePager;
    }
}
