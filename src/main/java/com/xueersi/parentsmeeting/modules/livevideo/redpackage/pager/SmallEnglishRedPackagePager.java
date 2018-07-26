package com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SmallEnglishRedPackagePager extends BasePager {
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
    private RedPackageOpenListenr redPackageOpenListenr;
    //关闭红包的监听器
    private CancelRedPackageTouchListener cancelRedPackageTouchListener;
    //打开红包后的关闭按钮
    private ImageView ivOpenClose;
    //是否需要再执行一次动画
    private boolean isRepeat = true;

    public SmallEnglishRedPackagePager(Context context) {
        super(context);
        initListener();
    }

    public void setRedPackageOpenListenr(RedPackageOpenListenr RedPackageOpenListenr) {
        this.redPackageOpenListenr = RedPackageOpenListenr;
    }

    public void setCancelRedPackageTouchListener(CancelRedPackageTouchListener cancelRedPackageTouchListener) {
        this.cancelRedPackageTouchListener = cancelRedPackageTouchListener;
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.layout_livevideo_small_english_redpackage, null);
        rlArtsUnopenRed = view.findViewById(R.id.rl_livevideo_small_english_redpackage_unopen);
//        ivArtsBoard = view.findViewById(R.id.iv_livevideo_small_english_redpackage_board);
        ivArtsOpen = view.findViewById(R.id.iv_livevideo_small_english_redpackage_open);
        ivClose = view.findViewById(R.id.iv_livevideo_small_english_redpackage_close);

        rlArtsOpenRed = view.findViewById(R.id.rl_livevideo_small_english_redpackage_open);
        tvArtsOpenRedMoney = view.findViewById(R.id.tv_livevidoe_small_english_redpackage_money);
        ivOpenClose = view.findViewById(R.id.iv_livevideo_small_english_open_redpackage_close);

        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivArtsOpen.setOnClickListener(openListener);
        ivClose.setOnClickListener(closeTouchListener);
        ivOpenClose.setOnClickListener(openCloseTouchListener);

    }

    private View.OnClickListener openListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (redPackageOpenListenr != null) {
                redPackageOpenListenr.openRedPackage();
            }
        }
    };
    private View.OnClickListener closeTouchListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (cancelRedPackageTouchListener != null) {
                cancelRedPackageTouchListener.cancelRedPackage();
            }
        }

    };

    private View.OnClickListener openCloseTouchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cancelRedPackageTouchListener != null) {
                cancelRedPackageTouchListener.cancelRedPackage();
            }
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

    public interface RedPackageOpenListenr {
        void openRedPackage();
    }

    public interface CancelRedPackageTouchListener {
        void cancelRedPackage();
    }

    public CancelRedPackageTouchListener getCancelRedPackageTouchListener() {
        return cancelRedPackageTouchListener;
    }
}
