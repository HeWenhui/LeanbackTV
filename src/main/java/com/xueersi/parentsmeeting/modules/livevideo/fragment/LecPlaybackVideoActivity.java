package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Fragment;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;

/**
 * Created by linyuqiang on 2018/7/23.
 */

public class LecPlaybackVideoActivity extends LiveBackVideoActivityBase {


    private static final String TAG = "LecPlaybackVideoActivity";

    @Override
    protected LiveBackVideoFragmentBase getFragment() {

     /*   if(isBigLive()){
            try {
                String fname = "com.xueersi.parentsmeeting.modules.livebusiness.enter.LiveBusinessBackFragment";
                LiveBackVideoFragmentBase fragmentBase = (LiveBackVideoFragmentBase) Fragment.instantiate(this, fname);
                return fragmentBase;
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(TAG, e);
            }

        }*/
        return new LiveBackVideoFragment();

    }

  /*  private boolean isBigLive() {
        // TODO: 2019-08-20 返回是否是大班整合 回放
        boolean isBigLive = getIntent().getBooleanExtra("isBigLive",false);
        return true;
    }*/
}
