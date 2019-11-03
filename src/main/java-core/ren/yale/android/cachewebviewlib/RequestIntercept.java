package ren.yale.android.cachewebviewlib;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

public interface RequestIntercept {
    void onIntercept(String url,WebResourceResponse webResourceResponse);
}
