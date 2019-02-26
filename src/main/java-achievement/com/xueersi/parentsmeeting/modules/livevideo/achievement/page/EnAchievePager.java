package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

import java.util.Random;

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
    //    private ImageView progressImageView;
    private Activity activity;
    private TextView tvAchiveNumStar;
    private TextView tvAchiveNumGold;
    private TextView tvAchiveNumFire;
    private TextView tv_livevideo_en_achive_pk_energy_my;
    private TextView tv_livevideo_en_achive_pk_energy_other;
    private int starCount;
    private int goldCount;
    private int energyCount;
    private int myTotal = 0;
    private int otherTotal = 0;

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
        View view = activity.findViewById(R.id.iv_livevideo_message_small_bg);
        if (1 == englishPk.canUsePK && 1 == englishPk.hasGroup) {
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
    }

    public void onEnglishPk() {
        mLogtf.d("onEnglishPk");
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
        tv_livevideo_en_achive_pk_energy_my = pkView.findViewById(R.id.tv_livevideo_en_achive_pk_energy_my);
        tv_livevideo_en_achive_pk_energy_my.setText("" + myTotal);
        tv_livevideo_en_achive_pk_energy_other = pkView.findViewById(R.id.tv_livevideo_en_achive_pk_energy_other);
        tv_livevideo_en_achive_pk_energy_other.setText("" + otherTotal);
    }

    public void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
        //本场成就设置进度
        int myTeamTotal = enTeamPkRankEntity.getMyTeamTotal();
        if (myTeamTotal > myTotal) {
            myTotal = myTeamTotal;
            if (tv_livevideo_en_achive_pk_energy_my != null) {
                tv_livevideo_en_achive_pk_energy_my.setText("" + myTeamTotal);
            }
        } else {
            mLogtf.d("updateEnpk:myTeamTotal=" + myTeamTotal + ",otherTotal=" + myTotal);
        }
        int opTeamTotal = enTeamPkRankEntity.getOpTeamTotal();
        if (opTeamTotal > otherTotal) {
            otherTotal = opTeamTotal;
            if (tv_livevideo_en_achive_pk_energy_other != null) {
                tv_livevideo_en_achive_pk_energy_other.setText("" + opTeamTotal);
            }
        } else {
            mLogtf.d("updateEnpk:opTeamTotal=" + opTeamTotal + ",otherTotal=" + otherTotal);
        }
        if (myTotal + otherTotal != 0) {
            int progress = (int) ((float) myTotal * 100 / (float) (myTotal + otherTotal));
            logger.d("updateEnpk:progress=" + progress + "," + pgAchivePk.getProgress());
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
                        view.setLayoutParams(lp);
                    }
                    rlAchiveBack.setBackgroundResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_bg1_img_nor);
                } else {
                    if (view != null) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        lp.topMargin = 0;
                        view.setLayoutParams(lp);
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
            if (tv_livevideo_en_achive_pk_energy_my != null) {
                tv_livevideo_en_achive_pk_energy_my.setText("" + myTotal);
            }
        } else {
            mLogtf.d("onGetStar:myTeam=" + pkEnergy.myTeam + ",myTotal=" + myTotal);
        }
        if (pkEnergy.opTeam > otherTotal) {
            otherTotal = pkEnergy.opTeam;
            if (tv_livevideo_en_achive_pk_energy_other != null) {
                tv_livevideo_en_achive_pk_energy_other.setText("" + otherTotal);
            }
        } else {
            mLogtf.d("onGetStar:opTeam=" + pkEnergy.opTeam + ",otherTotal=" + myTotal);
        }
        ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        if (rl_livevideo_info != null) {
            final int energyCountAdd = starAndGoldEntity.getPkEnergy().me - energyCount;
            final int goldCountAdd = starAndGoldEntity.getGoldCount() - goldCount;
            energyCount = starAndGoldEntity.getPkEnergy().me;
            goldCount = starAndGoldEntity.getGoldCount();
            mLogtf.d("onGetStar:energyCountAdd=" + energyCountAdd + ",goldCountAdd=" + goldCountAdd);
            String LOTTIE_RES_ASSETS_ROOTDIR;
            String[] targetFileNames;
            final int type;
            if (energyCountAdd > 0 && goldCountAdd > 0) {
                LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/gold_energy";
                targetFileNames = new String[]{"img_0.png", "img_1.png"};
                type = 1;
            } else if (energyCountAdd > 0) {
                LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/nogold_energy";
                targetFileNames = new String[]{"img_0.png"};
                type = 2;
            } else if (goldCountAdd > 0) {
                LOTTIE_RES_ASSETS_ROOTDIR = "en_team_pk/gold_noenergy";
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
                if (tv_livevideo_en_achive_pk_energy_my != null) {
                    tv_livevideo_en_achive_pk_energy_my.setText("" + myTotal);
                }
            }
            if (pkEnergy.opTeam > otherTotal) {
                otherTotal = pkEnergy.opTeam;
                if (tv_livevideo_en_achive_pk_energy_other != null) {
                    tv_livevideo_en_achive_pk_energy_other.setText("" + otherTotal);
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
    }

    public void onStarAdd(int star, float x, float y) {
        starCount += star;
        tvAchiveNumStar.setText("" + starCount);
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
                flProgress.setVisibility(View.INVISIBLE);
                flProgress.setClipChildren(false);
                ImageView progressImageView = new ImageView(activity);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.app_livevideo_enteampk_pkbar_fire_pic_prog);
//                progressImageView.setImageResource(R.drawable.app_livevideo_enteampk_pkbar_fire_pic_prog);
                progressImageView.setImageDrawable(bitmapDrawable);
//                flProgress.setVisibility(View.INVISIBLE);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(progressWidth, progressWidth);
                layoutParams.gravity = Gravity.CENTER;
                flProgress.addView(progressImageView, layoutParams);
//                flProgress.addView(progressImageView);
//                rl_livevideo_info.addView(flProgress, width, width);
                rl_livevideo_info.addView(flProgress);
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

    private int lastFlProgress = 0;

    private void setLayoutOnDraw() {
        setLayout();
        final ViewTreeObserver viewTreeObserver = flProgress.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayout();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                flProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private boolean setLayout() {
        ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        int[] loc = ViewUtil.getLoc(pgAchivePk, rl_livevideo_info);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flProgress.getLayoutParams();
        int rlWidth = flProgress.getWidth();
        ImageView progressImageView = (ImageView) flProgress.getChildAt(0);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) progressImageView.getDrawable();
        int minWidth = bitmapDrawable.getBitmap().getWidth() * 108 / 176;
        if (rlWidth < minWidth) {
            rlWidth = minWidth;
        }
        int leftMargin = loc[0] - rlWidth / 2 + pgAchivePk.getWidth() * pgAchivePk.getProgress() / pgAchivePk.getMax();
        int topMargin = loc[1] - (flProgress.getHeight() - pgAchivePk.getHeight()) / 2;
        logger.d("initListener:left=" + loc[0] + ",top=" + loc[1] + ",width=" + lastFlProgress + "," + rlWidth + ",minWidth=" + minWidth);
        if (leftMargin != lp.leftMargin || topMargin != lp.topMargin) {
            lp.leftMargin = leftMargin;
            lp.topMargin = topMargin;
            flProgress.setLayoutParams(lp);
            flProgress.setVisibility(View.VISIBLE);
        }
//        if (lastFlProgress == flProgress.getWidth()) {
//            return true;
//        }
//        lastFlProgress = flProgress.getWidth();
        return false;
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
            paint.setColor(0xff4eacf1);
            canvas.drawText(drawText, (width - w) / 2, height, paint);
            return drawBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
