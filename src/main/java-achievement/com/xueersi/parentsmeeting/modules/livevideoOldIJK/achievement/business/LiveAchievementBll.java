package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LineEvaluator;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyuqiang on 2017/7/20.
 * 本场成就
 */
public class LiveAchievementBll implements StarInteractAction {
    private String TAG = "LiveAchievementBll";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private String eventId;
    private int liveType;
    private Activity activity;
    private View flyStat;
    private View flyLight;
    /**
     * 星星图标
     */
    private ImageView ivStarInteractStat;
    private ImageView ivStarInteractGold;
    /**
     * 本场成就星星数量
     */
    private TextView tvStarInteractCount;
    /**
     * 能量条上方，本场成就星星数量-背后隐藏数量
     */
    private TextView tvStarInteractCountHind;
    /**
     * 本场成就金币数量
     */
    private TextView tvStarInteractGoldCount;
    /**
     * 本场成就金币数量-背后隐藏数量
     */
    private TextView tvStarInteractGoldHind;
    private ViewGroup myView;
    private Point startPoint;
    /**
     * 右侧星星结束位置
     */
    private Point endStarPoint;
    /**
     * 右侧金币结束位置
     */
    private Point endGoldPoint;
    private Point controlPoint;
    /**
     * 星星互动开始
     */
    private boolean statInteractStart = false;
    /**
     * 是不是像老师发送过，目前没用
     */
    boolean isSend = false;
    String myMsg;
    private String mStarid;
    ArrayList<String> data;
    LiveAchievementHttp liveBll;
    LiveAndBackDebug liveAndBackDebug;
    private int starCount;
    private int goldCount;
    boolean mIsLand;
    int topMargin;
    RelativeLayout bottomContent;
    /**
     * 右侧星星数字动画出现
     */
    private Animation mStarCountAnimSlideIn;
    /**
     * 右侧金币数字动画出现
     */
    private Animation mGoldCountAnimSlideIn;
    /**
     * 右侧星星和金币数字动画消失
     */
    private Animation mStarCountAnimSlideOut;
    /**
     * 背景光的动画旋转
     */
    private Animation mStarLightAnimRotate;
    ArrayList<AllAnimation> allAnimations = new ArrayList<>();
    private AllAnimation firstAllAnimation;
    private AllAnimation lastAllAnimation;
    private final int AnimationType_STAR = 0;
    private final int AnimationType_GOLD = 1;
    AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    /**
     * 星星缩放最大值
     */
    private final float starScaleMax = 1.4f;//最大值
    /**
     * 星星从SCALE 0.0到1.0时间比例
     */
    private final float starScaleStep1 = 0.571f;//第一步比例
    /**
     * 星星从SCALE 1.0到1.4时间比例
     */
    private final float starScaleStep2 = 0.786f;//第二步比例
    /**
     * 星星从SCALE 1.0到1.4
     */
    private float starInLine1a, starInLine1b;
    /**
     * 星星从SCALE 1.4到1.0
     */
    private float starInLine2a, starInLine2b;
    /**
     * 星星晃动从20,0,-20
     */
    private float starRotateLine1a, starRotateLine1b;
    /**
     * 星星晃动从-20,0
     */
    private float starRotateLine2a, starRotateLine2b;
    private LiveGetInfo getInfo;
    //是否使用小英萌萌哒皮肤
    private boolean isSmallEnglish = false;
    LogToFile logToFile;
    /**
     * 右边进度条的星星动画是否显示出来
     */
    private boolean isStarVisible = false;

    public LiveAchievementBll(Activity activity, int liveType, LiveGetInfo mLiveGetInfo, boolean mIsLand) {
        this.activity = activity;
        this.liveType = liveType;
        this.getInfo = mLiveGetInfo;
        this.starCount = getInfo.getStarCount();
        this.goldCount = getInfo.getGoldCount();
        this.mIsLand = mIsLand;
        isSmallEnglish = mLiveGetInfo.getSmallEnglish();
        eventId = LiveVideoConfig.LIVE_STAR_INTERACT;
        //第一条线
        LineMath line1 = getAandB(starScaleStep1, 1.0f, starScaleStep2, starScaleMax);
        starInLine1a = line1.a;
        starInLine1b = line1.b;
        logger.d("StarInteractBll:starInLine1a=(" + starInLine1a + "," + starInLine1b + ")");
        //第二条线
        LineMath line2 = getAandB(starScaleStep2, starScaleMax, 1.0f, 1.0f);
        starInLine2a = line2.a;
        starInLine2b = line2.b;
        logger.d("StarInteractBll:starInLine2a=(" + starInLine2a + "," + starInLine2b + ")");
        LineMath line3 = getAandB(0.25f, 1f, 0.75f, -1f);
        starRotateLine1a = line3.a;
        starRotateLine1b = line3.b;
        LineMath line4 = getAandB(0.75f, -1f, 1f, 0f);
        starRotateLine2a = line4.a;
        starRotateLine2b = line4.b;
        logToFile = new LogToFile(activity, TAG);
        EventBusUtil.register(this);
    }


