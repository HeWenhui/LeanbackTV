package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 战队pk  队员列表 布局管理器
 *
 * @author chekun
 * created  at 2018/4/17 10:15
 */
public class TeamMemberGridlayoutManager extends GridLayoutManager {

    /**是否为快速滑动的 标准*/
    private float millisecondsPerInch;
    private InnerScroller linearSmoothScroller;

    public TeamMemberGridlayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        millisecondsPerInch = 40 * scale + 0.5f;
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

        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }


    private class InnerScroller extends LinearSmoothScroller {
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
            if (view != null) {
                //获取当前item的position
                final int firstChildPos = getPosition(getChildAt(0));
                //算出需要滑动的item数量
                int delta = Math.abs(getTargetPosition() - firstChildPos);
                if (delta == 0){
                    delta = 1;
                }
                return (millisecondsPerInch / delta) / displayMetrics.densityDpi;
            } else {
                return millisecondsPerInch / displayMetrics.densityDpi;
            }
        }
    }

}
