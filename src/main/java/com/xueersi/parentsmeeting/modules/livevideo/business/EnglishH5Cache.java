package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.DownloadCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsMoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreCache;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipProg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.util.MD5;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import ren.yale.android.cachewebviewlib.CachePreLoadService;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;

/**
 * 英语课件缓存
 * Created by linyuqiang on 2017/12/28.
 */
public class EnglishH5Cache implements EnglishH5CacheAction {
    String TAG = "EnglishH5Cache";
    Logger logger = LoggerFactory.getLogger(TAG);
    String eventId = LiveVideoConfig.LIVE_H5_CACHE;
    Context context;
    LiveAndBackDebug liveAndBackDebug;
    String liveId;
    File cacheFile;
    RelativeLayout bottomContent;
    ArrayList<CacheWebView> cacheWebViews = new ArrayList<>();
    CacheReceiver cacheReceiver;
    /** 网络类型 */
    private int netWorkType;
    boolean useService = true;
    boolean isStart = true;
    private List<MoreCache> mList;
    private List<ArtsMoreChoice> mArtsList;
    private File mMorecachein;
    private File mMorecacheout;
    /** 公共资源 */
    private File mPublicCacheout;
    /** 公共资源 */
    public static String mPublicCacheoutName = "publicRes";
    private File mArtsMorecachein;
    private File mArtsMorecacheout;
    private ArrayList<String> mUrls;
    private ArrayList<String> mArtsUrls;
    private LiveHttpManager mHttpManager;
    /** 公共资源 */
    private ArrayList<String> mtexts;
    private ArrayList<String> mfonts;
    private int count = 0;
    private Boolean add = true;

    private LiveGetInfo mGetInfo;

    public EnglishH5Cache(Context context, LiveGetInfo mGetInfo) {
        this.context = context;
        Activity activity = (Activity) context;
        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        bottomContent = (RelativeLayout) activity.findViewById(R.id.rl_course_video_live_question_content);
        this.liveId = mGetInfo.getId();
        this.mGetInfo = mGetInfo;
        cacheFile = LiveCacheFile.geCacheFile(context, "webviewCache");
//        cacheFile = new File(context.getCacheDir(), "cache/webviewCache");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        ProxUtil.getProxUtil().put(context, WebViewRequest.class, webViewRequest);
    }

//    public EnglishH5Cache(Context context, String liveId) {
//        this.context = context;
//        Activity activity = (Activity) context;
//        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
//        bottomContent = (RelativeLayout) activity.findViewById(R.id.rl_course_video_live_question_content);
//        this.liveId = liveId;
//        cacheFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/parentsmeeting/webviewCache");
//        if (cacheFile == null) {
//            cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
//        }
////        cacheFile = new File(context.getCacheDir(), "cache/webviewCache");
//        if (!cacheFile.exists()) {
//            cacheFile.mkdirs();
//        }
//        ProxUtil.getProxUtil().put(context, WebViewRequest.class, webViewRequest);
//    }

    private WebViewRequest webViewRequest = new WebViewRequest() {
        @Override
        public void requestWebView() {
            stop();
        }

        @Override
        public void releaseWebView() {
            start();
        }

    };

    public void setHttpManager(LiveHttpManager httpManager) {
        this.mHttpManager = httpManager;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            logger.d("handleMessage:cacheReceiver=" + (cacheReceiver == null));
            if (cacheReceiver == null) {
                return;
            }
            if (msg.what == 1) {
                String url = (String) msg.obj;
                int netWorkType = NetWorkHelper.getNetWorkState(context);
                boolean load = true;
                if (netWorkType == NetWorkHelper.NO_NETWORK) {
                    cacheReceiver.error(url);
                    load = false;
                } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
                    cacheReceiver.error(url);
                    load = false;
                }
                if (load) {
                    if (useService) {
                        CacheWebView.servicePreload(context, url);
                    } else {
                        loadUrl(url);
                    }
                }
            }
        }
    };

    @Override
    public void getCourseWareUrl() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveId);
        boolean exists = todayLiveCacheDir.exists();
        boolean mkdirs = false;
        if (!todayLiveCacheDir.exists()) {
            mkdirs = todayLiveCacheDir.mkdirs();
        }
        logger.d("getCourseWareUrl:exists=" + exists + ",mkdirs=" + mkdirs);
        CacheWebView.getCacheConfig().init(context, todayLiveCacheDir.getPath(), 1024 * 1024 * 100, 1024 * 1024 * 10)
                .enableDebug(AppConfig.DEBUG);//100M 磁盘缓存空间,10M 内存缓存空间
        //替换x5浏览器，缓存mp3经常出问题
