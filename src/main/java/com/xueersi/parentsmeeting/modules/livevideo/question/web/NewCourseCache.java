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
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;

/**
 * Created by linyuqiang on 2019/3/5.
 * 新课件预加载
 */
public class NewCourseCache {
    private String eventId = "NewCourseCache_cache";
    private String TAG = "NewCourseCache";
    protected Logger logger = LiveLoggerFactory.getLogger(TAG);
    LogToFile logToFile;
    private Context mContext;
    private File cacheFile;
    private File mMorecacheout;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    private HashMap header;
    private WebInstertJs webInstertJs;
    private String coursewarePages = "courseware_pages";
    private String mathJax = "MathJax";
    private String katex = "katex";

    public NewCourseCache(Context mContext, String liveId) {
        logToFile = new LogToFile(mContext, TAG);
        webInstertJs = new WebInstertJs(mContext);
        cacheFile = LiveCacheFile.geCacheFile(mContext, "webviewCache");
        mPublicCacheout = new File(cacheFile, CoursewarePreload.mPublicCacheoutName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveId);
        mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
    }

    public WebResourceResponse interceptJsRequest(WebView view, String url) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        InputStream inputStream = webInstertJs.indexStream();
        logToFile.d("interceptJsRequest:url=" + url + ",inputStream=" + (inputStream == null));
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
            inputStream = webInstertJs.readFile(url, file);
        }
        logToFile.d("interceptIndexRequest:url=" + url + ",inputStream1=" + (inputStream == null));
        if (inputStream == null) {
            inputStream = webInstertJs.httpRequest(url);
        }
        logToFile.d("interceptIndexRequest:url=" + url + ",inputStream2=" + (inputStream == null));
        if (inputStream != null) {
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
            String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", new WrapInputStream(inputStream));
            webResourceResponse.setResponseHeaders(header);
            return webResourceResponse;
        }
        return null;
    }

    /**
     * 新课件地址都带courseware_pages，和本地文件对比
     *
     * @param url
     * @return
     */
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
        int index = s.indexOf(coursewarePages);
        if (index != -1) {
            file = getCourseWarePagesFileName(s, index);
            logger.d("shouldInterceptRequest:file=" + file + ",file=" + file.exists());
        } else {
            index = s.indexOf(mathJax);
            if (index != -1) {
                file = getMathJaxFileName(s, index);
            } else {
                index = s.indexOf(katex);
                if (index != -1) {
                    file = getkatexFileName(s, index);
                } else {
                    file = getPubFileName(s);
                }
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

    private File getCourseWarePagesFileName(String s, int index) {
        String url2 = s.substring(index + coursewarePages.length());
        int index2 = url2.indexOf("?");
        if (index2 != -1) {
            url2 = url2.substring(0, index2);
        }
        File file = new File(mMorecacheout, url2);
        return file;
    }

    private File getMathJaxFileName(String s, int index) {
        String name;
        int questionIndex = s.indexOf("?");
        if (questionIndex != -1) {
            name = s.substring(index + mathJax.length() + 1, questionIndex);
        } else {
            name = s.substring(index + mathJax.length() + 1);
        }
        File file = new File(mPublicCacheout, name);
        return file;
    }

    private File getkatexFileName(String s, int index) {
        String name;
        int questionIndex = s.indexOf("?");
        if (questionIndex != -1) {
            name = s.substring(index + katex.length() + 1, questionIndex);
        } else {
            name = s.substring(index + katex.length() + 1);
        }
        name = name.replace("%40", "@");
        File file = new File(mPublicCacheout, name);
        return file;
    }

    private File getPubFileName(String s) {
        String path = s;
        int index = s.indexOf("://");
        if (index != -1) {
            path = s.substring(index + 3);
            index = path.indexOf("/");
            if (index != -1) {
                path = path.substring(index + 1);
            }
        }
        String filemd5 = MD5Utils.getMD5(path);
        File file = new File(mPublicCacheout, filemd5);
        return file;
    }
}
