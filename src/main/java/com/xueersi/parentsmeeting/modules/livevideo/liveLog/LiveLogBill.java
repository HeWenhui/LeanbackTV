package com.xueersi.parentsmeeting.modules.livevideo.liveLog;


import android.content.Context;

import com.hwl.bury.xrsbury.BuryPublicParam;
import com.hwl.log.LogConfig;
import com.hwl.log.xrsLog.UpdateParamInterface;
import com.hwl.log.xrsLog.XrsLogPublicParam;
import com.xueersi.common.base.XueErSiRunningEnvironment;
import com.xueersi.common.logerhelper.LogBill;
import com.xueersi.lib.framework.utils.AppUtils;

import java.io.File;

/**
 * 直播监控日志业务
 */
public class LiveLogBill {


    private static final String LOG_LIVE_LOG_NAME = "log_live_v1";

    public Context context;
    private static LiveLogBill mInstance;
    static LiveLogEntity param = new LiveLogEntity();
    static String url;

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
            param.live_id = liveId;
        }

    }

    /**
     * 开始直播监控日志
     */
    public void initLiveLog() {

        LiveLogSendLogRunnable logSendLogRunnable = new LiveLogSendLogRunnable();
        logSendLogRunnable.setPath(XueErSiRunningEnvironment.sAppContext.getFilesDir().getAbsolutePath()
                + File.separator + LOG_LIVE_LOG_NAME + android.os.Process.myPid());

        com.hwl.log.LogConfig apmConfig = new LogConfig.Builder()
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

    }


    /**
     * 开启直播监控日志
     */
    public void startLog() {
        LiveLog.startLog();
    }

    /**
     * 关闭直播监控日志
     */
    public void stopLog() {
        LiveLog.stopLog();
    }
}
