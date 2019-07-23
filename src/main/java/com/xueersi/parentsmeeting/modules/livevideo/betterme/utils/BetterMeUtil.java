package com.xueersi.parentsmeeting.modules.livevideo.betterme.utils;

/**
 * @Date on 2019/7/15 20:51
 * @Author zhangyuansun
 * @Description
 */
public class BetterMeUtil {
    public static String secondToMinite(String second) {
        try {
            double doubleSecond = Double.valueOf(second);
            int intSecond = (int) (Math.round(doubleSecond));
            int min = intSecond / 60;
            int sec = intSecond % 60;
            String stringSec = "" + sec;
            if (sec < 10) {
                stringSec = "0" + sec;
            }
            return min + ":" + stringSec;
        } catch (Exception e) {
            return second;
        }
    }
}
