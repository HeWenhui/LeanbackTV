package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SoundInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamSelectLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.InputEffectTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkRecyclerView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chenkun on 2018/4/12
 * 战队选择页面
 */
public class TeamPkTeamSelectPager extends BasePager implements View.OnClickListener {
    private static final String TAG = "TeamPkTeamSelectPager";
    private TeamPKBll mPKBll;
    private ImageView ivBg;
    private ImageView ivBgMask;
    private LottieAnimationView lavTeamSelectAnimView;    // 分队仪式 特效展示 主 lottie view
    private static final long MARQUEE_DURATION = 1800 * 2;    // 跑马灯展示时间
    private final float LAST_ANIMPUASE_FRACTION = 0.32f;    // 最后一次lottie 动画 暂停位置
    private final float TEAMINFOUI_HIDE_FRACTION = 0.57f;
    private final float LAST_ANIM_RESUME_FRACTION = 0.55f; // 最后一次 lottie 暂停后 复播位置
    String mTeamInfoStr = "我们狮子是古老而骄傲的动物森林之王,永不言败!";
    //分队仪式 资源在asset中的相对路径
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/team_select/";
    private TeamPkRecyclerView teamsRecyclView;
    private TeamAdapter teamAdapter;

    private static final int ANIMTYPE_START = 1;
    private static final int ANIMTYPE_TIME_COUTDOWN = 2;
    private static final int ANIMTYPE_TEAM_SELECTED = 3;
    private RelativeLayout rlTeamIntroduceRoot;
    private TeamInfoAnimListener teamInfoAnimListener;
    private TeamPkRecyclerView rclTeamMember;
    private TeamAdapter teamMemberAdapter;

    private static final int SOUND_TYPE_WAR_BG = 1;  // 战队背景音
    private static final int SOUND_TYPE_MARQUEE = 2;  //跑马灯音效
    private static final int SOUND_TYPE_CHEER = 3;   // 欢呼音效
    private static final int SOUND_TYPE_INPUT_EFFECT = 4;   // 打字音效
    private static final int SOUND_TYPE_WELCOME_TO_TEAMPK = 5;   // 欢迎音效

    private HashMap<Integer, SoundInfo> mSoundInfoMap; //= new HashMap<Integer,SoundInfo>();
    private SoundPool soundPool;
    boolean soundResAvailable;  //当前音效资源是否可用
    private AudioManager mAudioManager;
    private TimeCountDowTextView tvTimeCountDown;
    private static final float MUSIC_VOLUME_RATIO_BG = 0.3f;  //背景音乐 占系统音量的 比列
    private static final float MUSIC_VOLUME_RATIO_FRONT = 0.6f;  //前景音效的比列
    private TeamPkTeamInfoEntity mTeamInfo;
    private String teamName;
    private Bitmap teamLogo;

