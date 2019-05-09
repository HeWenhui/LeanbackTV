package com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;

/**
 * Created by linyuqiang on 2019/4/29.
 */
public class SpeechPraisePager extends LiveBasePager {
    private LottieAnimationView animationView;
    private static int times = 0;
    private String[] names = new String[]{"img_0.png", "img_9.png", "img_10.png"};
    /** 你太牛了,你太棒了,超级厉害 */
    private String[] datas = new String[]{"data.json", "data2.json", "data3.json"};
    private int[] rawsChild = new int[]{R.raw.speech_praise_001, R.raw.speech_praise_002, R.raw.speech_praise_003};
    private int[] rawsPrimary = new int[]{R.raw.speech_praise_004, R.raw.speech_praise_005, R.raw.speech_praise_006};
    private boolean isPrimary;
    private LiveSoundPool.SoundPlayTask task;
    private LiveSoundPool liveSoundPool;

    public SpeechPraisePager(Context context, boolean isPrimary) {
        super(context);
        this.isPrimary = isPrimary;
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
        int index = times % datas.length;
        String data = datas[index];
        String lottieJsonPath = "speech_collec_praise/" + data;
        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), "teacher_praise");
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                logger.d("fetchBitmap:name=" + lottieImageAsset.getFileName() + ",id=" + lottieImageAsset.getId());
                String fileName = lottieImageAsset.getFileName();
//                if ("img_0.png".equals(lottieImageAsset.getFileName())) {
//                    fileName = names[times % names.length];
//                }
                return effectInfo.fetchBitmapFromAssets(animationView, fileName,
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        times++;
        animationView.playAnimation();
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                onDestroy();
                onPagerClose.onClose(SpeechPraisePager.this);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        int raw;
        if (isPrimary) {
            raw = rawsPrimary[index];
        } else {
            raw = rawsChild[index];
        }
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
}
