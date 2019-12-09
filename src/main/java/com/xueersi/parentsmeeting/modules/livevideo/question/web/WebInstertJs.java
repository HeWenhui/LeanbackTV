package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.util.Log;

import com.airbnb.lottie.AssertUtil;
import com.xueersi.common.base.XrsCrashReport;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.WebTrustVerifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2019/3/5.
 * 往html中嵌入js
 */
public class WebInstertJs {
    String TAG = "WebInstertJs";
    Logger logger;
    Context context;
    File cacheDir;
    File innerFile;
    static long saveTime;
    private LogToFile logToFile;
    OnHttpCode onHttpCode;

    public WebInstertJs(Context context, String testid) {
        logToFile = new LogToFile(context, TAG);
        try {
            logToFile.addCommon("testid", testid);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        this.context = context;
        logger = LiveLoggerFactory.getLogger(TAG);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        cacheDir = LiveCacheFile.geCacheFile(context, "webviewCache/" + today);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        innerFile = new File(context.getCacheDir(), "webviewCache/" + today);
        if (!innerFile.exists()) {
            innerFile.mkdirs();
        }
        if (saveTime == 0) {
            saveTime = System.currentTimeMillis() / 60000;
        }
    }

    public OnHttpCode getOnHttpCode() {
        return onHttpCode;
    }

    public void setOnHttpCode(OnHttpCode onHttpCode) {
        this.onHttpCode = onHttpCode;
    }

    private InputStream insertJs(String url, File cacheDir, InputStream inputStream) throws Exception {
        String fileName = "index_" + url.hashCode() + "_" + saveTime + ".html";
        File saveFile = new File(cacheDir, fileName);
        logToFile.d("insertJs:fileName=" + saveFile + ",exists=" + saveFile.exists());
        if (saveFile.exists()) {
            return new FileInputStream(saveFile);
        }
        File saveFileTmp = new File(cacheDir, fileName + "tmp");
        FileOutputStream outputStream = new FileOutputStream(saveFileTmp);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        boolean addJs = false;
//                final String indexJs = "<script type=text/javascript crossorigin=anonymous src=" + "file://" + saveIndex().getPath() + "></script>";
        final String indexJs = "<script type=text/javascript src=." + indexStr() + "></script>";
        String findStr = "</script>";
        while ((line = br.readLine()) != null) {
//                    outputStream.write(line.getBytes());
            //找到第一个script标签。在后面添加自己的js
            if (!addJs) {
                int index = line.indexOf(findStr);
                if (index != -1) {
                    if (index == line.length() - 1) {
                        line = line + "\n" + indexJs;
                    } else {
                        line = line.substring(0, index + findStr.length()) + "\n" + indexJs + "\n" + line.substring(index + findStr.length());
                    }
                    logToFile.d("httpRequest:insertJs=" + line);
                    XrsCrashReport.d(TAG, "httpRequest:insertJs=" + line);
                    addJs = true;
                }
            }
            bufferedWriter.write(line + "\n");
            stringBuilder.append(line + "\n");
        }
        bufferedWriter.flush();
        boolean renameTo = saveFileTmp.renameTo(saveFile);
        logToFile.d("insertJs:fileName=" + fileName + ",renameTo=" + renameTo);
        return new FileInputStream(saveFile);
    }

    private InputStream insertJs(String url, InputStream inputStream) throws Exception {
        return insertJs(url, cacheDir, inputStream);
    }

    public InputStream readFile(String url, File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return insertJs(url, fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream httpRequestTry(String url, AtomicBoolean islocal) {
        String fileName = "index_" + url.hashCode() + "_" + saveTime + ".html";
        ArrayList<File> cacheDirs = new ArrayList<>();
        cacheDirs.add(cacheDir);
        cacheDirs.add(innerFile);
        //文件失败，需要换路径
        boolean fileError = false;
        for (int i = 0; i < cacheDirs.size(); i++) {
            File dir = cacheDirs.get(i);
            if (i == 1) {
                if (fileError) {
                    dir = cacheDirs.get(i);
                } else {
                    dir = cacheDirs.get(0);
                }
            }
            File saveFile = new File(dir, fileName);
            logToFile.d("httpRequestTry:i=" + i + ",fileError=" + fileError + ",fileName=" + saveFile + ",exists=" + saveFile.exists());
            if (saveFile.exists()) {
                try {
                    if (islocal != null) {
                        islocal.set(true);
                    }
                    return new FileInputStream(saveFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            HttpURLConnection httpURLConnection = null;
            boolean isFail = false;
            Exception dnsException = new Exception();
            try {
                WebTrustVerifier.trustVerifier();
                URL oldUrl = new URL(url);
                URL urlRequest = new URL(url);
                httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setReadTimeout(30000);

                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "httpRequest:responseCode=" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    return insertJs(url, dir, inputStream);
//                return null;
                } else {
                    if (onHttpCode != null) {
                        onHttpCode.onHttpCode(url, responseCode);
                    }
                    dnsException = new Exception("responseCode=" + responseCode);
                }
            } catch (MalformedURLException e) {
                dnsException = e;
                e.printStackTrace();
            } catch (UnknownHostException e) {
                dnsException = e;
                e.printStackTrace();
            } catch (IOException e) {
                dnsException = e;
                e.printStackTrace();
            } catch (Exception e) {
                dnsException = e;
                e.printStackTrace();
            } finally {

            }
            if (dnsException instanceof UnknownHostException) {
                logToFile.d("httpRequestTry:i=" + i + ",UnknownHostException");
            } else {
                logToFile.e("httpRequestTry:i=" + i, dnsException);
                if (dnsException instanceof java.io.FileNotFoundException) {
                    fileError = true;
                }
            }
        }
        return null;
    }

    public InputStream httpRequest(String url, AtomicBoolean islocal) {
        String fileName = "index_" + url.hashCode() + "_" + saveTime + ".html";
        File saveFile = new File(cacheDir, fileName);
        logToFile.d("httpRequest:fileName=" + saveFile + ",exists=" + saveFile.exists());
        if (saveFile.exists()) {
            try {
                if (islocal != null) {
                    islocal.set(true);
                }
                return new FileInputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        HttpURLConnection httpURLConnection = null;
        boolean isFail = false;
        Exception dnsException = new Exception();
        try {
            WebTrustVerifier.trustVerifier();
            URL oldUrl = new URL(url);
            URL urlRequest = new URL(url);
            httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);

            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "httpRequest:responseCode=" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                return insertJs(url, inputStream);
//                return null;
            } else {
                if (onHttpCode != null) {
                    onHttpCode.onHttpCode(url, responseCode);
                }
                dnsException = new Exception("responseCode=" + responseCode);
            }
        } catch (MalformedURLException e) {
            dnsException = e;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            dnsException = e;
            e.printStackTrace();
        } catch (IOException e) {
            dnsException = e;
            e.printStackTrace();
        } catch (Exception e) {
            dnsException = e;
            e.printStackTrace();
        } finally {

        }
        if (dnsException instanceof UnknownHostException) {
            logToFile.d("httpRequest:UnknownHostException");
        } else {
            logToFile.e("httpRequest", dnsException);
        }
        return null;
    }

    public InputStream indexStream() {
        InputStream inputStream = null;
        try {
            inputStream = AssertUtil.open("webview_postmessage/index.js");
            return new WrapInputStream(context, inputStream);
//            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String indexStr() {
        return "/android/courseware/index.js";
    }
}

