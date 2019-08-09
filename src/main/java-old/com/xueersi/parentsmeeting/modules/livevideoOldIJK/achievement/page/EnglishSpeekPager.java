package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class EnglishSpeekPager extends BasePager {
    private ImageView imageView;

    //在父view中的布局
    private RelativeLayout.LayoutParams layoutParams;
    public static final int REMIND = 0;
    public static final int PRAISE = 1;

    public EnglishSpeekPager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_small_english_english_speek_remind_praise, null);
        imageView = view.findViewById(R.id.iv_livevideo_small_english_english_speek);
        return view;
    }

    //背景变黑，不可点击
    @Override
    public void initData() {
        mView.setClickable(true);
    }

    public void updateStatus(int status) {
        if (status == REMIND) {
            imageView.setImageResource(R.drawable.bg_small_english_livevideo_english_speek_remind);
        } else {
            imageView.setImageResource(R.drawable.bg_small_english_livevideo_english_speek_praise);
        }
    }

    public RelativeLayout.LayoutParams getLayoutParams() {
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout
                    .LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return layoutParams;
    }
}
