package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.view.image.CircleImageView;


/**
 * 带倒计时的用户头像
 * Created by zouhao on 2018/4/2.
 */
public class CountDownHeadImageView extends CircleImageView {

    /** 倒计时时长 */
    private long countDownTime;

    /** 倒计总时长 */
    private long allCountDownTime;

    /** 正倒计时中 */
    private boolean isCountDowning;

    /** 已完成的边框颜色 */
    protected int mFinishBorderColor = Color.BLACK;
    /** 已完成的边框画笔 */
    protected final Paint mFinishBorderPaint = new Paint();
    /** 未完成的边框颜色 */
    protected int mUnFinishBorderColor = Color.BLACK;
    /** 未完成的边框画笔 */
    protected final Paint mUnFinishBorderPaint = new Paint();
    /** 倒计时上的小圆 */
    protected final Paint mUnFinishCirclePaint = new Paint();

    protected int mCountDownBorder = DEFAULT_BORDER_COLOR;

    /** 完成的绘制区域 */
    private RectF finishedOuterRect = new RectF();
    /** 未完成的绘制区域 */
    private RectF unfinishedOuterRect = new RectF();
    private int tempCountDownTime = 4;

    public CountDownHeadImageView(Context context) {
        super(context);
    }

    public CountDownHeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownHeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void restore() {
        Loger.i("RolePlayerSelfItemTest", "重置头像");
        isBeginCountdownTime = false;
        tempCountDownTime = 0;
        postInvalidate();

    }

    /**
     * 倒计时回调
     */
    public interface countDownTimeImpl {
        void countTime(long time);
    }

    /** 计时起点 */
    private long beginCountDownTime;

    public void setBeginCountdownTime(boolean beginCountdownTime) {
        isBeginCountdownTime = beginCountdownTime;
        countDownTime = 0;
        allCountDownTime=0;
    }

    /** 是否开启倒计时 */
    public boolean isBeginCountdownTime;

    /**
     * 开始倒计时
     *
     * @param countDownTime 毫秒
     */
    public synchronized void startCountDown(final int countDownTime, int endDownTime, final countDownTimeImpl downtimeImpl) {
        isBeginCountdownTime = true;
        beginCountDownTime = System.currentTimeMillis() - (countDownTime - endDownTime);
        this.allCountDownTime = countDownTime;
        this.countDownTime = endDownTime;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (CountDownHeadImageView.this.countDownTime <= 0) {
                    return;
                }

                CountDownHeadImageView.this.countDownTime = ((beginCountDownTime + (allCountDownTime) - System.currentTimeMillis()));
                invalidate();

                if (downtimeImpl != null) {
                    long endTime = (long) Math.ceil((double) CountDownHeadImageView.this.countDownTime / (double) 1000);
                    downtimeImpl.countTime(endTime);
                    if(endTime <= 4){
                        Loger.i("RolePlayerSelfItemTest", "去画圆弧");
                        tempCountDownTime --;
                    }
                }
                if (isBeginCountdownTime) {
                    postDelayed(this, 1000);
                }
            }
        }, 1000);
        invalidate();
    }

    /**
     * 修改已完成绘笔颜色
     *
     * @param borderColor
     */
    public void setFinishBorderColor(int borderColor) {
        if (borderColor == mFinishBorderColor) {
            return;
        }
        mFinishBorderColor = borderColor;
        mFinishBorderPaint.setColor(mFinishBorderColor);
        mFinishBorderPaint.setStyle(Paint.Style.STROKE);
        mFinishBorderPaint.setAntiAlias(true);
        mFinishBorderPaint.setStrokeWidth(mBorderWidth);
        invalidate();
    }

    /**
     * 修改已完成绘笔颜色
     *
     * @param borderColor
     */
    public void setUnFinishBorderColor(int borderColor) {
        if (borderColor == mUnFinishBorderColor) {
            return;
        }
        mUnFinishBorderColor = borderColor;
        mUnFinishBorderPaint.setColor(mUnFinishBorderColor);
        mUnFinishBorderPaint.setStyle(Paint.Style.STROKE);
        mUnFinishBorderPaint.setAntiAlias(true);
        mUnFinishBorderPaint.setStrokeWidth(mBorderWidth);

        mUnFinishCirclePaint.setColor(mUnFinishBorderColor);
        mUnFinishCirclePaint.setStyle(Paint.Style.FILL);
        mUnFinishCirclePaint.setStrokeWidth(mBorderWidth + 5);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
//        if (mBorderWidth > 0 && countDownTime == 0) {
//            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
//        } else
        if (mBorderWidth > 0 && (tempCountDownTime > 0 && tempCountDownTime <= 3)) {
            //画倒计时的圆弧

//            rectF.left = strokeWidth / 2;
//            rectF.top = strokeWidth / 2;
//            rectF.right = width - strokeWidth / 2;
//            rectF.bottom = height - strokeWidth / 2;
            int piding = 10;
            finishedOuterRect.set(mBorderWidth / 2 + piding, mBorderWidth / 2 + piding, (getWidth() - mBorderWidth / 2 - piding), (getHeight() - mBorderWidth / 2 - piding));
            unfinishedOuterRect.set(mBorderWidth / 2 + piding, mBorderWidth / 2 + piding, (getWidth() - mBorderWidth / 2 - piding), (getHeight() - mBorderWidth / 2 - piding));
            float unFinishRange = ((float) tempCountDownTime / (float) 3) * 360;
            canvas.drawArc(unfinishedOuterRect, -90, unFinishRange, false, mUnFinishBorderPaint);
            canvas.drawArc(finishedOuterRect, unFinishRange - 90, 360 - unFinishRange, false, mFinishBorderPaint);


            double arg = (unFinishRange - 90) * Math.PI / 180;
            int x = (int) (Math.cos(arg) * ((getWidth() - piding * 2 - mBorderWidth) / 2) + (getWidth()) / 2);
            int y = (int) (Math.sin(arg) * ((getHeight() - piding * 2 - mBorderWidth) / 2) + (getHeight()) / 2);
            canvas.drawCircle(x, y, mBorderWidth, mUnFinishCirclePaint);

        } else if (mBitmapWidth > 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }
    }
}
