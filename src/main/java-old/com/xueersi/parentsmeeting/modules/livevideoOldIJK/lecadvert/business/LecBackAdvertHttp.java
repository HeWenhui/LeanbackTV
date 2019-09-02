package com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;

/**
 * Created by linyuqiang on 2018/7/31.
 */

public interface LecBackAdvertHttp {
    void getAdOnLL(String liveId, final LecAdvertEntity lecAdvertEntity, final AbstractBusinessDataCallBack callBack);

    void getMoreCourseChoices(String liveid, AbstractBusinessDataCallBack getDataCallBack);
}
