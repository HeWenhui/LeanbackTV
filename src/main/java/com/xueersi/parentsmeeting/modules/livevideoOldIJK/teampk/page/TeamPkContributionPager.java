package com.xueersi.parentsmeeting.modules.livevideoOldIJK.teampk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkContribStarLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkPraiseLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 战队PK 二期  贡献之星
 *
 * @author chekun
 * created  at 2019/1/30 13:48
 */
public class TeamPkContributionPager extends TeamPkBasePager {
    /**
     * toast 展示时间
     **/
    private static final long DISPLAY_TIME_DURATION = 3000;
    private final TeamPkBll teamPkBll;
    private TeamEnergyAndContributionStarEntity mData;
    private LottieAnimationView animationView;
    private ImageView ivClose;
    private TimeCountDowTextView timeCountDowTextView;
    private TeamPkPraiseLayout pkPraiseLayout;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/contribution_star/";
    /**
     * 默认背景音效大小
     */
    private static final float DEFAULT_BG_VOLUME = 0.4f;
    /**
     * 默认前景音效大小
     */
    private static final float DEFAULT_FRONT_VOLUME = 0.6f;
    private SoundPoolHelper soundPoolHelper;
    int[] soundResArray = {
            R.raw.pk_contribution_starlist_bg
    };

