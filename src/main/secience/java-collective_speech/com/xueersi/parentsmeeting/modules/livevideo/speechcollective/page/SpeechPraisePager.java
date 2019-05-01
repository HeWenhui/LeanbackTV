package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

/**
 * Created by linyuqiang on 2019/4/29.
 */
public class SpeechPraisePager extends BasePager {
    private LottieAnimationView animationView;

    public SpeechPraisePager(Context context) {
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
        String lottieResPath = "team_pk/pkresult/teacher_praise/images";
        String lottieJsonPath = "team_pk/pkresult/teacher_praise/data.json";
        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext),"teacher_praise");
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        animationView.playAnimation();
    }

}
