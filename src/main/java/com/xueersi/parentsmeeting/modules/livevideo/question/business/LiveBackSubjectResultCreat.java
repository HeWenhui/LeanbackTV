package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SubjectResultX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by linyuqiang on 2018/7/26.
 * 直播回放的互动题结果页
 */
public class LiveBackSubjectResultCreat implements BaseSubjectResultCreat {
    private LiveGetInfo liveGetInfo;

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    @Override
    public SubjectResultX5Pager creat(final Context context, String testPaperUrl, String stuId, String liveid, final VideoQuestionLiveEntity videoQuestionLiveEntity, String stuCouId, final LiveBasePager.OnPagerClose onPagerClose) {
        SubjectResultX5Pager subjectResultPager = new SubjectResultX5Pager(context, videoQuestionLiveEntity,
                liveGetInfo.getSubjectiveTestAnswerResult(),
                liveGetInfo.getStuId(), liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(),
                stuCouId);
        subjectResultPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
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
        return subjectResultPager;
    }
}
