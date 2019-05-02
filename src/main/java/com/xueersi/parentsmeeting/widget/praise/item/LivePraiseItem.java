package com.xueersi.parentsmeeting.widget.praise.item;

import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

public class LivePraiseItem implements RItemViewInterface<PraiseContentEntity> {

    TextView  tvName;
    @Override
    public int getItemLayoutId() {
        return R.layout.item_livevideo_praise_list_dark;
    }

    @Override
    public boolean isShowView(PraiseContentEntity item, int position) {
        if(item.getViewType()== PraiseConfig.VIEW_TYPE_TITLE) {
            return false;
        } else {
            return true;
        }    }

    @Override
    public void initView(ViewHolder holder, int position) {

        tvName = holder.getView(R.id.tv_item_livevideo_praise_list_name);

    }

    @Override
    public void convert(ViewHolder holder, PraiseContentEntity praiseContentEntity, int position) {
        tvName.setText(praiseContentEntity.getName());
    }
}
