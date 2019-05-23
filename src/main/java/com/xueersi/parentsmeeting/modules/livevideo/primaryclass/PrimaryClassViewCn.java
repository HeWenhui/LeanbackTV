package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;


public class PrimaryClassViewCn implements PrimaryClassView {
    public PrimaryClassViewCn(Context context) {
        ProxUtil.getProxUtil().put(context, PrimaryClassView.class, this);
    }

    @Override
    public int getKuangjia() {
        return R.drawable.bg_live_primary_class_kuangjia_img_normal_cn;
    }

    @Override
    public int getBackImg() {
        return R.drawable.bg_livevideo_priclass_normal_cn;
    }

    @Override
    public void decorateItemMy(View view) {
        ImageView iv_livevideo_primary_team_energy = view.findViewById(R.id.iv_livevideo_primary_team_energy);
        iv_livevideo_primary_team_energy.setImageResource(R.drawable.bg_livevideo_toast_energe2_icon_normal_cn);
        RelativeLayout rl_livevideo_course_item_video_off = view.findViewById(R.id.rl_livevideo_course_item_video_off);
        rl_livevideo_course_item_video_off.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        RelativeLayout rl_livevideo_course_item_video_ufo = view.findViewById(R.id.rl_livevideo_course_item_video_ufo);
        rl_livevideo_course_item_video_ufo.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
    }

    @Override
    public void decorateItemOther(View view) {

    }

    @Override
    public void decorateItemBack(View view) {

    }

    @Override
    public void decorateItemEmpty(View view) {
        view.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
    }

    @Override
    public void decorateItemMyAddEnergy(View view) {
        ImageView iv_livevideo_primary_team_energy = view.findViewById(R.id.iv_livevideo_primary_team_energy);
        iv_livevideo_primary_team_energy.setImageResource(R.drawable.bg_livevideo_toast_energe1_icon_normal_cn);
    }
}
