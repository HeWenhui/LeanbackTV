package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


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


    int height = 0;
    int width  = 0;

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


    private Paint paint;


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
        paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
    }


    class DrawableInfo {

        Bitmap bitmap;
        float  left;
        float  top;

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
            canvas.drawBitmap(bitmap, left, top, paint);
        }


        public int getWidth(){
            return bitmap == null?0:bitmap.getWidth();
        }


        public int getHeight(){
            return bitmap == null?0:bitmap.getHeight();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int contentWidth ;
        int contentHeight;
        if(widthMode == MeasureSpec.EXACTLY){
            contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        }else{
            contentWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec),width);
        }

        if(heightMode == MeasureSpec.EXACTLY){
            contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        }else{
            contentHeight = Math.min(MeasureSpec.getSize(heightMeasureSpec), height);
        }
        int wMeasureSpec =  MeasureSpec.makeMeasureSpec(contentWidth,MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(contentHeight,MeasureSpec.EXACTLY);
        super.onMeasure(wMeasureSpec, hMeasureSpec);


    }

    List<DrawableInfo> drawableInfoList;
    /**
     * @param prefixResId
     * @param coinNum
     * @param suffixResId
     */
    public void setAwardInfo(int prefixResId, int coinNum, int suffixResId) {

        if(drawableInfoList == null){
            drawableInfoList = new ArrayList<DrawableInfo>();
        }

        // 获取前缀Bitmap
        Bitmap prefixBitmap = BitmapFactory.decodeResource(getContext().getResources(), prefixResId);
        DrawableInfo drawableInfo = new DrawableInfo(prefixBitmap);

        drawableInfo.setLeft(0f);
        drawableInfo.setTop(0f);
        drawableInfoList.add(drawableInfo);
        // 默认前缀 bitmap 的高度 就为整个 布局的高度
        height = prefixBitmap.getHeight();
        width = prefixBitmap.getWidth();

        String coinNumStr = coinNum +"";
        Bitmap numBitmap;

        // 获取 数字 对应的 bitMap
        for (char c : coinNumStr.toCharArray()) {
            numBitmap =  BitmapFactory.decodeResource(getContext().getResources(),
                    numberResIds[Integer.parseInt(String.valueOf(c))]);
            drawableInfo = new DrawableInfo(numBitmap);
            drawableInfoList.add(drawableInfo);
            drawableInfo.left = width;
            drawableInfo.top = (height - numBitmap.getHeight())/2;
            width += numBitmap.getWidth();
        }

        // 获取前缀Bitmap
        Bitmap suffixBitmap = BitmapFactory.decodeResource(getContext().getResources(), suffixResId);
        drawableInfo = new DrawableInfo(suffixBitmap);
        drawableInfo.setTop(0);
        drawableInfo.setLeft(width);

        drawableInfoList.add(drawableInfo);
        width += suffixBitmap.getWidth();
        //重新布局
        requestLayout();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(drawableInfoList != null && drawableInfoList.size() >0){
            for (DrawableInfo drawableInfo : drawableInfoList) {
                drawableInfo.draw(canvas);
            }
        }
    }

}
