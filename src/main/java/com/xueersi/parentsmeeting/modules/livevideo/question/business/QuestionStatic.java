package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

/**
 * Created by linyuqiang on 2018/7/12.
 * 互动题状态
 */
public interface QuestionStatic extends LiveProvide {
    boolean isAnaswer();

    void setVideoCachedDuration(long videoCachedDuration);
}
