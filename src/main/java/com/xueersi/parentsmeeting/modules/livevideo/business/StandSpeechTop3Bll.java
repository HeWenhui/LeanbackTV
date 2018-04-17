package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechAssessmentWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.StandSpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.StandSpeechTop3Pager;
import com.xueersi.xesalib.utils.log.Loger;

/**
 * Created by linyuqiang on 2018/4/10.
 * 语音评测结束后调排行榜
 */
public class StandSpeechTop3Bll implements SpeechEndAction {
    String TAG = "StandSpeechTop3Bll";
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
    public void examSubmitAll(final BaseSpeechAssessmentPager speechAssessmentPager, final String num) {
        bottomContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                //原生语音评测
                if (speechAssessmentPager instanceof StandSpeechAssAutoPager) {
                    liveBll.getSpeechEvalAnswerTeamRank(num, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            entity = (GoldTeamStatus) objData[0];
                            Loger.d(TAG, "getSpeechEvalAnswerTeamRank:stop=" + stop);
                            if (stop) {
                                onStopSpeech(speechAssessmentPager, num);
                            }
                        }
                    });
                    /** 语音评测 roleplay */
                } else if (speechAssessmentPager instanceof SpeechAssessmentWebPager) {
                    liveBll.getRolePlayAnswerTeamRank(num, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            entity = (GoldTeamStatus) objData[0];
                            Loger.d(TAG, "getRolePlayAnswerTeamRank:stop=" + stop);
                            if (stop) {
                                onStopSpeech(speechAssessmentPager, num);
                            }
                        }
                    });
                }
            }
        }, 3000);
    }

    @Override
    public void onStopSpeech(BaseSpeechAssessmentPager speechAssessmentPager, String num) {
        stop = true;
        Loger.d(TAG, "onStopSpeech:entity=" + (entity == null));
        if (entity == null) {
            return;
        }
        initTop(num);
    }

    private void initTop(String num) {
        if (standSpeechTop3Pager != null && num.equals(standSpeechTop3Pager.getId())) {
            Loger.d(TAG, "initTop:num=" + num);
            return;
        }
        standSpeechTop3Pager = new StandSpeechTop3Pager(bottomContent.getContext(), entity);
        standSpeechTop3Pager.setId(num);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(standSpeechTop3Pager.getRootView(), lp);
        entity = null;
        stop = false;
    }

}
