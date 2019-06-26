package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;

/**
 * Created by linyuqiang on 2018/7/25.
 * 创建课前测
 */
public interface BaseExamQuestionCreat {
    BaseExamQuestionInter creatBaseExamQuestion(Activity activity, final String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity, LivePagerBack livePagerBack, LiveBasePager.OnPagerClose onPagerClose);
}
