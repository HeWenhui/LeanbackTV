package com.xueersi.parentsmeeting.modules.livevideo.englishname.item;

import android.content.Context;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.utils.EnglishNameListener;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * 设置英文名 首字母
 */
public class SettingEnglishNameIndexItem implements RItemViewInterface<EngLishNameEntity> {

    private Context mContext;
    TextView tvIndex;
    EnglishNameListener englishNameListener;
    public SettingEnglishNameIndexItem(Context context,EnglishNameListener englishNameListener){
        mContext =context;
        this.englishNameListener = englishNameListener;

    }
    @Override
    public int getItemLayoutId() {
        return R.layout.item_setting_english_name_index;
    }

    @Override
    public boolean isShowView(EngLishNameEntity item, int position) {
        return item.isIndex();
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        tvIndex = holder.getView(R.id.tv_setting_english_name_index);
    }

    @Override
    public void convert(ViewHolder holder,final EngLishNameEntity entity, final int position) {
        tvIndex.setText(entity.getWordIndex());
    }


}
