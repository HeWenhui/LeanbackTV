package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/23.
 * 新直播回放
 */
public class LivePlaybackVideoActivity extends LiveBackVideoActivityBase {
    private static String TAG = "LivePlaybackVideoActivityLog";
    /**
     * 用来判断是否是站立直播体验课
     */
    private Boolean isExperience = false;

    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        String where = getIntent().getStringExtra("where");

        //讲座回放
        if ("PublicLiveDetailActivity".equals(where)) {
            //判断是否是大班整合
            if(isBigLive()){
                try {
                    String fname = "com.xueersi.parentsmeeting.modules.livebusiness.enter.LiveBusinessBackFragment";
                    LiveBackVideoFragmentBase fragmentBase = (LiveBackVideoFragmentBase) Fragment.instantiate(this, fname);
                    return fragmentBase;
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }else{
                return new LecBackVideoFragment();
            }

        }


        int pattern = getIntent().getIntExtra("pattern", 0);
        isExperience = getIntent().getBooleanExtra("isExperience", false);
        if (!isExperience) {
            if (pattern == LiveVideoConfig.LIVE_PATTERN_2) {
                try {
                    String fname = "com.xueersi.parentsmeeting.modules.livevideo.fragment.StandBackVideoFragment";
                    LiveBackVideoFragmentBase fragmentBase = (LiveBackVideoFragmentBase) Fragment.instantiate(this, fname);
                    return fragmentBase;
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }
            return new LiveBackVideoFragment();
        }
        String fname = "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandLiveVideoExperienceFragment";
        LiveBackVideoFragmentBase fragmentBase = (LiveBackVideoFragmentBase) Fragment.instantiate(this, fname);
        return fragmentBase;
    }


    /**
     * 判断是否是大班整合
     * @return
     */
    private boolean isBigLive() {
        Bundle bundle = getIntent().getExtras();
        boolean  result = bundle != null && bundle.getBoolean("isBigLive");
        return result;
    }

    @Override
    protected void updateIcon() {
        super.updateIcon();
        if (liveVideoFragmentBase instanceof LiveBackVideoFragment) {
            LiveBackVideoFragment liveBackVideoFragment = (LiveBackVideoFragment) liveVideoFragmentBase;
            liveBackVideoFragment.updateIcon();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (liveVideoFragmentBase instanceof LecBackVideoFragment) {
            LecBackVideoFragment liveVideoFragmentBase = (LecBackVideoFragment) this.liveVideoFragmentBase;
            liveVideoFragmentBase.onRestart();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (liveVideoFragmentBase instanceof LecBackVideoFragment) {
            LecBackVideoFragment lecBackVideoFragment = (LecBackVideoFragment) liveVideoFragmentBase;
            lecBackVideoFragment.onNewIntent(intent);
        }
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, LivePlaybackVideoActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        intent.putExtra("contextname", context.getClass().getSimpleName());
        try {
            context.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        try {
            VideoLivePlayBackEntity serializable = (VideoLivePlayBackEntity) bundle.getSerializable("videoliveplayback");
            if (serializable != null) {
                HashMap<String, String> hashMap = new HashMap();
                if (serializable.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_RECORDED) {
                    hashMap.put("logtype", "recorded");
                } else if (serializable.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
                    hashMap.put("logtype", "lecplayback");
                    hashMap.put("isBigLive",bundle.getBoolean("isBigLive")+"");
                } else {
                    hashMap.put("logtype", "liveplayback");
                }
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + context.getClass().getSimpleName());
                hashMap.put("bundle", "" + bundle);
                hashMap.put("liveid", "" + serializable.getLiveId());
                UmsAgentManager.umsAgentDebug(context, "LivePlaybackVideoActivityIntentTo", hashMap);
            } else {
                LiveCrashReport.postCatchedException(new Exception("" + bundle));
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("logtype", "videoliveplayback");
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + context.getClass().getSimpleName());
                hashMap.put("bundle", "" + bundle);
                hashMap.put("exception", "" + Log.getStackTraceString(new Exception()));
                UmsAgentManager.umsAgentDebug(context, "LivePlaybackVideoActivityIntentTo", hashMap);
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }
}
