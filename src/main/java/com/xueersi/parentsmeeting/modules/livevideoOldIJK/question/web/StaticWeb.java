package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.web;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linyuqiang on 2019/3/5.
 * 新课件和客户端通信
 */
public class StaticWeb {
    private static String TAG = "StaticWeb";
    private OnMessage onMessage;
    private LogToFile logToFile;

    public StaticWeb(Context activity, WebView wvSubjectWeb, OnMessage onMessage) {
        logToFile = new LogToFile(activity, TAG);
        this.onMessage = onMessage;
    }

    /**
     * 接收课件消息
     *
     * @param jsonStr
     */
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

    private static int CALL_TIMES = 0;

    /**
     * 调用sendToCourseware后的回执
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void onReceive(String jsonStr) {
        CALL_TIMES++;
        logToFile.d("onReceive:jsonStr=" + jsonStr + ",times=" + CALL_TIMES);
    }

    public static void sendToCourseware(final WebView wvSubjectWeb, final JSONObject type, String data) {
        final LogToFile logToFile = new LogToFile(wvSubjectWeb.getContext(), TAG);
        final int old = CALL_TIMES;
        wvSubjectWeb.loadUrl("javascript:sendToCourseware(" + type + ",'" + data + "')");
        wvSubjectWeb.post(new Runnable() {
            @Override
            public void run() {
                logToFile.d("sendToCourseware:type=" + type + ",old=" + old + ",times=" + CALL_TIMES);
            }
        });
    }
}
