package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

/**
 * 战队成员 展示 recyclerView
 *
 * @author chenkun
 * @version 1.0, 2018/4/13 上午10:27
 */


public class TeamPkRecyclerView extends RecyclerView {

    public TeamPkRecyclerView(Context context) {
        super(context);
    }

    public TeamPkRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TeamPkRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
