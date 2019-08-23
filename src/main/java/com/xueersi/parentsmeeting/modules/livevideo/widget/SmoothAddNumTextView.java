package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 平滑递增 数字展示器
 *
 * @author chekun
 * created  at 2018/4/17 10:09
 */
public class SmoothAddNumTextView extends android.support.v7.widget.AppCompatTextView {

    /**
     * 动画时间
     */
    private long duration = 1500;
    private int maxAddCount = 30;
    private IncrementTask task;


    /**每次刷新的时间间隔*/
    int timeGap;
    /**递增次数*/
    int addTimes;
    /**增量*/
    int increment;
    private String mPreFix = "";

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
            if (typedArray != null) {
                String durationStr = typedArray.getString(R.styleable.SmoothAddNumTextView_anim_duration);
                if (!TextUtils.isEmpty(durationStr)) {
                    duration = Long.parseLong(durationStr);
                }
                maxAddCount = typedArray.getInteger(R.styleable.SmoothAddNumTextView_max_add_count, 30);
                typedArray.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         setTypeface();
    }

    /**
     * @param numIncrement
     */
    public void smoothAddNum(int numIncrement) {
        try {
            if (isAnimRunning()) {
                cancelAnim();
            }
            if (!TextUtils.isEmpty(this.getText().toString())) {
                if (numIncrement > 0) {
                    int currentEnergy = Integer.parseInt(this.getText().toString());
                    addTimes = numIncrement > maxAddCount ? maxAddCount : numIncrement;
                    timeGap = (int) (duration / addTimes);
                    increment = numIncrement / addTimes;
                    if(task != null && task.isRunning){
                        task.cancel();
                    }
                    task = new IncrementTask(currentEnergy, numIncrement, increment, timeGap);
                    this.post(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置显示文案前缀
     * @param s
     */
    public void setPreFix(String s) {
        mPreFix = s;
    }


    class IncrementTask implements Runnable {
        int startNum;
        int endNum;
        int addNum;
        long timeGap;
        int increment;
        int currentNum;
        boolean isRunning;
        boolean canceled;

        IncrementTask(int startNum, int addNum, int increment, long timeGap) {
            this.startNum = startNum;
            this.addNum = addNum;
            this.timeGap = timeGap;
            this.increment = increment;
            this.endNum = startNum + addNum;
            this.currentNum = startNum;
            isRunning = false;
        }

        @Override
        public void run() {
            if (canceled) {
                return;
            }
            if (currentNum <= endNum) {
                isRunning = true;
                SmoothAddNumTextView.this.setText(mPreFix+currentNum + "");
                currentNum += increment;
                SmoothAddNumTextView.this.postDelayed(this, timeGap);
            } else {
                SmoothAddNumTextView.this.setText(mPreFix+endNum + "");
                isRunning = false;
            }
        }

        public void cancel() {
            canceled = true;
            currentNum = endNum;
            SmoothAddNumTextView.this.setText(mPreFix+endNum + "");
        }

        public boolean isRunning() {
            return isRunning;
        }

    }


    public boolean isAnimRunning() {
        return task != null && task.isRunning();
    }

    public void cancelAnim() {
        if (task != null) {
            task.cancel();
            removeCallbacks(task);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (task != null) {
            removeCallbacks(task);
        }
    }

    public void setTypeface() {
//        Typeface fontFace = FontCache.getTypeface(getContext(), "QanelasSoftDEMO-ExtraBold.otf");
        Typeface fontFace = FontCache.getTypeface(getContext(), "fangzhengcuyuan.ttf");
        if (fontFace != null) {
            setTypeface(fontFace);
        }
    }

}
