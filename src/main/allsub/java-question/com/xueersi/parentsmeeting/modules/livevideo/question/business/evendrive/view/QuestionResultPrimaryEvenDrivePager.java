package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.QuestionResultEvenDrivePager;

import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_KING_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_SHARP_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_TOP_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH;

public class QuestionResultPrimaryEvenDrivePager extends QuestionResultEvenDrivePager {
    public QuestionResultPrimaryEvenDrivePager(Context context, LiveGetInfo liveGetInfo) {
        super(context, liveGetInfo);
    }

    @Override
    public void showNum(int num) {
        super.showNum(num);

    }

    private void showPriAnimaNum(final int num) {
        String resPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "images",
                jsonPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "data.json";
        if (num >= 2 && num <= 3) {//锋芒毕露
            resPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "data.json";
        } else if (num >= 4 && num <= 5) {
            //无人能挡
            resPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "data.json";
        } else if (num >= 6 && num <= 7) {
            //遥遥领先
            resPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "data.json";
        } else if (num >= 8 && num <= 24) {
            resPath = EVEN_DRIVE_KING_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_KING_LOTTIE_PATH + "data.json";
        }

        final LottieEffectInfo effectInfo = new LottieEffectInfo(resPath, jsonPath);
        scoreLottieView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), null);
        //替换json资源文件
        ImageAssetDelegate delegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                String resPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "images",
                        jsonPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "data.json";
                if (num > 1 && num < 25) {
                    if (num >= 2 && num <= 3) {//锋芒毕露
                        resPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "data.json";
                        if (("img_8.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_SHARP_LOTTIE_PATH, "img_8.png");
                        }
                    } else if (num >= 4 && num <= 5) {
                        //无人能挡
                        resPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "data.json";
                        if (("img_3.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH, "img_3.png");
                        }
                    } else if (num >= 6 && num <= 7) {
                        //遥遥领先
                        resPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "data.json";
                        if (("img_0.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH, "img_0.png");
                        }
                    } else if (num >= 8 && num <= 24) {
                        resPath = EVEN_DRIVE_KING_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_KING_LOTTIE_PATH + "data.json";
                        if (("img_3.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_KING_LOTTIE_PATH, "img_3.png");
                        }
                    }
                } else if (num >= 25) {
                    resPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "images";
                    jsonPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "data.json";
                }
                LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
                return bubbleEffectInfo.fetchBitmapFromAssets(
                        scoreLottieView,
                        asset.getFileName(),
                        asset.getId(),
                        asset.getWidth(),
                        asset.getHeight(),
                        mContext);
            }
        };
        scoreLottieView.setImageAssetDelegate(delegate);
        doShowAnima();
    }
}
