package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.PaintTextUtil;

import java.io.IOException;

/**
 * Created by linyuqiang on 2018/3/23.
 * 站立直播背景Lottie动画，本场成就
 */
public class StandLiveLottieAnimationView extends LottieAnimationView {
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    int goldCount = -1;
    int starCount = -1;
    Paint paint;
    private String TAG = "StandLiveLottieAnimationView";
    Typeface fontFace;

    public StandLiveLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize(24);
        paint.setColor(Color.WHITE);
        fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
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
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/jindu/img_9.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(24);
            paint.setTypeface(fontFace);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            float width = paint.measureText(num);
            int baseline = PaintTextUtil.getBaseline(img_7Bitmap.getHeight(), paint);
            canvas.drawText(num, img_7Bitmap.getWidth() - width - 20, baseline, paint);
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e( "setGoldCount", e);
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
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/jindu/img_3.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            if (num.length() < 3) {
                paint.setTextSize(24);
            } else {
                paint.setTextSize(22);
            }
            paint.setTypeface(fontFace);
            paint.setAntiAlias(true);
            paint.setColor(0xff8C4302);
            float width = paint.measureText(num);
            int baseline = PaintTextUtil.getBaseline(img_7Bitmap.getHeight(), paint);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, baseline, paint);
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e( "setStarCount", e);
            return;
        }
        updateBitmap("image_3", img_7Bitmap);
    }
}
