package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.net.Uri;

import com.xueersi.parentsmeeting.modules.livevideo.video.URLDNS;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by linyuqiang on 2018/9/5.
 * 解析dns
 */
public class DNSUtil {
    static String TAG = "DNSUtil";

    public static URLDNS getDns(String url2) throws MalformedURLException, UnknownHostException {
        URLDNS urldns = new URLDNS();
        getDns(urldns, url2);
        return urldns;
    }

    public static String getHost(String url2) {
        int index = url2.indexOf("://");
        if (index != -1) {
            url2 = url2.substring(index + 3);
        }
        index = url2.indexOf("/");
        if (index != -1) {
            url2 = url2.substring(0, index);
        }
        index = url2.indexOf(":");
        if (index != -1) {
            url2 = url2.substring(0, index);
        }
        return url2;
    }

    public static void getDns(URLDNS urldns, String url2) throws UnknownHostException {
        try {
            urldns.url = url2;
            url2 = getHost(url2);
            Loger.d(TAG, "getDns:url2=" + urldns.url + "," + url2);
            long before = System.currentTimeMillis();
            InetAddress inetAddress = InetAddress.getByName(url2);
            urldns.ip = inetAddress.getHostAddress();
            urldns.time = System.currentTimeMillis() - before;
        } catch (UnknownHostException e) {
            Loger.e(TAG, "getDns", e);
            throw e;
        }
    }
}
