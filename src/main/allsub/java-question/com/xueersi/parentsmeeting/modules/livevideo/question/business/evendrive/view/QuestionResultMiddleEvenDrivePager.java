package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.ContiRightEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.QuestionResultEvenDrivePager;

import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL1_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL2_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL3_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL4_LOTTIE_PATH;

public class QuestionResultMiddleEvenDrivePager extends QuestionResultEvenDrivePager {

    public QuestionResultMiddleEvenDrivePager(Context context, LiveGetInfo liveGetInfo) {
        super(context, liveGetInfo);
    }

    @Override
    public void showNum(int num) {
        super.showNum(num);
    }


    private void showMiddleNum(final int num) {

        String replaceFileName = ANIM_ROOT_DIR + "/public/live_business_contiright_" + num + ".png";

        String resPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "images",
                jsonPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "data.json";
        int animLevel = 0;

        if (num >= 2 && num <= 3) {//锋芒毕露
            resPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "data.json";
            animLevel = 1;
        } else if (num >= 4 && num <= 5) {
            //无人能挡
            resPath = EVEN_DRIVE_LEVEL2_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL2_LOTTIE_PATH + "data.json";
            animLevel = 2;
        } else if (num >= 6 && num <= 7) {
            //遥遥领先
            resPath = EVEN_DRIVE_LEVEL3_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL3_LOTTIE_PATH + "data.json";
            animLevel = 3;
        } else if (num >= 8 && num <= 24) {
            resPath = EVEN_DRIVE_LEVEL4_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL4_LOTTIE_PATH + "data.json";
            animLevel = 4;
        }

        String targetFileName = getTargetFileName(animLevel);
        ContiRightEffectInfo effectInfo = null;
        if (!TextUtils.isEmpty(targetFileName)) {
            effectInfo = new ContiRightEffectInfo(resPath, jsonPath, targetFileName);
        } else {
            effectInfo = new ContiRightEffectInfo(resPath, jsonPath);
        }

        effectInfo.setReplaceFileName(replaceFileName);
//        xesLottieAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
//        final LottieEffectInfo effectInfo = new LottieEffectInfo(resPath, jsonPath);
        scoreLottieView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), null);

//        scoreLottieView.setImageAssetDelegate(delegate);
        final ContiRightEffectInfo finalEffectInfo = effectInfo;
        scoreLottieView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return finalEffectInfo.fetchBitmapFromAssets(scoreLottieView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        scoreLottieView.setMaxFrame(90);
        doShowAnima();
    }

    private String getTargetFileName(int animLevel) {
        String fileName = null;
        switch (animLevel) {
            case 1:
            case 2:
                fileName = "img_46.png";
                break;
            case 3:
            case 4:
                fileName = "img_6.png";
                break;
            default:
                break;
        }
        return fileName;

    }
}
