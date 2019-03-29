package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Date on 2019/3/15 18:22
 * @Author zhangyuansun
 * @Description 小组互动 - MVP单人模式
 */
public class GroupGameMVPMultPager extends LiveBasePager {
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
    private static String LOTTIE_RES_ASSETS_ROOTDIR = "en_group_game/";
    ArrayList<TeamMemberEntity> entities;

    public GroupGameMVPMultPager(Context context, ArrayList<TeamMemberEntity> entities) {
        super(context);
        this.entities = entities;
        if (entities.size() == 1) {
            LOTTIE_RES_ASSETS_ROOTDIR = "group_game_one/";
        } else if (entities.size() == 2) {
            LOTTIE_RES_ASSETS_ROOTDIR = "group_game_two/";
        } else if (entities.size() == 3) {
            LOTTIE_RES_ASSETS_ROOTDIR = "group_game_three/";
        }
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
        if (entities.size() == 1) {
            startLottieAnimationOne();
        } else if (entities.size() == 2) {
            startLottieAnimationOne();
        } else if (entities.size() == 3) {
            startLottieAnimationOne();
        }
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
                onPagerClose.onClose(GroupGameMVPMultPager.this);
            }
        });
    }

    int width = 129;
    int height = 128;
    Bitmap headBitmap2;

    private void startLottieAnimationOne() {
        final TeamMemberEntity teamMemberEntity = entities.get(0);
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        mLottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "group_game_one");
        mLottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                if (lottieImageAsset.getId().equals("image_5")) {
                    return creatGoldBitmap(teamMemberEntity.gold);
                }
                if (lottieImageAsset.getId().equals("image_6")) {
                    return creatFireBitmap(teamMemberEntity.energy);
                }
                if (lottieImageAsset.getId().equals("image_10")) {
                    return creatNameBitmap(teamMemberEntity.name);
                }
                if (lottieImageAsset.getId().equals("image_2")) {
                    if (headBitmap2 != null) {
                        return headBitmap2;
                    }
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
        ImageLoader.with(mContext).load(teamMemberEntity.headurl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "startLottieAnimationOne", teamMemberEntity.headurl);
                headBitmap2 = Bitmap.createScaledBitmap(headBitmap, width, height, false);
                if (headBitmap.getWidth() != width || headBitmap.getHeight() != height) {
                    headBitmap.recycle();
                }
                Bitmap oldBitmap = mLottieAnimationView.updateBitmap("image_2", headBitmap2);
                if (oldBitmap != null) {
                    logger.d("startLottieAnimationOne:oldBitmap.isRecycled=" + (oldBitmap.isRecycled()));
//                    oldBitmap.recycle();
                } else {
                    logger.d("startLottieAnimationOne:oldBitmap=null");
                }
            }

            @Override
            public void onFail() {

            }
        });
    }

    /**
     * 更新金币数量图片
     *
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
     *
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
            paint.setTextSize(SizeUtils.Dp2Px(mContext, 12));
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
