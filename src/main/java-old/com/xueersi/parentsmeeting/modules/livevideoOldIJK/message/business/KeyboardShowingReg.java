package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by linyuqiang on 2018/7/12.
 */

public interface KeyboardShowingReg {
    void addKeyboardShowing(KeyboardUtil.OnKeyboardShowingListener listener);

    void removeKeyboardShowing(KeyboardUtil.OnKeyboardShowingListener listener);
}
