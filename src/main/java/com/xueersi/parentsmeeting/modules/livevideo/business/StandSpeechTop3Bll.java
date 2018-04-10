package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.StandSpeechTop3Pager;

/**
 * Created by linyuqiang on 2018/4/10.
 */

public class StandSpeechTop3Bll implements SpeechEndAction {
    LiveBll liveBll;
    StandSpeechTop3Pager standSpeechTop3Pager;
    RelativeLayout bottomContent;
    GoldTeamStatus entity;
    boolean stop = false;

    public StandSpeechTop3Bll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    @Override
    public void examSubmitAll(final String num) {
        bottomContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                liveBll.getSpeechEvalAnswerTeamRank(num, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        entity = (GoldTeamStatus) objData[0];
                        if (stop) {
                            onStopSpeech(num);
                        }
                    }
                });
            }
        }, 3000);
    }

    @Override
    public void onStopSpeech(String num) {
        if (entity == null) {
            return;
        }
        stop = true;
        initTop();
    }

    private void initTop() {
        standSpeechTop3Pager = new StandSpeechTop3Pager(bottomContent.getContext(), entity);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(standSpeechTop3Pager.getRootView(), lp);
    }

}
