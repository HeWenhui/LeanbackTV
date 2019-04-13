package com.xueersi.parentsmeeting.modules.livevideo.fragment.se;

/**
 * 全身直播体验课，课后弹窗业务逻辑处理Presenter
 */
public interface IExperiencePresenter {
    //当前Presenter需要显示的View
    void showWindow();

    //移除当前显示的View
    void removeWindow();

    //显示下一个View。这里的View是一个接着一个展示的形式，前一个关闭，紧接着下一个就出来。
    void showNextWindow();
}
