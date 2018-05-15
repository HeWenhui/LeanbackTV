package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 平滑递增 数字展示器
 */
public class SmoothAddNumTextView extends android.support.v7.widget.AppCompatTextView {
    private long duration = 1500; // 动画时间
    private int maxAddCount = 30;
    private IncrementTask task;

    public SmoothAddNumTextView(Context context) {
        this(context, null);
    }

    public SmoothAddNumTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothAddNumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmoothAddNumTextView);
            if(typedArray != null){
                String durationStr = typedArray.getString(R.styleable.SmoothAddNumTextView_anim_duration);
                if(!TextUtils.isEmpty(durationStr)){
                    duration = Long.parseLong(durationStr);
                }
                maxAddCount = typedArray.getInteger(R.styleable.SmoothAddNumTextView_max_add_count, 30);
                typedArray.recycle();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * @param numIncrement
     */
    public void smoothAddNum(int numIncrement) {
        try {
            if (!TextUtils.isEmpty(this.getText().toString())) {
                if(numIncrement >0){
                    int currentEnergy = Integer.parseInt(this.getText().toString());
                    addTimes = numIncrement > maxAddCount ? maxAddCount : numIncrement;
                    timeGap = (int) (duration / addTimes);
                    increment = numIncrement / addTimes;
                    task = new IncrementTask(currentEnergy, numIncrement, increment, timeGap);
                    this.post(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int timeGap;
    int addTimes;
    int increment;

    class IncrementTask implements Runnable {
        int startNum;
        int endNum;
        int addNum;
        long timeGap;
        int increment;
        int currentNum;

        IncrementTask(int startNum, int addNum, int increment, long timeGap) {
            this.startNum = startNum;
            this.addNum = addNum;
            this.timeGap = timeGap;
            this.increment = increment;
            this.endNum = startNum + addNum;
            this.currentNum = startNum;
        }

        @Override
        public void run() {
            if (currentNum <= endNum) {
                SmoothAddNumTextView.this.setText(currentNum + "");
                currentNum += increment;
                SmoothAddNumTextView.this.postDelayed(this, timeGap);
            }else{
                SmoothAddNumTextView.this.setText(endNum + "");
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (task != null) {
            removeCallbacks(task);
        }
    }
}
