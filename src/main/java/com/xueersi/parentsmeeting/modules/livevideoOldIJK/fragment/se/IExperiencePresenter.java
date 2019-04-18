package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se;

public interface IExperiencePresenter {
    //当前Presenter需要显示的View
    void showWindow();

    //移出当前显示的View
    void removeWindow();

    //显示下一个View。这里的View是一个接着一个展示的形式，前一个关闭，紧接着下一个就出来。
    void showNextWindow();
}
