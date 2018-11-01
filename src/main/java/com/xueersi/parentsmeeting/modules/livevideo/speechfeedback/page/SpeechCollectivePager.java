package com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

/**
 * 集体语音互动
 */

public class SpeechCollectivePager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "feedback/";
    VolumeWaveView vwvSpeectevalWave;
    LottieAnimationView waveView;
    ImageView promtView;

    public SpeechCollectivePager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_speech_collective, null);
        vwvSpeectevalWave = (VolumeWaveView) view.findViewById(R.id.vwv_livevideo_speecteval_wave);
        waveView = view.findViewById(R.id.iv_livevideo_feedback_wave);
        promtView = view.findViewById(R.id.iv_livevideo_open_close);
        start();
        return view;
    }

    @Override
    public void initData() {
        int colors[] = {0x19ffe4aa, 0x32ffe4aa, 0x64ffe4aa, 0x96ffe4aa, 0xFFffe4aa};
        vwvSpeectevalWave.setColors(colors);
//        vwvSpeectevalWave.setBackColor(Color.TRANSPARENT);
        vwvSpeectevalWave.setIsOnTop(true);
        vwvSpeectevalWave.setZOrderMediaOverlay(true);
        vwvSpeectevalWave.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        vwvSpeectevalWave.setBackColor(Color.parseColor("#a84300"));
        int width = SizeUtils.Dp2Px(mContext, 85);
        int waveWidth = (int) (SizeUtils.Dp2Px(mContext, 70) * 21.5f / 57f);
        int waveHeight = (int) (SizeUtils.Dp2Px(mContext, 70) * 20f / 57f);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vwvSpeectevalWave.getLayoutParams();
        layoutParams.width = waveWidth;
        layoutParams.height = waveHeight;
        int margin = (width - waveWidth) / 2;
        layoutParams.leftMargin = margin + 3;
        layoutParams.topMargin = margin + 5;
        vwvSpeectevalWave.setLayoutParams(layoutParams);

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeectevalWave.start();
            }
        }, 10);

        startWaveAnimation();

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

    public void start() {
        promtView.setVisibility(View.VISIBLE);
        promtView.setImageResource(R.drawable.ic_livevideo_speech_collective_open);
        promtView.postDelayed(new Runnable() {
            @Override
            public void run() {
                promtView.setVisibility(View.GONE);
            }
        }, 1000);

    }

    public void stop() {
        promtView.setVisibility(View.VISIBLE);
        promtView.setImageResource(R.drawable.ic_livevideo_speech_collective_close);
    }
}