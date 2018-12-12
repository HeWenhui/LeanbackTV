package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnPagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * 英语小目标 完成小目标
 *
 * @author zhangyuansun
 * created  at 2018/12/10
 */
public class BetterMeCompleteTargetPager extends LiveBasePager {
    private OnPagerClose mOnpagerClose;
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
     * 图标 - 箭头
     */
    private ImageView ivCompletetarArrow;
    /**
     * 右侧段位
     */
    private LinearLayout llCompletetarLevelRight;
    /**
     * 升级后的段位
     */
    private TextView tvCompletetarLevelUpgraded;
    /**
     * 进度条 - 小目标
     */
    private ProgressBar pgComeletetarTarget;
    /**
     * 进度提示
     */
    private TextView tvComeletetarTips;
    /**
     * 目标值
     */
    private TextView tvComeletetarNumRight;


    private static final String CONGRATULATIONS_TO_UPGRADE = "恭喜你升级为";

    public BetterMeCompleteTargetPager(Context context, OnPagerClose onPagerClose) {
        super(context);
        this.mOnpagerClose = onPagerClose;
        initData();
        initListener();
    }

    /**
     * 测试代码，提测删除
     */
    private void test() {
        LinearLayout llContent = getRootView().findViewById(R.id.ll_livevideo_betterme_completetarget_content);
        LinearLayout llTest = new LinearLayout(mContext);
        llContent.addView(llTest);
        Button btnTest1 = new Button(mContext);
        btnTest1.setText("进度+1");
        Button btnTest2 = new Button(mContext);
        btnTest2.setText("进度-1");
        llTest.addView(btnTest1);
        llTest.addView(btnTest2);
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEngTargetPro(pgComeletetarTarget.getProgress() + 1);
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEngTargetPro(pgComeletetarTarget.getProgress() - 1);
            }
        });
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_complete_target, null);
        ivCompletetaraTitle = view.findViewById(R.id.iv_livevideo_betterme_completetarget_title);
        ivCompletetarGreat = view.findViewById(R.id.iv_livevideo_betterme_completetarget_great);
        tvCompletetarCountdown = view.findViewById(R.id.tv_livevideo_betterme_completetarget_countdown);
        ivCompletetarArrow = view.findViewById(R.id.iv_livevideo_betterme_completetarget_arrow);
        llCompletetarLevelRight = view.findViewById(R.id.ll_livevideo_betterme_completetarget_level_right);
        tvCompletetarLevelUpgraded = view.findViewById(R.id.tv_livevideo_betterme_completetarget_level_upgraded);
        pgComeletetarTarget = view.findViewById(R.id.pg_livevideo_better_num_target);
        tvComeletetarTips = view.findViewById(R.id.tv_livevideo_betterme_num_tips);
        tvComeletetarNumRight = view.findViewById(R.id.tv_livevideo_betterme_num_right);
        return view;
    }

    @Override
    public void initData() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
        onTargetFail();
//        setEngTargetPro(70);
        test();
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
        llCompletetarLevelRight.setVisibility(View.GONE);
        String string = CONGRATULATIONS_TO_UPGRADE + "倔强青铜2级";
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
        logger.d("setEngTargetPro:progress=" + progress);
        if (pgComeletetarTarget == null) {
            return;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress > 100) {
            progress = 100;
        }
        pgComeletetarTarget.setProgress(progress);

//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(tvComeletetarTips.getLayoutParams());
//        lp.setMargins(SizeUtils.Dp2Px(mContext, 143) * progress / 100 - SizeUtils.Dp2Px(mContext, 14), SizeUtils.Dp2Px(mContext, 29), 0, 0);
//        tvComeletetarTips.setLayoutParams(lp);
    }
}
