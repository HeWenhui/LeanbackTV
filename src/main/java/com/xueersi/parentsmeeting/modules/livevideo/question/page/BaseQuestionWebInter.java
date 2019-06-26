package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionOnSubmit;

/**
 * Created by linyuqiang on 2018/5/29.
 * 普通互动题网页显示
 * 实现这个必须继承BasePager
 */
public interface BaseQuestionWebInter {
    String getTestId();

    View getRootView();

    void submitData();

    void setQuestionOnSubmit(QuestionOnSubmit questionOnSubmit);

    BasePager getBasePager();

    void onDestroy();

    /**
     * 是否展示过答题结果页面
     *
     * @return
     */
    boolean isResultRecived();

    interface StopWebQuestion {
        void stopWebQuestion(BasePager pager, String testId, BaseVideoQuestionEntity baseVideoQuestionEntity);
    }
}
