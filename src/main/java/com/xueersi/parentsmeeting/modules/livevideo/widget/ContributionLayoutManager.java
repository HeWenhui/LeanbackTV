package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 18/4/21 下午8:45
 */


public class ContributionLayoutManager extends RecyclerView.LayoutManager {

    private static final String Tag = "ContributionLayoutManager";
    public int mLineSize;  //每行item个数
    private int lineNum; //行数
    private SparseArray<Rect> allItemRects = new SparseArray<Rect>();
    private SparseBooleanArray itemVisibilities = new SparseBooleanArray();


    private int totalHeight;

    public ContributionLayoutManager(int lineSize) {
        mLineSize = lineSize;
    }

    List<Line> lines;
    private int minHdevidWithd =20;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        layoutChild(recycler);
        recycleAndFill(recycler, state);

    }


    private int mVerticalOffset;//竖直方向上的滚动偏移量

    private void layoutChild(RecyclerView.Recycler recycler) {
        //清空之前的view
        detachAndScrapAttachedViews(recycler);
        calculateChildrenSite(recycler);

    }


    /**
     * 计算子view 的摆放位置
     */
    private void calculateChildrenSite(RecyclerView.Recycler recycler) {
        View firstView = recycler.getViewForPosition(0);
        lineNum = 0;
        //获取 itemView 的尺寸信息
        measureChild(firstView, 0, 0);
        int itemHeight = getDecoratedMeasuredHeight(firstView);
        lines = new ArrayList<Line>();
        Line line = null;
        for (int i = 0; i < getItemCount(); i++) {
            if (i % mLineSize == 0) {
                line = new Line(0, lineNum * itemHeight,lineNum);
                lines.add(line);
                lineNum++;
            }
            line.addItemView(firstView);
        }
        totalHeight = lineNum * itemHeight;
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).layoutItemView();
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mVerticalOffset + dy < 0) {
            dy = -mVerticalOffset;
        }else if(totalHeight <= getVerticalSpace()){
            dy = 0;// 内容区间 小于 recyleview 的空间不支持向上滑动
        } else if (mVerticalOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
            dy = totalHeight - getVerticalSpace() - mVerticalOffset;
        }
        offsetChildrenVertical(-dy);
        recycleAndFill(recycler, state); //回收并显示View
        mVerticalOffset += dy;
        return dy;
    }


    private void recycleAndFill(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        // 当前scroll offset状态下的显示区域
        Rect displayRect = new Rect(0, mVerticalOffset, getHorizontalSpace(),  mVerticalOffset + getVerticalSpace());
        //重新显示需要出现在屏幕的子View
        for (int i = 0; i < getItemCount(); i++) {
            //判断ItemView的位置和当前显示区域是否重合
            if (Rect.intersects(displayRect, allItemRects.get(i))) {
                //获得Recycler中缓存的View
                View itemView = recycler.getViewForPosition(i);//
                measureChildWithMargins(itemView, 0, 0);
                //添加View到RecyclerView上
                addView(itemView);
                //取出先前存好的ItemView的位置矩形
                Rect rect = allItemRects.get(i);
                //将这个item布局出来
                layoutDecoratedWithMargins(itemView,
                        rect.left,
                        rect.top - mVerticalOffset,
                        rect.right,
                        rect.bottom - mVerticalOffset);
                itemVisibilities.put(i, true); //更新该View的状态为依附
            }
        }
    }

    int itemWith;

    public void setItemWidth(int width){
        itemWith = width;
    }


    private class Line {
        private List<View> list;
        private int mlineTop;
        private int mItemWidth;
        private int mItemHeight;
        private int mItemTotalWidth;
        private boolean averageShareSpce = true;
        private int lineIndex;

        public Line(int lineLeft, int lineTop,int index) {
            mlineTop = lineTop;
            lineIndex = index;

        }

        public void addItemView(View itemView) {

            if (list == null) {
                list = new ArrayList<View>();
            }
            list.add(itemView);
        }


        public void layoutItemView() {
            View itemView;
            int itemLeft = 0;
            int preleft = 0;

            for (int i = 0; i < list.size(); i++) {
                itemView = list.get(i);

                Rect mTmpRect =  mTmpRect = new Rect();
                if (i == 0) {
                    measureChild(itemView, 0, 0);
                    mItemWidth = itemWith;//getDecoratedMeasuredWidth(itemView);
                    mItemHeight = getDecoratedMeasuredHeight(itemView);
                    mItemTotalWidth = mItemWidth * list.size(); //+  minHdevidWithd;
                    averageShareSpce = minHdevidWithd == 0;
                }

                if (list.size() < mLineSize) {
                    //不再均分,采用整体居中
                    itemLeft = (ContributionLayoutManager.this.getWidth() - mItemTotalWidth) / 2 + i * mItemWidth;
                    if (i > 0) {
                        //itemLeft += minHdevidWithd;
                    }
                    mTmpRect.set(itemLeft, mlineTop, itemLeft + mItemWidth, mlineTop + mItemHeight);


                } else {
                    int lineWidth = ContributionLayoutManager.this.getWidth();
                    int middle = (lineWidth / (list.size() * 2)) * (2 * i + 1);
                    itemLeft = middle - mItemWidth / 2;
                    //获取 最新 水平间距
                    if (i == 1) {
                        minHdevidWithd = minHdevidWithd == 0 ? itemLeft - preleft - mItemWidth : Math.min(minHdevidWithd, itemLeft - preleft - mItemWidth);
                    }
                    preleft = itemLeft;
                    //SignLayoutManager.this.layoutDecorated(itemView, itemLeft, mlineTop, itemLeft + mItemWidth, mlineTop + mItemHeight);
                    mTmpRect.set(itemLeft, mlineTop, itemLeft + mItemWidth, mlineTop + mItemHeight);
                }
                allItemRects.put(lineIndex*mLineSize+i, mTmpRect);
                itemVisibilities.put(i, false);
            }
        }

    }


    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }


    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

}
