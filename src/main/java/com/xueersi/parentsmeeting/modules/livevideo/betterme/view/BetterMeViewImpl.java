package com.xueersi.parentsmeeting.modules.livevideo.betterme.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie.BubbleLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeCompleteTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeIntroductionPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeLevelDisplayPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeReceiveTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager.TeamPkBetterMeRewardsPager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
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
    public void showIntroductionPager() {
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
    public void showReceiveTargetPager() {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        currentPager = new BetterMeReceiveTargetPager(mBetterMePresenter.getStuSegmentEntity(), mBetterMePresenter
                .getBetterMeEntity(), mContext, this);
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

    @Override
    public void onBetterMeUpdate(AimRealTimeValEntity aimRealTimeValEntity) {

    }

    /**
     * 小目标奖励页面
     */
    public void showTeamPkBetterMeRewardsPager() {
        currentPager = new TeamPkBetterMeRewardsPager(mContext, new EnTeamPkRankEntity(), 1, new LiveBasePager
                .OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                rlBetterMeContent.removeView(currentPager.getRootView());
            }
        });
        rlBetterMeContent.addView(currentPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 更新小目标完成情况气泡
     */
    private void showUpdateBubble(String current, String target, boolean increasing) {
    }

    /**
     * 本场小目标气泡
     */
    @Override
    public void showTargetBubble() {
        BetterMeEntity mBetterMeEntity = mBetterMePresenter.getBetterMeEntity();
        StringBuilder message = new StringBuilder("本场目标：");
        String target = mBetterMeEntity.getAimValue();
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(mBetterMeEntity.getAimType())) {
            message.append(BetterMeConfig.CORRECTRATE);
            target = (int) (Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(mBetterMeEntity.getAimType())) {
            message.append(BetterMeConfig.PARTICIPATERATE);
            target = (int) (Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(mBetterMeEntity.getAimType())) {
            message.append(BetterMeConfig.TALKTIME);
        }
        message.append("达到").append(target);

        ViewGroup rlLivevideoinfo = ((Activity) mContext).findViewById(R.id.rl_livevideo_info);
        if (rlLivevideoinfo != null) {
            ViewGroup viewGroup = (ViewGroup) rlLivevideoinfo.getParent();
            final LottieEffectInfo bubbleEffectInfo = new BubbleLottieEffectInfo(mContext, message.toString());
            final LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
            ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return bubbleEffectInfo.fetchBitmapFromAssets(
                            lottieAnimationView,
                            lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(),
                            lottieImageAsset.getHeight(),
                            mContext);
                }
            };
            lottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "bubble");
            lottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
            lottieAnimationView.useHardwareAcceleration(true);
            lottieAnimationView.playAnimation();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.rl_livevideo_info);
            lp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_livevideo_info);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.bottomMargin = ScreenUtils.getScreenHeight() - rlLivevideoinfo.getTop();
            viewGroup.addView(lottieAnimationView, lp);
        }
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
        currentPager.getRootView().setVisibility(View.VISIBLE);
        if (rlBetterMeContent != null) {
            rlBetterMeContent.removeView(basePager.getRootView());
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
            case PAGER_INTRODUCTION:
                showIntroductionPager();
                break;
            case PAGER_LEVEL_DISPLAY:
                showLevelDisplayPager();
                break;
            case PAGER_RECEIVE_TARGET:
                showReceiveTargetPager();
                break;
            default:
                break;
        }
    }
}
