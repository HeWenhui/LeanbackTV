package com.xueersi.parentsmeeting.modules.livevideo.enteampk.item;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ViewUtil;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class TeamMemberItem implements AdapterItemInterface<TeamMemberEntity> {
    private Logger logger = LiveLoggerFactory.getLogger("TeamMemberItem");
    private ImageView ivTeampkMember;
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
//            ivTeampkMember.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_light_bg_img_nor);
            tvTeampkName.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    tvTeampkName.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] loc = ViewUtil.getLoc(tvTeampkName, group);
                    ImageView imageView = new ImageView(group.getContext());
                    Bitmap bitmap = DrawableHelper.bitmapFromResource(group.getResources(), R.drawable.app_zhanduipk_xuanzhong_pic);
                    imageView.setImageBitmap(bitmap);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.leftMargin = loc[0] - (bitmap.getWidth() - tvTeampkName.getWidth()) / 2;
                    lp.topMargin = loc[1];
                    logger.d("updateViews:my=" + loc[0] + "," + loc[1]);
                    group.addView(imageView, lp);
                    return false;
                }
            });
        } else {
//            tvTeampkName.setBackgroundResource(0);
//            ivTeampkMember.setBackgroundResource(R.drawable.app_livevideo_enteampk_morentouxiang_bg_img_nor);
        }
        BetterMeUtil.addSegment(ivTeampkMember, entity.getSegmentType(), entity.getStar());
        ImageLoader.with(ContextManager.getContext()).load(entity.headurl).error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civTeampkHead);
    }
}
