package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.view.View;

import com.xueersi.parentsmeeting.base.BasePager;

import java.util.Map;

/**
 * Created by lyqai on 2018/5/29.
 * 普通互动题网页显示
 * 实现这个必须继承BasePager
 */
public interface BaseQuestionWebPager {
    String getTestId();

    View getRootView();

    void examSubmitAll();

    interface StopWebQuestion {
        void stopWebQuestion(BasePager pager, String testId);

        void umsAgentDebugSys(String eventId, final Map<String, String> mData);
    }
}