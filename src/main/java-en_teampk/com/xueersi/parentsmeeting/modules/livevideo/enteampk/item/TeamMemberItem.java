package com.xueersi.parentsmeeting.modules.livevideo.enteampk.item;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class TeamMemberItem implements AdapterItemInterface<TeamMemberEntity> {
    private RelativeLayout ivTeampkMember;
    private TextView tvTeampkName;
    private ImageView civTeampkHead;
    private RelativeLayout group;

    public TeamMemberItem(RelativeLayout group) {
        this.group = group;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_en_team_member;
    }

    @Override
    public void initViews(View root) {
        ivTeampkMember = root.findViewById(R.id.rl_livevideo_en_teampk_member);
        tvTeampkName = root.findViewById(R.id.tv_livevideo_en_teampk_name);
        civTeampkHead = root.findViewById(R.id.civ_livevideo_en_teampk_head);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        tvTeampkName.setText(entity.name);
        if (entity.isMy) {
//            tvTeampkName.setBackgroundResource(R.drawable.app_zhanduipk_xuanzhong_pic);
            ivTeampkMember.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_light_bg_img_nor);
            tvTeampkName.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    tvTeampkName.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] loc = ViewUtil.getLoc(tvTeampkName, group);
                    ImageView imageView = new ImageView(group.getContext());
                    imageView.setImageResource(R.drawable.app_zhanduipk_xuanzhong_pic);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.leftMargin = loc[0];
                    lp.topMargin = loc[1];
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    group.addView(imageView, lp);
                    return false;
                }
            });
        } else {
//            tvTeampkName.setBackgroundResource(0);
            ivTeampkMember.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_bg_img_nor);
        }
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civTeampkHead);
    }
}
