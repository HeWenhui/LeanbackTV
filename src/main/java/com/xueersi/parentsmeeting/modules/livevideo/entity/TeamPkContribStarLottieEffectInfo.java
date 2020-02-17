package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 战队pk二期 贡献之星lottie 动效数据
 *
 * @author chekun
 * created  at 2019/2/13 12:58
 */
public class TeamPkContribStarLottieEffectInfo extends LottieEffectInfo {

    /**
     * 名字 字体颜色
     **/
    private int nameTextColor;
    /**
     * 名字 字体大小
     **/
    private int nameTextSize;
    /**
     * 能量字体大小
     **/
    private int energyTextSize;
    /**
     * 能量字体颜色
     **/
    private int energyTextColor;

    private List<DetailInfo> mNameInfoList;
    private List<DetailInfo> mHeadImgInfoList;
    private List<DetailInfo> mEnergyInfoList;
    private List<DetailInfo> mAnimBgInfoList;
    private List<DetailInfo> mEnergyIconList;
    //默认头像
    private static  final String DEF_HEADIMG_FILENAME ="img_22.png";

    public static class DetailInfo {
        String fileName;
        String value;
        boolean show;

        public DetailInfo(String fileName, String value, boolean show) {
            this.fileName = fileName;
            this.value = value;
            this.show = show;
        }

        public DetailInfo(){

        }


        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }
    }

    public void addName(DetailInfo info) {
        if (mNameInfoList == null) {
            mNameInfoList = new ArrayList<DetailInfo>();
        }
        mNameInfoList.add(info);
    }

    public void addHeadImg(DetailInfo info) {
        if (mHeadImgInfoList == null) {
            mHeadImgInfoList = new ArrayList<DetailInfo>();
        }
        mHeadImgInfoList.add(info);
    }

    public void addEnergy(DetailInfo info) {
        if (mEnergyInfoList == null) {
            mEnergyInfoList = new ArrayList<DetailInfo>();
        }
        mEnergyInfoList.add(info);
    }

    public void addAnimBg(DetailInfo info) {
        if (mAnimBgInfoList == null) {
            mAnimBgInfoList = new ArrayList<DetailInfo>();
        }
        mAnimBgInfoList.add(info);
    }

    public void addEnergyIcon(DetailInfo info) {
        if (mEnergyIconList == null) {
            mEnergyIconList = new ArrayList<DetailInfo>();
        }
        mEnergyIconList.add(info);
    }

    public void setNameTextColor(int nameTextColor) {
        this.nameTextColor = nameTextColor;
    }

    public void setNameTextSize(int nameTextSize) {
        this.nameTextSize = nameTextSize;
    }

    public void setEnergyTextColor(int energyTextColor) {
        this.energyTextColor = energyTextColor;
    }

    public void setEnergyTextSize(int energyTextSize) {
        this.energyTextSize = energyTextSize;
    }

    public TeamPkContribStarLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        DetailInfo info;
        if ((info = getAnimInfo(fileName, mNameInfoList)) != null) {
            return createMsgBitmap(width, height, info.value, nameTextSize, nameTextColor);
        } else if ((info = getAnimInfo(fileName, mHeadImgInfoList)) != null) {
            Bitmap bitmap = getBitMapFromAssets(DEF_HEADIMG_FILENAME,animationView.getContext());
            if(!TextUtils.isEmpty(info.getValue())){
                upDateLottieBitMap(animationView, bitmapId, info.value, width, height);
            }
            return bitmap;
        } else if ((info = getAnimInfo(fileName, mEnergyInfoList)) != null && info.show) {
            return createMsgBitmap(width, height, info.value, energyTextSize, energyTextColor);
        } else if ((info = getAnimInfo(fileName, mAnimBgInfoList)) != null && info.show) {
            return getBitMapFromAssets(fileName,animationView.getContext());
        } else if ((info = getAnimInfo(fileName, mEnergyIconList)) != null && info.show) {
            return getBitMapFromAssets(fileName,animationView.getContext());
        }
        return null;
    }

    private void upDateLottieBitMap(final LottieAnimationView animationView, final String bitmapId, String url, final
    int width, final int height) {
        ImageLoader.with(animationView.getContext()).load(url).asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap resultBitmap = null;
                if (drawable instanceof GifDrawable) {
                    resultBitmap = ((GifDrawable) drawable).getFirstFrame();
                }
                else {
                    resultBitmap = DrawableHelper.drawable2bitmap(drawable);
                }
                if(resultBitmap != null){
                    Bitmap tempBitmap = circleBitmap(resultBitmap, Math.min(width, height) / 2);
                    animationView.updateBitmap(bitmapId, tempBitmap);
                }
            }
            @Override
            public void onFail() {
            }
        });
    }

    /**
     * 生成文字图片
     *
     * @param width
     * @param height
     * @param textSize
     * @param textColor
     * @return
     */
    private Bitmap createMsgBitmap(int width, int height, String msg, int textSize, int textColor) {
        Bitmap resultBitmap = null;
        if (!TextUtils.isEmpty(msg)) {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.LEFT);
            Typeface fontFace = FontCache.getTypeface(ContextManager.getContext(), "fangzhengcuyuan.ttf");
            if(fontFace != null){
                paint.setTypeface(fontFace);
            }
            Rect fontRect = new Rect();
            paint.getTextBounds(msg, 0, msg.length(), fontRect);
            int offsetX = Math.max((width - fontRect.width()) / 2, 0);
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int baseLine = (height - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2 - fontMetricsInt.ascent;
            canvas.drawText(msg, offsetX, baseLine, paint);
        }
        return resultBitmap;
    }

    private DetailInfo getAnimInfo(String fileName, List<DetailInfo> list) {
        DetailInfo info = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).fileName.equals(fileName)) {
                    info = list.get(i);
                    break;
                }
            }
        }
        return info;
    }

}
