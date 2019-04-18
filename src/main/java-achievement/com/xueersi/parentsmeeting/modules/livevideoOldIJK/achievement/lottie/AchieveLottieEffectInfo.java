package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.lottie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

public class AchieveLottieEffectInfo extends LottieEffectInfo {
    protected Activity activity;

    public AchieveLottieEffectInfo(Activity activity, String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
        this.activity = activity;
    }

    public Bitmap createBitmap(int energyCount, int width, int height) {
        try {
            Bitmap drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Typeface fontFace = FontCache.getTypeface(activity, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            paint.setTextSize(height + 5);
            String drawText = "+" + energyCount;
            float w = paint.measureText(drawText);
//            paint.setColor(Color.CYAN);
//            canvas.drawRect(0, 0, width, height, paint);
            paint.setColor(0xff4eacf1);
            canvas.drawText(drawText, (width - w) / 2, height, paint);
            return drawBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
