package com.xueersi.parentsmeeting.modules.livevideo.achievement.lottie;

import android.app.Activity;
import android.graphics.Bitmap;

import com.airbnb.lottie.LottieAnimationView;

public class AchieveType1LottieEffectInfo extends AchieveLottieEffectInfo {
    int energyCountAdd;
    int goldCountAdd;

    public AchieveType1LottieEffectInfo(Activity activity, int energyCountAdd, int goldCountAdd, String imgDir, String jsonFilePath) {
        super(activity, imgDir, jsonFilePath, "img_0.png", "img_3.png");
        this.energyCountAdd = energyCountAdd;
        this.goldCountAdd = goldCountAdd;
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
        if ("img_3.png".equals(fileName)) {
            Bitmap bitmap2 = createBitmap(energyCountAdd, width, height);
            if (bitmap2 != null) {
                return bitmap2;
            }
        } else if ("img_0.png".equals(fileName)) {
            Bitmap bitmap2 = createBitmap(goldCountAdd, width, height);
            if (bitmap2 != null) {
                return bitmap2;
            }
        }
        return null;
    }

}
