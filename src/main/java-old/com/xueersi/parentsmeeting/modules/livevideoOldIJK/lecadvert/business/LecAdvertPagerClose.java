package com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;

/**
 * Created by linyuqiang on 2018/1/15.
 */
public interface LecAdvertPagerClose {
    void close(boolean land);

    void onPaySuccess(LecAdvertEntity lecAdvertEntity);
}
