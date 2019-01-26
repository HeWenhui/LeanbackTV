package com.xueersi.parentsmeeting.modules.livevideo.chpk.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.business.ChinesePkBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CoinAwardDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkRecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    private CoinAwardDisplayer cadTeamCoin;
    /**
     * 战队获得 ai 碎片
     */
    private CoinAwardDisplayer cadTeamPatch;

    private LottieAnimationView lottieAnimationView;
    private TeamPkRecyclerView recyclerView;
    /**
     * lottie 可点击区域
     */
    private Rect mClickAbleRect;

    private ImageView ivBgMask;
    private WinnerAdapter mAdapter;
    private boolean startShowWinner;
    private CoinAwardDisplayer cadMyCoin;

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
    private RelativeLayout rlLuckyStartRoot;
    private SoundPoolHelper soundPoolHelper;

    int[] soundResArray = {
            R.raw.war_bg,
            R.raw.box_open
    };
    private ImageView ivClose;
    private LinearLayout llAipatnerAwardRoot;
    private LinearLayout llTeamCoinContainer;
    private int spanCount;


    public PkOpenAwardPager(Context context, ChinesePkBll pkBll) {
        super(context);
        this.pkBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chpk_openaward, null);
        rlLuckyStartRoot = view.findViewById(R.id.rl_teampk_open_box_lucy_start_root);
        cadTeamCoin = view.findViewById(R.id.cad_teampk_open_box_team_coin);
        cadTeamPatch = view.findViewById(R.id.cad_teampk_open_box_team_patch);
        llTeamCoinContainer = view.findViewById(R.id.ll_teampk_open_box_team_coin_container);

        cadMyCoin = view.findViewById(R.id.cad_teampk_open_box_my_coin);
        llAipatnerAwardRoot = view.findViewById(R.id.ll_teampk_aipartner_award_root);

        recyclerView = view.findViewById(R.id.rcl_teampk_open_box_rank);
        lottieAnimationView = view.findViewById(R.id.lav_teampk_open_box);
        ivOpenState = view.findViewById(R.id.iv_teampk_open_box_open_state);
        ivOpenState.setVisibility(View.GONE);
        ivBgMask = view.findViewById(R.id.iv_teampk_open_box_bg_mask);
        ivBgMask.setVisibility(View.GONE);
        ivClose = view.findViewById(R.id.iv_teampk_open_box_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAwardPager();
            }
        });

        lottieAnimationView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                if (event.getAction() == MotionEvent.ACTION_UP && mClickAbleRect != null) {
                    return !mClickAbleRect.contains(x, y);
                }

                return false;
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
     * 初始化 宝箱可点击范围
     *
     * @param width
     * @param height
     */
    private void initClickAbleRect(int width, int height) {
        int screenWidth = getRootView().getMeasuredWidth();
        int screenHeight = getRootView().getMeasuredHeight();
        int lef = (screenWidth - width) / 2;
        int top = (screenHeight - height) / 2;

        int right = lef + width;
        int bottom = top + height;
        mClickAbleRect = new Rect(lef, top, right, bottom);
    }

    /**
     * 日志埋点所需参数
     */
    private String logUUID;


    /**
     * 显示开宝箱动画
     */
    public void showBoxLoop() {
        mIsWin = pkBll.isWin();

        final String targetImgName = "img_0.png";

        ivClose.setVisibility(View.VISIBLE);
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int realY = Math.min(point.x, point.y);
        int topMargin = (int) (realY * 0.8f);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivOpenState.getLayoutParams();
        layoutParams.topMargin = topMargin;
        ivOpenState.setVisibility(View.VISIBLE);

        ivOpenState.setImageResource(pkBll.isAIPartner() ? R.drawable.livevideo_get_award : R.drawable.live_video_get_coin);
        String lottiePath = mIsWin ? "chinesePk/largebox" : "chinesePk/smallbox";
        final String lottieResPath = lottiePath + "/images/";
        final String lottieJsonPath = lottiePath + "/data.json";

        // step 0 播放背景音效
        playMusic(R.raw.war_bg, DEFAULT_BG_VOLUME, true);
        // step 1 展示背景遮罩
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim.anim_livevido_teampk_bg_mask);
        alphaAnimation.setFillAfter(true);
        ivBgMask.setVisibility(View.VISIBLE);
        ivBgMask.startAnimation(alphaAnimation);

        // step 2 展示lottie 动画
        lottieAnimationView.setImageAssetsFolder(lottieResPath);

        //设置循环播放
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

        lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                if (targetImgName.equals(asset.getFileName()) && mClickAbleRect == null) {
                    initClickAbleRect(asset.getWidth(), asset.getHeight());
                }

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

        final Runnable action = new Runnable() {
            @Override
            public void run() {
                if (pkBll != null) {
                    logUUID = StableLogHashMap.creatNonce();
                }

                //防止快速 连续点击
                lottieAnimationView.setClickable(false);
                getStuChestInfo();
            }
        };

        lottieAnimationView.postDelayed(action, 5 * 1000);

        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieAnimationView.removeCallbacks(action);

                if (pkBll != null) {
                    logUUID = StableLogHashMap.creatNonce();
                }

                //防止快速 连续点击
                lottieAnimationView.setClickable(false);
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
                lottieAnimationView.setClickable(true);

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
                    Point point = new Point();
                    ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
                    int realY = Math.min(point.x, point.y);
                    int topMargin = (int) (realY * 0.8f);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivOpenState.getLayoutParams();
                    layoutParams.topMargin = topMargin;
                    ivOpenState.setVisibility(View.VISIBLE);
                    ivOpenState.setImageResource(R.drawable.livevideo_alertview_kaiguo_img_disable);
                    ivOpenState.setLayoutParams(layoutParams);
                }

                if (pkBll != null) {
                    pkBll.updatePkStateLayout(false);
//                    TeamPkLog.openTreasureBox(pkBll.getLiveBll(), studentChestEntity.getGold() + "", logUUID, true);
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                lottieAnimationView.setClickable(true);
                if (pkBll != null) {
//                    TeamPkLog.openTreasureBox(pkBll.getLiveBll(), "", logUUID, false);
                }
                showToast("获取宝箱数据失败");
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                if (pkBll != null) {
//                    TeamPkLog.openTreasureBox(pkBll.getLiveBll(), "", logUUID, false);
                }

                String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "获取宝箱数据失败" :
                        responseEntity.getErrorMsg();
                showToast(errorMsg);
                lottieAnimationView.setClickable(true);
            }
        };

        pkBll.getmHttpManager().getCHStuChest(mIsWin ? 1 : 0,
                pkBll.getRoomInitInfo().getStudentLiveInfo().getClassId(),
                pkBll.getRoomInitInfo().getStudentLiveInfo().getTeamId(),
                pkBll.getRoomInitInfo().getStuId(),
                pkBll.getLiveBll().getLiveId(),
                pkBll.isAIPartner(),
                callback);

    }

    /**
     * 播放开宝箱动画
     */
    private void startOpenBoxAnim(String lottieResPath, String lottieJsonPath) {
        // 清空listener
        lottieAnimationView.setImageAssetDelegate(null);
        lottieAnimationView.setOnClickListener(null);
        lottieAnimationView.setOnTouchListener(null);
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setRepeatCount(0);
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
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
        lottieAnimationView.setImageAssetDelegate(null);
        lottieAnimationView.setOnClickListener(null);
        lottieAnimationView.setOnTouchListener(null);
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

    }


    private void showAwardDetail(StudentChestEntity studentChestEntity) {
        //展示 获得金币数
        int gold = studentChestEntity.getGold();
        int patch = studentChestEntity.getChipNum();
        ivOpenState.setVisibility(View.GONE);
        Animation alphaAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_open_box_coin_in);

        if (pkBll.isAIPartner()) {
            cadMyCoin.setVisibility(View.GONE);
            llAipatnerAwardRoot.setVisibility(View.VISIBLE);
            // 展示碎片信息
            TextView tvPatch = llAipatnerAwardRoot.findViewById(R.id.tv_teampk_aipartner_award_patch);
            TextView tvPatchName = llAipatnerAwardRoot.findViewById(R.id.tv_teampk_aipartner_award_patchname);

            // 显示 碎片图片
            ImageView ivPatch = llAipatnerAwardRoot.findViewById(R.id.iv_teampk_aipatner_chip);
            ImageLoader.with(BaseApplication.getContext()).load(studentChestEntity.getChipUrl()).into(ivPatch);

            tvPatch.setVisibility(View.VISIBLE);
            TextView tvRemind = llAipatnerAwardRoot.findViewById(R.id.tv_teampk_aipartner_award_remind);
            tvRemind.setVisibility(View.VISIBLE);
            tvPatch.setText("+" + patch);
            tvPatchName.setText("（" + studentChestEntity.getChipName() + "）");
            // 展示金币信息
            TextView tvCoin = llAipatnerAwardRoot.findViewById(R.id.tv_teampk_aipartner_award_coin);
            tvCoin.setVisibility(View.VISIBLE);
            tvCoin.setText("+" + gold);
            llAipatnerAwardRoot.startAnimation(alphaAnimation);
        } else {
            cadMyCoin.setVisibility(View.VISIBLE);
            llAipatnerAwardRoot.setVisibility(View.GONE);
            cadMyCoin.setAwardInfo(R.drawable.livevideo_alertview_tosmoke_img_disable, gold,
                    R.drawable.livevideo_alertview_goldwenzi_img_disable);
            cadMyCoin.startAnimation(alphaAnimation);
        }
    }

    /**
     * 显示班级获奖列表
     *
     * @param data
     */
    public void showClassChest(ClassChestEntity data) {
        classChestEntity = data;

        mIsWin = pkBll.isWin();

        showWinners();
    }

    private void showWinners() {
        ivClose.setVisibility(View.GONE);
        if (ivBgMask.getVisibility() != View.VISIBLE) {
            ivBgMask.setVisibility(View.VISIBLE);
        }
        if (classChestEntity == null) {
            return;
        }
        if (cadMyCoin.getParent() != null) {
            ((ViewGroup) cadMyCoin.getParent()).removeView(cadMyCoin);
        }
        if (llAipatnerAwardRoot.getParent() != null) {
            ((ViewGroup) llAipatnerAwardRoot.getParent()).removeView(llAipatnerAwardRoot);
        }


        playMusic(R.raw.war_bg, DEFAULT_BG_VOLUME, true);

        String lottiePath = mIsWin ? "chinesePk/retlargeboxopen" : "chinesePk/retsmallboxopen";

        // step 1 展示lottie
        String imgDir = lottiePath + "/images";
        String jsonPath = lottiePath + "/data.json";
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setImageAssetDelegate(null);
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.setImageAssetsFolder(imgDir);
        lottieAnimationView.setRepeatCount(0);
        LottieComposition.Factory.fromAssetFileName(mContext, jsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

        final float frationShowDetail = 0.7f;
        lottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!startShowWinner && animation.getAnimatedFraction() > frationShowDetail) {
                    startShowWinner = true;
                    showDetailInfo(classChestEntity);
                }
            }
        });


        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimationView.postDelayed(new Runnable() {
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
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setImageAssetDelegate(null);
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.setRepeatCount(-1);
        lottieAnimationView.setImageAssetsFolder(imgDir);

        LottieComposition.Factory.fromAssetFileName(mContext, jsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

    }

    private void showDetailInfo(ClassChestEntity data) {
        if (data == null) {
            return;
        }

        rlLuckyStartRoot.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlLuckyStartRoot.getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int realY = Math.min(point.x, point.y);
        int topMargin = (int) (realY * 0.36);
        layoutParams.topMargin = topMargin;
        rlLuckyStartRoot.setLayoutParams(layoutParams);

        // step 2 展示 获得金币信息
        if (pkBll.isAIPartner()) {
            cadTeamPatch.setVisibility(View.VISIBLE);
            cadTeamCoin.setVisibility(View.VISIBLE);
            cadTeamCoin.setAwardInfo(R.drawable.livevideo_alertview_guafen_img_disable, (int) data.getSumGold(), R.drawable.livevideo_aipatner_coinsuffix);
            cadTeamPatch.setAwardInfo(-1, (int) data.getSumChip(), R.drawable.livevideo_aipatner_suffix_patch);

        } else {
            cadTeamCoin.setVisibility(View.VISIBLE);
            cadTeamPatch.setVisibility(View.GONE);
            cadTeamCoin.setAwardInfo(R.drawable.livevideo_alertview_guafen_img_disable, (int) data.getSumGold(), R.drawable.livevideo_alertview_gegoldwenzi_img_disable);
        }
        Animation alphaAnimation = AnimationUtils.loadAnimation(mContext, R.anim
                .anim_livevideo_teampk_open_box_coin_in);
        llTeamCoinContainer.startAnimation(alphaAnimation);

        // step 3 展示队员信息
        spanCount = pkBll.isAIPartner() ? 2 : 3;
        recyclerView.setLayoutManager(new TeamMemberGridlayoutManager(mContext, spanCount,
                LinearLayoutManager.VERTICAL, false));
        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        recyclerView.setLayoutAnimation(animationController);
        if (mAdapter == null) {
            mAdapter = new WinnerAdapter(data.getSubChestEntityList(), pkBll.isAIPartner());
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.getData().clear();
            mAdapter.getData().addAll(data.getSubChestEntityList());
            mAdapter.notifyDataSetChanged();
        }
        recyclerView.scheduleLayoutAnimation();

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition >= spanCount) {
                    top = SizeUtils.Dp2Px(mContext, 5);
                }
                outRect.set(left, top, right, bottom);
            }
        });

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

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        ImageView ivLuckyStar;
        TextView tvName;
        TextView tvCoin;
        TextView tvPatch;
        ImageView ivChip;

        public ItemHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_teampk_open_box_winner_head);
            ivLuckyStar = itemView.findViewById(R.id.iv_teampk_open_box_lucky_guy);
            tvName = itemView.findViewById(R.id.tv_teampk_open_box_winner_name);
            tvCoin = itemView.findViewById(R.id.tv_teampk_open_box_winner_coin);
            tvPatch = itemView.findViewById(R.id.tv_teampk_lucky_start_patch);
            ivChip = itemView.findViewById(R.id.iv_teampk_aipatner_chip);
        }

        public void bindData(ClassChestEntity.SubChestEntity data, int postion) {
            ImageLoader.with(BaseApplication.getContext()).load(data.getAvatarPath())
                    .placeHolder(R.drawable.livevideo_list_headportrait_ic_disable)
                    .asBitmap(new SingleConfig.BitmapListener() {
                        @Override
                        public void onSuccess(Drawable drawable) {
                            Bitmap resultBitmap = null;
                            if (drawable instanceof BitmapDrawable) {
                                resultBitmap = ((BitmapDrawable) drawable).getBitmap();
                            } else if (drawable instanceof GifDrawable) {
                                resultBitmap = ((GifDrawable) drawable).getFirstFrame();
                            }
                            if (resultBitmap != null) {
                                Bitmap circleBitmap = scaleBitmap(resultBitmap, Math.min(resultBitmap.getWidth(),
                                        resultBitmap.getHeight()) / 2);
                                ivHead.setImageBitmap(circleBitmap);
                            }
                        }

                        @Override
                        public void onFail() {
                        }
                    });
            ivLuckyStar.setVisibility(postion <= 4 ? View.VISIBLE : View.GONE);
            tvName.setText(data.getStuName());
            tvCoin.setText("+" + data.getGold());
            if (tvPatch != null) {
                tvPatch.setText("+" + data.getChipNum());
            }
            if (ivChip != null) {
                ImageLoader.with(BaseApplication.getContext()).load(data.getChipUrl()).into(ivChip);
            }
        }
    }

    static class WinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ClassChestEntity.SubChestEntity> mData;
        private boolean isAiPatner;

        WinnerAdapter(List<ClassChestEntity.SubChestEntity> data, boolean isAiPatner) {
            this.mData = data;
            this.isAiPatner = isAiPatner;
        }

        public List<ClassChestEntity.SubChestEntity> getData() {
            return mData;
        }

        public void setData(List<ClassChestEntity.SubChestEntity> data) {
            this.mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (isAiPatner) {
                return new ItemHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_teampk_open_box_aipatnerwinner, parent, false));
            } else {
                return new ItemHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_teampk_open_box_winner, parent, false));
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemHolder) holder).bindData(mData.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
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
