package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;

import org.json.JSONObject;

public class ScienceVotePlayBackBll extends LiveBackBaseBll{
    private long questionStopTime;

    public ScienceVotePlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_SCIENCE_VOTE};
    }

    @Override
    public void onPositionChanged(long position) {
        if (questionStopTime > 0 && position >= questionStopTime) {
            questionStopTime = 0;
            // 分发互动题收题动作
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {

        if (questionEntity == null) {
            return;
        }

        try {
            String orgDataStr = questionEntity.getOrgDataStr();
            JSONObject data = new JSONObject(orgDataStr);
            JSONObject properties = data.getJSONObject("properties");
            questionStopTime = data.optInt("endTime");
            int packageId = properties.optInt("packageId");
            String pageIds = data.optString("pageIds");
            int courseWareId = properties.optInt("coursewareId");
            int time = properties.optInt("timeLimit");
            String interactId = properties.optString("interactionId");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
