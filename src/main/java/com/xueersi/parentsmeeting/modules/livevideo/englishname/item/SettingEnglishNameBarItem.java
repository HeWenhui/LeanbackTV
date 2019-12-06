package com.xueersi.parentsmeeting.modules.livevideo.englishname.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.config.EnglishNameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.utils.EnglishNameListener;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * 设置英文名 首字母
 */
public class SettingEnglishNameBarItem implements RItemViewInterface<EngLishNameEntity> {

    private Context mContext;
    TextView tvIndex;
    EnglishNameListener englishNameListener;
    public SettingEnglishNameBarItem(Context context,EnglishNameListener englishNameListener){
        mContext =context;
        this.englishNameListener = englishNameListener;

    }
    @Override
    public int getItemLayoutId() {
        return R.layout.item_setting_english_name_bar_index;
    }

    @Override
    public boolean isShowView(EngLishNameEntity item, int position) {
        return true;
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        tvIndex = holder.getView(R.id.tv_setting_english_name_bar_index);
    }

    @Override
    public void convert(ViewHolder holder, final EngLishNameEntity entity, final int position) {
        tvIndex.setText(entity.getWordIndex());
        if (entity.isSelect()) {
            tvIndex.setBackgroundResource(R.drawable.bg_group_class_grouping_bar_select);
            tvIndex.setTextColor(mContext.getResources().getColor(R.color.COLOR_FFFFFF));
        } else {
            tvIndex.setBackground(null);
            tvIndex.setTextColor(mContext.getResources().getColor(R.color.COLOR_FD963E));
        }
        tvIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                englishNameListener.select(EnglishNameConfig.GROUP_CLASS_ENGLISH_NAME_BAR,position,entity.getWordIndex(),entity.getAudioPath());
            }
        });
    }


}
