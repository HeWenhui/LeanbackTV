package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ScoreRange;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.VoiceAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

/**
 * 文科答题结果
 *
 * @author chenkun
 * @version 1.0, 2018/7/27 下午5:36
 */

public class ArtsAnswerResultBll extends LiveBaseBll implements NoticeAction, AnswerResultStateListener {
    private static final String TAG = "ArtsAnswerResultBll";
    private RelativeLayout rlAnswerResultLayout;

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
    private AnswerResultEntity mAnswerReulst;
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
    /**是否需要更新右侧金币数*/
    private boolean shoulUpdateGold;

    /**
     * 当前语音题的答题结果
     */
    private VoiceAnswerResultEvent mVoiceAnswerResult;
    /**
     * 是否正在展示表扬
     */
    private boolean praiseViewShowing;
    private ArtsAnswerResultEvent mArtsAnswerResultEvent;

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
     *  用于直播回放
     * @param context
     * @param rootView
     */
    public ArtsAnswerResultBll(Activity context,String liveId,int liveType,RelativeLayout rootView){
        super(context,liveId,liveType);
        mRootView = rootView;
    }


    private void attachToView() {
        EventBus.getDefault().register(this);
        rlAnswerResultLayout = mRootView;
    }

    private void addPager() {
        logger.e("ArtsAnswerResultBll:addPager:" + mDsipalyer);
        if (mDsipalyer != null) {
            return;
        }

        if (isPse) {
            mDsipalyer = new ArtsPSEAnswerResultPager(mContext, mAnswerReulst, this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlAnswerResultLayout.addView(mDsipalyer.getRootLayout(), layoutParams);
        } else {
            mDsipalyer = new ArtsAnswerResultPager(mContext, mAnswerReulst, this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlAnswerResultLayout.addView(mDsipalyer.getRootLayout(), layoutParams);
        }
        logger.e( "==========> ArtsAnswerResultBll addPager called:");
    }

    /**
     * 展示答题结果
     */
    private void showAnswerReulst() {
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (remindView != null) {
                    remindView.setVisibility(View.GONE);
                }
                addPager();
            }
        });
    }

    // private static final int TEST_TYPE_SELECT = 2;
    // private static final int TEST_TYPE_BLANK  = 1;

    /**
     * 游戏类型试题
     */
    private static final int TEST_TYPE_GAME = 12;

    private void onAnswerResult(String result) {
        logger.e( "=======>onAnswerResult:" + result);
        boolean showAnswerResult = false;
        try {
            JSONObject jsonObject = new JSONObject(result);
            int stat = jsonObject.optInt("stat");
            if (stat == 1 && jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
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
                    mAnswerReulst.setRightRate(totalObject.optDouble("rightRate"));
                    mAnswerReulst.setCreateTime(totalObject.optLong("createTime"));
                    JSONArray testIds = totalObject.optJSONArray("testIds");
                    if (testIds != null && testIds.length() > 0) {
                        List<String> idList = new ArrayList<>();
                        for (int i = 0; i < testIds.length(); i++) {
                            idList.add(testIds.getString(i));
                        }
                        mAnswerReulst.setIdArray(idList);
                    }

                    int type = totalObject.optInt("type");
                    //不是游戏类型的试题 就显示 统计面板  (仿照pc端处理逻辑)
                    showAnswerResult = (type != TEST_TYPE_GAME);

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
                    if (showAnswerResult) {
                        shoulUpdateGold = true;
                        showAnswerReulst();
                    }
                } else {
                    // TODO: 2018/9/18 新平台老课件
                    mAnswerReulst.setResultType(AnswerResultEntity.RESULT_TYPE_OLD_COURSE_WARE);
                    mAnswerReulst.setGold(dataObject.optInt("goldnum"));
                    JSONArray testIds = dataObject.optJSONArray("testId");
                    if (testIds != null && testIds.length() > 0) {
                        List<String> idList = new ArrayList<>();
                        for (int i = 0; i < testIds.length(); i++) {
                            idList.add(testIds.getString(i));
                        }
                        mAnswerReulst.setIdArray(idList);
                    }
                    mAnswerReulst.setIsRight(dataObject.optInt("isRight"));

                    Log.e("008","======>:"+mAnswerReulst.getIsRight() +":"+mAnswerReulst.getIdArray());
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

    public void closeAnswerResult(boolean forceSumbmit) {
        logger.e( "=====>closeAnswerResult:" + forceSumbmit + ":" + mDsipalyer);
        // 已展示过答题结果
        if (mDsipalyer != null) {
            mDsipalyer.close();
            mDsipalyer = null;
            EventBus.getDefault().post(new AnswerResultCplShowEvent());
        }
        logger.e("=====>closeAnswerResult:" + forceSumbmit + ":" + this);
        this.forceSumbmit = forceSumbmit;
    }


    /**
     * 延时关闭 提交提示UI
     */
    private Runnable autoCloseTask = new Runnable() {
        @Override
        public void run() {
            if (remindView != null) {
                remindView.setVisibility(View.GONE);
            }
        }
    };

    public void remindSubmit() {
        logger.e("======>remindSubmit:" + mDsipalyer + ":" + this);
        //没有答题结果页时才展示
        if (mArtsAnswerResultEvent == null) {
            rlAnswerResultLayout.post(new Runnable() {
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
                        rlAnswerResultLayout.addView(remindView, params);
                    }
                    remindView.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim
                            .anim_livevido_arts_answer_result_alpha_in);
                    remindView.startAnimation(alphaAnimation);

                    rlAnswerResultLayout.removeCallbacks(autoCloseTask);
                    rlAnswerResultLayout.postDelayed(autoCloseTask, REMIND_UI_CLOSE_DELAY);
                }
            });
        }
    }

    @Override
    public void onCompeletShow() {
        logger.e( "=======onCompeletShow called:" + forceSumbmit + ":" + this);
        if (forceSumbmit) {
            mRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new AnswerResultCplShowEvent());
                }
            }, AUTO_CLOSE_DELAY);
        }
    }


    /**
     * 表扬答题全对
     */
    private void praiseAnswerAllRight(JSONArray ids) {

        if (ids != null && ids.length() > 0) {
            if (mAnswerReulst != null && mAnswerReulst.getIdArray() != null && mAnswerReulst.getIdArray().size() != 0) {
                String id = null;
                boolean showPraise = true;
                // 判断 表扬id 是否包含在 答题结果id里面
                for (int i = 0; i < ids.length(); i++) {
                    id = ids.optString(i);
                    if (!mAnswerReulst.getIdArray().contains(id)) {
                        showPraise = false;
                        break;
                    }
                }

                Log.e( "008","=======>praiseAllRight:" + showPraise);
                if (showPraise && mAnswerReulst.getIsRight() == ANSWER_RESULT_ALL_RIGHT) {
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
        if (mAnswerReulst != null && mAnswerReulst.getAnswerList() != null) {
            AnswerResultEntity.Answer answer = null;
            for (int i = 0; i < mAnswerReulst.getAnswerList().size(); i++) {
                answer = mAnswerReulst.getAnswerList().get(i);
                if (testId.equals(answer.getTestId()) && answer.getIsRight() == 2
                        && mAnswerReulst.getResultType() == AnswerResultEntity.RESULT_TYPE_NEW_COURSE_WARE) {
                    //新课件平台 2代表正确
                    showPraise();
                    break;
                } else if (testId.equals(answer.getTestId()) && answer.getIsRight() == 1
                        && mAnswerReulst.getResultType() == AnswerResultEntity.RESULT_TYPE_OLD_COURSE_WARE) {
                    //老课件平台 1 代表正确
                    showPraise();
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
        logger.e( "=====>onNotice :" + "type=:" + type + ":data=" + data.toString());
        switch (type) {
            case XESCODE.ARTS_REMID_SUBMIT:
                int pType = data.optInt("ptype");
                // 语文跟读不支持 提醒答题
                if (ARTS_FOLLOW_UP != pType) {
                    remindSubmit();
                }
                break;
            case XESCODE.ARTS_PARISE_ANSWER_RIGHT:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Arts_Praise_Answer_right:").append(data.toString());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll" + "loadLibrary", stringBuilder.toString());

                // 语文跟读不支持 表扬
                if (ARTS_FOLLOW_UP != data.optInt("ptype")) {
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
                }
                break;
            case XESCODE.ARTS_PRAISE_ANSWER_RIGHT_SINGLE:
                String testId = data.optString("id");
                if (!TextUtils.isEmpty(testId)) {
                    Log.e("008","======>notice: pariseSingle 111");
                    pariseSingleAnswerRight(testId);
                } else {
                    Log.e("008","======>notice: pariseAll");
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
                closeAnswerResult(true);
                break;
            case XESCODE.ARTS_H5_COURSEWARE:
                String status = data.optString("status", "off");
                mArtsAnswerResultEvent = null;
                if ("off".equals(status)) {
                    closeAnswerResult(true);
                } else if ("on".equals(status)) {
                    forceSumbmit = false;
                }
                break;
            default:
                break;
        }
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
        logger.e("====>praiseVoiceAnswer:" + range + ":" + mVoiceAnswerResult);
        if (range != null && mVoiceAnswerResult != null) {
            logger.e("====>praiseVoiceAnswer:" + scoreRange + ":" + testId + ":"
                    + mVoiceAnswerResult.getTestId() + ":" + mVoiceAnswerResult.getScore());
            if (testId.equals(mVoiceAnswerResult.getTestId())) {
                if (mVoiceAnswerResult.getScore() >= range.getLow() && mVoiceAnswerResult.getScore() <= range.getHigh
                        ()) {
                    showPraise();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewClose(LiveRoomH5CloseEvent event) {
        logger.e( "=======>onWebviewClose called");
        //mArtsAnswerResultEvent = null;
        closeAnswerResult(false);
        //刷新右侧 金币
        if(mAnswerReulst != null && mAnswerReulst.getGold() > 0 && shoulUpdateGold){
            shoulUpdateGold = false;
            upDateGold();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerResult(ArtsAnswerResultEvent event) {
        logger.e("====>ArtsAnswerResultEvent:" + event);
        if (event != null && !event.equals(mArtsAnswerResultEvent)) {
            mArtsAnswerResultEvent = event;
            if (ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT == event.getType()) {
                onAnswerResult(event.getDataStr());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ArtsAnswerResult_:").append(event.getDataStr());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll" ,stringBuilder.toString());


            } else if (ArtsAnswerResultEvent.TYPE_ROLEPLAY_ANSWERRESULT == event.getType()) {
                onRolePlayAnswerResult(event.getDataStr());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ArtsAnswerResult_rolePlay:").append(event.getDataStr());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), "ArtsAnswerResultBll"+"_ArtsAnswerResult_rolePlay",stringBuilder.toString());
            }
        }
    }


    /**
     * 刷新学生金币
     */
    private void upDateGold() {
        UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(mContext, UpdateAchievement.class);
        if (updateAchievement != null) {
            updateAchievement.getStuGoldCount();
        }
    }


    /**
     * rolePlay 答题结果
     *
     * @param dataStr
     */
    private void onRolePlayAnswerResult(String dataStr) {
        if (!TextUtils.isEmpty(dataStr)) {
            try {
                JSONObject jsonObject = new JSONObject(dataStr);
                int stat = jsonObject.optInt("stat");
                if (stat == 1) {
                    JSONObject dataJsonObj = jsonObject.optJSONObject("data");
                    if (dataJsonObj != null && dataJsonObj.has("total")) {
                        JSONObject totalObject = dataJsonObj.getJSONObject("total");
                        String testId = totalObject.optString("testIds");
                        int score = totalObject.optInt("score");
                        mVoiceAnswerResult = new VoiceAnswerResultEvent(testId, score);
                        logger.e("========>onRolePlayAnswerResult:" + mVoiceAnswerResult
                                .getScore() + ":" + mVoiceAnswerResult.getTestId());
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
        logger.e( "====>onVoiceAnswerReuslt:" + event);
        if (event != null && !event.equals(mVoiceAnswerResult)) {
            mVoiceAnswerResult = event;
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
    public void onDestory() {
        super.onDestory();
        praiseViewShowing = false;
        mArtsAnswerResultEvent = null;
        EventBus.getDefault().unregister(this);
    }
}
