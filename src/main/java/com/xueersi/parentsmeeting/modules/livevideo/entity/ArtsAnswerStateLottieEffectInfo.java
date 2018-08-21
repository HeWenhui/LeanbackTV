package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文科答题统计面板
 *
 * @author chenkun
 * @version 1.0, 2018/8/7 上午10:36
 */

public class ArtsAnswerStateLottieEffectInfo extends LottieEffectInfo {

    private String mCoinStr;

    private static final String TEXTCOLOR = "#FFDB2A";
    private static final int TEXTSIZE = 25;

    private static final String TITLE_FILE_NAME = "img_15.png";
    private static final String TITLE_BG_FILE_NAME = "img_16.png";
    private static final String COIN_FILE_NAME = "img_14.png";
    private String mTitlePath;
    private String mTitleBgPath;


    public ArtsAnswerStateLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int
            height) {
        Bitmap resultBitmap = null;
        if (COIN_FILE_NAME.equals(fileName)) {
            resultBitmap = generateCoinNum(width, height);
        } else if (TITLE_FILE_NAME.equals(fileName)) {
            resultBitmap = getBitMap(animationView.getContext(),mTitlePath);
        } else if (TITLE_BG_FILE_NAME.equals(fileName)) {
            resultBitmap = getBitMap(animationView.getContext(),mTitleBgPath);

        }
        return resultBitmap;
    }


    private Bitmap getBitMap(Context context,String path) {
        Bitmap resultBitMap = null;
        InputStream in = null;
        try {
            in = context.getAssets().open(path);
            resultBitMap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultBitMap;
    }

    /**
     * 设置title 图片路径
     *
     * @param filePath
     */
    public void setTilteFilePath(String filePath) {

        mTitlePath = filePath;

    }

    /**
     * 设置title 背景 图片路径
     *
     * @param filePath
     */
    public void setTitleBgFilePath(String filePath) {

        mTitleBgPath = filePath;

    }


    public void setCoinStr(String coinStr) {
        this.mCoinStr = coinStr;
    }


    private Bitmap generateCoinNum(int width, int height) {

        Bitmap resultBitmap = null;

        if (!TextUtils.isEmpty(mCoinStr)) {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor(TEXTCOLOR));
            paint.setTextSize(TEXTSIZE);
            paint.setTextAlign(Paint.Align.LEFT);


            Rect fontRect = new Rect();
            paint.getTextBounds(mCoinStr, 0, mCoinStr.length(), fontRect);
            int textHeight = fontRect.height();
            Rect drawRect = null;
            drawRect = new Rect(0, 0, width, height - textHeight / 2);
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int baseLine = (drawRect.bottom + drawRect.bottom - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            canvas.drawText(mCoinStr, 0, baseLine, paint);
        }
        return resultBitmap;
    }
}
