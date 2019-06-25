package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * @author linyuqiang
 *         互动试卷基础
 * @date 2018/6/6
 */
public interface BaseExamQuestionInter {
    View getRootView();

    void onKeyboardShowing(boolean isShowing);

    void examSubmitAll();

    BasePager getBasePager();

    String getNum();

    void onDestroy();

    /**
     * 是否展示过答题结果页面
     * @return
     */
    boolean isResultRecived();
}
