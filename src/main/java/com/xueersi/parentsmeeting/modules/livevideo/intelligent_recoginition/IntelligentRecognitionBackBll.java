package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recoginition;

import android.app.Activity;
import android.os.Bundle;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.aievaluation.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;

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
//                intelligentRecognitionRecord.setAnswerTime(questionEntity.get);
                intelligentRecognitionRecord.setStuId(liveGetInfo.getStuId());
                intelligentRecognitionRecord.setStuCouId(liveGetInfo.getStuCouId());
                intelligentRecognitionRecord.setLiveId(liveGetInfo.getId());
//                JSONArray jsonArray = data.optJSONArray("id");
//                if (jsonArray != null && jsonArray.length() > 0) {
                intelligentRecognitionRecord.setMaterialId(questionEntity.getvQuestionID());
//                }
                intelligentRecognitionRecord.setIsPlayBack("1");
//                if (liveGetInfo.getStudentLiveInfo() != null) {
//                    intelligentRecognitionRecord.setClassId(liveGetInfo.getStudentLiveInfo().getClassId());
//                    intelligentRecognitionRecord.setTeamId(liveGetInfo.getStudentLiveInfo().getTeamId());
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
