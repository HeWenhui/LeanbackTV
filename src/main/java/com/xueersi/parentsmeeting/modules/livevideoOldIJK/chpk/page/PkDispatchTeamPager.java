package com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.business.ChinesePkBll;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 分队进行中
 *
 * @author yuanwei
 *         <p>
 *         created  at 2018/11/14 11:31
 */
public class PkDispatchTeamPager extends BasePager implements View.OnClickListener {
    private ImageView ivEnter;
    private final ChinesePkBll mTeamPkBll;
    /**
     * 呼吸动画持续时间
     */
    private static final int ANIM_DURATION = 1500;

    public PkDispatchTeamPager(Context context, ChinesePkBll pkBll) {
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chpk_dispatchteam, null);

        final LottieAnimationView lottieView = view.findViewById(R.id.lav_teampk_team_select);
        final String imagesPath = "chinesePk/team_select/team_selecting/images";
        final String configPath = "chinesePk/team_select/team_selecting/data.json";

        lottieView.setImageAssetsFolder(imagesPath);

//        lottieView.setImageAssetDelegate(new ImageAssetDelegate() {
//            @Override
//            public Bitmap fetchBitmap(LottieImageAsset asset) {
//                return getBitmapFromAsset(imagesPath, asset.getFileName());
//            }
//        });

        lottieView.setRepeatCount(ValueAnimator.INFINITE);
        String config = getStringFromAsset(configPath);
        lottieView.setAnimationFromJson(config);
        lottieView.playAnimation();


        ivEnter = view.findViewById(R.id.iv_teampk_enter_teamselect);
        ivEnter.setOnClickListener(this);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(-1);
        scaleAnimation.setDuration(ANIM_DURATION);
        ivEnter.startAnimation(scaleAnimation);


        return view;
    }


    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        mTeamPkBll.enterTeamSelectScene();
    }

    /**
     * 关闭当前页面
     */
    public void closeTeamSelectPager() {
        mTeamPkBll.closeCurrentPager();
    }

    public String getStringFromAsset(String assetPath) {

        BufferedReader reader = null;
        String result = null;

        try {
            InputStream in = mContext.getAssets().open(assetPath);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();

            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Bitmap getBitmapFromAsset(String assetDir, String asssetName) {
        Bitmap result = null;

        try {
            InputStream in = mContext.getAssets().open(assetDir + File.separator + asssetName);
            result = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
