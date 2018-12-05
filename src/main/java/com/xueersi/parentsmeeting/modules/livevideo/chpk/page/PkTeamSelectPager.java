package com.xueersi.parentsmeeting.modules.livevideo.chpk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.business.ChinesePkBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamSelectLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.InputEffectTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkRecyclerView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 战队选择页面
 *
 * @author yuanwei
 *         <p>
 *         created  at 2018/11/14 11:31
 */
public class PkTeamSelectPager extends BasePager implements View.OnClickListener {
    private static final String TAG = "TeamPkTeamSelectPager";
    private ChinesePkBll mPKBll;
    private ImageView ivBg;
    private ImageView ivBgMask;

    /**
     * 分队仪式 特效展示 主 lottie view
     */
    private LottieAnimationView lavTeamSelectAnimView;

    /**
     * 跑马灯展示时间
     */
    private static final long MARQUEE_DURATION = 1800 * 2;

    /**
     * 最后一次 lottie 暂停后 复播位置
     */
    private final float LAST_ANIM_RESUME_FRACTION = 0.55f;

    String mTeamInfoStr;
    /**
     * 分队仪式 资源在asset中的相对路径
     */
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "chinesePk/team_select/";

    private TeamPkRecyclerView teamsRecyclerView;

    private TeamAdapter teamAdapter;

    private RelativeLayout rlTeamIntroduceRoot;

    private TeamAdapter teamMemberAdapter;


    /**
     * 跑马灯动画 传递到下一个item 是动画进度
     */
    private static final float MARQUEE_ANIM_DISPATCH_FRACTION = 0.3f;

    private static final int ADAPTER_TYPE_TEAM = 1;
    private static final int ADAPTER_TYPE_TEAM_MEMBER = 2;

    private TimeCountDowTextView tvTimeCountDown;
    /**
     * 背景音乐 音量
     */
    private static final float MUSIC_VOLUME_RATIO_BG = 0.3f;
    /**
     * 前景音效 音量
     */
    private static final float MUSIC_VOLUME_RATIO_FRONT = 0.6f;
    private TeamPkTeamInfoEntity mTeamInfo;
    private String mTeamName;
//    private List<AnimInfo> teamInfoAnimList;
    /**
     * 半透明遮罩背景 进入时间点
     */
    private static final float FRACTION_BG_MASK_FADE_IN = 0.4f;

    private List<TeamItemAnimInfo> teamItemAnimInfoList;
    /**
     * 跑马灯动画 item 索引
     */
    private int mTeamIndex;
    /**
     * 跑马灯 每个item 动画时间
     */
    private static final long MARQUEE_ANIM_DURATION = 700;
    private boolean bgHasFadeIn = false;
    /**
     * 跑马灯 战队logo 垂直方向 间隔
     */
    private int mTopGap = -1;
    private SoundPoolHelper soundPoolHelper;
    /**
     * 分队仪式中所用到的 音效 资源id
     */
    int[] soundResArray = {
            R.raw.war_bg,
            R.raw.marquee,
            R.raw.cheering,
            R.raw.input_effect,
            R.raw.welcome_to_teampk
    };


