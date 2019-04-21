package com.xueersi.parentsmeeting.modules.livevideo.question.irc;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class QueIrcParse {
    public static VideoQuestionLiveEntity parseBigQues(JSONObject object) throws JSONException {
        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
        videoQuestionLiveEntity.id = object.getString("testId");
        videoQuestionLiveEntity.setDotId(object.getString("dotId"));
        videoQuestionLiveEntity.setDotType(object.getInt("dotType"));
        videoQuestionLiveEntity.num = object.getInt("itemNum");
        return videoQuestionLiveEntity;
    }
}
