package com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEnergyBonusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

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
    private static String LOTTIE_RES_ASSETS_ROOTDIR = "en_better_me/energy_bonus";
    private static String IMAGE_RES_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
    private static String JSON_PATH = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
    private ArrayList<TeamMemberEntity> myTeam;
    private ArrayList<TeamMemberEntity> opTeam;

    public RisingBubbleLottieEffectInfo(Context context, LottieAnimationView lottieView, BetterMeEnergyBonusEntity
            entity) {
        super(IMAGE_RES_PATH, JSON_PATH,
                "img_0.png", "img_2.png", "img_3.png", "img_4.png", "img_5.png", "img_6.png", "img_7.png",
                "img_8.png", "img_9.png", "img_10.png", "img_11.png", "img_12.png");
        this.mContext = context;
        this.mLottieView = lottieView;
        this.entity = entity;
        initData();
    }

    private void initData() {
        myTeam = entity.getMyTeamMemberList();
        opTeam = entity.getOpTeamBMemberList();
    }

    @Override
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int
            height) {
        if ("img_12.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, myTeam.get(0));
        } else if ("img_11.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, myTeam.get(1));
        } else if ("img_10.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, myTeam.get(2));
        } else if ("img_7.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, myTeam.get(3));
        } else if ("img_9.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, myTeam.get(4));
        } else if ("img_8.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, myTeam.get(5));
        } else if ("img_4.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, opTeam.get(0));
        } else if ("img_5.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, opTeam.get(1));
        } else if ("img_2.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, opTeam.get(2));
        } else if ("img_3.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, opTeam.get(3));
        } else if ("img_0.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, opTeam.get(4));
        } else if ("img_6.png".equals(fileName)) {
            return creatBitmap(bitmapId, width, height, opTeam.get(5));
        }
        return null;
    }

    private Bitmap creatBitmap(final String bitmapId, final int width, final int height, final TeamMemberEntity
            teamMemberEntity) {
        final Bitmap bitmap;
        try {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_livevideo_betterme_energybonus, null);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            final ImageView head = view.findViewById(R.id.iv_item_livevideo_betterme_energy_head);
            TextView name = view.findViewById(R.id.tv_item_livevideo_betterme_energy_name);
            TextView fire = view.findViewById(R.id.tv_item_livevideo_betterme_energy_fire);
            name.setText(teamMemberEntity.name);
            fire.setText("+" + teamMemberEntity.getEnergy());

            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthMeasureSpec, heightMeasureSpec);
            view.layout(0, 0, width, height);
            view.draw(canvas);

            ImageLoader.with(ContextManager.getContext()).load(teamMemberEntity.headurl).asCircle().asBitmap(new SingleConfig
                    .BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    head.setImageDrawable(drawable);
                    view.draw(canvas);
                    mLottieView.updateBitmap(bitmapId, bitmap);
                }

                @Override
                public void onFail() {

                }
            });
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
