package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.web.NbCourseCache;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.InfoUtils;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WrapInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Nb 物理实验 资源 预加载
 *
 * @author chekun
 * created  at 2019/4/13 11:08
 */
public class NbCourseCache {
    private Logger logger = LoggerFactory.getLogger(NbCourseCache.class.getSimpleName());
    /**
     * 本地assets 包下 资源  url 路径中关键字
     **/
    private static final String ASSESTFILE_PATH_KEY_WORD = "/assets/";

    /**
     * 本地build 包下 资源  url 路径中关键字
     **/
    private static final String BUILDFILE_PATH_KEY_WORD = "/build/";

    /**
     * 本地资源包 根路径下  url 路径中关键字
     **/
    private static final String ROOT_FILE_PATH_KEY_WORD = "physics-libs";

    private static final String FREE_FILE_PATH_KEY_WORD = "wuliplayercdn.nobook.com";
    private static final String RESROOTDIR = "";


    private HashMap header;
    /**
     * Nb 预加载资源包文件
     **/
    private File mNbCacheFileDir;

    private NbCourseWareEntity nbCourseWareEntity;

    public NbCourseCache(Context context, NbCourseWareEntity nbCourseWareEntity) {
        header = new HashMap();
        this.nbCourseWareEntity = nbCourseWareEntity;
//        String resDir = ShareDataManager.getInstance().getString(NbCourseWareConfig.LOCAL_RES_DIR, "",
//                ShareDataManager.SHAREDATA_NOT_CLEAR);
//        File mResDir = LiveCacheFile.geCacheFile(context, NbCourseWareConfig.NB_RESOURSE_CACHE_DIR);
//        if (mResDir.exists() && !TextUtils.isEmpty(resDir)) {
//            mNbCacheFileDir = new File(mResDir, resDir);
//        }

        File tempFile; //= LiveCacheFile.geCacheFile(context, NbCourseWareConfig.NB_RESOURSE_CACHE_DIR);
        tempFile = InfoUtils.getNbFilePath(context);
//        if (AppConfig.DEBUG && nbCourseWareEntity.isNbExperiment() == NbCourseWareEntity.NB_FREE_EXPERIMENT) {
//            mNbCacheFileDir = new File(
//                    tempFile.getPath() +
//                            File.separator +
//                            "257961cc0edc4edd31a9f8394fc4fb194792219270");
//        } else {
//        mNbCacheFileDir = new File(
//                tempFile.getPath() +
//                        File.separator +
//                        nbCourseWareEntity.getExperimentId());
//        }
        if (nbCourseWareEntity.getExperimentId() != null) {
            mNbCacheFileDir = new File(tempFile, nbCourseWareEntity.getExperimentId());
        }
        header.put("Access-Control-Allow-Origin", "*");
        webInstertJs = new WebInstertJs(context, "99999");
    }