    public PkTeamSelectPager(Context context, ChinesePkBll pkBll) {
        super(context);
        mPKBll = pkBll;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_chpk_teamselect, null);
        ivBg = view.findViewById(R.id.iv_teampk_team_select_bg);
        ivBgMask = view.findViewById(R.id.iv_teampk_bgmask);
        lavTeamSelectAnimView = view.findViewById(R.id.lav_teampk_team_select);
        rlTeamIntroduceRoot = view.findViewById(R.id.rl_teampk_teaminfo_root);
        tvTimeCountDown = view.findViewById(R.id.tv_teampk_team_select_timecoutdown);
        tvTimeCountDown.setTimeSuffix("秒后进入下一步");
        loadSoundRes();
        return view;
    }

    /**
     * 加载音效资源
     */
    private void loadSoundRes() {
        soundPoolHelper = new SoundPoolHelper(mContext, 5, AudioManager.STREAM_MUSIC);

    }

    private void playBgMusic() {
        soundPoolHelper.playMusic(R.raw.war_bg, MUSIC_VOLUME_RATIO_BG, true);
    }

    private void playWelcomeMusic() {
        soundPoolHelper.playMusic(R.raw.welcome_to_teampk, MUSIC_VOLUME_RATIO_FRONT, false);
    }

    /**
     * 播发跑马灯音效
     */
    private void playMarquee() {
        soundPoolHelper.playMusic(R.raw.marquee, MUSIC_VOLUME_RATIO_FRONT, true);
    }


    /**
     * 播放欢呼音效
     */
    private void playCheering() {
        soundPoolHelper.playMusic(R.raw.cheering, MUSIC_VOLUME_RATIO_FRONT, false);
    }

    /**
     * 播放打字音效
     */
    private void playInputEffect() {
        soundPoolHelper.playMusic(R.raw.input_effect, MUSIC_VOLUME_RATIO_FRONT, true);
    }

    /**
     * 暂停播放音效
     *
     * @param soundType
     */
    private void stopMusic(int soundType) {
        if (soundPoolHelper != null) {
            soundPoolHelper.stopMusic(soundType);
        }
    }


    /**
     * 暂停音效
     * 注 此处的暂停  只是将音量设置为0  （因为 动画和音效是 同步的）
     */
    private void pauseMusic() {

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
        if (soundPoolHelper == null) {
            return;
        }

        for (int i = 0; i < soundResArray.length; i++) {
            if (soundResArray[i] == R.raw.war_bg) {
                soundPoolHelper.setVolume(soundResArray[i], MUSIC_VOLUME_RATIO_BG);
            } else {
                soundPoolHelper.setVolume(soundResArray[i], MUSIC_VOLUME_RATIO_FRONT);
            }
        }
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
     * 展示分队仪式 lottie 动画
     */
    public void showTeamSelectedScene(boolean isHalfIn) {

        if (isHalfIn) {
            playBgMusic();
        }

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "team_selected/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "team_selected/data.json";
        final TeamSelectLottieEffectInfo effectInfo = new TeamSelectLottieEffectInfo(lottieResPath, lottieJsonPath, "img_0.png");
        effectInfo.setLogoUrl(mTeamInfo.getTeamInfo().getImg());
        lavTeamSelectAnimView.setVisibility(View.VISIBLE);
        lavTeamSelectAnimView.removeAllAnimatorListeners();
        lavTeamSelectAnimView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                playCheering();
            }
        });

        lavTeamSelectAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
        lavTeamSelectAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(lavTeamSelectAnimView, lottieImageAsset.getFileName(), lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });
        lavTeamSelectAnimView.playAnimation();

        lavTeamSelectAnimView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showTeamIntroduce(lavTeamSelectAnimView);
            }

        }, 4000);


    }

    /**
     * 结束分队仪式
     */
    private void finishTeamSelect() {
        ImageView ivClose = mView.findViewById(R.id.iv_teampk_finish_team_select);
        if (ivClose.getVisibility() != View.VISIBLE) {
            ivClose.setVisibility(View.VISIBLE);
            ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                    loadAnimation(mContext, R.anim.anim_livevido_teampk_click_btn);
            scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.19f));
            ivClose.startAnimation(scaleAnimation);
        }
        // 去除 选队场景
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeTeamSelectPager();
            }
        });
    }

    /**
     * 关闭 页面
     */
    public void closeTeamSelectPager() {
        releaseRes();
        mPKBll.closeCurrentPager();
    }

    private void showTeamIntroduce(LottieAnimationView bgAnimView) {
        // step 1  显示 背景黑色遮罩动画

        bgMaskFadeIn();

        // step 2 显示队伍介绍
        rlTeamIntroduceRoot.setVisibility(View.VISIBLE);

        //动态设置 战队信息介绍的 topMargin  多机型适配
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlTeamIntroduceRoot.getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int realY = Math.min(point.x, point.y);
        int topMargin = (int) (realY * 0.456f);
        layoutParams.topMargin = topMargin;
        rlTeamIntroduceRoot.setLayoutParams(layoutParams);
        logger.e("=====>showTeamIntroduce:" + topMargin);
        displayTeamInfo();
    }

    private void displayTeamInfo() {
        RelativeLayout rlTeamInfo = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_team_introduce);
        rlTeamInfo.setVisibility(View.VISIBLE);
        rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule).setVisibility(View.GONE);
        TextView tvTeamName = rlTeamInfo.findViewById(R.id.tv_teampk_team_name);
        tvTeamName.setText("恭喜你成为“" + mTeamName + "”的一员！");

        AlphaAnimation animation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim.anim_livevido_teampk_alpha_in);
        animation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                showTeamInfoWithInputEffect(rlTeamIntroduceRoot, mTeamInfoStr);
            }
        });

        tvTeamName.startAnimation(animation);
    }

    /**
     * 展示 获取能量 规则
     */
    private void displayRulInfo() {

        rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_team_introduce).setVisibility(View.GONE);
        rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule).setVisibility(View.VISIBLE);

        final ImageView readyBtn = rlTeamIntroduceRoot.findViewById(R.id.iv_teampk_btn_ok);
        final TextView ruleTitle = rlTeamIntroduceRoot.findViewById(R.id.tv_teampk_rule_title);
        final View rule1 = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule_1);
        final View rule2 = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule_2);
        final View rule3 = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule_3);

        ruleTitle.setVisibility(View.INVISIBLE);
        readyBtn.setVisibility(View.INVISIBLE);
        rule1.setVisibility(View.INVISIBLE);
        rule2.setVisibility(View.INVISIBLE);
        rule3.setVisibility(View.INVISIBLE);

        Animation.AnimationListener listener = new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (soundPoolHelper != null) {
                    soundPoolHelper.playMusic(R.raw.marquee, MUSIC_VOLUME_RATIO_FRONT, false);
                }
            }
        };

        Animation.AnimationListener finish = new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (soundPoolHelper != null) {
                    soundPoolHelper.playMusic(R.raw.marquee, MUSIC_VOLUME_RATIO_FRONT, false);
                }

                startAutoEnterNextStep();
            }
        };

        Interpolator it = new SpringScaleInterpolator(0.19f);

        long delayed = 10;
        delayed = delayed + bindAnimation(ruleTitle, listener, R.anim.anim_livevido_teampk_alpha_in, delayed, null) + 1000;
        delayed = delayed + bindAnimation(rule1, listener, R.anim.anim_livevideo_teampk_rule_in, delayed, null) + 1000;
        delayed = delayed + bindAnimation(rule2, listener, R.anim.anim_livevideo_teampk_rule_in, delayed, null) + 1000;
        delayed = delayed + bindAnimation(rule3, listener, R.anim.anim_livevideo_teampk_rule_in, delayed, null) + 1000;
        delayed = delayed + bindAnimation(readyBtn, finish, R.anim.anim_livevido_teampk_click_btn, delayed, it) + 1000;

        ImageView ivReadyBtn = rlTeamIntroduceRoot.findViewById(R.id.iv_teampk_btn_ok);
        ivReadyBtn.setOnClickListener(this);
    }

    private long bindAnimation(final View target, final Animation.AnimationListener listener, final int animId, long delayed, Interpolator interpolator) {

        final Animation anim = AnimationUtils.loadAnimation(mContext, animId);
        anim.setAnimationListener(listener);
        if (interpolator != null) {
            anim.setInterpolator(interpolator);
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {

                if (target.getVisibility() != View.VISIBLE) {
                    target.setVisibility(View.VISIBLE);
                }


                target.setAnimation(anim);
                anim.start();
            }
        };

        target.postDelayed(action, delayed);
        return anim.getDuration();
    }

    private void startAutoEnterNextStep() {
        tvTimeCountDown.setTimeDuration(10);
        tvTimeCountDown.startCountDow(5000);
        tvTimeCountDown.setTimeCountDowListener(new TimeCountDowTextView.TimeCountDowListener() {
            @Override
            public void onFinish() {
                upLoadStudentReady();
            }
        });
    }

    /**
     * 上报学生 分队准备ok
     */
    private void upLoadStudentReady() {
        rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule).setVisibility(View.GONE);
        tvTimeCountDown.setVisibility(View.GONE);

        lavTeamSelectAnimView.setVisibility(View.INVISIBLE);
        lavTeamSelectAnimView.cancelAnimation();

        showTeamMembers();
        mPKBll.sendStudentReady();
    }

    private void bgMaskFadeIn() {
        logger.e("=====>bgMaskFadeIn called:");
        if (ivBgMask.getVisibility() != View.VISIBLE) {
            ivBgMask.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.
                    loadAnimation(mContext, R.anim.anim_livevido_teampk_bg_mask);
            alphaAnimation.setFillAfter(true);
            ivBgMask.startAnimation(alphaAnimation);
        }
    }

    public void setData(TeamPkTeamInfoEntity teamInfoEntity) {
        mTeamInfo = teamInfoEntity;
        mTeamInfoStr = mTeamInfo.getTeamInfo().getBackGroud();
        mTeamName = mTeamInfo.getTeamInfo().getTeamName();
    }

    private final int rule_anim_count = 5;

    /**
     * 展示 战队成员列表
     */
    private void showTeamMembers() {
        logger.e("=====>showTeamMembers called");
        final TeamPkRecyclerView rclTeamMember = mView.findViewById(R.id.rcl_teampk_teammember);
        rclTeamMember.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rclTeamMember.getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int realY = Math.min(point.x, point.y);

        layoutParams.topMargin = (int) (realY * 0.32);
        rclTeamMember.setLayoutParams(layoutParams);
        final int spanCount = 5;
        rclTeamMember.setLayoutManager(new TeamMemberGridlayoutManager(mContext, 5, LinearLayoutManager.VERTICAL, false));
        ((ViewGroup) mView).setClipChildren(true);
        teamMemberAdapter = new TeamAdapter(ADAPTER_TYPE_TEAM_MEMBER);
        rclTeamMember.setAdapter(teamMemberAdapter);

        rclTeamMember.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition >= spanCount) {
                    top = SizeUtils.Dp2Px(mContext, 10);
                }
                outRect.set(left, top, right, bottom);
            }
        });


        GridLayoutAnimationController animationController = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        rclTeamMember.setLayoutAnimation(animationController);
        rclTeamMember.scheduleLayoutAnimation();

        rclTeamMember.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //判断当前队员是否显示完毕  未显示完 则自动滑动到底部
                if (rclTeamMember.canScrollVertically(1)) {
                    rclTeamMember.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rclTeamMember.smoothScrollToPosition((teamMemberAdapter.getItemCount() - 1));
                        }
                    }, 1500);
                    rclTeamMember.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                            int postion = gridLayoutManager.findLastVisibleItemPosition();
                            if (postion == recyclerView.getAdapter().getItemCount() - 1) {
                                finishTeamSelect();
                            }
                        }
                    });
                } else {
                    // 队员显示完毕 显示关闭按钮
                    finishTeamSelect();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rclTeamMember.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    rclTeamMember.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

    }

    private void showTeamInfoWithInputEffect(RelativeLayout rlTeamInfo, String teamInfo) {
        InputEffectTextView inputEffectTextView = rlTeamInfo.findViewById(R.id.itv_teampk_team_info);
        inputEffectTextView.setText(teamInfo, new InputEffectTextView.InputEffectListener() {
            @Override
            public void onFinish() {

                stopMusic(R.raw.input_effect);

                rlTeamIntroduceRoot.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayRulInfo();
                    }
                }, 1500);
            }
        });

        playInputEffect();
    }


    /**
     * 开启分队仪式
     */
    public void startTeamSelect() {
        playBgMusic();
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "team_select_start/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "team_select_start/data.json";
        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        lavTeamSelectAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
        lavTeamSelectAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(lavTeamSelectAnimView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });

        lavTeamSelectAnimView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() > FRACTION_BG_MASK_FADE_IN && !bgHasFadeIn) {
                    bgHasFadeIn = true;
                    ivBgMask.setVisibility(View.VISIBLE);
                }
            }
        });

        lavTeamSelectAnimView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                playWelcomeMusic();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showTimeCutdown();
            }
        });

        lavTeamSelectAnimView.playAnimation();
    }

    private void showMarquee() {
        logger.e("========> showMarquee");
        ((ViewGroup) mView).setClipChildren(false);
        final int spanCount = 3;
        lavTeamSelectAnimView.cancelAnimation();
        lavTeamSelectAnimView.removeAllAnimatorListeners();
        lavTeamSelectAnimView.setVisibility(View.GONE);
        lavTeamSelectAnimView.setImageDrawable(null);

        teamsRecyclerView = getRootView().findViewById(R.id.rcl_teampk_team);
        teamsRecyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount, LinearLayoutManager.VERTICAL, false));
        teamAdapter = new TeamAdapter(ADAPTER_TYPE_TEAM);
        teamsRecyclerView.setAdapter(teamAdapter);
        teamsRecyclerView.setVisibility(View.VISIBLE);


        teamsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition >= spanCount) {
                    top = getTopGap(teamsRecyclerView, spanCount);
                    top = top < 0 ? 0 : top;
                }
                logger.e("top:" + top);
                outRect.set(left, top, right, bottom);
            }
        });

        long delay = 500;

        GridLayoutAnimationController animationController = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_team_list);
        teamsRecyclerView.setLayoutAnimation(animationController);
        teamsRecyclerView.scheduleLayoutAnimation();
        teamsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                playMarquee();

                startMarquee();
            }
        }, delay);

        teamsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    cancelMarquee();
                    showTeamSelectedScene(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, MARQUEE_DURATION + delay);

    }

    private int getTopGap(RecyclerView recyclerView, int spanCount) {
        if (mTopGap == -1) {
            int rowNum = (recyclerView.getAdapter().getItemCount() % spanCount == 0) ? recyclerView.getAdapter()
                    .getItemCount()
                    / spanCount : recyclerView.getAdapter().getItemCount() / spanCount + 1;
            mTopGap = rowNum > 1 ? (recyclerView.getLayoutParams().height - rowNum * SizeUtils.Dp2Px(mContext, 97)) /
                    (rowNum - 1) : 0;
        }
        return mTopGap;
    }

    class ItemAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private boolean animDispatched = false;

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (!animDispatched && animation.getAnimatedFraction() > MARQUEE_ANIM_DISPATCH_FRACTION) {
                animDispatched = true;
                mTeamIndex++;
                startMarquee();
            }
        }

        public void reset() {
            animDispatched = false;
        }
    }

    private void cancelMarquee() {
        logger.e("======>cancelMarquee");
        stopMusic(R.raw.marquee);

        if (teamItemAnimInfoList != null && teamItemAnimInfoList.size() > 0) {
            for (TeamItemAnimInfo itemAnimInfo : teamItemAnimInfoList) {
                ((ObjectAnimator) itemAnimInfo.mAnimatorSet.getChildAnimations().get(0)).removeAllUpdateListeners();
                itemAnimInfo.mAnimatorSet.cancel();
                itemAnimInfo.mAnimatorSet.removeAllListeners();
            }
            teamItemAnimInfoList.clear();
        }

        if (teamsRecyclerView != null && teamsRecyclerView.getParent() != null) {
            ((ViewGroup) teamsRecyclerView.getParent()).removeView(teamsRecyclerView);
        }
        logger.e("======>cancelMarquee done");
    }

    private void startMarquee() {
        if (teamAdapter == null || teamAdapter.getItemCount() <= 0) {
            return;
        }
        if (teamItemAnimInfoList == null) {
            teamItemAnimInfoList = new ArrayList<TeamItemAnimInfo>();
        }
        int adapterPosition = mTeamIndex % teamAdapter.getItemCount();
        RecyclerView.ViewHolder viewHolder = teamsRecyclerView.findViewHolderForAdapterPosition(adapterPosition);
        if (viewHolder != null) {
            if (teamItemAnimInfoList.size() >= teamAdapter.getItemCount()) {
                TeamItemAnimInfo animInfo = teamItemAnimInfoList.get(adapterPosition);
                animInfo.mUpdateListener.reset();
                ((ObjectAnimator) animInfo.mAnimatorSet.getChildAnimations().get(0)).addUpdateListener(animInfo.mUpdateListener);
                animInfo.mAnimatorSet.start();
            } else {
                AnimatorSet itemAnimatorSet;
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0f, 1.50f, 1.0f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0f, 1.5f, 1.0f);

                itemAnimatorSet = new AnimatorSet();
                itemAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator);
                itemAnimatorSet.setDuration(MARQUEE_ANIM_DURATION);
                itemAnimatorSet.start();
                ItemAnimUpdateListener listener = new ItemAnimUpdateListener();
                scaleXAnimator.addUpdateListener(listener);
                TeamItemAnimInfo itemAnimInfo = new TeamItemAnimInfo(adapterPosition, itemAnimatorSet, listener);
                teamItemAnimInfoList.add(itemAnimInfo);
            }
        }
    }

    private void showTimeCutdown() {
        logger.e("===>show time cut down");
        ivBgMask.setVisibility(View.GONE);
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "time_cutdown/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "time_cutdown/data.json";
        lavTeamSelectAnimView.cancelAnimation();
        lavTeamSelectAnimView.setRepeatCount(0);
        lavTeamSelectAnimView.removeAllAnimatorListeners();
        lavTeamSelectAnimView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showMarquee();
            }
        });

        final LottieEffectInfo lottieEffectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        lavTeamSelectAnimView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
        lavTeamSelectAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return lottieEffectInfo.fetchBitmapFromAssets(lavTeamSelectAnimView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });
        lavTeamSelectAnimView.playAnimation();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_teampk_btn_ok) {
            upLoadStudentReady();
        }
    }

    @Override
    public void initData() {
        //logger.e( "======> initData called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRes();
    }

    private void releaseRes() {
        try {
            releaseSoundRes();
            cancelMarquee();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseSoundRes() {
        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }
    }

    private class TeamItemAnimInfo {
        int mAdapterPosition;
        AnimatorSet mAnimatorSet;
        ItemAnimUpdateListener mUpdateListener;

        TeamItemAnimInfo(int position, AnimatorSet animatorSet, ItemAnimUpdateListener listener) {
            this.mAdapterPosition = position;
            this.mAnimatorSet = animatorSet;
            this.mUpdateListener = listener;
        }
    }

    private class TeamItemHolder extends RecyclerView.ViewHolder {

        ImageView ivTeamLogo;

        public TeamItemHolder(View itemView) {
            super(itemView);
            ivTeamLogo = itemView.findViewById(R.id.iv_teampk_team_logo);
        }

        public void bindData(String logoUrl) {
            ImageLoader.with(mContext).load(logoUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivTeamLogo);
        }
    }

    private class TeamMemberHolder extends RecyclerView.ViewHolder {

        private ImageView ivHead;
        private TextView tvName;

        public TeamMemberHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_teampk_member_head);
            tvName = itemView.findViewById(R.id.tv_teampk_member_name);
        }

        public void bindData(TeamPkTeamInfoEntity.StudentEntity studentEntity) {
            tvName.setText(studentEntity.getUserName());
            ImageLoader.with(mContext).load(studentEntity.getImg()).asCircle().into(ivHead);
        }
    }

    private class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        int mAdapterType;

        public TeamAdapter(int type) {
            this.mAdapterType = type;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (mAdapterType == ADAPTER_TYPE_TEAM) {
                return new TeamItemHolder(LayoutInflater.from(mContext).
                        inflate(R.layout.item_teampk_team, parent, false));
            } else {
                return new TeamMemberHolder(LayoutInflater.from(mContext).
                        inflate(R.layout.item_teampk_teammember, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (mAdapterType == ADAPTER_TYPE_TEAM) {
                ((TeamItemHolder) holder).bindData(mTeamInfo.getTeamLogoList().get(position));
            } else {
                ((TeamMemberHolder) holder).bindData(mTeamInfo.getTeamMembers().get(position));
            }
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            if (mAdapterType == ADAPTER_TYPE_TEAM_MEMBER) {
                if (mTeamInfo != null && mTeamInfo.getTeamMembers() != null) {
                    itemCount = mTeamInfo.getTeamMembers().size();
                }
            } else {
                if (mTeamInfo != null && mTeamInfo.getTeamLogoList() != null) {
                    itemCount = mTeamInfo.getTeamLogoList().size();
                }
            }
            return itemCount;
        }
    }

}
