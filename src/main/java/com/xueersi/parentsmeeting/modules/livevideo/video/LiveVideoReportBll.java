package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.framework.utils.DeviceUtils;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBllL;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.TotalFrameStat;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lyqai on 2018/6/23.
 */

public class LiveVideoReportBll {
    private final String TAG = "LiveVideoReportBll";
    private LiveHttpManager mHttpManager;
    /** 直播帧数统计 */
    private TotalFrameStat totalFrameStat;
    private AtomicInteger mOpenCount = new AtomicInteger(0);
    private LogToFile mLogtf;
    long openStartTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LiveGetInfo mGetInfo;
    private Context mContext;
    LiveBllL liveBll;
    private PlayServerEntity mServer;
    private PlayServerEntity.PlayserverEntity playserverEntity;

    LiveVideoReportBll() {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
    }

    public void setServer(PlayServerEntity server) {
        this.mServer = server;
    }

    public void setPlayserverEntity(PlayServerEntity.PlayserverEntity playserverEntity) {
        this.playserverEntity = playserverEntity;
    }

    public void setTotalFrameStat(TotalFrameStat totalFrameStat) {
        this.totalFrameStat = totalFrameStat;
    }

    public void onLiveInit(LiveGetInfo getInfo, LiveTopic liveTopic) {
        this.mGetInfo = getInfo;
    }

