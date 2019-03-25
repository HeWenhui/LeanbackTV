package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.permission.PermissionCallback;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class PhoneView extends BasePager implements GoldPhoneContract.GoldPhoneView, GoldPhoneContract.CloseTipPresenter {

    private GoldPhoneContract.GoldPhonePresenter mPresenter;

    private ImageView ivClose, ivMicroPhone;
    private TextView tvTipWindow;

    private ImageView ivSetting;

    private Group settingGroup, microhpneGroup, teacherTipGroup;
    private GoldPhoneContract.CloseTipView tipView;


    public PhoneView(Context context, PhoneBll presenter) {
        super(context, false);
        this.mPresenter = presenter;

//        this.tipPresenter = presenter;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chinese_gold_microphone, null);
        ivClose = view.findViewById(R.id.iv_livevideo_gold_microphone_cancel);
        tvTipWindow = view.findViewById(R.id.tv_gold_microphone_teacher_tip);
        ivMicroPhone = view.findViewById(R.id.iv_livevideo_gold_microphone_bg);
        ivSetting = view.findViewById(R.id.iv_livevideo_gold_microphone_setting);
        settingGroup = view.findViewById(R.id.iv_livevideo_gold_microphone_setting_group);
        microhpneGroup = view.findViewById(R.id.group_livevideo_gold_microphone_microphone_group);
        teacherTipGroup = view.findViewById(R.id.group_livevideo_gold_microphone_teacher_tip_window);

        view.postDelayed(microphoneShowRunnable, 700);

        view.postDelayed(teacherTipCloseRunnable, 1000);

        view.postDelayed(closeBtnRunnable, 20000);
        initListener();

        return view;
    }

    /**
     * 隐藏老师开启，关闭金话筒
     */
    Runnable teacherTipCloseRunnable = new Runnable() {
        @Override
        public void run() {
            if (teacherTipGroup != null && teacherTipGroup.getVisibility() != View.GONE) {
                teacherTipGroup.setVisibility(View.GONE);
            }
        }
    };
    /**
     * 显示金话筒View
     */
    Runnable microphoneShowRunnable = new Runnable() {
        @Override
        public void run() {
            if (microhpneGroup != null && microhpneGroup.getVisibility() != View.VISIBLE) {
                microhpneGroup.setVisibility(View.VISIBLE);
            }
        }
    };
    Runnable microphoneCloseRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };
    Runnable closeBtnRunnable = new Runnable() {
        @Override
        public void run() {
            if (ivClose != null && ivClose.getVisibility() != View.VISIBLE) {
                ivClose.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mView.removeCallbacks(closeBtnRunnable);

    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipView == null) {
                    tipView = new MicroPhoneCloseTipView(mContext, PhoneView.this);
                    ((ViewGroup) mView).addView(tipView.getRootView());
                }
            }
        });
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyAudioPermission();
            }
        });
    }

    private void applyAudioPermission() {
        boolean have = XesPermission.checkPermission(mContext, new PermissionCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        mPresenter.startAudioRecord();
                    }
                },
                PermissionConfig.PERMISSION_CODE_AUDIO);
    }

    @Override
    public void initData() {

    }

    @Override
    public void removeCloseView(View view) {
        if (view.getParent() == mView) {
            ((ViewGroup) mView).removeView(view);
        }
    }

    /**
     * 移除金话筒这个功能
     */
    @Override
    public void removeGoldView() {
        float curY = ivMicroPhone.getTranslationY();
        ObjectAnimator goneAnimator = ObjectAnimator.ofFloat(ivMicroPhone, "translationY", curY, -100f);
        goneAnimator.setDuration(1000);
        goneAnimator.start();
        goneAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPresenter.remove(mView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 关闭金话筒
     */
    @Override
    public void showCloseView() {
        tvTipWindow.setText("老师关闭了金话筒");
        if (teacherTipGroup != null && teacherTipGroup.getVisibility() != View.VISIBLE) {
            teacherTipGroup.setVisibility(View.VISIBLE);
        }
        removeGoldView();
    }

    @Override
    public void showSettingView(boolean isVisible) {
        settingGroup.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
