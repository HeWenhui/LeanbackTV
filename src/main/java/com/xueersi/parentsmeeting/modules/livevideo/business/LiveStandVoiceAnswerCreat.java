package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2018/4/3.
 * 直播创建语音答题
 */
public class LiveStandVoiceAnswerCreat implements BaseVoiceAnswerCreat {
    @Override
    public BaseVoiceAnswerPager create(Activity activity, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type,
                                       QuestionSwitch questionSwitch, RelativeLayout rlQuestionContent, SpeechEvaluatorUtils mIse, LiveAndBackDebug liveAndBackDebug) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        VoiceAnswerStandPager voiceAnswerPager2 = new VoiceAnswerStandPager(activity, baseVideoQuestionEntity, assess_ref, videoQuestionLiveEntity.type, questionSwitch, liveAndBackDebug);
        voiceAnswerPager2.setIse(mIse);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlQuestionContent.addView(voiceAnswerPager2.getRootView(), params);
        return voiceAnswerPager2;
    }

    @Override
    public void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
//        if (rightMargin != params.rightMargin) {
//            params.rightMargin = rightMargin;
//            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
//        }
    }

}
