package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.TeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkResultLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCutImage;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ContributionLayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SmoothAddNumTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

/**
 * 战队 pk 结果页
 *
 * @author chekun
 * created  at 2018/4/17 16:15
 */
public class TeamPkResultPager extends TeamPkBasePager {
    private static final String TAG = "TeamPkResultPager";
    private LottieAnimationView lottieAnimationView;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/pkresult/";
    private final TeamPkBll mTeamPkBll;
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
     * pk 结果 信息 根节点
     */
    private RelativeLayout rlResultRootView;
    //private ImageView ivMyteamState;
    //private ImageView ivOtherTeamState;
    private ImageView ivMyTeamLogo;
    private ImageView ivOtherTeamLogo;
    private ImageView ivMyTeacherHead;
    private ImageView ivOtherTeacherHead;
    private TextView tvMyTeacherName;
    private TextView tvOtherTeacherName;
    private TextView tvMyTeamSlogan;
    private TextView tvOtherTeamSlogan;
    private SmoothAddNumTextView tvMyTeamEnergy;
    private SmoothAddNumTextView tvOtherTeamEnergy;
    private TextView tvAddEnergy;
    private TeamPkProgressBar tpbEnergyBar;

    /**
     * 背景音效大小
     */
    private static final float SOUND_VOLUME_BG = 0.3f;
    /**
     * 前景音效大小
     */
    private static final float SOUND_VOLUME_FRONT = 0.6f;
    private TimeCountDowTextView timeCountDowTextView;
    /**
     * pk 对手 展示时间
     */
    private static final int TIME_DELAY_AUTO_CLOSE = 10;
    /**
     * 每题pk 结果页显示时长
     */
    private static final int CURRENT_PK_RESULT_AUTO_CLOSE_DRUATION = 10;
    private StudentPkResultEntity mFinalPkResult;
    private ContributionLayoutManager mLayoutManager;
    private SoundPoolHelper soundPoolHelper;

    /**
     * pk 结果页 所用到的 音效资源id
     */
    int[] soundResArray = {
            R.raw.war_bg,
            R.raw.pk_adversary,
            R.raw.lose,
            R.raw.win
    };
    private RelativeLayout rlLottieRootView;
    private TeamPkProgressBar tpbFinalProgress;
    private RelativeLayout rlFinalPbBarContainer;

    /**
     * 底部贡献之星 右边距
     */
    private static final float CONTRIBUTION_VIEW_RIGHTMARGIN = 0.025f;

    /**
     * 半身直播 贡献之星 右边距
     */
    private static final float CONTRIBUTION_VIEW_RIGHTMARGIN_HALFBODY = 0.15f;
    private RelativeLayout rlCloseBtnCotainer;
    private ImageView ivCloseBtn;
    private ImageView ivPkState;


    public TeamPkResultPager(Context context, TeamPkBll pkBll) {
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_pkresult, null);
        rlLottieRootView = view.findViewById(R.id.rl_teampk_pk_result_lottie_root);
        lottieAnimationView = view.findViewById(R.id.lav_teampk_pkresult);
        lottieAnimationView.useHardwareAcceleration(true);
        tpbFinalProgress = view.findViewById(R.id.tpb_teampk_pkresult_pbbar_final);
        tpbFinalProgress.setMaxProgress(100);
        rlFinalPbBarContainer = view.findViewById(R.id.rl_teampk_pkresult_final_pbbar_container);
        ivPkState = view.findViewById(R.id.iv_teampk_pk_state);

        rlResultRootView = view.findViewById(R.id.rl_teampk_pkresult_root);
        ivMyTeamLogo = view.findViewById(R.id.iv_teampk_pkresult_myteam_logo);
        ivOtherTeamLogo = view.findViewById(R.id.iv_teampk_pkresult_otherteam_logo);

        ivMyTeacherHead = view.findViewById(R.id.iv_teampk_pkresult_myteam_teacher_head);
        ivOtherTeacherHead = view.findViewById(R.id.iv_teampk_pkresult_otherteam_teacher_head);

