package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.R;


/**
 * 战队成员 展示 recyclerView
 *
 * @author chenkun
 * @version 1.0, 2018/4/13 上午10:27
 */


public class PriaseRecyclerView extends RecyclerView {


    private RclViewFastScroller fastScroller;

    public PriaseRecyclerView(Context context) {
        this(context, null);
    }


    public PriaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public PriaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RclViewFastScroller, defStyle, 0);
        StateListDrawable verticalThumbDrawable = (StateListDrawable) a
                .getDrawable(R.styleable.RclViewFastScroller_VerticalThumbDrawable);
        Drawable verticalTrackDrawable = a.getDrawable(R.styleable.RclViewFastScroller_VerticalTrackDrawable);
        a.recycle();
        bindFastScroll(verticalThumbDrawable, verticalTrackDrawable);
    }
    private void bindFastScroll(StateListDrawable verticalThumbDrawable, Drawable verticalTrackDrawable) {
        Resources resources = getContext().getResources();
        fastScroller = new RclViewFastScroller(this, verticalThumbDrawable, verticalTrackDrawable);
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {

        if (layout instanceof GridLayoutManager) {
            super.setLayoutManager(layout);
        } else {
            throw new ClassCastException("GridLayoutManager wanted");
        }
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {

        if (getAdapter() != null && getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null) {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            int columns = ((GridLayoutManager) getLayoutManager()).getSpanCount();
            animationParams.count = count;
            animationParams.index = index;
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }

    }
}
