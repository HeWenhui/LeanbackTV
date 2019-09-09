package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;

import com.airbnb.lottie.LottieAnimationView;

/**
 * 答题结果 Lottie 动画信息
 * @author chenkun
 * @version 1.0, 2018/8/9 下午2:15
 */

public class ArtsAnswerResultLottieEffectInfo extends LottieEffectInfo {

    private static final String VOTE_RESULT ="img_23.png";

    public ArtsAnswerResultLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        Bitmap bitmap = getBitMapFromAssets(VOTE_RESULT,animationView.getContext());
        return bitmap;
    }

    public Bitmap getBitMap(LottieAnimationView animationView){
        Bitmap bitmap = getBitMapFromAssets(VOTE_RESULT,animationView.getContext());
        return bitmap;
    }

}
