package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.airbnb.lottie.LottieAnimationView;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2018/7/12 下午4:10
 */

public class ArtsPraiseLottieEffectInfo extends LottieEffectInfo {

    private final String TITLE_IMGNAME = "img_0.png";
    private String mTitleStr;

    private static final int TEXT_SIZE = 32;
    private static final String TEXT_COLOR = "#FFF3D1";
    private static final String SHADOW_COLOR = "#A6000000";
    /**
     * 弧型文字 垂直方向上 偏移
     */
    private static final int VERTICAL_OFFSET = 5;

    public ArtsPraiseLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }


    public void setTitle(String title) {
        this.mTitleStr = title;
    }


    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        if (TITLE_IMGNAME.equals(fileName)) {
            return generateTitle(animationView, width, height);
        } else {
            return super.fetchTargetBitMap(animationView, fileName, bitmapId, width, height);

        }
    }

    private Bitmap generateTitle(LottieAnimationView animationView, int width, int height) {
        Bitmap resultBitmap = null;
        if (!TextUtils.isEmpty(mTitleStr)) {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(TEXT_SIZE);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.FILL);
//            Typeface typeface = Typeface.createFromAsset(animationView.getContext().getAssets(), "fangzhengcuyuan.ttf");
//            paint.setTypeface(typeface);
            paint.setColor(Color.parseColor(TEXT_COLOR));
            paint.setShadowLayer(7, 1, 1, Color.parseColor(SHADOW_COLOR));
            paint.setFakeBoldText(true);
            Path path = new Path();

            RectF bgRect = new RectF(0f, 0f, width, height);
            int top = height / 2;
            RectF realRect = new RectF(0, top, width, top + height);

            path.addArc(realRect, 180, 180);

            PathMeasure pathMeasure = new PathMeasure();
            pathMeasure.setPath(path, false);
            float textWidth = paint.measureText(mTitleStr);
            float pathLen = pathMeasure.getLength();
            canvas.drawTextOnPath(mTitleStr, path, (pathLen - textWidth) / 2, -VERTICAL_OFFSET, paint);

        }
        return resultBitmap;
    }

}
