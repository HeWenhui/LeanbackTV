package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;
import com.xueersi.ui.widget.WaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 英语智能测评Pager
 */
public class IntelligentRecognitionPager extends BasePager implements IIntelligentRecognitionView<IIntelligentRecognitionPresenter> {

    private FragmentActivity mActivity;

    IntelligentRecognitionViewModel viewModel;

    private IIntelligentRecognitionPresenter mPresenter;

    private LottieAnimationView waveLottie;

    private WaveView waveView;

    private LottieAnimationView scoreLottieView;

    private TextView tvContent;

    private LottieAnimationView readyGoLottieView;

    private Handler handler = new Handler(Looper.getMainLooper());
    /** 使用回答时间 */
    private int answerTime;

    public IntelligentRecognitionPager(FragmentActivity context) {
        super(context, false);
        this.mActivity = context;
        initData();
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
        performOpenViewStart();
        return mView;
    }

    private void performOpenViewStart() {
//        addUnityView();
        showReadyGo();
//        delayWaveView();
//        unityInit();
    }

    private class Action implements Runnable {
        @Override
        public void run() {
            if (viewModel != null) {
                viewModel = ViewModelProviders.of(mActivity).get(IntelligentRecognitionViewModel.class);
            }
            viewModel.getIsFinish().setValue(true);
        }
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

    private boolean waveViewinit = false;

    /** 延迟初始化WaveView */
    private Observable delayWaveView() {
        return Observable.
                <Boolean>empty().
                delay(400, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread());
//                subscribe(getConsumer());
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
//                        if (mPresenter != null) {
//
//                        }
            }
        };
    }

    /** 显示得分的Lottie动画 */
    private void showGetGold(String gold) {
        if (scoreLottieView != null) {
            scoreLottieView.playAnimation();
        }
    }

    /** speech是否初始化成功 */
    private boolean isSpeechReady = true;
    /** 是否拿到后台接口返回的数据 */
    private boolean isResultGet = true;

    @Override
    public void initData() {
        viewModel = ViewModelProviders.
                of(mActivity).
                get(IntelligentRecognitionViewModel.class);

        viewModel.getIeResultData().
                observe(mActivity, new Observer<IEResult>() {
                    @Override
                    public void onChanged(@Nullable IEResult ieResult) {
                        isResultGet = true;
                        handleResult(ieResult);
                    }
                });
        viewModel.getIsSpeechReady().
                observe(mActivity, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        isSpeechReady = aBoolean;
//                        if (isSpeechReady && isResultGet) {
                        performStartWaveLottie();
//                        }
                    }
                });
        viewModel.getVolume().
                observe(mActivity, new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer integer) {
                        if (waveViewinit) {
                            waveView.setWaveAmplitude((float) (integer / 15.0));
                        }
                    }
                });
//        String sTime = viewModel.getRecordData().getAnswerTime();
//        if (TextUtils.isEmpty(sTime)) {
//            answerTime = Integer.valueOf(sTime);
//        }
//        mView.postDelayed(new Action(), answerTime);
    }

    /**
     * 显示WaveView和lottieView
     */
    private void performStartWaveLottie() {
        if (isSpeechReady && isResultGet) {
            if (waveLottie.getVisibility() != View.VISIBLE) {
                waveLottie.setVisibility(View.VISIBLE);
            }
            waveLottie.playAnimation();
            waveLottie.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    logger.i("waveLottie end");
                    super.onAnimationEnd(animation);
                    if (waveView.getVisibility() != View.VISIBLE) {
                        waveView.setVisibility(View.VISIBLE);
                    }
                    mPresenter.startSpeech();
                    Observable.
                            <Boolean>empty().
                            defer(new Callable<ObservableSource<? extends Boolean>>() {
                                @Override
                                public ObservableSource<? extends Boolean> call() throws Exception {
                                    return delayWaveView();
                                }
                            }).
                            delay(1, TimeUnit.SECONDS).
                            observeOn(AndroidSchedulers.mainThread()).
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
    public void receiveStopEvent(String goldJSON) {
        int type = 0;
        String gold = "0";
        try {
            JSONObject jsonObject = new JSONObject(goldJSON);
            type = jsonObject.optInt("type");
            gold = jsonObject.optString("gold");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalGold = gold;
        Observable.
                <Boolean>just(XESCODE.ARTS_SEND_QUESTION == type).
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
                        showGetGold(finalGold);
                    }
                });

    }

    @Override
    public void setPresenter(IIntelligentRecognitionPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

}
