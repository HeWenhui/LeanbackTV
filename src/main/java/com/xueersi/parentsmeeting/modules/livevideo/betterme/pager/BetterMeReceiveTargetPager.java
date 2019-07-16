package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.OtherBllEntrance;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnBettePagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMeViewImpl;

/**
 * 英语小目标 本场小目标
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeReceiveTargetPager extends BasePager {
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
        return view;
    }

    @Override
    public void initData() {
        logger.i("initData()");
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
        String target = mBetterMeEntity.getAimValue();
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(mBetterMeEntity.getAimType())) {
            tvReceiveTarType.setText(BetterMeConfig.CORRECTRATE);
            target = (int) (Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(mBetterMeEntity.getAimType())) {
            tvReceiveTarType.setText(BetterMeConfig.PARTICIPATERATE);
            target = (int) (Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(mBetterMeEntity.getAimType())) {
            tvReceiveTarType.setText(BetterMeConfig.TALKTIME);
            target = BetterMeUtil.secondToMinite(target);
        }
        tvReceiveTarValue.setText(target);

        tvReceiveTarCurrentLevel.setText(mStuSegmentEntity.getSegment() + mStuSegmentEntity.getStar() + "星");
        tvReceiveTarUpdateTips.setText("还需完成" + mStuSegmentEntity.getAimNumber() + "场目标可升级");

        //设置当前段位的背景
        int currentLevelIndex = getCurrentLevelIndex(mStuSegmentEntity.getSegment());
        ivReceivetarCurrentLevel.setBackgroundResource(BetterMeConfig.LEVEL_IMAGE_RES_NOSTAR[currentLevelIndex]);
        //当前星星的数量
        int currentStarsNumber = getCurrentStarsNumber(mStuSegmentEntity.getStar());
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
            tvReceiveTarNextLevel.setText(BetterMeConfig.LEVEL_NAMES[nextLevelIndex] + nextStarsNumber + "星");
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
                OtherBllEntrance.EnglishTeamPK.startPK(mContext, true);
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
            OtherBllEntrance.EnglishTeamPK.startPK(mContext, true);
        }
    };

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
        int stasNumber = -1;
        try {
            stasNumber = Integer.parseInt(star);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stasNumber;
    }
}
