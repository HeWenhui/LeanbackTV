package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;

/**
 * Created by linyuqiang on 2019/3/18.
 * 新课件，结果页
 */
public class MiddleResult {
    private static String TAG = "MiddleResult";
    private LogToFile logToFile;

    public MiddleResult(Context activity) {
        logToFile = new LogToFile(activity, TAG);
    }

    @JavascriptInterface
    public final void resultPageLoaded(String data) {
        logToFile.d("resultPageLoaded:data=" + data);
        onResultPageLoaded(data);
    }

    protected void onResultPageLoaded(String data) {

    }
}
