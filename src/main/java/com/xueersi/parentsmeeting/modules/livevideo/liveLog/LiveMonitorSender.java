package com.xueersi.parentsmeeting.modules.livevideo.liveLog;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * @ClassName LiveMonitorSender
 * @Description 直播自检日志发送器
 * @Author lizheng
 * @Date 2019-11-01 17:40
 * @Version 1.0
 */
public class LiveMonitorSender {

    public static final String UP_URL = "https://log.xescdn.com/log";

    private static HashMap<String, String> getActionHeader() {

        HashMap<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json"); //二进制上传
        return map;
    }


    public static void send(String log) {
        send(UP_URL, new ByteArrayInputStream(log.getBytes()), null);
    }

    private static boolean send(String url, InputStream inputData, Map<String, String> headerMap) {

        boolean data = false;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection c = null;
        ByteArrayOutputStream back;
        byte[] Buffer = new byte[2048];
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            if (c instanceof HttpsURLConnection) {
                ((HttpsURLConnection) c).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            if (headerMap != null) {
                Set<Map.Entry<String, String>> entrySet = headerMap.entrySet();
                for (Map.Entry<String, String> tempEntry : entrySet) {
                    c.addRequestProperty(tempEntry.getKey(), tempEntry.getValue());
                }
            }
            HashMap<String, String> actionHeader = getActionHeader();

            Set<Map.Entry<String, String>> actionEntries = actionHeader.entrySet();
            //add base head params
            for (Map.Entry<String, String> actionEntry : actionEntries) {
                c.addRequestProperty(actionEntry.getKey(), actionEntry.getValue());
            }
            c.setReadTimeout(15000);
            c.setConnectTimeout(60);
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            outputStream = c.getOutputStream();
            int i;
            Log.e("LiveMonitorSender", "read start-------");
            while ((i = inputData.read(Buffer)) != -1) {
                //Log.e("stone", "buffer:-------pre");
                outputStream.write(Buffer, 0, i);
                //Log.e("stone", "buffer:-------end");
            }
            outputStream.flush();
            int res = c.getResponseCode();
            if (res == 200) {
                Log.e("LiveMonitorSender", "res code:------200,url:" + url);
                data = true;
            } else {
                Log.e("LiveMonitorSender", "res code:-----error,code=" + res);
            }

        } catch (ProtocolException e) {
            Log.e("LiveMonitorSender", "error -------"+e.getMessage());
        } catch (MalformedURLException e) {
            Log.e("LiveMonitorSender", "error -------"+e.getMessage());
        } catch (Exception e) {
            Log.e("LiveMonitorSender", "error -------"+e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputData != null) {
                try {
                    inputData.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (c != null) {
                c.disconnect();
            }
        }
        return data;
    }

}
