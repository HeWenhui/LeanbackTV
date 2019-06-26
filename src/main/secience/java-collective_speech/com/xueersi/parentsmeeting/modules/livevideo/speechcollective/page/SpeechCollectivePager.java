package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.InterationVolumeWaveView;

/**
 * 集体语音互动
 */

public class SpeechCollectivePager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "feedback/";
    InterationVolumeWaveView vwvSpeectevalWave;
    LottieAnimationView waveView;
    TextView promtView;
    View promtGroup;
    View waveGroup;
    TextView countDownView;
    private View waveDisableView;

    private CountDownListener countDownListener;

    public interface CountDownListener {
        void onCountDownFinish();
    }


    public SpeechCollectivePager(Context context, CountDownListener countDownListener) {
        super(context);
        this.countDownListener = countDownListener;
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_speech_collective, null);
        vwvSpeectevalWave = (InterationVolumeWaveView) view.findViewById(R.id.vwv_livevideo_speecteval_wave);
        waveDisableView = view.findViewById(R.id.iv_livevideo_wave_disable);
        countDownView = view.findViewById(R.id.tv_livevideo_speechcollective_countdown);
        waveView = view.findViewById(R.id.iv_livevideo_feedback_wave);
        promtGroup = view.findViewById(R.id.rl_livevideo_open_close_layout);
        promtView = view.findViewById(R.id.iv_livevideo_open_close);
        waveGroup = view.findViewById(R.id.fl_livevideo_wave_layout);
        Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        countDownView.setTypeface(fontFace);
        promtView.setTypeface(fontFace);

        promtGroup.setVisibility(View.VISIBLE);
        promtView.setText("老师开启了集体发言\n踊跃参与吧！");

        return view;
    }


    public void setCountDownText(long millisUntilFinished) {
        countDownView.setText(((millisUntilFinished / 1000) + 1) + "秒后请回答");
    }

    public void setCountDownFinish() {
        if (countDownListener != null) {
            countDownListener.onCountDownFinish();
        }
        countDownView.setVisibility(View.GONE);
        waveDisableView.setVisibility(View.GONE);
        waveView.setVisibility(View.VISIBLE);
        vwvSpeectevalWave.setVisibility(View.VISIBLE);

    }


    @Override
    public void initData() {
        int colors[] = {0x19ffce65, 0x32ffce65, 0x64ffce65, 0x96ffce65, 0xffffce65};
        vwvSpeectevalWave.setColors(colors);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#a84300"));
        vwvSpeectevalWave.setCircleBack(paint);
        vwvSpeectevalWave.setOpaque(false);
        int topGap = LiveVideoPoint.getInstance().y2;
        int paddingBottom = (int) (8 * ScreenUtils.getScreenDensity());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) waveGroup.getLayoutParams();
        layoutParams.bottomMargin = paddingBottom;
        layoutParams.rightMargin = LiveVideoPoint.getInstance().getRightMargin() + SizeUtils.Dp2Px(mContext, 14);
        waveGroup.setLayoutParams(layoutParams);

        //如果是退出直播间再进来，不弹出倒计时和灰色收音球
        final boolean isOnTopic = ShareDataManager.getInstance().getBoolean("isOnTopic", false, ShareDataManager
                .SHAREDATA_USER);

        if (isOnTopic) {
            if (countDownListener != null) {
                countDownListener.onCountDownFinish();
            }
            waveDisableView.setVisibility(View.GONE);
            countDownView.setVisibility(View.GONE);
            vwvSpeectevalWave.setVisibility(View.VISIBLE);
            waveView.setVisibility(View.VISIBLE);

        } else {
            waveDisableView.setVisibility(View.VISIBLE);
            vwvSpeectevalWave.setVisibility(View.INVISIBLE);
            waveView.setVisibility(View.INVISIBLE);
        }

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeectevalWave.start();
            }
        }, 10);

        startWaveAnimation();

        promtView.postDelayed(new Runnable() {
            @Override
            public void run() {
                promtGroup.setVisibility(View.GONE);
                if (!isOnTopic) {
                    countDownView.setVisibility(View.VISIBLE);
                    countDownTimer.start();
                }
            }
        }, 3000);

    }

    /**
     * 波纹动效
     */
    private void startWaveAnimation() {
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        waveView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "wave");
        waveView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return bubbleEffectInfo.fetchBitmapFromAssets(waveView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        waveView.setRepeatCount(-1);
        waveView.setImageAssetDelegate(imageAssetDelegate);
        waveView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        waveView.playAnimation();
    }

    public void setVolume(float volume) {
        vwvSpeectevalWave.setVolume(volume);
    }


    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            setCountDownText(millisUntilFinished);

        }

        @Override
        public void onFinish() {

            setCountDownFinish();

        }
    };

    public void stop() {
        waveGroup.setVisibility(View.GONE);
        vwvSpeectevalWave.stop();
        promtGroup.setVisibility(View.VISIBLE);
        promtView.setText("老师结束了集体发言");
        countDownView.setVisibility(View.GONE);
        countDownTimer.cancel();
    }
}
