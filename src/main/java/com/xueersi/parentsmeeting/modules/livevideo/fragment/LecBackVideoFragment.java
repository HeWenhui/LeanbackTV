package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.miracast.LetouLivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.miracast.MiracastLivebackBll;
import com.xueersi.parentsmeeting.modules.livevideo.miracast.MiracastPlaySucListener;
import com.xueersi.parentsmeeting.modules.livevideo.utils.BuryUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;
import com.xueersi.parentsmeeting.share.business.advert.AdvertSourceUtils;

/**
 * Created by linyuqiang on 2018/7/23.
 * 直播讲座的回放
 */
public class LecBackVideoFragment extends LiveBackVideoFragment implements LetouLivePlaybackMediaController.TvPlayClickListener, MiracastPlaySucListener {
    LecAdvertPlayBackBll lecAdvertPlayBackBll;
    MiracastLivebackBll miracastLivebackBll;
    LetouLivePlaybackMediaController mMediaController;
    private static final int REQUEST_MUST_PERMISSION = 1;
    private boolean shouldShowPage = false;
//    {
//        mLayoutVideo = R.layout.fram_live_stand_back_video;
//    }

    @Override
    protected void addBusiness(Activity activity) {
        super.addBusiness(activity);
        lecAdvertPlayBackBll = new LecAdvertPlayBackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(lecAdvertPlayBackBll);
        miracastLivebackBll = new MiracastLivebackBll(activity, liveBackBll);
        miracastLivebackBll.setMiracastPlaySucListener(this);
        liveBackBll.addBusinessBll(miracastLivebackBll);
    }

    protected void onNewIntent(Intent intent) {
        liveBackBll.onNewIntent(intent);
    }


    @Override
    protected LivePlaybackMediaController createLivePlaybackMediaController() {
        mMediaController = new LetouLivePlaybackMediaController(activity, liveBackPlayVideoFragment, mIsLand.get());
        mMediaController.setTvPlayBtnClickListener(this);
        return mMediaController;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mIsLand.get()) {
            if (miracastLivebackBll != null) {
                shouldShowPage = false;
                miracastLivebackBll.showPager(getContentView());

            }
        }
    }

    @Override
    protected void resultComplete() {
        if (lecAdvertPlayBackBll.getLecAdvertPager() == null) {
            super.resultComplete();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestReadPhoneStatesPermissions() {
        if (isAdded()){
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_MUST_PERMISSION);
        }

    }


    @Override
    public void onTvClick(View view) {
        if (mIsLand.get()) {
            shouldShowPage = true;
            changeLOrP();
        } else {
            if (miracastLivebackBll != null) {
                miracastLivebackBll.showPager(getContentView());
            }
        }
        BuryUtil.click(R.string.click_03_84_026, mVideoEntity.getLiveId());

    }

    @Override
    public void onTvPlaySuccess() {
        if (!mIsLand.get()) {
            changeLOrP();
        }
    }

    @Override
    public void onSearchRequestPromession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestReadPhoneStatesPermissions();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (miracastLivebackBll != null) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    miracastLivebackBll.startSearch();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        XrsBury.pageStartBury(getResources().getString(R.string.pv_02_85), mVideoEntity != null ? mVideoEntity.getLiveId() : "",
                vPlayer != null ? vPlayer.getCurrentPosition() : "",
                AdvertSourceUtils.getInstance().getSourceid(), AdvertSourceUtils.getInstance().getAdvertid());
    }

    @Override
    public void onPause() {
        super.onPause();
        XrsBury.pageEndBury(getResources().getString(R.string.pv_02_85),mVideoEntity != null ? mVideoEntity.getLiveId() : "",
                vPlayer != null ? vPlayer.getCurrentPosition() : "",
                AdvertSourceUtils.getInstance().getSourceid(), AdvertSourceUtils.getInstance().getAdvertid());
    }

}
