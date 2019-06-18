package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/12/11.
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=13844232
 * 接麦两个步骤
 */
public class VideoAudioChatLog {
    private static String eventId = LogConfig.LIVE_LINK_NEWMIC;
    //sno logtype                          类型  stable   nonce   日志说明
    //2 getRaiseHandMsg                  系统日志 1       获取 学生收到接麦指令(A)
    //3 showLinkMicPanel                 展现日志 2       获取 学生端展示接麦面板(A)
    //4 clickedRaiseHand                 交互日志 2       获取 用户点击举手(A)
    //5 raiseHandToPhp                   系统日志 2       获取 告诉后台用户举手
    //6 cancelRaiseHand                  交互日志 2       获取 用户取消举手(A)
    //9 getSelectedMsg/getLeaveMsg       系统日志 1       获取 学生收到上麦 /下麦指令(B)
    //10 studentLinkMic/studentLeaveMic  展现日志 2       获取 用户上麦 /下麦(B)
    //12 getCloseMsg                     系统日志 1       获取 收到老师关闭连麦的消息(C)

    /** 接麦第二步，学生收到老师发送的举手指令 */
    public static void getRaiseHandMsgSno2(LiveAndBackDebug liveAndBackDebug, String mictype, String linkmicid, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getRaiseHandMsg");
        logHashMap.put("mictype", mictype);
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.addSno("2").addExY().addNonce(nonce).addStable("1");
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap);
    }

    /** 接麦第三步，学生端展示接麦面板 */
    public static void showLinkMicPanelSno3(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("showLinkMicPanel");
        logHashMap.put("mictype", mictype);
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.addSno("3").addExY().addNonce(nonce).addStable("2");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap);
    }

    /** 接麦第四步，学生点击举手 */
    public static void clickedRaiseHandSno4(LiveAndBackDebug liveAndBackDebug, String camera, String linkmicid, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("clickedRaiseHand");
        logHashMap.put("camera", camera);
        logHashMap.put("mictype", mictype);
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.addSno("4").addStable("2").addExY();
        logHashMap.addNonce(nonce);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
    }

    /** 接麦第五步，通知后台用户举手 */
    public static void raiseHandToPhpSno5(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce, boolean success, String errorcode, long duration) {
        StableLogHashMap logHashMap = new StableLogHashMap("raiseHandToPhp");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addSno("5").addStable("2");
        logHashMap.addEx(success);
        logHashMap.addNonce(nonce);
        Map<String, String> analysis = logHashMap.getAnalysis();
        analysis.put("success", "" + success);
        analysis.put("errorcode", errorcode);
        analysis.put("duration", "" + duration);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap);
    }

    /** 接麦第六步，用户取消举手 */
    public static void cancelRaiseHandSno6(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("cancelRaiseHand");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addNonce(nonce).addExY();
        logHashMap.addSno("6").addStable("2");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
    }

    /** 接麦第六步，用户收到上麦/下麦指令 */
    public static void getSelectedMsgSno9(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getSelectedMsg");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addNonce(nonce).addExY();
        logHashMap.addSno("9").addStable("1");
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap);
    }

    /** 接麦第六步，用户收到上麦/下麦指令 */
    public static void getLeaveMsgSno9(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getLeaveMsg");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addNonce(nonce).addExY();
        logHashMap.addSno("9").addStable("1");
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap);
    }

    /** 接麦第十步，学生上麦/下麦结果 */
    public static void studentLinkMicSno10(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce, boolean success, String errorcode) {
        StableLogHashMap logHashMap = new StableLogHashMap("studentLinkMic");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addNonce(nonce);
        logHashMap.addEx(success);
        logHashMap.addSno("10").addStable("2");
        Map<String, String> analysis = logHashMap.getAnalysis();
        analysis.put("success", "" + success);
        analysis.put("errorcode", errorcode);
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap);
    }

    /** 接麦第十步，学生上麦/下麦结果 */
    public static void studentLeaveMic10(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce, boolean success, String errorcode) {
        StableLogHashMap logHashMap = new StableLogHashMap("studentLeaveMic");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addNonce(nonce);
        logHashMap.addEx(success);
        logHashMap.addSno("10").addStable("2");
        Map<String, String> analysis = logHashMap.getAnalysis();
        analysis.put("success", "" + success);
        analysis.put("errorcode", errorcode);
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap);
    }

    /** 接麦第十二步，收到老师关闭连麦的消息 */
    public static void getCloseMsgSno12(LiveAndBackDebug liveAndBackDebug, String linkmicid, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getCloseMsg");
        logHashMap.put("linkmicid", linkmicid);
        logHashMap.put("mictype", mictype);
        logHashMap.addNonce(nonce).addExY();
        logHashMap.addSno("12").addStable("1");
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap);
    }
}
