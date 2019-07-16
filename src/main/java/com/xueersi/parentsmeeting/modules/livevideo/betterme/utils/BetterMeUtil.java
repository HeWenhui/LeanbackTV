package com.xueersi.parentsmeeting.modules.livevideo.betterme.utils;

/**
 * @Date on 2019/7/15 20:51
 * @Author zhangyuansun
 * @Description
 */
public class BetterMeUtil {
    public static String secondToMinite(String second) {
        try {
            int intSecond = Integer.valueOf(second);
            int min = intSecond / 60;
            int sec = intSecond % 60;
            return min + ":" + sec;
        } catch (Exception e) {
            return second;
        }
    }
}
