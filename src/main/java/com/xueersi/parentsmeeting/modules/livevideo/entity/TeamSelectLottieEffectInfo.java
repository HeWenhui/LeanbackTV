package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;

/**
 * 分队仪式 lottie 资源信息
 */
public class TeamSelectLottieEffectInfo extends LottieEffectInfo {

    private String logoUrl;

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public TeamSelectLottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
        updateTeamLog(bitmapId, animationView, width, height);
        return null;
    }

    /**
     * 更新战队图片
     *
     * @param bitmap_id
     * @param lavTeamSelectAnimView
     */
    private void updateTeamLog(final String bitmap_id, final LottieAnimationView lavTeamSelectAnimView, final int width, int height) {

        ImageLoader.with(lavTeamSelectAnimView.getContext()).load(logoUrl).asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap logo = DrawableHelper.drawable2bitmap(drawable);
                Bitmap resultBitmap = null;
                if (logo != null) {
                    float ratio = width / (float) logo.getWidth();
                    resultBitmap = scaleBitmap(logo, ratio);
                }
                lavTeamSelectAnimView.updateBitmap(bitmap_id, resultBitmap);
            }

            @Override
            public void onFail() {
            }
        });
    }


}
