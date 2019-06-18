package com.xueersi.parentsmeeting.modules.livevideoOldIJK.video;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.framework.utils.DeviceUtils;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.video.LivePlayLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

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
    protected Logger logger = LoggerFactory.getLogger(TAG);
    private LiveHttpManager mHttpManager;
    /** 直播帧数统计 */
    private LivePlayLog livePlayLog;
    private AtomicInteger mOpenCount = new AtomicInteger(0);
    private LogToFile mLogtf;
    long openStartTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LiveGetInfo mGetInfo;
    private Context mContext;
    LiveBll2 liveBll;
    private PlayServerEntity mServer;
    private PlayServerEntity.PlayserverEntity playserverEntity;

    LiveVideoReportBll(Activity activity, LiveBll2 liveBll) {
        this.liveBll = liveBll;
        mLogtf = new LogToFile(activity, TAG);
    }

    public void setServer(PlayServerEntity server) {
        this.mServer = server;
    }

    public void setPlayserverEntity(PlayServerEntity.PlayserverEntity playserverEntity) {
        this.playserverEntity = playserverEntity;
    }

    public void setLivePlayLog(LivePlayLog livePlayLog) {
        this.livePlayLog = livePlayLog;
    }

    public void onLiveInit(LiveGetInfo getInfo, LiveTopic liveTopic) {
        this.mGetInfo = getInfo;
    }

    public void setHttpManager(LiveHttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    public VPlayerCallBack.SimpleVPlayerListener getVideoListener() {
        return mVideoListener;
    }

    private VPlayerCallBack.SimpleVPlayerListener mVideoListener = new VPlayerCallBack.SimpleVPlayerListener() {
        long bufferStartTime;
        boolean isOpenSuccess = false;

        @Override
        public void onOpenStart() {
            isOpenSuccess = false;
            mOpenCount.set(mOpenCount.get() + 1);
            openStartTime = System.currentTimeMillis();
            mLogtf.d("onOpenStart");
            if (livePlayLog != null) {
                livePlayLog.onOpenStart();
            }
        }

        @Override
        public void onOpenSuccess() {
            isOpenSuccess = true;
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenSuccess:openTime=" + openTime);
            streamReport(LiveVideoReportBll.MegId.MEGID_12102, mGetInfo.getChannelname(), openTime);
            if (livePlayLog != null) {
                livePlayLog.onOpenSuccess();
            }
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            if (isOpenSuccess) {
                MegId megId = MegId.MEGID_12103;
                megId.detail = "fail " + LivePlayLog.getErrorCodeInt(arg2) + " ";
                streamReport(megId, mGetInfo.getChannelname(), -1);
            }
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenFailed:openTime=" + openTime + ",arg2=" + arg2 + ",NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
            if (livePlayLog != null) {
                livePlayLog.onOpenFailed(arg1, arg2);
            }
        }

        @Override
        public void onBufferStart() {
            bufferStartTime = System.currentTimeMillis();
            mLogtf.d("onBufferStart:bufferCount=" + ",NetWorkState=" +
                    NetWorkHelper
                            .getNetWorkState(mContext));
            if (livePlayLog != null) {
                livePlayLog.onBufferStart();
            }
        }

        @Override
        public void onBufferComplete() {
            long bufferTime = System.currentTimeMillis() - bufferStartTime;
            mLogtf.d("onBufferComplete:bufferTime=" + bufferTime);
            if (livePlayLog != null) {
                livePlayLog.onBufferComplete();
            }
        }

        @Override
        public void onPlaybackComplete() {
            mLogtf.d("onPlaybackComplete:completeCount=" + liveBll.getModeTeacher() + "," +
                    "NetWorkState=" + NetWorkHelper.getNetWorkState(mContext));
            if (livePlayLog != null) {
                livePlayLog.onPlaybackComplete();
            }
        }

        @Override
        public void onPlayError() {
            super.onPlayError();
            if (livePlayLog != null) {
                livePlayLog.onPlayError();
            }
        }

        @Override
        public void onSeekComplete() {
            super.onSeekComplete();
            if (livePlayLog != null) {
                livePlayLog.onSeekComplete();
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
            if (livePlayLog != null) {
                String cpuName = livePlayLog.getCpuName();
                String memsize = livePlayLog.getMemsize();
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
                logger.i("streamReport:onFailure=", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.i("streamReport:onResponse:response=" + response.message());
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
                logger.i("live_report_play_duration:onFailure=", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.i("live_report_play_duration:onResponse:response=" + response.message());
            }
        });
    }

    public void onDestory() {

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