    public TeamPkTeamSelectPager(Context context, TeamPKBll pkBll) {
        super(context);
        mPKBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_teamselect, null);
        ivBg = view.findViewById(R.id.iv_teampk_team_select_bg);
        ivBgMask = view.findViewById(R.id.iv_teampk_bgmask);
        lavTeamSelectAnimView = view.findViewById(R.id.lav_teampk_team_select);
        rlTeamIntroduceRoot = view.findViewById(R.id.rl_teampk_teaminfo_root);
        tvTimeCountDown = view.findViewById(R.id.tv_teampk_team_select_timecoutdown);
        tvTimeCountDown.setTimeSuffix("秒后进入下一步");
        loadSoundRes();
        startTeamSelect();
        return view;
    }

    /**
     * 加载音效资源
     */
    private void loadSoundRes() {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundInfoMap = new HashMap<Integer, SoundInfo>();
    }

    private void playBgMusic() {
        playMusicWithLoad(SOUND_TYPE_WAR_BG, R.raw.war_bg, MUSIC_VOLUME_RATIO_BG, true);
    }

    private void playWelcomeMusic() {
        playMusicWithLoad(SOUND_TYPE_WELCOME_TO_TEAMPK, R.raw.welcome_to_teampk, MUSIC_VOLUME_RATIO_FRONT, false);
    }

    /**
     * 播发跑马灯音效
     */
    private void playMarquee() {
        playMusicWithLoad(SOUND_TYPE_MARQUEE, R.raw.marquee, MUSIC_VOLUME_RATIO_FRONT, true);
    }


    /**
     * 播放欢呼音效
     */
    private void playCheering() {
        playMusicWithLoad(SOUND_TYPE_CHEER, R.raw.cheering, MUSIC_VOLUME_RATIO_FRONT, false);

    }


    /**
     * 播放打字音效
     */
    private void playInputEffect() {
        playMusicWithLoad(SOUND_TYPE_INPUT_EFFECT, R.raw.input_effect, MUSIC_VOLUME_RATIO_FRONT, true);
    }


    /**
     * 播放音效
     *
     * @param soundType
     * @param loop
     */
    private void playMusic(int soundType, float volume, boolean loop) {
        Log.e("TeampkTeamSelectPager", "======>playMusic:" + soundPool + ":" + mSoundInfoMap + ":" + soundResAvailable);
        if (soundPool != null && mSoundInfoMap != null && soundResAvailable) {
            SoundInfo soundInfo = mSoundInfoMap.get(soundType);
            if (soundInfo != null) {
                //int volume = getSysVolume();
                Log.e("TeampkTeamSelectPager", "======>playMusic: inner " + soundType + ":" + volume);

                int streamId = soundPool.play(soundInfo.getSoundId(), volume,
                        volume, 0, loop ? -1 : 0, 1);
                Log.e(TAG, "===>playMusic:" + streamId + ":" + volume + ":" + soundInfo.getSoundId());
                soundInfo.setSoundVolume((int) volume);
                soundInfo.setStreamId(streamId);
            }
        }
    }

    /**
     * 播放时 从新加载资源
     *
     * @param soundType
     * @param loop
     */
    private void playMusicWithLoad(final int soundType, int resId, final float volume, final boolean loop) {
        if (soundPool != null) {
            SoundInfo soundInfo = mSoundInfoMap.get(soundType);
            if (soundInfo != null) {
                int streamId = soundPool.play(soundInfo.getSoundId(), volume, volume, soundType, loop ? -1 : 0, 1);
                soundInfo.setStreamId(streamId);
                mSoundInfoMap.put(soundType,soundInfo);
                Log.e("", "======>playMusicWithLoad: cached info" + soundType +":"+streamId);
            } else {
                soundPool.load(mContext, resId, 1);
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        int streamId = soundPool.play(sampleId, volume, volume, soundType, loop ? -1 : 0, 1);
                        Log.e("TeamSelectedPager", "======>playMusicWithLoad: new info" + soundType + ":" + sampleId);
                        SoundInfo soundInfo = new SoundInfo(sampleId, streamId);
                        mSoundInfoMap.put(soundType, soundInfo);
                    }
                });
            }
        }
    }

    /**
     * 设置指定音乐的 音量大小
     *
     * @param soundType
     * @param volume
     */
    private void setSoundVolume(int soundType, int volume) {
        if (soundPool != null && mSoundInfoMap != null && soundResAvailable) {
            SoundInfo soundInfo = mSoundInfoMap.get(soundType);
            if (soundInfo != null) {
                Log.e(TAG, "========>setSoundVolume:" + soundInfo.getSoundVolume() + ":" + soundInfo.getStreamId() + ":" + volume);
                soundPool.setVolume(soundInfo.getStreamId(), volume, volume);
                soundInfo.setSoundVolume(volume);
            }
        }
    }

    /**
     * 暂停播放音效
     *
     * @param soundType
     */
    private void stopMusic(int soundType) {

        Log.e("TeamPkTeamSelectPager", "======>stopMusic:" + soundPool + ":" + mSoundInfoMap + ":" + soundResAvailable);
        if (soundPool != null && mSoundInfoMap != null) {
            SoundInfo soundInfo = mSoundInfoMap.get(soundType);
            Log.e("TeampkSelectpager", "=====>stopMusic:" + soundType);
            if (soundInfo != null) {
                soundPool.stop(soundInfo.getStreamId());
                Log.e("TeamPkTeamSelectPager", "======>stopMusic:" + soundInfo.getStreamId());
            }
        }
    }

    /**
     * 已选中 战队信息
     */
    boolean paused = false;
    boolean teamInfoUiReaMoved = false;

    /**
     * 展示分队仪式 lottie 动画
     */
    public void showTeamSelectedScene() {
        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "team_selected/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "team_selected/data.json";
        final TeamSelectLottieEffectInfo effectInfo = new TeamSelectLottieEffectInfo(lottieResPath, lottieJsonPath, "img_1.png");
        effectInfo.setLogoUrl(mTeamInfo.getTeamInfo().getImg());
        lavTeamSelectAnimView.setVisibility(View.VISIBLE);
        lavTeamSelectAnimView.removeAllAnimatorListeners();
        lavTeamSelectAnimView.addAnimatorListener(new TeamSelectAnimatorListener(ANIMTYPE_TEAM_SELECTED));

        lavTeamSelectAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
        lavTeamSelectAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(lavTeamSelectAnimView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });
        lavTeamSelectAnimView.playAnimation();


        // 0.35 进度 : 显示 战队介绍
        // 监听 动画执行进度
        // lavTeamSelectAnimView.setProgress(0.0f); // lottie 中读取的 读取的初始值 不为0
        lavTeamSelectAnimView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean isInited = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e(TAG, "====>onAnimationUpdate:" + animation.getAnimatedFraction());
                if (!isInited && animation.getAnimatedFraction() > 0) {
                    isInited = true;
                    return;
                }
                if (!paused && animation.getAnimatedFraction() > LAST_ANIMPUASE_FRACTION) {
                    paused = true;
                    lavTeamSelectAnimView.pauseAnimation();
                    showTeamIntroduce(lavTeamSelectAnimView);
                }
                if (!teamInfoUiReaMoved && animation.getAnimatedFraction() >= TEAMINFOUI_HIDE_FRACTION) {
                    teamInfoUiReaMoved = true;
                    hideTeamInfoUi();
                    showTeamMembers();
                }
            }
        });

    }


    private void hideTeamInfoUi() {
        rlTeamIntroduceRoot.setVisibility(View.GONE);
    }

    // 展示 战队成员列表
    private void showTeamMembers() {
        Log.e(TAG, "=====>showTeamMembers called");
        rclTeamMember = mView.findViewById(R.id.rcl_teampk_teammember);
        rclTeamMember.setVisibility(View.VISIBLE);
        rclTeamMember.setLayoutManager(new TeamMemberGridlayoutManager(mContext, 5,
                LinearLayoutManager.VERTICAL, false));
        ((ViewGroup) mView).setClipChildren(true);
        teamMemberAdapter = new TeamAdapter(ADAPTER_TYPE_TEAM_MEMBER);
        rclTeamMember.setAdapter(teamMemberAdapter);

        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        rclTeamMember.setLayoutAnimation(animationController);
        rclTeamMember.scheduleLayoutAnimation();

        // 队员人数小于 一屏
        if (rclTeamMember.getAdapter().getItemCount() <= 15) {
            finishTeamSelect();
        } else {
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
        }
    }

    /**
     * 结束分队仪式
     */
    private void finishTeamSelect() {
        // todo  显示关闭按钮
        //Log.e(TAG,"======>显示关闭按钮");
        // show btn anim
        ImageView ivClose = mView.findViewById(R.id.iv_teampk_finish_team_select);
        if (ivClose.getVisibility() != View.VISIBLE) {
            ivClose.setVisibility(View.VISIBLE);
            ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                    loadAnimation(mContext, R.anim.anim_livevido_teampk_click_btn);
            scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.19f));
            ivClose.startAnimation(scaleAnimation);
        }
        // 清除所有 lottie 资源
        //lavTeamSelectAnimView.cancelAnimation();
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
        mView.post(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        });
    }

    private void showTeamIntroduce(LottieAnimationView bgAnimView) {
        // step 1  显示 背景黑色遮罩动画
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_bg_mask);
        alphaAnimation.setFillAfter(true);
        ivBgMask.setVisibility(View.VISIBLE);
        ivBgMask.startAnimation(alphaAnimation);
        // step 2 显示队伍介绍
        rlTeamIntroduceRoot.setVisibility(View.VISIBLE);
        displayTeamInfo();
    }


    private final int TEAM_INFO_ANIM_TYPE_TEAM_NAME = 1;  //战队名称动画
    private final int TEAM_INFO_ANIM_TYPE_RUL = 2;        //规则介绍动画

    public void setData(TeamPkTeamInfoEntity teamInfoEntity) {
        mTeamInfo = teamInfoEntity;
        mTeamInfoStr = mTeamInfo.getTeamInfo().getBackGroud();
        teamName = mTeamInfo.getTeamInfo().getTeamName();
        //预加载队伍图标
        String logourl = mTeamInfo.getTeamInfo().getImg();
        ImageLoader.with(mContext).load(logourl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .asBitmap(new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        teamLogo = ((BitmapDrawable) drawable).getBitmap();
                    }

                    @Override
                    public void onFail() {
                    }
                });
    }

    class AnimInfo {
        View targetView;
        Animation animation;
        TeamInfoAnimListener animationListener;
        int animType;

        public AnimInfo(View targetView, Animation animation, int animType, TeamInfoAnimListener animationListener) {
            this.targetView = targetView;
            this.animation = animation;
            this.animationListener = animationListener;
            this.animType = animType;
        }

        public void startAnim() {

            if (animationListener != null) {
                targetView.setVisibility(View.VISIBLE);
                animationListener.setAnimType(animType);
                animation.setAnimationListener(animationListener);
                targetView.startAnimation(animation);
            }
        }
    }


    private List<AnimInfo> teamInfoAnimList;

    /**
     * 战队信息 动画监听
     */
    private class TeamInfoAnimListener implements Animation.AnimationListener {

        private int animType;

        TeamInfoAnimListener(int animType) {
            this.animType = animType;
        }

        public void setAnimType(int animType) {
            this.animType = animType;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (animType) {
                case TEAM_INFO_ANIM_TYPE_TEAM_NAME:
                    showTeamInfoWithInputEffect(rlTeamIntroduceRoot, mTeamInfoStr);
                    break;
                case TEAM_INFO_ANIM_TYPE_RUL:
                    if (teamInfoAnimList != null && teamInfoAnimList.size() > 0) {
                        // 三条规则播放 音效  teamInfoAnimList 中前3条为 规则
                        if (teamInfoAnimList.size() < 5 && teamInfoAnimList.size() > 1) {
                            playMusicWithLoad(SOUND_TYPE_MARQUEE, R.raw.marquee, MUSIC_VOLUME_RATIO_FRONT, false);
                        }
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                teamInfoAnimList.remove(0).startAnim();
                            }
                        }, 1000);
                    }
                    // 按钮缩放动画完成
                    if (teamInfoAnimList != null && teamInfoAnimList.size() == 0) {
                        startAutoEnterNextStep();
                    }
                    break;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
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


    private void displayTeamInfo() {
        RelativeLayout rlTeamInfo = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_team_introduce);
        rlTeamInfo.setVisibility(View.VISIBLE);
        RelativeLayout rlRul = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule);
        rlRul.setVisibility(View.GONE);
        TextView tvTeamName = rlTeamInfo.findViewById(R.id.tv_teampk_team_name);
        tvTeamName.setText("恭喜你成为“" + teamName + "”的一员！");
        AlphaAnimation animation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim.anim_livevido_teampk_alpha_in);
        teamInfoAnimListener = new TeamInfoAnimListener(TEAM_INFO_ANIM_TYPE_TEAM_NAME);
        animation.setAnimationListener(teamInfoAnimListener);
        tvTeamName.startAnimation(animation);
    }


    private void showTeamInfoWithInputEffect(RelativeLayout rlTeamInfo, String teamInfo) {
        InputEffectTextView inputEffectTextView = rlTeamInfo.findViewById(R.id.itv_teampk_team_info);
        inputEffectTextView.setText(teamInfo, new InputEffectTextView.InputEffectListener() {
            @Override
            public void onFinish() {
                stopMusic(SOUND_TYPE_INPUT_EFFECT);
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
     * 展示 获取能量 规则
     */
    private void displayRulInfo() {
        RelativeLayout rlTeamInfo = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_team_introduce);
        rlTeamInfo.setVisibility(View.GONE);
        RelativeLayout rlRul = rlTeamIntroduceRoot.findViewById(R.id.rl_teampk_rule);
        rlRul.setVisibility(View.VISIBLE);

        AlphaAnimation animation = (AlphaAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_alpha_in);

        if (teamInfoAnimListener == null) {
            teamInfoAnimListener = new TeamInfoAnimListener(TEAM_INFO_ANIM_TYPE_RUL);
        } else {
            teamInfoAnimListener.setAnimType(TEAM_INFO_ANIM_TYPE_RUL);
        }
        initPkRuleAnim(rlRul);

        TextView ruleTitle = rlRul.findViewById(R.id.tv_teampk_rule_title);
        animation.setAnimationListener(teamInfoAnimListener);
        ruleTitle.startAnimation(animation);

        ImageView ivReadyBtn = rlRul.findViewById(R.id.iv_teampk_btn_ok);
        ivReadyBtn.setOnClickListener(this);
    }


    private void initPkRuleAnim(RelativeLayout rlRul) {
        if (teamInfoAnimList == null) {
            teamInfoAnimList = new ArrayList<AnimInfo>();
        }
        RelativeLayout rlRule_1 = rlRul.findViewById(R.id.rl_teampk_rule_1);
        RelativeLayout rlRule_2 = rlRul.findViewById(R.id.rl_teampk_rule_2);
        RelativeLayout rlRule_3 = rlRul.findViewById(R.id.rl_teampk_rule_3);
        TextView tvReady = rlRul.findViewById(R.id.tv_teampk_team_ready);

        teamInfoAnimList.addAll(bindAnim2View(rlRule_1, rlRule_2, rlRule_3, tvReady));

        ImageView ivReadyBtn = rlRul.findViewById(R.id.iv_teampk_btn_ok);
        ivReadyBtn.setVisibility(View.INVISIBLE);
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_click_btn);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.19f));

        AnimInfo animInfoRule_5 = new AnimInfo(ivReadyBtn, scaleAnimation, TEAM_INFO_ANIM_TYPE_RUL,
                teamInfoAnimListener);
        teamInfoAnimList.add(animInfoRule_5);

    }

    private List<AnimInfo> bindAnim2View(View... views) {
        List<AnimInfo> resultList = new ArrayList<AnimInfo>();
        AnimationSet animationSet;
        AnimInfo info = null;
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.INVISIBLE);
            animationSet = (AnimationSet) AnimationUtils.
                    loadAnimation(mContext, R.anim.anim_livevideo_teampk_rule_in);
            info = new AnimInfo(views[i], animationSet, TEAM_INFO_ANIM_TYPE_RUL, teamInfoAnimListener);
            resultList.add(info);
        }
        return resultList;
    }

    ;

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
        lavTeamSelectAnimView.addAnimatorListener(new TeamSelectAnimatorListener(ANIMTYPE_START));
        lavTeamSelectAnimView.playAnimation();
    }

    class TeamSelectAnimatorListener implements Animator.AnimatorListener {
        private int mAnimType;

        TeamSelectAnimatorListener(int animType) {
            mAnimType = animType;
        }

        @Override
        public void onAnimationStart(Animator animation) {

            switch (mAnimType) {
                case ANIMTYPE_START:
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playWelcomeMusic();
                        }
                    },1000);
                    break;
                case ANIMTYPE_TEAM_SELECTED:
                    playCheering();
                    break;
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (mAnimType) {
                case ANIMTYPE_START:
                    showTimeCutdown();
                    break;
                case ANIMTYPE_TIME_COUTDOWN:
                    showMarquee();
                    break;
                case ANIMTYPE_TEAM_SELECTED:
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

        private ImageView ivhead;
        private TextView tvName;

        public TeamMemberHolder(View itemView) {
            super(itemView);
            ivhead = itemView.findViewById(R.id.iv_teampk_member_head);
            tvName = itemView.findViewById(R.id.tv_teampk_member_name);
        }

        public void bindData(TeamPkTeamInfoEntity.StudentEntity studentEntity) {
            tvName.setText(studentEntity.getUserName());
            ImageLoader.with(mContext).load(studentEntity.getImg()).asCircle().into(ivhead);
        }
    }

    private static final int ADAPTER_TYPE_TEAM = 1;
    private static final int ADAPTER_TYPE_TEAM_MEMBER = 2;

    private class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        int adapterType;

        public TeamAdapter(int type) {
            this.adapterType = type;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (adapterType == ADAPTER_TYPE_TEAM) {
                return new TeamItemHolder(LayoutInflater.from(mContext).
                        inflate(R.layout.item_teampk_team, parent, false));
            } else {
                return new TeamMemberHolder(LayoutInflater.from(mContext).
                        inflate(R.layout.item_teampk_teammember, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (adapterType == ADAPTER_TYPE_TEAM) {
                ((TeamItemHolder) holder).bindData(mTeamInfo.getTeamLogoList().get(position));
            } else {
                ((TeamMemberHolder) holder).bindData(mTeamInfo.getTeamMembers().get(position));
            }
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            if (adapterType == ADAPTER_TYPE_TEAM_MEMBER) {
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

    private void showMarquee() {
        ((ViewGroup) mView).setClipChildren(false);
        final int spanCount = 3;
        lavTeamSelectAnimView.cancelAnimation();
        lavTeamSelectAnimView.removeAllAnimatorListeners();
        lavTeamSelectAnimView.setVisibility(View.GONE);
        lavTeamSelectAnimView.setImageDrawable(null);

        teamsRecyclView = getRootView().findViewById(R.id.rcl_teampk_team);
        teamsRecyclView.setLayoutManager(new GridLayoutManager(mContext, spanCount,
                LinearLayoutManager.VERTICAL, false));
        teamAdapter = new TeamAdapter(ADAPTER_TYPE_TEAM);
        teamsRecyclView.setAdapter(teamAdapter);
        teamsRecyclView.setVisibility(View.VISIBLE);

        teamsRecyclView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int topGap = getTopGap(parent, spanCount);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition >= spanCount) {
                    top = topGap;
                }
                outRect.set(left, top, right, bottom);
            }
        });

        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_team_list);
        teamsRecyclView.setLayoutAnimation(animationController);
        teamsRecyclView.scheduleLayoutAnimation();

        teamsRecyclView.postDelayed(new Runnable() {
            @Override
            public void run() {
                playMarquee();
                startMarquee();
                getRootView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cancelMarquee();
                            showTeamSelectedScene();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, MARQUEE_DURATION);
            }
        }, 500);

    }

    private int mTopGap = -1;

    private int getTopGap(RecyclerView recyclerView, int spanCount) {
        if (mTopGap == -1) {
            int rowNum = (recyclerView.getAdapter().getItemCount() % spanCount == 0) ? recyclerView.getAdapter().getItemCount()
                    / spanCount : recyclerView.getAdapter().getItemCount() / spanCount + 1;
            mTopGap = rowNum > 1 ? (recyclerView.getMeasuredHeight() - rowNum * SizeUtils.Dp2Px(mContext, 97)) / (rowNum - 1) : 0;
        }
        return mTopGap;
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

    class ItemAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private boolean animDispathed = false;

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (!animDispathed && animation.getAnimatedFraction() > 0.3f) {
                animDispathed = true;
                mTeamIndex++;
                startMarquee();
            }
        }

        public void reset() {
            animDispathed = false;
        }
    }

    private void cancelMarquee() {
        Log.e(TAG, "======>cancelMarquee");
        stopMusic(SOUND_TYPE_MARQUEE);
        if (teamItemAnimInfoList != null && teamItemAnimInfoList.size() > 0) {
            for (TeamItemAnimInfo itemAnimInfo : teamItemAnimInfoList) {
                ((ObjectAnimator) itemAnimInfo.mAnimatorSet.getChildAnimations().get(0)).removeAllUpdateListeners();
                itemAnimInfo.mAnimatorSet.cancel();
                itemAnimInfo.mAnimatorSet.removeAllListeners();
            }
            teamItemAnimInfoList.clear();
        }
        if (teamsRecyclView.getParent() != null) {
            ((ViewGroup) teamsRecyclView.getParent()).removeView(teamsRecyclView);
        }
    }


    private List<TeamItemAnimInfo> teamItemAnimInfoList;
    private int mTeamIndex;

    /**
     * 执行  战队 跑马灯动画
     */
    private void startMarquee() {
        if (teamAdapter == null || teamAdapter.getItemCount() <= 0) {
            return;
        }
        if (teamItemAnimInfoList == null) {
            teamItemAnimInfoList = new ArrayList<TeamItemAnimInfo>();
        }
        int adapterPosition = mTeamIndex % teamAdapter.getItemCount();
        RecyclerView.ViewHolder viewHolder = teamsRecyclView.findViewHolderForAdapterPosition(adapterPosition);
        if (teamItemAnimInfoList.size() >= teamAdapter.getItemCount()) {
            TeamItemAnimInfo animInfo = teamItemAnimInfoList.get(adapterPosition);
            animInfo.mUpdateListener.reset();
            ((ObjectAnimator) animInfo.mAnimatorSet.getChildAnimations().get(0))
                    .addUpdateListener(animInfo.mUpdateListener);
            animInfo.mAnimatorSet.start();
        } else {
            AnimatorSet itemAnimatorSet;
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(viewHolder.itemView
                    , "scaleX", 1.0f, 1.50f, 1.0f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(viewHolder.itemView
                    , "scaleY", 1.0f, 1.5f, 1.0f);

            itemAnimatorSet = new AnimatorSet();
            itemAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            itemAnimatorSet.setDuration(700);
            itemAnimatorSet.start();
            ItemAnimUpdateListener listener = new ItemAnimUpdateListener();
            scaleXAnimator.addUpdateListener(listener);
            TeamItemAnimInfo itemAnimInfo = new TeamItemAnimInfo(adapterPosition, itemAnimatorSet, listener);
            teamItemAnimInfoList.add(itemAnimInfo);
        }
    }


    private void showTimeCutdown() {
        Log.e(TAG, "===>show time cut down");
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "time_cutdown/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "time_cutdown/data.json";
        lavTeamSelectAnimView.cancelAnimation();
        lavTeamSelectAnimView.setRepeatCount(0);
        lavTeamSelectAnimView.removeAllAnimatorListeners();
        lavTeamSelectAnimView.addAnimatorListener(new TeamSelectAnimatorListener(ANIMTYPE_TIME_COUTDOWN));
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

    //上报学生 分队准备ok
    private void upLoadStudentReady() {
        try {
            ((ViewGroup) mView).removeView(tvTimeCountDown);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // step 2 继续 lottie 动画
        lavTeamSelectAnimView.resumeAnimation();
        lavTeamSelectAnimView.setProgress(LAST_ANIM_RESUME_FRACTION);
        mPKBll.sendStudentReady();
    }

    @Override
    public void initData() {
        Log.e(TAG, "======> initData called");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRes();
    }

    private void releaseRes() {
        cancelMarquee();
        releaseSoundRes();
    }

    private void releaseSoundRes() {
        if (soundPool != null) {
            soundPool.release();
            mSoundInfoMap.clear();
            soundResAvailable = false;
            soundPool = null;
        }
    }
}
