package com.xueersi.parentsmeeting.modules.livevideoOldIJK.experience.bussiness;

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
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.experience.pager.ExperienceQuitFeedbackPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.IExperiencePresenter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.examination.IStandExperienceEvaluationContract;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.examination.StandExperienceEvaluationPager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by：WangDe on 2018/12/4 20:54
 */
public class ExperienceQuitFeedbackBll extends LiveBackBaseBll implements ExperienceQuitFeedbackPager
        .IButtonClickListener, IExperiencePresenter {
    LivePlayBackHttpManager livePlayBackHttpManager;
    ExperienceQuitFeedbackPager expPager;
    private RelativeLayout rlViewContent;
    private boolean firstTime = true;
    private long startTime;
    private long SHOW_QUIT_DIALOG_THRESHOLD = 1500000;
    private boolean isShowQuitDialog = false;
    private IStandExperienceEvaluationContract.IEvaluationView mEvaluationView;
    private boolean isStand;
    private LiveVideoActivityBase liveVideoActivityBase;
    VideoLivePlayBackEntity mVideoEntity;

    public ExperienceQuitFeedbackBll(Activity activity, LiveBackBll liveBackBll, boolean isStand) {
        super(activity, liveBackBll);
        this.isStand = isStand;
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        this.mVideoEntity = mVideoEntity;
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
            logger.i("create expager");
            livePlayBackHttpManager = new LivePlayBackHttpManager(mContext);
            expPager = new ExperienceQuitFeedbackPager(mContext);
            expPager.setButtonListener(this);
            expPager.setIExperiencePresenter(this);
        }
    }

    public void setExperienceType(boolean isStand) {
        this.isStand = isStand;
    }

    public void setLiveVideo(LiveVideoActivityBase liveVideoActivityBase) {
        this.liveVideoActivityBase = liveVideoActivityBase;
    }

    public void playComplete() {
        logger.i("playComplete");
        isShowQuitDialog = false;
    }

    /**
     * 显示体验课退出反馈弹窗
     *
     * @return
     */
    @Override
    public boolean showPager() {
        //isShowQuitDialog 会在显示定级卷和完成体验课时置为false
        if (isShowQuitDialog && mRootView != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("onClassFeedbackOpen");
            logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
            UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                    logHashMap.getData());
            if (!mVideoEntity.isPrek() && !TextUtils.isEmpty(mVideoEntity.getExamUrl())) {
                expPager.showGradingPaper(true);
            }
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (rlViewContent == null) {
                rlViewContent = new RelativeLayout(activity);
                rlViewContent.setId(R.id.rl_livevideo_experience_feedback_quit);
                mRootView.setVisibility(View.VISIBLE);
                mRootView.addView(rlViewContent, params);
            } else {
                rlViewContent.removeAllViews();
            }
            View view = expPager.getRootView();
            logger.i("showpager");
            rlViewContent.addView(view, params);
            return true;
        } else {
            logger.i("quitlive");
            return false;
        }
    }

    /**
     * 返回体验课直播间
     *
     * @return
     */
    @Override
    public boolean removePager() {
        StableLogHashMap logHashMap = new StableLogHashMap("onClassFeedbackClose");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        logHashMap.put("closetype", "2");
        UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                logHashMap.getData());
        expPager.removeAllCheck();
        if (rlViewContent != null) {
            rlViewContent.removeAllViews();
        }
        logger.i("removepager");
        return false;
    }

    /**
     * 退出体验课直播间
     *
     * @param data
     */
    @Override
    public void leaveClass(Map<String, Boolean> data) {
        StableLogHashMap logHashMap = new StableLogHashMap("onClassFeedbackClose");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        logHashMap.put("closetype", "1");
        UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                logHashMap.getData());

        String content = "";
        for (String key : data.keySet()) {
            if (data.get(key)) {
                content = content + key + ",";
            }
        }
        content = content.substring(0, content.length() - 1);
        livePlayBackHttpManager.sendExperienceQuitFeedback(LiveAppUserInfo.getInstance()
                .getStuId(), mVideoEntity.getChapterId(), content, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("success" + responseEntity.toString());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
//                super.onPmFailure(error, msg);
                logger.i("failure" + msg);
            }
        });
        if (isStand) {
            ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
            activityChangeLand.changeLOrP();
        } else {
            if (liveVideoActivityBase != null) {
                liveVideoActivityBase.changeLOrP();
            }
        }
        activity.finish();
    }

    /**
     * 显示定级卷
     */
    @Override
    public void showWindow() {
        StableLogHashMap logHashMap = new StableLogHashMap("openLevelTestOnLive");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        UmsAgentManager.umsAgentOtherBusiness(activity, "1305801", UmsConstants.uploadBehavior,
                logHashMap.getData());
        if (mEvaluationView == null) {
            mEvaluationView = new StandExperienceEvaluationPager(activity, this);
        }
//            liveBackBll.getvPlayer().stop();
        //跳转到定级卷时暂停播放
        liveBackBll.getvPlayer().pause();
        isShowQuitDialog = false;
        mEvaluationView.showWebView(mVideoEntity.getExamUrl());
        mRootView.addView(mEvaluationView.getRootView(), RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

    }

    /**
     * 关闭定级卷
     */
    @Override
    public void removeWindow() {
        if (mEvaluationView != null && mEvaluationView.getRootView() != null && mEvaluationView.getRootView()
                .getParent() == mRootView) {
            mRootView.removeView(mEvaluationView.getRootView());
            if (liveBackBll != null) {
                //定级卷关闭回到直播间 重新开始播放
                liveBackBll.getvPlayer().start();
                long mTotaltime = liveBackBll.getvPlayer().getDuration();
                long currentPos = Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000 + (System.currentTimeMillis() -
                        startTime);
                //判断体验课是否完成了，来跳转进度
                if (mTotaltime > currentPos) {
                    isShowQuitDialog = true;
                    liveBackBll.getvPlayer().seekTo(Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000 + (System
                            .currentTimeMillis() -
                            startTime));
                } else {
                    liveBackBll.getvPlayer().seekTo(mTotaltime);
                }

            }
            if (expPager != null) {
                expPager.showGradingPaper(false);
            }
            if (mEvaluationView instanceof StandExperienceEvaluationPager) {
                ((StandExperienceEvaluationPager) mEvaluationView).onDestroy();
                mEvaluationView = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (expPager != null) {
            expPager.onDestroy();
        }
        if (mEvaluationView instanceof StandExperienceEvaluationPager) {
            ((StandExperienceEvaluationPager) mEvaluationView).onDestroy();
        }
    }

    @Override
    public void showNextWindow() {

    }
}
