package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 倒计时 展示控件
 *
 * @author chekun
 * created  at 2018/4/24 10:19
 */
public class TimeCountDowTextView extends android.support.v7.widget.AppCompatTextView {

    private int mDuration;
    private String mTimePrefix;
    private String mTimeSuffix;
    TimeCountDowListener mListener;
    private TimeCountDowTask mTask;

    public TimeCountDowTextView(Context context) {
        super(context);
    }

    public TimeCountDowTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeCountDowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param time 倒计时总时长
     */
    public void setTimeDuration(int time) {
        mDuration = time;
    }

    /**
     * 倒计时前缀文字
     * @param prefix
     */
    public void setTimePrefix(String prefix) {
        mTimePrefix = prefix;
    }

    /**
     * 倒计时 后最文字
     *
     * @param suffix
     */
    public void setTimeSuffix(String suffix) {
        mTimeSuffix = suffix;
    }


    class TimeCountDowTask implements Runnable {
        int mTime;

        TimeCountDowTask(int time) {
            mTime = time;
        }

        @Override
        public void run() {
            if (mTime > 0) {
                String prefix = TextUtils.isEmpty(mTimePrefix) ? "" : mTimePrefix;
                String suffix = TextUtils.isEmpty(mTimeSuffix) ? "" : mTimeSuffix;
                TimeCountDowTextView.this.setText(prefix + mTime + suffix);
                mTime--;
                postDelayed(this, 1000);
            } else {
                setVisibility(GONE);
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        }
    }

    /**
     * 开始倒计时
     */
    public void startCountDow() {
        setVisibility(VISIBLE);
        if (mTask != null) {
            removeCallbacks(mTask);
            mTask = null;
        }
        mTask = new TimeCountDowTask(mDuration);
        post(mTask);
    }

    public void startCountDow(long delay) {
        setVisibility(VISIBLE);
        if (mTask != null) {
            removeCallbacks(mTask);
            mTask = null;
        }
        mTask = new TimeCountDowTask(mDuration);
        postDelayed(mTask, delay);
    }


    public void setTimeCountDowListener(TimeCountDowListener listener) {
        this.mListener = listener;
    }

    public interface TimeCountDowListener {
        void onFinish();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTask != null) {
            removeCallbacks(mTask);
        }
        this.mListener = null;
    }
}