    /**
     * 拦截请求看是否存在本地资源文件
     *
     * @param view
     * @param url
     * @return
     */
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse response = null;
        try {
            if (mNbCacheFileDir != null && mNbCacheFileDir.exists()) {
                File resFile = null;
                if (isAssetsRes(url)) {
                    resFile = getAssetsFile(url);
                } else if (isBulidRes(url)) {
                    resFile = getBulidFile(url);
                } else if (isRootRes(url)) {
                    resFile = getRootFile(url);
                }
                FileInputStream inputStream = null;
                if (resFile != null && resFile.exists() && resFile.length() > 0) {
                    try {
                        inputStream = new FileInputStream(resFile);

                        String extension = MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                        if (TextUtils.isEmpty(mimeType)) {
                            mimeType = extension.endsWith("js") ? "application/x-javascript" :
                                    "application/octet-stream";
                        }
                        response = new WebResourceResponse(mimeType, "", inputStream);
                        response.setResponseHeaders(header);
                        logger.i("需要加载的url:" + url + " 找到本地资源,资源路径" + resFile.getPath());
                        // Log.e("NbCourseCache", "====>return local resource:"+mimeType+":"+extension);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (resFile != null) {
                        logger.i("需要加载的url:" + url + " 寻找的本地文件不存在，路径:" + resFile.getPath());
                    } else {
                        logger.i("需要加载的url:" + url + " 寻找的本地文件不存在，路径为null:");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e.getMessage());
        }
        return response;
    }

    /**
     * 是否是 本地资源包根路径下的 资源
     *
     * @param url
     * @return
     */
    private boolean isRootRes(String url) {
        boolean result = false;
        if (!TextUtils.isEmpty(url)) {
            int index = url.lastIndexOf(File.separator);
            if (index != -1) {
                String fileName = url.substring(index + 1, url.length());
                if (fileName.endsWith(".js")) {
                    result = fileName.contains(ROOT_FILE_PATH_KEY_WORD);
                }
            }
            if (nbCourseWareEntity.isNbExperiment() == NbCourseWareEntity.NB_FREE_EXPERIMENT) {
                result = url.contains(FREE_FILE_PATH_KEY_WORD);
            }
        }

        return result;
    }

    private File getRootFile(String url) {
        File resultFile = null;
        if (!TextUtils.isEmpty(url)) {
            int index = url.lastIndexOf(File.separator);
            if (index != -1) {
                String fileName = url.substring(index + 1, url.length());
                //Log.e("NbCourseCache", "======>getRootFile: filePath=" + fileName);
//                resultFile = new File(mNbCacheFileDir, fileName);
                resultFile = new File(mNbCacheFileDir, fileName);
            }
        }
        return resultFile;
    }

    /**
     * 是否是本地 build 文件夹中的文件
     *
     * @param url
     * @return
     */
    private boolean isBulidRes(String url) {
        return !TextUtils.isEmpty(url) && url.contains(BUILDFILE_PATH_KEY_WORD);
    }

    private File getBulidFile(String url) {
        File resultFile = null;
        int index = url.lastIndexOf(BUILDFILE_PATH_KEY_WORD);
        if (index != -1) {
            String filePath = url.substring(index + 1, url.length());
            //Log.e("NbCourseCache", "======>getBulidFile: filePath=" + filePath);
//            resultFile = new File(mNbCacheFileDir, filePath);
            resultFile = new File(mNbCacheFileDir, filePath);
        }
        return resultFile;
    }


    /**
     * 是否是本地 assetsRes中的文件
     *
     * @param url
     * @return
     */
    private boolean isAssetsRes(String url) {
        return !TextUtils.isEmpty(url) && url.contains(ASSESTFILE_PATH_KEY_WORD);
    }

    private File getAssetsFile(String url) {
        File resultFile = null;
        int index = url.lastIndexOf(ASSESTFILE_PATH_KEY_WORD);
        if (index != -1) {
            String filePath = url.substring(index + 1, url.length());
            //Log.e("NbCourseCache", "======>getAssetsFile: filePath=" + filePath);
            resultFile = new File(mNbCacheFileDir, filePath);
//            resultFile = new File(mNbCacheFileDir.getPath() + filePath + File.pathSeparator + filePath);
        }
        return resultFile;
    }

    private WebInstertJs webInstertJs;

    public WebResourceResponse interceptIndexRequest(WebView view, String url) {
        File file = getCourseWareFile(url);
        InputStream inputStream = null;
        if (file != null) {
            inputStream = webInstertJs.readFile(url, file);
        }
        //logToFile.d("interceptIndexRequest:url=" + url + ",inputStream1=" + (inputStream == null));
        AtomicBoolean islocal = new AtomicBoolean();
        if (inputStream == null) {
            inputStream = webInstertJs.httpRequest(url, islocal);
        }
        //logToFile.d("interceptIndexRequest:url=" + url + ",inputStream2=" + (inputStream == null));
        if (inputStream != null) {
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
            String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", new WrapInputStream
                    (view.getContext(), inputStream));
            webResourceResponse.setResponseHeaders(header);
            return webResourceResponse;
        }
        return null;
    }

    private File getCourseWareFile(String url) {
        return null;
    }


    public WebResourceResponse interceptJsRequest(WebView view, String url) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        InputStream inputStream = webInstertJs.indexStream();
        //logToFile.d("interceptJsRequest:url=" + url + ",inputStream=" + (inputStream == null));
        if (inputStream != null) {
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
            webResourceResponse.setResponseHeaders(header);
            return webResourceResponse;
        }
        return null;
    }

}

