package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.lottie.AchieveType1LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.lottie.AchieveType2LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.lottie.AchieveType3LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.lottie.BubbleLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.config.EnglishPk;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

public class EnAchievePager extends LiveBasePager {
    private RelativeLayout parent;
    private LiveGetInfo mLiveGetInfo;
    private CheckBox cbAchiveTitle;
    private RelativeLayout rlAchiveBack;
    private RelativeLayout rlAchiveContent;
    private ViewStub vsAchiveBottom;
    private ViewStub vsAchiveBottom2;
    private RelativeLayout pkView;
    private ViewGroup pkEmptyView;
    private ProgressBar pgAchivePk;
    private FrameLayout flProgress;
    private ImageView progressImageView;
    private Activity activity;
    private TextView tvAchiveNumStar;
    private TextView tvAchiveNumGold;
    private TextView tvAchiveNumFire;
    private TextView tvPkEnergyMy;
    private TextView tvPkEnergyOther;
    private int starCount;
    private int goldCount;
    private int energyCount;
    private int myTotal = 0;
    private int otherTotal = 0;

    /**
     * 小目标控件
     */
    private TextView tvAchiveAimEmpty;
    private RelativeLayout rlAchiveAimContent;
    private TextView tvAchiveAimType;
    private TextView tvAchiveAimValue;
    private ProgressBar pgAchiveAim;
    private TextView tvAchiveAimTips;
    private int WIDTH_PROGRESS_BAR_AIM_VIEWGROUP = 165;
    private int WIDTH_SOLID_PROGRESS_BAR_AIM = 137;
    private int currentProgress = 0;
    private String curentValue;

    public EnAchievePager(Context context, RelativeLayout relativeLayout, LiveGetInfo mLiveGetInfo) {
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
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_livevodeo_en_achive, parent, false);
        cbAchiveTitle = mView.findViewById(R.id.cb_livevideo_en_achive_title);
        rlAchiveBack = mView.findViewById(R.id.rl_livevideo_en_achive_back);
        rlAchiveContent = mView.findViewById(R.id.rl_livevideo_en_achive_content);
        vsAchiveBottom = mView.findViewById(R.id.vs_livevideo_en_achive_bottom);
        vsAchiveBottom2 = mView.findViewById(R.id.vs_livevideo_en_achive_bottom2);
        tvAchiveNumStar = mView.findViewById(R.id.tv_livevideo_en_achive_num_star);
        tvAchiveNumGold = mView.findViewById(R.id.tv_livevideo_en_achive_num_gold);
        tvAchiveNumFire = mView.findViewById(R.id.tv_livevideo_en_achive_num_fire);

        /**
         * 小目标控件绑定
         */
        tvAchiveAimEmpty = mView.findViewById(R.id.tv_livevideo_en_achive_aim_empty);
        rlAchiveAimContent = mView.findViewById(R.id.rl_livevideo_en_achive_aim_content);
        tvAchiveAimType = mView.findViewById(R.id.tv_livevideo_en_achive_aimtype);
        tvAchiveAimValue = mView.findViewById(R.id.tv_livevideo_en_achive_aimvalue);
        tvAchiveAimTips = mView.findViewById(R.id.tv_livevideo_en_achive_aimtips);
        pgAchiveAim = mView.findViewById(R.id.pg_livevideo_en_achive_aim);
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
        try {
            mLogtf.d("initData:canUsePK=" + englishPk.canUsePK + ",hasGroup=" + englishPk.hasGroup);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        if (1 == englishPk.canUsePK && EnglishPk.HAS_GROUP_MAIN == englishPk.hasGroup) {
            showPk();
        } else {
            pkEmptyView = (ViewGroup) vsAchiveBottom2.inflate();
            ImageView tv_livevideo_en_achive_pk_energy_my_lable = mView.findViewById(R.id.tv_livevideo_en_achive_pk_energy_my_lable);
            if (1 == englishPk.canUsePK) {
                tv_livevideo_en_achive_pk_energy_my_lable.setImageResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_zhunbeizuozhan_title_pic_nor);
            } else {
                tv_livevideo_en_achive_pk_energy_my_lable.setImageResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_zhanduiweifenpei_title_pic_nor);
            }
        }
        //默认展开才需要
