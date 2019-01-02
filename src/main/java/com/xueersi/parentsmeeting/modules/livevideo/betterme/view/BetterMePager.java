package com.xueersi.parentsmeeting.modules.livevideo.betterme.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.OnPagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeCompleteTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeIntroductionPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeLevelDisplayPager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.pager.BetterMeReceiveTargetPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;

/**
 * 英语小目标 view层
 *
 * @author zhangyuansun
 * created  at 2018/12/4
 */
public class BetterMePager implements BetterMeContract.BetterMeView, OnPagerClose {
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

    public BetterMePager(Context context) {
        this.mContext = context;
    }

    /**
     * 测试代码，提测删除
     */
    private void test() {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            rlBetterMeContent.setId(R.id.rl_livevideo_content_speechbul);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        LinearLayout llTest = new LinearLayout(mContext);
        rlBetterMeContent.addView(llTest);
        Button btnTest1 = new Button(mContext);
        btnTest1.setText("小目标介绍");
        Button btnTest2 = new Button(mContext);
        btnTest2.setText("段位展示");
        Button btnTest3 = new Button(mContext);
        btnTest3.setText("本场小目标");
        Button btnTest4 = new Button(mContext);
        btnTest4.setText("完成小目标");
        llTest.addView(btnTest1);
        llTest.addView(btnTest2);
        llTest.addView(btnTest3);
        llTest.addView(btnTest4);
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntroductionPager();
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelDisplayPager();
            }
        });
        btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBetterMePresenter.getBetterMe();
            }
        });
        btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBetterMePresenter.getStuAimResult();
            }
        });
    }

    @Override
    public void setRootView(RelativeLayout rootView) {
        this.mRootView = rootView;
        test();
    }

    /**
     * 小目标介绍
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
        rlBetterMeContent.addView(new BetterMeIntroductionPager(mContext, this).getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 段位展示
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
        rlBetterMeContent.addView(new BetterMeLevelDisplayPager(mContext, this).getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 收到本场小目标
     */
    @Override
    public void showReceiveTargetPager(StuSegmentEntity stuSegmentEntity, BetterMeEntity betterMeEntity) {
        if (rlBetterMeContent == null) {
            rlBetterMeContent = new RelativeLayout(mContext);
            if (mRootView != null) {
                mRootView.addView(rlBetterMeContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        rlBetterMeContent.addView(new BetterMeReceiveTargetPager(stuSegmentEntity, betterMeEntity, mContext, this).getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 完成本场小目标
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
        rlBetterMeContent.addView(new BetterMeCompleteTargetPager(stuAimResultEntity, mContext, this).getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
    public void onNext(final int pagerType, int duration) {
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onNext(pagerType);
            }
        }, duration);
    }

    @Override
    public void onNext(int pagerType) {
        switch (pagerType) {
            case PAGER_INTRODUCTION:
                showIntroductionPager();
                break;
            case PAGER_LEVEL_DISPLAY:
                showLevelDisplayPager();
                break;
            case PAGER_RECEIVE_TARGET:
                break;
            default:
                break;
        }
    }
}
