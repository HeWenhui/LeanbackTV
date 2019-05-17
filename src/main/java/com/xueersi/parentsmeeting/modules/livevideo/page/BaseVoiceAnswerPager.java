package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;

import com.tal.speech.utils.SpeechUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;

/**
 * Created by linyuqiang on 2018/4/3.
 */
public abstract class BaseVoiceAnswerPager extends LiveBasePager {

    public BaseVoiceAnswerPager(Context context) {
        super(context);
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(context, BasePlayerFragment.class);
        if (videoFragment != null) {
            boolean succ = videoFragment.setVolume(0, 0, setVolumeListener);
            logger.d(TAG + ":setVolume:0");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("stop");
            stableLogHashMap.put("tag", TAG);
            stableLogHashMap.put("setsucc", "" + succ);
            umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d(TAG + ":setVolume:null");
        }
    }

    private BasePlayerFragment.SetVolumeListener setVolumeListener = new BasePlayerFragment.SetVolumeListener() {
        @Override
        public void onSuccess(boolean succ) {
            try {
                StableLogHashMap stableLogHashMap = new StableLogHashMap("status");
                stableLogHashMap.put("tag", TAG);
                stableLogHashMap.put("setsucc", "" + succ);
                stableLogHashMap.put("creattime", "" + creattime);
                umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    };

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
            boolean succ = videoFragment.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME, null);
            logger.d("onDestroy:setVolume:1");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("start");
            stableLogHashMap.put("tag", TAG);
            stableLogHashMap.put("setsucc", "" + succ);
            umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d("onDestroy:setVolume:null");
        }
    }

}
