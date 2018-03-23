package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;

/**
 * Created by lyqai on 2018/3/23.
 */

public class StandLiveLottieAnimationView extends LottieAnimationView {
    int goldCount;
    Paint paint;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("" + goldCount, 100, 100, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
