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
            LiveBackVideoFragment liveBackVideoFragment = (LiveBackVideoFragment) liveVideoFragmentBase;
            liveBackVideoFragment.onRestart();
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
