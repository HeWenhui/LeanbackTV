package com.xueersi.parentsmeeting.modules.livevideoOldIJK.teampk.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ScienceAnswerResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAnswerRightLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 小理战队pk 二期  答题表扬，徽章 动效
 *
 * @author chekun
 * created  at 2019/2/15 13:25
 */
public class TeamPkPraiseBll {

    private Activity mActivity;
    private TeamPkBll mPkBll;
    private boolean isAnimStart;

    private ViewGroup decorView;
    private View praiseRootView;
    private LottieAnimationView animView;

    /**
     * lottie动效资源路径
     **/
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/teacher_praise/";
    private String mResPath;
    private String mJsonFilePath;
    private String mAnimScriptCacheKey;

    private static final int BADGE_LOW_BOUND = 0;
    private static final int BADGE_UP_BOUND = 10;
    /**
     * lottie 动画 脚本缓存key
     */
    private static final String LOTTIE_JSON_PRAISE = "teacherPraise";
    private static final String LOTTIE_JSON_BADGE = "badge";
    private static final String LOTTIE_JSON_ANSWERRIGHT = "paraise_answer_right";
    /**
     * 队伍徽章表扬
     **/
    private static final int PRAISE_TYPE_BADGE = 1;
    /**
     * 表扬答对题
     **/
    private static final int PRAISE_TYPE_ANSWER_RIGHT = 2;
    /**
     * 表扬答对超难题
     **/
    private static final int PRAISE_TYPE_ANSWERT_RIGHT_DOUBLE = 3;

    private TextView tvAnswerRightEnergy;
    private ImageView ivAnswerRightEnergy;
    private int mPraiseType;

    /**
     * 是否取消动画展示
     */
    private boolean animCance;

    /**
     * 能量动画开始时间点
     */
    private static final float ENERGY_ANIM_ENTER_FRACTION = 0.37f;
    private boolean energyAnimRuning;
    private Handler mHandler;

    public TeamPkPraiseBll(Activity activity, TeamPkBll pkBll) {
        mActivity = activity;
        mPkBll = pkBll;
    }


    /**
     * 表扬信息
     */
    private static class PraiseInfo {
        private int praiseType;
        private Object data;
        private String nonce;

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getNonce() {
            return nonce;
        }

        public int getPraiseType() {
            return praiseType;
        }

        public void setPraiseType(int praiseType) {
            this.praiseType = praiseType;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    /**
     * 表扬缓存 任务队列
     */
    private List<PraiseInfo> praiseInfoList = new ArrayList<>();


    /**
     * @param sourceNick
     * @param target
     * @param data
     * @param type
     */
    public void onPraise(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.TEAM_PK_TEACHER_PRAISE:
                int praiseType = data.optInt("praiseType", 0);
                if (isMyTeam(data)) {
                    PraiseInfo info = new PraiseInfo();
                    info.setPraiseType(XESCODE.TEAM_PK_TEACHER_PRAISE);
                    info.setNonce(data.optString("nonce",""));
                    info.setData(praiseType);
                    cachePraiseInfo(info);
                }
                break;
            case XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT:
                boolean isDouble = data.optInt("isDouble", 0) == 1;
                PraiseInfo info = new PraiseInfo();
                info.setNonce(data.optString("nonce",""));
                info.setPraiseType(XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT);
                info.setData(isDouble);
                cachePraiseInfo(info);
                break;
            default:
                break;
        }
    }

    /**
     * 缓存表扬信息
     *
     * @param info
     */
    private void cachePraiseInfo(PraiseInfo info) {
        synchronized (praiseInfoList) {
            if (praiseInfoList.isEmpty()) {
                praiseInfoList.add(info);
                consumPraiseInfo(info);
            } else {
                praiseInfoList.add(info);
            }
        }
    }

