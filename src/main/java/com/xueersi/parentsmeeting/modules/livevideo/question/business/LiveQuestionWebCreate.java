package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionWebX5Pager;

/**
 * @author linyuqiang
 * @date 2018/10/8
 * 创建普通互动题，h5显示页面-直播
 */
public class LiveQuestionWebCreate implements QuestionWebCreate {
    @Override
    public QuestionWebX5Pager create(Context context, VideoQuestionLiveEntity videoQuestionLiveEntity, BaseQuestionWebInter.StopWebQuestion questionBll, LiveGetInfo liveGetInfo, int isArts, String stuCouId) {
        videoQuestionLiveEntity.setLive(true);
        QuestionWebX5Pager questionWebPager = new QuestionWebX5Pager(context, videoQuestionLiveEntity, questionBll, liveGetInfo
                .getTestPaperUrl(), liveGetInfo.getStuId(), liveGetInfo.getUname(),
                liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(),
                videoQuestionLiveEntity.nonce, liveGetInfo.getIs_show_ranks(), isArts, stuCouId,
                "1".equals(liveGetInfo.getIsAllowTeamPk()));
        return questionWebPager;
    }
}
