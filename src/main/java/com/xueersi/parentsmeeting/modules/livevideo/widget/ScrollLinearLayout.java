package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 一个可展开的布局
 *
 * @author linyuqiang
 */
public class ScrollLinearLayout extends LinearLayout {
    String TAG = "ScrollLinearLayout";
    private Scroller mScroller;
    private int mDuration;
    /**
     * Y value reported by mScroller on the previous fling
     */
    private int mLastFlingY;

    public ScrollLinearLayout(Context context) {
        this(context, null);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        mScroller = new Scroller(context);
    }

    // 调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    // 调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        // 设置mScroller的滚动偏移量
        mLastFlingY = 0;
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, mDuration);
        invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    // 调用此方法设置滚动的相对偏移
    public void stop() {
        if (getChildCount() >= 4) {
            removeViewAt(0);
        }
        mScroller.setFinalY(0);
        mScroller.abortAnimation();
    }


    @Override
    public void computeScroll() {
        // 先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            // 这里调用View的scrollTo()完成实际的滚动
//            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            final int y = mScroller.getCurrY();

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int delta = mLastFlingY - y;
            //logger.i( "computeScroll:delta=" + delta + ",y=" + y);
            offsetChildrenTopAndBottom(delta);
            mLastFlingY = y;
            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * Offset the horizontal location of all children of this view by the
     * specified number of pixels.
     *
     * @param offset the number of pixels to offset
     */
    private void offsetChildrenTopAndBottom(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetTopAndBottom(offset);
        }
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }
}
