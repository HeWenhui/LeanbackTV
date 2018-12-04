package com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.ExperienceLiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.experience.pager.ExperienceQuitFeedbackPager;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.IExperiencePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination.IStandExperienceEvaluationContract;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination.StandExperienceEvaluationPager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by：WangDe on 2018/12/4 20:54
 */
public class ExperienceQuitFeedbackBll extends LiveBackBaseBll implements ExperienceQuitFeedbackPager
        .IButtonClickListener,IExperiencePresenter {
    LivePlayBackHttpManager livePlayBackHttpManager;
    ExperienceQuitFeedbackPager expPager;
    private RelativeLayout rlLiveMessageContent;
    private boolean firstTime;
    private long startTime;
    private long SHOW_QUIT_DIALOG_THRESHOLD = 1500000;
    private boolean isShowQuitDialog = false;
    private IStandExperienceEvaluationContract.IEvaluationView mEvaluationView;
    private boolean isStand;
    private ExperienceLiveVideoActivity liveVideoActivityBase;
    LiveAndBackDebug ums;

    public ExperienceQuitFeedbackBll(Activity activity, LiveBackBll liveBackBll,LiveAndBackDebug ums,boolean isStand) {
        super(activity, liveBackBll);
        this.ums = ums;
        this.isStand = isStand;
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        if (firstTime) {
            startTime = System.currentTimeMillis();
            firstTime = false;
        }
        Long keyTime = Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000 + (System.currentTimeMillis() -
                startTime);
        if (keyTime < SHOW_QUIT_DIALOG_THRESHOLD) {
            isShowQuitDialog = true;
        }
        if (isShowQuitDialog) {
            livePlayBackHttpManager = new LivePlayBackHttpManager(mContext);
            expPager = new ExperienceQuitFeedbackPager(mContext);
            expPager.setButtonListener(this);
            expPager.setIExperiencePresenter(this);
        }
    }

    public void setExperienceType(boolean isStand){
        this.isStand = isStand;
    }

    public void setLiveVideo(ExperienceLiveVideoActivity liveVideoActivityBase){
        this.liveVideoActivityBase = liveVideoActivityBase;
    }


    @Override
    public boolean showPager() {
        StableLogHashMap logHashMap = new StableLogHashMap("onClassFeedbackOpen");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        if (ums != null){
            ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
        }else {
            UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                    logHashMap.getData());
        }

        if (!mVideoEntity.isPrek() && !TextUtils.isEmpty(mVideoEntity.getExamUrl())) {
            expPager.showGradingPaper();
            mEvaluationView = new StandExperienceEvaluationPager(activity, this);
        }
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (rlLiveMessageContent == null) {
            rlLiveMessageContent = new RelativeLayout(activity);
            mRootView.addView(rlLiveMessageContent, params);
        } else {
            rlLiveMessageContent.removeAllViews();
        }
        final View view = expPager.getRootView();
        rlLiveMessageContent.addView(view, params);
        return true;
    }

    @Override
    public boolean removePager() {
        if (rlLiveMessageContent != null) {
            rlLiveMessageContent.removeAllViews();
        }
        return false;
    }

    @Override
    public void leaveClass(Map<String,Boolean> data) {
        StableLogHashMap logHashMap = new StableLogHashMap("onClassFeedbackClose");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        logHashMap.put("closetype", "1");
        if (ums != null){
            ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
        }else {
            UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                    logHashMap.getData());
        }
        String content = "";
        for (String key : data.keySet()) {
            if (data.get(key)) {
                content = content + key + ",";
            }
        }
        content = content.substring(0, content.length() - 1);
        livePlayBackHttpManager.sendExperienceQuitFeedback(UserBll.getInstance().getMyUserInfoEntity()
                .getStuId(), mVideoEntity.getChapterId(), content, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("success" + responseEntity.toString());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.i("failure" + msg);
            }
        });
        if(isStand){
            ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
            activityChangeLand.changeLOrP();
        }else {
            if (liveVideoActivityBase != null){
                liveVideoActivityBase.changeLOrP();
            }
        }
        activity.finish();
    }

    @Override
    public void backClass() {
        StableLogHashMap logHashMap = new StableLogHashMap("onClassFeedbackClose");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        logHashMap.put("closetype", "2");
        if (ums != null){
            ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
        }else {
            UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                    logHashMap.getData());
        }
        if (rlLiveMessageContent != null) {
            rlLiveMessageContent.removeAllViews();
        }
    }

    @Override
    public void showWindow() {
        StableLogHashMap logHashMap = new StableLogHashMap("openLevelTestOnLive");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        if (ums != null){
            ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
        }else {
            UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                    logHashMap.getData());
        }
        if (mEvaluationView != null) {
            if(isStand){
                ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
                activityChangeLand.changeLOrP();
            }else {
                if (liveVideoActivityBase != null){
                    liveVideoActivityBase.changeLOrP();
                }
            }
            logger.i("旋转屏幕");
            mEvaluationView.showWebView(mVideoEntity.getExamUrl());
            mRootView.addView(mEvaluationView.getRootView(), RelativeLayout.LayoutParams
                    .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void removeWindow() {
        if (mEvaluationView.getRootView() != null && mEvaluationView.getRootView().getParent() == mRootView) {
            mRootView.removeView(mEvaluationView.getRootView());
        }
    }

    @Override
    public void showNextWindow() {

    }
}
