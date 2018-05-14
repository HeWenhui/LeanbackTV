package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkResultLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ContributionLayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SmoothAddNumTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenkun on 2018/4/12
 * 战队 pk 结果页
 */
public class TeamPkResultPager extends BasePager {
    private static final String TAG = "TeamPkResultPager";
    private LottieAnimationView lottieAnimationView;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/pkresult/";
    private final TeamPKBll mTeamPkBll;
    /**老师点赞动画*/
    private static final int ANIM_TYPE_PRIASE = 1;
    /**pk 对手动画*/
    private static final int ANIM_TYPE_PK_ADVERSARY = 2;
    /**pk 结果*/
    private static final int ANIM_TYPE_PK_REUSLT = 3;
    /**pk 结果 信息 根节点*/
    private RelativeLayout rlResultRootView;
    private ImageView ivMyteamState;
    private ImageView ivOtherTeamState;
    private ImageView ivMyTeamLogo;
    private ImageView ivOtherTeamLogo;
    private ImageView ivMyTeacherHead;
    private ImageView ivOtherTeacherHead;
    private TextView tvMyTeacherName;
    private TextView tvOtherTeacherName;
    private TextView tvMyTeamSlogan;
    private TextView tvOtherTeamSlogan;
    private SmoothAddNumTextView tvMyTeamEnergy;
    private TextView tvOtherTeamEnergy;
    private TextView tvAddEnergy;
    private TeamPkProgressBar tpbEnergyBar;
    private RecyclerView rclContributionRank;

    /**背景音效大小*/
    private static final float SOUND_VOLUME_BG = 0.3f;
    /**前景音效大小*/
    private static final float SOUND_VOLUME_FRONT = 0.6f;
    private TimeCountDowTextView timeCountDowTextView;
    /**pk 对手 展示时间*/
    private static final int TIME_DELAY_AUTO_CLOSE = 8;
    /** 每题pk 结果页显示时长*/
    private static final int CURRENT_PK_RESULT_AUTO_CLOSE_DRUATION = 10;
    private StudentPkResultEntity mFinalPkRsult;
    private PkResultAdapter pkResultAdapter;
    private List<TeamEnergyAndContributionStarEntity.ContributionStar> mContributions;
    private ContributionLayoutManager layoutmanager;
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


    public TeamPkResultPager(Context context, TeamPKBll pkBll) {
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_pkresult, null);
        lottieAnimationView = view.findViewById(R.id.lav_teampk_pkresult);
        rlResultRootView = view.findViewById(R.id.rl_teampk_pkresult_root);

        ivMyteamState = view.findViewById(R.id.iv_teampk_pkresult_myteam_state);
        ivOtherTeamState = view.findViewById(R.id.iv_teampk_pkresult_otherteam_state);
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
        timeCountDowTextView = view.findViewById(R.id.tv_teampk_pkresult_time_countdow);

