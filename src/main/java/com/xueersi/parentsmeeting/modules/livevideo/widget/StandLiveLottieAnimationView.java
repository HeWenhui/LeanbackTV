package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandAchievementBll;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by lyqai on 2018/3/23.
 */

public class StandLiveLottieAnimationView extends LottieAnimationView {
    int goldCount = -1;
    int starCount = -1;
    Paint paint;
    private String TAG = "StandLiveLottieAnimationView";

    public StandLiveLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize(24);
        paint.setColor(Color.WHITE);
    }

    /**
     * 设置金币数量
     *
     * @param goldCount
     */
    public void setGoldCount(int goldCount) {
        if (this.goldCount == goldCount) {
            return;
        }
        this.goldCount = goldCount;
//        invalidate();
        String num = "" + goldCount;
        AssetManager manager = getContext().getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_9.png"));
            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            paint.setColor(Color.WHITE);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - img_3Bitmap.getWidth() / 2) / 2 + img_3Bitmap.getWidth() / 2 - width / 2, img_7Bitmap.getHeight() / 2 + paint.measureText("a") / 2, paint);
//                    canvas.drawRect(img_9Bitmap.getWidth()/2, 0, img_3Bitmap.getWidth(), img_3Bitmap.getHeight(), paint);
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_9", img_7Bitmap);
    }

    public void setStarCount(int starCount) {
        if (this.starCount == starCount) {
            return;
        }
        this.starCount = starCount;
//        invalidate();
        String num = "" + starCount;
        AssetManager manager = getContext().getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_4.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            paint.setColor(0xff8C4302);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, (img_7Bitmap.getHeight()+ paint.measureText("a")) / 2, paint);
//                    canvas.drawRect(img_9Bitmap.getWidth()/2, 0, img_3Bitmap.getWidth(), img_3Bitmap.getHeight(), paint);
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
//            e.printStackTrace();
            return;
        }
        updateBitmap("image_4", img_7Bitmap);
    }
}
