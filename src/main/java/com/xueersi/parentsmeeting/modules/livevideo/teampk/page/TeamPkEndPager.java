package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

/**
 * 战队PK 二期  老师结束Pk
 *
 * @author chekun
 * created  at 2019/1/18 14:44
 */
public class TeamPkEndPager extends TeamPkBasePager {
    private TextView tvToast;
    /**
     * 缩放动画弹性系数
     */
    private static final float SCALE_ANIM_FACTOR = 0.23f;
    /**
     * toast 展示时间
     **/
    private static final long DISPLAY_TIME_DURATION = 3000;
    private final TeamPkBll teamPkBll;

    public TeamPkEndPager(Context context, TeamPkBll pkBll){
        super(context);
        teamPkBll = pkBll;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_end, null);
        tvToast = view.findViewById(R.id.tv_teampk_pk_end);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0) {
                    showPkEndToast();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        return view;
    }

    private void showPkEndToast() {
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_aq_award);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mView.postDelayed(autoCloseTask, DISPLAY_TIME_DURATION);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvToast.startAnimation(scaleAnimation);
    }

    Runnable autoCloseTask = new Runnable() {
        @Override
        public void run() {
            closePager();
        }
    };

    private void closePager() {
        try {
            if (mView.getParent() != null) {
                teamPkBll.closeCurrentPager();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mView.removeCallbacks(autoCloseTask);
        }
    }

}
