package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.liveback;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.SuperSpeakerRedPackageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page.SuperSpeakerPopWindowPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.SuperSpeakerBridge;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;

import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_SUPER_SPEAKER;
import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils.audioUrl;

public class SuperSpeakerBackBll extends LiveBackBaseBll implements ISuperSpeakerContract.ICameraPresenter {

    public SuperSpeakerBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        super.onQuestionEnd(questionEntity);

    }

    private String srcType, coursewareId;

    private SuperSpeakerBridge superSpeakerBridge;

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
//        SuperSpeakerCameraPager cameraPager = new SuperSpeakerCameraPager(mContext, this);
        srcType = questionEntity.getSrcType();
        coursewareId = questionEntity.getvQuestionID();

        int uploadStatus = questionEntity.getIsupload();
        SuperSpeakerPopWindowPager superSpeakerPopWindowPager;
        if (uploadStatus == 1) {
            superSpeakerPopWindowPager = new SuperSpeakerPopWindowPager(mContext);
            superSpeakerPopWindowPager.setTextTip("你已提交过视频");
            addPopWindowPager(superSpeakerPopWindowPager.getRootView());
        } else {
            uploadStatus = ShareDataManager.getInstance().getInt(
                    ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveGetInfo.getId() + "_" + coursewareId,
                    0,
                    ShareDataManager.SHAREDATA_NOT_CLEAR);
            if (uploadStatus == 0) {
                superSpeakerBridge = new SuperSpeakerBridge(mContext, this, mRootView, liveGetInfo.getId(), coursewareId);
                superSpeakerBridge.performShowRecordCamera(questionEntity.getAnswerTime(), questionEntity.getRecordTime());
            } else if (uploadStatus == 2) {
                superSpeakerPopWindowPager = new SuperSpeakerPopWindowPager(mContext);
                superSpeakerPopWindowPager.setTextTip("你已提交过视频");
                addPopWindowPager(superSpeakerPopWindowPager.getRootView());
            } else {
                superSpeakerPopWindowPager = new SuperSpeakerPopWindowPager(mContext);
                superSpeakerPopWindowPager.setTextTip("视频后台上传中");
                addPopWindowPager(superSpeakerPopWindowPager.getRootView());
            }
        }
    }

    private void addPopWindowPager(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mRootView.addView(view, layoutParams);
    }

    @Override
    public int[] getCategorys() {
        return new int[]{CATEGORY_SUPER_SPEAKER};
    }

    @Override
    public void submitSpeechShow(String isForce, String videoDuration) {
        getCourseHttpManager().sendSuperSpeakersubmitSpeech(
                liveGetInfo.getId(),
                liveGetInfo.getStuCouId(),
                liveGetInfo.getStuId(),
                "2",
                coursewareId,
                srcType,
                isForce,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        SuperSpeakerRedPackageEntity entity = getCourseHttpResponseParser().parseSuperSpeakerSubmitEntity(responseEntity);
                        if (superSpeakerBridge != null) {
                            superSpeakerBridge.updateNum(entity.getMoney());
                        }
                    }
                });
    }

    @Override
    public void stopRecord() {

    }

    @Override
    public void uploadSucess(String videoUrl, String audioUrkl, String averVocieDecibel) {
        getCourseHttpManager().uploadSpeechShow(
                liveGetInfo.getId(),
                liveGetInfo.getStuCouId(),
                liveGetInfo.getStuId(),
                "2",
                coursewareId,
                srcType,
                videoUrl,
                audioUrl,
                "1",
                averVocieDecibel,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    }
                }
        );
    }
}
