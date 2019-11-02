package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class ScienceVotePlayBackBll extends LiveBackBaseBll {
    private long questionStopTime;
    private static final String VOTE_STATE_OPEN = "open";
    private static final String VOTE_STATE_CLOSE = "close";
    private String rightAnswer;
    private String interactionId;
    private boolean isAnswer = false;
    ScienceVotePager scienceVotePager;
    String liveId;
    String nickname;

    public ScienceVotePlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_SCIENCE_VOTE};
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        liveId = mVideoEntity.getLiveId();
        nickname = "s_" + liveGetInfo.getLiveType() + "_"
                + liveGetInfo.getId() + "_" + liveGetInfo.getStuId() + "_" + liveGetInfo.getStuSex();
    }

    @Override
    public void onPositionChanged(long position) {
//        if (questionStopTime > 0 && position >= questionStopTime) {
//            questionStopTime = 0;
//            // 分发互动题收题动作
//            closeView();
//        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {

        if (questionEntity == null) {
            return;
        }

        try {
            String orgDataStr = questionEntity.getOrgDataStr();
            JSONObject data = new JSONObject(orgDataStr);
//            JSONObject properties = data.getJSONObject("properties");
//            questionStopTime = data.optInt("endTime");

            String open = data.optString("open");
            interactionId = data.optString("id");
            if (TextUtils.equals(VOTE_STATE_OPEN, open)) {
                JSONArray optionsJSONArray = data.optJSONArray("options");
                showChoice(optionsJSONArray);
                for (int i = 0; i < optionsJSONArray.length(); i++) {
                    JSONObject optionsJSONObject = optionsJSONArray.getJSONObject(i);
                    if (TextUtils.equals(optionsJSONObject.optString("right"), "1")) {
                        rightAnswer = optionsJSONObject.optString("option");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showChoice(final JSONArray jsonArray) {
        post(new Runnable() {
            @Override
            public void run() {
                scienceVotePager = new ScienceVotePager(mContext, jsonArray, new ScienceVoteBll.ScienceVoteBllBack() {
                    @Override
                    public void submit() {
                        submitResult();
                    }
                });
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                addView(LiveVideoLevel.LEVEL_QUES, scienceVotePager.getRootView(), layoutParams);
            }
        });
    }

    private String getUserAnswer() {
        if (scienceVotePager != null) {
            return scienceVotePager.userAnswer;
        }
        return "";
    }

    private void submitResult() {
        getmHttpManager().ScienceVoteCommit(liveId, liveGetInfo.getStudentLiveInfo().getClassId(), interactionId, getUserAnswer(), nickname, liveGetInfo.getStuName(), new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("ScienceVoteCommit:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                isAnswer = true;
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                if (jsonObject.optBoolean("isRepeat")) {
                    XESToastUtils.showToast(mContext, "已作答");
                } else {
                    if (TextUtils.isEmpty(rightAnswer)) {
                        if (scienceVotePager != null) {
                            scienceVotePager.submitSuccess(0);
                        }
                    } else {
                        if (TextUtils.equals(getUserAnswer(), rightAnswer)) {
                            if (scienceVotePager != null) {
                                scienceVotePager.submitSuccess(1);
                            }
                        } else {
                            if (scienceVotePager != null) {
                                scienceVotePager.submitSuccess(2);
                            }
                        }
                    }
                }
                closeView();
            }
        });
    }

    private void closeView() {
        post(new Runnable() {
            @Override
            public void run() {
                if (scienceVotePager != null) {
                    isAnswer = false;
                    rightAnswer = "";
                    scienceVotePager.destroyView();
                    removeView(scienceVotePager.getRootView());
                }
            }
        });
    }
}
