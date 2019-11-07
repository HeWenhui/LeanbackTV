package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScienceVoteBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private static final String VOTE_STATE_OPEN = "open";
    private static final String VOTE_STATE_CLOSE = "close";
    private String rightAnswer;
    private String interactionId;
    private boolean hasNotice = false;
    private boolean isAnswer = false;
    ScienceVotePager scienceVotePager;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    private ContextLiveAndBackDebug liveAndBackDebug;
    private static String eventId = "quickchoice";
    private LottieAnimationView lottieAnimationView;

    public ScienceVoteBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        liveMediaControllerBottom = getInstance(BaseLiveMediaControllerBottom.class);
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.e("=====>onNotice =:" + data.toString());
        if (LiveTopic.MODE_TRANING.equals(mGetInfo.getMode())) {
            closeView();
        } else {
            try {
                switch (type) {
                    case XESCODE.SCIENCE_VOTE:
                        hasNotice = true;
                        String open = data.optString("open");
                        interactionId = data.optString("id");
                        if (TextUtils.equals(VOTE_STATE_OPEN, open)) {
                            JSONArray optionsJSONArray = data.optJSONArray("options");
                            showChoice(optionsJSONArray);
                            liveLogInteractive("1", "1", "receivequickchoice", interactionId);
                            for (int i = 0; i < optionsJSONArray.length(); i++) {
                                JSONObject optionsJSONObject = optionsJSONArray.getJSONObject(i);
                                if (TextUtils.equals(optionsJSONObject.optString("right"), "1")) {
                                    rightAnswer = optionsJSONObject.optString("option");
                                }
                            }
                        } else if (TextUtils.equals(VOTE_STATE_CLOSE, open)) {
                            if (isAnswer) {
                                closeView();
                            } else {
                                if (!TextUtils.isEmpty(getUserAnswer())) {
                                    submitResult();
                                } else {
                                    closeView();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showChoice(final JSONArray jsonArray) {
        post(new Runnable() {
            @Override
            public void run() {
                if (scienceVotePager != null) {
                    isAnswer = false;
                    scienceVotePager.destroyView();
                    removeView(scienceVotePager.getRootView());
                    scienceVotePager = null;
                }
                if (liveMediaControllerBottom.getController() != null &&
                        liveMediaControllerBottom instanceof LiveMediaControllerBottom) {
                    ((LiveMediaControllerBottom) liveMediaControllerBottom).interceptHideBtmMediaCtr(true);
                }
                scienceVotePager = new ScienceVotePager(mContext, jsonArray, new ScienceVoteBllBack() {
                    @Override
                    public void submit() {
                        submitResult();
                    }
                });
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                addView(LiveVideoLevel.LEVEL_QUES, scienceVotePager.getRootView(), layoutParams);
            }
        });
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.SCIENCE_VOTE};
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        logger.e("=====>onTopic =:" + jsonObject.toString());
        if (!hasNotice) {
            if (LiveTopic.MODE_TRANING.equals(mGetInfo.getMode())) {
                closeView();
            } else {
                try {
                    JSONObject room_1 = jsonObject.optJSONObject("room_1");
                    if (room_1 != null) {
                        final JSONObject dataJson = room_1.optJSONObject("vote_test");
                        if (dataJson != null) {
                            String open = dataJson.optString("open");
                            interactionId = dataJson.optString("id");
                            if (TextUtils.equals(VOTE_STATE_OPEN, open)) {
                                JSONArray optionsJSONArray = dataJson.optJSONArray("options");
                                showChoice(optionsJSONArray);
                                for (int i = 0; i < optionsJSONArray.length(); i++) {
                                    JSONObject optionsJSONObject = optionsJSONArray.getJSONObject(i);
                                    if (TextUtils.equals(optionsJSONObject.optString("right"), "1")) {
                                        rightAnswer = optionsJSONObject.optString("option");
                                    }
                                }
                            } else if (TextUtils.equals(VOTE_STATE_CLOSE, open)) {
                                if (isAnswer) {
                                    closeView();
                                } else {
                                    if (!TextUtils.isEmpty(getUserAnswer())) {
                                        submitResult();
                                    } else {
                                        closeView();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        hasNotice = false;
    }

    private String getUserAnswer() {
        if (scienceVotePager != null) {
            return scienceVotePager.userAnswer;
        }
        return "";
    }

    private void submitResult() {
        getHttpManager().ScienceVoteCommit(mLiveId, mGetInfo.getStudentLiveInfo().getClassId(), interactionId, getUserAnswer(), mLiveBll.getNickname(), mGetInfo.getStuName(), new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("ScienceVoteCommit:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                isAnswer = true;
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                if (jsonObject.optBoolean("isRepeat")) {
                    XESToastUtils.showToast(mContext, "已作答");
                } else {
                    if (TextUtils.isEmpty(rightAnswer)) {
                        submitSuccess(0);
                        liveLogInteractive("2", "2", "submitquickchoice", interactionId, "");
                    } else {
                        if (TextUtils.equals(getUserAnswer(), rightAnswer)) {
                            submitSuccess(1);
                            liveLogInteractive("2", "2", "submitquickchoice", interactionId, "right");
                        } else {
                            submitSuccess(2);
                            liveLogInteractive("2", "2", "submitquickchoice", interactionId, "wrong");
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
                    scienceVotePager = null;
                    if (liveMediaControllerBottom.getController() != null &&
                            liveMediaControllerBottom instanceof LiveMediaControllerBottom) {
                        ((LiveMediaControllerBottom) liveMediaControllerBottom).interceptHideBtmMediaCtr(false);
                    }
                }
            }
        });
    }

    public interface ScienceVoteBllBack {
        void submit();
    }


    public void submitSuccess(int type) {
        final RelativeLayout relativeLayout =
                (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_science_vote_submit, null);
        lottieAnimationView = relativeLayout.findViewById(R.id.livevideo_science_vote_lottie);
        String resPath = "";
        String jsonPath = "";
        if (type == 0) {
            resPath = "vote_submit_success/images";
            jsonPath = "vote_submit_success/data.json";
        } else if (type == 1) {
            resPath = "vote_submit_thumb_up/images";
            jsonPath = "vote_submit_thumb_up/data.json";
        } else if (type == 2) {
            resPath = "vote_submit_come_on/images";
            jsonPath = "vote_submit_come_on/data.json";
        }
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        lottieAnimationView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext));
        lottieAnimationView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(lottieAnimationView, fileName,
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
                return bitmap;
            }
        };
        lottieAnimationView.setImageAssetDelegate(imageAssetDelegate);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(relativeLayout, layoutParams);
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                removeView(relativeLayout);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 日志
     *
     * @param sno
     * @param table
     * @param logType
     */
    public void liveLogInteractive(String sno, String table, String logType, String interactionId) {
        if (liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap(logType);
            logHashMap.addSno(sno).addStable(table);
            logHashMap.addInteractionId(interactionId);
            logHashMap.put("", "");
            liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
        }
    }

    public void liveLogInteractive(String sno, String table, String logType, String interactionId, String isRight) {
        if (liveAndBackDebug != null) {
            StableLogHashMap logHashMap = new StableLogHashMap(logType);
            logHashMap.addSno(sno).addStable(table);
            logHashMap.addInteractionId(interactionId);
            logHashMap.put("isRight", isRight);
            liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
        }
    }
}
