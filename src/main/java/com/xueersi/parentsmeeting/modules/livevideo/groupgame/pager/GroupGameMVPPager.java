package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

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
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "group_game_one/";

    public GroupGameMVPPager(Context context, OnPagerClose onPagerClose) {
        super(context);
        this.onPagerClose = onPagerClose;
        initData();
        initListener();
    }

    private int fireNum = 0;
    private int goldNum = 0;
    private String name = "";
    private String headUrl = "";

    public GroupGameMVPPager(Context context, OnPagerClose onPagerClose, int fireNum, int goldNum, String name, String headUrl) {
        super(context);
        this.onPagerClose = onPagerClose;
        this.fireNum = fireNum;
        this.goldNum = goldNum;
        this.name = name;
        this.headUrl = headUrl;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_groupgame_mvp, null);
        mLottieAnimationView = view.findViewById(R.id.lav_livevideo_groupgame_mvp);
        tvTime = view.findViewById(R.id.tv_livevideo_groupgame_mvp_time);
        ivClose = view.findViewById(R.id.iv_livevideo_groupgame_mvp_close);
        ImageView iv_livevideo_groupgame_mvp_bg = view.findViewById(R.id.iv_livevideo_groupgame_mvp_bg);
        {
            LiveVideoPoint instance = LiveVideoPoint.getInstance();
            int[] newWidthHeight = instance.getNewWidthHeight();
            int newWidth = newWidthHeight[0];
            int newHeight = newWidthHeight[1];
            ViewGroup.LayoutParams lp = mLottieAnimationView.getLayoutParams();
            lp.width = newWidth;
            lp.height = newHeight;
            mLottieAnimationView.setLayoutParams(lp);
            lp = iv_livevideo_groupgame_mvp_bg.getLayoutParams();
            lp.width = newWidth;
            lp.height = newHeight;
            iv_livevideo_groupgame_mvp_bg.setLayoutParams(lp);
        }
        Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
        tvTime.setTypeface(fontFace);
        return view;
    }

    @Override
    public void initData() {
        startLottieAnimation();
        tvTime.setTimeDuration(3);
        tvTime.setTimeSuffix("s");
        tvTime.startCountDow();
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
                    return creatGoldBitmap(goldNum, lottieImageAsset.getFileName());
                }
                if (lottieImageAsset.getId().equals("image_6")) {
                    return creatFireBitmap(fireNum, lottieImageAsset.getFileName());
                }
                if (lottieImageAsset.getId().equals("image_10")) {
                    return creatNameBitmap(name, lottieImageAsset.getFileName());
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
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mLottieAnimationView.playAnimation();
    }

    /**
     * 更新金币数量图片
     * @param fireNum
     * @return
     */
    public Bitmap creatGoldBitmap(int fireNum, String lottieId) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(LOTTIE_RES_ASSETS_ROOTDIR + "images/" + lottieId));
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
    public Bitmap creatFireBitmap(int fireNum, String lottieId) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(LOTTIE_RES_ASSETS_ROOTDIR + "images/" + lottieId));
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
    public Bitmap creatNameBitmap(String name, String lottieId) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(LOTTIE_RES_ASSETS_ROOTDIR + "images/" + lottieId));
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap creatBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_en_groupgame_mvp_name, null);
            TextView tvCourseMvpName = view.findViewById(R.id.tv_livevideo_course_mvp_name);
            tvCourseMvpName.setText(name);
            float size = height * 8.0f / 10.0f / ScreenUtils.getScreenDensity();
            logger.d("creatNameBitmap:size=" + size);
            tvCourseMvpName.setTextSize(size);
//            tvCourseMvpName.setTextSize(15);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthMeasureSpec, heightMeasureSpec);
            view.layout(0, 0, width, height);
            view.draw(canvas);
//            paint.setAntiAlias(true);
//            float textSize = bitmap.getHeight() * 8.5f / 10f;
//            paint.setTextSize(textSize);
//            paint.setColor(0xFFFFF4EB);
//            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
//            paint.setTypeface(fontFace);
//            float width = paint.measureText(name);
////            canvas.drawText(name, 0, bitmap.getHeight() - (bitmap.getHeight() - textSize) / 2, paint);
//            canvas.drawText(name, 0, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (IOException e) {
            logger.e("creatNameBitmap", e);
        }
        return null;
    }
}
