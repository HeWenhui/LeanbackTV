package com.xueersi.parentsmeeting.modules.livevideo.liveLog;

import android.text.TextUtils;

import com.xueersi.common.base.AppOpenConfigEntity;
import com.xueersi.common.base.AppUpBusiConfigInterface;
import com.xueersi.common.base.XueErSiRunningEnvironment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 直播监控日志 app启动初始化
 */
public class LiveApmLogAppUpConfig implements AppUpBusiConfigInterface {


    @Override
    public void init() {
        LiveLogBill.getInstance().initLiveLog(); //初始化直播日志

        final String processName =getProcessName(android.os.Process.myPid());
        if(XueErSiRunningEnvironment.sAppContext.getPackageName().equals(processName)){
            LiveLogBill.getInstance().openAppLiveLog();//主进程开启直播性能启动日志
        }
        LiveLogBill.getInstance().initLiveBisLog();//初始化直播业务日志
    }

    @Override
    public AppOpenConfigEntity getConfig() {

        AppOpenConfigEntity entity = new AppOpenConfigEntity();
        entity.delayTime = 200; //200毫秒
        entity.onMainThread = true;
        return entity;
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
