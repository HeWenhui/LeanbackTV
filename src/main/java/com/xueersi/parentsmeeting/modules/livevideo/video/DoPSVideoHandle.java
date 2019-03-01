package com.xueersi.parentsmeeting.modules.livevideo.video;

public class DoPSVideoHandle {
    /**
     * 去掉Url里面的域名
     *
     * @param url
     * @return
     */
    public static String getPSVideoPath(String url) {
        int len = url.length();
        char lastCh = 'a';
        //第一次遇到这种 '/'+字母的形式
        boolean isFirst = true;
        int pos;
        for (pos = 1; pos < len; pos++) {
            char ch = url.charAt(pos);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') && (lastCh == '/')) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    break;
                }
            }
            lastCh = ch;
        }
        return url.substring(pos - 1);
    }
}
