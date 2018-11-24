package com.xueersi.parentsmeeting.modules.livevideo.enteampk.item;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class TeamMemberItem implements AdapterItemInterface<TeamMemberEntity> {
    private RelativeLayout iv_livevideo_en_teampk_member;
    private TextView tv_livevideo_en_teampk_name;
    private ImageView civ_livevideo_en_teampk_head;

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_en_team_member;
    }

    @Override
    public void initViews(View root) {
        iv_livevideo_en_teampk_member = root.findViewById(R.id.rl_livevideo_en_teampk_member);
        tv_livevideo_en_teampk_name = root.findViewById(R.id.tv_livevideo_en_teampk_name);
        civ_livevideo_en_teampk_head = root.findViewById(R.id.civ_livevideo_en_teampk_head);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        tv_livevideo_en_teampk_name.setText(entity.name);
        if (entity.isMy) {
            tv_livevideo_en_teampk_name.setBackgroundResource(R.drawable.app_zhanduipk_xuanzhong_pic);
            iv_livevideo_en_teampk_member.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_light_bg_img_nor);
        } else {
            tv_livevideo_en_teampk_name.setBackgroundResource(0);
            iv_livevideo_en_teampk_member.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_bg_img_nor);
        }
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civ_livevideo_en_teampk_head);
    }
}
