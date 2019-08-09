package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.page;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager.SmallEnglishRedPackagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ViewUtil;
import com.xueersi.ui.widget.CircleImageView;

import net.grandcentrix.tray.core.ItemNotFoundException;

import java.util.List;
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
    private FrameLayout rlAchiveStandBg;
    private ViewGroup pkview = null;
    private ProgressBar pgAchivePk;
    private ImageView progressImageView;
    private TextView tvAchiveEnergyMy;
    private TextView tvAchiveEnergyOther;
    private CheckBox cbAchiveTitle;
    private int starCount;
    private int goldCount;
    private int energyCount;
    private int myTotal = 0;
    private int otherTotal = 0;
    private boolean firstCheck = false;

    /**用户头像*/
    CircleImageView civUserImage;
    LinearLayout llImageContent;
    String ACHIEVE_LAYOUT_RIGHT = "0";
    RelativeLayout rlAchieveContent;
    ArtsExtLiveInfo mExtLiveInfo;
    String LAYOUT_SUMMER_SIZE = "0";
    public EnStandAchievePager(Context context, RelativeLayout relativeLayout, LiveGetInfo mLiveGetInfo) {
        super(context, false);
        this.parent = relativeLayout;
        this.mLiveGetInfo = mLiveGetInfo;
        LiveGetInfo.EnPkEnergy enpkEnergy = mLiveGetInfo.getEnpkEnergy();
        starCount = mLiveGetInfo.getStarCount();
        goldCount = mLiveGetInfo.getGoldCount();
        energyCount = enpkEnergy.me;
        activity = (Activity) context;
        try {
            LAYOUT_SUMMER_SIZE =  UmsAgentTrayPreference.getInstance().getString(ShareDataConfig.SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE);
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
        }
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
        civUserImage = mView.findViewById(R.id.iv_livevideo_en_stand_achive_user_head_imge);
        llImageContent = mView.findViewById(R.id.ll_livevideo_en_stand_achive_user_head_imge);
        rlAchieveContent = mView.findViewById(R.id.rl_livevideo_en_stand_achive__content);
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
            myTotal = enpkEnergy.myTeam;
            otherTotal = enpkEnergy.opTeam;
            setEnpkView();
        } else {
            vsAchiveBottom2.inflate();
        }
        setRlAchieveContent(null);
        setUserHeadImage();
        cbAchiveTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rlAchiveStandBg.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (!firstCheck) {
                    firstCheck = true;
                    if (pgAchivePk != null) {
                        setEngPro(pgAchivePk.getProgress());
                    }
                }
//                if (com.xueersi.common.config.AppConfig.DEBUG) {
//
//                    setRlAchieveContent(null);
//                    Random random = new Random();
//                    StarAndGoldEntity starAndGoldEntity = new StarAndGoldEntity();
//                    int nextInt = random.nextInt();
//                    int goldCount2 = goldCount + 1;
//                    int energyCount2 = energyCount + 1;
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
    /**
     * 设置贡献之星
     */
    public void setRlAchieveContent(ArtsExtLiveInfo extLiveInfo){
        mExtLiveInfo = extLiveInfo;
        if (mExtLiveInfo != null ){
            LAYOUT_SUMMER_SIZE = mExtLiveInfo.getSummerCourseWareSize();
        }
        if(ACHIEVE_LAYOUT_RIGHT.equals(LAYOUT_SUMMER_SIZE)) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)rlAchieveContent.getLayoutParams();
            LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
            layoutParams.rightMargin = SizeUtils.Dp2Px(activity,10);
            layoutParams.leftMargin  = 0;
            layoutParams.topMargin = SizeUtils.Dp2Px(activity,8);
            layoutParams.width = SizeUtils.Dp2Px(activity,177);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rlAchieveContent.setLayoutParams(layoutParams);

//            RelativeLayout.LayoutParams cbParams = (RelativeLayout.LayoutParams)cbAchiveTitle.getLayoutParams();
//            cbParams.rightMargin = SizeUtils.Dp2Px(activity,13);
//            cbParams.leftMargin = SizeUtils.Dp2Px(activity,0);
//
//            cbParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
         //   cbAchiveTitle.setLayoutParams(cbParams);



            llImageContent.setVisibility(View.GONE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)rlAchieveContent.getLayoutParams();
            LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
            layoutParams.leftMargin =   videoPoint.screenWidth - videoPoint.x4+SizeUtils.Dp2Px(activity,10);

            layoutParams.topMargin = SizeUtils.Dp2Px(activity,8);
            layoutParams.width = SizeUtils.Dp2Px(activity,177);

            layoutParams.rightMargin = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            rlAchieveContent.setLayoutParams(layoutParams);

