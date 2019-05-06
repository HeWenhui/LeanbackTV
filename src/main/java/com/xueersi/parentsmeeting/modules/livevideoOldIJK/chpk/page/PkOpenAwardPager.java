package com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.adapter.WinnerAdapter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.business.ChinesePkBll;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.widget.AwardNumberView;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentChestEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkRecyclerView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;

/**
 * 开宝箱页面
 *
 * @author yuanwei
 *         <p>
 *         created  at 2018/11/14 11:31
 */
public class PkOpenAwardPager extends BasePager {
    private static final String TAG = "TeamPkAwardPager";
    Logger loger = LoggerFactory.getLogger(TAG);
    private AwardNumberView awardTeamNumber;
    private LottieAnimationView lottieEffectView;
    private ImageView awardClickImage;
    private TeamPkRecyclerView recyclerView;

    private WinnerAdapter mAdapter;
    private boolean startShowWinner;
    private AwardNumberView awardUserNumber;

    /**
     * 开宝箱结果展示时间
     */
    private static final long TIME_DELAY_SHOW_WINNER = 5 * 1000;
    /**
     * 榜单显示时间
     */
    private static final long TIME_DELAY_AUTO_FINISH = 10 * 1000;

    /**
     * 默认背景音效大小
     */
    private static final float DEFAULT_BG_VOLUME = 0.4f;
    /**
     * 默认前景音效大小
     */
    private static final float DEFAULT_FRONT_VOLUME = 0.6f;

    private ClassChestEntity classChestEntity;
    private final ChinesePkBll pkBll;
    private boolean mIsWin;
    private ImageView ivOpenState;
    private SoundPoolHelper soundPoolHelper;

    int[] soundResArray = {
            R.raw.war_bg,
            R.raw.box_open
    };
    private ImageView ivClose;

    public PkOpenAwardPager(Context context, ChinesePkBll pkBll) {
        super(context);
        this.pkBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chpk_openaward, null);
        awardTeamNumber = view.findViewById(R.id.av_livevideo_chpk_awardTeamNumber);
        awardUserNumber = view.findViewById(R.id.av_livevideo_chpk_awardUserNumber);
        recyclerView = view.findViewById(R.id.iv_livevideo_chpk_openAwardRanker);
        awardClickImage = view.findViewById(R.id.iv_livevideo_chpk_awardClickImage);
        lottieEffectView = view.findViewById(R.id.lv_livevideo_chpk_lottieEffectView);
        ivOpenState = view.findViewById(R.id.iv_livevideo_chpk_openAwardState);
        ivOpenState.setVisibility(View.GONE);

