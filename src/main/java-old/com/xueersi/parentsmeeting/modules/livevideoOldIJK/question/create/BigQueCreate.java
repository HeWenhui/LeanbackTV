package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.create;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseLiveBigQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;

/**
 * Created by linyuqiang on 2019/4/21.
 * 大题互动创建
 */
public interface BigQueCreate {
    BaseLiveBigQuestionPager create(VideoQuestionLiveEntity videoQuestionLiveEntity, RelativeLayout rlQuestionResContent, LiveBasePager.OnPagerClose onPagerClose, OnSubmit onSubmit);

    interface OnSubmit {
        void onSubmit(LiveBasePager liveBasePager);
    }
}
