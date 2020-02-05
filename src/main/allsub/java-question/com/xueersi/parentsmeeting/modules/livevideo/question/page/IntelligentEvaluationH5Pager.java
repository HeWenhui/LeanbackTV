package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;

/**
 * 评测H5pager.
 */
public class IntelligentEvaluationH5Pager implements BaseEnglishH5CoursewarePager {
    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void close(String method) {

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void onBack() {

    }

    @Override
    public void destroy(String method) {

    }

    @Override
    public View getRootView() {
        return null;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void submitData() {

    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {

    }

    @Override
    public BasePager getBasePager() {
        return null;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        return null;
    }

    @Override
    public boolean isResultRecived() {
        return false;
    }
}
