package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;

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

    protected int mCountDownBorder = DEFAULT_BORDER_COLOR;

    /** 完成的绘制区域 */
    private RectF finishedOuterRect = new RectF();
    /** 未完成的绘制区域 */
    private RectF unfinishedOuterRect = new RectF();

    public CountDownHeadImageView(Context context) {
        super(context);
    }

    public CountDownHeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownHeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 倒计时回调
     */
    public interface countDownTimeImpl {
        void countTime(long time);
    }

    private long beginCountDownTime;

    /**
     * 开始倒计时
     *
     * @param countDownTime 毫秒
     */
    public void startCountDown(int countDownTime, final countDownTimeImpl downtimeImpl) {
        beginCountDownTime = System.currentTimeMillis();
        this.allCountDownTime = countDownTime;
        this.countDownTime = countDownTime;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                CountDownHeadImageView.this.countDownTime = ((beginCountDownTime + (allCountDownTime) - System.currentTimeMillis()));
                invalidate();
                if (downtimeImpl != null) {
                    downtimeImpl.countTime((long)Math.ceil((double) CountDownHeadImageView.this.countDownTime / (double)1000));
                }
                if (CountDownHeadImageView.this.countDownTime > 0) {
                    postDelayed(this, 50);
                }
            }
        }, 50);
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
        if (mBorderWidth > 0 && countDownTime > 0) {
            //画倒计时的圆弧
            finishedOuterRect.set(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight() - mBorderWidth);
            unfinishedOuterRect.set(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight() - mBorderWidth);
            float unFinishRange = ((float) countDownTime / (float) allCountDownTime) * 360;
            canvas.drawArc(unfinishedOuterRect, -90, unFinishRange, false, mUnFinishBorderPaint);
            canvas.drawArc(finishedOuterRect, unFinishRange - 90, 360 - unFinishRange, false, mFinishBorderPaint);

        }
    }
}
