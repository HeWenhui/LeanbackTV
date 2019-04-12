package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandLiveVideoExperienceFragment;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/23.
 * 新直播回放
 */
public class LivePlaybackVideoActivity extends LiveBackVideoActivityBase {

    /**
     * 用来判断是否是站立直播体验课
     */
    private Boolean isExperience = false;

    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        String where = getIntent().getStringExtra("where");
        if ("PublicLiveDetailActivity".equals(where)) {
            return new LecBackVideoFragment();
        }
        int pattern = getIntent().getIntExtra("pattern", 0);

        isExperience = getIntent().getBooleanExtra("isExperience", false);
        if (!isExperience) {
            if (pattern == 2) {
                return new StandBackVideoFragment();
            }
            return new LiveBackVideoFragment();
        }
//        setRequestedOrientation(Configuration.ORIENTATION_LANDSCAPE);
        return StandLiveVideoExperienceFragment.newInstance(isExperience);
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
            CrashReport.postCatchedException(e);
        }
        try {
            VideoLivePlayBackEntity serializable = (VideoLivePlayBackEntity) bundle.getSerializable("videoliveplayback");
            if (serializable != null) {
                HashMap<String, String> hashMap = new HashMap();
                if (serializable.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_RECORDED) {
                    hashMap.put("logtype", "recorded");
                } else if (serializable.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
                    hashMap.put("logtype", "lecplayback");
                } else {
                    hashMap.put("logtype", "liveplayback");
                }
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + context.getClass().getSimpleName());
                hashMap.put("bundle", "" + bundle);
                hashMap.put("liveid", "" + serializable.getLiveId());
                UmsAgentManager.umsAgentDebug(context, "LivePlaybackVideoActivityIntentTo", hashMap);
            } else {
                CrashReport.postCatchedException(new Exception("" + bundle));
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("logtype", "videoliveplayback");
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + context.getClass().getSimpleName());
                hashMap.put("bundle", "" + bundle);
                hashMap.put("exception", "" + Log.getStackTraceString(new Exception()));
                UmsAgentManager.umsAgentDebug(context, "LivePlaybackVideoActivityIntentTo", hashMap);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }
}
