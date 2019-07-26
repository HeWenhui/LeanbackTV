package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

/**
 * Created by linyuqiang on 2018/7/27.
 */

public interface MediaControllerAction extends LiveProvide {
    void attachMediaController();

    void release();
}