    public void setLiveBll(LiveAchievementHttp liveBll) {
        if (liveBll instanceof LiveAndBackDebug) {
            liveAndBackDebug = (LiveAndBackDebug) liveBll;
        }
        this.liveBll = liveBll;
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void initView(RelativeLayout bottomContent, RelativeLayout mContentView) {
        this.bottomContent = bottomContent;
//        if (myView != null) {
//            ViewGroup group = (ViewGroup) myView.getParent();
//            if (group != null) {
//                group.removeView(myView);
//            }
//            bottomContent.addView(myView);
//            group = (ViewGroup) flyStat.getParent();
//            if (group != null) {
//                group.removeView(flyStat);
//            }
//            bottomContent.addView(flyStat);
//            return;
//        }
//        myView = activity.getLayoutInflater().inflate(R.layout.item_livevideo_stat, bottomContent, false);
        myView = (ViewGroup) activity.findViewById(R.id.rl_livevideo_star_content);
        if (myView == null) {
            myView = mContentView.findViewById(R.id.rl_livevideo_star_content);
        }
        myView.setVisibility(View.VISIBLE);

        View layout_livevideo_stat_gold;
        if (!isSmallEnglish) {
            layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_stat_gold,
                    myView, false);
        } else {
            layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout
                    .layout_livevideo_small_english_stat_gold, myView, false);
        }
        myView.addView(layout_livevideo_stat_gold);
        ivStarInteractStat = (ImageView) myView.findViewById(R.id.iv_livevideo_starinteract_stat);
        tvStarInteractCount = (TextView) myView.findViewById(R.id.tv_livevideo_starinteract_count);
        tvStarInteractCountHind = (TextView) myView.findViewById(R.id.tv_livevideo_starinteract_count_hind);

