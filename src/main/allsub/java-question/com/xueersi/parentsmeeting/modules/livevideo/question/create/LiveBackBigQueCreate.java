package com.xueersi.parentsmeeting.modules.livevideo.question.create;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveBigQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BigQuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BigQuestionSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by linyuqiang on 2019/4/21.
 * 直播大题互动创建
 */
public class LiveBackBigQueCreate implements BigQueCreate {
    private Activity activity;
    private QuestionSecHttp questionSecHttp;

    public LiveBackBigQueCreate(Activity activity, QuestionSecHttp questionSecHttp) {
        this.activity = activity;
        this.questionSecHttp = questionSecHttp;
    }

    @Override
    public BaseLiveBigQuestionPager create(final VideoQuestionLiveEntity videoQuestionLiveEntity, LiveViewAction liveViewAction, final LiveBasePager.OnPagerClose onPagerClose, OnSubmit onSubmit) {
        if (videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_SELE || videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_MUL_SELE) {
            BigQuestionSelectLivePager bigQuestionSelectLivePager = new BigQuestionSelectLivePager(activity, videoQuestionLiveEntity);
            bigQuestionSelectLivePager.setQuestionSecHttp(questionSecHttp);
            bigQuestionSelectLivePager.setRlQuestionResContent(liveViewAction);
            bigQuestionSelectLivePager.setOnSubmit(onSubmit);
            bigQuestionSelectLivePager.setPlayback(true);
            bigQuestionSelectLivePager.setOnPagerClose(new LiveBasePager.WrapOnPagerClose(onPagerClose) {
                @Override
                public void onClose(LiveBasePager basePager) {
                    onPagerClose.onClose(basePager);
                    BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(activity, BackMediaPlayerControl.class);
                    if (mediaPlayerControl != null) {
                        mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                    }
                }
            });
            return bigQuestionSelectLivePager;
        } else if (videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_FILL) {
            BigQuestionFillInBlankLivePager bigQuestionFillInBlankLivePager = new BigQuestionFillInBlankLivePager(activity, videoQuestionLiveEntity, true);
            bigQuestionFillInBlankLivePager.setQuestionSecHttp(questionSecHttp);
            bigQuestionFillInBlankLivePager.setRlQuestionResContent(liveViewAction);
            bigQuestionFillInBlankLivePager.setOnSubmit(onSubmit);
            bigQuestionFillInBlankLivePager.setOnPagerClose(new LiveBasePager.WrapOnPagerClose(onPagerClose) {
                @Override
                public void onClose(LiveBasePager basePager) {
                    onPagerClose.onClose(basePager);
                    BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(activity, BackMediaPlayerControl.class);
                    if (mediaPlayerControl != null) {
                        mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                    }
                }
            });
            return bigQuestionFillInBlankLivePager;
        }
        return null;
    }

}
