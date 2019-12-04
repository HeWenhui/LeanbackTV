package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.are.ContextManager;
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
                            liveLogInteractive("2", "1", "receivequickchoice", interactionId);
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
        getHttpManager().ScienceVoteCommit("0", mLiveId, mGetInfo.getLiveType(), mGetInfo.getStudentLiveInfo().getClassId(), interactionId, getUserAnswer(), mLiveBll.getNickname(), mGetInfo.getStuName(), new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("ScienceVoteCommit:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                isAnswer = true;
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                if (jsonObject.optBoolean("isRepeat")) {
                    XESToastUtils.showToast(mContext, "已作答");
                } else {
                    int gold = jsonObject.optInt("gold");
                    if (TextUtils.isEmpty(rightAnswer)) {
                        submitSuccess(0, gold);
                        liveLogInteractive("3", "2", "submitquickchoice", interactionId, "");
                    } else {
                        if (TextUtils.equals(getUserAnswer(), rightAnswer)) {
                            submitSuccess(1, gold);
                            liveLogInteractive("3", "2", "submitquickchoice", interactionId, "right");
                        } else {
                            submitSuccess(2, gold);
                            liveLogInteractive("3", "2", "submitquickchoice", interactionId, "wrong");
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


    public void submitSuccess(final int type, final int gold) {
        final RelativeLayout relativeLayout =
                (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_science_vote_submit, null);
        lottieAnimationView = relativeLayout.findViewById(R.id.livevideo_science_vote_lottie);
        String resPath = "";
        String jsonPath = "";
        if (type == 0) {
            if (gold > 0) {
                resPath = "vote_submit_success_gold/images";
                jsonPath = "vote_submit_success_gold/data.json";
            } else {
                resPath = "vote_submit_success/images";
                jsonPath = "vote_submit_success/data.json";
            }
        } else if (type == 1) {
            if (gold > 0) {
                resPath = "vote_submit_thumb_up_gold/images";
                jsonPath = "vote_submit_thumb_up_gold/data.json";
            } else {
                resPath = "vote_submit_thumb_up/images";
                jsonPath = "vote_submit_thumb_up/data.json";
            }
        } else if (type == 2) {
            resPath = "vote_submit_come_on/images";
            jsonPath = "vote_submit_come_on/data.json";
        }
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath, "img_0.png") {
            @Override
            public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
                if ("img_0.png".equals(fileName) && type != 2) {
                    return createMsgBitmap(width, height, "+" + gold);
                }
                return super.fetchTargetBitMap(animationView, fileName, bitmapId, width, height);
            }
        };
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
            try {
                StableLogHashMap logHashMap = new StableLogHashMap(logType);
                logHashMap.addSno(sno).addStable(table);
                logHashMap.addInteractionId(interactionId);
                logHashMap.put("liveid", mLiveId);
                logHashMap.put("courseid", mGetInfo.getStudentLiveInfo().getCourseId());
                logHashMap.put("gradeid", String.valueOf(mGetInfo.getGrade()));
                String subjects = "";
                if (mGetInfo.getSubjectIds() != null) {
                    String subjectIds[] = mGetInfo.getSubjectIds();
                    for (int i = 0; i < subjectIds.length; i++) {
                        subjects += subjectIds[i];
                        if (i == subjectIds.length - 1) {
                            break;
                        }
                        subjects += ",";
                    }
                }
                logHashMap.put("subjectid", subjects);
                liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void liveLogInteractive(String sno, String table, String logType, String interactionId, String isRight) {
        if (liveAndBackDebug != null) {
            try {
                StableLogHashMap logHashMap = new StableLogHashMap(logType);
                logHashMap.addSno(sno).addStable(table);
                logHashMap.addInteractionId(interactionId);
                logHashMap.put("liveid", mLiveId);
                logHashMap.put("courseid", mGetInfo.getStudentLiveInfo().getCourseId());
                logHashMap.put("gradeid", String.valueOf(mGetInfo.getGrade()));
                String subjects = "";
                if (mGetInfo.getSubjectIds() != null) {
                    String subjectIds[] = mGetInfo.getSubjectIds();
                    for (int i = 0; i < subjectIds.length; i++) {
                        subjects += subjectIds[i];
                        if (i == subjectIds.length - 1) {
                            break;
                        }
                        subjects += ",";
                    }
                }
                logHashMap.put("subjectid", subjects);
                logHashMap.put("answer", isRight);
                liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap createMsgBitmap(int width, int height, String msg) {
        Bitmap resultBitmap = null;
        if (!TextUtils.isEmpty(msg)) {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor("#513C1B"));
            paint.setTextSize(60);
            paint.setTextAlign(Paint.Align.LEFT);
            Typeface fontFace = FontCache.getTypeface(ContextManager.getContext(), "fangzhengcuyuan.ttf");
            if (fontFace != null) {
                paint.setTypeface(fontFace);
            }
            Rect fontRect = new Rect();
            paint.getTextBounds(msg, 0, msg.length(), fontRect);
            int offsetX = Math.max((width - fontRect.width()) / 2, 0);
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int baseLine = (height - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2 - fontMetricsInt.ascent;
            canvas.drawText(msg, offsetX, baseLine, paint);
        }
        return resultBitmap;
    }

}
