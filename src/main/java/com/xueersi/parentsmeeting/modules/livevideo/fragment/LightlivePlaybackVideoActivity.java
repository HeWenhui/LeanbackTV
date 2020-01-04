package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

import java.util.HashMap;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.fragment
 * @ClassName: LightlivePlaybackVideoActivity
 * @Description: 轻直播
 * @Author: WangDe
 * @CreateDate: 2019/12/25 14:21
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/25 14:21
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightlivePlaybackVideoActivity extends LiveBackVideoActivityBase {
    private static String TAG = "LightlivePlaybackVideoActivity";

    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        return new LightLiveBackVideoFragment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (liveVideoFragmentBase instanceof LightLiveBackVideoFragment) {
            LightLiveBackVideoFragment liveVideoFragmentBase = (LightLiveBackVideoFragment) this.liveVideoFragmentBase;
            liveVideoFragmentBase.onRestart();
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
        Intent intent = new Intent(context, LightlivePlaybackVideoActivity.class);
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
            VideoLivePlayBackEntity serializable = (VideoLivePlayBackEntity) bundle.getSerializable(
                    "videoliveplayback");
            if (serializable != null) {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("logtype", "lightplayback");
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + context.getClass().getSimpleName());
                hashMap.put("bundle", "" + bundle);
                hashMap.put("liveid", "" + serializable.getLiveId());
                UmsAgentManager.umsAgentDebug(context, "LightlivePlaybackVideoActivityIntentTo", hashMap);
            } else {
                LiveCrashReport.postCatchedException(new Exception("" + bundle));
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("logtype", "errorplayback");
                hashMap.put("where", "" + where);
                hashMap.put("contextname", "" + context.getClass().getSimpleName());
                hashMap.put("bundle", "" + bundle);
                hashMap.put("exception", "" + Log.getStackTraceString(new Exception()));
                UmsAgentManager.umsAgentDebug(context, "LightlivePlaybackVideoActivityIntentTo", hashMap);
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }
}
