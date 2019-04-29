package com.xueersi.parentsmeeting.modules.livevideo.question.create;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveBigQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BigQuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BigQuestionSelectLivePager;

/**
 * Created by linyuqiang on 2019/4/21.
 * 直播大题互动创建
 */
public class LiveBigQueCreate implements BigQueCreate {
    private Activity activity;
    private QuestionSecHttp questionSecHttp;

    public LiveBigQueCreate(Activity activity, QuestionSecHttp questionSecHttp) {
        this.activity = activity;
        this.questionSecHttp = questionSecHttp;
    }

    @Override
    public BaseLiveBigQuestionPager create(VideoQuestionLiveEntity videoQuestionLiveEntity, RelativeLayout rlQuestionResContent, LiveBasePager.OnPagerClose onPagerClose, OnSubmit onSubmit) {
        if (videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_SELE || videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_MUL_SELE) {
            BigQuestionSelectLivePager bigQuestionSelectLivePager = new BigQuestionSelectLivePager(activity, videoQuestionLiveEntity);
            bigQuestionSelectLivePager.setQuestionSecHttp(questionSecHttp);
            bigQuestionSelectLivePager.setRlQuestionResContent(rlQuestionResContent);
            bigQuestionSelectLivePager.setOnPagerClose(onPagerClose);
            bigQuestionSelectLivePager.setOnSubmit(onSubmit);
            return bigQuestionSelectLivePager;
        } else if (videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_FILL) {
            BigQuestionFillInBlankLivePager bigQuestionFillInBlankLivePager = new BigQuestionFillInBlankLivePager(activity, videoQuestionLiveEntity);
            bigQuestionFillInBlankLivePager.setQuestionSecHttp(questionSecHttp);
            bigQuestionFillInBlankLivePager.setRlQuestionResContent(rlQuestionResContent);
            bigQuestionFillInBlankLivePager.setOnPagerClose(onPagerClose);
            bigQuestionFillInBlankLivePager.setOnSubmit(onSubmit);
            return bigQuestionFillInBlankLivePager;
        }
        return null;
    }

}
