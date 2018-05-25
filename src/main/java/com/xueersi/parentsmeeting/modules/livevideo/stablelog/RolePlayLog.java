
package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import android.content.Context;

import com.xueersi.parentsmeeting.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.xesalib.umsagent.UmsAgentManager;
import com.xueersi.xesalib.umsagent.UmsConstants;
import com.xueersi.xesalib.utils.log.Loger;


/**
 * Created by yzl on 2018/4/24.
 * roleplay日志
 * roleplay 2. 学生链接websocket 展示日志
 */

public class RolePlayLog {
    private static String eventId = LiveVideoConfig.LIVE_ROLE_PLAY;


    /**
     * roleplay 2. 学生链接websocket 展示日志
     */

    public static void sno2(LiveAndBackDebug liveAndBackDebug,String testId, Context context) {
        if(liveAndBackDebug == null){
            Loger.i("RolePlayerDemoTestlog", " liveAndBackDebug 为空，不记录日志 ");
            return;
        }
        StableLogHashMap logHashMap = new StableLogHashMap("websocketconnected");
        logHashMap.put("testid", "" + testId);
        logHashMap.addExY().addSno("2");
        liveAndBackDebug.umsAgentDebugPv(eventId,logHashMap.getData());
    }

    /**
     * roleplay 4. 用户弹出答题框 展示日志
     */

    public static void sno4(LiveAndBackDebug liveAndBackDebug,VideoQuestionLiveEntity videoQuestionLiveEntity, Context context) {
        if(liveAndBackDebug == null || videoQuestionLiveEntity == null){
            Loger.i("RolePlayerDemoTestlog", " liveAndBackDebug 或 videoQuestionLiveEntity，不记录日志 ");
            return;
        }
        StableLogHashMap logHashMap = new StableLogHashMap("showmutirole");
        logHashMap.put("testid",  "" + videoQuestionLiveEntity.getvQuestionID());
        logHashMap.addExY().addSno("4");
        liveAndBackDebug.umsAgentDebugPv(eventId,logHashMap.getData());
    }

    /**
     * roleplay 6. 用户提交结果 交互日志
     */

    public static void sno6(LiveAndBackDebug liveAndBackDebug,RolePlayerEntity rolePlayerEntity, Context context) {
        if(liveAndBackDebug == null || rolePlayerEntity == null){
            Loger.i("RolePlayerDemoTestlog", " liveAndBackDebug 或 rolePlayerEntity为空，不记录日志 ");
            return;
        }
        StableLogHashMap logHashMap = new StableLogHashMap("submitmutirole");
        logHashMap.put("goldnum", "" + rolePlayerEntity.getGoldCount());
        logHashMap.put("starnum", "" + rolePlayerEntity.getSelfRoleHead().getResultStar());
        logHashMap.put("totalscore", "" + rolePlayerEntity.getSelfRoleHead().getSpeechScore());
        logHashMap.put("speaktime", "" + rolePlayerEntity.getSelfValidSpeechTime());
        logHashMap.put("testid", "" + rolePlayerEntity.getTestId());
        logHashMap.addExY().addSno("6");
        liveAndBackDebug.umsAgentDebugInter(eventId,logHashMap.getData());
        Loger.i("RolePlayerDemoTestlog", " speaktime =  "+rolePlayerEntity.getSelfValidSpeechTime());
    }

    /**
     * roleplay 7. 用户结果展示 展示日志
     */

    public static void sno7(LiveAndBackDebug liveAndBackDebug,RolePlayerEntity rolePlayerEntity, Context context) {
        if(liveAndBackDebug == null || rolePlayerEntity == null){
            Loger.i("RolePlayerDemoTestlog", " liveAndBackDebug 或 rolePlayerEntity为空，不记录日志 ");
            return;
        }
        StableLogHashMap logHashMap = new StableLogHashMap("showmutirolersultdlg");
        logHashMap.put("testid", "" + rolePlayerEntity.getTestId());
        logHashMap.addExY().addSno("7");
        liveAndBackDebug.umsAgentDebugPv(eventId,logHashMap.getData());
    }

    /**
     * roleplay 8. 点击播放音频  交互日志
     */

    public static void sno8(LiveAndBackDebug liveAndBackDebug,RolePlayerEntity.RolePlayerMessage rolePlayermsg, Context context) {
        if(liveAndBackDebug == null || rolePlayermsg == null){
            Loger.i("RolePlayerDemoTestlog", " liveAndBackDebug 或 rolePlayermsg，不记录日志 ");
            return;
        }
        StableLogHashMap logHashMap = new StableLogHashMap("playoneaudio");
        logHashMap.put("testid", "" + rolePlayermsg.getTestId());
        logHashMap.addExY().addSno("8");
        liveAndBackDebug.umsAgentDebugInter(eventId,logHashMap.getData());
    }
}

