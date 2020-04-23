package com.xueersi.parentsmeeting.modules.livevideo.liveLog;


import android.content.Context;
import android.os.Process;


import com.google.gson.Gson;
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
import com.xueersi.common.logerhelper.network.PingInfo;
import com.xueersi.lib.analytics.umsagent.CommonUtil;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.framework.utils.ListUtil;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog.LiveBusiLog;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog.LiveBusiLogSendLogRunnable;

import net.grandcentrix.tray.core.ItemNotFoundException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播监控日志业务
 */
public class LiveLogBill {


    private static final String LOG_LIVE_LOG_NAME = "log_live_apm_v1";

    private static final String LOG_LIVE_BUSI_LOG_NAME = "log_live_busi_v1";

    public Context context;
    private static LiveLogBill mInstance;
    static LiveLogEntity param = new LiveLogEntity();
    static String url;
    static MyUserInfoEntity myUserInfoEntity;

    public static LiveRemoteConfigInfo mLiveRemoteConfigInfo;
    private Thread thread = new Thread();
    private boolean isRunning; //日志在上报中
    private int anrCount;//当前触发次数
    private Gson mGson;

    public static LiveLogBill getInstance() {
        if (mInstance == null) {
            synchronized (LiveLogBill.class) {
                if (mInstance == null) {
                    mInstance = new LiveLogBill(XueErSiRunningEnvironment.sAppContext);
                }
            }
        }
        return mInstance;
    }


