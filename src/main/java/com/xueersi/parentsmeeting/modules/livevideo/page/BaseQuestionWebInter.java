package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.view.View;

import com.xueersi.common.base.BasePager;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/5/29.
 * 普通互动题网页显示
 * 实现这个必须继承BasePager
 */
public interface BaseQuestionWebInter {
    String getTestId();

    View getRootView();

    void examSubmitAll();

    BasePager getBasePager();

    interface StopWebQuestion {
        void stopWebQuestion(BasePager pager, String testId);

        void umsAgentDebugSys(String eventId, final Map<String, String> mData);
    }
}
