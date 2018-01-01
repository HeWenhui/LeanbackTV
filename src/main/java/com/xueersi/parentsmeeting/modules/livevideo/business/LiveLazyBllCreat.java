package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;

/**
 * Created by linyuqiang on 2018/1/1.
 * 直播一些类的用的时候加载
 */
public class LiveLazyBllCreat {
    LiveVideoActivity liveVideoActivity;
    RelativeLayout bottomContent;
    LiveBll liveBll;
    private LiveVoteBll liveVoteBll;
    private PraiseOrEncourageBll praiseOrEncourageBll;

    public LiveLazyBllCreat(LiveVideoActivity liveVideoActivity, LiveBll liveBll) {
        this.liveVideoActivity = liveVideoActivity;
        this.liveBll = liveBll;
    }

    public void setBottomContent(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    LiveVoteAction createLiveVoteAction() {
        if (liveVoteBll == null) {
            liveVoteBll = new LiveVoteBll(liveVideoActivity);
            liveVoteBll.initView(bottomContent);
            liveVoteBll.setLiveBll(liveBll);
            liveBll.setLiveVoteAction(liveVoteBll);
        }
        return liveVoteBll;
    }

    PraiseOrEncourageAction createPraiseOrEncourageAction() {
        if (praiseOrEncourageBll == null) {
            praiseOrEncourageBll = new PraiseOrEncourageBll(liveVideoActivity);
            praiseOrEncourageBll.initView(bottomContent);
        }
        return praiseOrEncourageBll;
    }
}
