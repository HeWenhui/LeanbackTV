package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

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
        LiveGetInfo.EnglishPk englishPk = mLiveGetInfo.getEnglishPk();
        if (1 == englishPk.canUsePK) {
            View v = vsAchiveBottom.inflate();
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
