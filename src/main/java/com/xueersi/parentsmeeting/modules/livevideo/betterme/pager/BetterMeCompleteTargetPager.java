package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

/**
 * 英语小目标 完成小目标
 *
 * @author zhangyuansun
 * created  at 2018/12/10
 */
public class BetterMeCompleteTargetPager extends LiveBasePager {
    private OnBettePagerClose mOnpagerClose;
    /**
     * 图标 - 标题
     */
    private ImageView ivCompletetaraTitle;
    /**
     * 按钮 - 太棒啦
     */
    private ImageView ivCompletetarGreat;
    /**
     * 10s倒计时
     */
    private TextView tvCompletetarCountdown;
    /**
     * 进度条 - 小目标
     */
    private ProgressBar pgComeletetar;
    /**
     * 进度提示
     */
    private TextView tvComeletetarTips;

    private TextView tvComeletetarAimType;
    /**
     * 目标值
     */
    private TextView tvComeletetarAimValue;
    /**
     * 图标 - 箭头
     */
    private ImageView ivCompletetarArrow;
    /**
     * 下一段位布局
     */
    private LinearLayout llCompletetarNextLevel;
    private TextView tvCompletetarCurrentLevel;
    private ImageView ivCompletetarCurrentLevel;
    private TextView tvCompletetarNextLevel;
    private ImageView ivCompletetarNextLevel;
    /**
     * 升级提示
     */
    private TextView tvCompletetarLevelUpgraded;

