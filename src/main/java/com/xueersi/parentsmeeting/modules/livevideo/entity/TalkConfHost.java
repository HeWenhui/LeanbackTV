package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.xesalib.utils.log.Loger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lyqai
 * @date 2018/5/22
 */
public class TalkConfHost {
    private static String TAG = "TalkConfHost";
    private String host;
    private boolean isIp = false;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        String ip = getCookieDomain(host);
        if (ip != null) {
            isIp = true;
        }
//        getCookieDomain("10.99.1.86");
//        getCookieDomain("http://10.99.1.86/getserver");
    }

    public boolean isIp() {
        return isIp;
    }

    public void setIp(boolean ip) {
        isIp = ip;
    }

    public static String PATTERN_IP = "(\\d*\\.){3}\\d*";

    public static String getCookieDomain(String url) {
         /* 以IP形式访问时，返回IP */
        Pattern ipPattern = Pattern.compile(PATTERN_IP);
        Matcher matcher = ipPattern.matcher(url);
        if (matcher.find()) {
            String ip = matcher.group();
            Loger.d(TAG, "getCookieDomain:ip=" + ip);
            return ip;
        }
        Loger.d(TAG, "getCookieDomain:url=" + url);
        return null;
    }
}
