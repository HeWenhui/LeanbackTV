package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertPlayBackBll;
import com.xueersi.parentsmeeting.share.business.advert.AdvertSourceUtils;

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

    @Override
    public void onResume() {
        super.onResume();
        XrsBury.pageStartBury(getResources().getString(R.string.pv_02_85), mVideoEntity != null ? mVideoEntity.getLiveId() : "",
                vPlayer.getCurrentPosition(),
                AdvertSourceUtils.getInstance().getSourceid(), AdvertSourceUtils.getInstance().getAdvertid());
    }

    @Override
    public void onPause() {
        super.onPause();
        XrsBury.pageEndBury(getResources().getString(R.string.pv_02_85),mVideoEntity != null ? mVideoEntity.getLiveId() : "",
                vPlayer.getCurrentPosition(),
                AdvertSourceUtils.getInstance().getSourceid(), AdvertSourceUtils.getInstance().getAdvertid());
    }
}
