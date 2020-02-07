package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hpplay.common.utils.NetworkUtil;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayer;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.hpplay.sdk.source.browse.api.LelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkSetting;
import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.utils.BuryUtil;

import java.util.HashMap;
import java.util.List;

import static com.xueersi.lib.framework.are.ContextManager.getApplication;
import static com.xueersi.parentsmeeting.modules.livevideo.miracast.MiracastPager.FROM_PLAYBACK;

/**
 * Created by: WangDe on 2019/2/25
 */
public class MiracastLivebackBll extends LiveBackBaseBll implements IBrowseListener, IConnectListener, ILelinkPlayerListener {

    ILelinkServiceManager lelinkServiceManager;
    LelinkPlayer leLinkPlayer;
    private MiracastPager miracastPager;
    private RelativeLayout rlLiveMessageContent;
    List<LelinkServiceInfo> devList;
    private String url;
    IMiracastState iMiracastState;

    private ViewGroup mFragmentRootView;

    private MiracastPlaySucListener miracastPlaySucListener;

    private boolean isInit = false;

    public MiracastLivebackBll(Activity context, LiveBackBll liveBll) {
        super(context, liveBll);
    }


    @Override
    public void initView() {
        super.initView();

    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        logger.i("hpplay MiracastLivebackBll onCreate");


    }

    public void initLetouSdk() {
        //LelinkSetting lelinkSetting = new LelinkSetting.LelinkSettingBuilder("10494", "699d23d680136ee1d3e7d9cbf767ac0a").build();
        LelinkSetting lelinkSetting = new LelinkSetting.LelinkSettingBuilder("10495", "42417c9f85b4842c026e2240eec88ae2").build();
        lelinkServiceManager = LelinkServiceManager.getInstance(mContext.getApplicationContext());
        lelinkServiceManager.setLelinkSetting(lelinkSetting);
        lelinkServiceManager.setDebug(true);
        lelinkServiceManager.setOption(IAPI.OPTION_5, false);
        lelinkServiceManager.setOnBrowseListener(this);
        leLinkPlayer = new LelinkPlayer(mContext.getApplicationContext());
        leLinkPlayer.setPlayerListener(this);
        leLinkPlayer.setConnectListener(this);
        miracastPager = new MiracastPager(mContext, FROM_PLAYBACK, mVideoEntity.getLiveId());
        miracastPager.setLeLinkPlayer(leLinkPlayer);
        miracastPager.setILelinkServiceManager(lelinkServiceManager);
        String videoPath = mVideoEntity.getVideoPath();
        logger.i("hpplay MiracastLivebackBll url " + videoPath);
        setUrl(videoPath);
        iMiracastState = miracastPager.getiMiracastState();
        isInit = true;
    }