        tvMyTeacherName = view.findViewById(R.id.tv_teampk_pkresult_myteacher_name);
        tvOtherTeacherName = view.findViewById(R.id.tv_teampk_pkresult_otherteacher_name);

        tvMyTeamSlogan = view.findViewById(R.id.iv_teampk_pkresult_myteam_slogan);
        tvOtherTeamSlogan = view.findViewById(R.id.iv_teampk_pkresult_otherteam_slogan);

        tvMyTeamEnergy = view.findViewById(R.id.tv_teampk_myteam_energy);
        tvOtherTeamEnergy = view.findViewById(R.id.tv_teampk_otherteam_energy);
        tvAddEnergy = view.findViewById(R.id.tv_teampk_myteam_add_energy);

        tpbEnergyBar = view.findViewById(R.id.tpb_teampk_pkresult_pbbar);
        tpbEnergyBar.setMaxProgress(100);

        rlCloseBtnCotainer = view.findViewById(R.id.rl_teampk_close_btn_container);
        ivCloseBtn = view.findViewById(R.id.iv_teampk_close_btn);
        ivCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePkResultPager();
            }
        });
        timeCountDowTextView = view.findViewById(R.id.tv_teampk_pkresult_time_countdow);
        return view;
    }

    private void startAddEnergyEffect(int increment) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_add_energy);
        animation.setFillAfter(true);
        tvAddEnergy.setVisibility(View.VISIBLE);
        tvAddEnergy.setText("+" + increment);
        tvAddEnergy.startAnimation(animation);
        tvMyTeamEnergy.smoothAddNum(increment);
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
      /*  Loger.e("PkResult", "======> ResultPager show finalPkResult" + data.getMyTeamResultInfo().getEnergy() + ":" +
                data.getCompetitorResultInfo().getEnergy());*/
        mFinalPkResult = data;
        // 显示最终pk 进度值
        rlLottieRootView.setVisibility(View.VISIBLE);
        rlFinalPbBarContainer.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlFinalPbBarContainer
                .getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int realY = Math.min(point.x, point.y);
        layoutParams.topMargin = (int) (realY * 0.83);
        rlFinalPbBarContainer.setLayoutParams(layoutParams);

        int myTeamEnergy = (int) data.getMyTeamResultInfo().getEnergy();
        int otherTeamEnergy = (int) data.getCompetitorResultInfo().getEnergy();
        float ratio;
        if (myTeamEnergy + otherTeamEnergy > 0) {
            ratio = myTeamEnergy / (float) (myTeamEnergy + otherTeamEnergy);
        } else {
            ratio = 0.5f;
        }
        tpbFinalProgress.setProgress((int) (ratio * tpbFinalProgress.getMaxProgress()));
        SmoothAddNumTextView tvMyTeamFinalEngergy = rlLottieRootView.findViewById(R.id
                .tv_teampk_pkresult_myteam_final_anergy);
        tvMyTeamFinalEngergy.setText(myTeamEnergy + "");
        SmoothAddNumTextView tvOtherTeamFinalEngergy = rlLottieRootView.findViewById(R.id
                .tv_teampk_pkresult_otherteam_final_anergy);
        tvOtherTeamFinalEngergy.setText(otherTeamEnergy + "");


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
        if (data != null) {
            if (isForceSubmit()) {
                showResultWithOutEffect(data);
            } else {
                showResultWithEffect(data);
            }
        }
    }


    /**
     * 展示每题pk 结果 带动效
     * @param data
     */
    private void showResultWithEffect(final TeamEnergyAndContributionStarEntity data) {
        //延迟 播放音效  匹配动画展示
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                playMusic(R.raw.pk_answer_result_bg, SOUND_VOLUME_BG, false);
            }
        }, 500);
        rlResultRootView.setVisibility(View.VISIBLE);
        rlLottieRootView.setVisibility(View.GONE);

        //进度条动画
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateProgressBar(data);
                }
            }, 200);
        // 初始战队信息
        long myTeamTotalEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
        long otherTeamTotalEnergy = data.getCompetitorEngerInfo().getTotalEnergy();
        if (myTeamTotalEnergy > otherTeamTotalEnergy) {
            // 最近一次pk 状态为落后
            if (mTeamPkBll != null && mTeamPkBll.getLatesPkState() == TeamPkConfig.PK_STATE_BEHIND) {
                ivPkState.setImageResource(R.drawable.live_teampk_state_inverse);
            } else {
                ivPkState.setImageResource(R.drawable.live_teampk_state_lead);
            }
        } else if (otherTeamTotalEnergy > myTeamTotalEnergy) {
            ivPkState.setImageResource(R.drawable.live_teampk_state_follow);
        } else if (myTeamTotalEnergy == otherTeamTotalEnergy) {
            ivPkState.setImageResource(R.drawable.live_teampk_state_draw);
        }

        ImageLoader.with(ContextManager.getContext()).load(data.getMyTeamEngerInfo().getTeacherImg()).asBitmap
                (new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                        Bitmap resultBitmap = LiveCutImage.scaleBitmap(headBitmap, Math.min(headBitmap
                                .getWidth(), headBitmap
                                .getHeight()) / 2);
                        ivMyTeacherHead.setImageBitmap(resultBitmap);
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
                        Bitmap resultBitmap = LiveCutImage.scaleBitmap(headBitmap, Math.min(headBitmap
                                .getWidth(), headBitmap
                                .getHeight()) / 2);
                        ivOtherTeacherHead.setImageBitmap(resultBitmap);
                    }
                    @Override
                    public void onFail() {

                    }
                });
        ImageLoader.with(ContextManager.getContext()).load(data.getMyTeamEngerInfo().getImg()).into
                (ivMyTeamLogo);
        ImageLoader.with(ContextManager.getContext()).load(data.getCompetitorEngerInfo().getImg()).into
                (ivOtherTeamLogo);
        tvMyTeacherName.setText(data.getMyTeamEngerInfo().getTeacherName());
        tvOtherTeacherName.setText(data.getCompetitorEngerInfo().getTeacherName());
        tvMyTeamSlogan.setText(data.getMyTeamEngerInfo().getSlogon());
        tvOtherTeamSlogan.setText(data.getCompetitorEngerInfo().getSlogon());
        // startTimeCountDow(CURRENT_PK_RESULT_AUTO_CLOSE_DRUATION);
        timeCountDowTextView.setVisibility(View.INVISIBLE);
        ivCloseBtn.setVisibility(View.INVISIBLE);
        // 更新左侧pk 状态栏
        if (mTeamPkBll != null) {
            mTeamPkBll.updatePkStateLayout(true);
        }
        turn2ContributionPage(data,3000);
    }

    /**
     * 展示每题 pk 结果没动画
     * @param data
     */
    private void showResultWithOutEffect(TeamEnergyAndContributionStarEntity data) {
        rlResultRootView.setVisibility(View.VISIBLE);
        rlLottieRootView.setVisibility(View.GONE);
        //显示之前的pk 进度
        final long myTeamEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
        long otherTeamEnergy = data.getCompetitorEngerInfo().getTotalEnergy();
        float ratio;
        if ((myTeamEnergy + otherTeamEnergy) > 0) {
            ratio = myTeamEnergy / (float) (myTeamEnergy + otherTeamEnergy);
        } else {
            ratio = 0.5f;
        }
        int progress = (int) (ratio * tpbEnergyBar.getMaxProgress() + 0.5);
        tpbEnergyBar.setProgress(progress);
        tvMyTeamEnergy.setText(myTeamEnergy + "");
        tvOtherTeamEnergy.setText(otherTeamEnergy + "");

        // 初始战队信息
        long myTeamTotalEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
        long otherTeamTotalEnergy = data.getCompetitorEngerInfo().getTotalEnergy();
        if (myTeamTotalEnergy > otherTeamTotalEnergy) {
            // 最近一次pk 状态为落后
            if (mTeamPkBll != null && mTeamPkBll.getLatesPkState() == TeamPkConfig.PK_STATE_BEHIND) {
                ivPkState.setImageResource(R.drawable.live_teampk_state_inverse);
            } else {
                ivPkState.setImageResource(R.drawable.live_teampk_state_lead);
            }
        } else if (otherTeamTotalEnergy > myTeamTotalEnergy) {
            ivPkState.setImageResource(R.drawable.live_teampk_state_follow);
        } else if (myTeamTotalEnergy == otherTeamTotalEnergy) {
            ivPkState.setImageResource(R.drawable.live_teampk_state_draw);
        }

        ImageLoader.with(ContextManager.getContext()).load(data.getMyTeamEngerInfo().getTeacherImg()).asBitmap
                (new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                        Bitmap resultBitmap = LiveCutImage.scaleBitmap(headBitmap, Math.min(headBitmap
                                .getWidth(), headBitmap
                                .getHeight()) / 2);
                        ivMyTeacherHead.setImageBitmap(resultBitmap);
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
                        Bitmap resultBitmap = LiveCutImage.scaleBitmap(headBitmap, Math.min(headBitmap
                                .getWidth(), headBitmap
                                .getHeight()) / 2);
                        ivOtherTeacherHead.setImageBitmap(resultBitmap);
                    }

                    @Override
                    public void onFail() {

                    }
                });
        ImageLoader.with(ContextManager.getContext()).load(data.getMyTeamEngerInfo().getImg()).into
                (ivMyTeamLogo);
        ImageLoader.with(ContextManager.getContext()).load(data.getCompetitorEngerInfo().getImg()).into
                (ivOtherTeamLogo);
        tvMyTeacherName.setText(data.getMyTeamEngerInfo().getTeacherName());
        tvOtherTeacherName.setText(data.getCompetitorEngerInfo().getTeacherName());
        tvMyTeamSlogan.setText(data.getMyTeamEngerInfo().getSlogon());
        tvOtherTeamSlogan.setText(data.getCompetitorEngerInfo().getSlogon());
        timeCountDowTextView.setVisibility(View.INVISIBLE);
        ivCloseBtn.setVisibility(View.INVISIBLE);
        // 更新左侧pk 状态栏
        if (mTeamPkBll != null) {
            mTeamPkBll.updatePkStateLayout(true);
        }
        turn2ContributionPage(data,3000);
    }


    private void updateProgressBar(final TeamEnergyAndContributionStarEntity data) {
        //显示之前的pk 进度
        final long myTeamOldEnergy = data.getMyTeamEngerInfo().getTotalEnergy() - data.getMyTeamEngerInfo()
                .getAddEnergy();
        long otherTeamOldEnergy = data.getCompetitorEngerInfo().getTotalEnergy() - data.getCompetitorEngerInfo()
                .getAddEnergy();
        logger.e("========>updateProgressBar:" + myTeamOldEnergy + ":" + otherTeamOldEnergy);
        float ratio;
        if ((myTeamOldEnergy + otherTeamOldEnergy) > 0) {
            ratio = myTeamOldEnergy / (float) (myTeamOldEnergy + otherTeamOldEnergy);
        } else {
            ratio = 0.5f;
        }
        int progress = (int) (ratio * tpbEnergyBar.getMaxProgress() + 0.5);
        tpbEnergyBar.setProgress(progress);
        tvMyTeamEnergy.setText(myTeamOldEnergy + "");
        tvOtherTeamEnergy.setText(otherTeamOldEnergy + "");
        logger.e("========>updateProgressBar22222:" + progress);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNewProgress(data, myTeamOldEnergy);
            }
        }, 500);
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
        int currentProgress = (int) (newRatio * tpbEnergyBar.getMaxProgress() + 0.5);
        int addProgress = currentProgress - tpbEnergyBar.getProgress();
        if (addProgress > 0) {
            tpbEnergyBar.smoothAddProgress(addProgress);
        } else {
            tpbEnergyBar.setProgress(currentProgress);
        }
        tvMyTeamEnergy.setText(myTeamOldEnergy + "");
        tvOtherTeamEnergy.setText(otherTeamEnergy + "");
        int addEnergy = (int) data.getMyTeamEngerInfo().getAddEnergy();
        startAddEnergyEffect(addEnergy);
    }


    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        TextView tvName;
        TextView tvEnergy;

        public ItemHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_teampk_pkresult_student_head);
            tvName = itemView.findViewById(R.id.tv_teampk_pkresult_contribution_name);
            tvEnergy = itemView.findViewById(R.id.tv_teampk_student_add_energy);
        }

        public void bindData(TeamEnergyAndContributionStarEntity.ContributionStar data) {
            ImageLoader.with(ContextManager.getContext()).load(data.getAvaterPath()).asBitmap(new SingleConfig
                    .BitmapListener
                    () {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap headBitmap = null;
                    if (drawable instanceof BitmapDrawable) {
                        headBitmap = ((BitmapDrawable) drawable).getBitmap();
                    } else if (drawable instanceof GifDrawable) {
                        headBitmap = ((GifDrawable) drawable).getFirstFrame();
                    }
                    if (headBitmap != null) {
                        Bitmap resultBitmap = LiveCutImage.scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(),
                                headBitmap
                                .getHeight()) / 2);
                        ivHead.setImageBitmap(resultBitmap);
                    }
                }

                @Override
                public void onFail() {
                }
            });
            tvName.setText(data.getRealname());
            tvEnergy.setText("+" + data.getEnergy());
        }
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
        releaseSoundRes();
    }

    /**
     * 老师昵称最大字符数
     */
    private static final int TEACHER_NAME_MAXLEN = 6;
    /**
     * pk 对手音效进入时间点
     */
    private static final float FRACTION_MUSIC_IN = 0.015f;

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

        rlLottieRootView.setVisibility(View.VISIBLE);
        // 播放背景音乐
        playMusic(R.raw.war_bg, SOUND_VOLUME_BG, true);

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "pk_adversary/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "pk_adversary/data.json";
        String[] targetFileNames = {"img_6.png", "img_13.png", "img_14.png", "img_15.png",
                "img_5.png", "img_19.png", "img_20.png", "img_21.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_14.png", 31);
        lottieEffectInfo.setTextSize("img_20.png", 31);
        lottieEffectInfo.setTextSize("img_15.png", 32);
        lottieEffectInfo.setTextSize("img_21.png", 32);

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

        lottieEffectInfo.addTeacherName("img_14.png", myTeacherName);
        lottieEffectInfo.addTeacherName("img_20.png", otherTeacherName);

        lottieEffectInfo.addSlogan("img_15.png", data.getSelf().getSlogon());
        lottieEffectInfo.addSlogan("img_21.png", data.getOpponent().getSlogon());

        lottieEffectInfo.addLogo("img_6.png", data.getSelf().getImg());

        lottieEffectInfo.addLogo("img_5.png", data.getOpponent().getImg());

        lottieEffectInfo.addTeacherHead("img_13.png", data.getSelf().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_19.png", data.getOpponent().getTeacherImg());

        try {
            lottieAnimationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieAnimationView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieAnimationView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startTimeCountDow(TIME_DELAY_AUTO_CLOSE);

        lottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean soundPlayed;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!soundPlayed && animation.getAnimatedFraction() > FRACTION_MUSIC_IN) {
                    soundPlayed = true;
                    playMusic(R.raw.pk_adversary, SOUND_VOLUME_FRONT, false);
                }
            }
        });
    }


    /**
     * 显示平局 lottie anim
     */
    private void showDrawAnim(int pkResult) {
        // 播放胜利音效
        playMusic(R.raw.win, SOUND_VOLUME_FRONT, false);
        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "draw/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "draw/data.json";
        String[] targetFileNames = {"img_17.png", "img_14.png", "img_15.png", "img_16.png",
                                     "img_7.png", "img_9.png", "img_10.png", "img_11.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_15.png", 28);
        lottieEffectInfo.setTextSize("img_10.png", 28);

        lottieEffectInfo.setTextSize("img_16.png", 32);
        lottieEffectInfo.setTextSize("img_11.png", 32);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_15.png", mFinalPkResult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_10.png", mFinalPkResult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_16.png", mFinalPkResult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_11.png", mFinalPkResult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_17.png", mFinalPkResult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_7.png", mFinalPkResult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_14.png", mFinalPkResult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_9.png", mFinalPkResult.getCompetitorResultInfo().getTeacherImg());

        try {
            lottieAnimationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieAnimationView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieAnimationView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT, pkResult));
    }

    /**
     * 展示pk 失败lottie 动画
     */
    private void showLoseAnim(int pkResult) {

        // 播放胜利音效
        playMusic(R.raw.lose, SOUND_VOLUME_FRONT, false);

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "lose/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "lose/data.json";
        String[] targetFileNames = {"img_4.png", "img_1.png", "img_2.png", "img_3.png",
                                    "img_11.png", "img_13.png", "img_14.png", "img_15.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_2.png", 28);
        lottieEffectInfo.setTextSize("img_14.png", 28);

        lottieEffectInfo.setTextSize("img_3.png", 32);
        lottieEffectInfo.setTextSize("img_15.png", 32);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_2.png", mFinalPkResult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_14.png", mFinalPkResult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_3.png", mFinalPkResult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_15.png", mFinalPkResult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_4.png", mFinalPkResult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_11.png", mFinalPkResult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_1.png", mFinalPkResult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_13.png", mFinalPkResult.getCompetitorResultInfo().getTeacherImg());

        try {
            lottieAnimationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieAnimationView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieAnimationView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT, pkResult));
    }

    public void showWinAnim(int pkResult) {

        // 播放胜利音效
        playMusic(R.raw.win, SOUND_VOLUME_FRONT, false);

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "win/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "win/data.json";
        String[] targetFileNames = {"img_10.png", "img_7.png", "img_8.png", "img_9.png",
                                    "img_13.png", "img_15.png", "img_12.png", "img_16.png"
        };

        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);

        lottieEffectInfo.setTextSize("img_8.png", 28);
        lottieEffectInfo.setTextSize("img_12.png", 28);


        lottieEffectInfo.setTextSize("img_9.png", 32);
        lottieEffectInfo.setTextSize("img_16.png", 32);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_8.png", mFinalPkResult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_12.png", mFinalPkResult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_9.png", mFinalPkResult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_16.png", mFinalPkResult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_10.png", mFinalPkResult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_13.png", mFinalPkResult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_7.png", mFinalPkResult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_15.png", mFinalPkResult.getCompetitorResultInfo().getTeacherImg());

        try {
            lottieAnimationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
            lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return lottieEffectInfo.fetchBitmapFromAssets(lottieAnimationView,
                            lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                            lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                }
            });
            lottieAnimationView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT, pkResult));
    }

    private void startTimeCountDow(int duration) {
        rlCloseBtnCotainer.setVisibility(View.VISIBLE);
        timeCountDowTextView.setTimeDuration(duration);
        timeCountDowTextView.setTimeSuffix("s后关闭");
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

    private class PkAnimListener implements Animator.AnimatorListener {
        private int animType;
        private int pkResult;

        PkAnimListener(int animType, int pkResult) {
            this.animType = animType;
            this.pkResult = pkResult;
        }

        @Override
        public void onAnimationStart(Animator animation) {
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

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

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

    /**
     * 跳转到贡献之星页面
     *
     * @param data
     */
    private void turn2ContributionPage(final TeamEnergyAndContributionStarEntity data,long delay) {
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //closePkResultPager();
                mTeamPkBll.showContributionPage(data);
            }
        }, delay);
    }


    /**
     * 是否是强制提交
     *
     * @return
     */
    private boolean isForceSubmit() {
        return mTeamPkBll.getLatesH5CloseEvent() != null && mTeamPkBll.getLatesH5CloseEvent().isForceSubmit();
    }

    private void removeLottieView() {
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.setVisibility(View.GONE);
        rlLottieRootView.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        logger.e("======> initData called");
    }
}

