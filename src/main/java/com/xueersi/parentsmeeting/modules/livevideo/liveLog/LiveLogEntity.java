package com.xueersi.parentsmeeting.modules.livevideo.liveLog;


import com.xueersi.common.base.XueErSiRunningEnvironment;
import com.xueersi.common.logerhelper.matrix.ApmBill;
import com.xueersi.lib.analytics.umsagent.CommonUtil;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.framework.utils.AppUtils;


/**
 * 直播 日志参数
 */
public class LiveLogEntity {


    public int state=0; //0:正常  1：异常

    public String uid = CommonUtil.getUserIdentifier(XueErSiRunningEnvironment.sAppContext); //用户ID

    public String live_id;  //直播ID
    public int role = 0;    //角色  0：学生 1：老师

    public String version = AppUtils.getAppVersionName(XueErSiRunningEnvironment.sAppContext);  //app版本
    public String time = DeviceInfo.getDeviceTime();    //时间
    public String os = DeviceInfo.getOsVersion();       //操作系统
    public String modle = DeviceInfo.getDeviceName();        //手机型号
    public String resolution = DeviceInfo.getReasolution(); //分辨率


    public String network_type = DeviceInfo.getNetworkTypeWIFI2G3G();  //网络类型 （无网，4G, wifi）
    public String dns=""; //网络DNS
    public String local_ip=ApmBill.getHostIP(); //本地IP
    public String network_ip=""; //外网IP，日志接受方填充

    public String mac = DeviceInfo.getWifiMac();    //wifi mac 地址

    public String cpu_modle=ApmBill.getCPUType();            //CPU架构
    public String cpu= ApmBill.getCPURateDesc();             //CPU使用百分比

    public String mem=ApmBill.getUseMemory()+"";              //内存使用状况
    public String mem_total=ApmBill.getMemory()+"";           //内存总容量


    //---------------------------------------------------------------------------------------

    public String displayCard = "none";        //显卡                         （移动端没有）

    public String streamNetwork;      //流网络是正常，视频流网络          nice   bad   week
    public String lrcNetwork;         //lrc网络状况， 聊天网络           nice   bad   week
    public String h5Network;          //h5网络状况， 互动题网络           nice   bad   week
    public String resourceNetwork;    //资源网络状况                    nice   bad   week
    public String baiduTest;          //百度网络测试                    nice   bad   week

    public int micDeviceInfo = 1;         //麦克风    1：可用   2：不可用
    public int speakerDeviceInfo = 1;     //扬声器    1：可用   2：不可用
    public int videoDeviceInfo = 1;       //摄像头    1：可用   2：不可用


}