    public void showPager(ViewGroup rootView) {
        if (!isInit) {
            initLetouSdk();
        }
        logger.i("hpplay MiracastLivebackBll showPager");
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (rootView != null && miracastPager != null) {
            mFragmentRootView = rootView;
            rootView.removeView(miracastPager.getRootView());
            rootView.addView(miracastPager.getRootView(), params);
            if (NetworkUtil.isWiFiOpen(mContext)) {
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ContextCompat.checkSelfPermission(getApplication(),
                                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_DENIED
                                && ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                            miracastPager.startSearch();
                            miracastPager.setNetworkStatus(true);
                        } else {
                            if (miracastPlaySucListener != null) {
                                miracastPlaySucListener.onSearchRequestPromession();
                            }
                        }

                    }
                }, 1000);
            } else {
                miracastPager.setNetworkStatus(false);
            }

        }
    }

    public void hildPage() {
        if (miracastPager != null && mFragmentRootView != null) {
            mFragmentRootView.post(new Runnable() {
                @Override
                public void run() {
                    mFragmentRootView.removeView(miracastPager.getRootView());
                    miracastPager.stopSearch();
                }
            });

        }
    }

    @Override
    public void onConnect(LelinkServiceInfo lelinkServiceInfo, int extra) {
        logger.i("hpplay 连接成功： info name:" + lelinkServiceInfo.getName() + " ip:" + lelinkServiceInfo.getIp());
        XESToastUtils.showToast("连接成功");
        BuryUtil.show(R.string.show_03_84_024, mVideoEntity.getLiveId());
        iMiracastState.onConnect();

    }

    @Override
    public void onDisconnect(LelinkServiceInfo lelinkServiceInfo, int what, int extra) {
        String text = null;
        if (IConnectListener.CONNECT_INFO_DISCONNECT == what) {
            logger.i("hpplay 连接断开");
            text = lelinkServiceInfo.getName() + "连接断开";
        } else if (IConnectListener.CONNECT_ERROR_FAILED == what) {
            logger.i("hpplay 连接失败");
            if (extra == IConnectListener.CONNECT_ERROR_IO) {
                text = lelinkServiceInfo.getName() + "连接失败";
            } else if (extra == IConnectListener.CONNECT_ERROR_IM_WAITTING) {
                text = lelinkServiceInfo.getName() + "等待确认";
            } else if (extra == IConnectListener.CONNECT_ERROR_IM_REJECT) {
                text = lelinkServiceInfo.getName() + "连接拒绝";
            } else if (extra == IConnectListener.CONNECT_ERROR_IM_TIMEOUT) {
                text = lelinkServiceInfo.getName() + "连接超时";
            } else if (extra == IConnectListener.CONNECT_ERROR_IM_BLACKLIST) {
                text = lelinkServiceInfo.getName() + "连接黑名单";
            }
        }


        if (!TextUtils.isEmpty(text)) {
            XESToastUtils.showToast(text);
        }

        iMiracastState.onDisConnect();
    }

    @Override
    public void onLoading() {
        logger.i("hpplay onLoading");
        //XESToastUtils.showToast("loading");
    }

    @Override
    public void onStart() {
        logger.i("hpplay onStart");
        XESToastUtils.showToast("投屏成功");
        hildPage();
        if (miracastPlaySucListener != null) {
            miracastPlaySucListener.onTvPlaySuccess();
        }
        iMiracastState.onStart();
        BuryUtil.show(R.string.show_03_84_025, mVideoEntity.getLiveId());
    }


    @Override
    public void onPause() {
        iMiracastState.onPause();

    }

    @Override
    public void onCompletion() {

        logger.i("hpplay onCompletion");
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onSeekComplete(int i) {

    }

    @Override
    public void onInfo(int i, int i1) {

    }

    @Override
    public void onError(int i, int i1) {
        XESToastUtils.showToast("投屏失败");
    }

    @Override
    public void onVolumeChanged(float v) {
        logger.i("hpplay onVolumeChanged");
    }

    @Override
    public void onPositionUpdate(long l, long l1) {
        logger.i("hpplay onPositionUpdate");
    }

    @Override
    public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
        logger.i("hpplay 搜索成功" + list.toString());
        if (IBrowseListener.BROWSE_SUCCESS == resultCode) {
            if (!list.isEmpty()) {
                // XESToastUtils.showToast( "搜索成功");
                miracastPager.setmLinklist(list);
            }
        } else {
            logger.i("hpplay 搜索失败，Auth错误，请检查您的网络设置或AppId和AppSecret");
            XESToastUtils.showToast("搜索失败");
        }
    }

    public void setUrl(String url) {
        logger.i("hpplay url:" + url);
        if (url != null) {
            miracastPager.setUrl(url);
            this.url = url;
        }

    }

    public void setMiracastPlaySucListener(MiracastPlaySucListener sucListener) {
        miracastPlaySucListener = sucListener;
    }

    public void startSearch() {
        if (miracastPager != null) {
            miracastPager.startSearch();
        }
    }
}