    public static LiveLogBill getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LiveLogBill.class) {
                if (mInstance == null) {
                    mInstance = new LiveLogBill(context);
                }
            }
        }
        return mInstance;
    }


    private LiveLogBill(Context context) {
        this.context = context;
        isRunning = false;
        mGson = new Gson();
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
     * 直播监控日志
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
     * 直播业务日志
     */
    public void initLiveBisLog() {

        LiveBusiLogSendLogRunnable logSendLogRunnable = new LiveBusiLogSendLogRunnable();
        logSendLogRunnable.setPath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                + File.separator + LOG_LIVE_BUSI_LOG_NAME + android.os.Process.myPid());

        LogConfig apmConfig = new LogConfig.Builder()
                .setCachePath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                        + File.separator + "LOG_LIVE_BUSI_LOG_NAME" + File.separator + android.os.Process.myPid())
                .setPath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                        + File.separator + LOG_LIVE_BUSI_LOG_NAME + android.os.Process.myPid())
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .setDay(5)
                .setMaxFile(10)
                .setMinSDCard(10)
                .build();

        LiveBusiLog.init(apmConfig, logSendLogRunnable, 5);
        LiveBusiLog.setUpParamInterface(new UpdateParamInterface() {
            @Override
            public XrsLogPublicParam getXrsLogPublicParam() {

                BuryPublicParam buryPublicParam = new BuryPublicParam();
                buryPublicParam.app_id = "1001637";
                buryPublicParam.app_clientid = "xesApp";
                buryPublicParam.devid = "8";
                buryPublicParam.sn = "android";
                buryPublicParam.user_id = CommonUtil.getUserIdentifier(XueErSiRunningEnvironment.sAppContext);
                try {
                    //系统日志,seesinnID app 启动后不变
                    buryPublicParam.session_id = UmsAgentTrayPreference.getInstance().getString(UmsAgentTrayPreference.UMSAGENT_APP_SESSID);
                } catch (ItemNotFoundException e) {
                    e.printStackTrace();
                }
                buryPublicParam.ver = AppUtils.getAppVersionName(XueErSiRunningEnvironment.sAppContext);
                buryPublicParam.clientIdentifier = AppBll.getInstance().getAppInfoEntity().getAppUUID();

                buryPublicParam.ci = ""; //IP
                buryPublicParam.ac = DeviceInfo.getNetworkTypeWIFI2G3G();
                buryPublicParam.access_subtype = CommonUtil.getNetworkType(XueErSiRunningEnvironment.sAppContext);
                buryPublicParam.ch = AppBll.getInstance().getAppChannel();
                //buryPublicParam.lt = DeviceInfo.getDeviceTime();
                buryPublicParam.lt = System.currentTimeMillis() + "";
                buryPublicParam.st = ""; //服务端时间
                buryPublicParam.log = "";
                buryPublicParam.et = "";
                buryPublicParam.imei = DeviceInfo.getDeviceIMEI();
                buryPublicParam.imsi = DeviceInfo.getIMSI();
                buryPublicParam.cr = ""; //运营商
                buryPublicParam.br = DeviceInfo.getDeviceName(); //手机品牌
                buryPublicParam.dm = DeviceInfo.getDeviceProduct();//手机型号
                buryPublicParam.os = DeviceInfo.getOsVersion(); //系统名称
                buryPublicParam.lbs = ""; //地址信息
                buryPublicParam.gps = DeviceInfo.getLatitude() + "*" + DeviceInfo.getLongitude();
                buryPublicParam.lan = DeviceInfo.getLanguage();
                buryPublicParam.ab_group = ""; //AB测试
                buryPublicParam.r = DeviceInfo.getReasolution(); //分辨率
                return buryPublicParam;
            }
        });
        LiveBusiLog.startLog();
    }


    /**
     * 开启直播监控日志(轮循)
     */
    public void startLog() {
        // LiveLog.startLog();
    }

    /**
     * 关闭直播监控日志(轮循)
     */
    public void stopLog() {
        // LiveLog.stopLog();
    }

    /**
     * 打开app 采集日志
     */
    public void openAppLiveLog() {


        livebaseLog(1);

//        if (mLiveRemoteConfigInfo.liveANRLogTag != 0) {
//            return;
//        }
//
//        LiveLogEntity log = new LiveLogEntity();
//        log.pri = "2";
//        if (myUserInfoEntity != null) {
//            log.psId = myUserInfoEntity.getPsimId();
//        }
//        if (LiveLogBill.param != null) {
//            log.liveid = LiveLogBill.param.liveid;
//        }
//        if (myUserInfoEntity == null) {
//            myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
//        }
//        List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
//        Pridata pridata = new Pridata();
//        pridata.ping = new HashMap<String, PingInfo>();
//        Map<String, String> pingMap = new HashMap<String, String>();
//        if (!ListUtil.isEmpty(domainList)) {
//            for (int i = 0; i < domainList.size(); i++) {
//                PingInfo info = NetUtil.ping(domainList.get(i));
//                pridata.ping.put(domainList.get(i), info);
//                pingMap.put(info.host, info.ip);
//            }
//
//            log.pridata = pridata;
//            pridata.dnsinfo = pingMap;
//        }
//        LiveLog.log(log);
//        LiveLog.sendLog();

    }

    /**
     * 打开直播日志
     */
    public void openLiveLog() {

        livebaseLog(2);


//        if (mLiveRemoteConfigInfo.liveANRLogTag != 0) {
//            return;
//        }
//
//        LiveLogEntity log = new LiveLogEntity();
//        log.pri = "2";
//        if (LiveLogBill.param != null) {
//            log.liveid = LiveLogBill.param.liveid;
//        }
//        if (myUserInfoEntity != null) {
//            log.psId = myUserInfoEntity.getPsimId();
//        }
//        if (myUserInfoEntity == null) {
//            myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
//        }
//        List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
//        Pridata pridata = new Pridata();
//        pridata.ping = new HashMap<String, PingInfo>();
//        Map<String, String> pingMap = new HashMap<String, String>();
//        if (!ListUtil.isEmpty(domainList)) {
//            for (int i = 0; i < domainList.size(); i++) {
//                PingInfo info = NetUtil.ping(domainList.get(i));
//                pridata.ping.put(domainList.get(i), info);
//                pingMap.put(info.host, info.ip);
//            }
//
//            log.pridata = pridata;
//            pridata.dnsinfo = pingMap;
//        }
//
//
//        LiveLog.log(log);
//        LiveLog.sendLog();
    }

    /**
     * 直播卡顿log
     */
    public void liveANRLog() {


        livebaseLog(3);


//        if (mLiveRemoteConfigInfo.liveANRLogTag != 0) {
//            return;
//        }
//
//        LiveLogEntity log = new LiveLogEntity();
//        log.pri = "3";
//        if (LiveLogBill.param != null) {
//            log.liveid = LiveLogBill.param.liveid;
//        }
//        if (myUserInfoEntity == null) {
//            myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
//        }
//        if (myUserInfoEntity != null) {
//            log.psId = myUserInfoEntity.getPsimId();
//        }
//        List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
//        Pridata pridata = new Pridata();
//        pridata.ping = new HashMap<String, PingInfo>();
//        Map<String, String> pingMap = new HashMap<String, String>();
//        if (!ListUtil.isEmpty(domainList)) {
//            for (int i = 0; i < domainList.size(); i++) {
//                PingInfo info = NetUtil.ping(domainList.get(i));
//                pridata.ping.put(domainList.get(i), info);
//                pingMap.put(info.host, info.ip);
//            }
//            log.pridata = pridata;
//            pridata.dnsinfo = pingMap;
//        }
//        LiveLog.log(log);
//        LiveLog.sendLog();

    }


    /**
     * 直播卡顿log
     */
    private void livebaseLog(final int type) {

        if (mLiveRemoteConfigInfo.liveANRLogTag != 0 || isRunning) {
            return;
        }

        if (type == 3) {

            anrCount++;
            if (anrCount >= mLiveRemoteConfigInfo.liveANRLogPuhNum) {
                anrCount = 0;
            } else {
                return;
            }
        }

        new Thread() {
            @Override
            public void run() {

                isRunning = true;
                LiveLogEntity log = new LiveLogEntity();
                if (type == 1 || type == 2) {
                    log.pri = 2;
                } else {
                    log.pri = type;
                }
                if (LiveLogBill.param != null) {
                    log.liveid = LiveLogBill.param.liveid;
                }
                log.processId = android.os.Process.myPid();
                log.reason = type + "";
                if (myUserInfoEntity == null) {
                    myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                }
                if (myUserInfoEntity != null) {
                    log.psId = myUserInfoEntity.getPsimId();
                }

                if ("wifi".equals(DeviceInfo.getNetworkTypeWIFI2G3G())) {
                    log.net = 5;
                } else {
                    log.net = 9;
                }
                List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
                Pridata pridata = new Pridata();
                pridata.ping = new HashMap<String, PingInfo>();
                Map<String, String> pingMap = new HashMap<String, String>();
                if (!ListUtil.isEmpty(domainList)) {
                    for (int i = 0; i < domainList.size(); i++) {
                        PingInfo info = NetUtil.ping(domainList.get(i));
                        pridata.ping.put(domainList.get(i), info);
                        pingMap.put(domainList.get(i), info.ip);
                    }
                    log.pridata = pridata;
                    pridata.dnsinfo = pingMap;
                }
                String s = mGson.toJson(log);
                LiveMonitorDebug.dLog(s);
                //LiveLog.sendLog();
                LiveLog.log(log);
                LiveMonitorSender.send(s);
                isRunning = false;

            }
        }.start();

    }

    public void sendStuckLog() {
//        livebaseLog(3);

        new Thread() {
            @Override
            public void run() {
                LiveLogEntity log = new LiveLogEntity();
                log.pri = 3;
                if (LiveLogBill.param != null) {
                    log.liveid = LiveLogBill.param.liveid;
                }
                log.processId = Process.myPid();
                log.reason = "3";
                if (myUserInfoEntity == null) {
                    myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                }
                if (myUserInfoEntity != null) {
                    log.psId = myUserInfoEntity.getPsimId();
                }

                if ("wifi".equals(DeviceInfo.getNetworkTypeWIFI2G3G())) {
                    log.net = 5;
                } else {
                    log.net = 9;
                }
                List<String> domainList = mLiveRemoteConfigInfo.liveRemoteDomainConfigInfo;
                Pridata pridata = new Pridata();
                pridata.ping = new HashMap<String, PingInfo>();
                Map<String, String> pingMap = new HashMap<String, String>();
                if (!ListUtil.isEmpty(domainList)) {
                    for (int i = 0; i < domainList.size(); i++) {
                        PingInfo info = NetUtil.ping(domainList.get(i));
                        pridata.ping.put(domainList.get(i), info);
                        pingMap.put(domainList.get(i), info.ip);
                    }
                    log.pridata = pridata;
                    pridata.dnsinfo = pingMap;
                }

                String s = mGson.toJson(log);
                LiveMonitorDebug.dLog(s);
                //LiveLog.sendLog();
                LiveLog.log(log);
                LiveMonitorSender.send(s);
            }
        }.start();
    }
}
