package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.SpeechStopEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechScoreEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;
import com.xueersi.ui.widget.WaveView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 英语智能测评Pager
 */
class BaseIntelligentRecognitionPager extends BasePager implements IIntelligentRecognitionView<IIntelligentRecognitionPresenter> {

    protected FragmentActivity mActivity;

    IntelligentRecognitionViewModel viewModel;

    private IIntelligentRecognitionPresenter mPresenter;

    private LottieAnimationView waveLottie;

    private WaveView waveView;

    private LottieAnimationView scoreLottieView;

    private TextView tvContent;

    private LottieAnimationView readyGoLottieView;

//    private Handler handler = new Handler(Looper.getMainLooper());
    /** 使用回答时间 */
    private int answerTime;
    //结束前的提示结束提示
    private Group groupEndTip;
    /** speech是否初始化成功 */
    private boolean isSpeechReady = true;
    /** 是否拿到后台接口返回的数据 */
    private boolean isResultGet = true;
    /** waveViews是否初始化成功 */
    private boolean waveViewinit = false;

    protected ViewGroup settingViewGroup;

    public BaseIntelligentRecognitionPager(FragmentActivity context) {
        super(context, false);
        this.mActivity = context;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        return null;
    }

    public View initView(LayoutInflater inflater, ViewGroup containter, boolean attachToRoot) {
        mView = inflater.inflate(R.layout.page_livevideo_english_intelligent_recognition, containter, attachToRoot);
        waveView = mView.findViewById(R.id.wv_livevideo_intelligent_recognition_energy_bar);
        waveLottie = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_waveview_open_start);
        tvContent = mView.findViewById(R.id.tv_livevideo_intelligent_recognition_textview);
        readyGoLottieView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_ready_go);
        scoreLottieView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_get_score);
        groupEndTip = mView.findViewById(R.id.group_livevideo_intelligent_recognition_end_tip);
        settingViewGroup = mView.findViewById(R.id.layout_livevideo_intelligent_recognition_permission);
        performOpenViewStart();
        return mView;
    }

    protected void performOpenViewStart() {
        showReadyGo();
    }

    /** 显示开场的ReadyGo动画 */
    private void showReadyGo() {
        if (readyGoLottieView.getVisibility() != View.VISIBLE) {
            readyGoLottieView.setVisibility(View.VISIBLE);
        }
        readyGoLottieView.playAnimation();
        readyGoLottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                readyGoLottieView.setVisibility(View.GONE);
                logger.i("ready go end");
            }

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                logger.i("ready go start");
            }
        });
    }

    private Consumer getConsumer() {
        return new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                logger.i("waveView init");
                waveView.initialize();
                logger.i("waveView start");
                waveView.start();
                waveViewinit = true;
//                if (mPresenter != null) {
//
//                }
            }
        };
    }

    /** 显示得分的Lottie动画 */
    private void showGetGold(String gold) {
        if (scoreLottieView != null) {
            scoreLottieView.playAnimation();
        }
    }


    @Override
    public void initData() {
        viewModel = ViewModelProviders.
                of(mActivity).
                get(IntelligentRecognitionViewModel.class);

        viewModel.getIeResultData().observe(mActivity, new Observer<IEResult>() {
            @Override
            public void onChanged(@Nullable IEResult ieResult) {
                isResultGet = true;
                handleResult(ieResult);
            }
        });
        viewModel.getIsSpeechReady().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                isSpeechReady = aBoolean;
//                        if (isSpeechReady && isResultGet) {
                performStartWaveLottie();
//                        }
            }
        });
        viewModel.getVolume().observe(mActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (waveViewinit) {
                    logger.i("wave Volume:" + integer);
                    waveView.setWaveAmplitude(integer / 15.0f);
                }
            }
        });
        viewModel.getIsIntelligentSpeechFinish().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {
//                        if (waveLottie != null) {
                revertLottie(waveLottie);
//                showGetGold();

//                        }
//                        if (integer == IntelligentConstants.REPEAT_SENTENCE) {
//
//                        }
            }
        });
        viewModel.getIsSpeechJudgeFinish().observe(mActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == IntelligentConstants.PERFECT) {
                    performStartWaveLottie();
                }
            }
        });
        viewModel.getIsFinish().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (groupEndTip != null &&
                                groupEndTip.getVisibility() != View.VISIBLE) {
                            groupEndTip.setVisibility(View.VISIBLE);
                        }
                    }
                }).delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (groupEndTip.getVisibility() == View.VISIBLE) {
                            groupEndTip.setVisibility(View.GONE);
                        }
                    }
                }).delay(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mActivity.finish();
                    }
                });

            }
        });
        viewModel.getSpeechScoreData().observe(mActivity, new Observer<SpeechScoreEntity>() {
            @Override
            public void onChanged(@Nullable SpeechScoreEntity speechScoreEntity) {
                if (scoreLottieView != null) {
                    if (scoreLottieView.getVisibility() != View.VISIBLE) {
                        scoreLottieView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

//    private void positivePlayLottieView(LottieAnimationView loView) {
//        logger.i("positivePlayLottieView");
//        if (loView != null) {
//            loView.setSpeed(1.5f);
//            loView.addAnimatorListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    if (waveView != null && waveView.getVisibility() != View.VISIBLE) {
//                        waveView.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//            loView.playAnimation();
//        }
//
//    }

    /** 反转Wave动画 */
    private void revertLottie(LottieAnimationView loView) {
        logger.i("revertLottie");
        if (loView != null) {
            loView.setSpeed(-1.5f);
            loView.playAnimation();
        }
        if (waveView != null && waveView.getVisibility() != View.GONE) {
            waveView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示WaveView和lottieView
     */
    protected void performStartWaveLottie() {
        if (isSpeechReady && isResultGet) {
            if (waveLottie.getVisibility() != View.VISIBLE) {
                waveLottie.setVisibility(View.VISIBLE);
            }
            waveLottie.setSpeed(1.5f);
            waveLottie.playAnimation();
            waveLottie.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    logger.i("waveLottie end,waveView show");
                    super.onAnimationEnd(animation);
                    if (waveView.getVisibility() != View.VISIBLE) {
                        waveView.setVisibility(View.VISIBLE);
                    }
                    mPresenter.startSpeech();
                    Observable.
                            just(true).
                            delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                            subscribe(getConsumer());
                }
            });

        }
    }

    private void handleResult(IEResult ieResult) {
        String content;
        if (TextUtils.isEmpty(content = ieResult.getContent())) {
            tvContent.setText(content);
        }
        performStartWaveLottie();
    }

    /**
     * {
     * "answer": "friend",
     * "choiceType": 0,
     * "gold": "2",
     * "id": [
     * "3282277"
     * ],
     * "isAllow42": 1,
     * "isTestUseH5": "0",
     * "nonce": "a451339510194553b2198a0f99cbe300",
     * "num": 0,
     * "package_source": "2",
     * "ptype": 30,
     * "refresh": false,
     * "time": "3",
     * "type": "1104"
     * }
     *
     * @param goldJSON
     */
    @Override
    public void receiveStopEvent(final SpeechStopEntity stopEntity) {

        Observable.
                <Boolean>just(XESCODE.ARTS_SEND_QUESTION == stopEntity.getType()).
                filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).
                subscribeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        showGetGold(stopEntity.getGoldNum() + "");
                    }
                });

    }

    @Override
    public void setPresenter(IIntelligentRecognitionPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

}
