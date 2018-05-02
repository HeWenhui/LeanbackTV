package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.sobot.chat.utils.ToastUtil;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SoundInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CoinAwardDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkRecyclerView;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by chenkun on 2018/4/12
 * 开宝箱页面
 */
public class TeamPkAwardPager extends BasePager {
    private static final String TAG = "TeamPkAwardPager";
    private CoinAwardDisplayer cadTeamCoin;
    private LottieAnimationView lottieAnimationView;
    private TeamPkRecyclerView recyclerView;
    private Rect mClickAbleRect;   // lottie 可点击区域

    private String lottieResDir = "team_pk/award/small_box";
    private ImageView ivBgMask;
    private WinnerAdapter adapter;
    private boolean startShowWinner;
    private CoinAwardDisplayer cadMycoin;

    private static final long TIME_DELAY_SHOW_WINNER = 5 * 1000; //进入
    private static final long TIME_DELAY_AUTO_FINISH = 10 * 1000; // 榜单显示时间
    private SoundPool soundPool;
    private static final int SOUND_TYPE_BG = 1; //背景音效
    private static final int SOUND_TYPE_BOX_OPEN = 2; //宝箱打开音效
    private HashMap<Integer, SoundInfo> mSoundInfoMap;
    private static final int DEFAULT_BG_VOLUME = 4;     //默认背景音效大小
    private static final int DEFAULT_FRONT_VOLUME = 6;  //默认前景音效大小
    private ClassChestEntity classChestEntity;
    private final TeamPKBll teamPKBll;
    private boolean mIsWin;
    private ImageView ivOpenstate;