    /**
     * 消费表扬 信息
     *
     * @param info
     */
    private void consumPraiseInfo(PraiseInfo info) {
        if (info != null) {
            switch (info.getPraiseType()) {
                case XESCODE.TEAM_PK_TEACHER_PRAISE:
                    int praiseType = (int) info.getData();
                    if (praiseType > BADGE_LOW_BOUND && praiseType < BADGE_UP_BOUND) {
                        TeamPkLog.showPkTeamPraise(mPkBll.getLiveBll(),info.getNonce(),praiseType+"");
                        showBadge(praiseType);
                    } else {
                        consumPraiseInfo(getNextPraiseInfo());
                    }
                    break;
                case XESCODE.TEAM_PK_PARISE_ANWSER_RIGHT:
                    ScienceAnswerResult answerResult = mPkBll.getCurrentAnswerResult();
                    if (answerResult != null && answerResult.getIsRight() == ScienceAnswerResult.STATE_CODE_RIGHT) {
                        boolean isDouble = (boolean) info.getData();
                        if (isDouble) {
                            if (mPkBll.getLatesH5CloseEvent() != null && mPkBll.getLatesH5CloseEvent().getmEnergyNum
                                    () > 0) {
                                TeamPkLog.showPkPraise(mPkBll.getLiveBll(),info.getNonce(),"1");
                                showAnswerRightPraise(mPkBll.getLatesH5CloseEvent().getmEnergyNum());
                            }
                        } else {
                            TeamPkLog.showPkPraise(mPkBll.getLiveBll(),info.getNonce(),"0");
                            showPraise();
                        }
                    } else {
                        consumPraiseInfo(getNextPraiseInfo());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private int mEnergyNum;

    /**
     * 展示答对超难题表扬
     *
     * @param energyNum
     */
    private void showAnswerRightPraise(int energyNum) {
        mEnergyNum = energyNum;
        mPraiseType = PRAISE_TYPE_ANSWERT_RIGHT_DOUBLE;
        mResPath = "team_pk/pkresult/teacher_praise/images";
        mJsonFilePath = "team_pk/pkresult/teacher_praise/data.json";
        mAnimScriptCacheKey = LOTTIE_JSON_ANSWERRIGHT;
        addPraiseView();
    }

    /**
     * 展示老师表扬动画
     **/
    private void showPraise() {
        mPraiseType = PRAISE_TYPE_ANSWER_RIGHT;
        mResPath = "team_pk/pkresult/teacher_praise/images";
        mJsonFilePath = "team_pk/pkresult/teacher_praise/data.json";
        mAnimScriptCacheKey = LOTTIE_JSON_PRAISE;
        addPraiseView();
    }

    /**
     * 是否是本队
     *
     * @param data
     * @return
     */
    private boolean isMyTeam(JSONObject data) {
        boolean result = false;
        String classTeamId = mPkBll.getRoomInitInfo().getStudentLiveInfo().getClassId() + "-" + mPkBll
                .getRoomInitInfo().getStudentLiveInfo().getTeamId();
        try {
            JSONArray jsonArray = data.getJSONArray("teamList");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (classTeamId.equals(jsonArray.getString(i))) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 显示徽章动效
     *
     * @param badgeType 徽章类型
     */
    private void showBadge(int badgeType) {
        mPraiseType = PRAISE_TYPE_BADGE;
        mResPath = LOTTIE_RES_ASSETS_ROOTDIR + "badge_" + badgeType + "/images";
        mJsonFilePath = LOTTIE_RES_ASSETS_ROOTDIR + "badge_" + badgeType + "/data.json";
        mAnimScriptCacheKey = LOTTIE_JSON_BADGE;
        addPraiseView();
    }

    private void addPraiseView() {
        try {
            if (mActivity != null && !animCance) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAnimStart) {
                            isAnimStart = true;
                            decorView = (ViewGroup) mActivity.getWindow().getDecorView();
                            praiseRootView = View.inflate(mActivity, R.layout.teampk_teacher_praise_layout, null);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            decorView.addView(praiseRootView, lp);

                            if (mPraiseType == PRAISE_TYPE_ANSWERT_RIGHT_DOUBLE) {
                                View animeContainer = praiseRootView.findViewById(R.id.cstl_teampk_praise_answer_right);
                                animeContainer.setVisibility(View.VISIBLE);
                                animView = praiseRootView.findViewById(R.id.lav_teampk_praise_anwser_right);
                                ivAnswerRightEnergy = praiseRootView.findViewById(R.id.iv_teampk_praise_energy);
                                tvAnswerRightEnergy = praiseRootView.findViewById(R.id.tv_teampk_praise_energy);
                                startAnswerRightAnim();
                            } else {
                                animView = praiseRootView.findViewById(R.id.lav_teacher_priase);
                                animView.setVisibility(View.VISIBLE);
                                startAnim();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAnimStart = false;
        }
    }


    /**
     * 播放表扬答对超难题动画
     */
    private void startAnswerRightAnim() {
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "anwser_right/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "anwser_right/data.json";

        final TeamPkAnswerRightLottieEffectInfo effectInfo = new TeamPkAnswerRightLottieEffectInfo(lottieResPath,
                lottieJsonPath,"img_5.png");
        animView.useHardwareAcceleration(true);
        effectInfo.setEnergyNum("+"+mEnergyNum);
        animView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(animView.getContext()), LOTTIE_JSON_ANSWERRIGHT);
        animView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        animView.getContext());
            }
        });
        animView.playAnimation();
        animView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() > ENERGY_ANIM_ENTER_FRACTION && !energyAnimRuning) {
                    energyAnimRuning = true;
                    closeTeacherPriase();
                }
            }
        });
    }

    private void startAnim() {
        animView.useHardwareAcceleration(true);
        final LottieEffectInfo effectInfo = new LottieEffectInfo(mResPath, mJsonFilePath);
        animView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mActivity), mAnimScriptCacheKey);
        animView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mActivity);
            }
        });
        animView.playAnimation();
        animView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                closeTeacherPriase();
            }
        });
    }


    /**
     * 关闭表扬UI
     */
    private void closeTeacherPriase() {
        isAnimStart = false;
        mPraiseType = 0;
        mEnergyNum = 0;
        energyAnimRuning = false;
        try {
            if (decorView != null && praiseRootView != null) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(decorView != null){
                            decorView.removeView(praiseRootView);
                            decorView = null;
                            praiseRootView = null;
                        }
                    }
                });

                if(mHandler == null){
                    mHandler = new Handler();
                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PraiseInfo praiseInfo = getNextPraiseInfo();
                        if (praiseInfo != null) {
                            consumPraiseInfo(praiseInfo);
                        }
                    }
                }, 1200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void releas() {
        animCance = true;
        if (praiseInfoList != null) {
            praiseInfoList.clear();
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 获取下一个表扬任务；并且清除上一次的
     *
     * @return
     */
    private PraiseInfo getNextPraiseInfo() {
        PraiseInfo resultInfo = null;
        if (praiseInfoList != null) {
            synchronized (praiseInfoList) {
                if (praiseInfoList.size() > 0) {
                    //清除已展示 动画info
                    praiseInfoList.remove(0);
                    // 取下一个待展示的info
                    if (praiseInfoList.size() > 0) {
                        resultInfo = praiseInfoList.get(0);
                    }
                }
            }
        }
        return resultInfo;
    }
}
