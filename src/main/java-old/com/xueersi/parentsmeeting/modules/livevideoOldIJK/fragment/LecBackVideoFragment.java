package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.app.Activity;
import android.content.Intent;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business.LecAdvertPlayBackBll;

/**
 * Created by linyuqiang on 2018/7/23.
 * 直播讲座的回放
 */
public class LecBackVideoFragment extends LiveBackVideoFragment {
    LecAdvertPlayBackBll lecAdvertPlayBackBll;
//    {
//        mLayoutVideo = R.layout.fram_live_stand_back_video;
//    }

    @Override
    protected void addBusiness(Activity activity) {
        super.addBusiness(activity);
        lecAdvertPlayBackBll = new LecAdvertPlayBackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(lecAdvertPlayBackBll);
    }

    protected void onNewIntent(Intent intent) {
        liveBackBll.onNewIntent(intent);
    }

    @Override
    protected void resultComplete() {
        if (lecAdvertPlayBackBll.getLecAdvertPager() == null) {
            super.resultComplete();
        }
    }
}
