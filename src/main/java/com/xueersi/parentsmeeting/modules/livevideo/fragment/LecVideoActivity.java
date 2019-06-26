package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by linyuqiang on 2018/7/18.
 */

public class LecVideoActivity extends LiveVideoActivity {
    LectureLiveVideoFragment lectureLiveVideoFragment;

    @Override
    protected LiveVideoFragmentBase getFragment() {
        lectureLiveVideoFragment = new LectureLiveVideoFragment();
        return lectureLiveVideoFragment;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        lectureLiveVideoFragment.onNewIntent(intent);
    }


    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     */
    public static void intentTo(Activity context, Bundle bundle) {
        Intent intent = new Intent(context, LecVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
