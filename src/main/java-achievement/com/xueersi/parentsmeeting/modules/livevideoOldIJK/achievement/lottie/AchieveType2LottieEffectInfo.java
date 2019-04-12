package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.lottie;

import android.app.Activity;
import android.graphics.Bitmap;

import com.airbnb.lottie.LottieAnimationView;

public class AchieveType2LottieEffectInfo extends AchieveLottieEffectInfo {
    int energyCountAdd;

    public AchieveType2LottieEffectInfo(Activity activity, int energyCountAdd, String imgDir, String jsonFilePath) {
        super(activity, imgDir, jsonFilePath, "img_1.png");
        this.energyCountAdd = energyCountAdd;
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
        if ("img_1.png".equals(fileName)) {
            Bitmap bitmap2 = createBitmap(energyCountAdd, width, height);
            if (bitmap2 != null) {
                return bitmap2;
            }
        }
        return null;
    }

}
