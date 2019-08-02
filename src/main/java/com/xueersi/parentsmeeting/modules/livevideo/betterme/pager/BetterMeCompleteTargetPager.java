package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImpl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * 英语小目标 完成小目标
 *
 * @author zhangyuansun
 * created  at 2018/12/10
 */
public class BetterMeCompleteTargetPager extends LiveBasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/result/";
    private OnBettePagerClose mOnpagerClose;
    /**
     * 图标 - 标题
     */
    private ImageView ivTitle;
    /**
     * 按钮 - 太棒啦
     */
    private ImageView ivGreat;
    /**
     * 10s倒计时
     */
    private TextView tvCountdown;
    /**
     * 进度条 - 小目标
     */
    private ProgressBar pgComeletetar;
    /**
     * 进度提示
     */
    private TextView tvTips;

    private TextView tvAimType;
    /**
     * 目标值
     */
    private TextView tvAimValue;
    /**
     * 图标 - 箭头
     */
    private ImageView ivArrow;
    private TextView ivLevelIndroduction;
    /**
     * 下一段位布局
     */
    private LinearLayout llNextLevel;
    private TextView tvCurrentLevel;
    private ImageView ivCurrentLevel;
    private TextView tvNextLevel;
    private ImageView ivNextLevel;
    /**
     * 升级提示
     */
    private TextView tvLevelUpgraded;
    private LottieAnimationView mLottieAnimationView;
    private LinearLayout llContent;

    private StuAimResultEntity mStuAimResultEntity;
    private static final String CONGRATULATIONS_TO_UPGRADE = "恭喜你升级为";

    public BetterMeCompleteTargetPager(StuAimResultEntity stuAimResultEntity, Context context, OnBettePagerClose onPagerClose) {
        super(context);
        this.mStuAimResultEntity = stuAimResultEntity;
        this.mOnpagerClose = onPagerClose;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_complete_target, null);
        ivTitle = view.findViewById(R.id.iv_livevideo_betterme_completetarget_title);
        ivGreat = view.findViewById(R.id.iv_livevideo_betterme_completetarget_great);
        tvCountdown = view.findViewById(R.id.tv_livevideo_betterme_completetarget_countdown);
        ivArrow = view.findViewById(R.id.iv_livevideo_betterme_completetarget_arrow);
        ivLevelIndroduction = view.findViewById(R.id.tv_livevideo_betterme_completetarget_level_introduction);
        llNextLevel = view.findViewById(R.id.ll_livevideo_betterme_completetarget_next_level);
        tvLevelUpgraded = view.findViewById(R.id.tv_livevideo_betterme_completetarget_level_upgraded);
        pgComeletetar = view.findViewById(R.id.pg_livevideo_better_completetar);
        tvTips = view.findViewById(R.id.tv_livevideo_betterme_aimtips);
        tvAimType = view.findViewById(R.id.tv_livevideo_betterme_completetarget_aimtype);
        tvAimValue = view.findViewById(R.id.tv_livevideo_betterme_completetarget_aimvalue);
        tvCurrentLevel = view.findViewById(R.id.tv_livevideo_betterme_completetarget_current_level);
        ivCurrentLevel = view.findViewById(R.id.iv_livevideo_betterme_completetarget_current_level);
        tvNextLevel = view.findViewById(R.id.tv_livevideo_betterme_completetarget_next_level);
        ivNextLevel = view.findViewById(R.id.iv_livevideo_betterme_completetarget_next_level);
        llContent = view.findViewById(R.id.ll_livevideo_betterme_completetarget_content);
        mLottieAnimationView = view.findViewById(R.id.animation_view);
        return view;
    }

    @Override
    public void initData() {
        String reult = mStuAimResultEntity.getRealTimeVal();
        String target = mStuAimResultEntity.getAimValue();
        //目标类型
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(mStuAimResultEntity.getAimType())) {
            tvAimType.setText(BetterMeConfig.CORRECTRATE);
            reult = Math.round(Double.valueOf(reult) * 100) + "%";
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(mStuAimResultEntity.getAimType())) {
            tvAimType.setText(BetterMeConfig.PARTICIPATERATE);
            reult = Math.round(Double.valueOf(reult) * 100) + "%";
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(mStuAimResultEntity.getAimType())) {
            tvAimType.setText(BetterMeConfig.TALKTIME);
            reult = BetterMeUtil.secondToMinite(reult);
            target = BetterMeUtil.secondToMinite(target);
        }
        if (mStuAimResultEntity.isDoneAim()) {
//            tvAimValue.setText("已完成目标");
            tvAimValue.setText("目标" + target);
            pgComeletetar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable
                    .app_livevideo_enteampk_xiaomubiao_progressbar_finish));
        } else {
            tvAimValue.setText("目标" + target);
            pgComeletetar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable
                    .layerlst_livevideo_betterme_prog));
        }
        tvTips.setText(reult);

        double realTimeVal = Double.valueOf(mStuAimResultEntity.getRealTimeVal());
        double aimVal = Double.valueOf(mStuAimResultEntity.getAimValue());
        int persents = (int) (realTimeVal / aimVal * 100);
        setBetterMeProgress(persents);

        tvCurrentLevel.setText(mStuAimResultEntity.getSegment() + mStuAimResultEntity.getStar() + "级");
        tvLevelUpgraded.setText("还需完成" + mStuAimResultEntity.getAimNumber() + "场目标可升级");
        //设置当前段位的背景
        int currentLevelIndex = mStuAimResultEntity.getSegmentType() - 1;
        ivCurrentLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_NOSTAR[currentLevelIndex]);
        //当前星星的数量
        int currentStarsNumber = mStuAimResultEntity.getStar();
        //升段位需要的星星的数量
        int needsStarsNumber = BetterMeConfig.LEVEL_UPLEVEL_STARS[currentLevelIndex];
        switch (needsStarsNumber) {
            //下设3个小段位（星星）
            case 3:
                ivCurrentLevel.setImageResource(BetterMeConfig.QINFENBAIYIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设4个小段位（星星）
            case 4:
                ivCurrentLevel.setImageResource(BetterMeConfig.KEKUHUANGJIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设5个小段位（星星）
            case 5:
                ivCurrentLevel.setImageResource(BetterMeConfig.HENGXINBOJIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设6个小段位（星星）
            case 6:
                ivCurrentLevel.setImageResource(BetterMeConfig.ZUIQIANGXUEBA_STAR_IMAGE_RES[currentStarsNumber - 1]);
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
            tvNextLevel.setText(BetterMeConfig.LEVEL_NAMES[nextLevelIndex] + nextStarsNumber + "级");
            ivNextLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_DISS[nextLevelIndex]);
            switch (BetterMeConfig.LEVEL_UPLEVEL_STARS[nextLevelIndex]) {
                //下设3个小段位（星星）
                case 3:
                    ivNextLevel.setImageResource(BetterMeConfig.QINFENBAIYIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设4个小段位（星星）
                case 4:
                    ivNextLevel.setImageResource(BetterMeConfig.KEKUHUANGJIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设5个小段位（星星）
                case 5:
                    ivNextLevel.setImageResource(BetterMeConfig.HENGXINBOJIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设6个小段位（星星）
                case 6:
                    ivNextLevel.setImageResource(BetterMeConfig.ZUIQIANGXUEBA_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                default:
                    break;
            }
        } else {
            ivArrow.setVisibility(View.GONE);
            llNextLevel.setVisibility(View.GONE);
        }

        //小目标完成失败
        if (!mStuAimResultEntity.isDoneAim()) {
            onTargetFail();
        }
        //段位升级
        if (mStuAimResultEntity.isUpGrade()) {
            onUpgradeLevel();
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
                        || "img_4.png".equals(lottieImageAsset.getFileName()) ){
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
        ivGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                mOnpagerClose.onClose(BetterMeCompleteTargetPager.this);
            }
        });
        ivLevelIndroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnpagerClose.onShow(BetterMeViewImpl.PAGER_LEVEL_DISPLAY);
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
            tvCountdown.setText(seconds + "s ");
        }

        @Override
        public void onFinish() {
            mOnpagerClose.onClose(BetterMeCompleteTargetPager.this);
        }
    };

    /**
     * 段位升级
     */
    private void onUpgradeLevel() {
        ivArrow.setVisibility(View.GONE);
        llNextLevel.setVisibility(View.GONE);
        String string = CONGRATULATIONS_TO_UPGRADE + mStuAimResultEntity.getSegment() + mStuAimResultEntity.getStar() + "级";
        SpannableString spannableString = new SpannableString(string);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFE6600")), CONGRATULATIONS_TO_UPGRADE.length(), string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLevelUpgraded.setText(spannableString);
    }

    /**
     * 小目标完成失败
     */
    private void onTargetFail() {
        ivTitle.setImageResource(R.drawable.app_xiaomubiao_shellwindow_haokexi_title_pic);
        ivGreat.setImageResource(R.drawable.selector_livevideo_betterme_completetarget_keepon);
    }

    /**
     * 设置小目标进度
     */
    private void setBetterMeProgress(int progress) {
        logger.i("setBetterMeProgress : progress = " + progress);
        if (progress > 100) {
            progress = 100;
            pgComeletetar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable
                    .app_livevideo_enteampk_xiaomubiao_progressbar_finish));
        }
        pgComeletetar.setProgress(progress);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvTips.getLayoutParams();
        layoutParams.leftMargin = progress * SizeUtils.Dp2Px(mContext, 127) / 100;
        tvTips.setLayoutParams(layoutParams);
    }

    /**
     * 当前段位的索引
     */
    private int getCurrentLevelIndex(String level) {
        for (int i = 0; i < BetterMeConfig.LEVEL_NAMES.length; i++) {
            if (BetterMeConfig.LEVEL_NAMES[i].equals(level)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 当前星星的数量
     */
    private int getCurrentStarsNumber(String star) {
        int starNumber = -1;
        try {
            starNumber = Integer.parseInt(star);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return starNumber;
    }
}
