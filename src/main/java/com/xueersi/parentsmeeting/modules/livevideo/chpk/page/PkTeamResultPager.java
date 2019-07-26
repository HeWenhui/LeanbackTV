package com.xueersi.parentsmeeting.modules.livevideo.chpk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter.TeamStarAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.business.ChinesePkBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkResultLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SmoothAddNumTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SmoothProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 战队 pk 结果页
 *
 * @author yuanwei
 * <p>
 * created  at 2018/11/14 11:31
 */
public class PkTeamResultPager extends BasePager {
    private static final String TAG = "TeamPkResultPager";

    private final ChinesePkBll mTeamPkBll;
    /**
     * 老师点赞动画
     */
    private static final int ANIM_TYPE_PRIASE = 1;
    /**
     * pk 对手动画
     */
    private static final int ANIM_TYPE_PK_ADVERSARY = 2;
    /**
     * pk 结果
     */
    private static final int ANIM_TYPE_PK_REUSLT = 3;

    /**
     * 背景音效大小
     */
    private static final float SOUND_VOLUME_BG = 0.3f;
    /**
     * 前景音效大小
     */
    private static final float SOUND_VOLUME_FRONT = 0.6f;

    /**
     * 老师昵称最大字符数
     */
    private static final int TEACHER_NAME_MAXLEN = 6;
    /**
     * pk 对手音效进入时间点
     */
    private static final float FRACTION_MUSIC_IN = 0.11f;

    /**
     * 底部贡献之星 右边距
     */
    private static final float CONTRIBUTION_VIEW_RIGHTMARGIN = 0.046f;

    /**
     * 半身直播 贡献之星 右边距
     */
    private static final float CONTRIBUTION_VIEW_RIGHTMARGIN_HALFBODY = 0.15f;

    /**
     * pk 结果页 所用到的 音效资源id
     */
    int[] soundResArray = {
            R.raw.war_bg,
            R.raw.pk_adversary,
            R.raw.lose,
            R.raw.win
    };


    private LottieAnimationView lottieEffectView;
    private FrameLayout finalViewWrapper;
    private SmoothAddNumTextView finalOwnerEnergy;
    private SmoothAddNumTextView finalOtherEnergy;
    private SmoothProgressBar finalProgressBar;

    private FrameLayout frOwnerResultView;
    private ImageView ivOwnerTeamState;
    private ImageView ivOwnerTeamImage;
    private ImageView ivOwnerTeacherLogo;
    private TextView tvOwnerTeacherName;
    private TextView tvOwnerTeamSlogan;

    private ImageView resultTeamVsLogo;

    private FrameLayout frOtherResultView;
    private ImageView ivOtherTeamState;
    private ImageView ivOtherTeamLogo;
    private ImageView ivOtherTeacherLogo;
    private TextView tvOtherTeacherName;
    private TextView tvOtherTeamSlogan;

    private FrameLayout resultViewWrapper;
    private TextView resultMyAddEnergy;
    private SmoothAddNumTextView resultOwnerEnergy;
    private SmoothAddNumTextView resultOtherEnergy;
    private SmoothProgressBar resultProgressBar;

    private FrameLayout contributesRoot;
    private RecyclerView contributesView;
    private TimeCountDowTextView timeCountDowTextView;

    /**
     * 每题pk 结果页显示时长
     */
    private static final int CURRENT_PK_RESULT_AUTO_CLOSE_DRUATION = 10;
    private StudentPkResultEntity mFinalPkResult;
    private TeamStarAdapter pkResultAdapter;
    private List<TeamEnergyAndContributionStarEntity.ContributionStar> mContributions;
    private GridLayoutManager mLayoutManager;
    private SoundPoolHelper soundPoolHelper;

    public PkTeamResultPager(Context context, ChinesePkBll pkBll) {
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_chpk_teamresult, null);
        lottieEffectView = view.findViewById(R.id.lv_livevideo_chpk_lottieEffectView);
        finalViewWrapper = view.findViewById(R.id.fr_livevideo_chpk_finalViewWrapper);
        finalProgressBar = view.findViewById(R.id.pb_livevideo_chpk_finalProgerssBar);
        finalOwnerEnergy = view.findViewById(R.id.bt_livevideo_chpk_finalOwnerEnergy);
        finalOtherEnergy = view.findViewById(R.id.bt_livevideo_chpk_finalOtherEnergy);

