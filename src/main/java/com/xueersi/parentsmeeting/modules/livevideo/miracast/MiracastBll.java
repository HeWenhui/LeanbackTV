package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayer;
import com.hpplay.sdk.source.bean.DanmakuBean;
import com.hpplay.sdk.source.bean.DanmakuPropertyBean;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.hpplay.sdk.source.browse.api.LelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkSetting;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerServiceVideoUrlCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by: WangDe on 2019/2/25
 */
public class MiracastBll extends LiveBaseBll implements IBrowseListener, IConnectListener, ILelinkPlayerListener, NoticeAction {
    ILelinkServiceManager lelinkServiceManager;
    LelinkPlayer leLinkPlayer;
    private MiracastPager miracastPager;
    private RelativeLayout rlLiveMessageContent;
    List<LelinkServiceInfo> devList;
    private String url;
    private DanmakuBean danmakuBean;
    IMiracastState iMiracastState;
    PlayerService playerService;
    private ViewGroup mRootView;

    public MiracastBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }


    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        playerService = (PlayerService) mLiveBll.getBusinessShareParam("vPlayer");
        if (playerService != null) {
            playerService.setPlayServiceVideoUrlCallback(new PlayerServiceVideoUrlCallback() {
                @Override
                public void onServerList(int cur, int total, List<String> addrLists) {
                    if (addrLists != null && !addrLists.isEmpty()) {
                        String s = addrLists.get(0);
                        logger.i("hpplay url " + s);
                        miracastPager.setUrl(s);
                    }
                }
            });
        }

    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        LelinkSetting lelinkSetting = new LelinkSetting.LelinkSettingBuilder("10494", "699d23d680136ee1d3e7d9cbf767ac0a").build();
//        LelinkSetting lelinkSetting = new LelinkSetting.LelinkSettingBuilder("10495", "42417c9f85b4842c026e2240eec88ae2").build();
        lelinkServiceManager = LelinkServiceManager.getInstance(mContext.getApplicationContext());
        lelinkServiceManager.setLelinkSetting(lelinkSetting);
        lelinkServiceManager.setDebug(true);
        lelinkServiceManager.setOption(IAPI.OPTION_5, false);
        lelinkServiceManager.setOnBrowseListener(this);
        leLinkPlayer = new LelinkPlayer(mContext.getApplicationContext());
        leLinkPlayer.setPlayerListener(this);
        leLinkPlayer.setConnectListener(this);
        miracastPager = new MiracastPager(mContext);
        miracastPager.setLeLinkPlayer(leLinkPlayer);
        miracastPager.setILelinkServiceManager(lelinkServiceManager);
        iMiracastState = miracastPager.getiMiracastState();
        super.onLiveInited(getInfo);
        // showPager();

    }

    public void showPager(ViewGroup rootView) {
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRootView = rootView;
        if (rootView != null && miracastPager != null) {
            rootView.removeView(miracastPager.getRootView());
            rootView.addView(miracastPager.getRootView(), params);
        }

    }

    public void hildPage() {
        if (mRootView != null) {
            mRootView.removeView(miracastPager.getRootView());
        }
    }

    @Override
    public void onConnect(LelinkServiceInfo lelinkServiceInfo, int extra) {
        logger.i("hpplay 连接成功： info name:" + lelinkServiceInfo.getName() + " ip:" + lelinkServiceInfo.getIp());

        XESToastUtils.showToast("连接成功");
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
            text = lelinkServiceInfo.getName() + "连接失败";
        }

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

        XESToastUtils.showToast(text);
        iMiracastState.onDisConnect();
    }

    @Override
    public void onLoading() {
        logger.i("hpplay onLoading");
       // XESToastUtils.showToast("onLoading");
    }

    @Override
    public void onStart() {
        logger.i("hpplay onStart");
        XESToastUtils.showToast("投屏成功");
        hildPage();
        iMiracastState.onStart();
        super.onStart();
    }


    @Override
    public void onCompletion() {

        logger.i("hpplay onCompletion");
    }

    @Override
    public void onSeekComplete(int i) {

    }

    @Override
    public void onInfo(int what, int extra) {

    }

    @Override
    public void onError(int what, int extra) {

    }

    @Override
    public void onVolumeChanged(float percent) {
        logger.i("hpplay onVolumeChanged");
    }

    @Override
    public void onPositionUpdate(long duration, long position) {
        logger.i("hpplay onPositionUpdate");
    }

    List<LelinkServiceInfo> mList = new ArrayList<>();

    @Override
    public void onBrowse(int resultCode, final List<LelinkServiceInfo> list) {
        logger.i("hpplay 搜索成功" + list.toString());
        if (IBrowseListener.BROWSE_SUCCESS == resultCode) {
            if (!list.isEmpty()) {
                synchronized (this) {
                    miracastPager.setmLinklist(list);
                }
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

/*    @Override
    public void onDestory() {
        leLinkPlayer.stop();
        leLinkPlayer.release();
        super.onDestory();
    }*/

    @Override
    public void onDestroy() {
       /* leLinkPlayer.stop();
        leLinkPlayer.release();*/
        super.onDestroy();
    }

    public void sendDanmaku(String text, String color) {
        if (danmakuBean == null) {
            danmakuBean = new DanmakuBean();
        }
        if (leLinkPlayer != null) {
            logger.i("hpplay send danmuku");
            danmakuBean.setContent(text);
            danmakuBean.setFontColor(color);
//            danmakuBean.setDisplayTime(1000);
            danmakuBean.setImmShow(true);
            danmakuBean.setFontSize(50);
            danmakuBean.setColumSpace(5);
            leLinkPlayer.sendDanmaku(danmakuBean);
        }

    }

    public void sendDanmakuProperty() {
        DanmakuPropertyBean mDanmakuProperty = new DanmakuPropertyBean();
        mDanmakuProperty.setSwitch(true);
        mDanmakuProperty.setLines(DanmakuPropertyBean.Lines.LINES_1);
        mDanmakuProperty.setSpeed(DanmakuPropertyBean.Speed.SPEED_1);
        leLinkPlayer.sendDanmakuProperty(mDanmakuProperty);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.READPACAGE:
                sendDanmaku("红包来了，请在客户端查收", "#FFFFFFFF");
                break;
            case XESCODE.UNDERSTANDT:
                logger.i("hpplay 懂了么");
                sendDanmakuProperty();
                sendDanmaku("老师发送懂了吗，请在客户端查看", "#FFFFFFFF");
                break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.READPACAGE, XESCODE.UNDERSTANDT};
    }
}