        ivStarInteractGold = (ImageView) myView.findViewById(R.id.iv_livevideo_starinteract_gold);
        tvStarInteractGoldCount = (TextView) myView.findViewById(R.id.tv_livevideo_starinteract_gold_count);
        tvStarInteractGoldHind = (TextView) myView.findViewById(R.id.tv_livevideo_starinteract_gold_hind);
//        if (isExpe) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) myView.getLayoutParams();
//            lp.topMargin = (int) (44 * ScreenUtils.getScreenDensity() + 10);
//            bottomContent.addView(myView, lp);
//        } else {
//            bottomContent.addView(myView);
//        }
//        if (mIsLand) {
//            if (starCount > 0) {
//                myView.setVisibility(View.VISIBLE);
//            } else {
//                myView.setVisibility(View.INVISIBLE);
//            }
//        } else {
//            myView.setVisibility(View.INVISIBLE);
//        }
        if (starCount < 10) {
            tvStarInteractCount.setText(isSmallEnglish ? "0" + starCount : "×0" + starCount);
        } else {
            tvStarInteractCount.setText(isSmallEnglish ? "" + starCount : "×" + starCount);
        }
        if (goldCount < 10) {
            tvStarInteractGoldCount.setText(isSmallEnglish ? "0" + goldCount : "×0" + goldCount);
        } else {
            tvStarInteractGoldCount.setText(isSmallEnglish ? "" + goldCount : "×" + goldCount);
        }
//        if (AppConfig.DEBUG) {
//            ivStarInteractStat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    starCount++;
//                    if (starCount < 10) {
//                        tvStarInteractCountHind.setText("×0" + starCount);
//                    } else {
//                        tvStarInteractCountHind.setText("×" + starCount);
//                    }
//                    logger.i( "onClick:id=" + tvStarInteractCountHind.getId());
//                    AllAnimation allAnimation = onReceiveStat(AnimationType_STAR, 1, "");
//                    allAnimation.setOnAnimationEnd(new OnAnimationEnd() {
//                        @Override
//                        public void onEnd() {
//                            goldCount += 2;
//                            onReceiveStat(AnimationType_GOLD, 2, "");
//                        }
//                    });
////                    liveBll.getStuGoldCount();
//                }
//            });
//            ivStarInteractGold.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    goldCount++;
//                    if (goldCount < 10) {
//                        tvStarInteractGoldHind.setText("×0" + goldCount);
//                    } else {
//                        tvStarInteractGoldHind.setText("×" + goldCount);
//                    }
//                    logger.i( "onClick:id=" + tvStarInteractGoldHind.getId());
//                    onReceiveStat(AnimationType_GOLD, 1, "");
////                    liveBll.getStuGoldCount();
//                }
//            });
//        }
        View rl_livevideo_starinteract_layout = bottomContent.findViewById(R.id.rl_livevideo_starinteract_layout);
        if (rl_livevideo_starinteract_layout != null) {//移除旧的view
//            bottomContent.removeView(rl_livevideo_starinteract_layout);
            flyStat = rl_livevideo_starinteract_layout;
        } else {
            flyStat = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_stat_fly, bottomContent, false);
            bottomContent.addView(flyStat);
        }
        flyStat.setVisibility(View.INVISIBLE);
        View rl_livevideo_starinteract_stat_light_layout = bottomContent.findViewById(R.id
                .rl_livevideo_starinteract_stat_light_layout);
        if (rl_livevideo_starinteract_stat_light_layout != null) {//移除旧的view
//            bottomContent.removeView(rl_livevideo_starinteract_stat_light_layout);
            flyLight = rl_livevideo_starinteract_stat_light_layout;
        } else {
            flyLight = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_stat_fly_light, bottomContent,
                    false);
            bottomContent.addView(flyLight);
        }
        flyLight.setVisibility(View.INVISIBLE);
        getLayoutParams();
        mStarCountAnimSlideIn = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_text_in);
        mGoldCountAnimSlideIn = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_text_in);
        mStarCountAnimSlideOut = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_text_out);
        mStarLightAnimRotate = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_light_rotate);
        mStarCountAnimSlideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (tvStarInteractCountHind.getVisibility() != View.VISIBLE) {
                    tvStarInteractCountHind.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (starCount < 10) {
                    tvStarInteractCount.setText(isSmallEnglish ? "0" + starCount : "×0" + starCount);
                } else {
                    tvStarInteractCount.setText(isSmallEnglish ? "" + starCount : "×" + starCount);
                }
                TextView tempTextView = tvStarInteractCount;
                tvStarInteractCount = tvStarInteractCountHind;
                tvStarInteractCountHind = tempTextView;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mGoldCountAnimSlideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (tvStarInteractGoldHind.getVisibility() != View.VISIBLE) {
                    tvStarInteractGoldHind.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView tempTextView = tvStarInteractGoldCount;
                tvStarInteractGoldCount = tvStarInteractGoldHind;
                tvStarInteractGoldHind = tempTextView;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void getLayoutParams() {
//        {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myView.getLayoutParams();
//            params.rightMargin = (int) (wradio + 16 * ScreenUtils.getScreenDensity());
//            myView.setLayoutParams(params);
//        }
        ivStarInteractStat.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //飞的星星的位置
                int screenWidth = ScreenUtils.getScreenWidth();
                int screenHeight = ScreenUtils.getScreenHeight();
                int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
                {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
                    params.leftMargin = (screenWidth - wradio - flyStat.getWidth()) / 2;
                    topMargin = params.topMargin = (screenHeight - flyStat.getHeight()) / 2;
//                    flyStat.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(flyStat, params);
                    startPoint = new Point(params.leftMargin, params.topMargin);
                }
                {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyLight.getLayoutParams();
                    params.leftMargin = (screenWidth - wradio - flyLight.getWidth()) / 2;
                    params.topMargin = (screenHeight - flyLight.getHeight()) / 2;
//                    flyLight.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(flyLight, params);
                }
                ivStarInteractStat.getViewTreeObserver().removeOnPreDrawListener(this);
                int[] outLocation = new int[2];
                ivStarInteractStat.getLocationInWindow(outLocation);
                endStarPoint = new Point(outLocation[0] - (flyStat.getWidth() - ivStarInteractStat.getWidth()) / 2,
                        outLocation[1] - (flyStat.getHeight() - ivStarInteractStat.getHeight()) / 2);
                ivStarInteractGold.getLocationInWindow(outLocation);
                endGoldPoint = new Point(outLocation[0] - (flyStat.getWidth() - ivStarInteractGold.getWidth()) / 2,
                        outLocation[1] - (flyStat.getHeight() - ivStarInteractGold.getHeight()) / 2);
                controlPoint = new Point(outLocation[0] - 60, topMargin + 60);
                return false;
            }
        });
    }

    @Override
    public void onStarStart(ArrayList<String> data, String starid, String answer, String nonce) {
        this.mStarid = starid;
        this.data = data;
        statInteractStart = true;
        if ("".equals(answer)) {
            myMsg = null;
        } else {
            isSend = true;
            myMsg = answer;
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "starOpen");
        mData.put("starAnswer", "" + answer);
        mData.put("statue", "true");
        mData.put("starid", mStarid);
        if (!StringUtils.isEmpty(nonce)) {
            mData.put("ex", "Y");
            mData.put("sno", "2");
            mData.put("stable", "1");
        }
        liveAndBackDebug.umsAgentDebugPv(eventId, mData);
    }

    @Override
    public void onStarStop(final String id, ArrayList<String> answer, final String nonce) {
        statInteractStart = false;
        isSend = false;
        final String myAnswer = this.myMsg;
        this.myMsg = null;
        if (!answer.isEmpty() && myAnswer != null) {
            int receive = -1;
            for (int i = 0; i < answer.size(); i++) {
                if (myAnswer.equals(answer.get(i))) {
                    receive = i;
                    break;
                }
            }
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "starClose");
            mData.put("star_id", id);
            mData.put("status", "" + (receive > -1 ? 1 : 0));
            mData.put("answer", myAnswer);
            mData.put("starid", mStarid);
            mData.put("star_num", "" + (starCount));
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
            if (receive > -1) {
                liveBll.setStuStarCount(1000, id, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        starCount++;
                        if (starCount < 10) {
                            tvStarInteractCountHind.setText(isSmallEnglish ? "0" + starCount : "×0" + starCount);
                        } else {
                            tvStarInteractCountHind.setText(isSmallEnglish ? "" + starCount : "×" + starCount);
                        }
                        if (mIsLand) {
                            onReceiveStat(AnimationType_STAR, 1, nonce);
                        }
                        Map<String, String> mData = new HashMap<>();
                        mData.put("logtype", "setStuStarCount");
                        mData.put("answer", myAnswer);
                        mData.put("star_id", id);
                        mData.put("status", "success");
                        mData.put("starnum", "" + (starCount));
                        mData.put("starid", mStarid);
                        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        Map<String, String> mData = new HashMap<>();
                        mData.put("logtype", "setStuStarCount");
                        mData.put("answer", myAnswer);
                        mData.put("star_id", id);
                        mData.put("starnum", "" + (starCount));
                        if (errStatus == 1) {
                            mData.put("status", "failure");
                        } else {
                            mData.put("status", "error");
                        }
                        mData.put("msg", failMsg);
                        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
                    }
                });
            }
        }
    }

    @Override
    public void onSendMsg(String msg) {
        if (statInteractStart) {
//            if (!isSend) {
//                if ("1".equals(msg) || "2".equals(msg)) {
//                    myMsg = msg;
//                    liveBll.sendStat(msg);
//                    isSend = true;
//                }
//            }
            for (int i = 0; i < data.size(); i++) {
                String str = data.get(i);
                if (str.equalsIgnoreCase(msg)) {
                    myMsg = msg.toLowerCase();
                    liveBll.sendStat(i);
                    isSend = true;
                    Map<String, String> mData = new HashMap<>();
                    mData.put("logtype", "sendStarAnswer");
                    mData.put("answer", msg);
                    mData.put("status", "true");
                    mData.put("starid", mStarid);
                    liveAndBackDebug.umsAgentDebugSys(eventId, mData);
                    break;
                }
            }
        }
    }

    @Override
    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        int starCountAdd = starAndGoldEntity.getStarCount() - starCount;
        AllAnimation allAnimationStar = null;
        if (starCountAdd > 0) {
            allAnimationStar = onReceiveStat(AnimationType_STAR, starCountAdd, "");
        }
        starCount = starAndGoldEntity.getStarCount();
        final int goldCountAdd = starAndGoldEntity.getGoldCount() - goldCount;
        if (goldCountAdd > 0) {
            if (allAnimationStar == null) {
                onReceiveStat(AnimationType_GOLD, goldCountAdd, "");
            } else {
                allAnimationStar.setOnAnimationEnd(new OnAnimationEnd() {
                    @Override
                    public void onEnd() {
                        onReceiveStat(AnimationType_GOLD, goldCountAdd, "");
                    }
                });
            }
        }
        goldCount = starAndGoldEntity.getGoldCount();
        if (starCount < 10) {
            tvStarInteractCountHind.setText(isSmallEnglish ? "0" + starCount : "×0" + starCount);
        } else {
            tvStarInteractCountHind.setText(isSmallEnglish ? "" + starCount : "×" + starCount);
        }
        if (goldCount < 10) {
            tvStarInteractGoldHind.setText(isSmallEnglish ? "0" + goldCount : "×0" + goldCount);
        } else {
            tvStarInteractGoldHind.setText(isSmallEnglish ? "" + goldCount : "×" + goldCount);
        }
    }

    @Override
    public void onStarAdd(int star, float x, float y) {
        Point startPoint = new Point(x, y);
        starCount += star;
        if (starCount < 10) {
            tvStarInteractCountHind.setText(isSmallEnglish ? "0" + starCount : "×0" + starCount);
        } else {
            tvStarInteractCountHind.setText(isSmallEnglish ? "" + starCount : "×" + starCount);
        }
        final View flyStat = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_english_stat_fly,
                bottomContent, false);
        //隐藏进度条星星动画
        if (!isStarVisible) {
            flyStat.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
        params.leftMargin = (int) startPoint.getX();
        params.topMargin = (int) startPoint.getY();
        bottomContent.addView(flyStat, params);
        final ImageView iv_livevideo_starinteract_stat = (ImageView) flyStat.findViewById(R.id
                .iv_livevideo_starinteract_stat);
        iv_livevideo_starinteract_stat.setImageResource(isSmallEnglish ?
                R.drawable.bg_livevideo_small_english_statinteract_stat_big :
                R.drawable.bg_livevideo_statinteract_stat_big);

        if (endStarPoint == null) {
            logToFile.d("onStarAdd:endGoldPoint=null");
            int[] outLocation = new int[2];
            ivStarInteractStat.getLocationInWindow(outLocation);
            endStarPoint = new Point(outLocation[0] - (flyStat.getWidth() - ivStarInteractStat.getWidth()) / 2,
                    outLocation[1] - (flyStat.getHeight() - ivStarInteractStat.getHeight()) / 2);
        }
        ValueAnimator translateValueAnimator = ValueAnimator.ofObject(new LineEvaluator(), new LineEvaluator
                .PointAndFloat(startPoint), new LineEvaluator.PointAndFloat(endStarPoint));
        translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translateValueAnimator.setDuration(600);
        translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LineEvaluator.PointAndFloat currentPoint = (LineEvaluator.PointAndFloat) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
                params.topMargin = (int) currentPoint.point.getY();
                params.leftMargin = (int) currentPoint.point.getX();
//                    flyStat.setLayoutParams(params);
                if (isStarVisible) {
                    LayoutParamsUtil.setViewLayoutParams(flyStat, params);
                }
                float fraction = currentPoint.fraction;
                flyStat.setAlpha(1 - fraction);
                int width = iv_livevideo_starinteract_stat.getWidth();
                float scale = ((float) ivStarInteractStat.getWidth() / (float) width - 1) * fraction + 1;
                iv_livevideo_starinteract_stat.setScaleX(scale);
                iv_livevideo_starinteract_stat.setScaleY(scale);
                logger.i("onAnimationUpdate:fraction=" + fraction + ",leftMargin=" + params.leftMargin);
//                    logger.i( "onAnimationUpdate:fraction=" + fraction + ",scale=" + scale + ",s=" + ((float)
// ivStarInteractStat.getWidth() / (float) width));
            }
        });
        translateValueAnimator.addListener(new Animator.AnimatorListener() {
            long before;

            @Override
            public void onAnimationStart(Animator animation) {
                before = System.currentTimeMillis();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                tvStarInteractCountHind.startAnimation(mStarCountAnimSlideIn);
                tvStarInteractCount.startAnimation(mStarCountAnimSlideOut);
                flyStat.post(new Runnable() {
                    @Override
                    public void run() {
                        int[] outLocation = new int[2];
                        flyStat.getLocationInWindow(outLocation);
                        String location1 = outLocation[0] + "-" + outLocation[1];
                        ivStarInteractStat.getLocationInWindow(outLocation);
                        String location2 = outLocation[0] + "-" + outLocation[1];
                        bottomContent.removeView(flyStat);
//                        String status = "true";
//                        Map<String, String> mData = new HashMap<>();
//                        mData.put("logtype", "showAnimation");
//                        mData.put("startnum", "" + starCount);
//                        mData.put("status", status);
//                        mData.put("time", "" + (System.currentTimeMillis() - before));
//                        mData.put("location1", location1);
//                        mData.put("location2", location2);
//                        liveBll.umsAgentDebugPv(eventId, mData);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translateValueAnimator.start();
    }

    private AllAnimation onReceiveStat(int type, int starCount, String nonce) {
        if (starCount == 0) {
            return null;
        }
        if (startPoint == null) {
            logToFile.d("onReceiveStat:startPoint=null");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
            startPoint = new Point(params.leftMargin, params.topMargin);
        }
        final View flyStat = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_stat_fly, bottomContent,
                false);
        TextView tv_livevideo_statinteract_count = (TextView) flyStat.findViewById(R.id
                .tv_livevideo_starinteract_count);
        tv_livevideo_statinteract_count.setText("×" + starCount);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
        params.leftMargin = (int) startPoint.getX();
        params.topMargin = (int) startPoint.getY();
        bottomContent.addView(flyStat, params);
        AllAnimation allAnimation;
        if (!allAnimations.isEmpty()) {
            allAnimation = allAnimations.remove(0);
            logger.i("onReceiveStat:allAnimation=old");
        } else {
            allAnimation = new AllAnimation();
            logger.i("onReceiveStat:allAnimation=new");
        }
        allAnimation.setFlyStat(type, flyStat);
        allAnimation.setNonce(nonce);
        allAnimation.start();
        return allAnimation;
//        myView.setVisibility(View.VISIBLE);
    }

    public void onConfigurationChanged(boolean isLand) {
        this.mIsLand = isLand;
        if (myView != null) {
//            if (mIsLand) {
//                if (starCount > 0) {
//                    myView.setVisibility(View.VISIBLE);
//                } else {
//                    myView.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                myView.setVisibility(View.INVISIBLE);
//            }
            getLayoutParams();
        }
    }

    class LineMath {
        float a;
        float b;

        public LineMath(float a, float b) {
            this.a = a;
            this.b = b;
        }
    }

    LineMath getAandB(float x1, float y1, float x2, float y2) {
        //y=ax+b;
        float a = (y2 - y1) / (x2 - x1);
        float b = y1 - a * x1;
        return new LineMath(a, b);
    }

    private class AllAnimation {
        int type;
        View flyStat;
        /**
         * 动画出现
         */
        private Animation mAnimSlideInStep1;
        /**
         * 晃动动画
         */
        private Animation mAnimSlideRotate;
        /**
         * 移动动画
         */
        ValueAnimator translateValueAnimator;
        ImageView ivStarinteractStat;
        boolean isLightRotate = false;
        LineEvaluator.PointAndFloat endLinePoint;
        OnAnimationEnd onAnimationEnd;
        String nonce;

        AllAnimation() {
            mAnimSlideInStep1 = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_in_step1);
            mAnimSlideInStep1.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    float output;
                    if (input < starScaleStep1) {
                        output = accelerateDecelerateInterpolator.getInterpolation(input / starScaleStep1);
                    } else if (input < starScaleStep2) {
                        output = starInLine1a * input + starInLine1b;
                        if (!isLightRotate) {
                            isLightRotate = true;
                            if (firstAllAnimation == AllAnimation.this) {
                                flyLight.startAnimation(mStarLightAnimRotate);
                            }
                        }
//                        output = accelerateInterpolator.getInterpolation(output);
//                        output = accelerateDecelerateInterpolator.getInterpolation(output);
                    } else {
                        output = starInLine2a * input + starInLine2b;
//                        output = accelerateInterpolator.getInterpolation(output);
//                        output = accelerateDecelerateInterpolator.getInterpolation(output);
                    }
                    logger.i("getInterpolation:input=" + input + ",output=" + output + ",sameIn=" +
                            (firstAllAnimation == AllAnimation.this) + "," + (lastAllAnimation == null));
                    if (firstAllAnimation == AllAnimation.this) {
                        flyLight.setScaleX(output);
                        flyLight.setScaleY(output);
                    }
                    return output;
                }
            });
            mAnimSlideRotate = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_rotate);
//            mAnimSlideRotate.setRepeatCount(2);
            if (startPoint == null) {
                logToFile.d("AllAnimation:startPoint=null");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
                startPoint = new Point(params.leftMargin, params.topMargin);
            }
            endLinePoint = new LineEvaluator.PointAndFloat();
            translateValueAnimator = ValueAnimator.ofObject(new LineEvaluator(), new LineEvaluator.PointAndFloat
                    (startPoint), endLinePoint);
            translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            translateValueAnimator.setDuration(600);
            translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LineEvaluator.PointAndFloat currentPoint = (LineEvaluator.PointAndFloat) animation
                            .getAnimatedValue();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
                    params.topMargin = (int) currentPoint.point.getY();
                    params.leftMargin = (int) currentPoint.point.getX();
//                    flyStat.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(flyStat, params);
                    float fraction = currentPoint.fraction;
                    flyStat.setAlpha(1 - fraction);
                    int width = ivStarinteractStat.getWidth();
                    float scale = ((float) ivStarInteractStat.getWidth() / (float) width - 1) * fraction + 1;
                    ivStarinteractStat.setScaleX(scale);
                    ivStarinteractStat.setScaleY(scale);
                    logger.i("onAnimationUpdate:fraction=" + fraction + ",leftMargin=" + params.leftMargin);
//                    logger.i( "onAnimationUpdate:fraction=" + fraction + ",scale=" + scale + ",s=" + ((float)
// ivStarInteractStat.getWidth() / (float) width));
                }
            });
            translateValueAnimator.addListener(new Animator.AnimatorListener() {
                long before;

                @Override
                public void onAnimationStart(Animator animation) {
                    before = System.currentTimeMillis();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (type == AnimationType_STAR) {
                        tvStarInteractCountHind.startAnimation(mStarCountAnimSlideIn);
                        tvStarInteractCount.startAnimation(mStarCountAnimSlideOut);
                    } else {
                        tvStarInteractGoldHind.startAnimation(mGoldCountAnimSlideIn);
                        tvStarInteractGoldCount.startAnimation(mStarCountAnimSlideOut);
                    }
                    if (onAnimationEnd != null) {
                        onAnimationEnd.onEnd();
                        onAnimationEnd = null;
                    }
                    flyStat.post(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, String> mData = new HashMap<>();
                            int[] outLocation = new int[2];
                            flyStat.getLocationInWindow(outLocation);
                            String location1 = outLocation[0] + "-" + outLocation[1];
                            ivStarInteractStat.getLocationInWindow(outLocation);
                            String location2 = outLocation[0] + "-" + outLocation[1];
                            String status = "true";
                            mData.put("logtype", "showAnimation");
                            mData.put("startnum", "" + starCount);
                            mData.put("status", status);
                            mData.put("starid", mStarid);
                            if (!StringUtils.isEmpty(nonce)) {
                                mData.put("ex", "Y");
                                mData.put("sno", "4");
                                mData.put("stable", "1");
                            }
                            mData.put("time", "" + (System.currentTimeMillis() - before));
                            mData.put("location1", location1);
                            mData.put("location2", location2);
                            liveAndBackDebug.umsAgentDebugPv(eventId, mData);
                            bottomContent.removeView(flyStat);
                            allAnimations.add(AllAnimation.this);
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mAnimSlideInStep1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    flyStat.clearAnimation();
                    flyStat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flyStat.startAnimation(mAnimSlideRotate);
//                            rotateValueAnimator.start();
                        }
                    }, 300);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mAnimSlideRotate.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float fraction) {
                    float output = fraction;
                    if (fraction < 0.25f) {
                        output = fraction / 0.25f;
                    } else if (fraction < 0.75f) {
                        output = starRotateLine1a * fraction + starRotateLine1b;
                    } else {
                        output = starRotateLine2a * fraction + starRotateLine2b;
                    }
                    logger.d("RotateInterpolator:input=" + fraction + "," + output);
                    return output;
                }
            });
            mAnimSlideRotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    flyLight.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lastAllAnimation == AllAnimation.this) {
                                lastAllAnimation = null;
                                firstAllAnimation = null;
                                flyLight.clearAnimation();
                                flyLight.setVisibility(View.GONE);
                            }
                            translateValueAnimator.start();
                        }
                    }, 300);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        public void setFlyStat(int type, View flyStat) {
            this.type = type;
            this.flyStat = flyStat;
            ivStarinteractStat = (ImageView) flyStat.findViewById(R.id.iv_livevideo_starinteract_stat);
            if (type == AnimationType_STAR) {
                endLinePoint.point = endStarPoint;
                ivStarinteractStat.setImageResource(isSmallEnglish
                        ? R.drawable.bg_livevideo_small_english_statinteract_stat_big
                        : R.drawable.bg_livevideo_statinteract_stat_big);
            } else {
                endLinePoint.point = endGoldPoint;
                ivStarinteractStat.setImageResource(isSmallEnglish
                        ? R.drawable.bg_livevideo_small_english_statinteract_gold_big
                        : R.drawable.bg_livevideo_statinteract_gold_big);
            }
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public void start() {
            if (firstAllAnimation == null) {
                firstAllAnimation = AllAnimation.this;
            }
            lastAllAnimation = AllAnimation.this;
            isLightRotate = false;
            flyLight.setVisibility(View.VISIBLE);
            flyStat.startAnimation(mAnimSlideInStep1);
//            flyStat.startAnimation(mAnimSlideRotate);
        }

        public void setOnAnimationEnd(OnAnimationEnd onAnimationEnd) {
            this.onAnimationEnd = onAnimationEnd;
        }
    }

    interface OnAnimationEnd {
        void onEnd();
    }
}

