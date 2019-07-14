package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.alibaba.fastjson.JSON;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.util.FontCache;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ScoreRange;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.VoiceAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文科答题结果
 *
 * @author chenkun
 * @version 1.0, 2018/7/27 下午5:36
 */

public class ArtsAnswerResultBll extends LiveBaseBll implements NoticeAction, AnswerResultStateListener {
    private static final String TAG = "ArtsAnswerResultBll";

    /**
     * 语文跟读
     */
    private static final int ARTS_FOLLOW_UP = 6;

    /**
     * 强制收卷 答题结果展示 时间
     **/
    private final long AUTO_CLOSE_DELAY = 2000;
    /**
     * 普通 统计 UI
     */
    private static final int UI_TYPE_NORMAL = 1;
    /**
     * 小学英语 统计面板
     */
    private static final int UI_TYPE_PSE = 2;
    /**
     * 答题全对
     */
    private static final int ANSWER_RESULT_ALL_RIGHT = 2;

    private IArtsAnswerRsultDisplayer mDsipalyer;

    /** 当前答题结果 */
    private AnswerResultEntity mAnswerReulst;


    /** 用户在直播间内所有非语音答题结果 */
    private List<AnswerResultEntity> mAnswerResultList = new ArrayList<>();

    /** 用户在当前直播间内所有语音题 答题结果 */
    private List<VoiceAnswerResultEvent> mVoiceAnswerResultList = new ArrayList<>();

    /**
     * 提示提交展示时间
     */
    private final long REMIND_UI_CLOSE_DELAY = 3000;
    /**
     * 是否是小学英语
     */
    private boolean isPse;
    private View remindView;

    private ViewGroup decorView;
    private View praiseRootView;
    private boolean isPerfectRight;
    private HashMap<Integer, ScoreRange> mScoreRangeMap;
    /** 是否需要更新右侧金币数 */
    private boolean shoulUpdateGold;
    private boolean close = false;

    /**
     * 当前语音题的答题结果
     */
    //private VoiceAnswerResultEvent mVoiceAnswerResult;
    /**
     * 是否正在展示表扬
     */
    private boolean praiseViewShowing;
    private ArtsAnswerResultEvent mArtsAnswerResultEvent;
    private LiveSoundPool mLiveSoundPool;
    private RelativeLayout mRlResult;

