package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.os.SystemClock;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.video.URLDNS;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Created by linyuqiang on 2018/9/5.
 * 解析dns
 */
public class DNSUtil {
    static String TAG = "DNSUtil";
    static Logger logger = LoggerFactory.getLogger(TAG);

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
        UnknownHostException exception = null;
//        long before = System.currentTimeMillis();
        long before = SystemClock.elapsedRealtime();
        try {
            urldns.url = url2;
            url2 = getHost(url2);
            logger.d("getDns:url2=" + urldns.url + "," + url2);
            InetAddress inetAddress = InetAddress.getByName(url2);
            urldns.ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            logger.e("getDns", e);
            exception = e;
        }
//        urldns.time = System.currentTimeMillis() - before;
        urldns.time = SystemClock.elapsedRealtime() - before;
        logger.d("getDns:url2=" + urldns.url + ",time=" + urldns.time);
        if (exception != null) {
            throw exception;
        }
    }
}