//            RelativeLayout.LayoutParams cbParams = (RelativeLayout.LayoutParams)cbAchiveTitle.getLayoutParams();
//            cbParams.leftMargin = SizeUtils.Dp2Px(activity,13);
//            cbParams.rightMargin = SizeUtils.Dp2Px(activity,0);
//
//            cbParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            cbAchiveTitle.setLayoutParams(cbParams);

            llImageContent.setVisibility(View.VISIBLE);
        }
    }

    /**设置头像*/
    private void setUserHeadImage(){
        String img = mLiveGetInfo.getStuImg();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)llImageContent.getLayoutParams();
        layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth -  LiveVideoPoint.getInstance().x4 + SizeUtils.Dp2Px(activity,10);
        layoutParams.topMargin = SizeUtils.Dp2Px(mContext,10);
        llImageContent.setLayoutParams(layoutParams);
        ImageLoader.with(activity).load(img).into(civUserImage);
    }

    private void setEnpkView() {
        pkview = (ViewGroup) vsAchiveBottom.inflate();
        pgAchivePk = pkview.findViewById(R.id.pg_livevideo_en_achive_pk);
        tvAchiveEnergyMy = pkview.findViewById(R.id.tv_livevideo_en_achive_pk_energy_my);
        tvAchiveEnergyOther = pkview.findViewById(R.id.tv_livevideo_en_achive_pk_energy_other);

        tvAchiveEnergyMy.setText("" + myTotal);
        tvAchiveEnergyOther.setText("" + otherTotal);
        int progress = 50;
        if (myTotal + otherTotal != 0) {
            progress = myTotal * 100 / (myTotal + otherTotal);
        }
        setEngPro(progress);
    }

    private void setEngPro(int progress) {
        if (pgAchivePk == null) {
            return;
        }
        pgAchivePk.setProgress(progress);
        if (progressImageView == null) {
            progressImageView = new ImageView(activity);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.pc_livevideo_enteampk_pkbar_fire_pic_nor);
            progressImageView.setImageDrawable(bitmapDrawable);
            progressImageView.setVisibility(View.INVISIBLE);
            rlAchiveStandBg.addView(progressImageView, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
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
        if (progressImageView.getWidth() == 0) {
            progressImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    progressImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] loc = ViewUtil.getLoc(pgAchivePk, rlAchiveStandBg);
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) progressImageView.getLayoutParams();
                    int leftMargin = loc[0] - progressImageView.getWidth() / 2 + pgAchivePk.getWidth() * pgAchivePk.getProgress() / pgAchivePk.getMax();
                    int topMargin = loc[1] - (progressImageView.getHeight() - pgAchivePk.getHeight()) / 2;
                    logger.d("initListener1:left=" + loc[0] + ",top=" + loc[1]);
                    if (lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                        lp.topMargin = topMargin;
                        lp.leftMargin = leftMargin;
                        progressImageView.setLayoutParams(lp);
                    }
                    return false;
                }
            });
        } else {
            int[] loc = ViewUtil.getLoc(pgAchivePk, rlAchiveStandBg);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) progressImageView.getLayoutParams();
            lp.leftMargin = loc[0] - progressImageView.getWidth() / 2 + pgAchivePk.getWidth() * pgAchivePk.getProgress() / pgAchivePk.getMax();
            lp.topMargin = loc[1] - (progressImageView.getHeight() - pgAchivePk.getHeight()) / 2;
            logger.d("initListener2:left=" + loc[0] + ",top=" + loc[1]);
            progressImageView.setLayoutParams(lp);
        }
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
        if (pkEnergy.myTeam > myTotal) {
            myTotal = pkEnergy.myTeam;
            if (tvAchiveEnergyMy != null) {
                tvAchiveEnergyMy.setText("" + myTotal);
            }
        } else {
            mLogtf.d("onGetStar:myTeam=" + pkEnergy.myTeam + ",myTotal=" + myTotal);
        }
        if (pkEnergy.opTeam > otherTotal) {
            otherTotal = pkEnergy.opTeam;
            if (tvAchiveEnergyOther != null) {
                tvAchiveEnergyOther.setText("" + otherTotal);
            }
            mLogtf.d("onGetStar:otherTotal=" + otherTotal);
        } else {
            mLogtf.d("onGetStar:opTeam=" + pkEnergy.opTeam + ",otherTotal=" + otherTotal);
        }
        if (myTotal + otherTotal != 0) {
            int progress = myTotal * 100 / (myTotal + otherTotal);
            setEngPro(progress);
        }
        final int energyCountAdd = starAndGoldEntity.getPkEnergy().me - energyCount;
        final int goldCountAdd = starAndGoldEntity.getGoldCount() - goldCount;
        energyCount = starAndGoldEntity.getPkEnergy().me;
        goldCount = starAndGoldEntity.getGoldCount();
        mLogtf.d("onGetStar:energyCountAdd=" + energyCountAdd + ",goldCountAdd=" + goldCountAdd + ",visibility=" + rlAchiveStandBg.getVisibility());
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
        lp.topMargin = cbAchiveTitle.getHeight() * 144 / 189;
        if (ACHIEVE_LAYOUT_RIGHT.equals(LAYOUT_SUMMER_SIZE)){
            lp.rightMargin = SizeUtils.Dp2Px(mContext,30);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
         } else {
            lp.leftMargin = SizeUtils.Dp2Px(mContext, 30);
         }
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

    public void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
        int myTeamTotal = enTeamPkRankEntity.getMyTeamTotal();
        if (myTeamTotal > myTotal) {
            myTotal = myTeamTotal;
            tvAchiveEnergyMy.setText("" + myTotal);
        } else {
            mLogtf.d("updateEnpk:myTeamTotal=" + myTeamTotal + ",myTotal=" + myTotal);
        }
        int opTeamTotal = enTeamPkRankEntity.getOpTeamTotal();
        if (opTeamTotal > otherTotal) {
            otherTotal = opTeamTotal;
            tvAchiveEnergyOther.setText("" + otherTotal);
            mLogtf.d("updateEnpk:otherTotal=" + otherTotal);
        } else {
            mLogtf.d("updateEnpk:opTeamTotal=" + opTeamTotal + ",otherTotal=" + otherTotal);
        }
        if (myTotal + otherTotal != 0) {
            int progress = myTotal * 100 / (myTotal + otherTotal);
            setEngPro(progress);
        }
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

    public void onEnglishPk() {
        mLogtf.d("onEnglishPk:pkview=null?" + (pkview == null));
        if (pkview == null) {
            setEnpkView();
        }
    }
}