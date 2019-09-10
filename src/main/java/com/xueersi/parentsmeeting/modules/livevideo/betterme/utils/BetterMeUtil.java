package com.xueersi.parentsmeeting.modules.livevideo.betterme.utils;

import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;

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

    public static void addSegment(ImageView imageView, int segmentType, int star) {
        if (segmentType < 1 || segmentType > 6) {
            imageView.setImageResource(R.drawable.app_livevideo_enteampk_morentouxiang_bg_img_nor);
        } else if (star < 1 || star > BetterMeConfig.LEVEL_UPLEVEL_STARS[segmentType - 1]) {
            imageView.setImageResource(R.drawable.app_livevideo_enteampk_morentouxiang_bg_img_nor);
        } else {
            imageView.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_HEAD[segmentType - 1]);
            imageView.setImageResource(BetterMeConfig.STAR_IMAGE_RES[segmentType - 1][star - 1]);
        }
    }
}