    StuAimResultEntity mStuAimResultEntity;
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
        ivCompletetaraTitle = view.findViewById(R.id.iv_livevideo_betterme_completetarget_title);
        ivCompletetarGreat = view.findViewById(R.id.iv_livevideo_betterme_completetarget_great);
        tvCompletetarCountdown = view.findViewById(R.id.tv_livevideo_betterme_completetarget_countdown);
        ivCompletetarArrow = view.findViewById(R.id.iv_livevideo_betterme_completetarget_arrow);
        llCompletetarNextLevel = view.findViewById(R.id.ll_livevideo_betterme_completetarget_next_level);
        tvCompletetarLevelUpgraded = view.findViewById(R.id.tv_livevideo_betterme_completetarget_level_upgraded);
        pgComeletetar = view.findViewById(R.id.pg_livevideo_better_completetar);
        tvComeletetarTips = view.findViewById(R.id.tv_livevideo_betterme_aimtips);
        tvComeletetarAimType = view.findViewById(R.id.tv_livevideo_betterme_completetarget_aimtype);
        tvComeletetarAimValue = view.findViewById(R.id.tv_livevideo_betterme_completetarget_aimvalue);
        tvCompletetarCurrentLevel = view.findViewById(R.id.tv_livevideo_betterme_completetarget_current_level);
        ivCompletetarCurrentLevel = view.findViewById(R.id.iv_livevideo_betterme_completetarget_current_level);
        tvCompletetarNextLevel = view.findViewById(R.id.tv_livevideo_betterme_completetarget_next_level);
        ivCompletetarNextLevel = view.findViewById(R.id.iv_livevideo_betterme_completetarget_next_level);
        return view;
    }

    @Override
    public void initData() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }

        //目标类型
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(mStuAimResultEntity.getAimType())) {
            tvComeletetarAimType.setText(BetterMeConfig.CORRECTRATE);
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(mStuAimResultEntity.getAimType())) {
            tvComeletetarAimType.setText(BetterMeConfig.PARTICIPATERATE);
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(mStuAimResultEntity.getAimType())) {
            tvComeletetarAimType.setText(BetterMeConfig.TALKTIME);
        }

        tvComeletetarAimValue.setText("目标" + mStuAimResultEntity.getAimValue());

        try {
            int realTimeVal = Integer.parseInt(mStuAimResultEntity.getRealTimeVal());
            int aimVal = Integer.parseInt(mStuAimResultEntity.getAimValue());
            int persents = (int) ((float) realTimeVal / (float) aimVal * 100);
            setEngTargetPro(persents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvCompletetarCurrentLevel.setText(mStuAimResultEntity.getSegment() + mStuAimResultEntity.getStar() + "星");
        tvCompletetarLevelUpgraded.setText("还需完成" + mStuAimResultEntity.getAimNumber() + "场目标可升级");
        //设置当前段位的背景
        int currentLevelIndex = getCurrentLevelIndex(mStuAimResultEntity.getSegment());
        ivCompletetarCurrentLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_NOSTAR[currentLevelIndex]);
        //当前星星的数量
        int currentStarsNumber = getCurrentStarsNumber(mStuAimResultEntity.getStar());
        //升段位需要的星星的数量
        int needsStarsNumber = BetterMeConfig.LEVEL_UPLEVEL_STARS[currentLevelIndex];
        switch (needsStarsNumber) {
            //下设3个小段位（星星）
            case 3:
                ivCompletetarCurrentLevel.setImageResource(BetterMeConfig.QINFENBAIYIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设4个小段位（星星）
            case 4:
                ivCompletetarCurrentLevel.setImageResource(BetterMeConfig.KEKUHUANGJIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设5个小段位（星星）
            case 5:
                ivCompletetarCurrentLevel.setImageResource(BetterMeConfig.HENGXINBOJIN_STAR_IMAGE_RES[currentStarsNumber - 1]);
                break;
            //下设6个小段位（星星）
            case 6:
                ivCompletetarCurrentLevel.setImageResource(BetterMeConfig.ZUIQIANGXUEBA_STAR_IMAGE_RES[currentStarsNumber - 1]);
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
            tvCompletetarNextLevel.setText(BetterMeConfig.LEVEL_NAMES[nextLevelIndex] + nextStarsNumber + "星");
            ivCompletetarNextLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_DISS[nextLevelIndex]);
            switch (BetterMeConfig.LEVEL_UPLEVEL_STARS[nextLevelIndex]) {
                //下设3个小段位（星星）
                case 3:
                    ivCompletetarNextLevel.setImageResource(BetterMeConfig.QINFENBAIYIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设4个小段位（星星）
                case 4:
                    ivCompletetarNextLevel.setImageResource(BetterMeConfig.KEKUHUANGJIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设5个小段位（星星）
                case 5:
                    ivCompletetarNextLevel.setImageResource(BetterMeConfig.HENGXINBOJIN_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                //下设6个小段位（星星）
                case 6:
                    ivCompletetarNextLevel.setImageResource(BetterMeConfig.ZUIQIANGXUEBA_STAR_IMAGE_RES[nextStarsNumber - 1]);
                    break;
                default:
                    break;
            }
        } else {
            ivCompletetarArrow.setVisibility(View.GONE);
            llCompletetarNextLevel.setVisibility(View.GONE);
        }

        //小目标完成失败
        if ("0".equals(mStuAimResultEntity.getIsDoneAim())) {
            onTargetFail();
        }
        //段位升级
        if ("1".equals(mStuAimResultEntity.getIsUpGrade())) {
            onUpgradeLevel();
        }
    }

    @Override
    public void initListener() {
        ivCompletetarGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                mOnpagerClose.onClose(BetterMeCompleteTargetPager.this);
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
            mOnpagerClose.onClose(BetterMeCompleteTargetPager.this);
        }
    };

    /**
     * 段位升级
     */
    private void onUpgradeLevel() {
        ivCompletetarArrow.setVisibility(View.GONE);
        llCompletetarNextLevel.setVisibility(View.GONE);
        String string = CONGRATULATIONS_TO_UPGRADE + mStuAimResultEntity.getSegment() + mStuAimResultEntity.getStar() + "星";
        SpannableString spannableString = new SpannableString(string);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFE6600")), CONGRATULATIONS_TO_UPGRADE.length(), string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCompletetarLevelUpgraded.setText(spannableString);
    }

    /**
     * 小目标完成失败
     */
    private void onTargetFail() {
        ivCompletetaraTitle.setImageResource(R.drawable.app_xiaomubiao_shellwindow_haokexi_title_pic);
        ivCompletetarGreat.setImageResource(R.drawable.selector_livevideo_betterme_completetarget_keepon);
    }

    /**
     * 设置小目标进度
     */
    private void setEngTargetPro(int progress) {
        logger.i("setEngTargetPro:progress=" + progress);
        if (pgComeletetar == null) {
            return;
        }
        pgComeletetar.setProgress(progress);
        tvComeletetarTips.setText(progress + "%");

        pgComeletetar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                pgComeletetar.getViewTreeObserver().removeOnPreDrawListener(this);
                setTipsLayout();
                return false;
            }
        });
    }

    /**
     * 设置Tips跟小目标进度条对齐
     */
    private void setTipsLayout() {
        ViewGroup rlCompletetarAim = mView.findViewById(R.id.rl_livevideo_betterme_completetarget_aim);
        int[] loc = ViewUtil.getLoc(pgComeletetar, rlCompletetarAim);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvComeletetarTips.getLayoutParams();
        lp.leftMargin = loc[0] - tvComeletetarTips.getWidth() / 2 + pgComeletetar.getWidth() * pgComeletetar.getProgress() / pgComeletetar.getMax();
        logger.i("setLayout:left=" + loc[0] + ",top=" + loc[1]);
        tvComeletetarTips.setLayoutParams(lp);
        tvComeletetarTips.setVisibility(View.VISIBLE);
    }

    /**
     * 当前段位的索引
     */
    public int getCurrentLevelIndex(String level) {
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
    public int getCurrentStarsNumber(String star) {
        int starNumber = -1;
        try {
            starNumber = Integer.parseInt(star);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return starNumber;
    }
}
