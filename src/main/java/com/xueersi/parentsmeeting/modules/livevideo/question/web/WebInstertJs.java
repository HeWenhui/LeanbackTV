package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.util.Log;
import android.webkit.WebViewClient;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

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

public class WebInstertJs {
    String TAG = "WebInstertJs";
    Logger logger;
    Context context;
    File cacheDir;
    static long saveTime;

    public WebInstertJs(Context context) {
        this.context = context;
        logger = LiveLoggerFactory.getLogger(TAG);
        cacheDir = LiveCacheFile.geCacheFile(context, "webview");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (saveTime == 0) {
            saveTime = System.currentTimeMillis() / 60000;
        }
    }

    private InputStream insertJs(InputStream inputStream) throws Exception {
        String fileName = "index_" + saveTime + ".html";
        File saveFile = new File(cacheDir, fileName);
        logger.d("insertJs:fileName=" + fileName + ",exists=" + saveFile.exists());
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
        final String indexJs = "<script type=text/javascript crossorigin=anonymous src=" + indexStr() + "></script>";
        String findStr = "</script>";
        while ((line = br.readLine()) != null) {
//                    outputStream.write(line.getBytes());
            if (!addJs) {
                int index = line.indexOf(findStr);
                if (index != -1) {
                    if (index == line.length() - 1) {
                        line = line + "\n" + indexJs;
                    } else {
                        line = line.substring(0, index + findStr.length()) + "\n" + indexJs + "\n" + line.substring(index);
                    }
                    Log.d(TAG, "httpRequest:index=" + index);
                    addJs = true;
                }
            }
            bufferedWriter.write(line + "\n");
            stringBuilder.append(line + "\n");
        }
        bufferedWriter.flush();
        boolean renameTo = saveFileTmp.renameTo(saveFile);
        logger.d("insertJs:fileName=" + fileName + ",renameTo=" + renameTo);
        return new FileInputStream(saveFile);
    }

    public InputStream readFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return insertJs(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream httpRequest(String url) {
        String fileName = "index_" + saveTime + ".html";
        File saveFile = new File(cacheDir, fileName);
        logger.d("httpRequest:fileName=" + fileName + ",exists=" + saveFile.exists());
        if (saveFile.exists()) {
            try {
                return new FileInputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        HttpURLConnection httpURLConnection = null;
        boolean isFail = false;
        Exception dnsException = new Exception();
        try {
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
                return insertJs(inputStream);
//                return null;
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
        Log.e(TAG, "httpRequest", dnsException);
        return null;
    }

    public InputStream indexStream() {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("webview_postmessage/index.js");
            return new WrapInputStream(inputStream);
//            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String indexStr() {
        return "https://live.xueersi.com/android/index.js";
    }
}
