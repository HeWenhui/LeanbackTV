package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

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
        updateTeamLog(bitmapId,animationView);
        return null;
    }

    /**
     * 更新战队图片
     * @param bitmap_id
     * @param lavTeamSelectAnimView
     */
    private void updateTeamLog(final String  bitmap_id, final LottieAnimationView lavTeamSelectAnimView) {

        ImageLoader.with(lavTeamSelectAnimView.getContext()).load(logoUrl).asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap logo = ((BitmapDrawable)drawable).getBitmap();
                lavTeamSelectAnimView.updateBitmap(bitmap_id,logo);
            }
            @Override
            public void onFail() {
            }
        });
    }





}
