package com.xueersi.parentsmeeting.modules.livevideo.question.create;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveBigQuestionPager;

/**
 * Created by linyuqiang on 2019/4/21.
 * 大题互动创建
 */
public interface BigQueCreate {
    BaseLiveBigQuestionPager create(VideoQuestionLiveEntity videoQuestionLiveEntity, LiveViewAction liveViewAction, LiveBasePager.OnPagerClose onPagerClose, OnSubmit onSubmit);

    interface OnSubmit {
        void onSubmit(LiveBasePager liveBasePager);
    }
}
