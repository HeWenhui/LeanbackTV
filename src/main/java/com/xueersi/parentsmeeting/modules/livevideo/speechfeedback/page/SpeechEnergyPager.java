package com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.page;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page.SpeechPraisePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;

import java.util.Random;

/**
 * Created by linyuqiang on 2019/5/4.
 */
public class SpeechEnergyPager extends LiveBasePager {
    private LottieAnimationView animationView;
    private LiveSoundPool.SoundPlayTask task;
    private LiveSoundPool liveSoundPool;
    int[] startPosition = new int[2];

    public SpeechEnergyPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_speech_coll_praise, null);
        animationView = view.findViewById(R.id.iv_livevideo_speechcollective_praise);
        return view;
    }

    @Override
    public void initData() {
        animationView.useHardwareAcceleration(true);
        String lottieResPath = "speech_collec_praise/images";
        final String lottieJsonPath = "speech_collec_praise/data5.json";
        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), "teacher_praise");
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                logger.d("fetchBitmap:name=" + lottieImageAsset.getFileName() + ",id=" + lottieImageAsset.getId());
                String fileName = lottieImageAsset.getFileName();
                return effectInfo.fetchBitmapFromAssets(animationView, fileName,
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        animationView.playAnimation();
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                animationView.getLocationOnScreen(startPosition);
//                float scrcle = animationView.getHeight() / 1440f;
//                startPosition[0] = (int) (startPosition[1] + scrcle * 899.5f);
//                startPosition[1] = (int) (startPosition[1] + scrcle * 890);
                startPosition[0] = ScreenUtils.getScreenWidth() / 2;
                startPosition[1] = ScreenUtils.getScreenHeight() / 2;
                onPagerClose.onClose(SpeechEnergyPager.this);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        Random random = new Random();
        int index = random.nextInt(300) % 3;
        int raw = SpeechPraisePager.rawsPrimary[index];
        logger.d("initData:index=" + index + ",raw=" + raw);
        liveSoundPool = LiveSoundPool.createSoundPool();
        task = new LiveSoundPool.SoundPlayTask(raw, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, false);
        LiveSoundPool.play(mContext, liveSoundPool, task);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        liveSoundPool.stop(task);
        liveSoundPool.release();
    }

    public int[] getEnergyPosition() {
        return startPosition;
    }
}
