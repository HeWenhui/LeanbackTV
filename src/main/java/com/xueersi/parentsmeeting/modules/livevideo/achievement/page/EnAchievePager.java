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

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;

public class EnAchievePager extends LiveBasePager {
    RelativeLayout parent;
    LiveGetInfo mLiveGetInfo;
    CheckBox cb_livevideo_en_achive_title;
    RelativeLayout rl_livevideo_en_achive_back;
    RelativeLayout rl_livevideo_en_achive_content;
    ViewStub vs_livevideo_en_achive_bottom;
    ViewStub vs_livevideo_en_achive_bottom2;
    private ProgressBar pg_livevideo_en_achive_pk;
    private ImageView progressImageView;
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
            View v = vs_livevideo_en_achive_bottom.inflate();
            pg_livevideo_en_achive_pk = v.findViewById(R.id.pg_livevideo_en_achive_pk);
            setEngPro(20);
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

    private void setEngPro(int progress) {
        pg_livevideo_en_achive_pk.setProgress(progress);
        final ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        if (rl_livevideo_info != null) {
            if (progressImageView == null) {
                progressImageView = new ImageView(activity);
                progressImageView.setImageResource(R.drawable.app_livevideo_enteampk_pkbar_fire_pic_prog);
                progressImageView.setVisibility(View.INVISIBLE);
                rl_livevideo_info.addView(progressImageView);
                pg_livevideo_en_achive_pk.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        pg_livevideo_en_achive_pk.getViewTreeObserver().removeOnPreDrawListener(this);
                        setLayout();
                        return false;
                    }
                });
            } else {
                setLayout();
            }
        }
    }

    private void setLayout() {
        ViewGroup rl_livevideo_info = activity.findViewById(R.id.rl_livevideo_info);
        int[] loc = ViewUtil.getLoc(pg_livevideo_en_achive_pk, rl_livevideo_info);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressImageView.getLayoutParams();
        lp.leftMargin = loc[0] - progressImageView.getWidth() / 2 + pg_livevideo_en_achive_pk.getWidth() * pg_livevideo_en_achive_pk.getProgress() / pg_livevideo_en_achive_pk.getMax();
        lp.topMargin = loc[1] - (progressImageView.getHeight() - pg_livevideo_en_achive_pk.getHeight()) / 2;
        logger.d("initListener:left=" + loc[0] + ",top=" + loc[1]);
        progressImageView.setLayoutParams(lp);
        progressImageView.setVisibility(View.VISIBLE);
    }
}
