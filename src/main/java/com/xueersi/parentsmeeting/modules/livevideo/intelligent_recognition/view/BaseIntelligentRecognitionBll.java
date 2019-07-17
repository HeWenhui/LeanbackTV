package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.IntelligentRecognitionHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.IntelligentRecognitionHttpResponseParser;

import java.lang.reflect.ParameterizedType;

public class BaseIntelligentRecognitionBll<T extends ViewModel> {

    protected FragmentActivity mActivity;

    protected T mViewModel;

    private volatile IntelligentRecognitionHttpManager httpManager;

    private volatile IntelligentRecognitionHttpResponseParser intelligentRecognitionHttpResponseParser;

    protected BaseIntelligentRecognitionBll(FragmentActivity context, Class<T> clazz) {
        httpManager = new IntelligentRecognitionHttpManager(mActivity = context);
        intelligentRecognitionHttpResponseParser = new IntelligentRecognitionHttpResponseParser();
        mViewModel = ViewModelProviders.of(mActivity).get(clazz);
    }

    private Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    protected IntelligentRecognitionHttpManager getHttpManager() {
        if (httpManager == null) {
            synchronized (this) {
                if (httpManager == null) {
                    httpManager = new IntelligentRecognitionHttpManager(mActivity);
                }
            }
        }
        return httpManager;
    }

    protected IntelligentRecognitionHttpResponseParser getHttpResponseParser() {
        if (intelligentRecognitionHttpResponseParser == null) {
            synchronized (this) {
                if (intelligentRecognitionHttpResponseParser == null) {
                    intelligentRecognitionHttpResponseParser = new IntelligentRecognitionHttpResponseParser();
                }
            }
        }
        return intelligentRecognitionHttpResponseParser;
    }

}