//        AllAnimation() {
//            final AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new
// AccelerateDecelerateInterpolator();
//            mAnimSlideInStep1 = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_in_step1);
//            mAnimSlideInStep1.setInterpolator(accelerateDecelerateInterpolator);
//            mAnimSlideInStep2 = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_in_step2);
//            mAnimSlideRotate = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_rotate);
//            mAnimSlideRotate.setRepeatCount(2);
//            translateValueAnimator = ValueAnimator.ofObject(new LineEvaluator(), new LineEvaluator.PointAndFloat
// (startPoint), new LineEvaluator.PointAndFloat(endPoint));
//            translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//            translateValueAnimator.setDuration(600);
//            translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    LineEvaluator.PointAndFloat currentPoint = (LineEvaluator.PointAndFloat) animation
// .getAnimatedValue();
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
//                    params.topMargin = (int) currentPoint.point.getY();
//                    params.leftMargin = (int) currentPoint.point.getX();
//                    flyStat.setLayoutParams(params);
//                    float fraction = currentPoint.fraction;
//                    flyStat.setAlpha(1 - fraction);
//                    int width = ivStarinteractStat.getWidth();
//                    float scale = ((float) ivStarInteractStat.getWidth() / (float) width - 1) * fraction + 1;
//                    ivStarinteractStat.setScaleX(scale);
//                    ivStarinteractStat.setScaleY(scale);
//                    logger.i( "onAnimationUpdate:fraction=" + fraction + ",scale=" + scale + ",s=" + ((float)
// ivStarInteractStat.getWidth() / (float) width));
//                }
//            });
//            translateValueAnimator.addListener(new Animator.AnimatorListener() {
//                long before;
//
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    before = System.currentTimeMillis();
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    tvStarInteractCountHind.startAnimation(mStarCountAnimSlideIn);
//                    tvStarInteractCount.startAnimation(mStarCountAnimSlideOut);
//                    flyStat.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Map<String, String> mData = new HashMap<>();
//                            int[] outLocation = new int[2];
//                            flyStat.getLocationInWindow(outLocation);
//                            String location1 = outLocation[0] + "-" + outLocation[1];
//                            ivStarInteractStat.getLocationInWindow(outLocation);
//                            String location2 = outLocation[0] + "-" + outLocation[1];
//                            String status = "true";
//                            if (!location1.equals(location2)) {
////                                setLayoutParams();
//                                status = "false";
//                            }
//                            mData.put("log_type", "showAnimation");
//                            mData.put("star_num", "" + starCount);
//                            mData.put("status", status);
//                            mData.put("time", "" + (System.currentTimeMillis() - before));
//                            mData.put("location1", location1);
//                            mData.put("location2", location2);
//                            liveBll.umsAgentDebugSys(eventId, mData);
//                            bottomContent.removeView(flyStat);
//                            allAnimations.add(AllAnimation.this);
//                        }
//                    });
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
//            mAnimSlideInStep1.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    if (firstAllAnimation == AllAnimation.this) {
//                        flyLight.startAnimation(mStarLightAnimRotate);
//                    }
//                    flyStat.startAnimation(mAnimSlideInStep2);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//            mAnimSlideInStep2.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    flyStat.startAnimation(mAnimSlideRotate);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//            mAnimSlideRotate.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    if (lastAllAnimation == AllAnimation.this) {
//                        lastAllAnimation = null;
//                        firstAllAnimation = null;
//                        flyLight.clearAnimation();
//                        flyLight.setVisibility(View.GONE);
//                    }
//                    translateValueAnimator.start();
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//        }