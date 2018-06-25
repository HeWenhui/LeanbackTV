package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/5/7.
 * 直播的一些公共方法
 */
public abstract class LiveFragmentBase extends LiveVideoFragmentBase {
    public abstract AtomicBoolean getStartRemote();

    public abstract void stopPlay();

    public abstract void rePlay(boolean b);
}
