package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business;

import android.view.View;

/**
 * NB 实验 加载页面  行为定义
 *
 * @author chekun
 * created  at 2019/4/15 16:54
 */
public interface NbH5PagerAction {

    /** 页面加载的Url地址 **/
    public String getUrl();

    /**
     * 获取页面根布局
     *
     * @return
     */
    public View getRootView();

    /**
     * 获取实验id
     *
     * @return
     */
    public String getTestId();

    /**
     * 提交试题
     */
    public void submitData();

    /**
     * 点击系统返回键
     *
     * @return
     */
    public boolean onBack();

    /**
     * 为free实验新加方法，在登陆成功后，加载url
     */
    void loadUrl();

    /**
     * 销毁
     *
     * @return
     */
    public void destroy();
}
