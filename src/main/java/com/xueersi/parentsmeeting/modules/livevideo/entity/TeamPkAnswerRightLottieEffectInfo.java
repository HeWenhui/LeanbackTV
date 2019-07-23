package com.xueersi.parentsmeeting.modules.livevideo.entity;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.are.ContextManager;

/**
 * 战队pk二期 答对超难题 lottie 动效
 *
 * @author chekun
 * created  at 2019/3/28 15:29
 */
public class TeamPkAnswerRightLottieEffectInfo extends LottieEffectInfo {
    private static final String TARGET_IMG = "img_5.png";
    private static final String TEXTCOLOR = "#FF6700";
    private static final int TEXTSIZE = 52;
    /**
     * 能量值
     **/
    private String mEnergyNum;

    public void setEnergyNum(String energyNum) {
        this.mEnergyNum = energyNum;
    }

    public TeamPkAnswerRightLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        if (TARGET_IMG.equals(fileName)) {
            return generateCoinNum(width,height);
        } else {
            return super.fetchTargetBitMap(animationView, fileName, bitmapId, width, height);
        }
    }


    private Bitmap generateCoinNum(int width, int height) {

        Bitmap resultBitmap = null;

        if (!TextUtils.isEmpty(mEnergyNum)) {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor(TEXTCOLOR));
            int textSize = width/mEnergyNum.length();
            textSize = Math.min(textSize,TEXTSIZE);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.LEFT);

            Typeface fontFace = FontCache.getTypeface(ContextManager.getContext(), "fangzhengcuyuan.ttf");
            if (fontFace != null) {
                paint.setTypeface(fontFace);
            }

            Rect fontRect = new Rect();
            paint.getTextBounds(mEnergyNum, 0, mEnergyNum.length(), fontRect);
            int textHeight = fontRect.height();
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int baseLine = (height - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2 - fontMetricsInt.ascent;
            canvas.drawText(mEnergyNum, 0, baseLine, paint);
        }
        return resultBitmap;
    }
}
