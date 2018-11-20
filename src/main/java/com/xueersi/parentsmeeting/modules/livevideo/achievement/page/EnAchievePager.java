package com.xueersi.parentsmeeting.modules.livevideo.achievement.page;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public class EnAchievePager extends LiveBasePager {
    RelativeLayout parent;
    LiveGetInfo mLiveGetInfo;
    CheckBox cb_livevideo_en_achive_title;
    RelativeLayout rl_livevideo_en_achive_back;
    RelativeLayout rl_livevideo_en_achive_content;
    ViewStub vs_livevideo_en_achive_bottom;
    ViewStub vs_livevideo_en_achive_bottom2;
    Activity activity;
    private TextView tv_livevideo_en_achive_num_star;
    private TextView tv_livevideo_en_achive_num_gold;
    private int starCount;
    private int goldCount;

    public EnAchievePager(Context context, RelativeLayout relativeLayout, LiveGetInfo mLiveGetInfo) {
        super(context, false);
        this.parent = relativeLayout;
        this.mLiveGetInfo = mLiveGetInfo;
        starCount = mLiveGetInfo.getStarCount();
        goldCount = mLiveGetInfo.getGoldCount();
        activity = (Activity) context;
        initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_livevodeo_en_achive, parent, false);
        cb_livevideo_en_achive_title = mView.findViewById(R.id.cb_livevideo_en_achive_title);
        rl_livevideo_en_achive_back = mView.findViewById(R.id.rl_livevideo_en_achive_back);
        rl_livevideo_en_achive_content = mView.findViewById(R.id.rl_livevideo_en_achive_content);
        vs_livevideo_en_achive_bottom = mView.findViewById(R.id.vs_livevideo_en_achive_bottom);
        vs_livevideo_en_achive_bottom2 = mView.findViewById(R.id.vs_livevideo_en_achive_bottom2);
        tv_livevideo_en_achive_num_star = mView.findViewById(R.id.tv_livevideo_en_achive_num_star);
        tv_livevideo_en_achive_num_gold = mView.findViewById(R.id.tv_livevideo_en_achive_num_gold);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
        tv_livevideo_en_achive_num_star.setText("" + starCount);
        tv_livevideo_en_achive_num_gold.setText("" + goldCount);
    }

    @Override
    public void initListener() {
        super.initListener();
        cb_livevideo_en_achive_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rl_livevideo_en_achive_content.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                View view = activity.findViewById(R.id.iv_livevideo_message_small_bg);
                if (isChecked) {
                    if (view != null) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        lp.topMargin = (int) (73 * ScreenUtils.getScreenDensity());
                        view.setLayoutParams(lp);
                    }
                    rl_livevideo_en_achive_back.setBackgroundResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_bg1_img_nor);
                } else {
                    if (view != null) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        lp.topMargin = 0;
                        view.setLayoutParams(lp);
                    }
                    rl_livevideo_en_achive_back.setBackgroundResource(R.drawable.app_livevideo_enteampk_benchangchengjiu_bg_img_nor);
                }
            }
        });
        LiveGetInfo.EnglishPk englishPk = mLiveGetInfo.getEnglishPk();
        View view = activity.findViewById(R.id.iv_livevideo_message_small_bg);
        if (1 == englishPk.canUsePK) {
            vs_livevideo_en_achive_bottom.inflate();
        } else {
            vs_livevideo_en_achive_bottom2.inflate();
        }
        if (view != null) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            lp.topMargin = (int) (73 * ScreenUtils.getScreenDensity());
            view.setLayoutParams(lp);
        }
    }

    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        int starCountAdd = starAndGoldEntity.getStarCount() - starCount;
        int goldCountAdd = starAndGoldEntity.getGoldCount() - goldCount;
        tv_livevideo_en_achive_num_star.setText("" + starAndGoldEntity.getStarCount());
        tv_livevideo_en_achive_num_gold.setText("" + starAndGoldEntity.getGoldCount());
    }

    public void onStarAdd(int star, float x, float y) {
        starCount += star;
        tv_livevideo_en_achive_num_star.setText("" + starCount);
    }
}
