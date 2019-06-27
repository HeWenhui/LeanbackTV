package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.unity3d.player.UnityPlayer;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionViewModel;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.ui.widget.WaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 英语智能测评Pager
 */
public class IntelligentRecognitionPager extends BasePager implements IIntelligentRecognitionView {

    private LottieAnimationView waveLottie;
    private WaveView waveView;

    private IIntelligentRecognitionPresenter presenter;

    private LottieAnimationView scoreLottieView;

    private FragmentActivity mActivity;

    private TextView tvContent;

    private LottieAnimationView readyGoLottieView;

    private UnityPlayer unityPlayer;

    public IntelligentRecognitionPager(FragmentActivity context) {
        super(context, false);
        this.mActivity = context;
        initData();
    }

    @Override
    public View initView() {
        return null;
    }

    public View initView(LayoutInflater inflater, ViewGroup containter) {
        mView = inflater.inflate(R.layout.activity_intelligent_recognition, containter, false);
        waveView = mView.findViewById(R.id.wv_livevideo_intelligent_recognition_energy_bar);
        waveLottie = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_waveview_open_start);
        tvContent = mView.findViewById(R.id.tv_livevideo_intelligent_recognition_textview);
        readyGoLottieView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_ready_go);

        scoreLottieView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_get_score);
        performOpenViewStart();
        return mView;
    }

    private void performOpenViewStart() {
        showReadyGo();
        delayWaveView();
    }

    /** 显示开场的ReadyGo动画 */
    private void showReadyGo() {
        scoreLottieView.playAnimation();
        scoreLottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scoreLottieView.setVisibility(View.GONE);
            }
        });
    }

    /** 延迟初始化WaveView */
    private void delayWaveView() {
        Observable.
                <Boolean>empty().
                delay(400, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        waveView.initialize();
                        waveView.start();
                        if(presenter!=null){

                        }
                    }
                });
    }

    /** 显示得分的Lottie动画 */
    private void showGetGold(String gold) {
        if (scoreLottieView != null) {
            scoreLottieView.playAnimation();
        }
    }

    @Override
    public void initData() {
        ViewModelProviders.
                of(mActivity).
                get(IntelligentRecognitionViewModel.class).
                getIeResultData().
                observe(mActivity, new Observer<IEResult>() {
                    @Override
                    public void onChanged(@Nullable IEResult ieResult) {
                        handleResult(ieResult);
                    }
                });
    }

    private void handleResult(IEResult ieResult) {
        String content;
        if (TextUtils.isEmpty(content = ieResult.getContent())) {
            tvContent.setText(content);
        }
        waveLottie.playAnimation();
        waveLottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (waveView.getVisibility() != View.VISIBLE) {
                    waveView.setVisibility(View.VISIBLE);
                }
            }
        });
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
        this.presenter = mPresenter;
    }

}
