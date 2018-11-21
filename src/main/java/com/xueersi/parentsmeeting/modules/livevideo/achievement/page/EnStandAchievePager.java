package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public class EnStandAchievePager extends LiveBasePager {
    RelativeLayout parent;
    LiveGetInfo mLiveGetInfo;
    Activity activity;
    ViewStub vs_livevideo_en_achive_bottom;
    ViewStub vs_livevideo_en_achive_bottom2;
    private TextView tv_livevideo_en_achive_num_fire;
    private TextView tv_livevideo_en_achive_num_star;
    private TextView tv_livevideo_en_achive_num_gold;
    private int starCount;
    private int goldCount;
    private int energyCount;

    public EnStandAchievePager(Context context, RelativeLayout relativeLayout, LiveGetInfo mLiveGetInfo) {
        super(context, false);
        this.parent = relativeLayout;
        this.mLiveGetInfo = mLiveGetInfo;
        starCount = mLiveGetInfo.getStarCount();
        goldCount = mLiveGetInfo.getGoldCount();
        energyCount = mLiveGetInfo.getEnergyCount();
        activity = (Activity) context;
        initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_livevodeo_en_stand_achive, parent, false);
        tv_livevideo_en_achive_num_fire = mView.findViewById(R.id.tv_livevideo_en_achive_num_fire);
        tv_livevideo_en_achive_num_star = mView.findViewById(R.id.tv_livevideo_en_achive_num_star);
        tv_livevideo_en_achive_num_gold = mView.findViewById(R.id.tv_livevideo_en_achive_num_gold);
        vs_livevideo_en_achive_bottom = mView.findViewById(R.id.vs_livevideo_en_achive_bottom);
        vs_livevideo_en_achive_bottom2 = mView.findViewById(R.id.vs_livevideo_en_achive_bottom2);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
        LiveGetInfo.EnglishPk englishPk = mLiveGetInfo.getEnglishPk();
        if (1 == englishPk.canUsePK) {
            View v = vs_livevideo_en_achive_bottom.inflate();
        } else {
            vs_livevideo_en_achive_bottom2.inflate();
        }
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        tv_livevideo_en_achive_num_fire.setText(starAndGoldEntity.getEnergyCount());
        tv_livevideo_en_achive_num_star.setText(starAndGoldEntity.getStarCount());
        tv_livevideo_en_achive_num_gold.setText(starAndGoldEntity.getGoldCount());
    }

    public void onStarAdd(int star, float x, float y) {
        starCount += star;
        tv_livevideo_en_achive_num_star.setText("" + starCount);
    }

}
