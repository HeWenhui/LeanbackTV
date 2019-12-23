package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;

import java.util.ArrayList;
import java.util.List;

public class TeamPkResultLottieEffectInfo extends LottieEffectInfo {
    private static final String TAG = "TeamPkResultLottieEffectInfo";
    private int textColor;

    public static class DetailIfo {
        String fileName;
        String value;
    }

    public static class TextSizeInfo{
        String fileName;
        int   textSize;
    }

    private List<TextSizeInfo> textSizeInfos = new ArrayList<TextSizeInfo>();

    private List<DetailIfo> teacherNameInfoList = new ArrayList<DetailIfo>();
    private List<DetailIfo> sloganInfoList = new ArrayList<DetailIfo>();
    private List<DetailIfo> logoInfoList = new ArrayList<DetailIfo>();
    private List<DetailIfo> teacherHeadInfoList = new ArrayList<DetailIfo>();

    public void setTextSize(String fileName,int textSize){
        TextSizeInfo textSizeInfo = new TextSizeInfo();
        textSizeInfo.fileName = fileName;
        textSizeInfo.textSize = textSize;
        textSizeInfos.add(textSizeInfo);
    }





    public void addTeacherName(String fileName, String value) {
        DetailIfo ifo = new DetailIfo();
        ifo.fileName = fileName;
        ifo.value = value;
        teacherNameInfoList.add(ifo);
    }


    public void addSlogan(String fileName, String value) {
        DetailIfo ifo = new DetailIfo();
        ifo.fileName = fileName;
        ifo.value = value;
        sloganInfoList.add(ifo);
    }


    public void addLogo(String fileName, String value) {
        DetailIfo ifo = new DetailIfo();
        ifo.fileName = fileName;
        ifo.value = value;
        logoInfoList.add(ifo);
    }

