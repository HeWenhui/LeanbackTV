

package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.MotionEvent;

import com.xueersi.lib.log.Loger;

/**
 * @author chenkun
 * recycle view 右侧滑动条
 * created 2018/7/8 下午3:25
 * version 1.0
 * 注 ：滑动条 宽度不能太小  不然 事件拦截有问题
 */
public class RclViewFastScroller extends ItemDecoration implements OnItemTouchListener {

    private  final String Tag = "RclViewFastScroller";
    private int mSliderHeight;


    private static final int FASTSCROLL_MINIMUM_RANGE = 50;
    private static final int FASTSCROLL_MARGIN = 0;


    private static final int STATE_HIDDEN = 0;

    private static final int STATE_VISIBLE = 1;
    private static final int STATE_DRAGGING = 2;

    private static final int  MIN_SCROLL_Y = 2;

    private static final int DRAG_NONE = 0;
    private static final int DRAG_X = 1;
    private static final int DRAG_Y = 2;

    private static final int ANIMATION_STATE_OUT = 0;
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_FADING_OUT = 3;

    private static final int SHOW_DURATION_MS = 500;
    private static final int SCROLLBAR_FULL_OPAQUE = 255;

    private static final int[] PRESSED_STATE_SET = new int[]{android.R.attr.state_pressed};
    private static final int[] EMPTY_STATE_SET = new int[]{};

    private final int mScrollbarMinimumRange;
    private final int mMargin;

    private final StateListDrawable mVerticalThumbDrawable;
    private final Drawable mVerticalTrackDrawable;
    private final int mVerticalThumbWidth;
    private final int mVerticalTrackWidth;


    int mVerticalThumbHeight;
    int mVerticalThumbCenterY;
    float mVerticalDragY;


    private int mRecyclerViewWidth = 0;
    private int mRecyclerViewHeight = 0;

    private RecyclerView mRecyclerView;
    private boolean mNeedVerticalScrollbar = false;
    private int mState = STATE_HIDDEN;
    private int mDragState = DRAG_NONE;

    private final int[] mVerticalRange = new int[2];

    private final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(0, 1);
    private int mAnimationState = ANIMATION_STATE_OUT;


