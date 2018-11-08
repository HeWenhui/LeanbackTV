package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.parentsmeeting.module.browser.provider.WebFunctionProvider;

public class ChineseFunction extends WebFunctionProvider {

    public ChineseFunction(WebView webView) {
        super(webView);
    }

    @Override
    public void close() {
//        super.close();
        callBack.close();
    }

    @Override
    public void start(String name, String callback) {
        super.start(name, callback);
    }

    public static class CallBack {
        void close() {

        }

        void start() {

        }
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    //
    CallBack callBack;
}