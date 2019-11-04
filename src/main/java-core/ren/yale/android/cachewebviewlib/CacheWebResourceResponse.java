package ren.yale.android.cachewebviewlib;


import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import java.io.InputStream;

public class CacheWebResourceResponse extends WebResourceResponse {
    private boolean isFile = false;

    public CacheWebResourceResponse(String s, String s1, InputStream inputStream) {
        super(s, s1, inputStream);
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isFile() {
        return isFile;
    }
}
