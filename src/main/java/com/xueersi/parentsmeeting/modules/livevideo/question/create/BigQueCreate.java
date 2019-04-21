package com.xueersi.parentsmeeting.modules.livevideo.question.create;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveBigQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveQuestionPager;

/**
 * Created by linyuqiang on 2019/4/21.
 * 大题互动创建
 */
public interface BigQueCreate {
    BaseLiveBigQuestionPager create(VideoQuestionLiveEntity videoQuestionLiveEntity);
}
