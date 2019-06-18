package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.learnfeedback;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.IExperiencePresenter;

import org.json.JSONArray;

public interface IStandExperienceLearnFeedbackContract {

    interface IExperienceSendHttp extends IExperiencePresenter {
        void sendHttp(String useId, String liveId, String subjectId, String gradId, String chapterId, String
                suggest, JSONArray jsonObject, HttpCallBack httpCallBack);
    }
}
