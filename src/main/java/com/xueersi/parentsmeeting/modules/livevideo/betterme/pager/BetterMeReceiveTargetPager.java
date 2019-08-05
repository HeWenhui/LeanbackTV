package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImpl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * 英语小目标 本场小目标
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeReceiveTargetPager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/introduction/";
    StuSegmentEntity mStuSegmentEntity;
    BetterMeEntity mBetterMeEntity;
    private OnBettePagerClose onBettePagerClose;
    /**
     * 目标的类型
     */
    private TextView tvReceiveTarType;
    /**
     * 目标的值
     */
    private TextView tvReceiveTarValue;
    /**
     * 段位升级
     */
    private TextView tvReceiveTarLevelUp;
    /**
     * 当前段位
     */
    private TextView tvReceiveTarCurrentLevel;
    /**
     * 下一段位
     */
    private TextView tvReceiveTarNextLevel;
    /**
     * 升级提示
     */
    private TextView tvReceiveTarUpdateTips;
    /**
     * 按钮 - 准备好啦
     */
    private ImageView ivReceivetarReady;
    /**
     * 10s倒计时
     */
    private TextView tvCompletetarCountdown;
    /**
     * 当前段位图片
     */
    private ImageView ivReceivetarCurrentLevel;
    /**
     * 下一段位图片
     */
    private ImageView ivReceivetarNextLevel;
    /**
     * 箭头
     */
    private ImageView ivReceivetarArrow;
    /**
     * 下一段位布局
     */
    private LinearLayout llReveivetarNextLevel;
    private LottieAnimationView mLottieAnimationView;
    private LinearLayout llContent;

    public BetterMeReceiveTargetPager(StuSegmentEntity stuSegmentEntity, BetterMeEntity betterMeEntity, Context context, OnBettePagerClose onBettePagerClose) {
        super(context);
        this.mStuSegmentEntity = stuSegmentEntity;
        this.mBetterMeEntity = betterMeEntity;
        this.onBettePagerClose = onBettePagerClose;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_receive_target, null);
        ivReceivetarReady = view.findViewById(R.id.iv_livevideo_betterme_receivetarget_ready);
        tvCompletetarCountdown = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_countdown);
        tvReceiveTarType = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_type);
        tvReceiveTarValue = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_value);
        tvReceiveTarLevelUp = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_level_up);
        tvReceiveTarCurrentLevel = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_current_level);
        tvReceiveTarNextLevel = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_next_level);
        tvReceiveTarUpdateTips = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_update_tips);
        ivReceivetarCurrentLevel = view.findViewById(R.id.iv_livevideo_betterme_receivetarget_current_level);
        ivReceivetarNextLevel = view.findViewById(R.id.iv_livevideo_betterme_receivetarget_next_level);
        ivReceivetarArrow = view.findViewById(R.id.iv_livevideo_betterme_receivetarget_arrow);
        llReveivetarNextLevel = view.findViewById(R.id.ll_livevideo_betterme_receivetarget_next_level);
        mLottieAnimationView = view.findViewById(R.id.animation_view);
        llContent = view.findViewById(R.id.ll_livevideo_betterme_receivetarget_content);
        return view;
    }

    @Override
    public void initData() {
        logger.i("initData()");
        String target = mBetterMeEntity.getAimValue();
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(mBetterMeEntity.getAimType())) {
            tvReceiveTarType.setText(BetterMeConfig.CORRECTRATE);
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(mBetterMeEntity.getAimType())) {
            tvReceiveTarType.setText(BetterMeConfig.PARTICIPATERATE);
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(mBetterMeEntity.getAimType())) {
            tvReceiveTarType.setText(BetterMeConfig.TALKTIME);
            target = BetterMeUtil.secondToMinite(target);
        }
        tvReceiveTarValue.setText(target);

        tvReceiveTarCurrentLevel.setText(mStuSegmentEntity.getSegment() + mStuSegmentEntity.getStar() + "级");
        tvReceiveTarUpdateTips.setText("还需完成" + mStuSegmentEntity.getAimNumber() + "场目标可升级");

        //设置当前段位的背景
        int currentLevelIndex = mStuSegmentEntity.getSegmentType() - 1;
        ivReceivetarCurrentLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_NOSTAR[currentLevelIndex]);
        //当前星星的数量
        int currentStarsNumber = mStuSegmentEntity.getStar();
        //升段位需要的星星的数量
        int needsStarsNumber = BetterMeConfig.LEVEL_UPLEVEL_STARS[currentLevelIndex];
        switch (needsStarsNumber) {
            //下设3个小段位（星星）
            case 3:
                ivReceivetarCurrentLevel.setImageResource(BetterMeConfig.QINFENBAIYIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设4个小段位（星星）
            case 4:
                ivReceivetarCurrentLevel.setImageResource(BetterMeConfig.KEKUHUANGJIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设5个小段位（星星）
            case 5:
                ivReceivetarCurrentLevel.setImageResource(BetterMeConfig.HENGXINBOJIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设6个小段位（星星）
            case 6:
                ivReceivetarCurrentLevel.setImageResource(BetterMeConfig.ZUIQIANGXUEBA_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            default:
                break;
        }

        //升星后的段位索引
        int nextLevelIndex = currentLevelIndex;
        //升星后的星星数量
        int nextStarsNumber = currentStarsNumber + 1;
        //是否升段位
        boolean isUpdateLevel = nextStarsNumber > needsStarsNumber;
        if (isUpdateLevel) {
            nextLevelIndex = currentLevelIndex + 1;
            nextStarsNumber = 1;
        }
        if (nextLevelIndex < BetterMeConfig.LEVEL_IMAGE_RES_DISS.length) {
            tvReceiveTarNextLevel.setText(BetterMeConfig.LEVEL_NAMES[nextLevelIndex] + nextStarsNumber + "级");
            ivReceivetarNextLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_DISS[nextLevelIndex]);
            switch (BetterMeConfig.LEVEL_UPLEVEL_STARS[nextLevelIndex]) {
                //下设3个小段位（星星）
                case 3:
                    ivReceivetarNextLevel.setImageResource(BetterMeConfig.QINFENBAIYIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设4个小段位（星星）
                case 4:
                    ivReceivetarNextLevel.setImageResource(BetterMeConfig.KEKUHUANGJIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设5个小段位（星星）
                case 5:
                    ivReceivetarNextLevel.setImageResource(BetterMeConfig.HENGXINBOJIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设6个小段位（星星）
                case 6:
                    ivReceivetarNextLevel.setImageResource(BetterMeConfig.ZUIQIANGXUEBA_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                default:
                    break;
            }
        } else {
            ivReceivetarArrow.setVisibility(View.GONE);
            llReveivetarNextLevel.setVisibility(View.GONE);
        }

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
                        || "img_20.png".equals(lottieImageAsset.getFileName())
                        ) {
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
        mLottieAnimationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext), "receiveTarget");
        mLottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
        mLottieAnimationView.useHardwareAcceleration(true); //使用硬件加速
        mLottieAnimationView.playAnimation();
        mLottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (animatedFraction > 0.1) {
                    if (mCountDownTimer != null) {
                        mCountDownTimer.start();
                    }
                    llContent.setVisibility(View.VISIBLE);
                    mLottieAnimationView.removeUpdateListener(this);
                }
            }
        });
    }

    @Override
    public void initListener() {
        tvReceiveTarLevelUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBettePagerClose.onShow(BetterMeViewImpl.PAGER_LEVEL_DISPLAY);
            }
        });
        ivReceivetarReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                onBettePagerClose.onClose(BetterMeReceiveTargetPager.this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    /**
     * 10s后关闭倒计时
     */
    private CountDownTimer mCountDownTimer = new CountDownTimer(10100, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = millisUntilFinished / 1000;
            tvCompletetarCountdown.setText(seconds + "s ");
        }

        @Override
        public void onFinish() {
            onBettePagerClose.onClose(BetterMeReceiveTargetPager.this);
        }
    };
}
