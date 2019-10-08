package com.xueersi.parentsmeeting.modules.livevideo.widget;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
/**
*回放 互动题点击事件
*@author chekun
*created  at 2019/8/14 12:20
*/
public interface OnPointClick {

    void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position);
}
