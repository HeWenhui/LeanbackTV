package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.learnfeedback;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;

import org.json.JSONObject;

public interface LearnFeedBackContract {

    interface ISendHttp extends IPresenter {
        void sendHttp(String useId, String liveId, String subjectId, String gradId, String chapterId, String
                suggest, JSONObject jsonObject, HttpCallBack httpCallBack);
    }
}
