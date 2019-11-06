package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
/**
*连对动效
*@author chekun
*created  at 2019/9/26 17:38
*/
public class ContiRightEffectInfo  extends LottieEffectInfo {

    private String replaceFileName;

    public ContiRightEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    public void setReplaceFileName(String replaceFileName) {
        this.replaceFileName = replaceFileName;
    }


    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        Bitmap resultBitmap = null;
        try {
            resultBitmap = BitmapFactory.decodeStream(animationView.getContext().getAssets().open(replaceFileName));
        }catch (Exception e){
           e.printStackTrace();
        }
        return resultBitmap;
    }
}
