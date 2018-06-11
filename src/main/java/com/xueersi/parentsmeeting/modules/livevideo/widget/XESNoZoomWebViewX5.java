package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 一个光的浏览器控件
 *
 * @author tiger
 */
@SuppressLint("NewApi")
public class XESNoZoomWebViewX5 extends WebView {

    public XESNoZoomWebViewX5(Context context) {
        super(context);
        defaultController();
    }

    public XESNoZoomWebViewX5(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defaultController();
    }

    public XESNoZoomWebViewX5(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultController();
    }

    /**
     * 使得控制按钮不可用
     */
    private void defaultController() {
        // API version 大于11的时候，SDK提供了屏蔽缩放按钮的方法
        WebSettings webSettings = getSettings();
        webSettings.setBuiltInZoomControls(true);
        try {
            webSettings.setDisplayZoomControls(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        webSettings.setSavePassword(false);
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
    }

}
