package com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.InterationVolumeWaveView;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

/**
 * 集体语音互动
 */

public class SpeechCollectivePager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "feedback/";
    InterationVolumeWaveView vwvSpeectevalWave;
    LottieAnimationView waveView;
    ImageView promtView;
    View promtGroup;
    View waveGroup;

    public SpeechCollectivePager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_speech_collective, null);
        vwvSpeectevalWave = (InterationVolumeWaveView) view.findViewById(R.id.vwv_livevideo_speecteval_wave);

        waveView = view.findViewById(R.id.iv_livevideo_feedback_wave);
        promtGroup = view.findViewById(R.id.rl_livevideo_open_close_layout);
        promtView = view.findViewById(R.id.iv_livevideo_open_close);
        waveGroup = view.findViewById(R.id.fl_livevideo_wave_layout);
        start();
        return view;
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
        promtGroup.setVisibility(View.VISIBLE);
        promtView.setImageResource(R.drawable.ic_livevideo_speech_collective_open);
        promtView.postDelayed(new Runnable() {
            @Override
            public void run() {
                promtGroup.setVisibility(View.GONE);
                waveGroup.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    public void stop() {
        waveGroup.setVisibility(View.GONE);
        vwvSpeectevalWave.stop();
        promtGroup.setVisibility(View.VISIBLE);
        promtView.setImageResource(R.drawable.ic_livevideo_speech_collective_close);
    }
}
