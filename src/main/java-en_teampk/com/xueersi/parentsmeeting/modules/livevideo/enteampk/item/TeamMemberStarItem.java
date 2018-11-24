package com.xueersi.parentsmeeting.modules.livevideo.enteampk.item;

import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.ui.adapter.AdapterItemInterface;

import java.util.HashMap;

public class TeamMemberStarItem implements AdapterItemInterface<TeamMemberEntity> {
    Logger logger = LiveLoggerFactory.getLogger("TeamMemberStarItem");
    private RelativeLayout rl_livevideo_en_teampk_member;
    private ImageView civ_livevideo_en_teampk_member;
    private TextView tv_livevideo_en_teampk_name;
    private TextView tv_livevideo_en_teampk_fire;
    private RelativeLayout rl_livevideo_en_teampk_zan;
    //    private LottieAnimationView lav_livevideo_en_teampk_zan;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/dianzan";
    private Context context;
    private TeamMemberEntity entity;
    HashMap<TeamMemberEntity, LottieAnimationView> map = new HashMap<>();
    HashMap<LottieAnimationView, TeamMemberEntity> map2 = new HashMap<>();
    int width;
    int height;

    public TeamMemberStarItem(Context context) {
        this.context = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_en_team_member_star;
    }

    @Override
    public void initViews(View root) {
        TeamMemberStarItem.this.width = 83;
        TeamMemberStarItem.this.height = 39;
        rl_livevideo_en_teampk_member = root.findViewById(R.id.rl_livevideo_en_teampk_member);
        civ_livevideo_en_teampk_member = root.findViewById(R.id.civ_livevideo_en_teampk_member);
        tv_livevideo_en_teampk_name = root.findViewById(R.id.tv_livevideo_en_teampk_name);
        tv_livevideo_en_teampk_fire = root.findViewById(R.id.tv_livevideo_en_teampk_fire);
        rl_livevideo_en_teampk_zan = root.findViewById(R.id.rl_livevideo_en_teampk_zan);
//        lav_livevideo_en_teampk_zan = root.findViewById(R.id.lav_livevideo_en_teampk_zan);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(final TeamMemberEntity entity, int position, Object objTag) {
        this.entity = entity;
        LottieAnimationView lav_livevideo_en_teampk_zan = map.get(entity);
        if (lav_livevideo_en_teampk_zan == null) {
            rl_livevideo_en_teampk_zan.removeAllViews();
            lav_livevideo_en_teampk_zan = new LottieAnimationView(context);
            map.put(entity, lav_livevideo_en_teampk_zan);
            rl_livevideo_en_teampk_zan.addView(lav_livevideo_en_teampk_zan);
            String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
            String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
            LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath, "img_0.png") {
                @Override
                public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
                    if ("img_0.png".equals(fileName)) {
                        return createBitmap(entity.praiseCount, width, height);
                    }
                    return super.fetchTargetBitMap(animationView, fileName, bitmapId, width, height);
                }
            };
            final LottieEffectInfo finalBubbleEffectInfo = bubbleEffectInfo;
            final LottieAnimationView finalLav_livevideo_en_teampk_zan = lav_livevideo_en_teampk_zan;
            ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    Bitmap bitmap = finalBubbleEffectInfo.fetchBitmapFromAssets(finalLav_livevideo_en_teampk_zan, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), context);
                    return bitmap;
                }
            };
            lav_livevideo_en_teampk_zan.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(context), "lav_livevideo_en_teampk_zan+" + entity.id);
            lav_livevideo_en_teampk_zan.setImageAssetDelegate(imageAssetDelegate);
        } else {
            logger.d("updateViews:entity=" + entity.id + ",child=" + rl_livevideo_en_teampk_zan.getChildCount());
            for (int i = 0; i < rl_livevideo_en_teampk_zan.getChildCount(); i++) {
                View child = rl_livevideo_en_teampk_zan.getChildAt(i);
                if (child != lav_livevideo_en_teampk_zan) {
                    rl_livevideo_en_teampk_zan.removeView(child);
                    i--;
                }
            }
        }
        logger.d("updateViews:entity=" + entity.id + ",code=" + lav_livevideo_en_teampk_zan.hashCode());

        if (entity.isMy) {
            rl_livevideo_en_teampk_member.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_light_bg_img_nor);
        } else {
            rl_livevideo_en_teampk_member.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_bg_img_nor);
        }
        tv_livevideo_en_teampk_name.setText(entity.name);
        tv_livevideo_en_teampk_fire.setText("" + entity.energy);
        lav_livevideo_en_teampk_zan.setOnClickListener(new PraiseClick(lav_livevideo_en_teampk_zan));
        ImageLoader.with(context.getApplicationContext()).load(entity.headurl).into(civ_livevideo_en_teampk_member);
    }

    private class PraiseClick implements View.OnClickListener {
        long before = 0;
        Handler handler = new Handler(Looper.getMainLooper());
        LottieAnimationView lav_livevideo_en_teampk_zan;
        Runnable praiseRunnable = new Runnable() {
            @Override
            public void run() {
//                logger.d("praiseRunnable");
                before = 0;
//                videoChatHttp.praise(classmateEntity.getId(), classmateEntity.getLikes());
//                classmateEntity.setLikes(0);
            }
        };
        Runnable countRunnable = new Runnable() {
            @Override
            public void run() {
//                logger.d("countRunnable");
//                tv_livevideo_chat_count.setVisibility(View.GONE);
            }
        };

        public PraiseClick(LottieAnimationView lav_livevideo_en_teampk_zan) {
            this.lav_livevideo_en_teampk_zan = lav_livevideo_en_teampk_zan;
        }

        @Override
        public void onClick(View v) {
//            if (!pressLottileView.isAnimating()) {
            lav_livevideo_en_teampk_zan.playAnimation();
            entity.praiseCount++;
            lav_livevideo_en_teampk_zan.updateBitmap("image_0", createBitmap(entity.praiseCount, width, height));
            logger.d("onClick:classmateEntity=" + entity.id + ",v1=" + lav_livevideo_en_teampk_zan.hashCode() + ",v2=" + v.hashCode());
//            }
            if (before == 0) {
                before = System.currentTimeMillis();
            }
            long time = 5000 - (System.currentTimeMillis() - before);
//            logger.d("onClick:time=" + time);
            handler.removeCallbacks(praiseRunnable);
            if (time <= 0) {
                praiseRunnable.run();
            } else {
                handler.postDelayed(praiseRunnable, time);
            }
            handler.removeCallbacks(countRunnable);
            handler.postDelayed(countRunnable, 1000);
        }
    }

    private Bitmap createBitmap(int energyCount, int width, int height) {
        try {
            Bitmap drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Typeface fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            paint.setTextSize(height + 5);
            String drawText = "" + energyCount;
            float w = paint.measureText(drawText);
//            paint.setColor(Color.CYAN);
//            canvas.drawRect(0, 0, width, height, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(drawText, (width - w) / 2, height, paint);
            return drawBitmap;
        } catch (Exception e) {
            logger.e("createBitmap", e);
        }
        return null;
    }
}
