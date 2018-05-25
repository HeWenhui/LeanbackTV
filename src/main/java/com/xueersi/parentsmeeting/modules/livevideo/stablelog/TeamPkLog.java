package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import android.text.TextUtils;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * 战队pk 数据统计
 *
 * @author chekun
 * created  at 2018/5/21 13:55
 */
public class TeamPkLog {

    private static String eventId = LiveVideoConfig.LIVE_PK;

    /**
     * 是否上传 开关
     */
    private static boolean UPLOAD_OPEN = true;

    /**
     * 学生端收到创建战队
     *
     * @param liveAndBackDebug
     * @param nonce
     * @param isOpen           开启/关闭
     */
    public static void receiveCreateTeam(LiveAndBackDebug liveAndBackDebug, String nonce, boolean isOpen) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("receiveCreateTeam");
            logHashMap.addExY()
            .addSno("2")
            .addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.addStable("1");
            logHashMap.put("isopen", isOpen ? "1" : "0");
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        }

    }

    /**
     * 学生端展现分队仪式
     *
     * @param liveAndBackDebug
     */
    public static void showCreateTeam(LiveAndBackDebug liveAndBackDebug) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("showCreateTeam");
            logHashMap.addStable("0");
            liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
        }

    }

    /**
     * 学生端发送我准备好了
     *
     * @param liveAndBackDebug
     */
    public static void sendReady(LiveAndBackDebug liveAndBackDebug) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("sendReady");
            logHashMap.addStable("2").addSno("3");
            logHashMap.addExY();
            liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
        }

    }

    /**
     * 学生端收到匹配对手的指令
     *
     * @param liveAndBackDebug
     * @param nonce
     * @param isOpen           开启/关闭
     */
    public static void receiveMatchOpponent(LiveAndBackDebug liveAndBackDebug, String nonce, boolean isOpen) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("receiveMatchOpponent");
            logHashMap.addSno("5").addNonce(TextUtils.isEmpty(nonce)?"":nonce).addExY();
            logHashMap.addStable("1");
            logHashMap.put("isopen", isOpen ? "1" : "0");
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        }
    }

    /**
     * 学生端展示匹配对手页面
     *
     * @param liveAndBackDebug
     * @param iscomputer       对手是否是机器人
     * @param  teamName     本队名称
     * @param  optName 对手战队名称
     * @param  optId 对手战队id
     */
    public static void showOpponent(LiveAndBackDebug liveAndBackDebug, boolean iscomputer,String teamName,String optName,String optId) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("showOpponent");
            logHashMap.put("iscomputer", iscomputer ? "1" : "0");
            logHashMap.addStable("2");
            logHashMap.addSno("6");
            logHashMap.addExY();
            logHashMap.put("teamname",teamName);
            logHashMap.put("opponentname",optName);
            logHashMap.put("opponentid",optId);
            liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
        }

    }


    /**
     * 学生端展示单题pk结果页
     *
     * @param liveAndBackDebug
     * @param isStar           学生自己 是否是贡献之星
     * @param eventId          live_h5waretest H5课件互动题     live_h5test  H5题库互动题     live_exam h5测试卷
     * @param  teamName        本队战队昵称
     */
    public static void showPerTestPk(LiveAndBackDebug liveAndBackDebug, boolean isStar, String nonce, String eventId,String teamName) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("showPerTestPk");
            logHashMap.put("isstar", isStar ? "1" : "0");
            logHashMap.addStable("1");
            logHashMap.addSno("7");
            logHashMap.addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.addExY();
            logHashMap.put("teamname",teamName);
            liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
        }

    }

    /**
     * 学生端收到语音表扬指令
     *
     * @param liveAndBackDebug
     * @param nonce
     */
    public static void receiveVoicePraise(LiveAndBackDebug liveAndBackDebug, String nonce) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("receiveVoicePraise");
            logHashMap.addSno("8");
            logHashMap.addExY();
            logHashMap.addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.addStable("1");
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        }

    }

    /**
     * 学生端收到pk结果指令
     *
     * @param liveAndBackDebug
     * @param nonce
     */
    public static void receivePkResult(LiveAndBackDebug liveAndBackDebug, String nonce, boolean isOpen) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("receivePkResult");
            logHashMap.addSno("10");
            logHashMap.addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.addStable("1");
            logHashMap.addExY();
            logHashMap.put("isopen", isOpen ? "1" : "0");
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        }
    }


    /**
     * 学生端展示PK结果页
     *
     * @param liveAndBackDebug
     * @param isWin            1:获胜 ，0：打平  -1:失败
     */
    public static void showPkResult(LiveAndBackDebug liveAndBackDebug, boolean isWin) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("showPkResult");
            logHashMap.put("iswin", isWin ? "1" : "0");
            liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
        }

    }


    /**
     * 学生点击打开宝箱
     *
     * @param liveAndBackDebug
     * @param isWin            是否获胜宝箱
     * @param nonce            客户端生成
     */
    public static void clickTreasureBox(LiveAndBackDebug liveAndBackDebug, boolean isWin, String nonce) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("clickTreasureBox");
            logHashMap.put("iswin", isWin ? "1" : "0");
            logHashMap.addSno("11");
            logHashMap.addStable("1");
            logHashMap.addExY();
            logHashMap.addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.put("expect", "1");
            liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
        }
    }


    /**
     * 学生端打开宝箱结果
     *
     * @param liveAndBackDebug
     * @param goldNum          获得金币数
     * @param nonce            点击宝箱时生成的noce
     * @param ex               借口是否调用成功
     */
    public static void openTreasureBox(LiveAndBackDebug liveAndBackDebug, String goldNum, String nonce, boolean ex) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("openTreasureBox");
            logHashMap.put("gold", goldNum);
            logHashMap.addSno("12");
            logHashMap.addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.addStable("1");
            if (ex) {
                logHashMap.addExY();
            } else {
                logHashMap.addExN();
            }
            liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
        }
    }


    /**
     * 学生端收到班级宝箱开启指令
     *
     * @param liveAndBackDebug
     * @param nonce
     */
    public static void receiveClassBoxInfo(LiveAndBackDebug liveAndBackDebug, String nonce, boolean isOpen) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("receiveClassBoxInfo");
            logHashMap.addSno("14");
            logHashMap.addExY();
            logHashMap.addStable("1");
            logHashMap.addNonce(TextUtils.isEmpty(nonce)?"":nonce);
            logHashMap.put("isopen", isOpen ? "1" : "0");
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        }

    }


    /**
     * 学生端展示班级宝箱信息
     *
     * @param liveAndBackDebug
     * @param isLuckyStar      是否是幸运星
     */
    public static void showClassGoldInfo(LiveAndBackDebug liveAndBackDebug, boolean isLuckyStar) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("showClassGoldInfo");
            logHashMap.addSno("15");
            logHashMap.addExY();
            logHashMap.put("isluckystar", isLuckyStar ? "1" : "0");
            liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
        }

    }


    /**
     * 学生端点击快速入口进入分队仪式
     *
     * @param liveAndBackDebug
     */
    public static void clickFastEnter(LiveAndBackDebug liveAndBackDebug) {
        if (UPLOAD_OPEN && liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap("clickFastEnter");
            liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
        }
    }

}
