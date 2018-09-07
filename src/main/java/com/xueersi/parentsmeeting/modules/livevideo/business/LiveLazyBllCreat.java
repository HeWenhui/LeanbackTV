package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseListAction;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseListBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;

/**
 * Created by linyuqiang on 2018/1/1.
 * 直播一些类的用的时候加载
 */
public class LiveLazyBllCreat {
    Activity liveVideoActivity;
    RelativeLayout bottomContent;
    RelativeLayout praiselistContent;
    LiveBll liveBll;
    private LiveVoteBll liveVoteBll;
    private PraiseOrEncourageBll praiseOrEncourageBll;
    private LiveGetInfo liveGetInfo;
    private PraiseListBll praiseListBll;
    private QuestionBll questionBll;

    public LiveLazyBllCreat(Activity liveVideoActivity, LiveBll liveBll) {
        this.liveVideoActivity = liveVideoActivity;
        this.liveBll = liveBll;
    }

    public void setBottomContent(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    public void setPraiselistContent(RelativeLayout praiselistContent) {
        this.praiselistContent = praiselistContent;
    }

    public void setQuestionBll(QuestionBll questionBll) {
        this.questionBll = questionBll;
    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.liveGetInfo = getInfo;
    }

    LiveVoteAction createLiveVoteAction() {
        return liveVoteBll;
    }

    PraiseOrEncourageAction createPraiseOrEncourageAction() {
        if (praiseOrEncourageBll == null) {
            praiseOrEncourageBll = new PraiseOrEncourageBll(liveVideoActivity);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    praiseOrEncourageBll.initView(bottomContent);
                    praiseOrEncourageBll.onLiveInit(liveGetInfo);
                }
            });
            liveBll.setPraiseOrEncourageAction(praiseOrEncourageBll);
        }
        return praiseOrEncourageBll;
    }

    AnswerRankBll createAnswerRankBll() {
        AnswerRankBll answerRankBll = new AnswerRankBll(liveVideoActivity, liveBll);
        answerRankBll.initView(bottomContent);
        return answerRankBll;
    }

    public LiveAutoNoticeBll createAutoNoticeBll() {
        return new LiveAutoNoticeBll(liveVideoActivity, null, bottomContent);
    }

    RolePlayAction createRolePlayBll() {
        RolePlayerBll rolePlayerBll = new RolePlayerBll(liveVideoActivity, bottomContent, liveBll, liveGetInfo);
        questionBll.setRolePlayAction(rolePlayerBll);
        return rolePlayerBll;
    }

    PraiseListAction createPraiseListAction() {

        if (praiseListBll == null) {
            praiseListBll = new PraiseListBll(liveVideoActivity);
            praiselistContent.post(new Runnable() {
                @Override
                public void run() {
                    praiseListBll.initView(praiselistContent);

                }
            });
            //praiseListBll.setLiveBll(liveBll);
            liveBll.setPraiseListAction(praiseListBll);
        }
        return praiseListBll;
    }

    public TeamPkBll createTeamPkBll() {
        return null;//new TeamPkBll(liveVideoActivity, bottomContent);
    }

}