//        CacheExtensionConfig.addGlobalExtension("mp3");
//        CacheExtensionConfig.addGlobalExtension("WAV");
//        CacheExtensionConfig.removeNoCacheExtension("mp3");
        boolean isNewPreLoad = ((Activity) context).getIntent().getBooleanExtra("newCourse", false);
        if (!isNewPreLoad) {
            mHttpManager.getCourseWareUrl(new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    if (responseEntity.getJsonObject() instanceof JSONArray) {
                        ProxUtil.getProxUtil().remove(context, WebViewRequest.class);
                        return;
                    }
                    if (!isStart) {
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            //删除除了公共资源的文件夹
                            File files[] = cacheFile.listFiles();
                            if (files != null) {
                                for (int i = 0; i < files.length; i++) {
                                    File delectFile = files[i];
                                    if (!delectFile.getPath().equals(todayCacheDir.getPath())) {
                                        if (!delectFile.getName().startsWith(mPublicCacheoutName)) {
                                            if (delectFile.isDirectory()) {
                                                FileUtils.deleteDir(delectFile);
                                            } else {
                                                FileUtils.deleteFile(delectFile);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.start();
                    final JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    logger.d("getCourseWareUrl:onPmSuccess:jsonObject=" + jsonObject);
                    try {
                        JSONObject liveIdObj = jsonObject.getJSONObject(liveId);
                        JSONArray urlArray = liveIdObj.getJSONArray("url");
                        final ArrayList<String> urls = new ArrayList<>();
                        for (int i = 0; i < urlArray.length(); i++) {
                            String play_url = urlArray.getString(i);
                            File file = new File(todayLiveCacheDir, MD5.md5(play_url));
                            int index = play_url.indexOf("/index.html");
                            String startUrl = play_url.substring(0, index);
                            if (!file.exists()) {
                                urls.add(play_url);
                            }
//                        urls.add(play_url);
                        }
                        logger.d("getCourseWareUrl:onPmSuccess:urlArray=" + urlArray.length() + ",urls=" + urls.size());
                        JSONArray infoArray = liveIdObj.getJSONArray("infos");
                        for (int i = 0; i < infoArray.length(); i++) {
                            JSONObject infoObj = infoArray.getJSONObject(i);
                            String id = infoObj.getString("id");
                            String courseware_type = infoObj.getString("type");
                            String play_url = LiveHttpConfig.LIVE_HOST + "/Live/coursewareH5/" + liveId + "/" + id + "/" + courseware_type
                                    + "/123456";
                            logger.d("getCourseWareUrl:onPmSuccess:play_url=" + play_url);
//                        urls.add(play_url);
                        }
                        if (urls.isEmpty()) {
                            ProxUtil.getProxUtil().remove(context, WebViewRequest.class);
                            return;
                        }
                        final ArrayList<String> urls2 = new ArrayList<>();
                        urls2.addAll(urls);
                        IntentFilter intentFilter = new IntentFilter(CachePreLoadService.URL_CACHE_ACTION);
                        cacheReceiver = new CacheReceiver(urls2, todayLiveCacheDir);
                        context.registerReceiver(cacheReceiver, intentFilter);
//                    File cacheFile = new File(this.getCacheDir(), "cache_path_name");
                        for (int i = 0; i < urls.size(); i++) {
                            final String url = urls.get(i);
                            Message msg = handler.obtainMessage(1);
                            msg.what = 1;
                            msg.obj = url;
                            handler.sendMessageDelayed(msg, i * 20000);
                        }
                    } catch (JSONException e) {
                        logger.e("onPmSuccess", e);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    logger.e("getCourseWareUrl:onFailure:e=" + e);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    logger.e("getCourseWareUrl:onPmError:e=" + responseEntity.getErrorMsg());
                }
            });
        }
        mPublicCacheout = new File(cacheFile, mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        // 一次多发的接口调用
        if (LiveVideoConfig.isScience || mGetInfo != null && mGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
            if (!isNewPreLoad) {
                ScienceMulPreDownLoad(todayLiveCacheDir);
            }
            // TODO 理科小学
//            scienceStatic();
        } else if (mGetInfo != null && mGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {
            //语文一题多发
            if (!isNewPreLoad) {
                chineseMulPreDownLoad(todayLiveCacheDir);
            }
        } else {
            if (!isNewPreLoad) {
                ArtsMulPreDownLoad(todayLiveCacheDir);
            }
        }
    }

    private void ArtsMulPreDownLoad(final File path) {
        mHttpManager.getArtsMoreCoureWareUrl(liveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("responseEntity.getJsonObject=" + responseEntity.getJsonObject());
//                final Object jsonObject = responseEntity.getJsonObject();
                JSONObject objects = new JSONObject(responseEntity.getJsonObject().toString());
                JSONArray array = objects.optJSONArray("List");
                JSONArray font = objects.optJSONArray("Font");
                mArtsList = new ArrayList<>();
                mfonts = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    ArtsMoreChoice cache = new ArtsMoreChoice();
                    JSONObject object = array.getJSONObject(i);
                    cache.setSourceId(object.optString("sourceId"));
                    cache.setResourceUrl(object.optString("resourceUrl"));
                    cache.setTemplateUrl(object.optString("templateUrl"));
                    mArtsList.add(cache);
                }
                for (int i = 0; i < font.length(); i++) {
                    String txt = font.optString(i);
                    mfonts.add(txt);
                }
                if (mArtsList.size() > 0) {
                    Artsdownload(path);
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                logger.e("getCourseWareUrl:onFailure:e=" + e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("getCourseWareUrl:onPmError:e=" + responseEntity.getErrorMsg());
            }
        });
    }

    private void Artsdownload(File path) {
        mArtsUrls = new ArrayList<>();
        mArtsMorecachein = new File(path, liveId + "child");
        if (!mArtsMorecachein.exists()) {
            mArtsMorecachein.mkdirs();
        }
        mArtsMorecacheout = new File(path, liveId + "artschild");
        if (!mArtsMorecacheout.exists()) {
            mArtsMorecacheout.mkdirs();
        }
        // 文科一发多题下载及解压预加载的文件
        for (int i = 0; i < mArtsList.size(); i++) {
            if (!TextUtils.isEmpty(mArtsList.get(i).getResourceUrl()) && !mArtsUrls.contains(mArtsList.get(i).getResourceUrl())) {
                mArtsUrls.add(mArtsList.get(i).getResourceUrl());
            }
            if (!TextUtils.isEmpty(mArtsList.get(i).getTemplateUrl()) && !mArtsUrls.contains(mArtsList.get(i).getTemplateUrl())) {
                mArtsUrls.add(mArtsList.get(i).getTemplateUrl());
            }
        }
        for (int i = 0; i < mArtsUrls.size(); i++) {
            final String url = i + ".zip";
            final File save = new File(mArtsMorecachein, url);
            if (!fileIsExists(save.getPath())) {
                final File tempFile = new File(mArtsMorecachein, url + ".temp");
                mHttpManager.download(mArtsUrls.get(i), tempFile.getPath(), new DownloadCallBack() {
                    @Override
                    protected void onDownloadSuccess() {
                        boolean renameTo = tempFile.renameTo(save);
                        logger.d("onDownloadSuccess(mUrls):url=" + url + ",renameTo=" + renameTo);
                        new ZipExtractorTask(new File(mArtsMorecachein, url), mArtsMorecacheout, true, new Progresses()).execute();
                    }

                    @Override
                    protected void onDownloadFailed() {
                        logger.d("onDownloadFailed(mUrls):url=" + url);
//                        XESToastUtils.showToast(context, "下载文科资源包失败");
                    }

                    @Override
                    protected void onDownloadFailed(Exception e) {
                        logger.d("onDownloadFailed " + e.getMessage());
                        super.onDownloadFailed(e);
                    }
                });
            } else {
                logger.d("fileIsExists(mtexts):fileName=" + url);
            }
        }
        // 添加字体的下载链接
        if (mfonts.size() > 0) {
            // 字体文件直接下载到zip解压的文件夹中
            for (int i = 0; i < mfonts.size(); i++) {
                String url = mfonts.get(i);
                final String fileName = MD5Utils.getMD5(url);
                final File save = new File(mArtsMorecacheout, fileName);
                if (!fileIsExists(save.getPath())) {
                    final File tempFile = new File(mArtsMorecacheout, fileName + ".temp");
                    mHttpManager.download(mfonts.get(i), tempFile.getPath(), new DownloadCallBack() {
                        @Override
                        protected void onDownloadSuccess() {
                            boolean renameTo = tempFile.renameTo(save);
                            logger.d("onDownloadSuccess(mtexts):fileName=" + fileName + ",renameTo=" + renameTo);
                        }

                        @Override
                        protected void onDownloadFailed() {
                            logger.d("onDownloadFailed(mtexts):fileName=" + fileName);
//                            XESToastUtils.showToast(context, "下载文科字体包失败");
                        }
                    });
                } else {
                    logger.d("fileIsExists(mtexts):fileName=" + fileName);
                }
            }
        }
    }

    private void download(File path) {
        mUrls = new ArrayList<>();
        mMorecachein = new File(path, liveId);
        if (!mMorecachein.exists()) {
            mMorecachein.mkdirs();
        }
        mMorecacheout = new File(path, liveId + "child");
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
        // 下载以及解压预加载的文件
        for (int i = 0; i < mList.size(); i++) {
            if (!mUrls.contains(mList.get(i).getResourceUrl()) && !TextUtils.isEmpty(mList.get(i).getResourceUrl())) {
                mUrls.add(mList.get(i).getResourceUrl());
            }
            if (!TextUtils.isEmpty(mList.get(i).getTemplateUrl())) {
                mUrls.add(mList.get(i).getTemplateUrl());
            }
        }
        // 公共资源的下载链接
        if (mtexts.size() > 0) {
            for (int i = 0; i < mtexts.size(); i++) {
                final String url = mtexts.get(i);
                //带zip的下载解压
                if (url.endsWith(".zip")) {
                    final String fileName;
                    int index = url.lastIndexOf("/");
                    if (index != -1) {
                        fileName = url.substring(index + 1);
                    } else {
                        fileName = MD5Utils.getMD5(url);
                    }
                    final File save = new File(mPublicCacheout, fileName);
                    if (!fileIsExists(save.getPath())) {
                        final File tempFile = new File(mPublicCacheout, fileName + ".temp");
                        mHttpManager.download(mtexts.get(i), tempFile.getPath(), new DownloadCallBack() {
                            @Override
                            protected void onDownloadSuccess() {
                                boolean renameTo = tempFile.renameTo(save);
                                logger.d("onDownloadSuccess(mtexts zip):fileName=" + fileName + ",renameTo=" + renameTo);
                                new ZipExtractorTask(save, mPublicCacheout, true, new Progresses()).execute();
                            }

                            @Override
                            protected void onDownloadFailed() {
                                logger.d("onDownloadFailed(mtexts zip):fileName=" + fileName);
//                            XESToastUtils.showToast(context, "下载字体包失败");
                            }

                            @Override
                            protected void onDownloadFailed(Exception e) {
                                logger.d("onDownloadFailed " + e);
                                super.onDownloadFailed(e);
                            }
                        });
                    } else {
                        logger.d("fileIsExists(mtexts zip):fileName=" + fileName);
                    }
                } else {
                    final String fileName = MD5Utils.getMD5(url);
                    final File save = new File(mPublicCacheout, fileName);
                    if (!fileIsExists(save.getPath())) {
                        final File tempFile = new File(mPublicCacheout, fileName + ".temp");
                        mHttpManager.download(mtexts.get(i), tempFile.getPath(), new DownloadCallBack() {
                            @Override
                            protected void onDownloadSuccess() {
                                boolean renameTo = tempFile.renameTo(save);
                                logger.d("onDownloadSuccess(mtexts):fileName=" + fileName + ",renameTo=" + renameTo);
                            }

                            @Override
                            protected void onDownloadFailed() {
                                logger.d("onDownloadFailed(mtexts):fileName=" + fileName);
//                            XESToastUtils.showToast(context, "下载字体包失败");
                            }
                        });
                    } else {
                        logger.d("fileIsExists(mtexts):fileName=" + fileName);
                    }
                }
            }
        }
        mUrls.add("https://res17.xesimg.com/like/XiaoXueKeJian/animation/interact-active/right/img_5.png");
        for (int i = 0; i < mUrls.size(); i++) {
            final String url = i + ".zip";
            final File save;
            if (url.contains("img_5.png")) {
                save = new File(mMorecachein, "img_5.png");
            } else {
                save = new File(mMorecachein, url);
            }
            if (!fileIsExists(save.getPath())) {
                final File tempFile = new File(mMorecachein, url + ".temp");
                mHttpManager.download(mUrls.get(i), tempFile.getPath(), new DownloadCallBack() {
                    @Override
                    protected void onDownloadSuccess() {
                        boolean renameTo = tempFile.renameTo(save);
                        logger.d("onDownloadSuccess(mUrls):url=" + url + ",renameTo=" + renameTo);
                        new ZipExtractorTask(new File(mMorecachein, url), mMorecacheout, true, new Progresses()).execute();
                    }

                    @Override
                    protected void onDownloadFailed() {
                        logger.d("onDownloadFailed(mUrls):url=" + url);
//                        XESToastUtils.showToast(context, "下载资源包失败");
                    }
                });
            } else {
                logger.d("fileIsExists(mtexts):fileName=" + url);
            }
        }

    }

    private class Progresses implements ZipProg {
        @Override
        public void onProgressUpdate(Integer... values) {

        }

        @Override
        public void onPostExecute(Exception exception) {

        }

        @Override
        public void setMax(int max) {

        }
    }

    // TODO 理科小学
//    public void scienceStatic() {
//        ScienceStaticConfig scienceStaticConfig = mGetInfo.getScienceStaticConfig();
//        if (scienceStaticConfig != null) {
//            final ScienceStaticConfig.Version version = scienceStaticConfig.stringVersionHashMap.get(ScienceStaticConfig.THIS_VERSION);
//            if (version != null) {
//                File dir = new File(mPublicCacheout, "sciencestatic/" + ScienceStaticConfig.THIS_VERSION);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                int index = version.tarballURL.lastIndexOf("/");
//                String fileName = version.tarballURL.substring(index + 1);
//                int indexdot = fileName.indexOf(".");
//                if (indexdot != -1) {
//                    fileName = fileName.substring(0, indexdot);
//                }
//                final File filesave = new File(dir, fileName + "_save");
//                if (filesave.exists()) {
//                    version.localfile = filesave + "/xiaoxuekejian/local.html";
//                } else {
//                    final File filesaveTmp = new File(dir, fileName + "_savetmp");
//                    final File zipsave = new File(dir, fileName + ".zip");
//                    if (zipsave.exists()) {
//                        try {
//                            String md5Str = MD5.md5(zipsave);
//                            logger.d("scienceStatic:md5Str=" + md5Str + ",assetsHash=" + version.assetsHash);
//                            if (md5Str.equalsIgnoreCase(version.assetsHash)) {
//                                new ZipExtractorTask(zipsave, filesaveTmp, true, new Progresses() {
//                                    @Override
//                                    public void onPostExecute(Exception exception) {
//                                        if (exception == null) {
//                                            boolean renameTo = filesaveTmp.renameTo(filesave);
//                                            if (new File(filesave, "/xiaoxuekejian/local.html").exists()) {
//                                                version.localfile = filesave + "/xiaoxuekejian/local.html";
//                                            }
//                                            logger.d("scienceStatic:onPostExecute:localfile=" + version.localfile + ",renameTo=" + renameTo);
//                                        }
//                                    }
//                                }).execute();
//                                return;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    final File zipsavetmp = new File(dir, fileName + ".ziptmp");
//                    mHttpManager.download(version.tarballURL, zipsavetmp.getPath(), new DownloadCallBack() {
//                        @Override
//                        protected void onDownloadSuccess() {
//                            zipsavetmp.renameTo(zipsave);
//                            new ZipExtractorTask(zipsave, filesaveTmp, true, new Progresses() {
//                                @Override
//                                public void onPostExecute(Exception exception) {
//                                    if (exception == null) {
//                                        boolean renameTo = filesaveTmp.renameTo(filesave);
//                                        if (new File(filesave, "/xiaoxuekejian/local.html").exists()) {
//                                            version.localfile = filesave + "/xiaoxuekejian/local.html";
//                                        }
//                                        logger.d("scienceStatic:onPostExecute:localfile=" + version.localfile + ",renameTo=" + renameTo);
//                                    }
//                                }
//                            }).execute();
//                        }
//
//                        @Override
//                        protected void onDownloadFailed() {
//
//                        }
//
//                        @Override
//                        protected void onDownloadFailed(Exception e) {
//                            logger.d("scienceStatic:download", e);
//                        }
//                    });
//                }
//            }
//        }
//    }

    public void ScienceMulPreDownLoad(final File path) {
        mHttpManager.getMoreCoureWareUrl(liveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("responseEntity.getJsonObject=" + responseEntity.getJsonObject());
                JSONObject objects = new JSONObject(responseEntity.getJsonObject().toString());
                JSONArray array = objects.optJSONArray("list");
                JSONArray res = objects.optJSONArray("resource");
                JSONArray loadpages = objects.optJSONArray("loadpages");
                mList = new ArrayList<>();
                mtexts = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    MoreCache cache = new MoreCache();
                    JSONObject object = array.getJSONObject(i);
                    cache.setPackageId(object.optString("packageId"));
                    cache.setPackageSource(object.optString("packageSource"));
                    cache.setIsTemplate(object.optInt("isTemplate"));
                    cache.setPageId(object.optString("pageId"));
                    cache.setResourceUrl(object.optString("resourceUrl"));
                    cache.setTemplateUrl(object.optString("templateUrl"));
                    mList.add(cache);
                }
                for (int i = 0; i < res.length(); i++) {
                    String txt = res.optString(i);
                    mtexts.add(txt);
                }
                for (int i = 0; i < loadpages.length(); i++) {
                    String txt = loadpages.optString(i);
                    mtexts.add(txt);
                }
                logger.e("list=" + mList.size());
                logger.e("text=" + mtexts.size());
                if (mList.size() > 0) {
                    download(path);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                logger.e("getCourseWareUrl:onFailure:e=" + e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("getCourseWareUrl:onPmError:e=" + responseEntity.getErrorMsg());
            }
        });
    }

    public void chineseMulPreDownLoad(final File path) {
        mHttpManager.getChineseCoureWareUrl(liveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("responseEntity.getJsonObject=" + responseEntity.getJsonObject());
                JSONObject objects = new JSONObject(responseEntity.getJsonObject().toString());
                JSONArray array = objects.optJSONArray("list");
                JSONArray res = objects.optJSONArray("resource");
                JSONArray loadpages = objects.optJSONArray("loadpages");
                mList = new ArrayList<>();
                mtexts = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    MoreCache cache = new MoreCache();
                    JSONObject object = array.getJSONObject(i);
                    cache.setPackageId(object.optString("packageId"));
                    cache.setPackageSource(object.optString("packageSource"));
                    cache.setIsTemplate(object.optInt("isTemplate"));
                    cache.setPageId(object.optString("pageId"));
                    cache.setResourceUrl(object.optString("resourceUrl"));
                    cache.setTemplateUrl(object.optString("templateUrl"));
                    mList.add(cache);
                }
                for (int i = 0; i < res.length(); i++) {
                    String txt = res.optString(i);
                    mtexts.add(txt);
                }
                for (int i = 0; i < loadpages.length(); i++) {
                    String txt = loadpages.optString(i);
                    mtexts.add(txt);
                }
                logger.e("list=" + mList.size());
                logger.e("text=" + mtexts.size());
                if (mList.size() > 0) {
                    download(path);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                logger.e("getCourseWareUrl:onFailure:e=" + e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("getCourseWareUrl:onPmError:e=" + responseEntity.getErrorMsg());
            }
        });
    }

    public void start() {
        isStart = true;
        logger.d("start");
        getCourseWareUrl();
    }

    @Override
    public void stop() {
        isStart = false;
        logger.d("stop");
        handler.removeMessages(1);
        if (cacheReceiver != null) {
            context.unregisterReceiver(cacheReceiver);
            cacheReceiver = null;
        }
        if (useService) {
            Intent intent = new Intent(context, CachePreLoadService.class);
            intent.setPackage(AppConfig.APPLICATION_ID);
            context.stopService(intent);
        } else {
            for (int i = 0; i < cacheWebViews.size(); i++) {
                WebView view = cacheWebViews.get(i);
                view.destroy();
            }
        }
    }

    class CacheReceiver extends BroadcastReceiver {
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> errorUrls = new ArrayList<>();
        ArrayList<String> successUrls = new ArrayList<>();
        File cacheDir;
        boolean isRetry = false;
        int total;

        public CacheReceiver(ArrayList<String> urls, File cacheDir) {
            this.urls = urls;
            this.cacheDir = cacheDir;
            total = urls.size();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", 0);
            String url = intent.getStringExtra("url");
            long time = intent.getLongExtra("time", -1);
            if (status == 0) {
                success(url);
                ifOnEnd();
            } else {
                error(url);
            }
            logger.d("onReceive:status=" + status + ",time=" + time + ",url=" + url);
        }

        private void success(String url) {
            urls.remove(url);
            successUrls.add(url);
            File file = new File(cacheDir, MD5.md5(url));
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.d("onReceive:success:urls=" + urls.size() + ",errorUrls=" + errorUrls.size());
        }

        private void error(String url) {
            urls.remove(url);
            errorUrls.add(url);
            logger.d("onReceive:error:urls=" + urls.size() + ",errorUrls=" + errorUrls.size());
            ifOnEnd();
        }

        private void ifOnEnd() {
            if (urls.isEmpty()) {
                logger.d("onReceive:ifOnEnd:errorUrls=" + errorUrls.size() + ",successUrls=" + successUrls.size() + ",isRetry=" + isRetry);
                if (isRetry) {
                    if (cacheReceiver != null) {
                        context.unregisterReceiver(cacheReceiver);
                        cacheReceiver = null;
                    }
                    Intent intent = new Intent(context, CachePreLoadService.class);
                    intent.setPackage(AppConfig.APPLICATION_ID);
                    context.stopService(intent);
                    Map<String, String> mData = new HashMap<>();
                    mData.put("liveid", liveId);
                    mData.put("times", "2");
                    mData.put("error", "" + errorUrls.size());
                    mData.put("total", "" + total);
                    liveAndBackDebug.umsAgentDebugSys(eventId, mData);
                    ProxUtil.getProxUtil().remove(context, WebViewRequest.class);
                } else {
                    if (errorUrls.isEmpty()) {
                        context.unregisterReceiver(this);
                        Intent intent = new Intent(context, CachePreLoadService.class);
                        intent.setPackage(AppConfig.APPLICATION_ID);
                        context.stopService(intent);
                        cacheReceiver = null;
                        Map<String, String> mData = new HashMap<>();
                        mData.put("liveid", liveId);
                        mData.put("times", "1");
                        mData.put("error", "0");
                        mData.put("total", "" + total);
                        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
                        ProxUtil.getProxUtil().remove(context, WebViewRequest.class);
                    } else {
                        if (netWorkType == NetWorkHelper.NO_NETWORK) {
                            return;
                        } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
                            return;
                        }
                        retry();
                    }
                }
            }
        }

        private void retry() {
            isRetry = true;
            logger.d("retry");
            final ArrayList<String> errorUrls2 = new ArrayList<>();
            errorUrls2.addAll(errorUrls);
            errorUrls.clear();
            int size = errorUrls2.size();
            for (int i = 0; i < size; i++) {
                String url = errorUrls2.get(i);
                Message msg = handler.obtainMessage(1);
                msg.what = 1;
                msg.obj = url;
                handler.sendMessageDelayed(msg, i * 20000);
            }
        }
    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            return;
        } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
            return;
        }
        if (cacheReceiver != null) {
            logger.d("onNetWorkChange:urls=" + cacheReceiver.urls.size() + ",errorUrls=" + cacheReceiver.errorUrls.size());
            if (cacheReceiver.urls.isEmpty() && !cacheReceiver.errorUrls.isEmpty()) {
                cacheReceiver.retry();
            }
        }
    }

    private void loadUrl(final String url) {
        final View view = LayoutInflater.from(context).inflate(R.layout.page_livevideo_h5_courseware_cacheweb, bottomContent, false);
        final CacheWebView cacheWebView = (CacheWebView) view.findViewById(R.id.wv_livevideo_subject_web);
        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NORMAL);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(view, lp);
        cacheWebView.setWebViewClient(new WebViewClient() {
            long before = System.currentTimeMillis();
            String failingUrl;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                int status = failingUrl == null ? 0 : -3;
                Intent intent1 = new Intent(CachePreLoadService.URL_CACHE_ACTION);
                intent1.putExtra("url", url);
                intent1.putExtra("status", status);
                intent1.putExtra("time", (System.currentTimeMillis() - before));
                context.sendBroadcast(intent1);
                cacheWebViews.remove(cacheWebView);
                bottomContent.removeView(view);
                view.destroy();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                this.failingUrl = failingUrl;
            }
        });
        cacheWebView.loadUrl(url);
        logger.i("loadUrl:url=" + url);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cacheWebViews.contains(cacheWebView)) {
                    cacheWebView.destroy();
                    cacheWebViews.remove(cacheWebView);
                    bottomContent.removeView(view);
                    Intent intent1 = new Intent(CachePreLoadService.URL_CACHE_ACTION);
                    intent1.putExtra("url", url);
                    intent1.putExtra("status", -4);
                    long t = 10000;
                    intent1.putExtra("time", t);
                    context.sendBroadcast(intent1);
                }
            }
        }, 10000);
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


//    public class MyWebViewClient extends WebViewClient {
//        String failingUrl;
//
//        @Override
//        public void onPageFinished(final WebView view, String url) {
//            File file2 = new File(context.getCacheDir(), "org.chromium.android_webview");
//            logger.i( "onPageFinished:url=" + url + ",size=" + FileUtils.getDirSize(file2));
////            bottomContent.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    View view1 = (View) view.getParent();
////                    bottomContent.removeView(view1);
////                }
////            }, 3000);
//            view.destroy();
//            View view1 = (View) view.getParent();
//            bottomContent.removeView(view1);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
////            logger.i( "onPageStarted");
//        }
//
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            Loger.d(context, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
//                    "&&," + description, true);
//        }
//
////        @Override
////        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
////            logger.i( "shouldInterceptRequest:url=" + url);
////            return super.shouldInterceptRequest(view, url);
////        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
}