//        if (view != null) {
//            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//            lp.topMargin = (int) (73 * ScreenUtils.getScreenDensity());
//            view.setLayoutParams(lp);
//        }
        LiveGetInfo.BetterMe betterMe = mLiveGetInfo.getBetterMe();
        if (betterMe.isUseBetterMe() && betterMe.isArriveLate()) {
            onBetterMeLate();
        }
        if (betterMe.isUseBetterMe() && !betterMe.isArriveLate()) {
            if (betterMe.getTarget() != null) {
                onReceiveBetterMe(betterMe.getTarget(), false);
            }
            if (betterMe.getCurrent() != null) {
                onBetterMeUpdate(betterMe.getCurrent(), false);
            }
        }
    }

    public void onEnglishPk() {
        mLogtf.d("onEnglishPk:pkEmptyView=" + (pkEmptyView == null));
        if (pkEmptyView != null) {
            pkEmptyView.removeAllViews();
            ViewGroup group = (ViewGroup) pkEmptyView.getParent();
            if (group != null) {
                group.removeView(pkEmptyView);
            }
        }
        showPk();
    }

    private void showPk() {
        LiveGetInfo.EnPkEnergy enpkEnergy = mLiveGetInfo.getEnpkEnergy();
        ViewParent viewParent = vsAchiveBottom.getParent();
        if (viewParent == null) {
            mLogtf.d("showPk:pkView=null?" + (pkView == null));
            return;
        }
        View view = vsAchiveBottom.inflate();
        if (view == null) {
            return;
        }
        pkView = (RelativeLayout) view;
        pgAchivePk = pkView.findViewById(R.id.pg_livevideo_en_achive_pk);
        myTotal = enpkEnergy.myTeam;
        otherTotal = enpkEnergy.opTeam;
        int progress = 50;
        if (myTotal + otherTotal != 0) {
            progress = (int) ((float) myTotal * 100 / (float) (myTotal + otherTotal));
        }
        setEngPro(progress);
        tvPkEnergyMy = pkView.findViewById(R.id.tv_livevideo_en_achive_pk_energy_my);
        tvPkEnergyMy.setText("" + myTotal);
        tvPkEnergyOther = pkView.findViewById(R.id.tv_livevideo_en_achive_pk_energy_other);
        tvPkEnergyOther.setText("" + otherTotal);
    }

    public void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
        //本场成就设置进度
        int myTeamTotal = enTeamPkRankEntity.getMyTeamTotal();
        if (myTeamTotal > myTotal) {
            myTotal = myTeamTotal;
            if (tvPkEnergyMy != null) {
                tvPkEnergyMy.setText("" + myTeamTotal);
            }
        } else {
            mLogtf.d("updateEnpk:myTeamTotal=" + myTeamTotal + ",otherTotal=" + myTotal);
        }
        int opTeamTotal = enTeamPkRankEntity.getOpTeamTotal();
        if (opTeamTotal > otherTotal) {
            otherTotal = opTeamTotal;
            if (tvPkEnergyOther != null) {
                tvPkEnergyOther.setText("" + opTeamTotal);
            }
        } else {
            mLogtf.d("updateEnpk:opTeamTotal=" + opTeamTotal + ",otherTotal=" + otherTotal);
        }
        if (myTotal + otherTotal != 0) {
            int progress = (int) ((float) myTotal * 100 / (float) (myTotal + otherTotal));
            setEngPro(progress);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        cbAchiveTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rlAchiveContent.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                View view = activity.findViewById(R.id.iv_livevideo_message_small_bg);
//                {
//                    logger.d("onCheckedChanged:isChecked=" + isChecked + ",height=" + mView.getHeight());
//                    ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
//                    if (isChecked) {
//                        layoutParams.height = SizeUtils.Dp2Px(mContext, 133);
//                    } else {
//                        layoutParams.height = SizeUtils.Dp2Px(mContext, 60);
//                    }
//                    mView.setLayoutParams(layoutParams);
//                }
//                if (pkView != null) {
//                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pkView.getLayoutParams();
//                    if (isChecked) {
//                        layoutParams.topMargin = SizeUtils.Dp2Px(mContext, 106);
//                    } else {
//                        layoutParams.topMargin = SizeUtils.Dp2Px(mContext, 33);
//                    }
//                    pkView.setLayoutParams(layoutParams);
//                }
                if (isChecked) {
                    if (view != null) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        lp.topMargin = (int) (73 * ScreenUtils.getScreenDensity());
                        LayoutParamsUtil.setViewLayoutParams(view, lp);
                    }
                    rlAchiveBack.setBackgroundResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_bg1_img_nor);
                } else {
                    if (view != null) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        lp.topMargin = 0;
                        LayoutParamsUtil.setViewLayoutParams(view, lp);
                    }
                    rlAchiveBack.setBackgroundResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_bg_img_nor);
                }
                if (pgAchivePk != null) {
                    pgAchivePk.post(new Runnable() {
                        @Override
                        public void run() {
                            setEngPro(pgAchivePk.getProgress());
                        }
                    });
                }
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
//                    starAndGoldEntity.getPkEnergy().me = energyCount2;
//                    onGetStar(starAndGoldEntity);
//                }
            }
        });
    }

    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        tvAchiveNumStar.setText("" + starAndGoldEntity.getStarCount());
        tvAchiveNumGold.setText("" + starAndGoldEntity.getGoldCount());
        StarAndGoldEntity.PkEnergy pkEnergy = starAndGoldEntity.getPkEnergy();
        tvAchiveNumFire.setText("" + pkEnergy.me);
        //本场成就设置进度
        if (pkEnergy.myTeam > myTotal) {
            myTotal = pkEnergy.myTeam;
            if (tvPkEnergyMy != null) {
                tvPkEnergyMy.setText("" + myTotal);
            }
        } else {
            mLogtf.d("onGetStar:myTeam=" + pkEnergy.myTeam + ",myTotal=" + myTotal);
        }
        if (pkEnergy.opTeam > otherTotal) {
            otherTotal = pkEnergy.opTeam;
            if (tvPkEnergyOther != null) {
                tvPkEnergyOther.setText("" + otherTotal);
            }
        } else {
            mLogtf.d("onGetStar:opTeam=" + pkEnergy.opTeam + ",otherTotal=" + myTotal);
        }
        ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        if (rl_livevideo_info != null && !cbAchiveTitle.isChecked()) {
            final int energyCountAdd = starAndGoldEntity.getPkEnergy().me - energyCount;
            final int goldCountAdd = starAndGoldEntity.getGoldCount() - goldCount;
            final int startCountAdd = starAndGoldEntity.getStarCount() - starCount;
            mLogtf.d("onGetStar:energyCountAdd=" + energyCountAdd + ",goldCountAdd=" + goldCountAdd + ",startCountAdd=" + startCountAdd);
            String LOTTIE_RES_ASSETS_ROOTDIR;
            String bubbleResPath;
            String bubbleJsonPath;
            final LottieEffectInfo bubbleEffectInfo;
            if (energyCountAdd > 0 && goldCountAdd > 0) {
                LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/gold_energy";
                bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
                bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
                bubbleEffectInfo = new AchieveType1LottieEffectInfo(activity, energyCountAdd, goldCountAdd, bubbleResPath, bubbleJsonPath);
            } else if (energyCountAdd > 0) {
                LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/nogold_energy";
                bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
                bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
                bubbleEffectInfo = new AchieveType2LottieEffectInfo(activity, energyCountAdd, bubbleResPath, bubbleJsonPath);
            } else if (goldCountAdd > 0) {
                LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/gold_noenergy";
                bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "/images";
                bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "/data.json";
                bubbleEffectInfo = new AchieveType3LottieEffectInfo(activity, goldCountAdd, bubbleResPath, bubbleJsonPath);
            } else {
                return;
            }
            final ViewGroup viewGroup = (ViewGroup) rl_livevideo_info.getParent();
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
            lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.rl_livevideo_info);
            lp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_livevideo_info);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.bottomMargin = ScreenUtils.getScreenHeight() - rl_livevideo_info.getTop();
            logger.d("onGetStar:bottomMargin=" + lp.bottomMargin);
            viewGroup.addView(lottieAnimationView, lp);
            lottieAnimationView.playAnimation();
            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    logger.d("onAnimationStart");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    logger.d("onAnimationEnd");
                    viewGroup.removeView(lottieAnimationView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            if (pkEnergy.myTeam > myTotal) {
                myTotal = pkEnergy.myTeam;
                if (tvPkEnergyMy != null) {
                    tvPkEnergyMy.setText("" + myTotal);
                }
            }
            if (pkEnergy.opTeam > otherTotal) {
                otherTotal = pkEnergy.opTeam;
                if (tvPkEnergyOther != null) {
                    tvPkEnergyOther.setText("" + otherTotal);
                }
            }
            if (myTotal + otherTotal != 0) {
                int progress = (int) ((float) myTotal * 100 / (float) (myTotal + otherTotal));
                setEngPro(progress);
            }
        } else {
            tvAchiveNumFire.setText("" + starAndGoldEntity.getPkEnergy().me);
            tvAchiveNumGold.setText("" + starAndGoldEntity.getGoldCount());
        }
        energyCount = starAndGoldEntity.getPkEnergy().me;
        goldCount = starAndGoldEntity.getGoldCount();
        starCount = starAndGoldEntity.getStarCount();
    }

    public void onStarAdd(int star, float x, float y) {
        starCount += star;
        tvAchiveNumStar.setText("" + starCount);
    }

    /**
     * 收到本场小目标
     */
    public void onReceiveBetterMe(BetterMeEntity betterMeEntity, boolean isShowBubble) {
        onBetterMeLayoutChange();
        //隐藏没有小目标时的默认视图
        if (tvAchiveAimEmpty != null) {
            tvAchiveAimEmpty.setVisibility(View.GONE);
        }
        //显示小目标的内容
        if (rlAchiveAimContent != null) {
            rlAchiveAimContent.setVisibility(View.VISIBLE);
        }
        String current = "0%";
        String target = betterMeEntity.getAimValue();
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(betterMeEntity.getAimType())) {
            tvAchiveAimType.setText(BetterMeConfig.CORRECTRATE);
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(betterMeEntity.getAimType())) {
            tvAchiveAimType.setText(BetterMeConfig.PARTICIPATERATE);
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(betterMeEntity.getAimType())) {
            tvAchiveAimType.setText(BetterMeConfig.TALKTIME);
            target = BetterMeUtil.secondToMinite(target);
            current = "0:00";
        }
        tvAchiveAimValue.setText("目标" + target);
        tvAchiveAimTips.setText(current);
        setBetterMePro(0);
        if (isShowBubble) {
            receiveBetterMeBubble(betterMeEntity);
        }
        curentValue = "0";
    }

    /**
     * 更新本场小目标
     */
    public void onBetterMeUpdate(AimRealTimeValEntity aimRealTimeValEntity, boolean isShowBubble) {
        onBetterMeLayoutChange();
        //隐藏没有小目标时的默认视图
        if (tvAchiveAimEmpty != null) {
            tvAchiveAimEmpty.setVisibility(View.GONE);
        }
        //显示小目标的内容
        if (rlAchiveAimContent != null) {
            rlAchiveAimContent.setVisibility(View.VISIBLE);
        }
        String current = aimRealTimeValEntity.getRealTimeVal();
        String target = aimRealTimeValEntity.getAimValue();
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(aimRealTimeValEntity.getType())) {
            tvAchiveAimType.setText(BetterMeConfig.CORRECTRATE);
            current = Math.round(Double.valueOf(current) * 100) + "%";
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(aimRealTimeValEntity.getType())) {
            tvAchiveAimType.setText(BetterMeConfig.PARTICIPATERATE);
            current = Math.round(Double.valueOf(current) * 100) + "%";
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(aimRealTimeValEntity.getType())) {
            tvAchiveAimType.setText(BetterMeConfig.TALKTIME);
            current = BetterMeUtil.secondToMinite(current);
            target = BetterMeUtil.secondToMinite(target);
        }
        if (aimRealTimeValEntity.isDoneAim()) {
            tvAchiveAimValue.setText("已完成目标");
            pgAchiveAim.setProgressDrawable(mContext.getResources().getDrawable(R.drawable
                    .app_livevideo_enteampk_xiaomubiao_progressbar_finish));

        } else {
            tvAchiveAimValue.setText("目标" + target);
            pgAchiveAim.setProgressDrawable(mContext.getResources().getDrawable(R.drawable
                    .layerlst_livevideo_achieve_betterme_prog));
        }
        tvAchiveAimTips.setText(current);
        float realTimeVal = Float.parseFloat(aimRealTimeValEntity.getRealTimeVal());
        float aimVal = Float.parseFloat(aimRealTimeValEntity.getAimValue());
        int progress = (int) (realTimeVal / aimVal * 100);
        setBetterMePro(progress);
        if (isShowBubble) {
            updateBetterMeBubble(aimRealTimeValEntity);
        }
        this.curentValue = aimRealTimeValEntity.getRealTimeVal();
    }

    /**
     * 小目标迟到
     */
    private void onBetterMeLate() {
        if (tvAchiveAimEmpty != null) {
            tvAchiveAimEmpty.setText("早点来上课才有小目标哦~");
        }
    }

    /**
     * 设置小目标进度
     */
    private void setBetterMePro(int progress) {
        logger.i("setBetterMePro : progress = " + progress);
        if (progress > 100) {
            progress = 100;
        }
        currentProgress = progress;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvAchiveAimTips.getLayoutParams();
        layoutParams.leftMargin = progress * SizeUtils.Dp2Px(mContext, WIDTH_SOLID_PROGRESS_BAR_AIM) / 100;
        tvAchiveAimTips.setLayoutParams(layoutParams);

        if (progress == 0) {
            pgAchiveAim.setProgress(0);
        } else if (progress == 100) {
            pgAchiveAim.setProgress(100);
        } else {
            int a = 6 * 100 / (6 + WIDTH_SOLID_PROGRESS_BAR_AIM);
            int b = progress * WIDTH_SOLID_PROGRESS_BAR_AIM / (6 + WIDTH_SOLID_PROGRESS_BAR_AIM);
            pgAchiveAim.setProgress(a + b);
        }
    }

    /**
     * 本场小目标气泡
     */
    private void receiveBetterMeBubble(BetterMeEntity betterMeEntity) {
        StringBuilder message = new StringBuilder("本场目标：");
        String target = betterMeEntity.getAimValue();
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(betterMeEntity.getAimType())) {
            message.append(BetterMeConfig.CORRECTRATE);
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(betterMeEntity.getAimType())) {
            message.append(BetterMeConfig.PARTICIPATERATE);
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(betterMeEntity.getAimType())) {
            message.append(BetterMeConfig.TALKTIME);
            target = BetterMeUtil.secondToMinite(target);
        }
        message.append("达到").append(target);
        showBetterMeBubble(null, message.toString(), false, false);
    }

    /**
     * 更新小目标气泡
     */
    private void updateBetterMeBubble(AimRealTimeValEntity aimRealTimeValEntity) {
        if (cbAchiveTitle.isChecked()) {
            return;
        }
        StringBuilder message = new StringBuilder();
        String current = aimRealTimeValEntity.getRealTimeVal();
        String target = aimRealTimeValEntity.getAimValue();

        //当前完成率是上升还是下降
        boolean isIncrease = false;
        boolean isDecrease = false;
        if (curentValue != null) {
            double doubleCurrent = (Double.valueOf(current));
            double doublePrevious = (Double.valueOf(curentValue));
            isIncrease = doubleCurrent > doublePrevious;
            isDecrease = doubleCurrent < doublePrevious;
        }
        if (BetterMeConfig.TYPE_CORRECTRATE.equals(aimRealTimeValEntity.getType())) {
            message.append(BetterMeConfig.CORRECTRATE);
            current = Math.round(Double.valueOf(current) * 100) + "%";
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_PARTICIPATERATE.equals(aimRealTimeValEntity.getType())) {
            message.append(BetterMeConfig.PARTICIPATERATE);
            current = Math.round(Double.valueOf(current) * 100) + "%";
            target = Math.round(Double.valueOf(target) * 100) + "%";
        } else if (BetterMeConfig.TYPE_TALKTIME.equals(aimRealTimeValEntity.getType())) {
            message.append(BetterMeConfig.TALKTIME);
            current = BetterMeUtil.secondToMinite(current);
            target = BetterMeUtil.secondToMinite(target);
        }
        if (aimRealTimeValEntity.isDoneAim()) {
            target = "目标已完成";
        } else {
            target = "目标" + target;
        }
        message.append("当前").append(current);
        if (isIncrease || isDecrease) {
            showBetterMeBubble(message.toString(), target, isIncrease, isDecrease);
        }
    }

    /**
     * 蓝色气泡动效
     */
    private void showBetterMeBubble(String current, String target, boolean isIncrease, boolean isDecrease) {
        ViewGroup rlLivevideoInfo = ((Activity) mContext).findViewById(R.id.rl_livevideo_info);
        if (rlLivevideoInfo != null) {
            ViewGroup viewGroup = (ViewGroup) rlLivevideoInfo.getParent();
            final LottieEffectInfo bubbleEffectInfo = new BubbleLottieEffectInfo(mContext, current, target,
                    isIncrease, isDecrease);
            final LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
            ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return bubbleEffectInfo.fetchBitmapFromAssets(
                            lottieAnimationView,
                            lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(),
                            lottieImageAsset.getHeight(),
                            mContext);
                }
            };
            lottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "bubble");
            lottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
            lottieAnimationView.useHardwareAcceleration(true);
            lottieAnimationView.playAnimation();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.rl_livevideo_info);
            lp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_livevideo_info);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.bottomMargin = ScreenUtils.getScreenHeight() - rlLivevideoInfo.getTop() - SizeUtils.Dp2Px(mContext, 3);
            viewGroup.addView(lottieAnimationView, lp);
        }
    }

    private void setEngPro(int progress) {
        logger.d("setEngPro:progress=" + progress);
        if (pgAchivePk == null) {
            return;
        }
        pgAchivePk.setProgress(progress);
        final ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        if (rl_livevideo_info != null) {
            if (flProgress == null) {
                flProgress = new FrameLayout(activity);
                flProgress.setClipChildren(false);
                progressImageView = new ImageView(activity);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.app_livevideo_enteampk_pkbar_fire_pic_prog);
                progressImageView.setImageDrawable(bitmapDrawable);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
                flProgress.addView(progressImageView, layoutParams);
                rl_livevideo_info.addView(flProgress, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                final ViewTreeObserver viewTreeObserver = pgAchivePk.getViewTreeObserver();
                viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        setLayoutOnDraw();
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(this);
                        }
                        pgAchivePk.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
            } else {
                setLayout();
            }
        }
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        setLayoutOnDraw();
        onBetterMeLayoutChange();
    }

    private void onBetterMeLayoutChange() {
        if (rlAchiveContent != null) {
            rlAchiveContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (rlAchiveContent.getWidth() != 0) {
                        rlAchiveContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (rlAchiveContent.getWidth() < SizeUtils.Dp2Px(mContext, WIDTH_PROGRESS_BAR_AIM_VIEWGROUP)) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pgAchiveAim
                                    .getLayoutParams();
                            layoutParams.width = rlAchiveAimContent.getWidth() - SizeUtils.Dp2Px(mContext, 22);
                            pgAchiveAim.setLayoutParams(layoutParams);
                            WIDTH_SOLID_PROGRESS_BAR_AIM = SizeUtils.Px2Dp(mContext, rlAchiveAimContent.getWidth()) -
                                    28;
                        }
                        setBetterMePro(currentProgress);
                    }
                }
            });
        }
    }

    private void setLayoutOnDraw() {
        if (pgAchivePk == null) {
            return;
        }
        setLayout();
        pgAchivePk.postDelayed(new Runnable() {
            @Override
            public void run() {
                final ViewTreeObserver viewTreeObserver = pgAchivePk.getViewTreeObserver();
                viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        setLayout();
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(this);
                        }
                        pgAchivePk.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
            }
        }, 10);
    }

    private boolean setLayout() {
        ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        int[] loc = ViewUtil.getLoc(pgAchivePk, rl_livevideo_info);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) progressImageView.getLayoutParams();
        int rlWidth = progressImageView.getWidth();
        int edge = SizeUtils.Dp2Px(mContext, 5);
        int pgWidth = pgAchivePk.getWidth() - edge * 2;
        int leftMargin = loc[0] + edge - rlWidth / 2 + pgWidth * pgAchivePk.getProgress() / pgAchivePk.getMax();
        int topMargin = loc[1] - (progressImageView.getHeight() - pgAchivePk.getHeight()) / 2;
        logger.d("initListener:left=" + loc[0] + ",top=" + loc[1] + ",rlWidth=" + rlWidth
                + ",width=" + pgAchivePk.getWidth() + ",prog=" + pgAchivePk.getProgress() + ",leftMargin=" + leftMargin);
        if (leftMargin != lp.leftMargin || topMargin != lp.topMargin) {
            lp.leftMargin = leftMargin;
            lp.topMargin = topMargin;
            progressImageView.setLayoutParams(lp);
            progressImageView.setVisibility(View.VISIBLE);
        }
        return false;
    }

}
