package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;

/**
*战队pk 基类pager
*@author chekun
*created  at 2019/2/17 13:19
*/
public class TeamPkBasePager extends BasePager {

    private boolean fullScreenMode = false;

    public TeamPkBasePager(Context context){
        super(context);
    }

    /***
     * 构造函数
     *
     * @param context   上下文对象
     * @param isNewView 是否初始化布局
     */
    public TeamPkBasePager(Context context, boolean isNewView) {
        super(context,isNewView);
    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {

    }

    /**
     * 是否是全屏模式展示
     * @return
     */
    public boolean isFullScreenMode(){
        return fullScreenMode;
    }

    /**
     * 设置是否全屏展示
     * @param fullScreenMode
     */
    public void setFullScreenMode(boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
    }
}
