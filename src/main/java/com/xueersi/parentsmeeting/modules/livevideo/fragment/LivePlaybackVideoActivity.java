package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandLiveVideoExperienceFragment;

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
        try {
            context.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
