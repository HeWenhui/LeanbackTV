package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;

public class NewCourseCache {
    String eventId = "NewCourseCache_cache";
    protected Logger logger = LiveLoggerFactory.getLogger("NewCourseCache");
    private Context mContext;
    private File cacheFile;
    private File mMorecacheout;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    private HashMap header;
    WebInstertJs webInstertJs;

    public NewCourseCache(Context mContext) {
        cacheFile = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/parentsmeeting/webviewCache");
        webInstertJs = new WebInstertJs(mContext);
        if (cacheFile == null) {
            cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
        }
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        mPublicCacheout = new File(cacheFile, EnglishH5Cache.mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
    }

    public WebResourceResponse interceptJsRequest(WebView view, String url) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        InputStream inputStream = webInstertJs.indexStream();
        logger.d("interceptJsRequest:url=" + url + ",inputStream=" + (inputStream == null));
        if (inputStream != null) {
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
            webResourceResponse.setResponseHeaders(header);
            return webResourceResponse;
        }
        return null;
    }

    public WebResourceResponse interceptIndexRequest(WebView view, String url) {
        File file = getCourseWareFile(url);
        InputStream inputStream = null;
        if (file != null) {
            inputStream = webInstertJs.readFile(file);
        }
        if (inputStream == null) {
            inputStream = webInstertJs.httpRequest(url);
        }
        if (inputStream != null) {
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
            String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            logger.d("shouldInterceptRequest:url=" + url);
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
            webResourceResponse.setResponseHeaders(header);
            return webResourceResponse;
        }
        return null;
    }

    private File getCourseWareFile(String url) {
        File file = null;
        int index = url.indexOf("courseware_pages");
        if (index != -1) {
            String url2 = url.substring(index + "courseware_pages".length());
            int index2 = url2.indexOf("?");
            if (index2 != -1) {
                url2 = url2.substring(0, index2);
            }
            file = new File(mMorecacheout, url2);
            logger.d("getCourseWareFile:file=" + file + ",file=" + file.exists());
            if (!file.exists()) {
                return null;
            }
        }
        return file;
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
        File file = null;
        int index = s.indexOf("courseware_pages");
        if (index != -1) {
            String url2 = s.substring(index + "courseware_pages".length());
            int index2 = url2.indexOf("?");
            if (index2 != -1) {
                url2 = url2.substring(0, index2);
            }
            file = new File(mMorecacheout, url2);
            logger.d("shouldInterceptRequest:file=" + file + ",file=" + file.exists());
        } else {
            index = s.indexOf("MathJax");
            if (index != -1) {
                String name;
                int questionIndex = s.indexOf("?");
                if (questionIndex != -1) {
                    name = s.substring(index + 8, questionIndex);
                } else {
                    name = s.substring(index + 8);
                }
                File pubFile = new File(mPublicCacheout, name);
                file = pubFile;
            } else {
                String filemd5 = MD5Utils.getMD5(s);
                file = new File(mMorecacheout, filemd5);
            }
            index = s.lastIndexOf("/");
            String name = s;
            if (index != -1) {
                name = s.substring(index);
            }
            logger.d("shouldInterceptRequest:file2=" + file.getName() + ",name=" + name + ",file=" + file
                    .exists());
        }
        if (file.exists()) {
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("url", s);
                hashMap.put("filepath", file.getPath());
                hashMap.put("filelength", "" + file.length());
                UmsAgentManager.umsAgentDebug(mContext, eventId, hashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
            }
            if (file.length() > 0) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    String extension = MimeTypeMap.getFileExtensionFromUrl(s.toLowerCase());
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "",
                            inputStream);
                    webResourceResponse.setResponseHeaders(header);
                    Log.e("Duncan", "artsload");
                    return webResourceResponse;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
