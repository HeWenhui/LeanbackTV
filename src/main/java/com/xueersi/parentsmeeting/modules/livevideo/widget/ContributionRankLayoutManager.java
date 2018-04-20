package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *  居中摆放 item
 * @author chenkun
 * @version 1.0, 16/4/17 下午4:45
 */
public class ContributionRankLayoutManager extends RecyclerView.LayoutManager {
    private static final String Tag = "ContributionRankLayoutManager";
    private int mLineSize;               //每行item个数
    private int mVirticalDevideSpace = 0;// 竖直方向上的分割距离
    private int minHdevidWithd;
    private int totalHeight;
    private SparseArray<Rect> itemRects = new SparseArray<Rect>();
    private List<Line> lines;
    private Rect       displayRect;
    private int        mVerticalOffset;
    private int firstVisibleRow = -1;
    private int lastVisibleRow =0;

    public ContributionRankLayoutManager(int lineSize) {
        this(lineSize, 0);
    }

    public ContributionRankLayoutManager(int lineSize, int verticalDevideSpace) {
        mLineSize = lineSize;
        mVirticalDevideSpace = verticalDevideSpace;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        calculateChildrenSite(recycler);
        recycleAndFill(recycler, state);
    }


    public int getFirstVisibleRow() {
        return firstVisibleRow;
    }

    public int getLastVisibleRow() {
        return lastVisibleRow;
    }

    private void recycleAndFill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        displayRect = new Rect(0, mVerticalOffset, getHorizontalSpace(), mVerticalOffset + getVerticalSpace());

        firstVisibleRow = -1;
        lastVisibleRow  = 0;

        for (int i = 0; i < getItemCount(); i++) {
            //判断ItemView的位置和当前显示区域是否重合
            if (Rect.intersects(displayRect, itemRects.get(i))) {
                if(i%mLineSize == 0){
                    if(firstVisibleRow == -1 ){
                        firstVisibleRow = i/mLineSize;
                        lastVisibleRow = firstVisibleRow;
                    }
                    lastVisibleRow ++;
                }
                View itemView = recycler.getViewForPosition(i);//
                measureChildWithMargins(itemView, 0, 0);
                addView(itemView);
                Rect rect = itemRects.get(i);
                this.layoutDecorated(itemView, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset);
            }
        }
    }

    /**
     * 计算子view 的摆放位置,并缓存位置信息
     */
    private void calculateChildrenSite(RecyclerView.Recycler recycler) {
        // 获取item 的尺寸信息
        View firstView = recycler.getViewForPosition(0);
        //获取 itemView 的尺寸信息
        measureChild(firstView, 0, 0);
        int itemWith   = getDecoratedMeasuredWidth(firstView);
        int itemHeight = getDecoratedMeasuredHeight(firstView);
//        LogUtils.e(Tag, "=========>calculateChildrenSite:" + itemWith + ":" + itemHeight + ":" + getWidth());
        splitItem2Line(itemWith, itemHeight, firstView);
        totalHeight = lines.size() * itemHeight + (lines.size() - 1) * mVirticalDevideSpace;
        cacheItemRect();
    }


    private void splitItem2Line(int itemWith, int itemHeight, View itemView) {
        int lineNum = 0;     //行数
        lines = new ArrayList<Line>();
        Line line = null;
        for (int i = 0; i < getItemCount(); i++) {
            if (i % mLineSize == 0) {
                if (lineNum > 0) {
                    line = new Line(0, lineNum * itemHeight, lineNum);
                } else {
                    line = new Line(0, lineNum * itemHeight, lineNum);
                }
                lines.add(line);
                lineNum++;
            }
            line.addItemView(itemView);
        }
    }

    private void cacheItemRect() {
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).caculateItemRect();
        }
    }

    private class Line {
        private List<View> list;
        private int        mLineLeft;
        private int        mlineTop;
        private int        mItemWidth;
        private int        mItemHeight;
        private int        mItemTotalWidth;
        private boolean averageShareSpce = true;
        private int lineIndex;

        public Line(int lineLeft, int lineTop, int index) {
            mlineTop = lineTop;
            mLineLeft = lineLeft;
            lineIndex = index;
            mlineTop = lineIndex != 0 ? mlineTop + mVirticalDevideSpace * lineIndex : mlineTop;
        }

        public void addItemView(View itemView) {
            if (list == null) {
                list = new ArrayList<View>();
            }
            list.add(itemView);
        }

        public void caculateItemRect() {
            View itemView;
            int  itemLeft = 0;
            int  preleft  = 0;
            for (int i = 0; i < list.size(); i++) {
                itemView = list.get(i);
                Rect itemRect = new Rect();
                if (i == 0) {
                    measureChild(itemView, 0, 0);
                    mItemWidth = getDecoratedMeasuredWidth(itemView);
                    mItemHeight = getDecoratedMeasuredHeight(itemView);
                    mItemTotalWidth = mItemWidth * list.size() + (list.size() - 1) * minHdevidWithd;
                    averageShareSpce = minHdevidWithd == 0;
                }
                if (list.size() < mLineSize && !averageShareSpce) {
                    //不再均分,采用整体居中
                    itemLeft = (ContributionRankLayoutManager.this.getWidth() - mItemTotalWidth) / 2 + i * mItemWidth;
                    if (i != 0) {
                        itemLeft += i * minHdevidWithd;
                    }
                    itemRect.set(itemLeft, mlineTop, itemLeft + mItemWidth, mlineTop + mItemHeight);
                } else {
                    int lineWidth = ContributionRankLayoutManager.this.getWidth();
                    int middle    = (lineWidth / (list.size() * 2)) * (2 * i + 1);
                    itemLeft = middle - mItemWidth / 2;
                    //获取 最新 水平间距
                    if (i == 1) {
                        minHdevidWithd = minHdevidWithd == 0 ? itemLeft - preleft - mItemWidth : Math.min(minHdevidWithd, itemLeft - preleft - mItemWidth);
                    }
                    preleft = itemLeft;
                    itemRect.set(itemLeft, mlineTop, itemLeft + mItemWidth, mlineTop + mItemHeight);
                }
                itemRects.put(lineIndex * mLineSize + i, itemRect);
            }
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(totalHeight <= getVerticalSpace()){
            return  0;
        }
        if (mVerticalOffset + dy <= 0) {
            dy = -mVerticalOffset;
        } else if (mVerticalOffset + dy >= totalHeight - getVerticalSpace()) {//如果滑动到最底部
            dy = totalHeight - getVerticalSpace() - mVerticalOffset;
        }
        offsetChildrenVertical(-dy);
        detachAndScrapAttachedViews(recycler);
        recycleAndFill(recycler, state);
        mVerticalOffset += dy;
        return dy;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

}
