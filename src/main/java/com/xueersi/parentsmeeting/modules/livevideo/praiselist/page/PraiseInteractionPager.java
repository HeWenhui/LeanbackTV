package com.xueersi.parentsmeeting.modules.livevideo.praiselist.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll;

/**
 * 初高中点赞互动
 */

public class PraiseInteractionPager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/interaction/";

    //点赞按钮
    private ImageView praiseBtn;

    //呼吸光效动画
    private LottieAnimationView breathLottieAnimationView;


    public PraiseInteractionPager(Context context, PraiseInteractionBll praiseInteractionBll) {
        super(context);
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_praise_interaction, null);
        praiseBtn = view.findViewById(R.id.iv_livevideo_praise_interac_praise_btn);
        praiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleStar();
            }
        });
        breathLottieAnimationView = view.findViewById(R.id.lav_livevideo_praise_interac_breath);

        return view;
    }

    public void bubbleStar() {
        startBreathAnimation();
    }


    /**
     * 点赞按钮进场动画
     */
    public void startPraisBtnEnterAnimation() {
        int imageWidth = SizeUtils.Dp2Px(mContext, 40);
        int marginLeft = SizeUtils.Dp2Px(mContext, 45);

        float translationX = praiseBtn.getTranslationX();
        float distance = getRightMargin() + imageWidth + marginLeft;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(praiseBtn, "translationX", distance, translationX);
        objectAnimator.setDuration(1000);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                startBreathAnimation();
            }
        });
        objectAnimator.start();
    }

    /**
     * 开始点赞按钮呼吸动画
     */
    public void startBreathAnimation() {
        logger.d("startBreathAnimation");
        String advanceResPath = LOTTIE_RES_ASSETS_ROOTDIR + "breath/images";
        String advanceJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "breath/data.json";
        final LottieEffectInfo advanceEffectInfo = new LottieEffectInfo(advanceResPath, advanceJsonPath);
        breathLottieAnimationView.setAnimationFromJson(advanceEffectInfo.getJsonStrFromAssets(mContext));
        breathLottieAnimationView.useHardwareAcceleration(true);
        breathLottieAnimationView.setRepeatCount(-1);
        breathLottieAnimationView.setRepeatMode(LottieDrawable.RESTART);
        breathLottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                logger.d("fetchBitmap file=" + lottieImageAsset.getFileName());
                return advanceEffectInfo.fetchBitmapFromAssets(breathLottieAnimationView, lottieImageAsset
                                .getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });

        breathLottieAnimationView.playAnimation();
        logger.d("startBreathAnimation end");
    }


    @Override
    public void initData() {
    }

    private int getRightMargin() {
        return LiveVideoPoint.getInstance().getRightMargin();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
