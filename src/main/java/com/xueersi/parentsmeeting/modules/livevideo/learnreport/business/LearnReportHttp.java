package com.xueersi.parentsmeeting.modules.livevideo.learnreport.business;

import com.xueersi.common.http.HttpCallBack;

/**
 * Created by linyuqiang on 2018/7/12.
 */

public interface LearnReportHttp {
    void sendTeacherEvaluate(int[] score, final HttpCallBack requestCallBack);

    void showToast(String errorMsg);
}
