package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;

/**
 * Created by linyuqiang on 2018/7/18.
 */

public class LecVideoActivity extends LiveVideoActivity {
    LectureLiveVideoFragment lectureLiveVideoFragment;

    @Override
    protected LiveVideoFragmentBase getFragment() {
        //测试大班整合
        try {
            String fname = "com.xueersi.parentsmeeting.modules.livebusiness.enter.LiveBusinessFragment";
            LiveVideoFragmentBase fragmentBase = (LiveVideoFragmentBase) Fragment.instantiate(this, fname);
            return fragmentBase;
        } catch (Exception e) {

        }
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