    private final OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            updateScrollPosition(recyclerView.computeHorizontalScrollOffset(),
                    recyclerView.computeVerticalScrollOffset());
        }
    };

    public RclViewFastScroller(RecyclerView recyclerView, StateListDrawable verticalThumbDrawable,
                               Drawable verticalTrackDrawable) {

        mVerticalThumbDrawable = verticalThumbDrawable;
        mVerticalTrackDrawable = verticalTrackDrawable;


        mVerticalThumbWidth = verticalThumbDrawable.getIntrinsicWidth();
        mVerticalTrackWidth = verticalTrackDrawable.getIntrinsicWidth();


        mVerticalThumbHeight = verticalThumbDrawable.getIntrinsicHeight();
        mVerticalThumbCenterY = mVerticalThumbHeight / 2;


        float scale = recyclerView.getContext().getResources().getDisplayMetrics().density;

        mScrollbarMinimumRange = (int) (FASTSCROLL_MINIMUM_RANGE * scale + 0.5f);
        mMargin = (int) (FASTSCROLL_MARGIN * scale + 0.5f);


        mVerticalThumbDrawable.setAlpha(SCROLLBAR_FULL_OPAQUE);
        mVerticalTrackDrawable.setAlpha(SCROLLBAR_FULL_OPAQUE);

        mShowHideAnimator.addListener(new AnimatorListener());
        mShowHideAnimator.addUpdateListener(new AnimatorUpdater());

        attachToRecyclerView(recyclerView);
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (mRecyclerView == recyclerView) {
            return;
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            setupCallbacks();
        }
    }


    public void dettachToRecyclerView(){

        if(mRecyclerView != null){
            destroyCallbacks();
        }

    }


    private void setupCallbacks() {
        mRecyclerView.addItemDecoration(this);
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private void destroyCallbacks() {
        mRecyclerView.removeItemDecoration(this);
        mRecyclerView.removeOnItemTouchListener(this);
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
    }

    private void requestRedraw() {
        mRecyclerView.invalidate();
    }

    private void setState(int state) {
        if (state == STATE_DRAGGING && mState != STATE_DRAGGING) {
            mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
        }

        if (state == STATE_HIDDEN) {
            requestRedraw();
        } else {
            show();
        }

        if (mState == STATE_DRAGGING && state != STATE_DRAGGING) {
            mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
        }
        mState = state;
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(mRecyclerView) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public boolean isDragging() {
        return mState == STATE_DRAGGING;
    }

    boolean isVisible() {
        return mState == STATE_VISIBLE;
    }

    boolean isHidden() {
        return mState == STATE_HIDDEN;
    }


    public void show() {
        switch (mAnimationState) {
            case ANIMATION_STATE_FADING_OUT:
                mShowHideAnimator.cancel();
            case ANIMATION_STATE_OUT:
                mAnimationState = ANIMATION_STATE_FADING_IN;
                mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 1);
                mShowHideAnimator.setDuration(SHOW_DURATION_MS);
                mShowHideAnimator.setStartDelay(0);
                mShowHideAnimator.start();
                break;
            default:
                break;
        }
    }

    public void hide() {
        hide(0);
    }

    private void hide(int duration) {
        Loger.e("RclViewFastScroller","====>:hide called "+mAnimationState);
        switch (mAnimationState) {
            case ANIMATION_STATE_FADING_IN:
                mShowHideAnimator.cancel();
                // fall through
            case ANIMATION_STATE_IN:
                mAnimationState = ANIMATION_STATE_FADING_OUT;
                mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 0);
                mShowHideAnimator.setDuration(duration);
                mShowHideAnimator.start();
                break;
            default:
                break;
        }
    }


    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        if (mRecyclerViewWidth != mRecyclerView.getWidth()
                || mRecyclerViewHeight != mRecyclerView.getHeight()) {
            mRecyclerViewWidth = mRecyclerView.getWidth();
            mRecyclerViewHeight = mRecyclerView.getHeight();
            Loger.e("RclViewFastScroller","====>onDrawOver:"+mRecyclerView.computeVerticalScrollRange()+":"+mRecyclerViewHeight);
            if(mRecyclerView.computeVerticalScrollRange() > mRecyclerViewHeight){
                setState(STATE_VISIBLE);
                mNeedVerticalScrollbar = true;
            }else{
                setState(STATE_HIDDEN);
                mNeedVerticalScrollbar = false;
            }
            return;
        }
        if (mAnimationState != ANIMATION_STATE_OUT) {
            if (mNeedVerticalScrollbar) {
                drawVerticalScrollbar(canvas);
            }
        }

    }

    private void drawVerticalScrollbar(Canvas canvas) {

        int viewWidth = mRecyclerViewWidth;

        int left = viewWidth - mVerticalThumbWidth;

        float offeSetX = (mVerticalThumbWidth - mVerticalTrackWidth)/2.0f;
        offeSetX = offeSetX < 0?0:offeSetX;
        int top = mVerticalThumbCenterY - mVerticalThumbHeight / 2;
        top = top < 0 ? 0 : top;
        mVerticalThumbDrawable.setBounds(0, 0, mVerticalThumbWidth, mVerticalThumbHeight);
        mVerticalTrackDrawable.setBounds(0, 0, mVerticalTrackWidth, mRecyclerViewHeight);

        if (isLayoutRTL()) {
            mVerticalTrackDrawable.draw(canvas);
            canvas.translate(mVerticalThumbWidth, top);
            canvas.scale(-1, 1);
            mVerticalThumbDrawable.draw(canvas);
            canvas.scale(1, 1);
            canvas.translate(-mVerticalThumbWidth, -top);
        } else {
            canvas.translate(left + offeSetX, 0);
            mVerticalTrackDrawable.draw(canvas);
            canvas.translate(-offeSetX, top);
            mVerticalThumbDrawable.draw(canvas);
            canvas.translate(-left, -top);
        }
    }


    /**
     * @param offsetX The new scroll X offset.
     * @param offsetY The new scroll Y offset.
     */
    void updateScrollPosition(int offsetX, int offsetY) {
        int verticalContentLength = mRecyclerView.computeVerticalScrollRange();
        int verticalVisibleLength = mRecyclerViewHeight;
        mNeedVerticalScrollbar = verticalContentLength - verticalVisibleLength > 0
                && mRecyclerViewHeight >= mScrollbarMinimumRange;

        if (!mNeedVerticalScrollbar) {
            if (mState != STATE_HIDDEN) {
                setState(STATE_HIDDEN);
            }
            return;
        }

        if (mNeedVerticalScrollbar) {
            float middleScreenPos = offsetY + verticalVisibleLength / 2.0f;
            mVerticalThumbCenterY = mVerticalThumbHeight / 2 + (offsetY * (verticalVisibleLength -
                    mVerticalThumbHeight)) / (verticalContentLength - verticalVisibleLength);

        }

        if (mState == STATE_HIDDEN || mState == STATE_VISIBLE) {
            setState(STATE_VISIBLE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent ev) {
        final boolean handled;
        if (mState == STATE_VISIBLE) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(ev.getX(), ev.getY());
            if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && insideVerticalThumb) {
                mDragState = DRAG_Y;
                mVerticalDragY = (int) ev.getY();
                setState(STATE_DRAGGING);
                handled = true;
            } else {
                handled = false;
            }
        } else if (mState == STATE_DRAGGING) {
            handled = true;
        } else {
            handled = false;
        }

        return handled;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent me) {

        if (mState == STATE_HIDDEN) {
            return;
        }

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(me.getX(), me.getY());
            if (insideVerticalThumb) {
                mDragState = DRAG_Y;
                mVerticalDragY = (int) me.getY();
                setState(STATE_DRAGGING);
            }
        } else if (me.getAction() == MotionEvent.ACTION_UP && mState == STATE_DRAGGING) {
            mVerticalDragY = 0;
            setState(STATE_VISIBLE);
            mDragState = DRAG_NONE;
        } else if (me.getAction() == MotionEvent.ACTION_MOVE && mState == STATE_DRAGGING) {
            show();
            if (mDragState == DRAG_Y) {
                verticalScrollTo(me.getY());
            }
        }

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private void verticalScrollTo(float y) {
        final int[] scrollbarRange = getVerticalRange();
        y = Math.max(scrollbarRange[0], Math.min(scrollbarRange[1], y));
        if (Math.abs(mVerticalThumbCenterY - y) < MIN_SCROLL_Y) {
            return;
        }
        int scrollingBy = scrollTo(mVerticalDragY, y, scrollbarRange,
                mRecyclerView.computeVerticalScrollRange(),
                mRecyclerView.computeVerticalScrollOffset(), mRecyclerViewHeight);
        if (scrollingBy != 0) {
            mRecyclerView.scrollBy(0, scrollingBy);
        }
        mVerticalDragY = y;
    }


    private int scrollTo(float oldDragPos, float newDragPos, int[] scrollbarRange, int scrollRange,
                         int scrollOffset, int viewLength) {
        int scrollbarLength = scrollbarRange[1] - scrollbarRange[0];
        if (scrollbarLength == 0) {
            return 0;
        }
        float percentage = ((newDragPos - oldDragPos) / (float) scrollbarLength);
        int totalPossibleOffset = scrollRange - viewLength;
        int scrollingBy = (int) (percentage * totalPossibleOffset);
        int absoluteOffset = scrollOffset + scrollingBy;
        if (absoluteOffset < totalPossibleOffset && absoluteOffset >= 0) {
            return scrollingBy;
        } else {
            return 0;
        }
    }

    boolean isPointInsideVerticalThumb(float x, float y) {

        return (isLayoutRTL() ? x <= mVerticalThumbWidth / 2
                : x >= mRecyclerViewWidth - mVerticalThumbWidth)
                && y >= mVerticalThumbCenterY - mVerticalThumbHeight / 2
                && y <= mVerticalThumbCenterY + mVerticalThumbHeight / 2;
    }


    Drawable getVerticalTrackDrawable() {
        return mVerticalTrackDrawable;
    }

    Drawable getVerticalThumbDrawable() {
        return mVerticalThumbDrawable;
    }

    private int[] getVerticalRange() {
        mVerticalRange[0] = mMargin;
        mVerticalRange[1] = mRecyclerViewHeight - mMargin;
        return mVerticalRange;
    }


    private class AnimatorListener extends AnimatorListenerAdapter {

        private boolean mCanceled = false;

        @Override
        public void onAnimationEnd(Animator animation) {

            if (mCanceled) {
                mCanceled = false;
                return;
            }
            if ((float) mShowHideAnimator.getAnimatedValue() == 0) {
                mAnimationState = ANIMATION_STATE_OUT;
                setState(STATE_HIDDEN);
            } else {
                mAnimationState = ANIMATION_STATE_IN;
                requestRedraw();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCanceled = true;
        }
    }

    private class AnimatorUpdater implements AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int alpha = (int) (SCROLLBAR_FULL_OPAQUE * ((float) valueAnimator.getAnimatedValue()));
            mVerticalThumbDrawable.setAlpha(alpha);
            mVerticalTrackDrawable.setAlpha(alpha);
            requestRedraw();
        }
    }
}
