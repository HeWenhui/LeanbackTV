package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.Point;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveLottieAnimationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyuqiang on 2017/7/20.
 * 本场成就
 */
public class LiveStandAchievementBll implements StarInteractAction {
    private String TAG = "LiveStandAchievementBll";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private String eventId;
    private int liveType;
    private Activity activity;
    private View flyStat;
    private View flyLight;
    private StandLiveLottieAnimationView lottieAnimationView;
    @Deprecated
    private LottieComposition composition;
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
     * 右侧星星和金币数字动画消失
     */
    private Animation mStarCountAnimSlideOut;
    /**
     * 背景光的动画旋转
     */
    private Animation mStarLightAnimRotate;
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
    /**
     * 星星lottie动画是否显示
     */
    private final boolean isStarLottieVisible = false;
    /**
     * 星星数量
     */
    private TextView tvStarCount;
    /**
     * 金币数量
     */
    private TextView tvGoldCount;

    public LiveStandAchievementBll(Activity activity, int liveType, int starCount, int goldCount, boolean mIsLand) {
        this.activity = activity;
        this.liveType = liveType;
        this.starCount = starCount;
        this.goldCount = goldCount;
        this.mIsLand = mIsLand;
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
        View layout_livevideo_stat_gold = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_stand_stat_gold, myView, false);
        myView.addView(layout_livevideo_stat_gold);
        lottieAnimationView = activity.findViewById(R.id.lav_livevideo_chievement);
//        tvStarCount = myView.findViewById(R.id.tv_livevideo_star_count);

//        if (tvStarCount == null) {
        tvStarCount = activity.findViewById(R.id.tv_livevideo_star_count);
        tvGoldCount = activity.findViewById(R.id.tv_livevideo_gold_count);
//        }


