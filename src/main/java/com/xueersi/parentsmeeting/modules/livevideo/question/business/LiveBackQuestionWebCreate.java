package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionWebX5Pager;

/**
 * @author linyuqiang
 * @date 2018/10/8
 * 创建普通互动题，h5显示页面-回放
 */
public class LiveBackQuestionWebCreate implements QuestionWebCreate {
    @Override
    public QuestionWebX5Pager create(Context context, VideoQuestionLiveEntity videoQuestionLiveEntity, BaseQuestionWebInter.StopWebQuestion questionBll, LiveGetInfo liveGetInfo, boolean IS_SCIENCE, String stuCouId) {
        videoQuestionLiveEntity.setLive(false);
        QuestionWebX5Pager questionWebPager = new QuestionWebX5Pager(context, videoQuestionLiveEntity, questionBll, liveGetInfo
                .getTestPaperUrl(), liveGetInfo.getStuId(), liveGetInfo.getUname(),
                liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(),
                videoQuestionLiveEntity.nonce, liveGetInfo.getIs_show_ranks(), IS_SCIENCE, stuCouId,
                "1".equals(liveGetInfo.getIsAllowTeamPk()));
        return questionWebPager;
    }
}