    public void addTeacherHead(String fileName, String value) {
        DetailIfo ifo = new DetailIfo();
        ifo.fileName = fileName;
        ifo.value = value;
        teacherHeadInfoList.add(ifo);
    }


    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    public TeamPkResultLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
        DetailIfo info;
        if ((info = getTeacherNameInfo(fileName)) != null) {
            Log.e("PkResult","======>fetchTargetBitMap:fileName="+fileName);
            return createMsgBitmap(width, height, info.value, getTextSize(fileName), textColor);
        } else if ((info = getSlogan(fileName)) != null) {
            return createMsgBitmap(width, height, info.value, getTextSize(fileName), textColor);
        } else if ((info = getLogo(fileName)) != null) {
            upDateLottieBitmap(animationView, bitmapId, info.value, width, height, false);
        } else if ((info = getTeacherHead(fileName)) != null) {
            upDateLottieBitmap(animationView, bitmapId, info.value, width, height, true);
        }
        return null;
    }

    /**
     * 获取字体大小
     * @param fileName
     * @return
     */
    private int getTextSize(String fileName) {
        int textSize = 0;
        TextSizeInfo sizeInfo ;
        for (int i = 0; i < textSizeInfos.size(); i++) {
            sizeInfo = textSizeInfos.get(i);
            if(sizeInfo.fileName.equals(fileName)){
                textSize = sizeInfo.textSize;
                break;
            }
        }
        Log.e("TextSize","====>getTextSize:"+textSize);
        return textSize;
    }

    /**
     * 网络加载lottie 图片并进行更新
     *
     * @param animationView
     * @param bitmapId
     * @param url
     * @param width
     * @param height
     */
    private void upDateLottieBitmap(final LottieAnimationView animationView, final String bitmapId,
                                    String url, final int width, final int height, boolean modify) {

        if (!modify) {
            ImageLoader.with(animationView.getContext()).load(url).asBitmap(new SingleConfig.BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap resultBitmap = null;
                    Bitmap bitmap = DrawableHelper.drawable2bitmap(drawable);
                    if(bitmap != null){
                        float ratio = width / (float)bitmap.getWidth();
                        resultBitmap = scaleBitmap(bitmap,ratio);
                    }
                    animationView.updateBitmap(bitmapId, resultBitmap);
                }

                @Override
                public void onFail() {
                }
            });
        } else {
            ImageLoader.with(animationView.getContext()).load(url).asBitmap(new SingleConfig.BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap resultBitmap = DrawableHelper.drawable2bitmap(drawable);
                    Bitmap tempBitmap = circleBitmap(resultBitmap, Math.min(width, height) / 2);
                    animationView.updateBitmap(bitmapId, tempBitmap);
                }

                @Override
                public void onFail() {
                }
            });
        }

    }

    private DetailIfo getTeacherHead(String fileName) {
        DetailIfo result = null;
        if (teacherHeadInfoList.size() > 0) {
            for (int i = 0; i < teacherHeadInfoList.size(); i++) {
                if (teacherHeadInfoList.get(i).fileName.equals(fileName)) {
                    result = teacherHeadInfoList.get(i);
                    break;
                }
            }
        }
        return result;
    }

    private DetailIfo getLogo(String fileName) {
        DetailIfo result = null;
        if (logoInfoList.size() > 0) {
            for (int i = 0; i < logoInfoList.size(); i++) {
                if (logoInfoList.get(i).fileName.equals(fileName)) {
                    result = logoInfoList.get(i);
                    break;
                }
            }
        }
        return result;
    }

    private DetailIfo getSlogan(String fileName) {
        DetailIfo result = null;
        if (sloganInfoList.size() > 0) {
            for (int i = 0; i < sloganInfoList.size(); i++) {
                if (sloganInfoList.get(i).fileName.equals(fileName)) {
                    result = sloganInfoList.get(i);
                    break;
                }
            }
        }
        return result;
    }

    private DetailIfo getTeacherNameInfo(String fileName) {
        DetailIfo result = null;
        if (teacherNameInfoList.size() > 0) {
            for (int i = 0; i < teacherNameInfoList.size(); i++) {
                if (teacherNameInfoList.get(i).fileName.equals(fileName)) {
                    result = teacherNameInfoList.get(i);
                    break;
                }
            }
        }
        return result;
    }


    private Bitmap createMsgBitmap(int width, int height, String msg, int textSize, int textColor) {
        Bitmap resultBitmap = null;
        if (TextUtils.isEmpty(msg)) {
            return resultBitmap;
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        // measuer
        String singleCharacter = msg.substring(0, 1);
        float characterWidth = paint.measureText(singleCharacter);
        // 一行能放几个字儿
        int lineNum = (int) (width / characterWidth);
        Log.e("PkResult","======>createMsgBitmap:"+width+":"+characterWidth+":"+lineNum);

        Rect fontRect;
        List<String> stringList = getStrList(msg, lineNum);
        //最多显示2行
        if (stringList.size() > 2) {
            String line_1 = stringList.get(0);
            String line_2 = stringList.get(1);
            stringList.clear();
            stringList.add(line_1);
            stringList.add(line_2);
        }
        int size = height / stringList.size();
        resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Rect drawRect = null;
        Canvas canvas = new Canvas(resultBitmap);
        for (int i = 0; i < stringList.size(); i++) {
            fontRect = new Rect();
            paint.getTextBounds(stringList.get(i), 0, stringList.get(i).length(), fontRect);
            int textWidth = fontRect.width();
            int textHeight = fontRect.height();
            // int offsetX = (width - textWidth)/2;//无需居中显示
            if (drawRect == null) {
                drawRect = new Rect(0, 0, width, height / stringList.size() - textHeight / 2);
            } else {
                drawRect.left = 0;
                drawRect.top = drawRect.bottom;
                drawRect.bottom = drawRect.top + height / stringList.size();
            }
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int baseLine = (drawRect.bottom + drawRect.bottom - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            canvas.drawText(stringList.get(i), 0, baseLine, paint);
        }
        return resultBitmap;
    }


    private List<String> getStrList(String text, int length) {
        int size = text.length() / length;
        if (text.length() % length != 0) {
            size += 1;
        }
        return getStrList(text, length, size);
    }

    private List<String> getStrList(String text, int length, int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(text, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    private String substring(String text, int f, int t) {
        if (f > text.length()){
            return null;
        }
        if (t > text.length()) {
            return text.substring(f, text.length());
        } else {
            return text.substring(f, t);
        }
    }
}