        initlottieAnim();
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
        flyStat = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_stat_fly, bottomContent, false);
        flyStat.setVisibility(View.INVISIBLE);
        bottomContent.addView(flyStat);
        flyLight = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_stat_fly_light, bottomContent, false);
        flyLight.setVisibility(View.INVISIBLE);
        bottomContent.addView(flyLight);
        mStarCountAnimSlideOut = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_text_out);
        mStarLightAnimRotate = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_star_light_rotate);
    }

    private void initlottieAnim() {
        final String fileName = "live_stand/lottie/live_stand_jindu.json";
        final HashMap<String, String> assetFolders = new HashMap<String, String>();
        assetFolders.put(fileName, "live_stand/lottie/jindu");
        LottieComposition.Factory.fromAssetFileName(activity, fileName, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                logger.d("onCompositionLoaded:composition=" + composition + ",view=" + (lottieAnimationView == null));
                if (composition == null) {
//                    Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Toast.makeText(activity, "加载成功", Toast.LENGTH_SHORT).show();
                LiveStandAchievementBll.this.composition = composition;
                if (isStarLottieVisible) {
                    lottieAnimationView.setImageAssetsFolder(assetFolders.get(fileName));
                    lottieAnimationView.setComposition(composition);
                }
                setGoldCount();
            }
        });
    }

    private void setGoldCount() {
        int goldCount2 = goldCount;
        if (goldCount2 > 999) {
            goldCount2 = 999;
        }
        if (isStarLottieVisible) {
            lottieAnimationView.setGoldCount(goldCount2);
        } else {
            tvGoldCount.setText(String.valueOf(goldCount2));
        }
        int starCount2 = starCount;
        if (starCount2 > 999) {
            starCount2 = 999;
        }
        if (isStarLottieVisible) {
            lottieAnimationView.setStarCount(starCount2);
        } else {
            tvStarCount.setText(String.valueOf(starCount2));
        }
//        String num = "" + goldCount;
//        AssetManager manager = activity.getAssets();
//        Bitmap img_7Bitmap;
//        try {
//            img_7Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_9.png"));
//            Bitmap img_3Bitmap = BitmapFactory.decodeStream(manager.open("Images/jindu/img_3.png"));
//            Bitmap creatBitmap = Bitmap.createBitmap(img_7Bitmap.getWidth(), img_7Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(creatBitmap);
//            canvas.drawBitmap(img_7Bitmap, 0, 0, null);
//            Paint paint = new Paint();
//            paint.setTextSize(24);
//            paint.setColor(Color.WHITE);
//            float width = paint.measureText(num);
//            canvas.drawText(num, (img_7Bitmap.getWidth() - img_3Bitmap.getWidth() / 2) / 2 + img_3Bitmap.getWidth() / 2 - width / 2, img_7Bitmap.getHeight() / 2 + paint.measureText("a") / 2, paint);
////                    canvas.drawRect(img_9Bitmap.getWidth()/2, 0, img_3Bitmap.getWidth(), img_3Bitmap.getHeight(), paint);
//            img_7Bitmap = creatBitmap;
//        } catch (IOException e) {
////            e.printStackTrace();
//            return;
//        }
//        lottieAnimationView.updateBitmap("image_9", img_7Bitmap);
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
        starCount = starAndGoldEntity.getStarCount();
        goldCount = starAndGoldEntity.getGoldCount();
        setGoldCount();
    }

    @Override
    public void onStarAdd(int star, float x, float y) {
        Point startPoint = new Point(x, y);
        starCount += star;
//        final View flyStat = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_english_stat_fly, bottomContent, false);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
//        params.leftMargin = (int) startPoint.getX();
//        params.topMargin = (int) startPoint.getY();
//        bottomContent.addView(flyStat, params);
//        final ImageView ivStarinteractStat = (ImageView) flyStat.findViewById(R.id.ivStarinteractStat);
//        ValueAnimator translateValueAnimator = ValueAnimator.ofObject(new LineEvaluator(), new LineEvaluator.PointAndFloat(startPoint), new LineEvaluator.PointAndFloat(endStarPoint));
//        translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        translateValueAnimator.setDuration(600);
//        translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                LineEvaluator.PointAndFloat currentPoint = (LineEvaluator.PointAndFloat) animation.getAnimatedValue();
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
//                params.topMargin = (int) currentPoint.point.getY();
//                params.leftMargin = (int) currentPoint.point.getX();
////                    flyStat.setLayoutParams(params);
//                LayoutParamsUtil.setViewLayoutParams(flyStat, params);
//                float fraction = currentPoint.fraction;
//                flyStat.setAlpha(1 - fraction);
//                int width = ivStarinteractStat.getWidth();
////                float scale = ((float) ivStarInteractStat.getWidth() / (float) width - 1) * fraction + 1;
//                float scale = 1;
//                ivStarinteractStat.setScaleX(scale);
//                ivStarinteractStat.setScaleY(scale);
//                logger.i( "onAnimationUpdate:fraction=" + fraction + ",leftMargin=" + params.leftMargin);
////                    logger.i( "onAnimationUpdate:fraction=" + fraction + ",scale=" + scale + ",s=" + ((float) ivStarInteractStat.getWidth() / (float) width));
//            }
//        });
//        translateValueAnimator.start();
        setGoldCount();
    }

    private void onReceiveStat(int type, int starCount, String nonce) {
        if (starCount == 0) {
            return;
        }
//        final View flyStat = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_stat_fly, bottomContent, false);
//        TextView tv_livevideo_statinteract_count = (TextView) flyStat.findViewById(R.id.tv_livevideo_starinteract_count);
//        tv_livevideo_statinteract_count.setText("×" + starCount);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flyStat.getLayoutParams();
//        params.leftMargin = (int) startPoint.getX();
//        params.topMargin = (int) startPoint.getY();
//        bottomContent.addView(flyStat, params);
//        myView.setVisibility(View.VISIBLE);
        setGoldCount();
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

    private LiveHttpResponseParser mHttpResponseParser = null;

}