package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import java.io.IOException;

/**
 * @Date on 2019/3/15 18:22
 * @Author zhangyuansun
 * @Description 小组互动 - MVP单人模式
 */
public class GroupGameMVPPager extends LiveBasePager {
    /**
     * 主背景动画
     */
    private LottieAnimationView mLottieAnimationView;
    /**
     * 倒计时
     */
    private TimeCountDowTextView tvTime;
    /**
     * 关闭按钮
     */
    private ImageView ivClose;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "en_group_game/";
    public GroupGameMVPPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_groupgame_mvp, null);
        mLottieAnimationView = view.findViewById(R.id.lav_livevideo_groupgame_mvp);
        tvTime = view.findViewById(R.id.tv_livevideo_groupgame_mvp_time);
        ivClose = view.findViewById(R.id.iv_livevideo_groupgame_mvp_close);

        Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
        tvTime.setTypeface(fontFace);
        return view;
    }

    @Override
    public void initData() {
        tvTime.setTimeDuration(3);
        tvTime.setTimeSuffix("s");
        tvTime.startCountDow();
        startLottieAnimation();
    }

    @Override
    public void initListener() {
        tvTime.setTimeCountDowListener(new TimeCountDowTextView.TimeCountDowListener() {
            @Override
            public void onFinish() {
                ivClose.performClick();
            }

        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPagerClose.onClose(GroupGameMVPPager.this);
            }
        });
    }

    private void startLottieAnimation() {
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        mLottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext),"mvp");
        mLottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                if (lottieImageAsset.getId().equals("image_5")) {
                    return creatGoldBitmap(5);
                }
                if (lottieImageAsset.getId().equals("image_6")) {
                    return creatFireBitmap(10);
                }
                if (lottieImageAsset.getId().equals("image_10")) {
                    return creatNameBitmap("张远荪");
                }
                return bubbleEffectInfo.fetchBitmapFromAssets(
                        mLottieAnimationView,
                        lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(),
                        lottieImageAsset.getHeight(),
                        mContext);
            }
        };
        mLottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
        mLottieAnimationView.playAnimation();
    }

    /**
     * 更新金币数量图片
     * @param fireNum
     * @return
     */
    public Bitmap creatGoldBitmap(int fireNum) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(LOTTIE_RES_ASSETS_ROOTDIR + "images/img_5.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(bitmap.getHeight());
            paint.setColor(0xFFFFE376);
            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            canvas.drawText("+" + fireNum, 0, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (IOException e) {
            logger.e("creatGoldBitmap", e);
        }
        return null;
    }

    /**
     * 更新火焰数量图片
     * @param fireNum
     * @return
     */
    public Bitmap creatFireBitmap(int fireNum) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(LOTTIE_RES_ASSETS_ROOTDIR + "images/img_6.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(bitmap.getHeight());
            paint.setColor(0xFFFFE376);
            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            canvas.drawText("+" + fireNum, 0, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (IOException e) {
            logger.e("creatFireBitmap", e);
        }
        return null;
    }

    /**
     * 更新名字图片
     *
     * @param name
     * @return
     */
    public Bitmap creatNameBitmap(String name) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(LOTTIE_RES_ASSETS_ROOTDIR + "images/img_10.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(bitmap.getHeight());
            paint.setColor(0xFFFFF4EB);
            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            float width = paint.measureText(name);
            canvas.drawText(name, (bitmap.getWidth() - width) / 2, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (IOException e) {
            logger.e("creatNameBitmap", e);
        }
        return null;
    }
}
