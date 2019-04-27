package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Group;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.widget.SoundWaveView;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveView;

import java.util.List;

/**
 * Created by linyuqiang on 2019/4/26.
 * 集体发言2期布局
 */
public class SpeechCollectiveNo2Pager extends LiveBasePager implements SpeechCollectiveView {
    ViewGroup mRootView;
    private ImageView ivClose;
    private SoundWaveView swvView;
    private ConstraintLayout rootLayout;
    private Group microhpneGroup;
    private LottieAnimationView lottieAnimationView;
    private TextView ivSpeechcollectiveNoVolume;

    public SpeechCollectiveNo2Pager(Context context, ViewGroup mRootView) {
        super(context, false);
        this.mRootView = mRootView;
        mView = initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_collective_speech, mRootView, false);
        rootLayout = view.findViewById(R.id.layout_livevideo_gold_microphone);
        ivClose = view.findViewById(R.id.iv_livevideo_gold_microphone_cancel);
        microhpneGroup = view.findViewById(R.id.group_livevideo_gold_microphone_microphone_group);
        swvView = view.findViewById(R.id.swv_livevideo_gold_microphone_sound_wave);
        lottieAnimationView = view.findViewById(R.id.lottie_livevideo_gold_microphone_gold_view);
        ivSpeechcollectiveNoVolume = view.findViewById(R.id.iv_livevideo_speechcollective_novolume);
        return view;
    }

    @Override
    public void initData() {

    }

    public void start() {
        handler.post(microphoneShowRunnable);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivClose.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPagerClose.onClose(SpeechCollectiveNo2Pager.this);
            }
        });
    }


    @Override
    public void onNoVolume() {
        ivSpeechcollectiveNoVolume.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivSpeechcollectiveNoVolume.setVisibility(View.GONE);
            }
        }, 2000);
    }

    private Runnable microphoneShowRunnable = new Runnable() {
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
                    swvView.setStart(true);
                    swvView.invalidate();
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

    @Override
    public void addRipple(int level) {
        swvView.addRipple(new SoundWaveView.Circle(0, level));
    }

    @Override
    public List<SoundWaveView.Circle> getRipples() {
        return swvView.getRipples();
    }
}
