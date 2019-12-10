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
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class ScienceVotePlayBackBll extends LiveBackBaseBll {
    private long questionStopTime;
    private static final String VOTE_STATE_OPEN = "open";
    private static final String VOTE_STATE_CLOSE = "close";
    private String rightAnswer;
    private String interactionId;
    ScienceVotePager scienceVotePager;
    String liveId;
    String nickname;
    private static String eventId = "p-quickchoice";
    private LottieAnimationView lottieAnimationView;

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
        if (questionStopTime > 0 && position >= questionStopTime) {
            questionStopTime = 0;
            // 分发互动题收题动作
            if (!TextUtils.isEmpty(getUserAnswer())) {
                submitResult();
            } else {
                closeView();
            }
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {

        if (questionEntity == null) {
            return;
        }

        try {
            String orgDataStr = questionEntity.getOrgDataStr();
            JSONObject data = new JSONObject(orgDataStr);
            questionStopTime = data.optInt("endtime");

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
            }
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }

    private void showChoice(final JSONArray jsonArray) {
        post(new Runnable() {
            @Override
            public void run() {
                if (scienceVotePager != null) {
                    scienceVotePager.destroyView();
                    removeView(scienceVotePager.getRootView());
                    scienceVotePager = null;
                }
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
        getmHttpManager().ScienceVoteCommit("1", liveId, liveGetInfo.getLiveType(), liveGetInfo.getStudentLiveInfo().getClassId(), interactionId, getUserAnswer(), nickname, liveGetInfo.getStuName(), new HttpCallBack(true) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("ScienceVoteCommit:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
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
                    rightAnswer = "";
                    scienceVotePager.destroyView();
                    removeView(scienceVotePager.getRootView());
                    scienceVotePager = null;
                }
            }
        });
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
            if (gold > 0) {
                resPath = "vote_submit_come_on_gold/images";
                jsonPath = "vote_submit_come_on_gold/data.json";
            } else {
                resPath = "vote_submit_come_on/images";
                jsonPath = "vote_submit_come_on/data.json";
            }
        }
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath, "img_0.png") {
            @Override
            public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName, String bitmapId, int width, int height) {
                if ("img_0.png".equals(fileName) && gold > 0) {
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
        if (contextLiveAndBackDebug != null) {
            try {
                StableLogHashMap logHashMap = new StableLogHashMap(logType);
                logHashMap.addSno(sno).addStable(table);
                logHashMap.addInteractionId(interactionId);
                logHashMap.put("liveid", liveId);
                logHashMap.put("courseid", liveGetInfo.getStudentLiveInfo().getCourseId());
                logHashMap.put("gradeid", String.valueOf(liveGetInfo.getGrade()));
                String subjects = "";
                if (liveGetInfo.getSubjectIds() != null) {
                    String subjectIds[] = liveGetInfo.getSubjectIds();
                    for (int i = 0; i < subjectIds.length; i++) {
                        subjects += subjectIds[i];
                        if (i == subjectIds.length - 1) {
                            break;
                        }
                        subjects += ",";
                    }
                }
                logHashMap.put("subjectid", subjects);
                contextLiveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void liveLogInteractive(String sno, String table, String logType, String interactionId, String isRight) {
        if (contextLiveAndBackDebug != null) {
            try {
                StableLogHashMap logHashMap = new StableLogHashMap(logType);
                logHashMap.addSno(sno).addStable(table);
                logHashMap.addInteractionId(interactionId);
                logHashMap.put("liveid", liveId);
                logHashMap.put("courseid", liveGetInfo.getStudentLiveInfo().getCourseId());
                logHashMap.put("gradeid", String.valueOf(liveGetInfo.getGrade()));
                String subjects = "";
                if (liveGetInfo.getSubjectIds() != null) {
                    String subjectIds[] = liveGetInfo.getSubjectIds();
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
                contextLiveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);
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
