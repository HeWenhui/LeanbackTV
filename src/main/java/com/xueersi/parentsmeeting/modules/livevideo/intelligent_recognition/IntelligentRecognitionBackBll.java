package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.app.Activity;
import android.os.Bundle;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;

public class IntelligentRecognitionBackBll extends LiveBackBaseBll {
    public IntelligentRecognitionBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity,
                             LiveBackBll.ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        switch (questionEntity.getvCategory()) {
            case LocalCourseConfig.CATEGORY_BIG_TEST: {

                Bundle bundle = new Bundle();
                IntelligentRecognitionRecord intelligentRecognitionRecord = new IntelligentRecognitionRecord();
//                intelligentRecognitionRecord.setAnswerTime(data.optString("time"));
//                intelligentRecognitionRecord.setStuId(mGetInfo.getStuId());
//                intelligentRecognitionRecord.setStuCouId(mGetInfo.getStuCouId());
//                intelligentRecognitionRecord.setLiveId(mGetInfo.getId());
//                JSONArray jsonArray = data.optJSONArray("id");
//                if (jsonArray != null && jsonArray.length() > 0) {
//                    intelligentRecognitionRecord.setMaterialId(jsonArray.optString(0));
//                }
//                intelligentRecognitionRecord.setIsPlayBack("1");
//                if (mGetInfo.getStudentLiveInfo() != null) {
//                    intelligentRecognitionRecord.setClassId(mGetInfo.getStudentLiveInfo().getClassId());
//                    intelligentRecognitionRecord.setTeamId(mGetInfo.getStudentLiveInfo().getTeamId());
//                }
                bundle.putParcelable("intelligentRecognitionRecord", intelligentRecognitionRecord);
                XueErSiRouter.startModule(activity, "/english/intelligent_recognition", bundle);
                break;
            }
            case LocalCourseConfig.CATEGORY_SUPER_SPEAKER: {
                break;
            }
        }
    }

}
