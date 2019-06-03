package com.xueersi.parentsmeeting.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.xueersi.lib.log.Loger;
import com.xueersi.ui.component.R;
import com.xueersi.ui.dataload.DataErrorManager;
import com.xueersi.ui.dataload.DataLoadDialog;
import com.xueersi.ui.dataload.DataLoadEntity;

import java.util.Random;

/**
 * 数据加载页面模块
 * Created by ZouHao on 2016/6/21.
 */
public class DataLoadManager {

    /** 全屏锁的Dialog */
    private DataLoadDialog mProgressDialog;

    /** 最小Loading高 */
    private static final int LOADING_MIN_HEIGHT = 220;

    private Context mContext;


    public static DataLoadManager mDataLoadManager;

    public static DataLoadManager newInstance() {
        if (mDataLoadManager == null) {
            mDataLoadManager = new DataLoadManager();
        }
        return mDataLoadManager;
    }

    private DataLoadManager() {
    }


    /**
     * 根据dataLoadEntity属性改变页面中Loading状态
     */
    public void loadDataStyle(Activity activity, DataLoadEntity dataLoadEntity) {
        mContext = activity;
        if (dataLoadEntity.getLoadingType() == DataLoadEntity.LOCK_LOADING_TYPE) {
            //全屏锁的Loading
            switch (dataLoadEntity.getCurrentLoadingStatus()) {
                case DataLoadEntity.BEGIN_DATA_LOADING:
                    //显示Loading
                    if (activity.getClass().getSimpleName().equals(dataLoadEntity.getLockActivitySimpleName())) {
                        showLockLoading(activity, dataLoadEntity.getLoadingTipResource());
                    }
                    break;
                case DataLoadEntity.DATA_PROGRESS:
                    if (mProgressDialog!=null&&mProgressDialog.isShowing()&& !TextUtils.isEmpty(dataLoadEntity.getProgressTipResource())){
                        mProgressDialog.progress(dataLoadEntity.getProgressTipResource());
                    }
                    break;
                default:
                    //关闭Loading
                    if (activity.getClass().getSimpleName().equals(dataLoadEntity.getLockActivitySimpleName())) {
                        hideLockLoading();
                    }
                    break;
            }
        } else if (dataLoadEntity.getLoadingType() == DataLoadEntity.VIEWGROUP_LOADING_TYPE) {
            //局部锁Loading
            switch (dataLoadEntity.getCurrentLoadingStatus()) {
                case DataLoadEntity.BEGIN_DATA_LOADING:
                    //嵌入布局显示 Loading
                    showViewGroupLoading(activity.getWindow().getDecorView(), dataLoadEntity);
                    break;
                case DataLoadEntity.DATA_SUCCESS_CLOSED:
                    //嵌入式布局关闭 Loading
                    hideViewGroupLoading(activity.getWindow().getDecorView(), dataLoadEntity);
                    break;
                case DataLoadEntity.DATA_WEB_ERROR:
                    //嵌入式布局数据加载失败
                    showDataWebError(activity.getWindow().getDecorView(), dataLoadEntity);
                    break;
                case DataLoadEntity.DATA_IS_EMPTY:
                    //嵌入式布局数据加载为空
                    showDataIsEmpty(activity.getWindow().getDecorView(), dataLoadEntity);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadDataStyle(Context context, View vDialog, DataLoadEntity dataLoadEntity) {
        mContext = context;
        if (dataLoadEntity.getLoadingType() == DataLoadEntity.VIEWGROUP_LOADING_TYPE) {
            //局部锁Loading
            switch (dataLoadEntity.getCurrentLoadingStatus()) {
                case DataLoadEntity.BEGIN_DATA_LOADING:
                    //嵌入布局显示 Loading
                    showViewGroupLoading(vDialog, dataLoadEntity);
                    break;
                case DataLoadEntity.DATA_SUCCESS_CLOSED:
                    //嵌入式布局关闭 Loading
                    hideViewGroupLoading(vDialog, dataLoadEntity);
                    break;
                case DataLoadEntity.DATA_WEB_ERROR:
                    //嵌入式布局数据加载失败
                    showDataWebError(vDialog, dataLoadEntity);
                    break;
                case DataLoadEntity.DATA_IS_EMPTY:
                    //嵌入式布局数据加载为空
                    showDataIsEmpty(vDialog, dataLoadEntity);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示全屏锁Loading
     */
    private void showLockLoading(final Context context, final String tipResource) {


        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = new DataLoadDialog(context,tipResource);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * 关闭全屏锁Loading
     */
    private void hideLockLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    /**
     * 显示嵌入式Loading
     *
     * @param rView          当前页面
     * @param dataLoadEntity Loading加载数据对象
     */
    private void showViewGroupLoading(View rView, DataLoadEntity dataLoadEntity) {

        View view = rView.findViewById(dataLoadEntity.getViewGroupResourceID());

        if (view == null) {
            //界面中无此元素退出不响应事件
            return;
        }

        if (dataLoadEntity.isViewGroupAutoHide()) {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_alpha_visable));
        }

        if (view instanceof RelativeLayout) {
            //如果是相对布局，直接内部添加一个Loading
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.findViewById(dataLoadEntity.getViewGroupLoadingID()) != null) {
                //如果存在则表明该布局中已经在Loading了
                return;
            }
            //容器是相对布局
            View loadingView = View.inflate(mContext, R.layout.widget_loading, null);
            ImageView ivLoading = (ImageView) loadingView.findViewById(R.id.iv_data_loading_show);
            ((AnimationDrawable) ivLoading.getBackground()).start();
            loadingView.setId(dataLoadEntity.getViewGroupLoadingID());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, getLoadingHeight(viewGroup.getHeight()));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (dataLoadEntity.getOverrideBackgroundColor() != 0) {
                loadingView.setBackgroundColor(mContext.getResources().getColor(dataLoadEntity
                        .getOverrideBackgroundColor()));
            }
            loadingView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            View errorLayout = viewGroup.findViewById(dataLoadEntity.getViewErrorLayoutResourceID());
            if (errorLayout != null) {
                viewGroup.removeView(errorLayout);
            }
            viewGroup.addView(loadingView, -1, layoutParams);
        } else {
            //窗口是非相对布局,需要外面手动包裹一个相对布局，再覆盖Loading
            ViewGroup relativeGroup = (ViewGroup) view.findViewById(dataLoadEntity.getViewRelativeResourceID());

            if (relativeGroup != null && relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID()) != null) {
                //如果存在则表明正在loading中
                return;
            }

            View loadingView = View.inflate(mContext, R.layout.widget_loading, null);
            ImageView ivLoading = (ImageView) loadingView.findViewById(R.id.iv_data_loading_show);
            ((AnimationDrawable) ivLoading.getBackground()).start();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getLoadingHeight(view
                    .getHeight())
                    , getLoadingHeight(view.getHeight()));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            loadingView.setId(dataLoadEntity.getViewGroupLoadingID());
            loadingView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            if (relativeGroup != null) {
                View errorLayout = relativeGroup.findViewById(dataLoadEntity.getViewErrorLayoutResourceID());
                if (errorLayout != null) {
                    relativeGroup.removeView(errorLayout);
                    relativeGroup.addView(loadingView, layoutParams);
                    if (dataLoadEntity.getOverrideBackgroundColor() != 0) {
                        loadingView.setBackgroundColor(mContext.getResources().getColor(dataLoadEntity
                                .getOverrideBackgroundColor()));

                    }
                    return;
                }
            }
            relativeGroup = new RelativeLayout(mContext);
            relativeGroup.setId(new Random().nextInt(89999) + 10000);
            dataLoadEntity.setViewRelativeResourceID(relativeGroup.getId());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            ViewGroup rootView = ((ViewGroup) view.getParent());

            int index = 0;
            int rootCount = rootView.getChildCount();
            for (int i = 0; i < rootCount; i++) {
                index = i;
                if (rootView.getChildAt(i).equals(view)) {
                    break;
                }
            }
            dataLoadEntity.setRelativeIndex(index);
            if (dataLoadEntity.getOverrideBackgroundColor() != 0) {
                loadingView.setBackgroundColor(mContext.getResources().getColor(dataLoadEntity
                        .getOverrideBackgroundColor()));
            }

            rootView.removeView(view);
            relativeGroup.addView(view);
            rootView.addView(relativeGroup, index, params);
            relativeGroup.addView(loadingView, layoutParams);
        }
    }

    /**
     * 关闭嵌入式布局中的Loadingø
     *
     * @param rView
     * @param dataLoadEntity
     */
    private void hideViewGroupLoading(View rView, DataLoadEntity dataLoadEntity) {

        final View view = rView.findViewById(dataLoadEntity.getViewGroupResourceID());

        if (view == null) {
            //界面中无此元素退出不响应事件
            return;
        }

        if (dataLoadEntity.isViewGroupAutoHide()) {
            view.setVisibility(View.GONE);
        }

        if (view instanceof RelativeLayout) {
            //相对布局
            ViewGroup viewGroup = (ViewGroup) view;
            View loadingView = viewGroup.findViewById(dataLoadEntity.getViewGroupLoadingID());
            if (loadingView == null) {
                //如果不存在则表明该布局中无loading
                return;
            }
            viewGroup.removeView(loadingView);
        } else {
            //线性布局
            ViewGroup relativeGroup = (ViewGroup) rView.findViewById(dataLoadEntity.getViewRelativeResourceID());
            if (relativeGroup == null || relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID()) == null) {
                //表明没有正在Loading
                return;
            }
            View loadingView = relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID());
            relativeGroup.removeView(loadingView);
            ViewGroup rootView = ((ViewGroup) relativeGroup.getParent());
            relativeGroup.removeView(view);
            rootView.removeView(relativeGroup);
            if (view.getParent() != null) {
                ViewGroup vParent = (ViewGroup) view.getParent();
                vParent.removeView(view);
            }
            rootView.addView(view, dataLoadEntity.getRelativeIndex());
        }
    }

    /**
     * 显示加载错误页
     *
     * @param rView
     * @param dataLoadEntity
     */
    private void showDataWebError(View rView, DataLoadEntity dataLoadEntity) {

        ViewGroup viewGroup = (ViewGroup) rView.findViewById(dataLoadEntity.getViewGroupResourceID());

        if (viewGroup == null) {
            //界面中无此元素退出不响应事件
            return;
        }

        View vErrorLayout = DataErrorManager.newInstance().getErrorDataLayout(mContext, dataLoadEntity
                        .getErrorLayoutType(), DataErrorManager.WEB_ERROR, dataLoadEntity.getWebErrorTipResource(),
                dataLoadEntity.getDataIsEmptyTipResource(), dataLoadEntity.getOverrideBackgroundColor(),
                dataLoadEntity.getRunableRefresh(), false);
        if (viewGroup instanceof RelativeLayout) {
            //相对布局
            View loadingView = viewGroup.findViewById(dataLoadEntity.getViewGroupLoadingID());
            Loger.i("LoadingTestDemo", "showDataWebError");
            if (loadingView == null) {
                //如果不存在则表明该布局中无loading
                return;
            }
            vErrorLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            viewGroup.removeView(loadingView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, getLoadingHeight(viewGroup.getHeight()));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            dataLoadEntity.setViewErrorLayoutResourceID(vErrorLayout.getId());
            viewGroup.addView(vErrorLayout, -1, layoutParams);
        } else {
            //线性布局
            ViewGroup relativeGroup = (ViewGroup) rView.findViewById(dataLoadEntity.getViewRelativeResourceID());
            if (relativeGroup == null || relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID()) == null) {
                //表明没有正在Loading
                return;
            }

            View loadingView = relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID());
            relativeGroup.removeView(loadingView);
            dataLoadEntity.setViewErrorLayoutResourceID(vErrorLayout.getId());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(viewGroup.getWidth(),
                    getLoadingHeight(viewGroup.getHeight()));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeGroup.addView(vErrorLayout, layoutParams);
        }
    }

    /**
     * 显示数据为空的页面
     *
     * @param rView
     * @param dataLoadEntity
     */
    private void showDataIsEmpty(View rView, DataLoadEntity dataLoadEntity) {
        ViewGroup viewGroup = (ViewGroup) rView.findViewById(dataLoadEntity.getViewGroupResourceID());

        if (viewGroup == null) {
            //界面中无此元素退出不响应事件
            return;
        }

        View vErrorLayout = DataErrorManager.newInstance().getErrorDataLayout(mContext, dataLoadEntity
                        .getErrorLayoutType(), DataErrorManager.DATA_IS_EMPTY, dataLoadEntity.getWebErrorTipResource(),
                dataLoadEntity.getDataIsEmptyTipResource(), dataLoadEntity.getOverrideBackgroundColor(),
                dataLoadEntity.getRunableRefresh(), false);

        if (viewGroup instanceof RelativeLayout) {
            //相对布局
            View loadingView = viewGroup.findViewById(dataLoadEntity.getViewGroupLoadingID());
            if (loadingView == null) {
                //如果不存在则表明该布局中无loading
                return;
            }
            viewGroup.removeView(loadingView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, getLoadingHeight(viewGroup.getHeight()));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            dataLoadEntity.setViewErrorLayoutResourceID(vErrorLayout.getId());
            viewGroup.addView(vErrorLayout, -1, layoutParams);

        } else {
            //线性布局
            ViewGroup relativeGroup = (ViewGroup) rView.findViewById(dataLoadEntity.getViewRelativeResourceID());
            if (relativeGroup == null || relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID()) == null) {
                //表明没有正在Loading
                return;
            }

            View loadingView = relativeGroup.findViewById(dataLoadEntity.getViewGroupLoadingID());
            relativeGroup.removeView(loadingView);
            dataLoadEntity.setViewErrorLayoutResourceID(vErrorLayout.getId());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(viewGroup.getWidth(),
                    getLoadingHeight(viewGroup.getHeight()));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeGroup.addView(vErrorLayout, layoutParams);
        }
    }

    /**
     * 返回Loading布局占的高
     *
     * @param viewGroupHeight
     * @return
     */
    private int getLoadingHeight(int viewGroupHeight) {
        return viewGroupHeight == 0 ? RelativeLayout.LayoutParams.MATCH_PARENT : viewGroupHeight <=
                LOADING_MIN_HEIGHT ? LOADING_MIN_HEIGHT : viewGroupHeight;
    }
}
