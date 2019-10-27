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
public class SettingEnglishNameItem implements RItemViewInterface<EngLishNameEntity> {

    TextView tvName;

    private Context mContext;
    EnglishNameListener englishNameListener;
    public SettingEnglishNameItem(Context context,EnglishNameListener englishNameListener){
        mContext =context;
        this.englishNameListener = englishNameListener;

    }
    @Override
    public int getItemLayoutId() {
        return R.layout.item_setting_english_name;
    }

    @Override
    public boolean isShowView(EngLishNameEntity item, int position) {
        return !item.isIndex();
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        tvName = holder.getView(R.id.tv_setting_english_name);
    }

    @Override
    public void convert(ViewHolder holder,  EngLishNameEntity entity,  int position) {
        tvName.setText(entity.getName());

        if (entity.isSelect()) {
            tvName.setBackgroundResource(R.drawable.bg_groupclass_setting_english_select);
            tvName.setTextColor(mContext.getResources().getColor(R.color.COLOR_BE4C15));

        } else {
            tvName.setBackgroundResource(R.drawable.bg_groupclass_setting_english_normal);
            tvName.setTextColor(mContext.getResources().getColor(R.color.COLOR_7B6E6E));
        }
        setData(entity,position);
    }

    private void setData(final EngLishNameEntity entity, final int position) {

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                englishNameListener.select(EnglishNameConfig.GROUP_CLASS_ENGLISH_NAME_SELECT,position,entity.getName());
            }
        });
    }
}
