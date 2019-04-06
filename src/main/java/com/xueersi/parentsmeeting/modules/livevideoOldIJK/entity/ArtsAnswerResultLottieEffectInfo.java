package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import android.graphics.Bitmap;

import com.airbnb.lottie.LottieAnimationView;

/**
 * 答题结果 Lottie 动画信息
 * @author chenkun
 * @version 1.0, 2018/8/9 下午2:15
 */

public class ArtsAnswerResultLottieEffectInfo extends LottieEffectInfo {

    public ArtsAnswerResultLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        return super.fetchTargetBitMap(animationView, fileName, bitmapId, width, height);
    }

}
