package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;
import org.json.JSONObject;

public class StaticWeb {
    private String TAG = "StaticWeb";
    private Context activity;
    private WebView mWvSubjectWeb;
    private OnMessage onMessage;

    public StaticWeb(Context activity, WebView wvSubjectWeb, OnMessage onMessage) {
        this.activity = activity;
        this.mWvSubjectWeb = wvSubjectWeb;
        this.onMessage = onMessage;
    }

    @JavascriptInterface
    public void postMessage(String jsonStr) {
        Log.d(TAG, "postMessage:jsonStr=" + jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            onMessage.postMessage(jsonObject.getJSONObject("message"), jsonObject.optString("origin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnMessage {

        void postMessage(JSONObject message, String origin);
    }

    public static void sendToCourseware(WebView wvSubjectWeb, JSONObject type, String data) {
        wvSubjectWeb.loadUrl("javascript:sendToCourseware(" + type + ",'" + data + "')");
    }
}
