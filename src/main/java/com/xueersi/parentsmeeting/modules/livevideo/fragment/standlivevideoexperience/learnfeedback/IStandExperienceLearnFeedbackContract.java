package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.learnfeedback;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;

import org.json.JSONArray;

public interface IStandExperienceLearnFeedbackContract {

    interface ISendHttp extends IPresenter {
        void sendHttp(String useId, String liveId, String subjectId, String gradId, String chapterId, String
                suggest, JSONArray jsonObject, HttpCallBack httpCallBack);
    }
}
