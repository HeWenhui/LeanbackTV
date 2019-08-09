package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;

import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5PlaybackPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by linyuqiang on 2018/7/25.
 * 回放测试卷
 */
public class LiveBackExamQuestionCreat implements BaseExamQuestionCreat {
    private int isArts;
    private LiveGetInfo liveGetInfo;
    private Context context;
    private LivePagerBack livePagerBack;

    public void setLivePagerBack(Context context, LivePagerBack livePagerBack) {
        this.context = context;
        this.livePagerBack = livePagerBack;
    }

    public void setArts(int arts) {
        this.isArts = arts;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    @Override
    public BaseExamQuestionInter creatBaseExamQuestion(Activity activity, final String liveid, final VideoQuestionLiveEntity videoQuestionLiveEntity, LivePagerBack livePagerBack, final LiveBasePager.OnPagerClose onPagerClose) {
        ExamQuestionX5PlaybackPager examQuestionPlaybackPager = new ExamQuestionX5PlaybackPager(activity,
                liveid, videoQuestionLiveEntity, isArts, liveGetInfo.getStuCouId(), livePagerBack);
        examQuestionPlaybackPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                onPagerClose.onClose(basePager);
                LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
                showQuestion.onHide(videoQuestionLiveEntity);
                BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, BackMediaPlayerControl.class);
                mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
                mediaPlayerControl.start();
            }
        });
        examQuestionPlaybackPager.setLivePagerBack(livePagerBack);
        return examQuestionPlaybackPager;
    }
}
