package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;


import android.content.Context;

import com.tal.speech.utils.SpeechUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SetVolumeListener;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.BasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

/**
 * Created by linyuqiang on 2017/11/21.
 */

public abstract class BaseSpeechAssessmentPager extends LiveBasePager {
    /** 停止声音 */
    public static String LIVE_STOP_VOLUME = "live_stop_volume";
    /** 语音评测 */
    protected SpeechUtils mIse;

    public BaseSpeechAssessmentPager(Context context) {
        super(context);
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(context, BasePlayerFragment.class);
        if (videoFragment != null) {
            boolean succ = videoFragment.setVolume(0, 0, setVolumeListener);
            logger.d(TAG + ":setVolume:0");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("stop");
            stableLogHashMap.put("tag", TAG);
            stableLogHashMap.put("creattime", "" + creattime);
            stableLogHashMap.put("setsucc", "" + succ);
            umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d(TAG + ":setVolume:null");
        }
    }

    private SetVolumeListener setVolumeListener = new SetVolumeListener() {
        @Override
        public void onSuccess(boolean succ) {
            try {
                StableLogHashMap stableLogHashMap = new StableLogHashMap("status");
                stableLogHashMap.put("tag", TAG);
                stableLogHashMap.put("setsucc", "" + succ);
                stableLogHashMap.put("creattime", "" + creattime);
                umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            boolean succ = videoFragment.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME, null);
            logger.d("onDestroy:setVolume:1");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("start");
            stableLogHashMap.put("tag", TAG);
            stableLogHashMap.put("creattime", "" + creattime);
            stableLogHashMap.put("setsucc", "" + succ);
            umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            try {
                StableLogHashMap stableLogHashMap = new StableLogHashMap("error");
                stableLogHashMap.put("tag", TAG);
                stableLogHashMap.put("creattime", "" + creattime);
                umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
            logger.d("onDestroy:setVolume:null");
        }
    }

    public abstract void examSubmitAll();

    public abstract String getId();

    public abstract void jsExamSubmit();

    public abstract void stopPlayer();

    public void setIse(SpeechUtils mIse) {
        this.mIse = mIse;
    }
}
