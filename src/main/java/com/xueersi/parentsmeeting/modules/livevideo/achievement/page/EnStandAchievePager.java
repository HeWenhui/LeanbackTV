package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

import java.util.Random;

public class EnStandAchievePager extends LiveBasePager {
    private RelativeLayout parent;
    private LiveGetInfo mLiveGetInfo;
    private Activity activity;
    private ViewStub vsAchiveBottom;
    private ViewStub vsAchiveBottom2;
    private TextView tvAchiveNumFire;
    private TextView tvAchiveNumStar;
    private TextView tvAchiveNumGold;
    private RelativeLayout rlAchiveStandBg;
    private ViewGroup pkview = null;
    private ProgressBar pgAchivePk;
    private ImageView progressImageView;
    private TextView tv_livevideo_en_achive_pk_energy_my;
    private TextView tv_livevideo_en_achive_pk_energy_other;
    private CheckBox cbAchiveTitle;
    private int starCount;
    private int goldCount;
    private int energyCount;

    public EnStandAchievePager(Context context, RelativeLayout relativeLayout, LiveGetInfo mLiveGetInfo) {
        super(context, false);
        this.parent = relativeLayout;
        this.mLiveGetInfo = mLiveGetInfo;
        LiveGetInfo.EnPkEnergy enpkEnergy = mLiveGetInfo.getEnpkEnergy();
        starCount = mLiveGetInfo.getStarCount();
        goldCount = mLiveGetInfo.getGoldCount();
        energyCount = enpkEnergy.me;
        activity = (Activity) context;
        initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_livevodeo_en_stand_achive, parent, false);
        tvAchiveNumFire = mView.findViewById(R.id.tv_livevideo_en_achive_num_fire);
        tvAchiveNumStar = mView.findViewById(R.id.tv_livevideo_en_achive_num_star);
        tvAchiveNumGold = mView.findViewById(R.id.tv_livevideo_en_achive_num_gold);
        vsAchiveBottom = mView.findViewById(R.id.vs_livevideo_en_achive_bottom);
        vsAchiveBottom2 = mView.findViewById(R.id.vs_livevideo_en_achive_bottom2);
        rlAchiveStandBg = mView.findViewById(R.id.rl_livevideo_en_achive_stand_bg);
        cbAchiveTitle = mView.findViewById(R.id.cb_livevideo_en_stand_achive_title);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
        LiveGetInfo.EnPkEnergy enpkEnergy = mLiveGetInfo.getEnpkEnergy();
        tvAchiveNumStar.setText("" + starCount);
        tvAchiveNumGold.setText("" + goldCount);
        tvAchiveNumFire.setText("" + enpkEnergy.me);
        LiveGetInfo.EnglishPk englishPk = mLiveGetInfo.getEnglishPk();
        if (1 == englishPk.canUsePK) {
            pkview = (ViewGroup) vsAchiveBottom.inflate();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pkview.getLayoutParams();
            layoutParams.topMargin = SizeUtils.Dp2Px(mContext, 12);
            pkview.setLayoutParams(layoutParams);
            pgAchivePk = pkview.findViewById(R.id.pg_livevideo_en_achive_pk);
            tv_livevideo_en_achive_pk_energy_my = pkview.findViewById(R.id.tv_livevideo_en_achive_pk_energy_my);
            tv_livevideo_en_achive_pk_energy_other = pkview.findViewById(R.id.tv_livevideo_en_achive_pk_energy_other);
            tv_livevideo_en_achive_pk_energy_my.setText("" + enpkEnergy.myTeam);
            tv_livevideo_en_achive_pk_energy_other.setText("" + enpkEnergy.opTeam);
            int progress = 50;
            if (enpkEnergy.myTeam + enpkEnergy.opTeam != 0) {
                progress = enpkEnergy.myTeam * 100 / (enpkEnergy.myTeam + enpkEnergy.opTeam);
            }
            setEngPro(progress);
        } else {
            vsAchiveBottom2.inflate();
        }
        cbAchiveTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rlAchiveStandBg.setVisibility(isChecked ? View.VISIBLE : View.GONE);
//                if (AppConfig.DEBUG) {
//                    Random random = new Random();
//                    StarAndGoldEntity starAndGoldEntity = new StarAndGoldEntity();
//                    int nextInt = random.nextInt();
//                    int goldCount2 = goldCount;
//                    int energyCount2 = energyCount;
//                    if (nextInt % 3 == 0) {
//                        goldCount2 += random.nextInt(20);
//                        energyCount2 += random.nextInt(20);
//                    } else if (nextInt % 3 == 1) {
//                        goldCount2 += random.nextInt(20);
//                    } else {
//                        energyCount2 += random.nextInt(20);
//                    }
//                    starAndGoldEntity.setGoldCount(goldCount2);
//                    StarAndGoldEntity.PkEnergy pkEnergy = starAndGoldEntity.getPkEnergy();
//                    pkEnergy.me = energyCount2;
//                    pkEnergy.myTeam = 20;
//                    pkEnergy.opTeam = 12;
//                    onGetStar(starAndGoldEntity);
//                }
            }
        });
    }

    private void setEngPro(int progress) {
        if (pgAchivePk == null) {
            return;
        }
        pgAchivePk.setProgress(progress);
        if (progressImageView == null) {
            progressImageView = new ImageView(activity);
            progressImageView.setImageResource(R.drawable.livevideo_enteampk_benchangchengjiu_pkfair1_img_nor);
            progressImageView.setVisibility(View.INVISIBLE);
            pkview.addView(progressImageView);
            pgAchivePk.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    pgAchivePk.getViewTreeObserver().removeOnPreDrawListener(this);
                    setLayout();
                    return false;
                }
            });
        } else {
            setLayout();
        }
    }

    private void setLayout() {
        int[] loc = ViewUtil.getLoc(pgAchivePk, pkview);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressImageView.getLayoutParams();
        lp.leftMargin = loc[0] - progressImageView.getWidth() / 2 + pgAchivePk.getWidth() * pgAchivePk.getProgress() / pgAchivePk.getMax();
        lp.topMargin = loc[1] - (progressImageView.getHeight() - pgAchivePk.getHeight()) / 2 - 18;
        logger.d("initListener:left=" + loc[0] + ",top=" + loc[1]);
        progressImageView.setLayoutParams(lp);
        progressImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        tvAchiveNumFire.setText("" + starAndGoldEntity.getPkEnergy().me);
        tvAchiveNumStar.setText("" + starAndGoldEntity.getStarCount());
        tvAchiveNumGold.setText("" + starAndGoldEntity.getGoldCount());
        StarAndGoldEntity.PkEnergy pkEnergy = starAndGoldEntity.getPkEnergy();
        tv_livevideo_en_achive_pk_energy_my.setText("" + pkEnergy.myTeam);
        tv_livevideo_en_achive_pk_energy_other.setText("" + pkEnergy.opTeam);
        if (pkEnergy.myTeam + pkEnergy.opTeam != 0) {
            int progress = pkEnergy.myTeam * 100 / (pkEnergy.myTeam + pkEnergy.opTeam);
            setEngPro(progress);
        }
        final int energyCountAdd = starAndGoldEntity.getPkEnergy().me - energyCount;
        final int goldCountAdd = starAndGoldEntity.getGoldCount() - goldCount;
        energyCount = starAndGoldEntity.getPkEnergy().me;
        goldCount = starAndGoldEntity.getGoldCount();
        logger.d("onGetStar:energyCountAdd=" + energyCountAdd + ",goldCountAdd=" + goldCountAdd);
        if (rlAchiveStandBg.getVisibility() == View.VISIBLE) {
            return;
        }
        String LOTTIE_RES_ASSETS_ROOTDIR;
        String[] targetFileNames;
        final int type;
        if (energyCountAdd > 0 && goldCountAdd > 0) {
            LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/gold_energy_stand";
            targetFileNames = new String[]{"img_0.png", "img_1.png"};
            type = 1;
        } else if (energyCountAdd > 0) {
            LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/nogold_energy_stand";
            targetFileNames = new String[]{"img_0.png"};
            type = 2;
        } else if (goldCountAdd > 0) {
            LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/gold_noenergy_stand";
            targetFileNames = new String[]{"img_0.png"};
            type = 3;
        } else {
            return;
        }
        String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
        String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath, targetFileNames) {
            @Override
            public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
                if ("img_0.png".equals(fileName)) {
                    if (type == 1) {
                        Bitmap bitmap2 = createBitmap(energyCountAdd, width, height);
                        if (bitmap2 != null) {
                            return bitmap2;
                        }
                    } else if (type == 2) {
                        Bitmap bitmap2 = createBitmap(energyCountAdd, width, height);
                        if (bitmap2 != null) {
                            return bitmap2;
                        }
                    } else {
                        Bitmap bitmap2 = createBitmap(goldCountAdd, width, height);
                        if (bitmap2 != null) {
                            return bitmap2;
                        }
                    }
                } else if ("img_1.png".equals(fileName)) {
                    Bitmap bitmap2 = createBitmap(goldCountAdd, width, height);
                    if (bitmap2 != null) {
                        return bitmap2;
                    }
                }
                return null;
            }
        };
        final LottieAnimationView lottieAnimationView = new LottieAnimationView(activity);
        lottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(activity), "fir_energy");
        lottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(lottieAnimationView, fileName,
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), activity);
                return bitmap;
            }
        };
        lottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.BELOW, cbAchiveTitle.getId());
        final ViewGroup viewGroup = (ViewGroup) mView;
        viewGroup.addView(lottieAnimationView, lp);
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewGroup.removeView(lottieAnimationView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void onStarAdd(int star, float x, float y) {
        starCount += star;
        tvAchiveNumStar.setText("" + starCount);
    }

    private Bitmap createBitmap(int energyCount, int width, int height) {
        try {
            Bitmap drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Typeface fontFace = FontCache.getTypeface(activity, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            paint.setTextSize(height + 5);
            String drawText = "+" + energyCount;
            float w = paint.measureText(drawText);
//            paint.setColor(Color.CYAN);
//            canvas.drawRect(0, 0, width, height, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(drawText, (width - w) / 2, height, paint);
            return drawBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
