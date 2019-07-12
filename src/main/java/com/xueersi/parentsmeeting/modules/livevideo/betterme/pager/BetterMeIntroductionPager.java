package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImpl;

/**
 * 英语小目标 小目标介绍
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeIntroductionPager extends BasePager {
    /**
     * 查看段位
     */
    private TextView tvViewLevel;
    /**
     * 知道啦
     */
    private ImageView btnGotit;
    private LottieAnimationView mLottieAnimationView;
    private LinearLayout llContent;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/introduction/";
    private OnBettePagerClose onBettePagerClose;

    public BetterMeIntroductionPager(Context context, OnBettePagerClose onBettePagerClose) {
        super(context);
        this.onBettePagerClose = onBettePagerClose;
        initData();
        initListener();
    }

    public BetterMeIntroductionPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_introduction, null);
        tvViewLevel = view.findViewById(R.id.tv_livevideo_betterme_introduction_viewlevel);
        btnGotit = view.findViewById(R.id.btn_livevideo_betterme_introduction_gotit);
        llContent = view.findViewById(R.id.ll_livevideo_betterme_introduction_content);
        mLottieAnimationView = view.findViewById(R.id.animation_view);
        return view;
    }

    @Override
    public void initData() {
  /*      final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        final String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        mLottieAnimationView.setAnimation(lottieJsonPath);
        mLottieAnimationView.setImageAssetsFolder(lottieResPath);
        mLottieAnimationView.useHardwareAcceleration(true); //使用硬件加速
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mLottieAnimationView.playAnimation();*/
        llContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        tvViewLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBettePagerClose.onShow(BetterMeViewImpl.PAGER_LEVEL_DISPLAY);
            }
        });
        btnGotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBettePagerClose.onClose(BetterMeIntroductionPager.this);
                onBettePagerClose.onShow(BetterMeViewImpl.PAGER_RECEIVE_TARGET, 2000);
            }
        });
    }
}