        GridLayoutManager manager = new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = SizeUtils.Dp2Px(mContext, 5);
            }
        });

        ivClose = view.findViewById(R.id.iv_livevideo_chpk_openAwardClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAwardPager();
            }
        });

        return view;
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

    @Override
    public void initData() {
    }

    /**
     * 关闭 宝箱展示页面
     */
    public void closeAwardPager() {
        releaseRes();
        pkBll.closeCurrentPager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRes();
    }

    private void releaseRes() {
        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }
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
                    soundPoolHelper.setVolume(soundResArray[i], DEFAULT_BG_VOLUME);
                } else {
                    soundPoolHelper.setVolume(soundResArray[i], DEFAULT_FRONT_VOLUME);
                }

            }
        }
    }

    /**
     * 显示开宝箱动画
     */
    public void showBoxLoop() {
        mIsWin = pkBll.isWin();
        ivClose.setVisibility(View.VISIBLE);
        ivOpenState.setVisibility(View.VISIBLE);
        ivOpenState.setImageResource(R.drawable.livevideo_clickboxgetgold);

        String lottiePath = mIsWin ? "chinesePk/largebox" : "chinesePk/smallbox";
        final String lottieResPath = lottiePath + "/images/";
        final String lottieJsonPath = lottiePath + "/data.json";

        // step 0 播放背景音效
        playMusic(R.raw.war_bg, DEFAULT_BG_VOLUME, true);

        // step 2 展示lottie 动画
        lottieEffectView.setImageAssetsFolder(lottieResPath);

        //设置循环播放
        lottieEffectView.setRepeatCount(LottieDrawable.INFINITE);

        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieEffectView.setComposition(lottieComposition);
                lottieEffectView.playAnimation();
            }
        });

        lottieEffectView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                Bitmap result = null;
                try {
                    InputStream in = mContext.getAssets().open(lottieResPath + asset.getFileName());
                    result = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        });

        awardClickImage.setVisibility(View.VISIBLE);
        awardClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止快速 连续点击
                awardClickImage.setClickable(false);
                getStuChestInfo();
            }
        });

    }

    /**
     * 获取学生宝箱信息
     */
    private void getStuChestInfo() {

        HttpCallBack callback = new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                awardClickImage.setClickable(true);
                awardClickImage.setVisibility(View.GONE);

                String lottiePath = mIsWin ? "chinesePk/largeboxopen" : "chinesePk/smallboxopen";

                //播放动画
                String lottieResPath = lottiePath + "/images";
                String lottieJsonPath = lottiePath + "/data.json";

                startOpenBoxAnim(lottieResPath, lottieJsonPath);

                StudentChestEntity studentChestEntity = pkBll.getmHttpResponseParser().parseStuChest(responseEntity);
                String strUnGetGoldState = "0";

                if (strUnGetGoldState.equals(studentChestEntity.getIsGet())) {
                    showAwardDetail(studentChestEntity);
                } else {
                    ivOpenState.setVisibility(View.VISIBLE);
                    ivOpenState.setImageResource(R.drawable.livevideo_chpk_openalready);
                }

                if (pkBll != null) {
                    pkBll.updatePkStateLayout(false);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                awardClickImage.setClickable(true);
                showToast("获取宝箱数据失败");
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "获取宝箱数据失败" : responseEntity.getErrorMsg();
                showToast(errorMsg);
                awardClickImage.setClickable(true);
            }
        };

        pkBll.requestStuChest(mIsWin ? 1 : 0, callback);

    }

    /**
     * 播放开宝箱动画
     */
    private void startOpenBoxAnim(String lottieResPath, String lottieJsonPath) {
        // 清空listener
        lottieEffectView.setImageAssetDelegate(null);
        lottieEffectView.cancelAnimation();
        lottieEffectView.setRepeatCount(0);
        lottieEffectView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieEffectView.setComposition(lottieComposition);
                lottieEffectView.playAnimation();
            }
        });

        lottieEffectView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 播放开宝箱音效
                playMusic(R.raw.box_open, DEFAULT_FRONT_VOLUME, false);
                String lottiePath = mIsWin ? "chinesePk/largeboxdone" : "chinesePk/smallboxdone";

                //开启背景循环动效
                String lottieResPath = lottiePath + "/images";
                String lottieJsonPath = lottiePath + "/data.json";
                showAfterOpenAnim(lottieResPath, lottieJsonPath);
            }
        });
    }

    /**
     * @param lottieResPath
     * @param lottieJsonPath
     */
    private void showAfterOpenAnim(String lottieResPath, String lottieJsonPath) {
        lottieEffectView.setImageAssetDelegate(null);
        lottieEffectView.cancelAnimation();
        lottieEffectView.setRepeatCount(LottieDrawable.INFINITE);

        lottieEffectView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieEffectView.setComposition(lottieComposition);
                lottieEffectView.playAnimation();
            }
        });

        //不再自动关闭
        lottieEffectView.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeAwardPager(); //自动关闭
            }
        }, TIME_DELAY_SHOW_WINNER);

    }


    private void showAwardDetail(StudentChestEntity studentChestEntity) {
        //展示 获得金币数
        int gold = studentChestEntity.getGold();
        ivOpenState.setVisibility(View.GONE);
        Animation alphaAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_open_box_coin_in);

        awardUserNumber.setVisibility(View.VISIBLE);
        awardUserNumber.setGoldNumber(gold, R.drawable.livevideo_chpk_usercoinleft, R.drawable.livevideo_chpk_usercoinright);
        awardUserNumber.startAnimation(alphaAnimation);
    }

    /**
     * 显示班级获奖列表
     *
     * @param data
     */
    public void showClassChest(ClassChestEntity data) {
        classChestEntity = data;

        mIsWin = pkBll.isWin();

        ivClose.setVisibility(View.GONE);

        if (classChestEntity == null) {
            return;
        }

        awardUserNumber.setVisibility(View.GONE);
        playMusic(R.raw.war_bg, DEFAULT_BG_VOLUME, true);

        String lottiePath = mIsWin ? "chinesePk/retlargeboxopen" : "chinesePk/retsmallboxopen";

        // step 1 展示lottie
        String imgDir = lottiePath + "/images";
        String jsonPath = lottiePath + "/data.json";
        lottieEffectView.cancelAnimation();
        lottieEffectView.setImageAssetDelegate(null);
        lottieEffectView.removeAllAnimatorListeners();
        lottieEffectView.setImageAssetsFolder(imgDir);
        lottieEffectView.setRepeatCount(0);
        LottieComposition.Factory.fromAssetFileName(mContext, jsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieEffectView.setComposition(lottieComposition);
                lottieEffectView.playAnimation();
            }
        });

        final float frationShowDetail = 0.7f;
        lottieEffectView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!startShowWinner && animation.getAnimatedFraction() > frationShowDetail) {
                    startShowWinner = true;
                    showDetailInfo(classChestEntity);
                }
            }
        });


        lottieEffectView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieEffectView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTopLoopAnim();
                    }
                }, 500);
            }
        });

    }


    private void startTopLoopAnim() {

        String lottiePath = mIsWin ? "chinesePk/retlargeboxdone" : "chinesePk/retsmallboxdone";

        // step 1 展示lottie
        String imgDir = lottiePath + "/images";
        String jsonPath = lottiePath + "/data.json";
        lottieEffectView.cancelAnimation();
        lottieEffectView.setImageAssetDelegate(null);
        lottieEffectView.removeAllAnimatorListeners();
        lottieEffectView.setRepeatCount(-1);
        lottieEffectView.setImageAssetsFolder(imgDir);

        LottieComposition.Factory.fromAssetFileName(mContext, jsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieEffectView.setComposition(lottieComposition);
                lottieEffectView.playAnimation();
            }
        });

    }

    private void showDetailInfo(ClassChestEntity data) {
        if (data == null) {
            return;
        }

        awardTeamNumber.setVisibility(View.VISIBLE);
        awardTeamNumber.setGoldNumber((int) data.getSumGold(), R.drawable.livevideo_chpk_teamcoinleft, R.drawable.livevideo_chpk_teamcoinright);

        GridLayoutAnimationController animationController = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        recyclerView.setLayoutAnimation(animationController);

        if (mAdapter == null) {
            mAdapter = new WinnerAdapter(data.getSubChestEntityList());
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.getData().clear();
            mAdapter.getData().addAll(data.getSubChestEntityList());
            mAdapter.notifyDataSetChanged();
        }
        recyclerView.scheduleLayoutAnimation();

        Runnable action = new Runnable() {
            @Override
            public void run() {
                int itemCount = mAdapter.getItemCount();
                if (itemCount > 0) {
                    recyclerView.smoothScrollToPosition(itemCount - 1);
                }

            }
        };

        recyclerView.postDelayed(action, 2000);

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
                if (studyReportAction != null) {
                    studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_PK_GOLD, mView, false, false);
                }
                closeAwardPager();
            }
        }, TIME_DELAY_AUTO_FINISH);
    }


}
