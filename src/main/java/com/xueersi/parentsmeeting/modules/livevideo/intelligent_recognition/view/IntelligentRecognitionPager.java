package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
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

    private WaveView waveView;

    private IIntelligentRecognitionPresenter presenter;

    private LottieAnimationView lottieAnimationView;

    public IntelligentRecognitionPager(Context context) {
        super(context, false);
        this.presenter = presenter;
    }

    @Override
    public View initView() {
        return null;
    }

    public View initView(LayoutInflater inflater, ViewGroup containter) {
        mView = inflater.inflate(R.layout.activity_intelligent_recognition, containter, false);
        waveView = mView.findViewById(R.id.wv_livevideo_intelligent_recognition_energy_bar);
        delayWaveView();
        lottieAnimationView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition);
        return mView;
    }

    /**
     * 延迟初始化WaveView
     */
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
                    }
                });
    }

    /** 显示得分的Lottie动画 */
    private void showGetGold() {
        if (lottieAnimationView != null) {
            lottieAnimationView.playAnimation();
//            lottieAnimationView.
        }

    }

    @Override
    public void initData() {

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
                        showGetGold();
                    }
                });

    }

    @Override
    public void setPresenter(IIntelligentRecognitionPresenter mPresenter) {
        this.presenter = mPresenter;
    }

}