    public TeamPkAwardPager(Context context, TeamPKBll pkBll) {
        super(context);
        teamPKBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_awardget, null);
        cadTeamCoin = view.findViewById(R.id.cad_teampk_open_box_team_coin);
        cadMycoin = view.findViewById(R.id.cad_teampk_open_box_my_coin);
        recyclerView = view.findViewById(R.id.rcl_teampk_open_box_rank);
        lottieAnimationView = view.findViewById(R.id.lav_teampk_open_box);
        ivOpenstate = view.findViewById(R.id.iv_teampk_open_box_open_state);
        ivOpenstate.setVisibility(View.GONE);
        ivBgMask = view.findViewById(R.id.iv_teampk_open_box_bg_mask);
        ivBgMask.setVisibility(View.GONE);
        addInputEventInterceptor();
        return view;
    }

    /**
     * 显示开宝箱动画
     * @param isWin
     */
    public void showBoxLoop(boolean isWin) {
        mIsWin = isWin;
        if (isWin) {
            lottieResDir = "team_pk/award/big_box";
        } else {
            lottieResDir = "team_pk/award/small_box";
        }
        String lottieResPath = lottieResDir + "_loop/images/";
        String lottieJsonPath = lottieResDir + "_loop/data.json";
        startBoxLoopAnim(lottieResPath, lottieJsonPath);
    }


    // 对lottie 拦截点击事件
    private void addInputEventInterceptor() {
        lottieAnimationView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        mClickAbleRect != null) {
                    return !mClickAbleRect.contains((int) event.getX(), (int) event.getY());
                }
                return false;
            }
        });
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
                playMusic(SOUND_TYPE_BOX_OPEN, R.raw.box_open, DEFAULT_FRONT_VOLUME, false);
                //开启背景循环动效
                String lottieResPath = lottieResDir + "_after_open/images";
                String lottieJsonPath = lottieResDir + "_after_open/data.json";
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
        lottieAnimationView.setRepeatCount(-1);

        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

        lottieAnimationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeAwardPager(); //自动关闭
            }
        }, TIME_DELAY_SHOW_WINNER);
    }


    private void showWinners() {
        if(ivBgMask.getVisibility() != View.VISIBLE){
            ivBgMask.setVisibility(View.VISIBLE);
        }
        if (classChestEntity == null) {
            return;
        }
        if (cadMycoin.getParent() != null) {
            ((ViewGroup) cadMycoin.getParent()).removeView(cadMycoin);
        }

        playMusic(SOUND_TYPE_BG, R.raw.war_bg, DEFAULT_BG_VOLUME, true);

        // step 1 展示lottie
        String imgDir = lottieResDir + "_top_open/images";
        String jsonPath = lottieResDir + "_top_open/data.json";
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

        lottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!startShowWinner && animation.getAnimatedFraction() > 0.7) {
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

        // step 1 展示lottie
        String imgDir = lottieResDir + "_top_loop/images";
        String jsonPath = lottieResDir + "_top_loop/data.json";
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
        // step 2 展示 获得金币信息
        cadTeamCoin.setVisibility(View.VISIBLE);
        Animation alphaAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_open_box_coin_in);
        cadTeamCoin.startAnimation(alphaAnimation);
        // step 3 展示队员信息
        recyclerView.setLayoutManager(new TeamMemberGridlayoutManager(mContext, 3,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setVisibility(View.VISIBLE);
        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        recyclerView.setLayoutAnimation(animationController);
        if (adapter == null) {
            adapter = new WinnerAdapter(data.getSubChestEntityList());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.getData().clear();
            adapter.getData().addAll(data.getSubChestEntityList());
            adapter.notifyDataSetChanged();
        }
        recyclerView.scheduleLayoutAnimation();

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeAwardPager();
            }
        }, TIME_DELAY_AUTO_FINISH);
    }

    /**
     * 关闭 宝箱展示页面
     */
    public void closeAwardPager() {
        releaseRes();
        if (getRootView().getParent() != null) {
            ((ViewGroup) getRootView().getParent()).removeView(getRootView());
        }
    }


    /**
     * @param soundType
     * @param resId
     * @param volume
     * @param loop
     */
    private void playMusic(final int soundType, int resId, final int volume, final boolean loop) {
        if (soundPool == null) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        if (mSoundInfoMap == null) {
            mSoundInfoMap = new HashMap<Integer, SoundInfo>();
        }
        soundPool.load(mContext, resId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                int streamId = soundPool.play(sampleId, volume, volume, 0, loop ? -1 : 0, 1);
                SoundInfo soundInfo = mSoundInfoMap.get(soundType);
                if (soundInfo == null) {
                    soundInfo = new SoundInfo(sampleId, streamId);
                    mSoundInfoMap.put(soundType, soundInfo);
                } else {
                    soundInfo.setStreamId(streamId);
                }
            }
        });
    }

    /**
     * 播放宝箱 循环抖动 动画
     *
     * @param lottieResPath
     * @param lottieJsonPath
     */
    private void startBoxLoopAnim(final String lottieResPath, String lottieJsonPath) {
        // step 0 播放背景音效
        playMusic(SOUND_TYPE_BG, R.raw.war_bg, DEFAULT_BG_VOLUME, true);
        // step 1 展示背景遮罩
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_bg_mask);
        alphaAnimation.setFillAfter(true);
        ivBgMask.setVisibility(View.VISIBLE);
        ivBgMask.startAnimation(alphaAnimation);

        // step 2 展示lottie 动画
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        lottieAnimationView.setRepeatCount(-1); //设置循环播放
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

        lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                if (lottieImageAsset.getFileName().equals("img_0.png") && mClickAbleRect == null) {
                    initClickAbleRect(lottieImageAsset.getWidth(), lottieImageAsset.getHeight());
                }
                Bitmap reusltBitmap = null;
                InputStream in = null;
                try {
                    in = mContext.getAssets().open(lottieResPath + lottieImageAsset.getFileName());
                    reusltBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return reusltBitmap;
            }
        });

        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStuChestInfo();
                String lottieResPath = lottieResDir + "_open/images";
                String lottieJsonPath = lottieResDir + "_open/data.json";
                startOpenBoxAnim(lottieResPath, lottieJsonPath);
            }
        });
    }

    /**
     * 获取学生宝箱信息
     */
    private void getStuChestInfo() {
        teamPKBll.getmHttpManager().getStuChest(mIsWin?1:0, teamPKBll.getRoomInitInfo().getStudentLiveInfo().getClassId()
                , teamPKBll.getRoomInitInfo().getStudentLiveInfo().getTeamId(),
                teamPKBll.getRoomInitInfo().getStuId(), teamPKBll.getmLiveBll().getLiveId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        StudentChestEntity studentChestEntity = teamPKBll.getmHttpResponseParser().parseStuChest(responseEntity);
                        if(studentChestEntity.getIsGet().equals("0")){
                            //展示 获得金币数
                            cadMycoin.setVisibility(View.VISIBLE);
                            try {
                                int gold =  Integer.parseInt( studentChestEntity.getGold()) ;
                                cadMycoin.setAwardInfo(R.drawable.livevideo_alertview_tosmoke_img_disable, gold,
                                        R.drawable.livevideo_alertview_goldwenzi_img_disable);
                                Animation alphaAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_open_box_coin_in);
                                cadMycoin.startAnimation(alphaAnimation);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                          //  ToastUtil.showToast(mContext,"您已经领过宝箱了");
                            ivOpenstate.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }
                });
    }


    /**
     * 初始化 宝箱可点击范围
     *
     * @param width
     * @param height
     */
    private void initClickAbleRect(int width, int height) {
        int screenWidth = this.getRootView().getMeasuredWidth();
        int screenHeight = this.getRootView().getMeasuredHeight();
        int lef = (screenWidth - width) / 2;
        int top = (screenHeight - height) / 2;
        int right = lef + width;
        int bottom = top + height;
        mClickAbleRect = new Rect(lef, top, right, bottom);
    }

    @Override
    public void initData() {
        Loger.e(TAG, "======> initData called");
    }

    /**
     * 显示班级获奖列表
     *
     * @param data
     * @param isWin 是否获胜
     */
    public void showClassChest(ClassChestEntity data, boolean isWin) {
        classChestEntity = data;
        mIsWin = isWin;
        if (isWin) {
            lottieResDir = "team_pk/award/big_box";
        } else {
            lottieResDir = "team_pk/award/small_box";
        }
        showWinners();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        ImageView ivLuckyStar;
        TextView tvName;
        TextView tvCoin;

        public ItemHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_teampk_open_box_winner_head);
            ivLuckyStar = itemView.findViewById(R.id.iv_teampk_open_box_lucky_guy);
            tvName = itemView.findViewById(R.id.tv_teampk_open_box_winner_name);
            tvCoin = itemView.findViewById(R.id.tv_teampk_open_box_winner_coin);
        }

        public void bindData(ClassChestEntity.SubChestEntity data, int postion) {
            ImageLoader.with(ivHead.getContext()).load(data.getAvatarPath())
                    .placeHolder(R.drawable.livevideo_list_headportrait_ic_disable)
                    .asBitmap(new SingleConfig.BitmapListener() {
                        @Override
                        public void onSuccess(Drawable drawable) {
                            Bitmap resultBitmap = ((BitmapDrawable) drawable).getBitmap();
                            Bitmap circleBitmap = scaleBitmap(resultBitmap, Math.min(resultBitmap.getWidth(), resultBitmap.getHeight()) / 2);
                            ivHead.setImageBitmap(circleBitmap);
                        }

                        @Override
                        public void onFail() {
                        }
                    });
            ivLuckyStar.setVisibility(postion <= 4 ? View.VISIBLE : View.GONE);
            tvName.setText(data.getStuName());
            tvCoin.setText("+"+data.getGold());
        }
    }

    static class WinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ClassChestEntity.SubChestEntity> data;

        WinnerAdapter(List<ClassChestEntity.SubChestEntity> data) {
            this.data = data;
        }

        public List<ClassChestEntity.SubChestEntity> getData() {
            return data;
        }

        public void setData(List<ClassChestEntity.SubChestEntity> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_teampk_open_box_winner, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemHolder) holder).bindData(data.get(position), position);
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            if (data != null) {
                itemCount = data.size();
            }
            return itemCount;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRes();
    }

    private void releaseRes() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (mSoundInfoMap != null) {
            mSoundInfoMap.clear();
            mSoundInfoMap = null;
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