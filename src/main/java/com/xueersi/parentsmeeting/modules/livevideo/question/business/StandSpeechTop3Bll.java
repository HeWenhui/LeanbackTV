package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayStandMachinePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechAssessmentWebX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.StandSpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.StandSpeechTop3Pager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.SpeechStandLog;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/4/10.
 * 语音评测结束后调排行榜
 */
public class StandSpeechTop3Bll implements SpeechEndAction {
    String TAG = "StandSpeechTop3Bll";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    SpeechEvalHttp questionIRCBll;
    LiveAndBackDebug liveAndBackDebug;
    StandSpeechTop3Pager standSpeechTop3Pager;
    RelativeLayout bottomContent;
    GoldTeamStatus entity;
    boolean stop = false;
    HashMap<String, GoldTeamStatus> goldTeamStatusHashMap = new HashMap<>();
    HashMap<String, OnTop3End> top3EndHashMap = new HashMap<>();
    LogToFile logToFile;

    public StandSpeechTop3Bll(Context context, SpeechEvalHttp questionIRCBll, LiveAndBackDebug liveAndBackDebug) {
        this.questionIRCBll = questionIRCBll;
        this.liveAndBackDebug = liveAndBackDebug;
        logToFile = new LogToFile(context, TAG);
    }

    @Override
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
                    questionIRCBll.getSpeechEvalAnswerTeamRank(num, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            entity = (GoldTeamStatus) objData[0];
                            entity.setId(num);
                            goldTeamStatusHashMap.put(num, entity);
                            logToFile.d("getSpeechEvalAnswerTeamRank:num=" + num + ",stop=" + stop + ",entity=" + entity.getStudents().size());
                            if (stop) {
                                onStopSpeech(speechAssessmentPager, num, null);
                            }
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            goldTeamStatusHashMap.put(num, null);
                            if (stop) {
                                onStopSpeech(speechAssessmentPager, num, null);
                            }
                        }
                    });
                    /** 语音评测 roleplay */
                } else if (speechAssessmentPager instanceof SpeechAssessmentWebX5Pager || speechAssessmentPager instanceof RolePlayStandMachinePager) {
                    questionIRCBll.getRolePlayAnswerTeamRank(num, new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            entity = (GoldTeamStatus) objData[0];
                            entity.setId(num);
                            goldTeamStatusHashMap.put(num, entity);
                            logToFile.d("getRolePlayAnswerTeamRank:num=" + num + ",stop=" + stop + ",entity=" + entity.getStudents().size());
                            if (stop) {
                                onStopSpeech(speechAssessmentPager, num, null);
                            }
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            goldTeamStatusHashMap.put(num, null);
                            if (stop) {
                                onStopSpeech(speechAssessmentPager, num, null);
                            }
                        }
                    });
                }
            }
        }, 3000);
    }

    @Override
    public void onStopSpeech(BaseSpeechAssessmentPager speechAssessmentPager, String num, OnTop3End top3End) {
        if (top3End != null) {
            top3EndHashMap.put(num, top3End);
        } else {
            top3End = top3EndHashMap.get(num);
            logToFile.d("onStopSpeech:num=" + num + ",top3End=" + (top3End == null));
        }
        stop = true;
        if (entity != null) {
            logToFile.d("onStopSpeech:entity=" + entity.getId() + ",num=" + num + ",size=" + goldTeamStatusHashMap.size());
        } else {
            logToFile.d("onStopSpeech:entity=null" + ",num=" + num + ",size=" + goldTeamStatusHashMap.size());
        }
        if (goldTeamStatusHashMap.containsKey(num)) {//说明请求了
            GoldTeamStatus entity = goldTeamStatusHashMap.get(num);
            if (entity == null) {//请求失败
                logToFile.d("onStopSpeech:entity=null" + ",num=" + num);
                if (top3End != null) {
                    top3End.onShowEnd();
                    top3EndHashMap.remove(num);
                }
                return;
            }
            logToFile.d("onStopSpeech:entity=" + entity.getId() + ",num=" + num);
            initTop(num, entity, top3End);
            if (speechAssessmentPager instanceof StandSpeechAssAutoPager) {
                SpeechStandLog.sno7(liveAndBackDebug, num);
            } else if (speechAssessmentPager instanceof SpeechAssessmentWebX5Pager) {
                RolePlayStandLog.sno6(liveAndBackDebug, num);
            }
        }
    }

    private void initTop(final String num, GoldTeamStatus entity, final OnTop3End top3End) {
        if (standSpeechTop3Pager != null && num.equals(standSpeechTop3Pager.getId())) {
            logger.d("initTop:num=" + num);
            return;
        }
        standSpeechTop3Pager = new StandSpeechTop3Pager(bottomContent.getContext(), entity);
        standSpeechTop3Pager.setId(num);
        standSpeechTop3Pager.initData();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(standSpeechTop3Pager.getRootView(), lp);
        StandSpeechTop3Bll.this.entity = null;
        goldTeamStatusHashMap.remove(num);
        stop = false;
        standSpeechTop3Pager.getRootView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                logToFile.d("initTop:Detached:num=" + num + ",top3End=" + (top3End == null));
                if (top3End != null) {
                    top3End.onShowEnd();
                    top3EndHashMap.remove(num);
                }
            }
        });
    }

}
