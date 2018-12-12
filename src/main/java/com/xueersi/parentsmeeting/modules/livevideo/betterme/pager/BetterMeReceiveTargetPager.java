package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnPagerClose;

/**
 * 英语小目标 本场小目标
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeReceiveTargetPager extends BasePager {
    private OnPagerClose mOnpagerClose;
    /**
     * 按钮 - 准备好啦
     */
    private ImageView ivReceivetarReady;
    /**
     * 10s倒计时
     */
    private TextView tvCompletetarCountdown;

    public BetterMeReceiveTargetPager(Context context, OnPagerClose onPagerClose) {
        super(context);
        this.mOnpagerClose = onPagerClose;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_receive_target, null);
        ivReceivetarReady = view.findViewById(R.id.iv_livevideo_betterme_receivetarget_ready);
        tvCompletetarCountdown = view.findViewById(R.id.tv_livevideo_betterme_receivetarget_countdown);
        return view;
    }

    @Override
    public void initData() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    @Override
    public void initListener() {
        ivReceivetarReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                mOnpagerClose.onClose(BetterMeReceiveTargetPager.this);
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
            long i = millisUntilFinished / 1000;
            tvCompletetarCountdown.setText(i + "s ");
        }

        @Override
        public void onFinish() {
            mOnpagerClose.onClose(BetterMeReceiveTargetPager.this);
        }
    };
}
