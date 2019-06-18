package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseEnglishH5CoursewarePager;

/**
 * Created by lyqai on 2018/7/26.
 */

public interface BaseEnglishH5CoursewareCreat {
    BaseEnglishH5CoursewarePager creat(Context context, VideoQuestionLiveEntity videoQuestionH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose, String mVSectionID);
}
