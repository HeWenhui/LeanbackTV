package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文科答题统计面板,带能量-部分正确
 *
 * @author chenkun
 * @version 1.0, 2018/8/7 上午10:36
 */

public class ArtsAnswerErrorEnergyStateLottieEffectInfo extends LottieEffectInfo {

    private String mCoinStr;
    /** 获得的能量数 */
    private String energyStr;
    protected Logger logger = LoggerFactory.getLogger("ArtsAnswerStateLottieEffectInfo");

    private static final String TEXTCOLOR = "#FFDB2A";
    private static final int TEXTSIZE = 30;

    private static final String COIN_FILE_NAME = "img_21.png";
    private static final String ENERGY_FILE_NAME = "img_20.png";
    private String mTitlePath;
    private String mTitleBgPath;


    public ArtsAnswerErrorEnergyStateLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int
            height) {
        Bitmap resultBitmap = null;
        if (COIN_FILE_NAME.equals(fileName)) {
            resultBitmap = generateCoinNum(mCoinStr, width, height);
        } else if (ENERGY_FILE_NAME.equals(fileName)) {
            resultBitmap = generateCoinNum(energyStr, width, height);
        }
        return resultBitmap;
    }


    private Bitmap getBitMap(Context context, String path) {
        Bitmap resultBitMap = null;
        InputStream in = null;
        try {
            in = AssertUtil.open(path);
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

    public void setEnergyStr(String energyStr) {
        this.energyStr = energyStr;
    }

    private Bitmap generateCoinNum(String str, int width, int height) {

        Bitmap resultBitmap = null;

        if (!TextUtils.isEmpty(str)) {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor(TEXTCOLOR));
            paint.setTextSize(TEXTSIZE);
            paint.setTextAlign(Paint.Align.LEFT);

            Typeface fontFace = FontCache.getTypeface(BaseApplication.getContext(), "fangzhengcuyuan.ttf");
            if (fontFace != null) {
                paint.setTypeface(fontFace);
            }

            Rect fontRect = new Rect();
            paint.getTextBounds(str, 0, str.length(), fontRect);
            int textHeight = fontRect.height();
            logger.e("=====>textHeight:" + textHeight + ":" + height);
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            //int baseLine = (height - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
            int baseLine = (height - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2 - fontMetricsInt.ascent;
            logger.e("=====>baseLine:" + baseLine);
            canvas.drawText(str, 0, baseLine, paint);
        }
        return resultBitmap;
    }
}
