package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.utils.string.RegexUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

/**
 * Created by David on 2018/8/14.
 */

public class CommonWordPsItem implements AdapterItemInterface<String> {
    protected Context mContext;
    protected View root;
    TextView tv_livevideo_common_word;
    private int messageSize = 0;
    CommonAdapter commonAdapter;

    public CommonWordPsItem(Context context, CommonAdapter commonAdapter) {
        this.mContext = context;
        this.commonAdapter = commonAdapter;
        messageSize = (int) (ScreenUtils.getScreenDensity() * 27);
    }

    @Override
    public int getLayoutResId() {
//        if(LiveVideoConfig.isPrimary){
//            return R.layout.item_livevideo_message_pscommonword;
//        } else {
//            return R.layout.item_livevideo_message_commonword;
//        }
        return R.layout.item_livevideo_message_pscommonword;
    }

    @Override
    public void initViews(View root) {
        this.root = root;
        tv_livevideo_common_word = (TextView) root.findViewById(R.id.tv_livevideo_common_word);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(String entity, int position, Object objTag) {
        if (position == 0) {
            root.setBackgroundResource(R.drawable.shape_livevideo_commomword_top_bg);
        } else if (position == commonAdapter.getCount() - 1) {
            root.setBackgroundResource(R.drawable.shape_livevideo_commomword_bottom_bg);
        } else {
            root.setBackgroundResource(R.drawable.shape_livevideo_commomword_bg);
        }
        SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                        .chatSendContentDeal(entity), mContext,
                messageSize);
        tv_livevideo_common_word.setText(sBuilder);
    }
}
