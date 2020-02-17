package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得金币 信息展示 控件
 *
 * @author chenkun
 * @version 1.0, 2018/4/12 下午3:22
 */


public class CoinAwardDisplayer extends View {
    private Paint mPaint;
    private int mHeight = 0;
    private int mWidth = 0;
    List<DrawableInfo> drawableInfoList;


    int[] numberResIds = {
            R.drawable.livevideo_alertview_gold0_img_disable,
            R.drawable.livevideo_alertview_gold1_img_disable,
            R.drawable.livevideo_alertview_gold2_img_disable,
            R.drawable.livevideo_alertview_gold3_img_disable,
            R.drawable.livevideo_alertview_gold4_img_disable,
            R.drawable.livevideo_alertview_gold5_img_disable,
            R.drawable.livevideo_alertview_gold6_img_disable,
            R.drawable.livevideo_alertview_gold7_img_disable,
            R.drawable.livevideo_alertview_gold8_img_disable,
            R.drawable.livevideo_alertview_gold9_img_disable,
    };


    public CoinAwardDisplayer(Context context) {
        this(context, null);
    }

    public CoinAwardDisplayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoinAwardDisplayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);
    }


    class DrawableInfo {
        Bitmap bitmap;
        float left;
        float top;

        DrawableInfo(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void setLeft(float left) {
            this.left = left;
        }


        public void setTop(float top) {
            this.top = top;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, left, top, mPaint);
        }


        public int getWidth() {
            return bitmap == null ? 0 : bitmap.getWidth();
        }


        public int getHeight() {
            return bitmap == null ? 0 : bitmap.getHeight();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int contentWidth;
        int contentHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            contentWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec), mWidth);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            contentHeight = Math.min(MeasureSpec.getSize(heightMeasureSpec), mHeight);
        }
        int wMeasureSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        super.onMeasure(wMeasureSpec, hMeasureSpec);
    }


    /**
     * 展示获奖信息
     *
     * @param prefixResId 金币前缀 图片资源id
     * @param coinNum     金币数
     * @param suffixResId 金币数后缀 图片资源id
     */
    public void setAwardInfo(int prefixResId, int coinNum, int suffixResId) {

        if (drawableInfoList == null) {
            drawableInfoList = new ArrayList<DrawableInfo>();
        }
        Bitmap prefixBitmap = null;
        Bitmap suffixBitmap = null;
        DrawableInfo drawableInfo = null;

        if (prefixResId > 0) {
            // 获取前缀Bitmap
            prefixBitmap = DrawableHelper.bitmapFromResource(getContext().getResources(), prefixResId);
        }
        if (suffixResId > 0) {
            // 获取前缀Bitmap
            suffixBitmap = DrawableHelper.bitmapFromResource(getContext().getResources(), suffixResId);
        }
        if (prefixBitmap != null) {
            drawableInfo = new DrawableInfo(prefixBitmap);
            drawableInfo.setLeft(0f);
            drawableInfo.setTop(0f);
            drawableInfoList.add(drawableInfo);
            // 默认前缀 bitmap 的高度 就为整个 布局的高度
            mHeight = prefixBitmap.getHeight();
            mWidth = prefixBitmap.getWidth();
        }
        if (suffixBitmap != null && mHeight <= 0) {
            mHeight = suffixBitmap.getHeight();
        }
        if (mHeight <= 0) {
            mHeight = DrawableHelper.bitmapFromResource(getContext().getResources(), numberResIds[0]).getHeight();
        }

        String coinNumStr = coinNum + "";
        Bitmap numBitmap;
        // 获取 数字 对应的 bitMap
        for (char c : coinNumStr.toCharArray()) {
            numBitmap = DrawableHelper.bitmapFromResource(getContext().getResources(),
                    numberResIds[Integer.parseInt(String.valueOf(c))]);
            drawableInfo = new DrawableInfo(numBitmap);
            drawableInfoList.add(drawableInfo);
            drawableInfo.left = mWidth;
            drawableInfo.top = (mHeight - numBitmap.getHeight()) / 2;
            mWidth += numBitmap.getWidth();
        }

        if (suffixBitmap != null) {
            drawableInfo = new DrawableInfo(suffixBitmap);
            drawableInfo.setTop(0);
            drawableInfo.setLeft(mWidth);
            drawableInfoList.add(drawableInfo);
            mWidth += suffixBitmap.getWidth();
        }
        //重新布局
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawableInfoList != null && drawableInfoList.size() > 0) {
            for (DrawableInfo drawableInfo : drawableInfoList) {
                drawableInfo.draw(canvas);
            }
        }
    }
}
