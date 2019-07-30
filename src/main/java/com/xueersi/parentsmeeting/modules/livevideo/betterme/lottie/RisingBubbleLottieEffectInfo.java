package com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEnergyBonusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.NoPaddingTextview;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Date on 2019/7/8 16:57
 * @Author zhangyuansun
 * @Description 完成目标奖励 上升气泡
 */
public class RisingBubbleLottieEffectInfo extends LottieEffectInfo {
    private static String TAG = "RisingBubbleLottieEffectInfo";
    private Context mContext;
    private LottieAnimationView mLottieView;
    private BetterMeEnergyBonusEntity entity;
    private LogToFile logToFile;
    private Typeface fontFace;
    private Bitmap defaultBitmap;
    private static String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/rising_bubble";
    private static String IMAGE_RES_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
    private static String JSON_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
    private int myTeamSize = 0;
    private int opTeamSize = 0;
    private ArrayList<TeamMemberEntity> myTeam;
    private ArrayList<TeamMemberEntity> opTeam;

    public RisingBubbleLottieEffectInfo(Context context, LottieAnimationView lottieView, BetterMeEnergyBonusEntity
            entity) {
        super(IMAGE_RES_PATH, JSON_PATH,
                "img_0.png", "img_2.png", "img_3.png",
                "img_5.png", "img_7.png", "img_8.png",
                "img_9.png", "img_11.png", "img_12.png",
                "img_13.png", "img_15.png", "img_16.png",
                "img_17.png", "img_19.png", "img_20.png",
                "img_21.png", "img_23.png", "img_24.png",
                "img_25.png", "img_27.png", "img_28.png",
                "img_29.png", "img_31.png", "img_32.png",
                "img_33.png", "img_35.png", "img_36.png",
                "img_37.png", "img_39.png", "img_40png",
                "img_41.png", "img_43.png", "img_44.png",
                "img_45.png", "img_47.png", "img_48.png");
        this.mContext = context;
        this.mLottieView = lottieView;
        this.entity = entity;
        initData();
    }

    private void initData() {
        logToFile = new LogToFile(mContext, TAG);
        fontFace = Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf");
        defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .app_livevideo_enteampk_boy_bg_img_nor);
        myTeamSize = entity.getMyTeamMemberList().size();
        opTeamSize = entity.getOpTeamBMemberList().size();
        myTeam = entity.getMyTeamMemberList();
        opTeam = entity.getOpTeamBMemberList();
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int
            height) {
        //替换本组第i个
        for (int i = 0; i <= 5; i++) {
            if (myTeamSize > i) {
                if (("img_" + (45 - i * 4) + ".png").equals(fileName)) {
                    return creatFireBitmap(width, height, myTeam.get(i).energy);
                }
                if (("img_" + (47 - i * 4) + ".png").equals(fileName)) {
                    updateHeadBitmap(width, height, entity.getMyTeamMemberList().get(i).headurl, "image_" + (47 - i *
                            4));
                }
                if (("img_" + (48 - i * 4) + ".png").equals(fileName)) {
                    return createNameBitmap(width, height, myTeam.get(i).name);
                }
            }
        }
        //替换对方组第i个
        for (int i = 0; i <= 4; i++) {
            if (opTeamSize > i) {
                if (("img_" + (21 - i * 4) + ".png").equals(fileName)) {
                    return creatFireBitmap(width, height, opTeam.get(i).energy);
                }
                if (("img_" + (23 - i * 4) + ".png").equals(fileName)) {
                    updateHeadBitmap(width, height, opTeam.get(i).headurl, "image_" + (23 - i * 4));
                }
                if (("img_" + (24 - i * 4) + ".png").equals(fileName)) {
                    return createNameBitmap(width, height, opTeam.get(i).name);
                }
            }
        }
        //替换对方组最后一个
        if (opTeamSize > 5) {
            if ("img_0.png".equals(fileName)) {
                return creatFireBitmap(width, height, opTeam.get(5).energy);
            }
            if ("img_2.png".equals(fileName)) {
                updateHeadBitmap(width, height,  opTeam.get(5).headurl, "image_2");
            }
            if ("img_3.png".equals(fileName)) {
                return createNameBitmap(width, height,  opTeam.get(5).name);
            }
        }
        return null;
    }

    private Bitmap creatFireBitmap(int width, int height, int number) {
        Bitmap bitmap;
        try {
            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout
                    .textview_en_betterme_energy_bonus, null);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            textView.setTypeface(fontFace);
            textView.setText("+" + number);
            textView.setTextSize(height * 9.8f / 10f / ScreenUtils.getScreenDensity());

            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            textView.layout(0, 0, width, height);
            textView.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void updateHeadBitmap(final int width, final int height, final String headUrl, final String imageId) {
        ImageLoader.with(ContextManager.getContext()).load(headUrl).asCircle().asBitmap(new SingleConfig
                .BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, logToFile, "createHeadBitmap", headUrl);
                headBitmap = Bitmap.createScaledBitmap(headBitmap, width, height, true);
                mLottieView.updateBitmap(imageId, headBitmap);
            }

            @Override
            public void onFail() {
                Bitmap headBitmap = Bitmap.createScaledBitmap(defaultBitmap, width, height, true);
                mLottieView.updateBitmap(imageId, headBitmap);
            }
        });
    }

    private Bitmap createNameBitmap(int width, int height, String name) {
        Bitmap bitmap;
        try {
            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout
                    .textview_en_betterme_energy_bonus, null);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            textView.setTypeface(fontFace);
            textView.setText(name);
            textView.setTextSize(height * 9.8f / 10f / ScreenUtils.getScreenDensity());
            textView.setTextColor(Color.WHITE);

            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            textView.layout(0, 0, width, height);
            textView.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
