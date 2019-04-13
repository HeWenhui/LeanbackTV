package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.item;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultMember;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * @author linyuqiang
 * 语音答题结果页,多人的分数名字
 */
public class SpeechResultOtherItem implements RItemViewInterface<SpeechResultMember> {
    private RelativeLayout rlSpeechResultMemberHead;
    /** 头像 */
    private ImageView civSpeechResultMemberHead;
    /** 分数 */
    private TextView tvSpeechResultMemberScore;
    /** 名字 */
    private TextView tvSpeechResultMemberName;

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
        rlSpeechResultMemberHead = holder.getView(R.id.rl_live_speech_result_member_head);
        civSpeechResultMemberHead = holder.getView(R.id.civ_live_speech_result_member_head);
        tvSpeechResultMemberScore = holder.getView(R.id.tv_live_speech_result_member_score);
        tvSpeechResultMemberName = holder.getView(R.id.tv_live_speech_result_member_name);
    }

    @Override
    public void convert(ViewHolder holder, SpeechResultMember speechResultMember, int position) {
        ImageLoader.with(ContextManager.getContext()).load(speechResultMember.headUrl)
                .error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civSpeechResultMemberHead);
        tvSpeechResultMemberScore.setText(speechResultMember.score + "分");
        tvSpeechResultMemberName.setText(speechResultMember.name);
        if (speechResultMember.isSelfRole) {
            rlSpeechResultMemberHead.setBackgroundResource(R.drawable.paiming_wodetouxiang_bg_my);
        } else {
            rlSpeechResultMemberHead.setBackgroundResource(R.drawable.paiming_wodetouxiang_bg_other);
        }
    }
}
