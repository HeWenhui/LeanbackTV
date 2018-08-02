package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by linyuqiang on 2018/7/23.
 * 新直播回放
 */
public class LivePlaybackVideoActivity extends LiveBackVideoActivityBase {

    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        String where = getIntent().getStringExtra("where");
        if ("PublicLiveDetailActivity".equals(where)) {
            return new LecBackVideoFragment();
        }
        int pattern = getIntent().getIntExtra("pattern", 0);
        if (pattern == 2) {
            return new StandBackVideoFragment();
        }
        return new LiveBackVideoFragment();
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
        if (liveVideoFragmentBase instanceof LiveBackVideoFragment) {
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
        context.startActivityForResult(intent, requestCode);
    }

}
