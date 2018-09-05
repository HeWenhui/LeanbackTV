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

    public static URLDNS getDns(String url2) throws MalformedURLException, UnknownHostException {
        URLDNS urldns = new URLDNS();
        getDns(urldns, url2);
        return urldns;
    }

    public static URLDNS getDns(URLDNS urldns, String url2) throws MalformedURLException, UnknownHostException {
        urldns.url = url2;
        long before = System.currentTimeMillis();
        URL url = new URL(url2);
        InetAddress inetAddress = InetAddress.getByName(url.getHost());
        urldns.ip = inetAddress.getHostAddress();
        urldns.time = System.currentTimeMillis() - before;
        return urldns;
    }
}