    /**
     * @param context
     * @param liveBll
     * @param rootView
     * @param isPse    是否是小学英语
     */
    public ArtsAnswerResultBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }


    /**
     * 用于直播回放
     *
     * @param context
     */
    public ArtsAnswerResultBll(Activity context, String liveId, int liveType) {
        super(context, liveId, liveType);
    }

    public void setLiveViewAction(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
    }

    private void attachToView() {
        EventBus.getDefault().register(this);
    }

    private void addPager(ArtsAnswerResultEvent event) {
        //logger.e("ArtsAnswerResultBll:addPager:" + mDsipalyer);

        if (mDsipalyer != null) {
            return;
        }

        if (isPse) {
            mDsipalyer = new ArtsPSEAnswerResultPager(mContext, mAnswerReulst, this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mDsipalyer.getRootLayout(), layoutParams);
        } else {
            UmsAgentManager.umsAgentDebug(mContext, "createViceResultView_result", JSON.toJSONString(mAnswerResultList));

            mDsipalyer = new ArtsAnswerResultPager(mContext, mAnswerReulst, this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mDsipalyer.getRootLayout(), layoutParams);
        }

        // logger.e( "==========> ArtsAnswerResultBll addPager called:");
    }

    /**
     * 展示答题结果
     */
    private void showAnswerReulst(final ArtsAnswerResultEvent event) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
                    showH5Result(mAnswerReulst);
                    close = false;
                } else {
                    closeRemindUI();
                    addPager(event);
                }
                try {
                    VideoQuestionLiveEntity detailInfo = event.getDetailInfo();
                    if (detailInfo != null) {
                        NewCourseLog.sno8(contextLiveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, LiveVideoSAConfig.ART_EN), event.isIspreload(), 0, detailInfo.isTUtor());
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
    }

    private void showH5Result(AnswerResultEntity detail) {
        if (detail.getIsRight() == 1 || detail.getIsRight() == 2) {
            String path = "live_stand_voice_my_right.json";
            LottieComposition.Factory.fromAssetFileName(mContext, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {
                        return;
                    }
                    disMissAnswerResult();
                    mRlResult = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_stand_voice_result, null);
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
                    lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/my_right");
                    lottieAnimationView.setComposition(lottieComposition);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mRlResult.addView(lottieAnimationView, lp);
//                    final ViewGroup group = (ViewGroup) baseVoiceAnswerPager.getRootView();
//                    group.addView(rlResult);
                    addView(mRlResult);
                    lottieAnimationView.playAnimation();
//                    LiveStandVoiceAnswerCreat.setRightGold(mContext, lottieAnimationView, mAnswerReulst.getGold(), mAnswerReulst.getEnergy());
                    LiveStandVoiceAnswerCreat.setRightGoldEnergy(mContext, lottieAnimationView, mAnswerReulst.getGold(), mAnswerReulst.getEnergy());
                    mLiveSoundPool = LiveSoundPool.createSoundPool();
                    final LiveSoundPool.SoundPlayTask task = StandLiveMethod.voiceRight(mLiveSoundPool);
                    mRlResult.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            close = true;
                            StandLiveMethod.onClickVoice(mLiveSoundPool);
                            removeView(mRlResult);
                        }
                    });
                    mRlResult.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            logger.d("onViewDetachedFromWindow right");
                            mLiveSoundPool.stop(task);
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!close) {
                                        StandLiveMethod.onClickVoice(mLiveSoundPool);
                                        removeView(mRlResult);
                                    }
                                }
                            });
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLiveSoundPool.release();
                                }
                            }, 500);
                        }
                    });
                }
            });
        } else {
            String path = "live_stand_voice_my_wrong.json";
            LottieComposition.Factory.fromAssetFileName(mContext, path, new OnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                    if (lottieComposition == null) {
                        return;
                    }
                    disMissAnswerResult();
                    mRlResult = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_stand_voice_result_wrong, null);
                    LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
                    lottieAnimationView.setImageAssetsFolder("live_stand/lottie/voice_answer/my_wrong");
                    lottieAnimationView.setComposition(lottieComposition);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mRlResult.addView(lottieAnimationView, lp);
//                    final ViewGroup group = (ViewGroup) baseVoiceAnswerPager.getRootView();
//                    group.addView(rlResult);
                    addView(mRlResult);
                    lottieAnimationView.playAnimation();
//                    LiveStandVoiceAnswerCreat.setWrongTip(mContext,lottieAnimationView,mAnswerReulst.getAnswerList().get(0).getRightAnswers().get(0));
                    String standardAnswer = "";
                    try {
                        standardAnswer = mAnswerReulst.getAnswerList().get(0).getRightAnswers().get(0);
                    } catch (Exception e) {

                    }
                    LiveStandVoiceAnswerCreat.setWrongTipEnergy(mContext, lottieAnimationView, standardAnswer, mAnswerReulst.getGold(), mAnswerReulst.getEnergy());
//                    setWrongTip(mContext, lottieAnimationView, mAnswerReulst.getAnswerList().get(0).getRightAnswers().get(0) + "");
                    mLiveSoundPool = LiveSoundPool.createSoundPool();
                    final LiveSoundPool.SoundPlayTask task = StandLiveMethod.voiceRight(mLiveSoundPool);
                    mRlResult.findViewById(R.id.iv_livevideo_speecteval_result_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            close = true;
                            StandLiveMethod.onClickVoice(mLiveSoundPool);
                            removeView(mRlResult);
                        }
                    });
                    mRlResult.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            logger.d("onViewDetachedFromWindow error");
                            mLiveSoundPool.stop(task);
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!close) {
                                        StandLiveMethod.onClickVoice(mLiveSoundPool);
                                        removeView(mRlResult);
                                    }
                                }
                            });
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLiveSoundPool.release();
                                }
                            }, 500);
                        }
                    });
                }
            });
        }

    }

    private void closeRemindUI() {
        if (remindView != null) {
            removeView(remindView);
            remindView = null;
        }
    }

    /**
     * 游戏类型试题
     */
    private static final int TEST_TYPE_GAME = 12;

    /**
     * @param result
     * @param resultFromVoice 是否是 本地语音答题（填空、选择）
     */
    private void onAnswerResult(ArtsAnswerResultEvent event, String result, boolean resultFromVoice) {
        //Log.e("AnswerResultBll", "======>onAnswerResult:" + result + ":" + resultFromVoice);
        //boolean showAnswerResult = false;
        try {
            JSONObject jsonObject = new JSONObject(result);
            int stat = jsonObject.optInt("stat");
            // logger.e("======>onAnswerResult2222:"+stat+":"+jsonObject.has("data"));
            JSONObject dataObject = null;
            if (resultFromVoice) {
                dataObject = jsonObject;
            } else {
                dataObject = jsonObject.optJSONObject("data");
            }

            if ((stat == 1 || resultFromVoice) && dataObject != null) {
                mAnswerReulst = new AnswerResultEntity();
                mAnswerReulst.setResultType(AnswerResultEntity.RESULT_TYPE_NEW_COURSE_WARE);
                if (dataObject.has("total")) {
                    JSONObject totalObject = dataObject.getJSONObject("total");
                    mAnswerReulst.setLiveId(totalObject.optString("liveId"));
                    mAnswerReulst.setStuId(totalObject.optString("stuId"));
                    mAnswerReulst.setVirtualId(totalObject.optString("virtualId"));
                    mAnswerReulst.setTestCount(totalObject.optInt("testCount"));
                    mAnswerReulst.setIsRight(totalObject.optInt("isRight"));
                    mAnswerReulst.setGold(totalObject.optInt("gold"));
                    mAnswerReulst.setEnergy(totalObject.optInt("energy"));
                    mAnswerReulst.setRightRate(totalObject.optDouble("rightRate"));
                    mAnswerReulst.setCreateTime(totalObject.optLong("createTime"));
                    JSONArray testIds = totalObject.optJSONArray("testIds");
                    if (testIds != null && testIds.length() > 0) {
                        List<String> idList = new ArrayList<>();
                        for (int i = 0; i < testIds.length(); i++) {
                            idList.add(testIds.getString(i));
                        }
                        mAnswerReulst.setIdArray(idList);
                        //Log.e("AnswerResultBll", "=======>parseAnswerResult:" + idList.size());
                    }

                    int type = totalObject.optInt("type");
                    //不是游戏类型的试题 就显示 统计面板  (仿照pc端处理逻辑)
                    //showAnswerResult = (type != TEST_TYPE_GAME);
                    mAnswerReulst.setType(type);

                    if (dataObject.has("split")) {
                        JSONArray splitArray = dataObject.getJSONArray("split");
                        JSONObject answerObject = null;
                        AnswerResultEntity.Answer answer = null;
                        JSONArray choiceArray = null;
                        JSONArray blankArray = null;
                        JSONArray rightAnswerArray = null;

                        List<AnswerResultEntity.Answer> answerList = new ArrayList<AnswerResultEntity.Answer>();
                        List<String> choiceList = null;
                        List<String> blankList = null;
                        List<String> rightAnswerList = null;

                        for (int i = 0; i < splitArray.length(); i++) {
                            choiceList = new ArrayList<>();
                            blankList = new ArrayList<>();
                            rightAnswerList = new ArrayList<>();
                            answerObject = splitArray.getJSONObject(i);
                            answer = new AnswerResultEntity.Answer();
                            answer.setLiveId(answerObject.optString("liveId"));
                            answer.setStuId(answerObject.optString("stuId"));
                            answer.setTestId(answerObject.optString("testId"));
                            answer.setTestSrc(answerObject.optString("testSrc"));
                            answer.setTestType(answerObject.optInt("testType"));
                         /*   // 答题结果里是否有选择题
                            if(answer.getTestType() == TEST_TYPE_SELECT || answer.getTestType() == TEST_TYPE_BLANK){
                                showAnswerResult = true;
                            }*/
                            answer.setIsRight(answerObject.optInt("isRight"));
                            answer.setRightRate(answerObject.optDouble("rightRate"));
                            answer.setCreateTime(answerObject.optLong("createTime"));

                            choiceArray = answerObject.optJSONArray("choice");
                            for (int i1 = 0; i1 < choiceArray.length(); i1++) {
                                choiceList.add(choiceArray.getString(i1));
                            }
                            answer.setChoiceList(choiceList);
                            blankArray = answerObject.optJSONArray("blank");

                            for (int i1 = 0; i1 < blankArray.length(); i1++) {
                                blankList.add(blankArray.getString(i1));
                            }
                            answer.setBlankList(blankList);
                            rightAnswerArray = answerObject.optJSONArray("rightAnswer");
                            if (rightAnswerArray != null) {
                                for (int i1 = 0; i1 < rightAnswerArray.length(); i1++) {
                                    rightAnswerList.add(rightAnswerArray.getString(i1));
                                }
                            }
                            answer.setRightAnswers(rightAnswerList);

                            answerList.add(answer);
                        }
                        mAnswerReulst.setAnswerList(answerList);
                    }
                    //答题结果里有填空选择 才展示 统计面板 (当前统计面UI 只支持显示 选择、填空题)
                    if (!resultFromVoice) {
                        shoulUpdateGold = true;
                        showAnswerReulst(event);
                    }

                } else {
                    // TODO: 2018/9/18 新平台老课件
                    mAnswerReulst.setResultType(AnswerResultEntity.RESULT_TYPE_OLD_COURSE_WARE);
                    mAnswerReulst.setGold(dataObject.optInt("goldnum"));
                    mAnswerReulst.setEnergy(dataObject.optInt("energy"));
                    JSONArray testIds = dataObject.optJSONArray("testId");
                    if (testIds != null && testIds.length() > 0) {
                        List<String> idList = new ArrayList<>();
                        for (int i = 0; i < testIds.length(); i++) {
                            idList.add(testIds.getString(i));
                        }
                        mAnswerReulst.setIdArray(idList);
                    }
                    mAnswerReulst.setIsRight(dataObject.optInt("isRight"));

                    // logger.e("======>:"+mAnswerReulst.getIsRight() +":"+mAnswerReulst.getIdArray());
                    JSONArray jsonArray = dataObject.optJSONArray("result");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        List<AnswerResultEntity.Answer> answerList = new ArrayList<AnswerResultEntity.Answer>();
                        AnswerResultEntity.Answer answer = null;
                        JSONObject answerObj = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            answerObj = jsonArray.getJSONObject(i);
                            answer = new AnswerResultEntity.Answer();
                            answer.setTestId(answerObj.optString("id"));
                            answer.setIsRight(answerObj.optInt("isright"));
                            answer.setRightRate(answerObj.optDouble("rate"));
                        }
                        mAnswerReulst.setAnswerList(answerList);
                    }
                }
                mAnswerResultList.add(mAnswerReulst);
            } else {
                String errorMsg = jsonObject.optString("msg");
                if (!TextUtils.isEmpty(errorMsg)) {
                    XESToastUtils.showToast(mContext, errorMsg);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            XESToastUtils.showToast(mContext, "答题结果数据解析失败");
        }
    }


    private boolean forceSumbmit;

    /**
     * 关闭作答结果页面
     *
     * @param forceSumbmit
     */
    public void closeAnswerResult(boolean forceSumbmit) {
        //logger.e( "=====>closeAnswerResult:" + forceSumbmit + ":" + mDsipalyer);
        // 已展示过答题结果
        if (mDsipalyer != null) {
            mDsipalyer.close();
            mDsipalyer = null;
            EventBus.getDefault().post(new AnswerResultCplShowEvent("closeAnswerResult1"));
        }
        if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
            EventBus.getDefault().post(new AnswerResultCplShowEvent("closeAnswerResult2"));
        }

        // logger.e("=====>closeAnswerResult:" + forceSumbmit + ":" + this);
        this.forceSumbmit = forceSumbmit;
    }


    /**
     * 延时关闭 提交提示UI
     */
    private Runnable autoCloseTask = new Runnable() {
        @Override
        public void run() {
            closeRemindUI();
        }
    };

    public void remindSubmit() {
        // logger.e("======>remindSubmit:" + mArtsAnswerResultEvent + ":" + this);
        //没有答题结果页时才展示
        if (mArtsAnswerResultEvent == null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (remindView == null) {
                        if (isPse) {
                            remindView = View.inflate(mContext, R.layout.live_remind_submit_layout_pse, null);
                        } else {
                            remindView = View.inflate(mContext, R.layout.live_remind_submit_layout_nor, null);
                        }
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        addView(remindView, params);
                    }
                    // remindView.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim
                            .anim_livevido_arts_answer_result_alpha_in);
                    remindView.startAnimation(alphaAnimation);
                    removeCallbacks(autoCloseTask);
                    postDelayed(autoCloseTask, REMIND_UI_CLOSE_DELAY);

                }
            });
        }
    }

    @Override
    public void onCompeletShow() {
        //logger.e( "=======onCompeletShow called:" + forceSumbmit + ":" + this);
        if (forceSumbmit) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new AnswerResultCplShowEvent("onCompeletShow"));
                }
            }, AUTO_CLOSE_DELAY);
        }
    }


    @Override
    public void onAutoClose(BasePager basePager) {
        if (mDsipalyer != null) {
            removeView(mDsipalyer.getRootLayout());
            mDsipalyer = null;
        }
    }


    @Override
    public void onCloseByUser() {
        closeAnswerResult(false);
    }

    /**
     * 表扬答题全对
     */
    private void praiseAnswerAllRight(JSONArray ids) {
        if (ids != null && ids.length() > 0) {
            if (mAnswerResultList != null && mAnswerResultList.size() > 0) {
                AnswerResultEntity resultEntity = null;
                //在所有答题结果中查找 目标答题结果
                for (int i = (mAnswerResultList.size() - 1); i >= 0; i--) {
                    resultEntity = mAnswerResultList.get(i);
                    boolean isTargetObj = true;
                    if (resultEntity != null) {
                        String id = null;
                        // 判断 表扬id 是否包含在 答题结果id里面
                        for (int j = 0; j < ids.length(); j++) {
                            id = ids.optString(j);
                            if (!resultEntity.getIdArray().contains(id)) {
                                isTargetObj = false;
                                //logger.e( "=======>praiseAnswerAllRight:1111111122222");
                                break;
                            }
                        }
                    }

                    //logger.e( "=======>praiseAnswerAllRight:4444444 "+isTargetObj);
                    if (isTargetObj && resultEntity != null) {
                        break;
                    }
                }
                //logger.e( "=======>praiseAnswerAllRight: targetObj="+ resultEntity);
                // 找到目标答题结果  显示表扬
                if (resultEntity != null && resultEntity.getIsRight() == ANSWER_RESULT_ALL_RIGHT) {
                    showPraise();
                }
            }
        }

    }

    /**
     * 表扬单题答对
     *
     * @param testId
     */
    private void pariseSingleAnswerRight(String testId) {
        if (mAnswerResultList != null && mAnswerResultList.size() > 0) {
            AnswerResultEntity resultEntity = null;
            boolean objFound = false;
            for (int i = (mAnswerResultList.size() - 1); i >= 0; i--) {
                resultEntity = mAnswerResultList.get(i);
                if (resultEntity != null && resultEntity.getAnswerList() != null) {
                    AnswerResultEntity.Answer answer = null;
                    for (int j = 0; j < resultEntity.getAnswerList().size(); j++) {
                        answer = resultEntity.getAnswerList().get(j);
                        if (testId.equals(answer.getTestId())) {
                            //logger.e("====> pariseSingleRight: find target obj");
                            objFound = true;
                            if (resultEntity.getResultType() == AnswerResultEntity.RESULT_TYPE_NEW_COURSE_WARE && answer.getIsRight() == 2) {
                                //logger.e("====> pariseSingleRight: new_course_ware showPraise");
                                //新课件平台 2代表正确
                                showPraise();
                            } else if (resultEntity.getResultType() == AnswerResultEntity.RESULT_TYPE_OLD_COURSE_WARE && answer.getIsRight() == 1) {
                                //老课件平台 1 代表正确
                                //logger.e("====> pariseSingleRight: old_course_ware showPraise");
                                showPraise();
                            }
                            break;
                        }
                    }
                }
                if (objFound) {
                    //logger.e("====> pariseSingleRight: end_target_search");
                    break;
                }
            }
        }

    }

    private void showPraise() {
        try {
            if (mContext != null && !praiseViewShowing) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        praiseViewShowing = true;
                        decorView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
                        int layoutId = isPse ? R.layout.arts_pseteacher_praise_layout : R.layout
                                .arts_teacher_praise_layout;
                        praiseRootView = View.inflate(mContext, layoutId, null);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                .MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                        decorView.addView(praiseRootView, lp);
                        View targetView = praiseRootView.findViewById(R.id.iv_arts_pse_teacher_praise);
                        palyAnim(targetView);
                    }
                });
            }
        } catch (Exception e) {
            praiseViewShowing = false;
            e.printStackTrace();
        }
    }

    private final float SCALE_ANIM_FACTOR = 0.40f;
    /**
     * 文科表扬UI 展示时间
     */
    private final long PARISE_UI_DISPLAY_DURATION = 4 * 1000;
    private Runnable mCloseTask;

    private void palyAnim(View targetView) {
        ScaleAnimation animation = (ScaleAnimation) AnimationUtils.loadAnimation(mContext, R.anim
                .anim_live_artsteahcer__praise);
        animation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
        targetView.startAnimation(animation);
        if (mCloseTask == null) {
            mCloseTask = new Runnable() {
                @Override
                public void run() {
                    closeTeacherPriase();
                }
            };
        }
        praiseRootView.postDelayed(mCloseTask, PARISE_UI_DISPLAY_DURATION);
    }

    private void closeTeacherPriase() {
        try {
            praiseViewShowing = false;
            if (decorView != null && praiseRootView != null) {
                if (mCloseTask != null) {
                    praiseRootView.removeCallbacks(mCloseTask);
                    mCloseTask = null;
                }
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(praiseRootView);
                        decorView = null;
                        praiseRootView = null;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        isPse = getInfo != null && getInfo.getSmallEnglish();
        attachToView();
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        //logger.e( "=====>onNotice :" + "type=:" + type + ":data=" + data.toString());
        switch (type) {
            case XESCODE.ARTS_REMID_SUBMIT:
                int pType = data.optInt("ptype");
                //语文跟读不支持 提醒答题
                if (ARTS_FOLLOW_UP != pType) {
                    remindSubmit();
                }
                break;
            case XESCODE.ARTS_PARISE_ANSWER_RIGHT:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Arts_Praise_Answer_right:").append(data.toString());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll" + "loadLibrary", stringBuilder.toString());
                String praiseType = data.optString("praiseType");
                if ("0".equals(praiseType)) {
                    JSONArray ids = data.optJSONArray("id");
                    praiseAnswerAllRight(ids);
                } else if ("1".equals(praiseType)) {
                    int scoreRangeIndex = data.optInt("scoreRange");
                    JSONArray jsonArray = data.optJSONArray("id");
                    if (jsonArray != null) {
                        try {
                            String testId = jsonArray.optString(0);
                            praiseVoiceAnswer(scoreRangeIndex, testId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case XESCODE.ARTS_PRAISE_ANSWER_RIGHT_SINGLE:
                String testId = data.optString("id");
                if (!TextUtils.isEmpty(testId)) {
                    //logger.e("======>notice: pariseSingle 111");
                    pariseSingleAnswerRight(testId);
                } else {
                    //logger.e("======>notice: pariseAll");
                    JSONArray ids = data.optJSONArray("ids");
                    praiseAnswerAllRight(ids);
                }
                break;

            case XESCODE.ARTS_SEND_QUESTION:
                mArtsAnswerResultEvent = null;
                forceSumbmit = false;
                break;
            case XESCODE.ARTS_STOP_QUESTION:
                mArtsAnswerResultEvent = null;
                if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (!close) {
                                removeView(mRlResult);
                            }

                        }
                    });
                    EventBus.getDefault().post(new AnswerResultCplShowEvent("ARTS_STOP_QUESTION"));
                } else {
                    closeAnswerResult(true);
                }
                break;
            case XESCODE.ARTS_H5_COURSEWARE:
                String status = data.optString("status", "off");
                mArtsAnswerResultEvent = null;
                if ("off".equals(status)) {
                    if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (!close) {
                                    removeView(mRlResult);
                                }

                            }
                        });
                        EventBus.getDefault().post(new AnswerResultCplShowEvent("ARTS_H5_COURSEWARE"));
                    } else {
                        closeAnswerResult(true);
                    }
                } else if ("on".equals(status)) {
                    forceSumbmit = false;
                }
                break;
            default:
                break;
        }
    }


    /**
     * 强制关闭ptype 为12的游戏题
     */
    private void forceCloseGamePage() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AnswerResultCplShowEvent("forceCloseGamePage"));
            }
        }, AUTO_CLOSE_DELAY);
    }


    /**
     * 分数区间索引：分别为2(0<=n<=40)、3(40<=n<60)、4(60<=n<80)、5(80<=n<90)、6(90<=n<=100)
     *
     * @param scoreRange
     */
    private void praiseVoiceAnswer(int scoreRange, String testId) {
        if (mScoreRangeMap == null) {
            mScoreRangeMap = new HashMap<Integer, ScoreRange>(5);
            mScoreRangeMap.put(2, new ScoreRange(0, 39));
            mScoreRangeMap.put(3, new ScoreRange(40, 59));
            mScoreRangeMap.put(4, new ScoreRange(60, 79));
            mScoreRangeMap.put(5, new ScoreRange(80, 89));
            mScoreRangeMap.put(6, new ScoreRange(90, 100));
        }
        ScoreRange range = mScoreRangeMap.get(scoreRange);
        // logger.e("====>praiseVoiceAnswer:" + range + ":" + mVoiceAnswerResultList.size());
        if (range != null && mVoiceAnswerResultList.size() > 0) {
            VoiceAnswerResultEvent voiceAnswerResult = null;
            for (int i = (mVoiceAnswerResultList.size() - 1); i >= 0; i--) {
                voiceAnswerResult = mVoiceAnswerResultList.get(i);
                if (voiceAnswerResult != null && testId.equals(voiceAnswerResult.getTestId())) {
                    if (voiceAnswerResult.getScore() >= range.getLow() && voiceAnswerResult.getScore() <= range.getHigh()) {
                        showPraise();
                    }
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewClose(LiveRoomH5CloseEvent event) {
        //logger.e( "=======>onWebviewClose called");
        //mArtsAnswerResultEvent = null;
        closeAnswerResult(false);
        //刷新右侧 金币
        if (mAnswerReulst != null && mAnswerReulst.getGold() > 0 && shoulUpdateGold) {
            shoulUpdateGold = false;
            upDateGold();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerResult(ArtsAnswerResultEvent event) {
        //logger.e("======>onAnswerResult:"+event);
        if (event != null && !event.equals(mArtsAnswerResultEvent)) {
            mArtsAnswerResultEvent = event;
            if (ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT == event.getType()
                    || ArtsAnswerResultEvent.TYPE_VOICE_SELECT_BLANK == event.getType()) {
                boolean resultFromVoice = event.getType() == ArtsAnswerResultEvent.TYPE_VOICE_SELECT_BLANK;
                onAnswerResult(event, event.getDataStr(), resultFromVoice);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ArtsAnswerResult_:").append(event.getDataStr());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll", stringBuilder.toString());

            } else if (ArtsAnswerResultEvent.TYPE_ROLEPLAY_ANSWERRESULT == event.getType()) {
                onRolePlayAnswerResult(event.getDataStr(), event.getSpeechResultEntity());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ArtsAnswerResult_rolePlay:").append(event.getDataStr());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll" + "_ArtsAnswerResult_rolePlay", stringBuilder.toString());
            } else if (ArtsAnswerResultEvent.TYPE_NATIVE_UPLOAD_VOICE_SELECT_BLANK == event.getType()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ArtsAnswerResult_native_upload_voice_selecet_blank:").append(event.getTestId())
                        .append("_isRight:").append(event.getIsRight());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll" +
                        "_ArtsAnswerResult_native_upload_voice_selecet_blank", stringBuilder.toString());
                AnswerResultEntity resultEntity = new AnswerResultEntity();
                resultEntity.setIsRight(event.getIsRight());
                List<String> idList = new ArrayList<>();
                idList.add(event.getTestId());
                resultEntity.setIdArray(idList);
                resultEntity.setResultType(AnswerResultEntity.RESULT_TYPE_NEW_COURSE_WARE);
                mAnswerResultList.add(resultEntity);
            }
        }
    }


    /**
     * 刷新学生金币
     */
    private void upDateGold() {
        UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
        if (updateAchievement != null) {
            updateAchievement.getStuGoldCount("upDateGold", UpdateAchievement.GET_TYPE_QUE);
        }
    }


    /**
     * rolePlay 答题结果
     *
     * @param dataStr
     */
    private void onRolePlayAnswerResult(String dataStr, SpeechResultEntity speechResultEntity) {
        if (!TextUtils.isEmpty(dataStr)) {
            try {
                JSONObject jsonObject = new JSONObject(dataStr);
                int stat = jsonObject.optInt("stat");
                if (stat == 1) {
                    JSONObject dataJsonObj = jsonObject.optJSONObject("data");
                    if (dataJsonObj != null && dataJsonObj.has("total")) {
                        JSONObject totalObject = dataJsonObj.getJSONObject("total");
                        JSONArray idJsonArray = totalObject.optJSONArray("testIds");
                        String testId = "";//totalObject.optString("testIds");
                        if (idJsonArray != null && idJsonArray.length() > 0) {
                            testId = idJsonArray.optString(0);
                        }
                        int type = totalObject.optInt("type");
                        int score = totalObject.optInt("score");
                        int gold = totalObject.optInt("gold");
                        int energy = totalObject.optInt("enery");
                        VoiceAnswerResultEvent voiceAnswerResultEvent = new VoiceAnswerResultEvent(testId, score);
                        voiceAnswerResultEvent.setType(type);
                        // logger.e("========>onRolePlayAnswerResult:" + voiceAnswerResultEvent
                        //    .getScore() + ":" + voiceAnswerResultEvent.getTestId());
                        saveVoiceAnswerResult(voiceAnswerResultEvent);
                        //全身直播不弹结果页
                        if (mGetInfo.getPattern() != LiveVideoConfig.LIVE_PATTERN_2) {
                            speechResultEntity.score = score;
                            speechResultEntity.gold = gold;
                            speechResultEntity.energy = energy;
                            SpeechResultPager speechResultPager = new SpeechResultPager(mContext, mRootView, speechResultEntity, mGetInfo);
                            addView(speechResultPager.getRootView());
                            speechResultPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                                @Override
                                public void onClose(LiveBasePager basePager) {
                                    removeView(basePager.getRootView());
                                }
                            });
                        }
                    }
                } else {
                    String errorMsg = jsonObject.optString("msg");
                    if (!TextUtils.isEmpty(errorMsg)) {
                        XESToastUtils.showToast(mContext, errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                XESToastUtils.showToast(mContext, "答题结果数据解析失败");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoiceAnswerReuslt(VoiceAnswerResultEvent event) {
        //  logger.e( "====>onVoiceAnswerReuslt:" + event);
        /*if (event != null && !event.equals(mVoiceAnswerResult)) {
            mVoiceAnswerResult = event;
        }*/
        saveVoiceAnswerResult(event);
    }

    /**
     * 缓存语音答题结果
     *
     * @param event
     */
    private void saveVoiceAnswerResult(VoiceAnswerResultEvent event) {
        if (!mVoiceAnswerResultList.contains(event)) {
            mVoiceAnswerResultList.add(event);
        }
    }

    int[] notices = {
            XESCODE.ARTS_REMID_SUBMIT,
            XESCODE.ARTS_PARISE_ANSWER_RIGHT,
            XESCODE.ARTS_STOP_QUESTION,
            XESCODE.ARTS_H5_COURSEWARE,
            XESCODE.ARTS_PRAISE_ANSWER_RIGHT_SINGLE,
            XESCODE.ARTS_SEND_QUESTION
    };

    @Override
    public int[] getNoticeFilter() {
        return notices;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAnswerResultList != null) {
            mAnswerResultList.clear();
        }
        if (mVoiceAnswerResultList != null) {
            mVoiceAnswerResultList.clear();
        }
        praiseViewShowing = false;
        mArtsAnswerResultEvent = null;
        EventBus.getDefault().unregister(this);
    }

    private void setRightGold(Context context, LottieAnimationView lottieAnimationView, int goldCount) {
        String num = "获得 " + goldCount + " 枚金币";
        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/voice_answer/my_right/img_22.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(48);
            paint.setColor(0xffCC6E12);
            Typeface fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, (img_7Bitmap.getHeight() + paint.measureText("a")) / 2, paint);
            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e("setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_22", img_7Bitmap);
    }

    private void setWrongTip(Context context, LottieAnimationView lottieAnimationView, String standardAnswer) {
        String num = "正确答案: " + standardAnswer;
        AssetManager manager = context.getAssets();
        Bitmap img_7Bitmap;
        try {
            img_7Bitmap = BitmapFactory.decodeStream(AssertUtil.open("live_stand/lottie/voice_answer/my_wrong/img_5.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(AssertUtil.open("Images/jindu/img_3.png"));
            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setTextSize(48);
            paint.setColor(0xff5586A3);
            Typeface fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            float width = paint.measureText(num);
            canvas.drawText(num, (img_7Bitmap.getWidth() - width) / 2, (img_7Bitmap.getHeight() + paint.measureText("a")) / 2, paint);
            img_7Bitmap.recycle();
            img_7Bitmap = creatBitmap;
        } catch (IOException e) {
            logger.e("setRightGold", e);
            return;
        }
        lottieAnimationView.updateBitmap("image_5", img_7Bitmap);
    }

    /**
     * 回答问题结果提示框延迟三秒消失
     */
    public void disMissAnswerResult() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!close) {
                    removeView(mRlResult);
                }
            }
        }, 5000);
    }
}
