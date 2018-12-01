package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.app.Activity;
import android.content.Context;
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

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

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
            pgAchivePk = pkview.findViewById(R.id.pg_livevideo_en_achive_pk);
            tv_livevideo_en_achive_pk_energy_my = pkview.findViewById(R.id.tv_livevideo_en_achive_pk_energy_my);
            tv_livevideo_en_achive_pk_energy_other = pkview.findViewById(R.id.tv_livevideo_en_achive_pk_energy_other);
            tv_livevideo_en_achive_pk_energy_my.setText("" + enpkEnergy.myTeam);
            tv_livevideo_en_achive_pk_energy_other.setText("" + enpkEnergy.opTeam);
            int progress = 0;
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
        lp.topMargin = loc[1] - (progressImageView.getHeight() - pgAchivePk.getHeight()) / 2 - 10;
        logger.d("initListener:left=" + loc[0] + ",top=" + loc[1]);
        progressImageView.setLayoutParams(lp);
        progressImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        tvAchiveNumFire.setText(starAndGoldEntity.getPkEnergy().me);
        tvAchiveNumStar.setText(starAndGoldEntity.getStarCount());
        tvAchiveNumGold.setText(starAndGoldEntity.getGoldCount());
    }

    public void onStarAdd(int star, float x, float y) {
        starCount += star;
        tvAchiveNumStar.setText("" + starCount);
    }

}
