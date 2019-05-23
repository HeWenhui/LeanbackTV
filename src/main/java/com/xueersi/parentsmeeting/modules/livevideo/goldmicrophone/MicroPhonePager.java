package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Group;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.permission.PermissionCallback;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;

import java.util.List;

public class MicroPhonePager extends BasePager implements GoldPhoneContract.GoldPhoneView, GoldPhoneContract.CloseTipPresenter {

    private GoldPhoneContract.GoldPhonePresenter mPresenter;

    private ImageView ivClose, ivMicroPhone;
    private TextView tvTipWindow;

    private ImageView ivSetting;

    private Group settingGroup, microhpneGroup, teacherTipGroup, speakLoudlyGroup;
    private GoldPhoneContract.CloseTipView closeTipView;

    private LottieAnimationView lottieAnimationView;

    private SoundWaveView swvView;

    private ConstraintLayout rootLayout;

    public MicroPhonePager(Context context, GoldMicroPhoneBll presenter) {
        super(context);
        this.mPresenter = presenter;

//        this.tipPresenter = presenter;
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.page_livevideo_chinese_gold_microphone, null);
        rootLayout = view.findViewById(R.id.layout_livevideo_gold_microphone);
        ivClose = view.findViewById(R.id.iv_livevideo_gold_microphone_cancel);
        tvTipWindow = view.findViewById(R.id.tv_gold_microphone_teacher_tip);
        ivMicroPhone = view.findViewById(R.id.iv_livevideo_gold_microphone_bg);
        ivSetting = view.findViewById(R.id.iv_livevideo_gold_microphone_setting);
        settingGroup = view.findViewById(R.id.iv_livevideo_gold_microphone_setting_group);
        microhpneGroup = view.findViewById(R.id.group_livevideo_gold_microphone_microphone_group);
        teacherTipGroup = view.findViewById(R.id.group_livevideo_gold_microphone_teacher_tip_window);
        speakLoudlyGroup = view.findViewById(R.id.group_livevideo_gold_microphone_speak_loudly);
        lottieAnimationView = view.findViewById(R.id.lottie_livevideo_gold_microphone_gold_view);
        swvView = view.findViewById(R.id.swv_livevideo_gold_microphone_sound_wave);
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
                logger.i("close tip");
                teacherTipGroup.setVisibility(View.GONE);
            }
        }
    };
    /**
     * 出场动画
     */
//    private ObjectAnimator openAnimator;
    /**
     * 显示金话筒View
     */
    Runnable microphoneShowRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            if (microhpneGroup != null && microhpneGroup.getVisibility() != View.VISIBLE) {
                microhpneGroup.setVisibility(View.VISIBLE);
//                swvView.setVisibility(View.VISIBLE);
                swvView.setVisibility(View.GONE);
            }
            ConstraintSet constraintSet = new ConstraintSet();

            constraintSet.clone(rootLayout);
            constraintSet.load(mContext, R.layout.page_livevideo_chinese_gold_microphone_start);
            Transition transition = new AutoTransition();
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    logger.i("animator start");
                    swvView.setVisibility(View.GONE);
                    swvView.setStart(false);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    logger.i("animator end");
                    swvView.setVisibility(View.VISIBLE);
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714001));
                    swvView.setStart(true);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
            TransitionManager.beginDelayedTransition(rootLayout, transition);

            constraintSet.applyTo(rootLayout);
//            if (openAnimator == null) {
//                float curY = -100;
//                float goldY = ivMicroPhone.getY();
//                openAnimator = ObjectAnimator.ofFloat(ivMicroPhone, "translationY", curY, goldY);
//                openAnimator.setDuration(1000);
//                openAnimator.start();
//            }

//            if (goneAnimator != null) {
//                goneAnimator.cancel();
//                goneAnimator.reverse();
//            }
//            ObjectAnimator showAnimator = ObjectAnimator.ofFloat(ivMicroPhone, "translationY", goldY, curY);
//            showAnimator.setDuration(1000);
//            showAnimator.start();
        }
    };
    Runnable microphoneCloseRunnable = new Runnable() {
        @Override
        public void run() {
            if (teacherTipGroup != null && teacherTipGroup.getVisibility() != View.GONE) {
                teacherTipGroup.setVisibility(View.GONE);
            }
        }
    };
    Runnable closeBtnRunnable = new Runnable() {
        @Override
        public void run() {
            if (ivClose != null && ivClose.getVisibility() != View.VISIBLE) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714004));
                ivClose.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mView.removeCallbacks(closeBtnRunnable);
