package com.xueersi.parentsmeeting.modules.livevideoOldIJK.groupgame.pager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
        this.entities = new ArrayList<>();
        this.entities.addAll(entities);
        Collections.sort(this.entities, comparator);
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

    private Comparator<TeamMemberEntity> comparator = new Comparator<TeamMemberEntity>() {
        @Override
        public int compare(TeamMemberEntity o1, TeamMemberEntity o2) {
            int com = o2.gold - o1.gold;
            if (com == 0 && (o2.isMy || o1.isMy)) {
                return -1;
            }
            return com;
        }
    };

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
        tvTime.setTimeDuration(3);
        tvTime.setTimeSuffix("s");
        tvTime.startCountDow();
        if (entities.size() == 1) {
            startLottieAnimationOne();
        } else if (entities.size() == 2) {
            startLottieAnimationTwo();
        } else if (entities.size() == 3) {
            startLottieAnimationThree();
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
                headBitHashMap.clear();
                onPagerClose.onClose(GroupGameMVPMultPager.this);
            }
        });
    }

    private int width = 129;
    private int height = 128;
    private HashMap<String, Bitmap> headBitHashMap = new HashMap<>();

    private void startLottieAnimationOne() {
        final TeamMemberEntity teamMemberEntityOne = entities.get(0);
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        mLottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "group_game_one");
        mLottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                if (lottieImageAsset.getId().equals("image_4")) {
                    return creatGoldBitmap(teamMemberEntityOne.gold, lottieImageAsset.getFileName());
                }
                if (lottieImageAsset.getId().equals("image_3")) {
                    return creatFireBitmap(teamMemberEntityOne.energy, lottieImageAsset.getFileName());
                }
                if (lottieImageAsset.getId().equals("image_8")) {
                    return creatNameBitmap(teamMemberEntityOne.name, lottieImageAsset.getFileName());
                }
                if (lottieImageAsset.getId().equals("image_0")) {
                    Bitmap headBitmap = headBitHashMap.get(lottieImageAsset.getId());
                    if (headBitmap != null) {
                        return headBitmap;
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
        setHead(teamMemberEntityOne.headurl, "image_0");
    }

    private void startLottieAnimationTwo() {
        final TeamMemberEntity teamMemberEntityOne = entities.get(1);
        final TeamMemberEntity teamMemberEntityTwo = entities.get(0);
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        mLottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "group_game_two");
        mLottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                {
                    //第一个人的
                    if (lottieImageAsset.getId().equals("image_1")) {
                        return creatGoldBitmap(teamMemberEntityOne.gold, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_2")) {
                        return creatFireBitmap(teamMemberEntityOne.energy, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_9")) {
                        return creatNameBitmap(teamMemberEntityOne.name, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_6")) {
                        Bitmap headBitmap = headBitHashMap.get(lottieImageAsset.getId());
                        if (headBitmap != null) {
                            return headBitmap;
                        }
                    }
                }
                {
                    //第二个人的
                    if (lottieImageAsset.getId().equals("image_13")) {
                        return creatGoldBitmap(teamMemberEntityTwo.gold, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_14")) {
                        return creatFireBitmap(teamMemberEntityTwo.energy, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_20")) {
                        return creatNameBitmap(teamMemberEntityTwo.name, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_18")) {
                        Bitmap headBitmap = headBitHashMap.get(lottieImageAsset.getId());
                        if (headBitmap != null) {
                            return headBitmap;
                        }
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
        setHead(teamMemberEntityOne.headurl, "image_6");
        setHead(teamMemberEntityTwo.headurl, "image_18");
    }

    private void startLottieAnimationThree() {
        final TeamMemberEntity teamMemberEntityOne = entities.get(2);
        final TeamMemberEntity teamMemberEntityTwo = entities.get(1);
        final TeamMemberEntity teamMemberEntityThree = entities.get(0);
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        mLottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "group_game_three");
        mLottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                {
                    //第一个人的
                    if (lottieImageAsset.getId().equals("image_4")) {
                        return creatGoldBitmap(teamMemberEntityOne.gold, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_3")) {
                        return creatFireBitmap(teamMemberEntityOne.energy, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_8")) {
                        return creatNameBitmap(teamMemberEntityOne.name, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_0")) {
                        Bitmap headBitmap = headBitHashMap.get(lottieImageAsset.getId());
                        if (headBitmap != null) {
                            return headBitmap;
                        }
                    }
                }
                {
                    //第二个人的
                    if (lottieImageAsset.getId().equals("image_23")) {
                        return creatGoldBitmap(teamMemberEntityTwo.gold, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_22")) {
                        return creatFireBitmap(teamMemberEntityTwo.energy, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_24")) {
                        return creatNameBitmap(teamMemberEntityTwo.name, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_19")) {
                        Bitmap headBitmap = headBitHashMap.get(lottieImageAsset.getId());
                        if (headBitmap != null) {
                            return headBitmap;
                        }
                    }
                }
                {
                    //第三个人的
                    if (lottieImageAsset.getId().equals("image_38")) {
                        return creatGoldBitmap(teamMemberEntityThree.gold, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_39")) {
                        return creatFireBitmap(teamMemberEntityThree.energy, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_40")) {
                        return creatNameBitmap(teamMemberEntityThree.name, lottieImageAsset.getFileName());
                    }
                    if (lottieImageAsset.getId().equals("image_35")) {
                        Bitmap headBitmap = headBitHashMap.get(lottieImageAsset.getId());
                        if (headBitmap != null) {
                            return headBitmap;
                        }
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
        setHead(teamMemberEntityOne.headurl, "image_0");
        setHead(teamMemberEntityTwo.headurl, "image_19");
        setHead(teamMemberEntityThree.headurl, "image_35");
    }

    private void setHead(final String headurl, final String lottieId) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing()) {
                return;
            }
        }
        ImageLoader.with(mContext).load(headurl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "startLottieAnimationOne", headurl);
                Bitmap headBitmap2 = Bitmap.createScaledBitmap(headBitmap, width, height, false);
                if (headBitmap.getWidth() != width || headBitmap.getHeight() != height) {
                    headBitmap.recycle();
                }
                Bitmap oldBitmap = mLottieAnimationView.updateBitmap(lottieId, headBitmap2);
                if (oldBitmap != null) {
                    logger.d("startLottieAnimationOne:oldBitmap.isRecycled=" + (oldBitmap.isRecycled()));
//                    oldBitmap.recycle();
                } else {
                    headBitHashMap.put(lottieId, headBitmap2);
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
     * @param lottieId
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
     *
     * @param fireNum
     * @param lottieId
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
     * @param lottieId
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
