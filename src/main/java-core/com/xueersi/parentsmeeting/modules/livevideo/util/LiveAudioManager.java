package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.media.AudioManager;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * 直播音量
 */
public class LiveAudioManager {
    /** 音量管理 */
    private AudioManager mAM;
    /** 最大音量 */
    private int mMaxVolume;
    /** 当前音量 */
    private int mVolume;
    private Context mContext;
    private String name;
    protected Logger logger = LoggerFactory.getLogger("LiveAudioManager");

    public LiveAudioManager(Context mContext, String name) {
        this.name = name;
        this.mContext = mContext;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // 音量管理
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(int index) {
        logger.d("setVolume:index=" + index);
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        StableLogHashMap stableLogHashMap = new StableLogHashMap("setVolume");
        stableLogHashMap.put("name", "" + name);
        stableLogHashMap.put("index", "" + index);
        UmsAgentManager.umsAgentDebug(mContext, LogConfig.LIVE_AUDIO, stableLogHashMap.getData());
    }

    public int getmMaxVolume() {
        return mMaxVolume;
    }

    public int getmVolume() {
        return mVolume;
    }

}
