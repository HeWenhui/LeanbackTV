package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.web;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
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

    public static void testCourseware(final WebView wvSubjectWeb) {
        try {
            final LogToFile logToFile = new LogToFile(wvSubjectWeb.getContext(), TAG);
            final int old = CALL_TIMES;
            final String data = "*";
            final JSONObject type = new JSONObject();
            wvSubjectWeb.loadUrl("javascript:testCourseware(" + type + ",'" + data + "')");
            wvSubjectWeb.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (CALL_TIMES == old) {
                            logToFile.d("testCourseware:add:type=" + type + ",old=" + old + ",times=" + CALL_TIMES);
//                    wvSubjectWeb.loadUrl("javascript:" + data);
                            wvSubjectWeb.loadUrl("javascript:window.parent = {}");
                            wvSubjectWeb.loadUrl("javascript:window.parent.postMessage = function (message, origin) {var data = {where: 'postMessage',message: message,origin: origin};console.log(data);window.xesApp && xesApp.postMessage(JSON.stringify(data));}");
                            wvSubjectWeb.loadUrl("javascript:window.addEventListener('message', function (e) {var data = {where: 'addEventListener',message: e.data,origin: e.origin};console.log(data);window.xesApp && xesApp.postMessage(JSON.stringify(data));})");
                            wvSubjectWeb.loadUrl("javascript:function sendToCourseware(message, origin) {window.postMessage(message, origin);window.xesApp && xesApp.onReceive(JSON.stringify(message));}");
                            wvSubjectWeb.loadUrl("javascript:function testCourseware(message, origin) {window.xesApp && xesApp.onReceive(JSON.stringify(message));}");
                        } else {
                            logToFile.d("testCourseware:type=" + type + ",old=" + old + ",times=" + CALL_TIMES);
                        }
                    }catch (Exception e){
                        CrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }
}
