package com.xueersi.parentsmeeting.modules.livevideo.achievement.lottie;

import android.app.Activity;
import android.graphics.Bitmap;

import com.airbnb.lottie.LottieAnimationView;

public class AchieveType3LottieEffectInfo extends AchieveLottieEffectInfo {
    int goldCountAdd;

    public AchieveType3LottieEffectInfo(Activity activity, int goldCountAdd, String imgDir, String jsonFilePath) {
        super(activity, imgDir, jsonFilePath, "img_1.png");
        this.goldCountAdd = goldCountAdd;
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
        if ("img_1.png".equals(fileName)) {
            Bitmap bitmap2 = createBitmap(goldCountAdd, width, height);
            if (bitmap2 != null) {
                return bitmap2;
            }
        }
        return null;
    }

}
