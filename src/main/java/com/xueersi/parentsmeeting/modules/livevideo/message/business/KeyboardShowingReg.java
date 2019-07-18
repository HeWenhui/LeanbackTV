package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by linyuqiang on 2018/7/12.
 */

public interface KeyboardShowingReg extends LiveProvide {
    void addKeyboardShowing(KeyboardUtil.OnKeyboardShowingListener listener);

    void removeKeyboardShowing(KeyboardUtil.OnKeyboardShowingListener listener);
}
