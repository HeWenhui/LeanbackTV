package com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.pager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;

public class SmallEnglishRedPackagePager extends BasePager implements Handler.Callback {
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

    //控制登录使用动画
    //rl_livevideo_small_english_redpackage_open_animotion
    private RelativeLayout openRedPackageAnimotion;

//    private Handler mHandler = new Handler(Looper.getMainLooper());

    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(Looper.getMainLooper(), this);

    public SmallEnglishRedPackagePager(Context context) {
        super(context);
        initListener();
        initStates();
    }

    private void initStates() {
        rlArtsUnopenRed.setVisibility(View.VISIBLE);
        rlArtsOpenRed.setVisibility(View.GONE);

    }

    public void setRedPackageOpenListenr(RedPackageOpenListenr RedPackageOpenListenr) {
        this.redPackageOpenListenr = RedPackageOpenListenr;
    }

    public void setCancelRedPackageTouchListener(CancelRedPackageTouchListener cancelRedPackageTouchListener) {
        this.cancelRedPackageTouchListener = cancelRedPackageTouchListener;
    }

    @Override
    public View initView() {

        mView = View.inflate(mContext, R.layout.layout_livevideo_small_english_redpackage, null);
        rlArtsUnopenRed = mView.findViewById(R.id.rl_livevideo_small_english_redpackage_unopen);
//        ivArtsBoard = view.findViewById(R.id.iv_livevideo_small_english_redpackage_board);
        ivArtsOpen = mView.findViewById(R.id.iv_livevideo_small_english_redpackage_open);
        ivClose = mView.findViewById(R.id.iv_livevideo_small_english_redpackage_close);

        rlArtsOpenRed = mView.findViewById(R.id.rl_livevideo_small_english_redpackage_open);
        tvArtsOpenRedMoney = mView.findViewById(R.id.tv_livevidoe_small_english_redpackage_money);
        ivOpenClose = mView.findViewById(R.id.iv_livevideo_small_english_open_redpackage_close);
        openRedPackageAnimotion = mView.findViewById(R.id.rl_livevideo_small_english_redpackage_open_animotion);
        return mView;
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
        @Override
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
     * 打开红包，更新状态，3秒后自动消失
     */
    public void updateStatus(String goldNum) {
        rlArtsUnopenRed.setVisibility(View.GONE);
        rlArtsOpenRed.setVisibility(View.VISIBLE);
        tvArtsOpenRedMoney.setText("+" + goldNum);
        if (mView != null) {
            mVPlayVideoControlHandler.removeCallbacks(closeRunnable);
            mVPlayVideoControlHandler.postDelayed(closeRunnable, 3000);
        }
        isRepeat = true;
        rlArtsOpenRed.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //执行一次后就不再执行
                if (isRepeat) {
                    startAnimotor(openRedPackageAnimotion);
                    isRepeat = false;
                }
            }
        });
    }

    Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            if (cancelRedPackageTouchListener != null && cancelRedPackageTouchListener.containsView()) {
                cancelRedPackageTouchListener.cancelRedPackage();
            }
        }
    };

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
            animatorSet.start();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    public interface RedPackageOpenListenr {
        void openRedPackage();
    }

    public interface CancelRedPackageTouchListener {
        void cancelRedPackage();

        boolean containsView();
    }

    public CancelRedPackageTouchListener getCancelRedPackageTouchListener() {
        return cancelRedPackageTouchListener;
    }
}
