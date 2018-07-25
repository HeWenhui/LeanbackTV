package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5Pager;

import java.util.ArrayList;

/**
 * Created by lyqai on 2018/7/25.
 */

public class LiveExamQuestionCreat implements BaseExamQuestionCreat {
    private boolean IS_SCIENCE;
    private LiveGetInfo liveGetInfo;
    private AnswerRankBll mAnswerRankBll;
    QuestionHttp questionHttp;

    public void setIS_SCIENCE(boolean IS_SCIENCE) {
        this.IS_SCIENCE = IS_SCIENCE;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setmAnswerRankBll(AnswerRankBll mAnswerRankBll) {
        this.mAnswerRankBll = mAnswerRankBll;
    }

    public void setQuestionHttp(QuestionHttp questionHttp) {
        this.questionHttp = questionHttp;
    }

    @Override
    public BaseExamQuestionInter creatBaseExamQuestion(Activity activity, QuestionBll questionBll, final String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (mAnswerRankBll != null) {
            mAnswerRankBll.showRankList(new ArrayList<RankUserEntity>(), XESCODE.EXAM_STOP);
            questionHttp.sendRankMessage(XESCODE.RANK_STU_RECONNECT_MESSAGE);
        }
        BaseExamQuestionInter examQuestionPager = new ExamQuestionX5Pager(activity, questionBll, liveGetInfo.getStuId
                (), liveGetInfo.getUname(), liveid, videoQuestionLiveEntity , mAnswerRankBll == null ? "0" : mAnswerRankBll
                .getIsShow(), IS_SCIENCE, liveGetInfo.getStuCouId(), 0);
        return examQuestionPager;
    }
}
