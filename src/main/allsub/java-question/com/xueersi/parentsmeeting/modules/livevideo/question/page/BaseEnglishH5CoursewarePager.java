package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;


/**
 * h5课件基础接口
 *
 * @author linyuqiang
 * @date 2018/5/22
 */
public interface BaseEnglishH5CoursewarePager {
    boolean isFinish();

    void close(String method);

    String getUrl();

    void onBack();

    void destroy(String method);

    View getRootView();

    void onPause();

    void onResume();

    void submitData();

    void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll);

    void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp);

    BasePager getBasePager();

    void setWebBackgroundColor(int color);

    EnglishH5Entity getEnglishH5Entity();

    /**
     * 是否展示过答题结果页面
     * @return
     */
    boolean isResultRecived();

}