        frOwnerResultView = view.findViewById(R.id.fr_livevideo_chpk_resultOwnerTeam);
        frOtherResultView = view.findViewById(R.id.fr_livevideo_chpk_resultOtherTeam);
        ivOwnerTeamState = view.findViewById(R.id.iv_livevideo_chpk_ownerTeamState);
        ivOwnerTeamImage = view.findViewById(R.id.iv_livevideo_chpk_OwnerTeamImage);
        ivOwnerTeacherLogo = view.findViewById(R.id.iv_livevideo_chpk_ownerTeacherLogo);
        tvOwnerTeacherName = view.findViewById(R.id.tv_livevideo_chpk_ownerTeacherName);
        tvOwnerTeamSlogan = view.findViewById(R.id.tv_livevideo_chpk_ownerTeamSlogan);

        resultTeamVsLogo = view.findViewById(R.id.iv_livevideo_chpk_resultTeamVsLogo);

        ivOtherTeamState = view.findViewById(R.id.iv_livevideo_chpk_otherTeamState);
        ivOtherTeamLogo = view.findViewById(R.id.iv_livevideo_chpk_otherTeamImage);
        ivOtherTeacherLogo = view.findViewById(R.id.iv_livevideo_chpk_otherTeacherLogo);
        tvOtherTeacherName = view.findViewById(R.id.tv_livevideo_chpk_otherTeacherName);
        tvOtherTeamSlogan = view.findViewById(R.id.tv_livevideo_chpk_otherTeamSlogan);

        resultViewWrapper = view.findViewById(R.id.fr_livevideo_chpk_resultViewWrapper);
        resultOwnerEnergy = view.findViewById(R.id.bt_livevideo_chpk_resultOwnerEnergy);
        resultOtherEnergy = view.findViewById(R.id.bt_livevideo_chpk_resultOtherEnergy);
        resultMyAddEnergy = view.findViewById(R.id.bt_livevideo_chpk_resultMyAddEnergy);
        resultProgressBar = view.findViewById(R.id.pb_livevideo_chpk_resultProgerssBar);

        timeCountDowTextView = view.findViewById(R.id.tv_teampk_pkresult_time_countdow);

        contributesRoot = view.findViewById(R.id.rv_livevideo_chpk_contributesRoot);
        contributesView = view.findViewById(R.id.rv_livevideo_chpk_contributesView);
        return view;
    }

    /**
     * 显示当场次答题 最终pk 结果
     *
     * @param data
     */
    public void showFinalPkResult(StudentPkResultEntity data) {

        if (data == null || data.getMyTeamResultInfo() == null || data.getCompetitorResultInfo() == null) {
            return;
        }

        mFinalPkResult = data;

        // 显示最终pk 进度值
        lottieEffectView.setVisibility(View.VISIBLE);
        finalViewWrapper.setVisibility(View.VISIBLE);

        int myTeamEnergy = (int) data.getMyTeamResultInfo().getEnergy();
        int otherTeamEnergy = (int) data.getCompetitorResultInfo().getEnergy();
        float ratio;
        if (myTeamEnergy + otherTeamEnergy > 0) {
            ratio = myTeamEnergy / (float) (myTeamEnergy + otherTeamEnergy);
        } else {
            ratio = 0.5f;
        }

        finalProgressBar.setProgress((int) (ratio * finalProgressBar.getMax()));
        finalOwnerEnergy.setText(myTeamEnergy + "");
        finalOtherEnergy.setText(otherTeamEnergy + "");


        int pkResult = (int) (data.getMyTeamResultInfo().getEnergy() - data.getCompetitorResultInfo().getEnergy());
        if (pkResult == 0) {
            showDrawAnim(pkResult);
            logger.e("======> ResultPager show showDraw");
        } else if (pkResult > 0) {
            showWinAnim(pkResult);
            logger.e("======> ResultPager show showWin");
        } else {
            showLoseAnim(pkResult);
            logger.e("======> ResultPager show showLose");
        }
    }

    /**
     * 展示当前答题的结果
     *
     * @param data
     */
    public void showCurrentResult(final TeamEnergyAndContributionStarEntity data) {

        frOwnerResultView.setVisibility(View.VISIBLE);
        frOtherResultView.setVisibility(View.VISIBLE);
        resultViewWrapper.setVisibility(View.VISIBLE);
        contributesRoot.setVisibility(View.VISIBLE);
        resultTeamVsLogo.setVisibility(View.VISIBLE);

        lottieEffectView.setVisibility(View.GONE);
        finalViewWrapper.setVisibility(View.GONE);

        //显示贡献之星
        if (data.getContributionStarList() != null && data.getContributionStarList().size() > 0) {
            if (mContributions == null) {
                mContributions = new ArrayList<TeamEnergyAndContributionStarEntity.ContributionStar>();
            }
            mContributions.clear();
            mContributions.addAll(data.getContributionStarList());
            initRecycleView();

            final StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
//            if (studyReportAction != null) {
//                mView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_PK_RESULT, mView, false, false);
//                    }
//                }, 300);
//
//            }
            // FIXME: 2019/6/12
//            if (studyReportAction != null && data.isMe()) {
            Single.
                    just(studyReportAction != null && data.isMe()).
                    filter(new Predicate<Boolean>() {
                        @Override
                        public boolean test(Boolean aBoolean) throws Exception {
                            return aBoolean;
                        }
                    }).
                    delay(300, TimeUnit.MILLISECONDS).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_PK_RESULT, mView, false, true);
                        }
                    });

