package com.xueersi.parentsmeeting.modules.livevideo.liveLog;


import com.xueersi.common.base.XueErSiRunningEnvironment;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.logerhelper.matrix.ApmBill;
import com.xueersi.common.logerhelper.matrix.CpuManager;
import com.xueersi.lib.analytics.umsagent.CommonUtil;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.analytics.umsagent.MurmurHashBase64;
import com.xueersi.lib.framework.utils.AppUtils;


/**
 * 直播 日志参数
 */
public class LiveLogEntity {


    public int state = 0;    //0:正常  1：异常
    public int role = 0;    //角色  0：学生 1：老师
    public String version = AppUtils.getAppVersionName(XueErSiRunningEnvironment.sAppContext);  //app版本
    public String time = DeviceInfo.getDeviceTime();    //时间

    public String modle = DeviceInfo.getDeviceName();        //手机型号
    public String resolution = DeviceInfo.getReasolution(); //分辨率
    public String network_type = DeviceInfo.getNetworkTypeWIFI2G3G();  //网络类型 （无网，4G, wifi）
    public String local_ip = ApmBill.getHostIP(); //本地IP
    public String network_ip = ""; //外网IP，日志接受方填充

    public String mem_total = ApmBill.getMemory() + "";           //内存总容量
    public String mac = "";    //wifi mac 地址
    public String cpu_modle = ApmBill.getCPUType();            //CPU架构


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

    //----------------------------------------------------------------------------------------

    public String liveid;  //直播ID

    public String userid = CommonUtil.getUserIdentifier(XueErSiRunningEnvironment.sAppContext); //用户ID

    public String productname = "";  //调取自检模块对应的业务

    public String reason = "";  //调取原因

    public String os = "Android :" + DeviceInfo.getOsVersion();   //操作系统

    public String psId;  //磐石ID

    public String arch = CpuManager.getCpuName(); //CPU型号

    public float ram = ApmBill.getMemory(); //内存

    public long ts = System.currentTimeMillis(); //时间

    public int ver = 1; //版本

    public String appVer = AppUtils.getAppVersionName(XueErSiRunningEnvironment.sAppContext);  //app版本

    public String cip = ""; //用户出口ip

    public float mem = ApmBill.getUseMemory();        //内存使用状况，单位KB

    public double cpu = ApmBill.getCPURate();             //CPU使用百分比

    public int net = 5;  //网络类型 （无网，4G, wifi,3G,lte)  5:wifi 9:蜂窝

    public String lip = ApmBill.getHostIP(); //本地ip

    public String appId = "1001906";   //日志Id

    public String agent = "android-" + AppUtils.getAppVersionName(XueErSiRunningEnvironment.sAppContext);

    public int pri = 2;  //日志业务类型

    public String tid = MurmurHashBase64.hashUnsigned(System.currentTimeMillis() + Math.random() +
            AppBll.getInstance().getAppInfoEntity().getAppUUID()).toBigInteger().toString(16);   //唯一日志Id ，tradeId

    public int serv = 2000;

    public String dev = DeviceInfo.getDeviceName();

    public Pridata pridata;

    public String dns;

    public int processId;


}


