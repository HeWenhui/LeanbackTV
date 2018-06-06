package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.view.View;

import com.xueersi.parentsmeeting.base.BasePager;

/**
 * @author linyuqiang
 *         互动试卷基础
 * @date 2018/6/6
 */
public interface BaseExamQuestionPager {
    View getRootView();

    void onKeyboardShowing(boolean isShowing);

    void examSubmitAll();

    BasePager getBasePager();

    String getNum();

    void onDestroy();

    interface ExamStop {
        void stopExam();
    }
}
