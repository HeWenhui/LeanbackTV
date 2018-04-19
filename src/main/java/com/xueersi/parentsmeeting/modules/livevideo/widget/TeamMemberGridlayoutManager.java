package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class TeamMemberGridlayoutManager extends GridLayoutManager {
    private float MILLISECONDS_PER_INCH;
    private InnerScroller linearSmoothScroller;
    public TeamMemberGridlayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context) {
        float scale=context.getResources().getDisplayMetrics().density;
        MILLISECONDS_PER_INCH =  40*scale+0.5f;
    }
    public TeamMemberGridlayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        init(context);
    }

    public TeamMemberGridlayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        init(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);

        if (linearSmoothScroller == null) {
            linearSmoothScroller = new InnerScroller(recyclerView.getContext());
        }

        linearSmoothScroller .setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }


    private class  InnerScroller extends LinearSmoothScroller{
        public InnerScroller(Context context) {
            super(context);
        }
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return TeamMemberGridlayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {

            View view = getChildAt(0);
            if(view != null) {
                final int firstChildPos = getPosition(getChildAt(0)); //获取当前item的position
                int delta = Math.abs(getTargetPosition() - firstChildPos);//算出需要滑动的item数量
                if(delta == 0)
                    delta = 1;
                return (MILLISECONDS_PER_INCH/delta) / displayMetrics.densityDpi;
            }
            else
            {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        }
    }

}
