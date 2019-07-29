package com.xueersi.parentsmeeting.modules.livevideo.betterme.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.TeamPKBetterMeRewardsEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeCompleteTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeIntroductionPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeLevelDisplayPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeReceiveTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.TeamPkBetterMeRewardsPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * 英语小目标 view层
 *
 * @author zhangyuansun
 * created  at 2018/12/4
 */
public class BetterMeViewImpl implements BetterMeContract.BetterMeView, OnBettePagerClose {
    private BetterMeContract.BetterMePresenter mBetterMePresenter;
    private RelativeLayout mRootView;
    private RelativeLayout rlBetterMeContent;
    private Context mContext;
    private WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    public static final int PAGER_INTRODUCTION = 1;
    public static final int PAGER_LEVEL_DISPLAY = 2;
    public static final int PAGER_RECEIVE_TARGET = 3;
    private BasePager currentPager;
    private boolean showPK;

    public BetterMeViewImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void setRootView(RelativeLayout rootView) {
        this.mRootView = rootView;
    }

    /**
     * 小目标介绍弹窗
     */
    @Override
    public void showIntroductionPager(boolean showPK) {
        this.showPK = showPK;
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            rlBetterMeContent.setId(R.id.rl_livevideo_content_speechbul);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        currentPager = new BetterMeIntroductionPager(mContext, this);
        rlBetterMeContent.addView(currentPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 段位展示弹窗
     */
    @Override
    public void showLevelDisplayPager() {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        currentPager.getRootView().setVisibility(View.GONE);
        rlBetterMeContent.addView(new BetterMeLevelDisplayPager(mContext, this).getRootView(), new ViewGroup
                .LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 收到本场小目标弹窗
     */
    @Override
    public void showReceiveTargetPager(boolean showPK) {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        currentPager = new BetterMeReceiveTargetPager(mBetterMePresenter.getStuSegmentEntity(), mBetterMePresenter
                .getBetterMeEntity(), mContext, this);
        ((BetterMeReceiveTargetPager) currentPager).setShowPK(showPK);
        rlBetterMeContent.addView(currentPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 完成本场小目标弹窗
     */
    @Override
    public void showCompleteTargetPager(StuAimResultEntity stuAimResultEntity) {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        currentPager = new BetterMeCompleteTargetPager(stuAimResultEntity, mContext, this);
        rlBetterMeContent.addView(currentPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 小目标奖励页面
     */
    public void showTeamPkBetterMeRewardsPager(int pattern) {
        currentPager = new TeamPkBetterMeRewardsPager(mContext, pattern, new TeamPKBetterMeRewardsEntity(), new LiveBasePager
                .OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                rlBetterMeContent.removeView(currentPager.getRootView());
            }
        });
        rlBetterMeContent.addView(currentPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    @Override
    public void setPresenter(BetterMeContract.BetterMePresenter presenter) {
        this.mBetterMePresenter = presenter;
    }

    /**
     * 关闭弹窗
     *
     * @param basePager
     */
    @Override
    public void onClose(BasePager basePager) {
        if (basePager instanceof BetterMeLevelDisplayPager) {
            currentPager.getRootView().setVisibility(View.VISIBLE);
            if (rlBetterMeContent != null) {
                rlBetterMeContent.removeView(basePager.getRootView());
            }
        } else {
            rlBetterMeContent.removeAllViews();
        }
    }

    /**
     * 显示下一弹窗
     *
     * @param pagerType
     * @param duration
     */
    @Override
    public void onShow(final int pagerType, int duration) {
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onShow(pagerType);
            }
        }, duration);
    }

    @Override
    public void onShow(int pagerType) {
        switch (pagerType) {
            case PAGER_LEVEL_DISPLAY:
                showLevelDisplayPager();
                break;
            case PAGER_RECEIVE_TARGET:
                showReceiveTargetPager(this.showPK);
                break;
            default:
                break;
        }
    }
}
