package com.xueersi.parentsmeeting.modules.livevideo.englishname.lottie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.SubGroupEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.SubMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;

import java.io.IOException;

public class EnglishNameLottieView extends LottieEffectInfo {

    private String TAG = "EnglishNameLottieView";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private LogToFile logToFile;

    public EnglishNameLottieView(String imgDir, String jsonFilePath, String... targetFileNames) {
        super(imgDir, jsonFilePath, targetFileNames);
    }

    public void setmSubGroupEntity(SubGroupEntity mSubGroupEntity) {

    }


    public void updateMyHeadUrl() {
        Bitmap headBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
      //  updateHead(headBitmap,"english_name/images/img_0.png","img_0");
//        ImageLoader.with(ContextManager.getContext()).load(R.drawable).asBitmap(new SingleConfig
//                .BitmapListener() {
//            @Override
//            public void onSuccess(Drawable drawable) {
//
//                Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, logToFile, "updateHeadUrl", headUrl);
//                if (headBitmap != null) {
//                   // updateHead(headBitmap,"english_name/images/img_0.png","img_0");
//                }
//
//            }
//
//            @Override
//            public void onFail() {
//                logger.e("onFail");
//            }
//        });
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width,
                                    int height) {
        Bitmap bitmap = getBitMapFromAssets("",animationView.getContext());
        return null;
    }


}
