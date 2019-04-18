package com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.item;

import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class EnTeamItem implements AdapterItemInterface<EnTeamEntity> {
    private ImageView ivTeampkMember;

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_en_teams;
    }

    @Override
    public void initViews(View root) {
        ivTeampkMember = root.findViewById(R.id.iv_livevideo_en_teampk_member);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(EnTeamEntity entity, int position, Object objTag) {
        ivTeampkMember.setImageResource(entity.resId);
    }
}
