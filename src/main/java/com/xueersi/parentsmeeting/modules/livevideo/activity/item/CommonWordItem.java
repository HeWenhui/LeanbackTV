package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;


/**
 * Created by linyuqiang on 2017/7/19.
 * 聊天常用词
 */
public class CommonWordItem implements AdapterItemInterface<String> {
    protected Context mContext;
    protected View root;
    TextView tv_livevideo_common_word;
    private int messageSize = 0;
    CommonAdapter commonAdapter;

    public CommonWordItem(Context context, CommonAdapter commonAdapter) {
        this.mContext = context;
        this.commonAdapter = commonAdapter;
        messageSize = (int) (ScreenUtils.getScreenDensity() * 27);
    }

    @Override
    public int getLayoutResId() {
        if(LiveVideoConfig.isPrimary){
            return R.layout.item_livevideo_message_pscommonword;
        } else {
            return R.layout.item_livevideo_message_commonword;
        }

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
