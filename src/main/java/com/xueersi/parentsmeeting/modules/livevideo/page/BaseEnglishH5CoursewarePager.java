package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.view.View;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;

/**
 * h5课件基础接口
 *
 * @author lyqai
 * @date 2018/5/22
 */
public interface BaseEnglishH5CoursewarePager {
    boolean isFinish();

    void close();

    String getUrl();

    void onBack();

    void destroy();

    View getRootView();

    void onPause();

    void onResume();

    void submitData();

    void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll);

    BasePager getBasePager();

    void setWebBackgroundColor(int color);
}
