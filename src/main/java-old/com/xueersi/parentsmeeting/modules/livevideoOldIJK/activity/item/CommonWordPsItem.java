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
 * Created by David on 2018/8/14.
 */

public class CommonWordPsItem implements AdapterItemInterface<String> {
    protected Context mContext;
    protected View root;
    TextView tv_livevideo_common_word;
    ImageView divider;
    private int messageSize = 0;
    CommonAdapter commonAdapter;

    public CommonWordPsItem(Context context, CommonAdapter commonAdapter) {
        this.mContext = context;
        this.commonAdapter = commonAdapter;
        messageSize = (int) (ScreenUtils.getScreenDensity() * 27);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_message_pscommonword;
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
//        if (position == 0) {
//            root.setBackgroundResource(R.drawable.shape_livevideo_commonwordps_top_bg);
//        } else if (position == commonAdapter.getCount() - 1) {
//            root.setBackgroundResource(R.drawable.shape_livevideo_commonwordps_bottom_bg);
//        } else {
//            root.setBackgroundResource(R.drawable.shape_livevideo_commonwordps_bg);
//        }
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
