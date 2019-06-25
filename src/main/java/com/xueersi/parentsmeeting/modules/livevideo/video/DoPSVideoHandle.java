package com.xueersi.parentsmeeting.modules.livevideo.video;

/**
 * 视频播放相关处理类
 */
public class DoPSVideoHandle {
    /**
     * 去掉Url里面的域名
     *
     * @param url
     * @return
     */
    public static String getPSVideoPath(String url) {
        int len = url.length();
        char lastCh = 'a' ;
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

    public static String getPlayBackVideoPath(String url) {
        String videoPath;
//        String url = mVideoEntity.getVideoPath();
        if (url.contains("http") || url.contains("https")) {
            videoPath = DoPSVideoHandle.getPSVideoPath(url);
        } else {
            videoPath = url;
        }
        return videoPath;
    }
}
