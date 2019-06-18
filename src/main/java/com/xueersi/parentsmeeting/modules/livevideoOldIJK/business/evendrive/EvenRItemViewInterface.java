package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive;

import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * 中学激励系统RecyclerView使用Adapter的子项，暂时未用
 */
public class EvenRItemViewInterface implements RItemViewInterface {

    protected TextView rankLeft;//tv_livevideo_rank_item_left;

    protected TextView rankMiddleLeft;//tv_livevideo_rank_item_mid_left

    protected TextView rankMiddleRight;//tv_livevideo_rank_item_mid_right

    protected TextView rankRight;//tv_livevideo_rank_item_right

    protected ImageView ivRedHeard;

    @Override
    public int getItemLayoutId() {
        return R.layout.item_livevideo_middle_science_even_drive_listview_item;
    }

    @Override
    public boolean isShowView(Object item, int position) {
        return true;
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        rankLeft = holder.getView(R.id.tv_livevideo_rank_item_left);
        rankMiddleLeft = holder.getView(R.id.tv_livevideo_rank_item_mid_left);
        rankMiddleRight = holder.getView(R.id.tv_livevideo_rank_item_mid_right);
        rankRight = holder.getView(R.id.tv_livevideo_rank_item_right);
        ivRedHeard = holder.getView(R.id.iv_livevideo_rank_item_right_leftimg);
    }

    @Override
    public void convert(ViewHolder holder, Object o, int position) {

    }
}