    public TeamPkContributionPager(Context context, TeamPkBll pkBll, TeamEnergyAndContributionStarEntity data) {
        super(context);
        teamPkBll = pkBll;
        mData = data;
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_teampk_contributionstar, null);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0) {
                    showContributionStar();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        Log.e("teampkContributionPager", "====>initViewcalled");
        animationView = view.findViewById(R.id.lav_teampk_contribution);
        ivClose = view.findViewById(R.id.iv_teampk_contribution_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePager();
            }
        });
        timeCountDowTextView = view.findViewById(R.id.tv_teampk_contribution_time);
        pkPraiseLayout = view.findViewById(R.id.pk_praise_layout);

        pkPraiseLayout.setPriaseStateListener(new TeamPkPraiseLayout.PraiseStateListener() {
            @Override
            public void onFinish(int clickCount) {
                TeamPkLog.sendContrbuteStarThumbCount(teamPkBll.getLiveBll(),teamPkBll.getNonce(),clickCount);
            }
        });
        return view;
    }


    private static class AnimInfo {
        TeamPkContribStarLottieEffectInfo.DetailInfo nameInfo;
        TeamPkContribStarLottieEffectInfo.DetailInfo headImgInfo;
        TeamPkContribStarLottieEffectInfo.DetailInfo energyInfo;
        TeamPkContribStarLottieEffectInfo.DetailInfo energyIconInfo;
        TeamPkContribStarLottieEffectInfo.DetailInfo animBgInfo;

        public TeamPkContribStarLottieEffectInfo.DetailInfo getNameInfo() {
            return nameInfo;
        }

        public void setNameInfo(TeamPkContribStarLottieEffectInfo.DetailInfo nameInfo) {
            this.nameInfo = nameInfo;
        }

        public TeamPkContribStarLottieEffectInfo.DetailInfo getHeadImgInfo() {
            return headImgInfo;
        }

        public void setHeadImgInfo(TeamPkContribStarLottieEffectInfo.DetailInfo headImgInfo) {
            this.headImgInfo = headImgInfo;
        }

        public TeamPkContribStarLottieEffectInfo.DetailInfo getEnergyInfo() {
            return energyInfo;
        }

        public void setEnergyInfo(TeamPkContribStarLottieEffectInfo.DetailInfo energyInfo) {
            this.energyInfo = energyInfo;
        }

        public TeamPkContribStarLottieEffectInfo.DetailInfo getEnergyIconInfo() {
            return energyIconInfo;
        }

        public void setEnergyIconInfo(TeamPkContribStarLottieEffectInfo.DetailInfo energyIconInfo) {
            this.energyIconInfo = energyIconInfo;
        }

        public TeamPkContribStarLottieEffectInfo.DetailInfo getAnimBgInfo() {
            return animBgInfo;
        }

        public void setAnimBgInfo(TeamPkContribStarLottieEffectInfo.DetailInfo animBgInfo) {
            this.animBgInfo = animBgInfo;
        }
    }


    private List<AnimInfo> animInfos;


    private static final String LOTTIE_CACHE_KEY_RANK = "contribution_rank";


    private void showContributionStar() {
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                playMusic(R.raw.pk_contribution_starlist_bg, DEFAULT_BG_VOLUME, false);
            }
        }, 700);

        pkPraiseLayout.setOnLineTeammates(teamPkBll.getOnlineTeamMates());
        pkPraiseLayout.setWrodList(teamPkBll.getPraiseText());

        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
        String[] targetFileNames = {"img_28.png", "img_26.png", "img_25.png", "img_24.png", "img_48.png",
                "img_34.png", "img_32.png", "img_31.png", "img_30.png", "img_49.png",
                "img_46.png", "img_42.png", "img_43.png", "img_44.png", "img_50.png",
                "img_40.png", "img_38.png", "img_37.png", "img_36.png", "img_51.png",
                "img_22.png", "img_18.png", "img_17.png", "img_19.png", "img_52.png"
        };

        final TeamPkContribStarLottieEffectInfo effectInfo = new TeamPkContribStarLottieEffectInfo(lottieResPath,
                lottieJsonPath);

        effectInfo.setTargetFileFilter(targetFileNames);
        //设置字体信息
        effectInfo.setNameTextSize(28);
        effectInfo.setNameTextColor(Color.WHITE);
        effectInfo.setEnergyTextColor(Color.WHITE);
        effectInfo.setEnergyTextSize(24);
        buildAnimInfo(effectInfo);

        animationView.useHardwareAcceleration(true);
        animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), LOTTIE_CACHE_KEY_RANK);
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animationView,
                        lottieImageAsset.getFileName(), lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });
        animationView.playAnimation();
        animationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //学习报告截图
                recordHighLight();
            }
        }, 2000);
        startAutoClose();
    }


    /**
     * 记录学生 高光时刻
     **/
    private void recordHighLight() {
        if (mData.getContributionStarList() != null) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext,
                            StudyReportAction.class);
                    if (studyReportAction != null && mData.isMe()) {
                        studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_PK_RESULT, mView,
                                false, false);
                    }
                }
            }, 200);
        }
    }

    /**
     * 构造 动画脚本信息
     *
     * @param effectInfo
     */
    private void buildAnimInfo(TeamPkContribStarLottieEffectInfo effectInfo) {
        if (animInfos == null) {
            animInfos = new ArrayList<AnimInfo>();
        }
        AnimInfo animInfo1 = new AnimInfo();
        AnimInfo animInfo2 = new AnimInfo();
        AnimInfo animInfo3 = new AnimInfo();
        AnimInfo animInfo4 = new AnimInfo();
        AnimInfo animInfo5 = new AnimInfo();
        //第一组
        animInfo1.setNameInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_42.png", "空缺", true));
        animInfo1.setHeadImgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_46.png", null, true));
        animInfo1.setEnergyInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_43.png", "0", false));
        animInfo1.setEnergyIconInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_44.png", null, false));
        animInfo1.setAnimBgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_51.png", null, false));
        animInfos.add(animInfo1);

        //第二组
        animInfo2.setNameInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_32.png", "空缺", true));
        animInfo2.setHeadImgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_34.png", null, true));
        animInfo2.setEnergyInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_31.png", "0", false));
        animInfo2.setEnergyIconInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_30.png", null, false));
        animInfo2.setAnimBgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_50.png", null, false));
        animInfos.add(animInfo2);

        //第三组
        animInfo3.setNameInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_38.png", "空缺", true));
        animInfo3.setHeadImgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_40.png", null, true));
        animInfo3.setEnergyInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_37.png", "0", false));
        animInfo3.setEnergyIconInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_36.png", null, false));
        animInfo3.setAnimBgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_52.png", null, false));
        animInfos.add(animInfo3);
        //第四组
        animInfo4.setNameInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_26.png", "空缺", true));
        animInfo4.setHeadImgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_28.png", null, true));
        animInfo4.setEnergyInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_25.png", "0", false));
        animInfo4.setEnergyIconInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_24.png", null, false));
        animInfo4.setAnimBgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_49.png", null, false));
        animInfos.add(animInfo4);
        //第五组
        animInfo5.setNameInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_18.png", "空缺", true));
        animInfo5.setHeadImgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_22.png", null, true));
        animInfo5.setEnergyInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_17.png", "0", false));
        animInfo5.setEnergyIconInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_19.png", null, false));
        animInfo5.setAnimBgInfo(new TeamPkContribStarLottieEffectInfo.DetailInfo("img_48.png", null, false));
        animInfos.add(animInfo5);


        List<TeamEnergyAndContributionStarEntity.ContributionStar> starList = mData.getContributionStarList();
        // 绑定 学生信息
        if (starList != null && starList.size() > 0) {
            AnimInfo info;
            TeamEnergyAndContributionStarEntity.ContributionStar starInfo;
            for (int i = 0; i < starList.size(); i++) {
                info = animInfos.get(i);
                starInfo = starList.get(i);
                // 绑定头像信息
                info.getHeadImgInfo().setShow(true);
                info.getHeadImgInfo().setValue(starInfo.getAvaterPath());
                // 绑定名字信息
                info.getNameInfo().setShow(true);
                info.getNameInfo().setValue(TextUtils.isEmpty(starInfo.getRealname()) ? starInfo.getNickname() :
                        starInfo.getRealname());
                // 绑定 能量信息
                info.getEnergyInfo().setShow(true);
                info.getEnergyInfo().setValue("+" + starInfo.getEnergy());
                //绑定 能量icon信息
                info.getEnergyIconInfo().setShow(true);
                //绑定 旋转背景信息
                //学生自己是贡献之星
                info.getAnimBgInfo().setShow(starInfo.getStuId().equals(UserBll.getInstance().getMyUserInfoEntity()
                        .getStuId()));
            }
        }
        AnimInfo info;
        for (int i = 0; i < animInfos.size(); i++) {
            info = animInfos.get(i);
            effectInfo.addName(info.getNameInfo());
            effectInfo.addEnergy(info.getEnergyInfo());
            effectInfo.addEnergyIcon(info.getEnergyIconInfo());
            effectInfo.addHeadImg(info.getHeadImgInfo());
            effectInfo.addAnimBg(info.getAnimBgInfo());
        }
    }


    /**
     * 是否是强制提交
     *
     * @return
     */
    private boolean isForceSubmit() {

        return teamPkBll.getLatesH5CloseEvent() != null && teamPkBll.getLatesH5CloseEvent().isForceSubmit();
    }

    /**
     * 开始自动关闭
     */
    public void startAutoClose() {
        timeCountDowTextView.setTimeDuration(isForceSubmit()?3:5);
        timeCountDowTextView.setTimeSuffix("s后关闭");
        timeCountDowTextView.startCountDow();
        timeCountDowTextView.setTimeCountDowListener(new TimeCountDowTextView.TimeCountDowListener() {
            @Override
            public void onFinish() {
                closePager();
            }
        });
    }

    private void closePager() {
        try {
            releasRes();
            if (mView.getParent() != null) {
                teamPkBll.closeCurrentPager();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasRes() {
        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }
    }

    @Override
    public void initData() {

    }


    /**
     * @param resId
     * @param volume
     * @param loop
     */
    private void playMusic(int resId, final float volume, final boolean loop) {
        if (soundPoolHelper == null) {
            soundPoolHelper = new SoundPoolHelper(mContext, 2, AudioManager.STREAM_MUSIC);
        }
        soundPoolHelper.playMusic(resId, volume, loop);
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
        if (soundPoolHelper != null) {
            for (int i = 0; i < soundResArray.length; i++) {
                if (soundResArray[i] == R.raw.war_bg) {
                    soundPoolHelper.setVolume(soundResArray[i], DEFAULT_BG_VOLUME);
                } else {
                    soundPoolHelper.setVolume(soundResArray[i], DEFAULT_FRONT_VOLUME);
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        resumeMusic();
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseMusic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasRes();
    }
}
