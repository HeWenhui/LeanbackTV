package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.business.H5CoursewareBll;

import java.util.HashMap;

/**
 * Created by：WangDe on 2018/9/14 10:06
 */
public class NBH5ExperienceBll extends LiveBackBaseBll {
    H5CoursewareBll h5CoursewareBll;

    public NBH5ExperienceBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {

    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_H5COURSE_WARE};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        if (h5CoursewareBll != null) {
            NbCourseWareEntity entity = new NbCourseWareEntity(liveBackBll.getRommInitData().getId(),questionEntity.getH5Play_url(),false);
            h5CoursewareBll.onH5Courseware(entity, "off");
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity,
                             LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_H5COURSE_WARE: {
                if (h5CoursewareBll == null) {
                    h5CoursewareBll = new H5CoursewareBll(mContext,liveBackBll.getRommInitData());
                    h5CoursewareBll.initView(mRootView);
                }
                if (oldQuestionEntity == null || questionEntity == null || !oldQuestionEntity.getvQuestionID().equals(questionEntity.getvQuestionID())) {
                    getCourseHttpManager().sendExpSpeechEvalResult(mVideoEntity.getSpeechEvalSubmitUrl(),
                            mVideoEntity.getLiveId(), questionEntity.getvQuestionID(), mVideoEntity.getChapterId(),
                            "0", "", new HttpCallBack() {

                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                        }
                    });
                }
                NbCourseWareEntity entity = new NbCourseWareEntity(liveBackBll.getRommInitData().getId(),questionEntity.getH5Play_url(),false);
                h5CoursewareBll.onH5Courseware(entity, "on");
                break;
            }
            default:
                break;
        }
    }


    @Override
    public void onDestory() {
        super.onDestory();
        if(h5CoursewareBll != null){
            h5CoursewareBll.onDestory();
        }
    }
}