//        mView.postDelayed(microphoneShowRunnable, 700);
//        mView.postDelayed(teacherTipCloseRunnable, 1000);
        mView.removeCallbacks(microphoneShowRunnable);
        mView.removeCallbacks(teacherTipCloseRunnable);
        mView.removeCallbacks(microphoneCloseRunnable);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714005));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.leftToLeft = R.id.layout_livevideo_gold_microphone;
                layoutParams.rightToRight = R.id.layout_livevideo_gold_microphone;
                layoutParams.topToTop = R.id.layout_livevideo_gold_microphone;
                layoutParams.bottomToBottom = R.id.layout_livevideo_gold_microphone;
                if (closeTipView == null) {
                    closeTipView = new MicroPhoneCloseTipPager(mContext, MicroPhonePager.this);
                    ((ViewGroup) mView).addView(closeTipView.getRootView(), layoutParams);
                } else if (closeTipView.getRootView().getParent() == null) {
                    ((ViewGroup) mView).addView(closeTipView.getRootView(), layoutParams);
                }
            }
        });
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyAudioPermission();
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714009));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isHavePermission = isHasAudioPermission();
//        if (isHavePermission) {
//            settingGroup.setVisibility(View.GONE);
//        }
        if (isHavePermission && settingGroup != null && settingGroup.getVisibility() != View.GONE) {
            settingGroup.setVisibility(View.GONE);
            mPresenter.startAudioRecord();
            isStop = false;
        }

    }

    /**
     * 是否有语音权限
     *
     * @return
     */
    private boolean isHasAudioPermission() {
        PackageManager pkm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()));
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
                        if (settingGroup != null && settingGroup.getVisibility() != View.GONE) {
                            settingGroup.setVisibility(View.GONE);
                        }
                        mPresenter.startAudioRecord();
                        isStop = false;
                    }
                },
                PermissionConfig.PERMISSION_CODE_AUDIO);
    }

    @Override
    public void initData() {

    }

    /**
     * 移除关闭提示弹窗的View
     *
     * @param view 关闭弹窗
     */
    @Override
    public void removeCloseTipView(View view) {
        if (view.getParent() == mView) {
            ((ViewGroup) mView).removeView(view);
        }
    }

    /**
     * 消失动画
     */
//    ObjectAnimator goneAnimator;
    /**
     * 当前声波是否处于活跃状态
     */
    private boolean isActive = false;
    /** 当前声波是否结束 */
    private boolean isStop = true;

    /**
     * 移除金话筒这个功能
     */
    @Override
    public void removeGoldView() {
        isStop = true;
        isActive = false;
        ivClose.setVisibility(View.GONE);
        lottieAnimationView.setVisibility(View.GONE);
        float curY, goldY;
        curY = ivMicroPhone.getTranslationY();
        goldY = curY + SizeUtils.Dp2Px(mContext,
                ivMicroPhone.getHeight() + ((ConstraintLayout.LayoutParams) ivMicroPhone.getLayoutParams()).bottomMargin);
        ObjectAnimator goneAnimator = ObjectAnimator.ofFloat(ivMicroPhone, "translationY", curY, goldY);
        logger.i("swvView set View.GONE");
        swvView.clear();

//        swvView.setVisibility(View.GONE);
        goneAnimator.setDuration(1000);
        goneAnimator.start();

        goneAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714002));
                if (!isActive) {
                    mPresenter.remove(mView);
                }
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
        if (mView != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    tvTipWindow.setText("老师关闭了金话筒");
                    logger.i("teacher close gold microphone");
                    if (teacherTipGroup != null && teacherTipGroup.getVisibility() != View.VISIBLE) {
                        teacherTipGroup.setVisibility(View.VISIBLE);
                        mView.postDelayed(microphoneCloseRunnable, 1000);
                    }
                    removeGoldView();
                }
            });
        }

    }

    @Override
    public void showSettingView(boolean isVisible) {
        settingGroup.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        isStop = isVisible;
        if (!isVisible) {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714008));
        }
    }

    @Override
    public void showSpeakLoudly() {
        speakLoudlyGroup.setVisibility(View.VISIBLE);
        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714010));
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                speakLoudlyGroup.setVisibility(View.GONE);
            }
        }, 1500);
    }


    @Override
    public void performAddView() {
        isActive = true;
        tvTipWindow.setText("老师开启了金话筒");

        teacherTipGroup.setVisibility(View.VISIBLE);

        mView.post(microphoneShowRunnable);

        mView.postDelayed(teacherTipCloseRunnable, 1000);

        mView.postDelayed(closeBtnRunnable, 20000);
    }

    @Override
    public void showLottieView() {
        if (lottieAnimationView != null) {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1714003));
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieAnimationView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    public void addRipple(int level) {
        if (swvView != null && !isStop) {
            swvView.addRipple(new SoundWaveView.Circle(0, level));
        }
    }

    @Override
    public List<SoundWaveView.Circle> getRipples() {
        return swvView.getRipples();
    }
}
