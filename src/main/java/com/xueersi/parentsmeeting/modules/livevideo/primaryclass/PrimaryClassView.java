package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public interface PrimaryClassView {

    void decorateBack(int width, int height, RelativeLayout rl_course_video_contentview);

    void decorateFrame(ImageView imageView);

    void decorateNovideo(View view);

    void decorateRlContent(View view, int width, int height);

    void decorateItemPager(View view);

    void decorateItemPagerView(RelativeLayout rl_livevideo_primary_team_content, ImageView iv_livevideo_primary_team_icon, LinearLayout ll_livevideo_primary_team_content, TextView tv_livevideo_primary_team_name_mid, int width, int height);

    void decorateItemMy(View view);

    void decorateItemOther(View view);

    void decorateItemEmpty(View view);

    void decorateItemBack(View view);

    void decorateItemMyAddEnergy(View view);
}
