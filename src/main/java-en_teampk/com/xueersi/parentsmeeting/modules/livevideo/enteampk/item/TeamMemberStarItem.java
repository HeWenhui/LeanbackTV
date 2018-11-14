package com.xueersi.parentsmeeting.modules.livevideo.enteampk.item;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class TeamMemberStarItem implements AdapterItemInterface<TeamMemberEntity> {
    private ImageView iv_livevideo_en_teampk_member;
    private TextView tv_livevideo_en_teampk_name;

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_en_team_member_star;
    }

    @Override
    public void initViews(View root) {
        iv_livevideo_en_teampk_member = root.findViewById(R.id.iv_livevideo_en_teampk_member);
        tv_livevideo_en_teampk_name = root.findViewById(R.id.tv_livevideo_en_teampk_name);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        tv_livevideo_en_teampk_name.setText(entity.name);
    }
}
