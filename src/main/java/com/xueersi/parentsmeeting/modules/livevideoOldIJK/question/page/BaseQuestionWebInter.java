package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;

/**
 * Created by lyqai on 2018/5/29.
 * 普通互动题网页显示
 * 实现这个必须继承BasePager
 */
public interface BaseQuestionWebInter {
    String getTestId();

    View getRootView();

    void submitData();

    BasePager getBasePager();

    void onDestroy();

    interface StopWebQuestion {
        void stopWebQuestion(BasePager pager, String testId, BaseVideoQuestionEntity baseVideoQuestionEntity);
    }
}
