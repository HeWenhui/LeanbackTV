package com.xueersi.parentsmeeting.widget.praise.item;

import android.content.Context;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * 表扬榜内容
 */
public class LivePraiseItem implements RItemViewInterface<PraiseContentEntity> {
    TextView  tvName;
    Context mContext;
    public LivePraiseItem(Context context){
        mContext =context;
    }
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
        setStyle(praiseContentEntity);
    }

    private void setStyle( PraiseContentEntity praiseContentEntity){
        if (praiseContentEntity.getPraiseStyle() == PraiseConfig.PRAISE_DARK) {
            if(PraiseConfig.PRAISE_IN == praiseContentEntity.getStatus()) {
                tvName.setTextColor(getColor(R.color.COLOR_FFBC2D));
            } else {
                tvName.setTextColor(getColor(R.color.COLOR_FFFFFF));

            }
        } else if (praiseContentEntity.getPraiseStyle() == PraiseConfig.PRAISE_LOVELY) {
            if(PraiseConfig.PRAISE_IN == praiseContentEntity.getStatus()) {
                tvName.setTextColor(getColor(R.color.COLOR_FFBC2D));

            } else {
                tvName.setTextColor(getColor(R.color.COLOR_707070));
            }
        } else if (praiseContentEntity.getPraiseStyle() == PraiseConfig.PRAISE_CHINA) {
            if(PraiseConfig.PRAISE_IN == praiseContentEntity.getStatus()) {
                tvName.setTextColor(getColor(R.color.COLOR_FF8400));
            } else {

                tvName.setTextColor(getColor(R.color.COLOR_707070));
            }
        } else {
            if(PraiseConfig.PRAISE_IN == praiseContentEntity.getStatus()) {
                tvName.setTextColor(getColor(R.color.COLOR_FFA421));
            } else {
                tvName.setTextColor(getColor(R.color.COLOR_985540));
            }
        }
    }
    private int getColor(int id){
        return mContext.getResources().getColor(id);
    }
}
