package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.util.Log;

import com.airbnb.lottie.AssertUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    protected String TAG = getClass().getSimpleName();
    protected Logger logger = LiveLoggerFactory.getLogger(TAG);
    LogToFile logToFile;
    protected Context mContext;
    private File cacheFile;
    private File mMorecacheout;
    protected File todayLiveCacheDir;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    protected HashMap header;
    protected WebInstertJs webInstertJs;
    /** 普通新课件标识 */
    protected String coursewarePages = "courseware_pages";
    /** 未来课件标识 */
    protected String XESlides = "XESlides";
    protected String mathJax = "MathJax";
    protected String katex = "katex";
    /** 初高中连对，取本地图片 */
    private String zhongXueKeJian = "ZhongXueKeJian";
    OnHttpCode onHttpCode;
    private ArrayList<InterceptRequest> interceptRequests = new ArrayList<>();

    public NewCourseCache(Context mContext, String liveId, String testid) {
        this.mContext = mContext;
        logToFile = new LogToFile(mContext, TAG);
        try {
            logToFile.addCommon("testid", testid);
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        webInstertJs = new WebInstertJs(mContext, testid);
        cacheFile = LiveCacheFile.geCacheFile(mContext, "webviewCache");
        mPublicCacheout = new File(cacheFile, CoursewarePreload.mPublicCacheoutName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        todayLiveCacheDir = new File(todayCacheDir, liveId);
        mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
    }

    public void add(InterceptRequest interceptRequest) {
        interceptRequests.add(interceptRequest);
    }

    public OnHttpCode getOnHttpCode() {
        return onHttpCode;
    }

    public void setOnHttpCode(OnHttpCode onHttpCode) {
        this.onHttpCode = onHttpCode;
        webInstertJs.setOnHttpCode(onHttpCode);
    }

    public int loadCourseWareUrl(String url) {
        int type = index(url, coursewarePages);
        if (type != 1) {
            type = index(url, XESlides);
        }
        return type;
    }

    private int index(String url, String content) {
        try {
            if (url.contains(content)) {
                ArrayList<String> urls = new ArrayList<>();
                int index = url.indexOf("?");
                if (index != -1) {
                    String url1 = url.substring(0, index);
                    String url2 = url.substring(index + 1, url.length());
                    if (url1.contains(content)) {
                        urls.add(url1);
                    }
                    if (url2.contains(content)) {
                        index = url2.indexOf("&");
                        if (index != -1) {
                            url2 = url2.substring(0, index);
                        }
                        urls.add(url2);
                    }
                } else {
                    urls.add(url);
                }
                logger.d("loadCourseWareUrl:url=" + urls.size());
                boolean ispreload = true;
                for (int i = 0; i < urls.size(); i++) {
                    String urlChild = urls.get(i);
                    index = urlChild.indexOf(content);
                    File file = getCourseWarePagesFileName(urlChild, content, index);
                    logger.d("loadCourseWareUrl:urlChild=" + urlChild + "," + file + ",exists=" + file.exists());
                    if (!file.exists()) {
                        ispreload = false;
                    }
                }
                return ispreload ? 1 : -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        return 0;
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
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", new WrapInputStream(view.getContext(), inputStream));
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
        int index = url.indexOf(coursewarePages);
        if (index != -1) {
            String url2 = url.substring(index + coursewarePages.length());
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
            file = getCourseWarePagesFileName(s, coursewarePages, index);
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
                    File interceptFile = null;
                    for (int i = 0; i < interceptRequests.size(); i++) {
                        InterceptRequest interceptRequest = interceptRequests.get(i);
                        interceptFile = interceptRequest.shouldInterceptRequest(view, s);
                        if (interceptFile != null) {
                            file = interceptFile;
                            break;
                        }
                    }
                    if (interceptFile == null) {
                        file = getPubFileName(s);
                    }
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

    protected File getCourseWarePagesFileName(String s, String contens, int index) {
        String url2 = s.substring(index + contens.length());
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
        if (s.endsWith(CoursewarePreload.FZY3JW_TTF)) {
            File file = new File(mPublicCacheout, CoursewarePreload.FZY3JW_TTF);
            if (file.exists()) {
                return file;
            }
        }
        String path = s;
        int index = s.indexOf("://");
        if (index != -1) {
            path = s.substring(index + 3);
            index = path.indexOf("/");
            if (index != -1) {
                path = path.substring(index);
            }
        }
        String filemd5 = MD5Utils.getMD5(path);
        File file = new File(mPublicCacheout, filemd5);
        return file;
    }

    public WebResourceResponse interceptZhongXueKeJian(String url) {
        int index2 = url.indexOf(zhongXueKeJian);
        if (index2 != -1) {
            if (url.contains("animations") || url.contains("assets")) {
                String fileSub = url.substring(index2 + zhongXueKeJian.length());
                try {
                    String fileName = "newcourse_result/sec/middleSchoolCourseware" + fileSub;
                    InputStream inputStream = AssertUtil.open(fileName);
                    String extension = MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", new WrapInputStream(mContext, inputStream));
                    webResourceResponse.setResponseHeaders(header);
                    logger.d("interceptZhongXueKeJian:fileName=" + fileName);
                    return webResourceResponse;
                } catch (Exception e) {
                    logger.d("interceptZhongXueKeJian:fileSub=" + fileSub);
                }
            }
        }
        return null;
    }

    /**
     * 对url进行文件拦截
     */
    interface InterceptRequest {
        File shouldInterceptRequest(WebView view, String url);
    }

    /**
     * 对url进行文件拦截，未来课件
     */
    public class FutureCourse implements InterceptRequest {

        @Override
        public File shouldInterceptRequest(WebView view, String url) {
            File file = null;
            int index = url.indexOf(XESlides);
            if (index != -1) {
                String url2 = url.substring(index + XESlides.length());
                int index2 = url2.indexOf("?");
                if (index2 != -1) {
                    url2 = url2.substring(0, index2);
                }
                file = new File(mMorecacheout, url2);
                logger.d("FutureCourse:Intercept:file=" + file + ",file=" + file.exists());
                if (!file.exists()) {
                    return null;
                }
            }
            return file;
        }
    }
}
