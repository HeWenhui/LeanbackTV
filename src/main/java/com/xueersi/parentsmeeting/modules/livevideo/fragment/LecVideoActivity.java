package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by linyuqiang on 2018/7/18.
 */

public class LecVideoActivity extends LiveVideoActivity {
    LectureLiveVideoFragment lectureLiveVideoFragment;
    LiveVideoFragmentBase videoFragmentBase;

    @Override
    protected LiveVideoFragmentBase getFragment() {
        lectureLiveVideoFragment = new LectureLiveVideoFragment();
        videoFragmentBase = new LightLiveVideoFragment();
        return videoFragmentBase;
//        return lectureLiveVideoFragment;
    }

    /**
     * 判断是否 是大班整合直播间
     * @return
     */
    private  boolean isBigLive(){
        Bundle bundle = getIntent().getExtras();
        boolean result = bundle!= null && bundle.getBoolean("isBigLive",false);
        return result;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        lectureLiveVideoFragment.onNewIntent(intent);
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
