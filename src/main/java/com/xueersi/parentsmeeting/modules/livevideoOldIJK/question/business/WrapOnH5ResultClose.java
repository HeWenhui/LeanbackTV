package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

/**
 * Created by lyqai on 2018/7/26.
 */

public class WrapOnH5ResultClose implements EnglishH5CoursewareBll.OnH5ResultClose {
    EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose;
    VideoQuestionLiveEntity videoQuestionH5Entity;
    Context context;

    public WrapOnH5ResultClose(Context context) {
        this.context = context;
    }

    public void setOnH5ResultClose(EnglishH5CoursewareBll.OnH5ResultClose onH5ResultClose) {
        this.onH5ResultClose = onH5ResultClose;
    }

    public void setVideoQuestionH5Entity(VideoQuestionLiveEntity videoQuestionH5Entity) {
        this.videoQuestionH5Entity = videoQuestionH5Entity;
    }

    @Override
    public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        onH5ResultClose.onH5ResultClose(baseEnglishH5CoursewarePager, baseVideoQuestionEntity);
        BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, BackMediaPlayerControl.class);
        if (mediaPlayerControl != null) {
            mediaPlayerControl.seekTo(videoQuestionH5Entity.getvEndTime() * 1000);
            mediaPlayerControl.start();
        }
        LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
        showQuestion.onHide(baseVideoQuestionEntity);
    }
}