        rclContributionRank = view.findViewById(R.id.rcl_teampk_pkresult_contribution_rank);
        return view;
    }

    private void startAddEnergyEffect(int increment) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_add_energy);
        animation.setFillAfter(true);
        tvAddEnergy.setVisibility(View.VISIBLE);
        tvAddEnergy.setText("+"+increment);
        tvAddEnergy.startAnimation(animation);
        tvMyTeamEnergy.smoothAddNum(increment);
    }


    private void initRecycleView() {
        mContributions = new ArrayList<TeamEnergyAndContributionStarEntity.ContributionStar>();
        layoutmanager = new ContributionLayoutManager(5);
        int itemWidth = rclContributionRank.getMeasuredWidth() / 5;
        layoutmanager.setItemWidth(itemWidth);
        Loger.e("TeamPkResultPager", "======>initRecycleView:" + itemWidth);
        rclContributionRank.setLayoutManager(layoutmanager);
        pkResultAdapter = new PkResultAdapter(mContributions,itemWidth);
        rclContributionRank.setAdapter(pkResultAdapter);
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
        Loger.e("PkResult", "======> ResultPager show finalPkResult" + data.getMyTeamResultInfo().getEnergy() + ":" + data.getCompetitorResultInfo().getEnergy());
        mFinalPkRsult = data;
        int pkResult = (int) (data.getMyTeamResultInfo().getEnergy() - data.getCompetitorResultInfo().getEnergy());
        if (pkResult == 0) {
            showDrawAnim();
            Loger.e("PkResult", "======> ResultPager show showDraw");
        } else if (pkResult > 0) {
             showWinAnim();
            Loger.e("PkResult", "======> ResultPager show showWin");
        } else {
             showLoseAnim();
            Loger.e("PkResult", "======> ResultPager show showLose");
        }
    }

    /**
     * 展示当前答题的结果
     *
     * @param data
     */
    public void showCurrentResult(final TeamEnergyAndContributionStarEntity data) {

        if (data != null) {
            rlResultRootView.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.GONE);
            //显示贡献之星
            if (data.getContributionStarList() != null) {
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initRecycleView();
                        mContributions.clear();
                        mContributions.addAll(data.getContributionStarList());
                        pkResultAdapter.notifyDataSetChanged();
                    }
                }, 200);
            }
            //进度条动画
            try {
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressBar(data);
                    }
                }, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 初始战队信息
            long myTeamTotalEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
            long otherTeamTotalEnergy = data.getCompetitorEngerInfo().getTotalEnergy();
            if (myTeamTotalEnergy == otherTeamTotalEnergy) {
                ivMyteamState.setVisibility(View.GONE);
                ivOtherTeamState.setVisibility(View.GONE);
            } else {
                ivMyteamState.setVisibility(View.VISIBLE);
                ivOtherTeamState.setVisibility(View.VISIBLE);
                if (myTeamTotalEnergy > otherTeamTotalEnergy) {
                    ivMyteamState.setImageResource(R.drawable.livevideo_list_lead_img_disable);
                    ivOtherTeamState.setImageResource(R.drawable.livevideo_list_catchup_img_disable);
                } else if (otherTeamTotalEnergy > myTeamTotalEnergy) {
                    ivOtherTeamState.setImageResource(R.drawable.livevideo_list_lead_img_disable);
                    ivMyteamState.setImageResource(R.drawable.livevideo_list_catchup_img_disable);
                } else {
                    ivOtherTeamState.setImageResource(R.drawable.livevideo_alertview_pingshou_img_disable);
                    ivMyteamState.setImageResource(R.drawable.livevideo_alertview_pingshou_img_disable);
                }
            }
            ImageLoader.with(ivMyTeacherHead.getContext()).load(data.getMyTeamEngerInfo().getTeacherImg()).asBitmap(new SingleConfig.BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                    Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap.getHeight()) / 2);
                    ivMyTeacherHead.setImageBitmap(resultBitmap);
                }

                @Override
                public void onFail() {

                }
            });

            ImageLoader.with(ivOtherTeacherHead.getContext()).load(data.getCompetitorEngerInfo().getTeacherImg()).asBitmap(new SingleConfig.BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                    Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap.getHeight()) / 2);
                    ivOtherTeacherHead.setImageBitmap(resultBitmap);
                }

                @Override
                public void onFail() {

                }
            });
            ImageLoader.with(ivMyTeamLogo.getContext()).load(data.getMyTeamEngerInfo().getImg()).into(ivMyTeamLogo);
            ImageLoader.with(ivOtherTeamLogo.getContext()).load(data.getCompetitorEngerInfo().getImg()).into(ivOtherTeamLogo);
            tvMyTeacherName.setText(data.getMyTeamEngerInfo().getTeacherName());
            tvOtherTeacherName.setText(data.getCompetitorEngerInfo().getTeacherName());
            tvMyTeamSlogan.setText(data.getMyTeamEngerInfo().getSlogon());
            tvOtherTeamSlogan.setText(data.getCompetitorEngerInfo().getSlogon());
            startTimeCountDow(CURRENT_PK_RESULT_AUTO_CLOSE_DRUATION);
            timeCountDowTextView.setVisibility(View.INVISIBLE);

            // 更新左侧pk 状态栏
            if(mTeamPkBll != null){
                mTeamPkBll.updatePkStateLayout();
            }
        }
    }

    private void updateProgressBar(final TeamEnergyAndContributionStarEntity data) {
        //显示之前的pk 进度
        final long myTeamOldEnergy = data.getMyTeamEngerInfo().getTotalEnergy() - data.getMyTeamEngerInfo().getAddEnergy();
        long otherTeamOldEnergy = data.getCompetitorEngerInfo().getTotalEnergy() - data.getCompetitorEngerInfo().getAddEnergy();
        Loger.e("TeamPkResultPager", "========>updateProgressBar:" + myTeamOldEnergy + ":" + otherTeamOldEnergy);
        float ratio = 0f;
        if ((myTeamOldEnergy + otherTeamOldEnergy) > 0) {
            ratio = myTeamOldEnergy / (float) (myTeamOldEnergy + otherTeamOldEnergy);
        }
        if (ratio == 0) {
            ratio = 0.5f;
        }
        int progress = (int) (ratio * tpbEnergyBar.getMaxProgress() + 0.5);
        tpbEnergyBar.setProgress(progress);
        tvMyTeamEnergy.setText(myTeamOldEnergy + "");
        tvOtherTeamEnergy.setText(otherTeamOldEnergy + "");
        Loger.e("TeamPkResultPager", "========>updateProgressBar22222:" + progress);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNewProgress(data, myTeamOldEnergy);
            }
        }, 2000);
    }

    private void showNewProgress(TeamEnergyAndContributionStarEntity data, long myTeamOldEnergy) {
        long myTeamTotalEnergy = data.getMyTeamEngerInfo().getTotalEnergy();
        long otherTeamEnergy = data.getCompetitorEngerInfo().getTotalEnergy();

        float newRatio = 0;
        if ((myTeamTotalEnergy + otherTeamEnergy) > 0) {
            newRatio = myTeamTotalEnergy / (float) (myTeamTotalEnergy + otherTeamEnergy);
        }
        if (newRatio == 0) {
            newRatio = 0.5f;
        }
        int currentProgress = (int) (newRatio * tpbEnergyBar.getMaxProgress() + 0.5);
        int addProgress = currentProgress - tpbEnergyBar.getProgress();
        tpbEnergyBar.smoothAddProgress(addProgress);
        tvMyTeamEnergy.setText(myTeamOldEnergy + "");
        tvOtherTeamEnergy.setText(otherTeamEnergy + "");
        int addEnergy = (int) data.getMyTeamEngerInfo().getAddEnergy();
        Loger.e("TeamPkResult", "=======>showNewProgress: addEnergy=" + addEnergy);
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
            ImageLoader.with(ivHead.getContext()).load(data.getAvaterPath()).asBitmap(new SingleConfig.BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                    Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap.getHeight()) / 2);
                    ivHead.setImageBitmap(resultBitmap);
                }

                @Override
                public void onFail() {
                }
            });
            tvName.setText(data.getRealname());
            tvEnergy.setText("+" + data.getEnergy());
        }
    }

    static class PkResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<TeamEnergyAndContributionStarEntity.ContributionStar> mData;
        int itemWidth;
        PkResultAdapter(List<TeamEnergyAndContributionStarEntity.ContributionStar> data,int itemWidth) {
            mData = data;
            this.itemWidth = itemWidth;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ItemHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_teampk_contribution, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                 ViewGroup.LayoutParams layoutParams =holder.itemView.getLayoutParams();
                 layoutParams.width = itemWidth;
                 holder.itemView.setLayoutParams(layoutParams);
                ((ItemHolder) holder).bindData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }





    private void playMusic(int resId,float volume,boolean loop){
        if(soundPoolHelper == null){
            soundPoolHelper = new SoundPoolHelper(mContext,2, AudioManager.STREAM_MUSIC);
        }
        soundPoolHelper.playMusic(resId,volume,loop);
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
    private void pauseMusic(){
        Loger.e("TeamPkResultPager","======>pauseMusic called");
        if(soundPoolHelper != null){
            for (int i = 0; i < soundResArray.length; i++) {
                soundPoolHelper.setVolume(soundResArray[i],0);
            }
        }

    }


    /**
     * 恢复音乐播放
     *  注释  将音量恢复为暂停之前的状态
     */
    private void resumeMusic(){
        Loger.e("TeamPkResultPager","======>resumeMusic called");
        if(soundPoolHelper != null){
            for (int i = 0; i < soundResArray.length; i++) {
                if(soundResArray[i] == R.raw.war_bg){
                    soundPoolHelper.setVolume(soundResArray[i],SOUND_VOLUME_BG);
                }else {
                    soundPoolHelper.setVolume(soundResArray[i],SOUND_VOLUME_FRONT);
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
        // 播放背景音乐
        //playMusic(SOUND_TYPE_BG, R.raw.war_bg, SOUND_VOLUME_BG, true);
         playMusic( R.raw.war_bg, SOUND_VOLUME_BG, true);

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "pk_adversary/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "pk_adversary/data.json";
        String[] targetFileNames = {"img_3.png"
                , "img_4.png", "img_5.png", "img_2.png", "img_6.png", "img_7.png", "img_9.png", "img_10.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_3.png",30);
        lottieEffectInfo.setTextSize("img_9.png",30);
        lottieEffectInfo.setTextSize("img_4.png",40);
        lottieEffectInfo.setTextSize("img_10.png",40);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);

        String myTeacherName = data.getSelf().getTeacherName();
        if (myTeacherName.length() > 6) {
            myTeacherName = myTeacherName.substring(0, 6);
        }

        String otherTeacherName = data.getOpponent().getTeacherName();
        if (otherTeacherName.length() > 6) {
            otherTeacherName = myTeacherName.substring(0, 6);
        }

        lottieEffectInfo.addTeacherName("img_3.png", myTeacherName);
        lottieEffectInfo.addTeacherName("img_9.png", otherTeacherName);

        lottieEffectInfo.addSlogan("img_4.png", data.getSelf().getSlogon());
        lottieEffectInfo.addSlogan("img_10.png", data.getOpponent().getSlogon());

        lottieEffectInfo.addLogo("img_5.png", data.getSelf().getImg());
        lottieEffectInfo.addLogo("img_6.png", data.getOpponent().getImg());

        lottieEffectInfo.addTeacherHead("img_2.png", data.getSelf().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_7.png", data.getOpponent().getTeacherImg());

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

        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startTimeCountDow(TIME_DELAY_AUTO_CLOSE);
            }
        });

        lottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean soundPlayed;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!soundPlayed && animation.getAnimatedFraction() > 0.11f) {
                    soundPlayed = true;
                    playMusic(R.raw.pk_adversary, SOUND_VOLUME_FRONT, false);
                }
            }
        });

    }


    /**
     * 显示平局 lottie anim
     */
    private void showDrawAnim() {
        // 播放胜利音效
        playMusic(R.raw.win, SOUND_VOLUME_FRONT, false);
        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "draw/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "draw/data.json";
        String[] targetFileNames = {"img_15.png"
                , "img_16.png", "img_17.png", "img_14.png", "img_9.png", "img_10.png", "img_11.png", "img_7.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_15.png",26);
        lottieEffectInfo.setTextSize("img_10.png",26);

        lottieEffectInfo.setTextSize("img_16.png",32);
        lottieEffectInfo.setTextSize("img_11.png",32);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_15.png", mFinalPkRsult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_10.png", mFinalPkRsult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_16.png", mFinalPkRsult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_11.png", mFinalPkRsult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_17.png", mFinalPkRsult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_7.png", mFinalPkRsult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_14.png", mFinalPkRsult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_9.png", mFinalPkRsult.getCompetitorResultInfo().getTeacherImg());

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
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT));
    }

    /**
     * 展示pk 失败lottie 动画
     */
    private void showLoseAnim() {

        // 播放胜利音效
        playMusic(R.raw.lose, SOUND_VOLUME_FRONT, false);

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "lose/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "lose/data.json";
        String[] targetFileNames = {"img_14.png"
                , "img_15.png", "img_16.png", "img_13.png", "img_9.png", "img_10.png", "img_6.png", "img_8.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);
        lottieEffectInfo.setTextSize("img_14.png",26);
        lottieEffectInfo.setTextSize("img_9.png",26);

        lottieEffectInfo.setTextSize("img_15.png",35);
        lottieEffectInfo.setTextSize("img_10.png",35);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_14.png", mFinalPkRsult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_9.png", mFinalPkRsult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_15.png", mFinalPkRsult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_10.png", mFinalPkRsult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_16.png", mFinalPkRsult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_6.png", mFinalPkRsult.getCompetitorResultInfo().getImg());

        lottieEffectInfo.addTeacherHead("img_13.png", mFinalPkRsult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_8.png", mFinalPkRsult.getCompetitorResultInfo().getTeacherImg());

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
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT));
    }

    public void showWinAnim() {

        // 播放胜利音效
        playMusic(R.raw.win, SOUND_VOLUME_FRONT, false);

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "win/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "win/data.json";
        String[] targetFileNames = {"img_13.png"
                , "img_9.png", "img_14.png", "img_10.png", "img_15.png", "img_6.png", "img_12.png", "img_8.png"};
        final TeamPkResultLottieEffectInfo lottieEffectInfo =
                new TeamPkResultLottieEffectInfo(lottieResPath, lottieJsonPath);
        lottieEffectInfo.setTargetFileFilter(targetFileNames);

        lottieEffectInfo.setTextSize("img_13.png",26);
        lottieEffectInfo.setTextSize("img_9.png",21);


        lottieEffectInfo.setTextSize("img_14.png",35);
        lottieEffectInfo.setTextSize("img_10.png",27);

        int color = Color.parseColor("#73510A");
        lottieEffectInfo.setTextColor(color);
        lottieEffectInfo.addTeacherName("img_13.png", mFinalPkRsult.getMyTeamResultInfo().getTeacherName());
        lottieEffectInfo.addTeacherName("img_9.png", mFinalPkRsult.getCompetitorResultInfo().getTeacherName());

        lottieEffectInfo.addSlogan("img_14.png", mFinalPkRsult.getMyTeamResultInfo().getSlogon());
        lottieEffectInfo.addSlogan("img_10.png", mFinalPkRsult.getCompetitorResultInfo().getSlogon());

        lottieEffectInfo.addLogo("img_15.png", mFinalPkRsult.getMyTeamResultInfo().getImg());
        lottieEffectInfo.addLogo("img_6.png", mFinalPkRsult.getCompetitorResultInfo().getImg());

        Log.e("TeamPkResultPager","======>showWin myTeamLogoUrl:"+mFinalPkRsult.getMyTeamResultInfo().getImg());
        Log.e("TeamPkResultPager","======>showWin otherTeamLogoUrl:"+mFinalPkRsult.getCompetitorResultInfo().getImg());


        lottieEffectInfo.addTeacherHead("img_12.png", mFinalPkRsult.getMyTeamResultInfo().getTeacherImg());
        lottieEffectInfo.addTeacherHead("img_8.png", mFinalPkRsult.getCompetitorResultInfo().getTeacherImg());

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
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PK_REUSLT));
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

      if(soundPoolHelper != null){
          soundPoolHelper.release();
      }

    }

    private class PkAnimListener implements Animator.AnimatorListener {
        private int animType;

        PkAnimListener(int animType) {
            this.animType = animType;
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
                            //releaseSoundRes();
                            turn2openBox();
                            closePkResultPager();
                        }
                    },500);
                    break;
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
        if (mFinalPkRsult != null && mFinalPkRsult.getMyTeamResultInfo()
                != null && mFinalPkRsult.getCompetitorResultInfo() != null) {
            Loger.e("teamPkResultPager", "======>turn2openBox called");
            mTeamPkBll.showOpenBoxScene(mFinalPkRsult.getMyTeamResultInfo().getEnergy()
                    >= mFinalPkRsult.getCompetitorResultInfo().getEnergy());
        }
    }

    private void removeLottieView() {
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Loger.e(TAG, "======> initData called");
    }
}

