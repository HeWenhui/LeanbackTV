package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImpl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * 英语小目标 小目标介绍
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeIntroductionPager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/introduction/";
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
        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        final String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo lottieEffectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                if ("img_0.png".equals(lottieImageAsset.getFileName())
                        || "img_1.png".equals(lottieImageAsset.getFileName())
                        || "img_2.png".equals(lottieImageAsset.getFileName())
                        || "img_3.png".equals(lottieImageAsset.getFileName())
                        || "img_4.png".equals(lottieImageAsset.getFileName())
                        || "img_20.png".equals(lottieImageAsset.getFileName())) {
                    return null;
                }
                return lottieEffectInfo.fetchBitmapFromAssets(
                        mLottieAnimationView,
                        lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(),
                        lottieImageAsset.getHeight(),
                        mContext);
            }
        };
        mLottieAnimationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext), "introduction");
        mLottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
        mLottieAnimationView.useHardwareAcceleration(true); //使用硬件加速
        mLottieAnimationView.playAnimation();
        mLottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (animatedFraction > 0.1) {
                    llContent.setVisibility(View.VISIBLE);
                    mLottieAnimationView.removeUpdateListener(this);
                }
            }
        });
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
