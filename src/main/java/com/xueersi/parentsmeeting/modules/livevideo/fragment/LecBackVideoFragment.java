package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.HashMap;
import java.util.Map;

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
