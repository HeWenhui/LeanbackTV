package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.HashMap;
import java.util.Map;

/**
*nb 物理实验log
*@author chekun
*created  at 2019/4/28 15:49
*/
public class NbCourseLog {

    private static final String eventId = "live_h5experiment";


    private static final String experimentFlow = "nb_experiment_flow";
    /**
     * 直播，NB实验 收到  收/发 实验指令
     * @param liveAndBackDebug
     * @param testid  实验id
     * @param status  on/off
     */
    public static void sno2(LiveAndBackDebug liveAndBackDebug,String testid,String status){
        StableLogHashMap logHashMap = new StableLogHashMap("experimentSwitch");
        logHashMap.put("testid", testid);
        logHashMap.put("status",  status);
        logHashMap.put("sno", "2");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     *  NB 加试实验 Loading 展示
      * @param liveAndBackDebug
     * @param testid
     * @param loadurl
     * @param playBack
     * @param preload
     */
   public static void sno3(LiveAndBackDebug liveAndBackDebug,String testid,String loadurl,boolean playBack,boolean preload){
       StableLogHashMap logHashMap = new StableLogHashMap("showExperimentLoading");
       logHashMap.put("testid", testid);
       logHashMap.put("loadurl",  loadurl);
       logHashMap.put("isplayback",  playBack?"1":"0");
       logHashMap.put("ispreload",  preload?"1":"0");
       logHashMap.put("sno", "3");
       liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
   }

    /**
     * 实验加载 结果
     * @param liveAndBackDebug
     * @param testid      实验id
     * @param loadurl
     * @param preLoad
     * @param loadTime   加载时长
     * @param playBack
     * @param status     0/1   成功:失败
     * @param isFresh    是否点击过刷新
     */
   public static void sno4(LiveAndBackDebug liveAndBackDebug,String testid,String loadurl,boolean preLoad,String loadTime,
                           boolean playBack,String status,boolean isFresh){
       StableLogHashMap logHashMap = new StableLogHashMap("showH5Experiment");
       logHashMap.put("testid", testid);
       logHashMap.put("loadurl",  loadurl);
       logHashMap.put("isplayback",  playBack?"1":"0");
       logHashMap.put("ispreload",  preLoad?"1":"0");
       logHashMap.put("loadtime",  loadTime);
       logHashMap.put("sno", "4");
       logHashMap.put("status", status);
       logHashMap.put("isfresh", isFresh?"1":"0");
       liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
   }

    /**
     *  nb 加试  学生 主动关闭实现 （点击退出实验确认按钮）
     * @param liveAndBackDebug
     * @param testid
     * @param playBack
     */
   public static void clickCloseExperiment(LiveAndBackDebug liveAndBackDebug,String testid,boolean playBack){
       StableLogHashMap logHashMap = new StableLogHashMap("clickCloseExperiment");
       logHashMap.put("testid", testid);
       logHashMap.put("isplayback", playBack?"1":"0");
       liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
   }

    /**
     * 开始提交 实验答题
     * @param liveAndBackDebug
     * @param testid
     * @param force
     * @param optime
     * @param playBack
     */
   public static void sno5(LiveAndBackDebug liveAndBackDebug,String testid,boolean force,String optime,boolean playBack){
       StableLogHashMap logHashMap = new StableLogHashMap("startSubmitExperiment");
       logHashMap.put("testid", testid);
       logHashMap.put("isplayback", playBack?"1":"0");
       logHashMap.put("sno", "5");
       logHashMap.put("isforce", force?"1":"0");
       logHashMap.put("optime", optime);
       liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
   }


    /**
     * 一次提交结束
     * @param liveAndBackDebug
     * @param testid
     * @param playBack
     * @param status        0/1  成功或者失败
     * @param submittime    接口耗时
     */
   public static void sno6(LiveAndBackDebug liveAndBackDebug,String testid,boolean playBack,String status,String submittime){
       StableLogHashMap logHashMap = new StableLogHashMap("submitExperimentResult");
       logHashMap.put("testid", testid);
       logHashMap.put("isplayback", playBack?"1":"0");
       logHashMap.put("sno", "6");
       logHashMap.put("status", status);
       logHashMap.put("submittime", submittime);
       liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());

   }

