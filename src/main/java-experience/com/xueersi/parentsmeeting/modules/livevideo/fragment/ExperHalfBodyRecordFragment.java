package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.module.browser.event.BrowserEvent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.HalfBodyLiveExperienceActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperHalfBodyLiveAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperStandLiveAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBackBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllExperienceConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness.ExperienceQuitFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination.StandExperienceEvaluationBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.learnfeedback.StandExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.livemessage.StandExperienceMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.recommodcourse.StandExperienceRecommondBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.understand.StandExperienceUnderstandBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.learnfeedback.business.HalfBodyExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.ExpRollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.weight.ExperMediaCtrl;
import com.xueersi.parentsmeeting.modules.livevideo.weight.LiveHalfBodyExpMediaCtrlBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.RoundProgressBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ExperHalfBodyRecordFragment extends ExperienceRecordFragmentBase {
    private String TAG = "ExperHalfBodyRecordFragment";
    /**
     * 我的课程业务层
     */
    LectureLivePlayBackBll lectureLivePlayBackBll;

    {
        mLayoutVideo = R.layout.frag_exper_half_live_back_video;
    }

    @Override
    protected void createLiveVideoAction() {
        experLiveAction = new ExperHalfBodyLiveAction(activity, mContentView, expLiveInfo);
    }

    @Override
    protected void initlizeData() {
        super.initlizeData();
        lectureLivePlayBackBll = new LectureLivePlayBackBll(activity, "");
    }

    @Override
    protected void onPlayOpenSuccess() {
        super.onPlayOpenSuccess();
        long mTotaltime = liveBackPlayVideoFragment.getDuration();
        if (mTotaltime < Long.parseLong(playBackEntity.getVisitTimeKey()) * 1000) {
            if (experienceQuitFeedbackBll != null) {
                experienceQuitFeedbackBll.playComplete();
            }
            // 测试体验课播放器的结果页面
            lectureLivePlayBackBll.getExperienceResult(playBackEntity.getChapterId(), playBackEntity.getLiveId(),
                    getDataCallBack);
        }
    }

    @Override
    protected void resultComplete() {
        super.resultComplete();
        if (experienceQuitFeedbackBll != null) {
            experienceQuitFeedbackBll.playComplete();
        }
        lectureLivePlayBackBll.getExperienceResult(playBackEntity.getChapterId(), playBackEntity.getLiveId(),
                getDataCallBack);
        EventBus.getDefault().post(new BrowserEvent.ExperienceLiveEndEvent(1));
    }

    @Override
    protected void addBusiness(Activity activity) {
        ArrayList<BllConfigEntity> bllConfigEntities = AllExperienceConfig.getHalfExperienceBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }
        learnFeedbackBll = new HalfBodyExperienceLearnFeedbackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(learnFeedbackBll);
        experienceQuitFeedbackBll = new ExperienceQuitFeedbackBll(activity, liveBackBll, false);
        experienceQuitFeedbackBll.setLiveVideo(liveBackPlayVideoFragment);
        liveBackBll.addBusinessBll(experienceQuitFeedbackBll);
        liveBackBll.onCreate();
    }

    @Override
    protected void createMediaControllerBottom() {
        ExperMediaCtrl experMediaCtrl = (ExperMediaCtrl) mMediaController;
        liveMediaControllerBottom = new LiveHalfBodyExpMediaCtrlBottom(activity, experMediaCtrl, liveBackPlayVideoFragment);
        LiveHalfBodyExpMediaCtrlBottom liveHalfBodyExpMediaCtrlBottom = (LiveHalfBodyExpMediaCtrlBottom) liveMediaControllerBottom;
        liveHalfBodyExpMediaCtrlBottom.onModeChange(expLiveInfo.getMode());
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, liveMediaControllerBottom,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onModeChanged() {
        LiveHalfBodyExpMediaCtrlBottom liveHalfBodyExpMediaCtrlBottom = (LiveHalfBodyExpMediaCtrlBottom) liveMediaControllerBottom;
        liveHalfBodyExpMediaCtrlBottom.onModeChange(expLiveInfo.getMode());
        super.onModeChanged();
    }

    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
        private boolean isFirstGetResult = true;

        @Override
        public void onDataSucess(Object... objData) {
            // 获取到数据之后的逻辑处理
            if (objData.length > 0) {
                ExperienceResult mData = (ExperienceResult) objData[0];
                // 测试体验课播放器的结果页面
                if (mData != null && isFirstGetResult) {
                    showPopupwinResult(mData);
                    isFirstGetResult = false;
                    setBackgroundAlpha(0.4f);
                }
            }
        }
    };
    private PopupWindow mWindow;

    private void showPopupwinResult(final ExperienceResult mData) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View result = inflater.inflate(R.layout.pop_halfbody_experience_learnback, null);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow = new PopupWindow(result, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams
                .MATCH_PARENT, false);
        mWindow.setOutsideTouchable(false);
        mWindow.showAtLocation(result, Gravity.CENTER, 0, 0);
        RoundProgressBar mProgressbar = (RoundProgressBar) result.findViewById(R.id.roundProgressBar);
        TextView recommand = (TextView) result.findViewById(R.id.tv_detail_result);
        TextView beat = (TextView) result.findViewById(R.id.tv_result);
        TextView totalscore = (TextView) result.findViewById(R.id.tv_total_score);

        beat.setText("恭喜，你打败了" + mData.getBeat() + "%的学生");
        if (TextUtils.isEmpty(mData.getRecommend())) {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("赶快去报班继续提高成绩吧");
        } else {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("推荐您报名" + mData.getRecommend());
        }
        totalscore.setText(mData.getCorrect() + "%");
        mProgressbar.setMax(100);
        if (mData.getCorrect() > 0) {
            mProgressbar.setProgress(mData.getCorrect());
        } else {
            mProgressbar.setProgress(0);
        }
        ImageButton shut = (ImageButton) result.findViewById(R.id.ib_shut);
        shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                showPopupwinFeedback();
                mWindow = null;
            }
        });
        Button chat = (Button) result.findViewById(R.id.bt_chat);
        if (TextUtils.isEmpty(mData.getWechatNum())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mData.getWechatNum());
                XESToastUtils.showToast("您已复制老师微信号，快去添加吧!");
            }
        });
        Button apply = (Button) result.findViewById(R.id.bt_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.getUrl() != null) {
                    BrowserActivity.openBrowser(activity, mData.getUrl());
                } else {
                    XESToastUtils.showToast(" 数据异常");
                }
            }
        });
    }

    private void showPopupwinFeedback() {
        setBackgroundAlpha(1.0f);
        learnFeedbackBll.showWindow();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

}
