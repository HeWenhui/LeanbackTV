//package com.xueersi.parentsmeeting.modules.livevideo.business;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.RelativeLayout;
//
//import com.xueersi.parentsmeeting.http.DownloadCallBack;
//import com.xueersi.parentsmeeting.http.HttpCallBack;
//import com.xueersi.parentsmeeting.http.ResponseEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
//import com.xueersi.xesalib.utils.file.FileUtils;
//import com.xueersi.xesalib.utils.log.Loger;
//import com.xueersi.xesalib.utils.network.NetWorkHelper;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xutils.xutils.common.util.MD5;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//import okhttp3.Call;
//import ren.yale.android.cachewebviewlib.CachePreLoadService;
//import ren.yale.android.cachewebviewlib.CacheWebView;
//import ren.yale.android.cachewebviewlib.WebViewCache;
//import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;
//
///**
// * 英语课件缓存
// * Created by linyuqiang on 2017/12/28.
// */
//public class EnglishH5CacheZip implements EnglishH5CacheAction {
//    String TAG = "EnglishH5CacheZip";
//    String eventId = LiveVideoConfig.LIVE_H5_CACHE;
//    Context context;
//    LiveBll liveBll;
//    String liveId;
//    File zipFile;
//    File unzipFile;
//    RelativeLayout bottomContent;
//    ArrayList<CacheWebView> cacheWebViews = new ArrayList<>();
//    ArrayList<String> startUrls = new ArrayList<String>();
//    /** 网络类型 */
//    private int netWorkType;
//    boolean useService = false;
//    boolean isStart = true;
//
//    public EnglishH5CacheZip(Context context, LiveBll liveBll, String liveId) {
//        this.context = context;
//        Activity activity = (Activity) context;
//        bottomContent = (RelativeLayout) activity.findViewById(R.id.rl_course_video_live_question_content);
//        this.liveBll = liveBll;
//        this.liveId = liveId;
//        zipFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/zip");
//        unzipFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/unzip");
////        cacheFile = new File(context.getCacheDir(), "cache/webviewCache");
//        if (!zipFile.exists()) {
//            zipFile.mkdirs();
//        }
//        if (!unzipFile.exists()) {
//            unzipFile.mkdirs();
//        }
//    }
//
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                String url = (String) msg.obj;
//                int netWorkType = NetWorkHelper.getNetWorkState(context);
//            }
//        }
//    };
//
//    @Override
//    public void getCourseWareUrl() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//        Date date = new Date();
//        final String today = dateFormat.format(date);
//        final File todayZipDir = new File(zipFile, today);
//        if (!todayZipDir.exists()) {
//            todayZipDir.mkdirs();
//        }
//        final File todayUnZipDir = new File(unzipFile, today);
//        if (!todayUnZipDir.exists()) {
//            todayUnZipDir.mkdirs();
//        }
//        liveBll.getCourseWareUrl(new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                if (responseEntity.getJsonObject() instanceof JSONArray) {
//                    return;
//                }
//                if (!isStart) {
//                    return;
//                }
//                new Thread() {
//                    @Override
//                    public void run() {
//                        File files[] = zipFile.listFiles();
//                        if (files != null) {
//                            for (int i = 0; i < files.length; i++) {
//                                File delectFile = files[i];
//                                if (!delectFile.getPath().equals(todayZipDir.getPath())) {
//                                    if (delectFile.isDirectory()) {
//                                        FileUtils.deleteDir(delectFile);
//                                    } else {
//                                        FileUtils.deleteFile(delectFile);
//                                    }
//                                }
//                            }
//                        }
//                        files = unzipFile.listFiles();
//                        if (files != null) {
//                            for (int i = 0; i < files.length; i++) {
//                                File delectFile = files[i];
//                                if (!delectFile.getPath().equals(todayUnZipDir.getPath())) {
//                                    if (delectFile.isDirectory()) {
//                                        FileUtils.deleteDir(delectFile);
//                                    } else {
//                                        FileUtils.deleteFile(delectFile);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }.start();
//                final JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                Loger.d(TAG, "getCourseWareUrl:onPmSuccess:jsonObject=" + jsonObject);
//                try {
//                    JSONObject liveIdObj = jsonObject.getJSONObject(liveId);
//                    JSONArray urlArray = liveIdObj.getJSONArray("url");
//                    final ArrayList<String> urls = new ArrayList<>();
//                    final ArrayList<String> zipUrls = new ArrayList<>();
//                    for (int i = 0; i < urlArray.length(); i++) {
//                        String play_url = urlArray.getString(i);
//                        int index = play_url.indexOf("/index.html");
//                        String startUrl = play_url.substring(0, index);
//                        startUrls.add(startUrl);
//                        String fileUrl = play_url.replace("/index.html", ".zip");
//                        index = fileUrl.lastIndexOf("/");
//                        final String saveName = fileUrl.substring(index + 1);
//                        final File saveFile = new File(todayZipDir, saveName);
//                        liveBll.download(fileUrl, saveFile.getPath(), new DownloadCallBack() {
//                            @Override
//                            protected void onDownloadSuccess() {
//                                Loger.d(TAG, "onDownloadSuccess:saveFile=" + saveFile.length());
//                                ZipExtractorTask zipExtractorTask = new ZipExtractorTask(saveFile, new File(todayUnZipDir, saveName.replace(".", "")), context, true);
//                                zipExtractorTask.execute();
//                                File[] files = saveFile.listFiles();
//                                if (files != null) {
//                                    for (int i = 0; i < files.length; i++) {
//                                        File file = files[i];
//                                        String s = file.getPath().substring(saveFile.getPath().length());
//                                        Loger.d(TAG, "onDownloadSuccess:s=" + s);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            protected void onDownloadFailed() {
//                                Loger.d(TAG, "onDownloadFailed:saveFile=" + saveFile.length());
//                            }
//                        });
//                        zipUrls.add(fileUrl);
//                    }
//                    Loger.d(TAG, "getCourseWareUrl:onPmSuccess:urlArray=" + urlArray.length() + ",urls=" + urls.size());
//                    JSONArray infoArray = liveIdObj.getJSONArray("infos");
//                    for (int i = 0; i < infoArray.length(); i++) {
//                        JSONObject infoObj = infoArray.getJSONObject(i);
//                        String id = infoObj.getString("id");
//                        String courseware_type = infoObj.getString("type");
//                        String play_url = "https://live.xueersi.com/Live/coursewareH5/" + liveId + "/" + id + "/" + courseware_type
//                                + "/123456";
//                        Loger.d(TAG, "getCourseWareUrl:onPmSuccess:play_url=" + play_url);
////                        urls.add(play_url);
//                    }
//                    if (urls.isEmpty()) {
//                        if (context instanceof WebViewRequest) {
//                            WebViewRequest webViewRequest = (WebViewRequest) context;
//                            webViewRequest.onWebViewEnd();
//                        }
//                    }
//                } catch (JSONException e) {
//                    Loger.e(TAG, "onPmSuccess", e);
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                super.onFailure(call, e);
//                Loger.e(TAG, "getCourseWareUrl:onFailure:e=" + e);
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                super.onPmError(responseEntity);
//                Loger.e(TAG, "getCourseWareUrl:onPmError:e=" + responseEntity.getErrorMsg());
//            }
//        });
//    }
//
//    public void start() {
//        isStart = true;
//        Loger.d(TAG, "start");
//        getCourseWareUrl();
//    }
//
//    public void stop() {
//        isStart = false;
//        Loger.d(TAG, "stop");
//        handler.removeMessages(1);
//    }
//
//    public void onNetWorkChange(int netWorkType) {
//        this.netWorkType = netWorkType;
//        if (netWorkType == NetWorkHelper.NO_NETWORK) {
//            return;
//        } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
//            return;
//        }
//    }
//}