    /**
     * 展示实验结果页
     * @param liveAndBackDebug
     * @param testid
     * @param playback
     * @param status
     * @param highrightcount   最高连对次数
     * @param goldcount        金币数
     */
   public static void sno7(LiveAndBackDebug liveAndBackDebug,String testid,boolean playback,String status,String highrightcount,String goldcount){
       StableLogHashMap logHashMap = new StableLogHashMap("showExperimentResult");
       logHashMap.put("testid", testid);
       logHashMap.put("isplayback", playback?"1":"0");
       logHashMap.put("sno", "7");
       logHashMap.put("status", status);
       logHashMap.put("highrightcount", highrightcount);
       logHashMap.put("goldcount", goldcount);
       liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
   }

    /**
     * 收到发实验指令
     * @param liveAndBackDebug
     * @param experimentId
     */
   public static void reciveStartCmd (LiveAndBackDebug liveAndBackDebug,String experimentId){
       StableLogHashMap logHashMap = new StableLogHashMap("reciveStartCmd");
       logHashMap.put("experimentId", experimentId);
       liveAndBackDebug.umsAgentDebugSys(experimentFlow, logHashMap.getData());
   }

    /**
     * Nb 登录
     * @param liveAndBackDebug
     * @param status   1/0 登录接口成功或者失败
     * @param msg      异常信息
     */
   public static void nbLogin(LiveAndBackDebug liveAndBackDebug,String status,String msg){
       StableLogHashMap logHashMap = new StableLogHashMap("nbLogin");
       logHashMap.put("status", status);
       logHashMap.put("message", msg);
       liveAndBackDebug.umsAgentDebugSys(experimentFlow, logHashMap.getData());
   }

    /**
     * 获取Nb 实验课程信息
     * @param liveAndBackDebug
     * @param experimentId
     * @param status    1/0   成功/失败
     * @param msg       异常信息
     */
   public static void getNbTestInfo(LiveAndBackDebug liveAndBackDebug,String experimentId,String status,String msg){
       StableLogHashMap logHashMap = new StableLogHashMap("getNbTestInfo");
       logHashMap.put("status", status);
       logHashMap.put("experimentId", experimentId);
       logHashMap.put("message", msg);
       liveAndBackDebug.umsAgentDebugSys(experimentFlow, logHashMap.getData());
   }


    /**
     * 加载Nb 课件
     * @param liveAndBackDebug
     * @param experimentId
     * @param status     1/0  成功/失败
     * @param msg        异常信息
     */
   public static void loadNbCourseWare(LiveAndBackDebug liveAndBackDebug,String experimentId,String status,String msg){
       StableLogHashMap logHashMap = new StableLogHashMap("loadNbCourseWare");
       logHashMap.put("status", status);
       logHashMap.put("experimentId", experimentId);
       logHashMap.put("message", msg);
       liveAndBackDebug.umsAgentDebugSys(experimentFlow, logHashMap.getData());
   }


    /**
     * 提交Nb 实验
     * @param liveAndBackDebug
     * @param experimentId
     * @param status  1/0 成功失败
     * @param msg
     */
   public static void submitNbCourseWare(LiveAndBackDebug liveAndBackDebug,String experimentId,String status,String msg){
       StableLogHashMap logHashMap = new StableLogHashMap("submitNbCourseWare");
       logHashMap.put("status", status);
       logHashMap.put("experimentId", experimentId);
       logHashMap.put("message", msg);
       liveAndBackDebug.umsAgentDebugSys(experimentFlow, logHashMap.getData());
   }

    /**
     * 收到结束作答指令
     * @param liveAndBackDebug
     * @param experimentId
     */
   public static void reciveEndCmd(LiveAndBackDebug liveAndBackDebug,String experimentId){
       StableLogHashMap logHashMap = new StableLogHashMap("submitNbCourseWare");
       logHashMap.put("experimentId", experimentId);
       liveAndBackDebug.umsAgentDebugSys(experimentFlow, logHashMap.getData());
   }

}
