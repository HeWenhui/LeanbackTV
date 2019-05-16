package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

public class TimeUtils {
    /**
     * 把毫秒转换成：1：20：30这样的形式
     *
     * @param size
     * @return
     */
    public static String stringForTime(long size) {
//        int totalSeconds = timeMs / 1000;
//        int seconds = totalSeconds % 60;
//        int minutes = (totalSeconds / 60) % 60;
//        int hours = totalSeconds / 3600;
////        mFormatBuilder.setLength(0);
//        if (hours > 0) {
//            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
//        } else {
//            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
//        }
        String time;
        if (size < 60) {
            time = String.format("00:%02d", size % 60);
        } else if (size < 3600) {
            time = String.format("%02d:%02d", size / 60, size % 60);
        } else {
            time = String.format("%02d:%02d:%02d", size / 3600, size % 3600 / 60, size % 60);
        }
        return time;
    }

    /**
     * 把毫秒转换成：1时20分30秒这样的形式
     */
    public static String stringForTimeChs(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        String sh = (h < 10 ? "0" : "") + h;
        String sd = (d < 10 ? "0" : "") + d;
        String ss = (s < 10 ? "0" : "") + s;
        StringBuilder stringBuilder = new StringBuilder();
        if (h != 0) {
            stringBuilder.append(sh + "时");
        }
//        if (d != 0) {
        stringBuilder.append(sd + "分");
//        }
//        if (s != 0) {
        stringBuilder.append(ss + "秒");
//        }
        return stringBuilder.toString();
    }
}
