package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

import org.json.JSONArray;

public class BigResultLog {
    static String live_eventId = LogConfig.LIVE_PLAT_DOT;
    static String back_eventId = LogConfig.LIVE_BACK_PLAT_DOT;

    public static void sno3(String isOpen, VideoQuestionLiveEntity videoQuestionLiveEntity, BaseLiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("recieveBigInteractDot");
        logHashMap.put("isOpen", "" + isOpen);
        logHashMap.put("testid", "" + videoQuestionLiveEntity.id);
        logHashMap.put("dotId", "" + videoQuestionLiveEntity.getDotId());
        logHashMap.put("dotType", "" + videoQuestionLiveEntity.getDotType());
        logHashMap.put("itemNum", "" + videoQuestionLiveEntity.num);
        logHashMap.addSno("3").addExY().addStable("1").addNonce(videoQuestionLiveEntity.nonce);
        String eventId = videoQuestionLiveEntity.isLive() ? live_eventId : back_eventId;
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    public static void sno4(VideoQuestionLiveEntity videoQuestionLiveEntity, BaseLiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("showBigInteractDot");
        logHashMap.put("testid", "" + videoQuestionLiveEntity.id);
        logHashMap.put("dotId", "" + videoQuestionLiveEntity.getDotId());
        logHashMap.put("dotType", "" + videoQuestionLiveEntity.getDotType());
        logHashMap.put("itemNum", "" + videoQuestionLiveEntity.num);
        logHashMap.addSno("4").addExY().addStable("1").addNonce(videoQuestionLiveEntity.nonce);
        String eventId = videoQuestionLiveEntity.isLive() ? live_eventId : back_eventId;
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    public static void sno5(VideoQuestionLiveEntity videoQuestionLiveEntity, JSONArray stuAnswer, boolean ex, BaseLiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("submitBigInteractDotAnswerToPhp");
        logHashMap.put("testid", "" + videoQuestionLiveEntity.id);
        logHashMap.put("dotId", "" + videoQuestionLiveEntity.getDotId());
        logHashMap.put("dotType", "" + videoQuestionLiveEntity.getDotType());
        logHashMap.put("stuAnswer", "" + stuAnswer);
        logHashMap.addSno("5").addEx(ex).addStable("2").addNonce(StableLogHashMap.creatNonce());
        String eventId = videoQuestionLiveEntity.isLive() ? live_eventId : back_eventId;
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    public static void sno6(VideoQuestionLiveEntity videoQuestionLiveEntity, Object data, boolean ex, BaseLiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("recieveBigInteractDotResult");
        logHashMap.put("testid", "" + videoQuestionLiveEntity.id);
        logHashMap.put("dotId", "" + videoQuestionLiveEntity.getDotId());
        logHashMap.put("dotType", "" + videoQuestionLiveEntity.getDotType());
        logHashMap.put("resultData", "" + data);
        logHashMap.addSno("6").addEx(ex).addStable("1").addNonce(StableLogHashMap.creatNonce());
        String eventId = videoQuestionLiveEntity.isLive() ? live_eventId : back_eventId;
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }
}
