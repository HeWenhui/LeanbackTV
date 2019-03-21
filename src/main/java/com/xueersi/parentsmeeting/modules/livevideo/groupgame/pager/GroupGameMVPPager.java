package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.io.IOException;

/**
 * @Date on 2019/3/15 18:22
 * @Author zhangyuansun
 * @Description 小组互动 - MVP单人模式
 */
public class GroupGameMVPPager extends LiveBasePager {
    LottieAnimationView mLottieAnimationView;
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
        return view;
    }

    @Override
    public void initData() {
        startLottieAnimation();
    }

    @Override
    public void initListener() {

    }

    private void startLottieAnimation() {
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        mLottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext));
        mLottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
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
        updateFireAndGold(mContext, mLottieAnimationView, 10, 20);
    }

    /**
     * 更新lottie动画中的火焰和金币数量
     *
     * @param context
     * @param lottieAnimationView
     * @param fireNum
     * @param goldNum
     */
    public void updateFireAndGold(Context context, LottieAnimationView lottieAnimationView, int fireNum, int goldNum) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_groupgame_mvp_fireandgold, null);
        TextView tvFire = view.findViewById(R.id.tv_livevideo_groupgame_mvp_fire);
        tvFire.setText("" + fireNum);
        TextView tvGold = view.findViewById(R.id.tv_livevideo_groupgame_mvp_gold);
        tvGold.setText("" + goldNum);
        AssetManager manager = context.getAssets();
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(manager.open(LOTTIE_RES_ASSETS_ROOTDIR + "images/img_5.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthMeasureSpec, heightMeasureSpec);
            view.layout(0, 0, width, height);
            view.draw(canvas);

            bitmap.recycle();
            bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e("updateFireAndGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_5", bitmap);
    }
}
