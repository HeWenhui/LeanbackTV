package com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;

/**
 * Created by lyqai on 2018/7/18.
 */

public interface LecAdvertHttp {
    void getAdOnLL(LecAdvertEntity lecAdvertEntity, AbstractBusinessDataCallBack abstractBusinessDataCallBack);
}
