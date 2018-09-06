package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 文科答题结果
 *
 * @author chenkun
 * @version 1.0, 2018/7/27 下午5:36
 */

public class ArtsAnswerResultBll extends LiveBaseBll implements NoticeAction, AnswerResultStateListener {
    private static  final String Tag = "ArtsAnswerResultBll";
    private RelativeLayout rlAnswerResultLayout;
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

    private static final String TAG = "ArtsAnswerResultBll";
    private IArtsAnswerRsultDisplayer mDsipalyer;
    private AnswerResultEntity mAnswerReulst;
    /**
     * 提示提交展示时间
     */
    private long REMIND_UI_CLOSE_DELAY = 3000;
    /**
     * 是否是小学英语
     */
    private boolean isPse;
    private View remindView;

    private ViewGroup decorView;
    private View praiseRootView;
    private boolean isPerfectRight;

    /**
     * @param context
     * @param liveBll
     * @param rootView
     * @param isPse    是否是小学英语
     */
    public ArtsAnswerResultBll(Activity context, LiveBll2 liveBll) {
        super(context,liveBll);
        this.isPse = isPse;
    }


    private void attachToView() {
        EventBus.getDefault().register(this);
        rlAnswerResultLayout = mRootView;
    }

    private void addPager() {
        Loger.e("ArtsAnswerResultBll:addPager:" + mDsipalyer);
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
        Loger.e("Arts", "==========> ArtsAnswerResultBll addPager called:");
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


     private void onAnswerResult(String result) {
        Loger.e(TAG, "=======>onAnswerResult:" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            int stat = jsonObject.optInt("stat");
            if (stat == 1) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                mAnswerReulst = new AnswerResultEntity();

                if (dataObject.has("total")) {
                    JSONObject totalObject = dataObject.getJSONObject("total");
                    mAnswerReulst.setLiveId(totalObject.optString("liveId"));
                    mAnswerReulst.setStuId(totalObject.optString("stuId"));
                    mAnswerReulst.setVirtualId(totalObject.optString("virtualId"));
                    mAnswerReulst.setTestCount(totalObject.optInt("testCount"));
                    mAnswerReulst.setIsRight(totalObject.getInt("isRight"));
                    mAnswerReulst.setGold(totalObject.optInt("gold"));
                    mAnswerReulst.setRightRate(totalObject.optDouble("rightRate"));
                    mAnswerReulst.setCreateTime(totalObject.optLong("createTime"));
                }

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
                showAnswerReulst();
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
        // 已展示过答题结果
        if (mDsipalyer != null) {
            mDsipalyer.close();
            mDsipalyer = null;
            EventBus.getDefault().post(new AnswerResultCplShowEvent());
        }
        Loger.e("ArtsAnswerBll", "=====>closeAnswerResult:" + forceSumbmit);
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
        Loger.e("ArtsAnswerResult","======>remindSubmit:"+mDsipalyer+":"+this);
        //没有答题结果页时才展示
        if (mDsipalyer == null) {
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
        Loger.e("ArtsAnswerResultBll", "=======onCompeletShow called:" + forceSumbmit);
        if (forceSumbmit) {
            mRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new AnswerResultCplShowEvent());
                }
            },AUTO_CLOSE_DELAY);
        }
    }



    public void teacherPraise() {
        if (mAnswerReulst != null && (mAnswerReulst.getIsRight() == 2)) {
            try {
                if (mContext != null) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                e.printStackTrace();
            }
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
        mCloseTask = new Runnable() {
            @Override
            public void run() {
                closeTeacherPriase();
            }
        };
        praiseRootView.postDelayed(mCloseTask, PARISE_UI_DISPLAY_DURATION);
    }

    private void closeTeacherPriase() {
        try {
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
        isPse = true;
        attachToView();
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
      Loger.e(Tag,"=====>onNotice :"+"type=:"+type+":data="+data.toString());
        switch (type) {
            case  XESCODE.ARTS_REMID_SUBMIT:
                remindSubmit();
                break;
            case  XESCODE.ARTS_TEACHER_PRAISE:
                teacherPraise();
                break;
            case XESCODE.ARTS_STOP_QUESTION:
                closeAnswerResult(true);
                break;
            case  XESCODE.ARTS_H5_COURSEWARE:
                String status = data.optString("status", "off");
                if("off".equals(status)){
                    closeAnswerResult(true);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewClose(LiveRoomH5CloseEvent event) {
        closeAnswerResult(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerResult(ArtsAnswerResultEvent event) {
        onAnswerResult(event.getDataStr());
    }

    int[] notices = {
            XESCODE.ARTS_REMID_SUBMIT,
            XESCODE.ARTS_TEACHER_PRAISE,
            XESCODE.ARTS_STOP_QUESTION,
            XESCODE.ARTS_H5_COURSEWARE
    };

    @Override
    public int[] getNoticeFilter() {
        return  notices;
    }


    @Override
    public void onDestory() {
        super.onDestory();
        EventBus.getDefault().unregister(this);
    }
}
