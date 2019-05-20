package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 计时 展示控件
 *
 * @author chekun
 * created  at 2018/4/24 10:19
 */
public class TimeCountTextView extends android.support.v7.widget.AppCompatTextView {

    private TimeCountTask mTask;

    public TimeCountTextView(Context context) {
        super(context);
    }

    public TimeCountTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeCountTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    class TimeCountTask implements Runnable {
        int mTime;
        StringBuilder sb = new StringBuilder();

        public int getTime(){
            return mTime;
        }

        @Override
        public void run() {
            mTime++;
            if (mTime >= 60) {
                sb.delete(0, sb.length());
                int min = mTime / 60;
                int second = mTime % 60;
                sb.append("用时: ").append(min).append("分");
                if(second > 0){
                    sb.append(second).append("秒");
                }
            } else {
                sb.delete(0, sb.length());
                sb.append("用时: ")
                        .append(mTime).append("秒");
            }
            TimeCountTextView.this.setText(sb.toString());
            postDelayed(this, 1000);
        }
    }

    /**
     * 开始倒计时
     */
    public void start() {
        //清空
        setText("");
        start(1000);
    }

    public void start(long delay) {
        setVisibility(VISIBLE);
        if (mTask != null) {
            removeCallbacks(mTask);
            mTask = null;
        }
        mTask = new TimeCountTask();
        postDelayed(mTask, delay);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public void stop(){
        if (mTask != null) {
            removeCallbacks(mTask);
        }
    }

    /**
     * 获取当前计时  单位秒
     * @return
     */
    public int getCurrentTime(){
        return mTask != null? mTask.getTime():0;
    }
}
