package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;

import org.json.JSONException;
import org.json.JSONObject;

public class StaticWeb {
    private static String TAG = "StaticWeb";
    private OnMessage onMessage;
    private LogToFile logToFile;

    public StaticWeb(Context activity, WebView wvSubjectWeb, OnMessage onMessage) {
        logToFile = new LogToFile(activity, TAG);
        this.onMessage = onMessage;
    }

    @JavascriptInterface
    public void postMessage(String jsonStr) {
        if (!("" + jsonStr).contains("errorInfo")) {
            logToFile.d("postMessage:jsonStr=" + jsonStr);
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject message = jsonObject.optJSONObject("message");
            if (message != null) {
                String where = jsonObject.optString("where");
                onMessage.postMessage(where, message, jsonObject.optString("origin"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnMessage {

        /**
         * @param where   assets\webview_postmessage\index.js 代码中定义
         * @param message
         * @param origin
         */
        void postMessage(String where, JSONObject message, String origin);
    }

    public static void sendToCourseware(WebView wvSubjectWeb, JSONObject type, String data) {
        LogToFile logToFile = new LogToFile(wvSubjectWeb.getContext(), TAG);
        logToFile.d("sendToCourseware:type=" + type);
        wvSubjectWeb.loadUrl("javascript:sendToCourseware(" + type + ",'" + data + "')");
    }
}
