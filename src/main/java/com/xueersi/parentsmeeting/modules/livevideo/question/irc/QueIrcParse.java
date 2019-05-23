package com.xueersi.parentsmeeting.modules.livevideo.question.irc;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class QueIrcParse {
    public static VideoQuestionLiveEntity parseBigQues(JSONObject object) throws JSONException {
        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
        videoQuestionLiveEntity.id = object.getString("testId");
        videoQuestionLiveEntity.setDotId(object.getString("dotId"));
        videoQuestionLiveEntity.setDotType(object.getInt("dotType"));
        videoQuestionLiveEntity.setSrcType(object.getString("srcType"));
        videoQuestionLiveEntity.nonce = object.optString("nonce");
        if (videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_FILL) {
            videoQuestionLiveEntity.num = object.getInt("itemNum");
        } else {
            videoQuestionLiveEntity.num = LiveQueConfig.DOTTYPE_SELE_NUM;
        }
        return videoQuestionLiveEntity;
    }
}
