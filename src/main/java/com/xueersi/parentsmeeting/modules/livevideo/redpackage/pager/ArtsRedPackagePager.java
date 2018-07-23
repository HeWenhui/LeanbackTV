package com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class ArtsRedPackagePager extends BasePager {
    //待领取红包的布局
    private RelativeLayout rlArtsUnopenRed;
    //已经领取的红包的布局
    private RelativeLayout rlArtsOpenRed;
    //    private ImageView ivArtsBoard;
    //待领取红包的领取按钮
    private ImageView ivArtsOpen;
    //关闭红包的关闭按钮
    private ImageView ivClose;
    //已经领取的红包的金额
    private TextView tvArtsOpenRedMoney;
    //打开红包的监听器
    private RedPackageTouchListenr RedPackageTouchListenr;
    //关闭红包的监听器
    private CancelRedPackageTouchListener cancelRedPackageTouchListener;
    //打开红包后的关闭按钮
    private ImageView ivOpenClose;
    //是否需要再执行一次动画
    private boolean isRepeat = true;

    public ArtsRedPackagePager(Context context) {
        super(context);
        initListener();
    }

    public void setRedPackageTouchListenr(RedPackageTouchListenr RedPackageTouchListenr) {
        this.RedPackageTouchListenr = RedPackageTouchListenr;
    }

    public void setCancelRedPackageTouchListener(CancelRedPackageTouchListener cancelRedPackageTouchListener) {
        this.cancelRedPackageTouchListener = cancelRedPackageTouchListener;
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.layout_livevideo_arts_redpackage, null);
        rlArtsUnopenRed = view.findViewById(R.id.rl_livevideo_arts_redpackage_unopen);
//        ivArtsBoard = view.findViewById(R.id.iv_livevideo_arts_redpackage_board);
        ivArtsOpen = view.findViewById(R.id.iv_livevideo_arts_redpackage_open);
        ivClose = view.findViewById(R.id.iv_livevideo_arts_redpackage_close);

        rlArtsOpenRed = view.findViewById(R.id.rl_livevideo_arts_redpackage_open);
        tvArtsOpenRedMoney = view.findViewById(R.id.tv_livevidoe_arts_redpackage_money);
        ivOpenClose = view.findViewById(R.id.iv_livevideo_arts_open_redpackage_close);

        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivArtsOpen.setOnTouchListener(openTouchListener);
        ivClose.setOnTouchListener(closeTouchListener);
        ivOpenClose.setOnTouchListener(openCloseTouchListener);

    }

    private View.OnTouchListener openTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (RedPackageTouchListenr != null) {
                RedPackageTouchListenr.openRedPackage();
            }
            return false;
        }
    };
    private View.OnTouchListener closeTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (cancelRedPackageTouchListener != null) {
                cancelRedPackageTouchListener.cancelRedPackage();
            }
            return false;
        }
    };

    private View.OnTouchListener openCloseTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (cancelRedPackageTouchListener != null) {
                cancelRedPackageTouchListener.cancelRedPackage();
            }
            return false;
        }
    };

    @Override
    public void initData() {

    }

    /**
     * 打开红包，更新状态
     */
    public void updateStatus(String goldNum) {
        rlArtsUnopenRed.setVisibility(View.GONE);
        rlArtsOpenRed.setVisibility(View.VISIBLE);
        tvArtsOpenRedMoney.setText("+" + goldNum);
        rlArtsOpenRed.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //执行一次后就不再执行
                if (isRepeat) {
                    startAnimotor(rlArtsOpenRed);
                    isRepeat = false;
                }

            }
        });

    }

    /**
     * 开始进行动画
     *
     * @param view
     */
    private void startAnimotor(View view) {
        if (view != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            Animator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.2f, 1.0f);
            Animator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.2f, 1.0f);
            animatorSet.playTogether(animatorX, animatorY);
            animatorSet.setDuration(500);
        }
    }

    public interface RedPackageTouchListenr {
        void openRedPackage();
    }

    public interface CancelRedPackageTouchListener {
        void cancelRedPackage();
    }

    public CancelRedPackageTouchListener getCancelRedPackageTouchListener() {
        return cancelRedPackageTouchListener;
    }
}
