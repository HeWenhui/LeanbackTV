package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

/**
* 战队PK 二期  贡献之星
*@author chekun
*created  at 2019/1/30 13:48
*/
public class TeamPkContributionPager extends BasePager {
    /**
     * toast 展示时间
     **/
    private static final long DISPLAY_TIME_DURATION = 3000;
    private final TeamPkBll teamPkBll;
    private TeamEnergyAndContributionStarEntity mData;
    private LottieAnimationView animationView;
    private ImageView ivClose;
    private TimeCountDowTextView timeCountDowTextView;

    public TeamPkContributionPager(Context context, TeamPkBll pkBll , TeamEnergyAndContributionStarEntity data){
        super(context);
        teamPkBll = pkBll;
        mData = data;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_contributionstar, null);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0) {
                    showContributionStar();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        Log.e("teampkContributionPager","====>initViewcalled");
        animationView = view.findViewById(R.id.lav_teampk_contribution);
        ivClose = view.findViewById(R.id.iv_teampk_contribution_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePager();
            }
        });
        timeCountDowTextView = view.findViewById(R.id.tv_teampk_contribution_time);
        return view;
    }


    private void  showContributionStar(){

    }


    /**
     * 开始自动关闭
     */
    public void startAutoClose(){
        timeCountDowTextView.setTimeDuration(3);
        timeCountDowTextView.setTimeSuffix("秒后关闭");
        timeCountDowTextView.startCountDow();
        timeCountDowTextView.setTimeCountDowListener(new TimeCountDowTextView.TimeCountDowListener() {
            @Override
            public void onFinish() {
                closePager();
            }
        });
    }


    private void closePager() {
        try {
            releasRes();
            if (mView.getParent() != null) {
                teamPkBll.closeCurrentPager();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasRes() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasRes();
    }
}
