package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.notice.LiveAutoNoticeIRCBll;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by lyqai on 2018/7/5.
 */

public class EnglishH5CoursewareIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private EnglishH5CoursewareBll englishH5CoursewareAction;
    private AnswerRankIRCBll mAnswerRankBll;
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;

    public EnglishH5CoursewareIRCBll(Activity context, LiveBll2 liveBll, ViewGroup rootView) {
        super(context, liveBll, rootView);
        englishH5CoursewareAction = new EnglishH5CoursewareBll(context);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mAnswerRankBll = getInstance(AnswerRankIRCBll.class);
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            if (englishH5CoursewareAction != null && jsonObject.has("H5_Courseware")) {
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                JSONObject h5_Experiment = jsonObject.getJSONObject("H5_Courseware");
                String play_url = "";
                String status = h5_Experiment.optString("status", "off");
                String id = "";
                String courseware_type = "";
                if ("on".equals(status)) {
                    id = h5_Experiment.getString("id");
                    courseware_type = h5_Experiment.getString("courseware_type");
                    play_url = mLiveBll.getLiveVideoSAConfig().inner.coursewareH5 + mLiveId + "/" + mLiveId + "/" + id +
                            "/" + courseware_type
                            + "/" + mGetInfo.getStuId();
                    videoQuestionLiveEntity.id = id;
                    videoQuestionLiveEntity.courseware_type = courseware_type;
                    videoQuestionLiveEntity.setUrl(play_url);
                    videoQuestionLiveEntity.nonce = "";
                    String isVoice = h5_Experiment.optString("isVoice");
                    videoQuestionLiveEntity.setIsVoice(isVoice);
                    if ("1".equals(isVoice)) {
                        videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = h5_Experiment
                                .optString("questiontype");
                        videoQuestionLiveEntity.assess_ref = h5_Experiment.optString("assess_ref");
                    }
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mAnswerRankBll.setType(videoQuestionLiveEntity.courseware_type);
                    }
                    if (mLiveAutoNoticeBll != null) {
                        mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.courseware_type);
                    }
                }
                englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onNotice(JSONObject object, int type) {
        switch (type) {
            case XESCODE.ENGLISH_H5_COURSEWARE:
                try {
                    if (englishH5CoursewareAction != null) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                        String play_url = "";
                        String status = object.optString("status", "off");
                        String nonce = object.optString("nonce");
                        String id = "";
                        String courseware_type = "";
                        if ("on".equals(status)) {
                            id = object.getString("id");
                            courseware_type = object.getString("courseware_type");
                            play_url = mLiveBll.getLiveVideoSAConfig().inner.coursewareH5 + mLiveId + "/" + mLiveId + "/"
                                    + id + "/" + courseware_type
                                    + "/" + mGetInfo.getStuId();
                            videoQuestionLiveEntity.id = id;
                            videoQuestionLiveEntity.courseware_type = courseware_type;
                            videoQuestionLiveEntity.setUrl(play_url);
                            videoQuestionLiveEntity.nonce = nonce;
                            String isVoice = object.optString("isVoice");
                            videoQuestionLiveEntity.setIsVoice(isVoice);
                            if ("1".equals(isVoice)) {
                                videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = object
                                        .optString("questiontype");
                                videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                            }
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                mAnswerRankBll.setType(videoQuestionLiveEntity.courseware_type);
                            }
                            if (mLiveAutoNoticeBll != null) {
                                mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.courseware_type);
                            }
                            if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                                ((EnglishH5CoursewareBll) englishH5CoursewareAction).setWebViewCloseByTeacher
                                        (false);
                            }
                        } else {
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setNonce(object.optString("nonce"));
                            }
                            if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                                ((EnglishH5CoursewareBll) englishH5CoursewareAction).setWebViewCloseByTeacher(true);
                            }
                        }
                        englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.ENGLISH_H5_COURSEWARE};
    }
}
