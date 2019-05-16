package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;


import android.content.Context;

import com.tal.speech.utils.SpeechUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.BasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

/**
 * Created by lyqai on 2017/11/21.
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
            videoFragment.setVolume(0, 0);
            logger.d(TAG + ":setVolume:0");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("stop");
            stableLogHashMap.put("tag", TAG);
            umsAgentDebugSys(LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d(TAG + ":setVolume:null");
        }
    }

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
            try {
                StableLogHashMap stableLogHashMap = new StableLogHashMap("error");
                stableLogHashMap.put("tag", TAG);
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
