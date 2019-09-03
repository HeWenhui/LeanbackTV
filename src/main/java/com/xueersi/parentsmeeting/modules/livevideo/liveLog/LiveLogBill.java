package com.xueersi.parentsmeeting.modules.livevideo.liveLog;


import android.content.Context;

import com.hwl.bury.xrsbury.BuryPublicParam;
import com.hwl.log.LogConfig;
import com.hwl.log.xrsLog.UpdateParamInterface;
import com.hwl.log.xrsLog.XrsLogPublicParam;
import com.xueersi.common.base.XueErSiRunningEnvironment;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.LiveRemoteConfigInfo;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.NetUtil;
import com.xueersi.common.logerhelper.LogBill;
import com.xueersi.common.logerhelper.network.PingInfo;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.framework.utils.ListUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播监控日志业务
 */
public class LiveLogBill {


    private static final String LOG_LIVE_LOG_NAME = "log_live_v1";

    public Context context;
    private static LiveLogBill mInstance;
    static LiveLogEntity param = new LiveLogEntity();
    static String url;
    static MyUserInfoEntity myUserInfoEntity;

    public static LiveRemoteConfigInfo mLiveRemoteConfigInfo;

    public static LiveLogBill getInstance() {
        if (mInstance == null) {
            synchronized (LogBill.class) {
                if (mInstance == null) {
                    mInstance = new LiveLogBill(XueErSiRunningEnvironment.sAppContext);
                }
            }
        }
        return mInstance;
    }


    public static LiveLogBill getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LogBill.class) {
                if (mInstance == null) {
                    mInstance = new LiveLogBill(context);
                }
            }
        }
        return mInstance;
    }


    private LiveLogBill(Context context) {
        this.context = context;

    }

    /**
     * 更新参数
     *
     * @param entity
     */
    public void updateParam(LiveLogEntity entity) {
        param = entity;
    }

    public void setCurrUrl(String url) {
        this.url = url;
    }

    /**
     * 设置状态
     *
     * @param status 0:正常 1：不正常
     */
    public void setStatus(int status) {
        if (param != null) {
            param.state = status;
        }

    }

    /**
     * 设置直播ID
     *
     * @param liveId
     */
    public void setLiveId(String liveId) {

        if (param != null) {
            param.liveid = liveId;
        }

    }

    /**
     * 开始直播监控日志
     */
    public void initLiveLog() {

        LiveLogSendLogRunnable logSendLogRunnable = new LiveLogSendLogRunnable();
        logSendLogRunnable.setPath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                + File.separator + LOG_LIVE_LOG_NAME + android.os.Process.myPid());

        LogConfig apmConfig = new LogConfig.Builder()
                .setCachePath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                        + File.separator + "LOG_LIVE_LOG_NAME" + File.separator + android.os.Process.myPid())
                .setPath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                        + File.separator + LOG_LIVE_LOG_NAME + android.os.Process.myPid())
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .setDay(5)
                .setMaxFile(10)
                .setMinSDCard(10)
                .build();

        LiveLog.init(apmConfig, logSendLogRunnable, 5);
        LiveLog.setUpParamInterface(new UpdateParamInterface() {
            @Override
            public XrsLogPublicParam getXrsLogPublicParam() {

                BuryPublicParam buryPublicParam = new BuryPublicParam();
                buryPublicParam.ver = AppUtils.getAppVersionName(XueErSiRunningEnvironment.sAppContext);
                return buryPublicParam;
            }
        });

        mLiveRemoteConfigInfo = AppBll.getAppBillInstance().getAppRemoteConfig(context).liveRemoteConfigInfo;
    }


    /**
     * 开启直播监控日志(轮循)
     */
    public void startLog() {
        LiveLog.startLog();
    }

    /**
     * 关闭直播监控日志(轮循)
     */
    public void stopLog() {
        LiveLog.stopLog();
    }

    /**
     * 打开app 采集日志
     */
    public void openAppLiveLog() {

        LiveLogEntity log = new LiveLogEntity();
        log.pri = "2";
        if (myUserInfoEntity != null) {
            log.psId = myUserInfoEntity.getPsimId();
        }
        if (myUserInfoEntity == null) {
            myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        }
        if (myUserInfoEntity != null) {
            log.psId = myUserInfoEntity.getPsimId();
        }
        List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
        if (!ListUtil.isEmpty(domainList)) {
            PingInfo info = NetUtil.ping(domainList.get(0));
            Pridata pridata = new Pridata();
            pridata.ping = new HashMap<String, PingInfo>();
            pridata.ping.put(domainList.get(0), info);
            log.pridata = pridata;

            if (LiveLogBill.param != null) {
                log.liveid = LiveLogBill.param.liveid;
            }

            Map<String, String> pingMap = new HashMap<String, String>();
            pingMap.put(info.host, info.ip);
            pridata.dnsinfo = pingMap;
        }
        LiveLog.log(log);
        LiveLog.sendLog();

    }

    /**
     * 打开直播日志
     */
    public void openLiveLog() {

        LiveLogEntity log = new LiveLogEntity();
        log.pri = "2";
        if (myUserInfoEntity != null) {
            log.psId = myUserInfoEntity.getPsimId();
        }
        if (myUserInfoEntity == null) {
            myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        }
        if (myUserInfoEntity != null) {
            log.psId = myUserInfoEntity.getPsimId();
        }
        List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;

        if (!ListUtil.isEmpty(domainList)) {
            PingInfo info = NetUtil.ping(domainList.get(0));
            Pridata pridata = new Pridata();
            pridata.ping = new HashMap<String, PingInfo>();
            pridata.ping.put(domainList.get(0), info);
            log.pridata = pridata;

            if (LiveLogBill.param != null) {
                log.liveid = LiveLogBill.param.liveid;
            }

            Map<String, String> pingMap = new HashMap<String, String>();
            pingMap.put(info.host, info.ip);
            pridata.dnsinfo = pingMap;
        }

        LiveLog.log(log);
        LiveLog.sendLog();
    }

    /**
     * 直播卡顿log
     */
    public void liveANRLog() {

        LiveLogEntity log = new LiveLogEntity();
        if (LiveLogBill.param != null) {
            log.liveid = LiveLogBill.param.liveid;
        }
        if (myUserInfoEntity == null) {
            myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        }
        if (myUserInfoEntity != null) {
            log.psId = myUserInfoEntity.getPsimId();
        }
        log.pri = "3";
        List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
        if (!ListUtil.isEmpty(domainList)) {
            PingInfo info = NetUtil.ping(domainList.get(0));
            Pridata pridata = new Pridata();
            pridata.ping = new HashMap<String, PingInfo>();
            pridata.ping.put(domainList.get(0), info);
            log.pridata = pridata;

            if (LiveLogBill.param != null) {
                log.liveid = LiveLogBill.param.liveid;
            }

            Map<String, String> pingMap = new HashMap<String, String>();
            pingMap.put(info.host, info.ip);
            pridata.dnsinfo = pingMap;
        }
        LiveLog.log(log);
        LiveLog.sendLog();
    }
}
