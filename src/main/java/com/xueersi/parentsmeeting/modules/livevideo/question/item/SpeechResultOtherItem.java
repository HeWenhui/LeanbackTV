package com.xueersi.parentsmeeting.modules.livevideo.question.item;

import android.content.ContextWrapper;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultMember;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

public class SpeechResultOtherItem implements RItemViewInterface<SpeechResultMember> {
    ImageView civ_live_speech_result_member_head;
    TextView tv_live_speech_result_member_score;
    TextView tv_live_speech_result_member_name;

    @Override
    public int getItemLayoutId() {
        return R.layout.item_livevideo_speech_result_member;
    }

    @Override
    public boolean isShowView(SpeechResultMember item, int position) {
        return true;
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        civ_live_speech_result_member_head = holder.getView(R.id.civ_live_speech_result_member_head);
        tv_live_speech_result_member_score = holder.getView(R.id.tv_live_speech_result_member_score);
        tv_live_speech_result_member_name = holder.getView(R.id.tv_live_speech_result_member_name);
    }

    @Override
    public void convert(ViewHolder holder, SpeechResultMember speechResultMember, int position) {
        ImageLoader.with(ContextManager.getContext()).load(speechResultMember.headUrl)
                .error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civ_live_speech_result_member_head);
        tv_live_speech_result_member_score.setText("" + speechResultMember.score);
        tv_live_speech_result_member_name.setText(speechResultMember.name);
    }
}
