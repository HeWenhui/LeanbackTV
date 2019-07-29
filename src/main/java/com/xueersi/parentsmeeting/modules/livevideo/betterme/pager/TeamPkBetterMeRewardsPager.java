package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.TeamPKBetterMeRewardsEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.lottie.RisingBubbleLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 完成目标奖励
 */
public class TeamPkBetterMeRewardsPager extends LiveBasePager {
    private TeamPKBetterMeRewardsEntity entity;
    private ProgressBar pgTeampkLead;
    private View rlTeampkLeadLeft;
    private View rlTeampkLeadRight;
    private ImageView ivTeampkLeadProg;
    private ImageView ivTeampkMine;
    private ImageView ivTeampkOther;
    private TextView tvTeampkLeadFireAddLeft;
    private TextView tvTeampkLeadScoreLeft;
    private TextView ivTeampkLeadFireAddRight;
    private TextView tvTeampkLeadScoreRight;
    private TextView tvClose;
    private LottieAnimationView mLottieView;
    private int pattern;
    private float finalFprog;
    private Handler handler = new Handler(Looper.getMainLooper());

    public TeamPkBetterMeRewardsPager(Context context, int pattern, TeamPKBetterMeRewardsEntity entity, OnPagerClose onPagerClose) {
        super(context, false);
        this.entity = entity;
        this.pattern = pattern;
        this.onPagerClose = onPagerClose;
        initData();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_betterme_rewards, null);
        pgTeampkLead = view.findViewById(R.id.pg_livevideo_en_teampk_lead);
        rlTeampkLeadLeft = view.findViewById(R.id.rl_livevideo_en_teampk_lead_left);
        rlTeampkLeadRight = view.findViewById(R.id.rl_livevideo_en_teampk_lead_right);
        ivTeampkLeadProg = view.findViewById(R.id.iv_livevideo_en_teampk_lead_prog);
        ivTeampkMine = view.findViewById(R.id.iv_livevideo_en_teampk_mine);
        ivTeampkOther = view.findViewById(R.id.iv_livevideo_en_teampk_other);
        tvTeampkLeadFireAddLeft = view.findViewById(R.id.tv_livevideo_en_teampk_lead_fire_add_left);
        tvTeampkLeadScoreLeft = view.findViewById(R.id.tv_livevideo_en_teampk_lead_score_left);
        ivTeampkLeadFireAddRight = view.findViewById(R.id.iv_livevideo_en_teampk_lead_fire_add_right);
        tvTeampkLeadScoreRight = view.findViewById(R.id.tv_livevideo_en_teampk_lead_score_right);
        tvClose = view.findViewById(R.id.tv_livevideo_en_teampk_betterme_rewards_close);
        mLottieView = view.findViewById(R.id.lav_livevideo_en_teampk_betterme_rewards_bubble);
        if (pattern == LiveVideoConfig.LIVE_PATTERN_COMMON) {
            //三分屏
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_4_3);
        } else if (pattern == LiveVideoConfig.LIVE_PATTERN_2) {
            //全身直播
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_16_9);
        }
        return view;
    }

    @Override
    public void initData() {
//        int[] res = EnTeamPkConfig.TEAM_RES;
//        ivTeampkMine.setImageResource(res[enTeamPkRankEntity.getMyTeam()]);
//        int progress = 50;
//        float fprog = 0.5f;
//        int total = enTeamPkRankEntity.getMyTeamTotal() + enTeamPkRankEntity.getOpTeamTotal();
//        ivTeampkOther.setImageResource(res[enTeamPkRankEntity.getBpkTeamId()]);
//        tvTeampkLeadFireAddLeft.setText("+" + enTeamPkRankEntity.getMyTeamCurrent());
//        tvTeampkLeadScoreLeft.setText("" + enTeamPkRankEntity.getMyTeamTotal());
//        ivTeampkLeadFireAddRight.setText("+" + enTeamPkRankEntity.getOpTeamCurrent());
//        tvTeampkLeadScoreRight.setText("" + enTeamPkRankEntity.getOpTeamTotal());
//        if (total != 0) {
//            fprog = (float) (enTeamPkRankEntity.getMyTeamTotal()) / (float) (total);
//            progress = (int) ((float) (enTeamPkRankEntity.getMyTeamTotal() * 100) / (float) (total));
//        }
        int closeDelay = 10000;
        final AtomicInteger integer = new AtomicInteger(closeDelay / 1000);
        int countDelay = 1000;

//        pgTeampkLead.setProgress(progress);
//        finalFprog = fprog;
        final ViewTreeObserver viewTreeObserver = pgTeampkLead.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                pgTeampkLead.getViewTreeObserver().removeOnPreDrawListener(this);
                setProgFire();
                return false;
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = integer.decrementAndGet();
                if (count == 0) {
                    if (onPagerClose != null) {
                        onPagerClose.onClose(TeamPkBetterMeRewardsPager.this);
                    } else {
                        ViewGroup group = (ViewGroup) mView.getParent();
                        if (group != null) {
                            group.removeView(mView);
                        }
                    }
                } else {
                    tvClose.setText(integer + "s后关闭");
                    tvClose.postDelayed(this, 1000);
                }
            }
        }, countDelay);
        startLottie();
    }

    private void startLottie() {
        final LottieEffectInfo bubbleEffectInfo = new RisingBubbleLottieEffectInfo(mContext, entity);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return bubbleEffectInfo.fetchBitmapFromAssets(
                        mLottieView,
                        lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(),
                        lottieImageAsset.getHeight(),
                        mContext);
            }
        };
        mLottieView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "rising_bubble");
        mLottieView.setImageAssetDelegate(imageAssetDelegate);
        mLottieView.useHardwareAcceleration(true);
        mLottieView.playAnimation();
    }

    public void setVideoLayout() {
        lastLeftMargin = 0;
        final ViewTreeObserver viewTreeObserver = pgTeampkLead.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                boolean same = setProgFire();
                if (same) {
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.removeOnPreDrawListener(this);
                    }
                    pgTeampkLead.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }
        });
    }

    /** 火焰上次的位置 */
    private int lastLeftMargin;
    private int lastwidthPg = 0;

    //设置火焰在进度条的位置
    private boolean setProgFire() {
        //左边的宽度
        int widthLeft = rlTeampkLeadLeft.getWidth();
        //右边的宽度
        int widthRight = rlTeampkLeadRight.getWidth();
        //总体的宽度
        int myWidth = mView.getWidth();
        int widthPg = myWidth - Math.max(widthLeft, widthRight) * 2 - SizeUtils.Dp2Px(mContext, 8);
        //进度条的最大宽度
        int maxPgWidth = SizeUtils.Dp2Px(mContext, 364);
        if (widthPg > maxPgWidth) {
            widthPg = maxPgWidth;
        }
        int pgLeft = (myWidth - widthPg) / 2;
        RelativeLayout.LayoutParams lpIvProg = (RelativeLayout.LayoutParams) ivTeampkLeadProg.getLayoutParams();
        RelativeLayout.LayoutParams lpPg = (RelativeLayout.LayoutParams) pgTeampkLead.getLayoutParams();
        if (lpPg.width != widthPg) {
            //设置进度条的宽度，可以显示下左右的布局
            lpPg.width = widthPg;
            pgTeampkLead.setLayoutParams(lpPg);
        }
        {
            int ivWidth = ivTeampkLeadProg.getWidth();
            int pgLeftMargin = (int) (pgTeampkLead.getLeft() + widthPg * finalFprog) - ivWidth / 2;
            //为了和进度条对齐，计算火的宽度
            float fireRatio = 88.0f / 395.0f;
            int fireWidth = (int) (ivWidth * fireRatio);
            //火最大和进度条右边距对齐
            int maxLeftMargin = (pgTeampkLead.getLeft() + widthPg - ivWidth / 2 - fireWidth / 2);
            int leftMargin2 = Math.min(pgLeftMargin, maxLeftMargin);
            if (lpIvProg.leftMargin != leftMargin2) {
                lpIvProg.leftMargin = leftMargin2;
                ivTeampkLeadProg.setLayoutParams(lpIvProg);
            }
            if (ivTeampkLeadProg.getVisibility() != View.VISIBLE) {
                ivTeampkLeadProg.setVisibility(View.VISIBLE);
            }
        }
        {
            //设置左边的左边距
            RelativeLayout.LayoutParams lpLet = (RelativeLayout.LayoutParams) rlTeampkLeadLeft.getLayoutParams();
            int leftMargin = pgLeft - widthLeft - SizeUtils.Dp2Px(mContext, 4);
            if (leftMargin != lpLet.leftMargin) {
                lpLet.leftMargin = leftMargin;
                rlTeampkLeadLeft.setLayoutParams(lpLet);
            }
        }
        {
            //设置右边的左边距
            RelativeLayout.LayoutParams lpRight = (RelativeLayout.LayoutParams) rlTeampkLeadRight.getLayoutParams();
            int leftMargin = (pgLeft + widthPg) + SizeUtils.Dp2Px(mContext, 4);
            if (leftMargin != lpRight.leftMargin) {
                lpRight.leftMargin = leftMargin;
                rlTeampkLeadRight.setLayoutParams(lpRight);
            }
        }
        //两次距离一样，说明绘制完成
        if (lastLeftMargin == lpIvProg.leftMargin && lastwidthPg == widthPg) {
            return true;
        }
        lastLeftMargin = lpIvProg.leftMargin;
        lastwidthPg = widthPg;
        return false;
    }
}