//            }

        }
        //进度条动画
        try {
            updateProgressBar(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 初始战队信息
        long myTeamTotalEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
        long otherTeamTotalEnergy = data.getCompetitorEngerInfo().getTotalEnergy();
        ivOwnerTeamState.setVisibility(View.VISIBLE);
        ivOtherTeamState.setVisibility(View.VISIBLE);

        if (myTeamTotalEnergy > otherTeamTotalEnergy) {
            ivOwnerTeamState.setImageResource(R.drawable.livevideo_chpk_result_lead);
            ivOtherTeamState.setImageResource(R.drawable.livevideo_chpk_result_catch);
        } else if (otherTeamTotalEnergy > myTeamTotalEnergy) {
            ivOtherTeamState.setImageResource(R.drawable.livevideo_chpk_result_lead);
            ivOwnerTeamState.setImageResource(R.drawable.livevideo_chpk_result_catch);
        } else {
            ivOtherTeamState.setImageResource(R.drawable.livevideo_chpk_result_equal);
            ivOwnerTeamState.setImageResource(R.drawable.livevideo_chpk_result_equal);
        }

        ImageLoader.with(ContextManager.getContext()).load(data.getMyTeamEngerInfo().getTeacherImg()).asBitmap
                (new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                        Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap
                                .getHeight()) / 2);
                        ivOwnerTeacherLogo.setImageBitmap(resultBitmap);
                    }

                    @Override
                    public void onFail() {

                    }
                });

        ImageLoader.with(ContextManager.getContext()).load(data.getCompetitorEngerInfo().getTeacherImg())
                .asBitmap(new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                        Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap.getHeight()) / 2);
                        ivOtherTeacherLogo.setImageBitmap(resultBitmap);
                    }

                    @Override
                    public void onFail() {

                    }
                });
        ImageLoader.with(ContextManager.getContext()).load(data.getMyTeamEngerInfo().getImg()).into(ivOwnerTeamImage);
        ImageLoader.with(ContextManager.getContext()).load(data.getCompetitorEngerInfo().getImg()).into
                (ivOtherTeamLogo);
        tvOwnerTeacherName.setText(data.getMyTeamEngerInfo().getTeacherName());
        tvOtherTeacherName.setText(data.getCompetitorEngerInfo().getTeacherName());
        tvOwnerTeamSlogan.setText(data.getMyTeamEngerInfo().getSlogon());
        tvOtherTeamSlogan.setText(data.getCompetitorEngerInfo().getSlogon());
        timeCountDowTextView.setVisibility(View.INVISIBLE);
        startTimeCountDow(CURRENT_PK_RESULT_AUTO_CLOSE_DRUATION);

        // 更新左侧pk 状态栏
        if (mTeamPkBll != null) {
            mTeamPkBll.updatePkStateLayout(true);
        }
    }

    private void initRecycleView() {
        float density = mContext.getResources().getDisplayMetrics().density;

        //一行显示item 个数
        int spanCount = mContributions.size();
        if (spanCount > 5) {
            spanCount = 5;
        }

        int width = (int) (82 * density + 0.5f) * spanCount;
        ViewGroup.LayoutParams lp = contributesView.getLayoutParams();

        if (lp.width != width) {
            lp.width = width;
            contributesView.setLayoutParams(lp);
        }

        mLayoutManager = new GridLayoutManager(mContext, spanCount);
        contributesView.setLayoutManager(mLayoutManager);
        pkResultAdapter = new TeamStarAdapter(mContributions);
        contributesView.setAdapter(pkResultAdapter);
    }

    private void updateProgressBar(final TeamEnergyAndContributionStarEntity data) {
        //显示之前的pk 进度
        final long myTeamOldEnergy = data.getMyTeamEngerInfo().getTotalEnergy() - data.getMyTeamEngerInfo().getAddEnergy();
        long otherTeamOldEnergy = data.getCompetitorEngerInfo().getTotalEnergy() - data.getCompetitorEngerInfo().getAddEnergy();
        logger.e("========>updateProgressBar:" + myTeamOldEnergy + ":" + otherTeamOldEnergy);

        float ratio;
        if ((myTeamOldEnergy + otherTeamOldEnergy) > 0) {
            ratio = myTeamOldEnergy / (float) (myTeamOldEnergy + otherTeamOldEnergy);
        } else {
            ratio = 0.5f;
        }
        int progress = (int) (ratio * resultProgressBar.getMax() + 0.5);
        resultProgressBar.setProgress(progress);
        resultOwnerEnergy.setText(myTeamOldEnergy + "");
        resultOtherEnergy.setText(otherTeamOldEnergy + "");
        logger.e("========>updateProgressBar22222:" + progress);
//        mView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 2000);

        showNewProgress(data, myTeamOldEnergy);
    }

    private void showNewProgress(TeamEnergyAndContributionStarEntity data, long myTeamOldEnergy) {
        long myTeamTotalEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
        long otherTeamEnergy = data.getCompetitorEngerInfo().getTotalEnergy();
        float newRatio;
        if ((myTeamTotalEnergy + otherTeamEnergy) > 0) {
            newRatio = myTeamTotalEnergy / (float) (myTeamTotalEnergy + otherTeamEnergy);
        } else {
            newRatio = 0.5f;
        }
        int currentProgress = (int) (newRatio * resultProgressBar.getMax() + 0.5);
        resultProgressBar.animateToProgress(currentProgress);

        resultOwnerEnergy.setText(myTeamOldEnergy + "");
        resultOtherEnergy.setText(otherTeamEnergy + "");
        int addEnergy = (int) data.getMyTeamEngerInfo().getAddEnergy();
        startAddEnergyEffect(addEnergy);
    }

    private void startAddEnergyEffect(int increment) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_add_energy);
        animation.setFillAfter(true);
        resultMyAddEnergy.setVisibility(View.VISIBLE);
        resultMyAddEnergy.setText("+" + increment);
        resultMyAddEnergy.startAnimation(animation);
        resultOwnerEnergy.smoothAddNum(increment);
    }

    /**
     * 播放音乐
     *
     * @param resId  raw 中音效资源id
     * @param volume 音量
     * @param loop   是否循环播放
     */
    private void playMusic(int resId, float volume, boolean loop) {
        if (soundPoolHelper == null) {
            soundPoolHelper = new SoundPoolHelper(mContext, 2, AudioManager.STREAM_MUSIC);
        }
        soundPoolHelper.playMusic(resId, volume, loop);
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeMusic();
    }


    /**
     * 暂停音效
     * 注 此处的暂停  只是将音量设置为0  （因为 动画和音效是 同步的）
     */
    private void pauseMusic() {
        logger.e("======>pauseMusic called");
        if (soundPoolHelper != null) {
            for (int i = 0; i < soundResArray.length; i++) {
                soundPoolHelper.setVolume(soundResArray[i], 0);
            }
        }

    }


    /**
     * 恢复音乐播放
     * 注释  将音量恢复为暂停之前的状态
     */
    private void resumeMusic() {
        logger.e("======>resumeMusic called");
        if (soundPoolHelper != null) {
            for (int i = 0; i < soundResArray.length; i++) {
                if (soundResArray[i] == R.raw.war_bg) {
                    soundPoolHelper.setVolume(soundResArray[i], SOUND_VOLUME_BG);
                } else {
                    soundPoolHelper.setVolume(soundResArray[i], SOUND_VOLUME_FRONT);
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        closePkResultPager();
    }


    /**
     * 展示pk对手 lottie动画
     */
    public void showPkAdversary(TeamPkAdversaryEntity data) {

        if (data == null) {
            return;
        }

        // 显示准备战斗 状态
        ViewGroup viewGroup = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
        TeamPkStateLayout pkStateRootView = viewGroup.findViewById(R.id.tpkL_teampk_pkstate_root);

        if (pkStateRootView != null) {
            pkStateRootView.showPkReady();
        }

        // 播放背景音乐
        playMusic(R.raw.war_bg, SOUND_VOLUME_BG, true);

        final String lottieResPath = "chinesePk/vsteam/images";
        String lottieJsonPath = "chinesePk/vsteam/data.json";
        String[] targetFileNames = {"img_2.png", "img_8.png", "img_1.png", "img_7.png", "img_0.png", "img_12.png", "img_3.png", "img_9.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo = new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_2.png", 30);
        lottieEffectInfo.setTextSize("img_8.png", 30);
        lottieEffectInfo.setTextSize("img_1.png", 33);
        lottieEffectInfo.setTextSize("img_7.png", 33);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);

        String myTeacherName = data.getSelf().getTeacherName();
        if (myTeacherName.length() > TEACHER_NAME_MAXLEN) {
            myTeacherName = myTeacherName.substring(0, 6);
        }

        String otherTeacherName = data.getOpponent().getTeacherName();
        if (otherTeacherName.length() > TEACHER_NAME_MAXLEN) {
            otherTeacherName = myTeacherName.substring(0, 6);
        }

        lottieEffectInfo.addTeacherName("img_8.png", myTeacherName);
        lottieEffectInfo.addTeacherName("img_2.png", otherTeacherName);

        lottieEffectInfo.addSlogan("img_7.png", data.getSelf().getSlogon());
        lottieEffectInfo.addSlogan("img_1.png", data.getOpponent().getSlogon());

        lottieEffectInfo.addLogo("img_12.png", data.getSelf().getImg());
        lottieEffectInfo.addLogo("img_0.png", data.getOpponent().getImg());

        lottieEffectInfo.addTeacherHead("img_9.png", data.getSelf().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_3.png", data.getOpponent().getTeacherImg());

        final Runnable action = new Runnable() {
            @Override
            public void run() {
                closePkResultPager();
            }
        };

        lottieEffectView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieEffectView.postDelayed(action, 10 * 1000);
            }
        });

        lottieEffectView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean soundPlayed;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!soundPlayed && animation.getAnimatedFraction() > FRACTION_MUSIC_IN) {
                    soundPlayed = true;
                    playMusic(R.raw.pk_adversary, SOUND_VOLUME_FRONT, false);
                }
            }
        });

        try {
            lottieEffectView.setVisibility(View.VISIBLE);
            lottieEffectView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext),"chinesePk_vsteam");
            lottieEffectView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieEffectView, lottieImageAsset.getFileName(), lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });

            lottieEffectView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示平局 lottie anim
     */
    private void showDrawAnim(int pkResult) {
        // 播放胜利音效
        playMusic(R.raw.win, SOUND_VOLUME_FRONT, false);
        final String lottieResPath = "chinesePk/equal/images";
        String lottieJsonPath = "chinesePk/equal/data.json";
        String[] targetFileNames = {"img_10.png", "img_3.png", "img_11.png", "img_4.png", "img_13.png", "img_6.png", "img_8.png", "img_1.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo = new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_10.png", 26);
        lottieEffectInfo.setTextSize("img_3.png", 26);

        lottieEffectInfo.setTextSize("img_11.png", 32);
        lottieEffectInfo.setTextSize("img_4.png", 32);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_10.png", mFinalPkResult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_3.png", mFinalPkResult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_11.png", mFinalPkResult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_4.png", mFinalPkResult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_13.png", mFinalPkResult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_6.png", mFinalPkResult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_8.png", mFinalPkResult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_1.png", mFinalPkResult.getCompetitorResultInfo().getTeacherImg());

        try {
            lottieEffectView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieEffectView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieEffectView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieEffectView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lottieEffectView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT, pkResult));
    }

    /**
     * 展示pk 失败lottie 动画
     */
    private void showLoseAnim(int pkResult) {

        // 播放胜利音效
        playMusic(R.raw.lose, SOUND_VOLUME_FRONT, false);

        final String lottieResPath = "chinesePk/faile/images";
        String lottieJsonPath = "chinesePk/faile/data.json";

        String[] targetFileNames = {"img_8.png", "img_1.png", "img_11.png", "img_4.png", "img_13.png", "img_6.png", "img_9.png", "img_2.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo = new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_8.png", 26);
        lottieEffectInfo.setTextSize("img_1.png", 26);

        lottieEffectInfo.setTextSize("img_11.png", 25);
        lottieEffectInfo.setTextSize("img_4.png", 35);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_8.png", mFinalPkResult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_1.png", mFinalPkResult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_11.png", mFinalPkResult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_4.png", mFinalPkResult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_13.png", mFinalPkResult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_6.png", mFinalPkResult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_9.png", mFinalPkResult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_2.png", mFinalPkResult.getCompetitorResultInfo().getTeacherImg());

        try {
            lottieEffectView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieEffectView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieEffectView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieEffectView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lottieEffectView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT, pkResult));
    }

    public void showWinAnim(int pkResult) {

        // 播放胜利音效
        playMusic(R.raw.win, SOUND_VOLUME_FRONT, false);

        final String lottieResPath = "chinesePk/winer/images";
        String lottieJsonPath = "chinesePk/winer/data.json";
        String[] targetFileNames = {"img_10.png", "img_1.png", "img_11.png", "img_4.png", "img_13.png", "img_6.png", "img_8.png", "img_2.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo = new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);

        lottieEffectInfo.setTextSize("img_10.png", 26);
        lottieEffectInfo.setTextSize("img_1.png", 21);


        lottieEffectInfo.setTextSize("img_11.png", 35);
        lottieEffectInfo.setTextSize("img_4.png", 27);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_10.png", mFinalPkResult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_1.png", mFinalPkResult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_11.png", mFinalPkResult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_4.png", mFinalPkResult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_13.png", mFinalPkResult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_6.png", mFinalPkResult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_8.png", mFinalPkResult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_2.png", mFinalPkResult.getCompetitorResultInfo().getTeacherImg());
        lottieEffectView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT, pkResult));

        try {
            lottieEffectView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieEffectView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieEffectView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieEffectView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startTimeCountDow(int duration) {
        timeCountDowTextView.setTimeDuration(duration);
        timeCountDowTextView.setTimeSuffix("秒后关闭");
        timeCountDowTextView.startCountDow();
        timeCountDowTextView.setTimeCountDowListener(null);
        timeCountDowTextView.setTimeCountDowListener(new TimeCountDowTextView.TimeCountDowListener() {
            @Override
            public void onFinish() {
                closePkResultPager();
            }
        });
    }

    /**
     * 关闭页面释放资源
     */
    public void closePkResultPager() {
        lottieEffectView.removeCallbacks(null);

        try {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    releaseSoundRes();
                    mTeamPkBll.closeCurrentPager();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseSoundRes() {

        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }

    }

    private class PkAnimListener extends AnimatorListenerAdapter {
        private int animType;
        private int pkResult;

        PkAnimListener(int animType, int pkResult) {
            this.animType = animType;
            this.pkResult = pkResult;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animType) {
                case ANIM_TYPE_PRIASE:
                    removeLottieView();
                    break;
                case ANIM_TYPE_PK_ADVERSARY:
                    break;
                case ANIM_TYPE_PK_REUSLT:

                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            turn2openBox();
                            closePkResultPager();
                        }
                    }, 500);
                    break;
                default:
                    break;
            }
            if (pkResult > 0) {
                StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
                if (studyReportAction != null) {
                    studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_PK_WIN, mView, false, false);
                }
            }
        }
    }

    private void turn2openBox() {
        if (mFinalPkResult != null && mFinalPkResult.getMyTeamResultInfo()
                != null && mFinalPkResult.getCompetitorResultInfo() != null) {
            logger.e("======>turn2openBox called");
            mTeamPkBll.showOpenBoxScene(mFinalPkResult.getMyTeamResultInfo().getEnergy()
                    >= mFinalPkResult.getCompetitorResultInfo().getEnergy());
        }
    }

    private void removeLottieView() {
        lottieEffectView.cancelAnimation();
        lottieEffectView.removeAllAnimatorListeners();
        lottieEffectView.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        logger.e("======> initData called");
    }

    public static Bitmap scaleBitmap(Bitmap input, int radius) {
        Bitmap result = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Rect src = new Rect(0, 0, input.getWidth(), input.getHeight());
        Rect dst = new Rect(0, 0, radius * 2, radius * 2);

        Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);
        canvas.clipPath(path);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(input, src, dst, paint);
        return result;
    }
}

