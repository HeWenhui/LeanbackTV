package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SuperSpeakerPopWindowPager extends BasePager {
    private TextView tvTip;

    public SuperSpeakerPopWindowPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_pop_window, null);
        tvTip = view.findViewById(R.id.tv_livevideo_super_speaker_pop_window_tip);
        return view;
    }

    @Override
    public void initData() {

    }

    public void setTextTip(String textTip) {
        tvTip.setText(textTip);
    }
}
