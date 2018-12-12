package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebugAnalysis;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/12/11.
 * 接麦两个步骤
 */
public class VideoAudioChatLog {
    private static String eventId = LogConfig.LIVE_LINK_NEWMIC;

    /** 接麦第2步，学生收到老师发送的举手指令 */
    public static void getRaiseHandMsgSno2(LiveAndBackDebugAnalysis liveAndBackDebug, String mictype, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getRaiseHandMsg");
        logHashMap.put("mictype", mictype);
        logHashMap.addSno("2").addExY().addNonce(nonce).addStable("1");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
    }

    /** 接麦第3步，学生端展示接麦面板 */
    public static void showLinkMicPanelSno3(LiveAndBackDebugAnalysis liveAndBackDebug, String teacher_type, String nonce, boolean microphoneable) {
        StableLogHashMap logHashMap = new StableLogHashMap("showLinkMicPanel");
        logHashMap.put("teachertype", teacher_type);
        logHashMap.put("microphoneable", microphoneable ? "true" : "false");
//        if (LiveVideoConfig.IS_SCIENCE) {
        logHashMap.addSno("3").addExY().addNonce(nonce).addStable("2");
//        }
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
    }

    /** 接麦第四步，学生点击举手 */
    public static void clickedRaiseHandSno4(LiveAndBackDebugAnalysis liveAndBackDebug, String camera, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("clickedRaiseHand");
        logHashMap.put("camera", camera);
        logHashMap.addSno("4").addStable("2");
        logHashMap.addExpect("1").addExY().addNonce(nonce);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
    }

    /** 接麦第五步，通知后台用户举手 */
    public static void raiseHandToPhpSno5(LiveAndBackDebugAnalysis liveAndBackDebug, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("raiseHandToPhp");
        logHashMap.addSno("5").addStable("2");
        logHashMap.addExY().addNonce(nonce);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
    }

    /** 接麦第六步，用户收到上麦/下麦指令 */
    public static void cancelRaiseHandSno6(LiveAndBackDebugAnalysis liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap logHashMap = new StableLogHashMap("cancelRaiseHand");
        logHashMap.addNonce(nonce);
        logHashMap.addSno("6").addStable("2");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap);
    }

    /** 接麦第七步，学生上麦/下麦结果 */
    public static void studentLinkMicSno10(LiveAndBackDebugAnalysis liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap logHashMap = new StableLogHashMap("studentLinkMic");
        logHashMap.addNonce(nonce);
        logHashMap.addSno("10").addStable("2");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap);
    }

    /** 接麦第十二步，收到老师关闭连麦的消息 */
    public static void getCloseMsgSno12(LiveAndBackDebugAnalysis liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap logHashMap = new StableLogHashMap("getCloseMsg");
        logHashMap.addNonce(nonce);
        logHashMap.addSno("12").addStable("2");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap);
    }
}
