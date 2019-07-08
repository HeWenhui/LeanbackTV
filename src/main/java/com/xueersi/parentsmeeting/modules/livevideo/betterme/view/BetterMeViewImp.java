package com.xueersi.parentsmeeting.modules.livevideo.betterme.view;

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
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie.BubbleLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeCompleteTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeIntroductionPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeLevelDisplayPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeReceiveTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * 英语小目标 view层
 *
 * @author zhangyuansun
 * created  at 2018/12/4
 */
public class BetterMeViewImp implements BetterMeContract.BetterMeView, OnBettePagerClose {
    BetterMeContract.BetterMePresenter mBetterMePresenter;
    RelativeLayout mRootView;
    RelativeLayout rlBetterMeContent;
    Context mContext;
    WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    public static final int PAGER_INTRODUCTION = 1;
    public static final int PAGER_LEVEL_DISPLAY = 2;
    public static final int PAGER_RECEIVE_TARGET = 3;

    private BasePager currentPager;

    public BetterMeViewImp(Context context) {
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

    private void showUpdateBubble(String current, String target, boolean increasing) {
    }

    /**
     * 本场小目标气泡
     */
    private void showTargetBubble(String text) {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        final LottieEffectInfo bubbleEffectInfo = new BubbleLottieEffectInfo(mContext, text);
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

        LiveVideoPoint point = LiveVideoPoint.getInstance();
        int bottomMargin = point.screenHeight - point.y3;
        int rightMargin = point.screenWidth - point.x4;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin = bottomMargin;
        layoutParams.rightMargin = rightMargin;
        rlBetterMeContent.addView(lottieAnimationView, layoutParams);
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
