package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageEmojiParser;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
/**
*幼教辅导态热词item
*@author chekun
*created  at 2019/5/7 10:07
*/
public class PreSchoolHotwordItem implements AdapterItemInterface<Integer> {
    protected Context mContext;
    protected View root;
    ImageView iv_livevideo_common_word;
    ImageView divider;
    CommonAdapter commonAdapter;

    public PreSchoolHotwordItem(Context context, CommonAdapter commonAdapter) {
        this.mContext = context;
        this.commonAdapter = commonAdapter;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_preschool_hotword;
    }

    @Override
    public void initViews(View root) {
        this.root = root;
        iv_livevideo_common_word = (ImageView) root.findViewById(R.id.iv_livevideo_common_word);
        divider = (ImageView) root.findViewById(R.id.iv_divider);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(Integer resId, int position, Object objTag) {
        if(position == commonAdapter.getCount() - 1){
            divider.setVisibility(View.GONE);
        }else{
            divider.setVisibility(View.VISIBLE);
        }
        if(resId > 0){
            iv_livevideo_common_word.setImageResource(resId);
        }
    }
}
