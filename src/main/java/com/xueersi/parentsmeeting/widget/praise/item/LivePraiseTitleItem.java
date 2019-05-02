package com.xueersi.parentsmeeting.widget.praise.item;

import android.content.Context;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * 表扬榜内容标题
 */
public class LivePraiseTitleItem implements RItemViewInterface<PraiseContentEntity> {
    /** 标题名*/
    TextView  tvName;
    Context mContext;
    public LivePraiseTitleItem(Context context){
        mContext =context;
    }
    @Override
    public int getItemLayoutId() {
        return R.layout.item_livevideo_praise_list_title_dark;
    }

    @Override
    public boolean isShowView(PraiseContentEntity item, int position) {
        if(item.getViewType()== PraiseConfig.VIEW_TYPE_TITLE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        tvName = holder.getView(R.id.tv_item_livevideo_praise_list_name);

    }

    @Override
    public void convert(ViewHolder holder, PraiseContentEntity praiseContentEntity, int position) {
        tvName.setText(praiseContentEntity.getName());
        setStyle(praiseContentEntity);
    }

    private void setStyle( PraiseContentEntity praiseContentEntity){
        if (praiseContentEntity.getPraiseStyle() == PraiseConfig.PRAISE_DARK) {
            tvName.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_dark_title);
        } else if (praiseContentEntity.getPraiseStyle() == PraiseConfig.PRAISE_LOVELY) {
            tvName.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_lovely_title);
        } else if (praiseContentEntity.getPraiseStyle() == PraiseConfig.PRAISE_CHINA) {
            tvName.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_china_title);
        } else {
            tvName.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_wood_title);
        }
    }


}
