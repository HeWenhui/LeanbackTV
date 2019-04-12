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
*半身直播 热词item
*@author chekun
*created  at 2018/11/8 19:05
*/
public class HalfBodyLiveCommonWordItem implements AdapterItemInterface<String> {
    protected Context mContext;
    protected View root;
    TextView tv_livevideo_common_word;
    ImageView divider;
    private int messageSize = 0;
    CommonAdapter commonAdapter;

    public HalfBodyLiveCommonWordItem(Context context, CommonAdapter commonAdapter) {
        this.mContext = context;
        this.commonAdapter = commonAdapter;
        messageSize = (int) (ScreenUtils.getScreenDensity() * 27);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_halfbody_hotwrod;
    }

    @Override
    public void initViews(View root) {
        this.root = root;
        tv_livevideo_common_word = (TextView) root.findViewById(R.id.tv_livevideo_common_word);
        divider = (ImageView) root.findViewById(R.id.iv_divider);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(String entity, int position, Object objTag) {
        if(position == commonAdapter.getCount() - 1){
            divider.setVisibility(View.GONE);
        }else{
            divider.setVisibility(View.VISIBLE);
        }
        SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                        .chatSendContentDeal(entity), mContext,
                messageSize);
        tv_livevideo_common_word.setText(sBuilder);
    }
}
