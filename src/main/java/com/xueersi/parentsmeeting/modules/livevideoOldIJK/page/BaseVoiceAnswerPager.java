package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.content.Context;

import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;

/**
 * Created by linyuqiang on 2018/4/3.
 */
public abstract class BaseVoiceAnswerPager extends LiveBasePager {

    /** 停止声音 */
    public static String LIVE_STOP_VOLUME = BaseSpeechAssessmentPager.LIVE_STOP_VOLUME;

    public BaseVoiceAnswerPager(Context context) {
        super(context);
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(context, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(0, 0);
            logger.d(TAG + ":setVolume:0");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("stop");
            stableLogHashMap.put("tag", TAG);
            umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d(TAG + ":setVolume:null");
        }
    }

    public abstract void setIse(SpeechUtils mIse);

    public abstract BaseVideoQuestionEntity getBaseVideoQuestionEntity();

    public abstract boolean isEnd();

    public abstract void setEnd();

    public abstract void stopPlayer();

    public abstract void setAudioRequest();

    public abstract void onNetWorkChange(int netWorkType);

    public abstract void examSubmitAll(String showQuestion, String s);

    public abstract void onUserBack();

    @Override
    public void onDestroy() {
        super.onDestroy();
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
            logger.d("onDestroy:setVolume:1");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("start");
            stableLogHashMap.put("tag", TAG);
            umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d("onDestroy:setVolume:null");
        }
    }

}
