package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseExamQuestionInter;

/**
 * Created by linyuqiang on 2018/7/25.
 * 创建课前测
 */
public interface BaseExamQuestionCreat {
    public BaseExamQuestionInter creatBaseExamQuestion(Activity activity, final String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity);
}
