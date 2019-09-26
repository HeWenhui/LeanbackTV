package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBackBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness.ExperienceQuitFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination.StandExperienceEvaluationBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.learnfeedback.StandExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.livemessage.StandExperienceMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.recommodcourse.StandExperienceRecommondBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.understand.StandExperienceUnderstandBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.ExpRollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.weight.ExperMediaCtrl;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExperStandRecordFragmentBase extends ExperienceRecordFragmentBase {
    private String TAG = "ExperStandRecordFragmentBase";

    {
        mLayoutVideo = R.layout.frag_exper_stand_live_back_video;
    }

    LiveStandFrameAnim liveStandFrameAnim;
    boolean isInit = false;
    private ExperienceQuitFeedbackBll experienceQuitFeedbackBll;

    @Override
    protected void initlizeBlls() {
        if (isInit) {
            initlizeBlls2();
            return;
        }
        isInit = true;
        liveStandFrameAnim = new LiveStandFrameAnim(activity);
        liveStandFrameAnim.check(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                View vsLiveStandUpdate = mContentView.findViewById(R.id.vs_live_stand_update);
                if (vsLiveStandUpdate != null) {
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                } else {
                    vsLiveStandUpdate = mContentView.findViewById(R.id.rl_live_stand_update);
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                }
                Map<String, String> mParams = new HashMap<>();
                mParams.put("logtype", "check_onDataSucess");
                mParams.put("isFinishing", "" + activity.isFinishing());
//                Loger.d(activity, TAG, mParams, true);
                UmsAgentManager.umsAgentDebug(activity, TAG, mParams);
                if (activity.isFinishing()) {
                    return;
                }
                initlizeBlls2();
                onModeChanged();
            }
        });
    }

    @Override
    protected void onModeChanged() {
        if (liveBackBll == null) {
            return;
        }
        super.onModeChanged();
    }

    protected void initlizeBlls2() {

        liveBackBll = new StandExperienceLiveBackBll(activity, playBackEntity);
        liveBackBll.setStuCourId(playBackEntity.getStuCourseId());
        liveBackBll.setvPlayer(vPlayer);

        expBusiness = new ExperienceBusiness(activity);
        initlizeTalk();

        addBusiness(activity);
        liveBackBll.onCreate();

        initBLlView();
    }

    @Override
    protected void addBusiness(Activity activity) {
        ArrayList<BllConfigEntity> bllConfigEntities = AllBackBllConfig.getStandLiveVideoExperienceBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }
        //站立直播体验课聊天区的添加
        liveBackBll.addBusinessBll(new StandExperienceMessageBll(activity, liveBackBll));
        //懂了吗
        liveBackBll.addBusinessBll(new StandExperienceUnderstandBll(activity, liveBackBll));
        //推荐课程信息
        liveBackBll.addBusinessBll(new StandExperienceRecommondBll(activity, liveBackBll, getVideoView()));
        //播放完成后的定级卷
        liveBackBll.addBusinessBll(new StandExperienceEvaluationBll(activity, liveBackBll));
        //定级完成后的结果页
//        liveBackBll.addBusinessBll(new ExperienceBuyCourseExperiencePresenter(activity, liveBackBll));
        //播放完成后的反馈弹窗
        liveBackBll.addBusinessBll(new StandExperienceLearnFeedbackBll(activity, liveBackBll));
        experienceQuitFeedbackBll = new ExperienceQuitFeedbackBll(activity, liveBackBll, true);
        liveBackBll.addBusinessBll(experienceQuitFeedbackBll);

        liveBackBll.addBusinessBll(new RedPackageExperienceBll(activity, liveBackBll, playBackEntity.getChapterId()));
        expRollCallBll = new ExpRollCallBll(activity, liveBackBll, expLiveInfo, expAutoLive.getTermId());
        liveBackBll.addBusinessBll(expRollCallBll);
    }

    @Override
    protected void createMediaControllerBottom() {
        ExperMediaCtrl experMediaCtrl = (ExperMediaCtrl) mMediaController;
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, experMediaCtrl, liveBackPlayVideoFragment);
    }

    @Override
    protected void resultComplete() {
        super.resultComplete();
        if (experienceQuitFeedbackBll != null) {
            experienceQuitFeedbackBll.playComplete();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveStandFrameAnim != null) {
            liveStandFrameAnim.onDestroy();
            liveStandFrameAnim = null;
        }
    }
}
