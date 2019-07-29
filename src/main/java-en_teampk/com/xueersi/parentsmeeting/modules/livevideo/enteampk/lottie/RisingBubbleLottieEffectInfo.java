package com.xueersi.parentsmeeting.modules.livevideo.enteampk.lottie;

import android.content.Context;
import android.graphics.Bitmap;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEnergyBonusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * @Date on 2019/7/8 16:57
 * @Author zhangyuansun
 * @Description 完成目标奖励 上升气泡
 */
public class RisingBubbleLottieEffectInfo extends LottieEffectInfo {
    private Context mContext;
    private BetterMeEnergyBonusEntity entity;
    private static String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/rising_bubble";
    private static String IMAGE_RES_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
    private static String JSON_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";

    public RisingBubbleLottieEffectInfo(Context context, BetterMeEnergyBonusEntity entity) {
        super(IMAGE_RES_PATH, JSON_PATH, "");
        this.mContext = context;
        this.entity = entity;
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        if ("".equals(fileName)) {
            Bitmap bitmap = createBitmap(width, height);
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }

    public Bitmap createBitmap(int width, int height) {
        Bitmap bitmap;
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