    public void setHttpManager(LiveHttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    public PlayerService.SimpleVPlayerListener getVideoListener() {
        return mVideoListener;
    }

    private PlayerService.SimpleVPlayerListener mVideoListener = new PlayerService.SimpleVPlayerListener() {
        long bufferStartTime;
        boolean isOpenSuccess = false;

        @Override
        public void onOpenStart() {
            isOpenSuccess = false;
            mOpenCount.set(mOpenCount.get() + 1);
            openStartTime = System.currentTimeMillis();
            mLogtf.d("onOpenStart");
            if (totalFrameStat != null) {
                totalFrameStat.onOpenStart();
            }
        }

        @Override
        public void onOpenSuccess() {
            isOpenSuccess = true;
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenSuccess:openTime=" + openTime);
            streamReport(LiveVideoReportBll.MegId.MEGID_12102, mGetInfo.getChannelname(), openTime);
            if (totalFrameStat != null) {
                totalFrameStat.onOpenSuccess();
            }
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            if (isOpenSuccess) {
                streamReport(LiveVideoReportBll.MegId.MEGID_12103, mGetInfo.getChannelname(), -1);
            }
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenFailed:openTime=" + openTime + ",arg2=" + arg2 + ",NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
            if (totalFrameStat != null) {
                totalFrameStat.onOpenFailed(arg1, arg2);
            }
        }

        @Override
        public void onBufferStart() {
            bufferStartTime = System.currentTimeMillis();
            mLogtf.d("onBufferStart:bufferCount=" + ",NetWorkState=" +
                    NetWorkHelper
                            .getNetWorkState(mContext));
        }

        @Override
        public void onBufferComplete() {
            long bufferTime = System.currentTimeMillis() - bufferStartTime;
            mLogtf.d("onBufferComplete:bufferTime=" + bufferTime);
        }

        @Override
        public void onPlaybackComplete() {
            mLogtf.d("onPlaybackComplete:completeCount=" + liveBll.getModeTeacher() + "," +
                    "NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
            if (totalFrameStat != null) {
                totalFrameStat.onPlaybackComplete();
            }
        }

        @Override
        public void onPlayError() {
            super.onPlayError();
            if (totalFrameStat != null) {
                totalFrameStat.onPlayError();
            }
        }
    };

    public void streamReport(LiveVideoReportBll.MegId msgid, String channelname, long connsec) {
        if (mServer == null || playserverEntity == null) {
            return;
        }
        HttpRequestParams entity = new HttpRequestParams();
        if (LiveVideoReportBll.MegId.MEGID_12107 == msgid) {
            boolean isPresent = liveBll.isPresent();
            if (!isPresent) {
                return;
            }
        } else if (LiveVideoReportBll.MegId.MEGID_12102 == msgid) {
            if (totalFrameStat != null) {
                String cpuName = totalFrameStat.getCpuName();
                String memsize = totalFrameStat.getMemsize();
                String ua = Build.VERSION.SDK_INT + ";" + cpuName + ";" + memsize;
                entity.addBodyParam("UA", ua);
            }
        }
        String url = mGetInfo.getLogServerUrl();
        entity.addBodyParam("msgid", msgid.msgid);
        entity.addBodyParam("userid", mGetInfo.getStuId());
        entity.addBodyParam("username", mGetInfo.getUname());
        entity.addBodyParam("channelname", channelname);
        entity.addBodyParam("ccode", mServer.getCcode());
        entity.addBodyParam("pcode", mServer.getPcode());
        entity.addBodyParam("acode", "");
        entity.addBodyParam("icode", mServer.getIcode());
        entity.addBodyParam("servercc", playserverEntity.getCcode());
        entity.addBodyParam("serverpc", playserverEntity.getPcode());
        entity.addBodyParam("serverac", playserverEntity.getAcode());
        entity.addBodyParam("serveric", playserverEntity.getIcode());
        entity.addBodyParam("servergroup", playserverEntity.getGroup());
        if (StringUtils.isEmpty(playserverEntity.getIpAddress())) {
            entity.addBodyParam("server", playserverEntity.getAddress());
        } else {
            entity.addBodyParam("server", playserverEntity.getIpAddress());
        }
        entity.addBodyParam("appname", mServer.getAppname());
        entity.addBodyParam("reconnnum", "" + (mOpenCount.get() - 1));
        entity.addBodyParam("connsec", "" + (connsec / 1000));
        try {
            if (DeviceUtils.isTablet(mContext)) {
                entity.addBodyParam("cfrom", "androidpad");
            } else {
                entity.addBodyParam("cfrom", "android");
            }
        } catch (Exception e) {
            entity.addBodyParam("cfrom", "android");
        }
        if (playserverEntity.isUseFlv()) {
            entity.addBodyParam("detail", msgid.detail + " flv");
        } else {
            entity.addBodyParam("detail", msgid.detail);
        }
        mHttpManager.sendGetNoBusiness(url, entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.i(TAG, "streamReport:onFailure=", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Loger.i(TAG, "streamReport:onResponse:response=" + response.message());
            }
        });
    }

    public void live_report_play_duration(String channelname, long cost, PlayServerEntity.PlayserverEntity
            playserverEntity, String detail) {
        if (this.playserverEntity == null) {
            return;
        }
        String url = mGetInfo.getGslbServerUrl();
        HttpRequestParams entity = new HttpRequestParams();
        entity.addBodyParam("cmd", "live_report_play_duration");
        entity.addBodyParam("userid", mGetInfo.getStuId());
        entity.addBodyParam("username", mGetInfo.getUname());
        entity.addBodyParam("channelname", channelname);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        date.setTime(openStartTime);
        entity.addBodyParam("start", "" + dateFormat.format(date));
        entity.addBodyParam("cost", "" + (cost / 1000));
        entity.addBodyParam("ccode", mServer.getCcode());
        entity.addBodyParam("pcode", mServer.getPcode());
        entity.addBodyParam("acode", "");
        entity.addBodyParam("icode", mServer.getIcode());
        entity.addBodyParam("servercc", this.playserverEntity.getCcode());
        entity.addBodyParam("serverpc", this.playserverEntity.getPcode());
        entity.addBodyParam("serverac", this.playserverEntity.getAcode());
        entity.addBodyParam("serveric", this.playserverEntity.getIcode());
        try {
            if (DeviceUtils.isTablet(mContext)) {
                entity.addBodyParam("cfrom", "androidpad");
            } else {
                entity.addBodyParam("cfrom", "android");
            }
        } catch (Exception e) {
            entity.addBodyParam("cfrom", "android");
        }
        entity.addBodyParam("detail", detail);
        mHttpManager.sendGetNoBusiness(url, entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.i(TAG, "live_report_play_duration:onFailure=", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Loger.i(TAG, "live_report_play_duration:onResponse:response=" + response.message());
            }
        });
    }

    public void onDestory() {
        if (totalFrameStat != null) {
            totalFrameStat.destory();
        }
    }

    public enum MegId {
        MEGID_12102("12102", "startplay"), MEGID_12103("12103", "fail"),
        MEGID_12107("12107", "bufreconnect"), MEGID_12137("12137", "bufreconnect"),
        MEGID_12130("12130", "delay");
        public String msgid;
        public String detail;

        MegId(String msgid, String detail) {
            this.msgid = msgid;
            this.detail = detail;
        }
    }
}
