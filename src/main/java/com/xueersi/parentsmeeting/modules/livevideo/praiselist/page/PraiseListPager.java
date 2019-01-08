package com.xueersi.parentsmeeting.modules.livevideo.praiselist.page;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.AutoVerticalScrollTextView;
import com.xueersi.ui.adapter.RCommonAdapter;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 *
 * 小理表扬榜
 */

public class PraiseListPager extends LiveBasePager {
    private PraiseListPresenter mPresenter;
    private ExcellentListEntity excellentListEntity;
    private LikeListEntity likeListEntity;
    private ProgressListEntity progressListEntity;

    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/";
    private static final int ANIMATOR_TYPE_MAIN = 1;
    private static final int ANIMATOR_TYPE_THANKS = ANIMATOR_TYPE_MAIN + 1;
    private static final int ANIMATOR_TYPE_TEACHER = ANIMATOR_TYPE_THANKS + 1;

    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    /**
     * 当前表扬榜类型
     */
    private int listType;
    public final static int PRAISE_LIST_TYPE_EXECELLENT = 1;//优秀榜
    public final static int PRAISE_LIST_TYPE_PROGRESS = 2;//进步榜
    public final static int PRAISE_LIST_TYPE_Like = 3;//点赞榜

    //主背景动画
    private LottieAnimationView lottieAnimationBGView;
    //主背景闪光循环动画
    private LottieAnimationView lottieAnimationLoopBGView;
    //感谢点赞
    private LottieAnimationView lottieAnimationThanksView;
    private View lottieAnimationThanksGroup;
    //老师表扬学生
    private LottieAnimationView lottieAnimationTeacherView;
    private View lottieAnimationTeacherGroup;
    private TextView teacherTipsView;
    private View contentGroup;
    /**
     * 金榜题名
     */
    private TextView tvOnList;
    /**
     * 表扬榜单
     */
    private RecyclerView rvPraiselist;
    /**
     * 点赞弹幕
     */
    private AutoVerticalScrollTextView tvDanmaku;
    /**
     * 点赞按钮
     */
    private Button btnLike;
    /**
     * 备注
     */
    private TextView tvNotes;
    /**
     * 我的姓名
     */
    private String mName;
    /**
     * 我是否上榜
     */
    private boolean isOnList = false;
    /**
     * 我在榜上的位置索引
     */
    private int listIndex = 0;
    /**
     * 给我点赞同学姓名
     */
    private ArrayList<String> nameList = new ArrayList<>();
    /**
     * 给我点赞数量
     */
    private ArrayList<Integer> numberList = new ArrayList<>();
    /**
     * 点赞文案
     */
    public String[] likeCopywriting;
    /**
     * 点赞文案索引
     */
    private ArrayList<Integer> likeCopywritingIndex = new ArrayList<>();
    /**
     * 点赞弹幕定时器
     */
    private Timer mTimer = null;
    /**
     * 点赞弹幕计数
     */
    private int danmakuCount = 0;
    /**
     * 点赞弹幕线程是否停止
     */
    private boolean isStop = true;
    /**
     * 声音池
     */
    private SoundPool mSoundPool;
    /**
     * 榜单弹出声音
     */
    private int soundPraiselistIn = 0;
    /**
     * 点赞声音
     */
    private int soundLike = 0;
    private LruCache<String, Bitmap> mBitmapCache;

    public PraiseListPager(Context context, ExcellentListEntity excellentListEntity, PraiseListPresenter presenter) {
        super(context);
        listType = PRAISE_LIST_TYPE_EXECELLENT;
        this.excellentListEntity = excellentListEntity;
        if (excellentListEntity != null && excellentListEntity.getIsMy() == 1) {
            isOnList = true;
        }
        this.mPresenter = presenter;
        initData();
    }

    public PraiseListPager(Context context, LikeListEntity likeListEntity, PraiseListPresenter presenter) {
        super(context);
        listType = PRAISE_LIST_TYPE_Like;
        this.likeListEntity = likeListEntity;
        if (likeListEntity != null && likeListEntity.getIsMy() == 1) {
            isOnList = true;
        }
        this.mPresenter = presenter;
        initData();
    }

    public PraiseListPager(Context context, ProgressListEntity progressListEntity, PraiseListPresenter presenter) {
        super(context);
        listType = PRAISE_LIST_TYPE_PROGRESS;
        this.progressListEntity = progressListEntity;
        if (progressListEntity != null && progressListEntity.getIsMy() == 1) {
            isOnList = true;
        }
        this.mPresenter = presenter;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_praiselist, null);

        lottieAnimationBGView = mView.findViewById(R.id.lav_livevideo_praise_pager_bg);
        lottieAnimationLoopBGView = mView.findViewById(R.id.lav_livevideo_praise_loop_pager_bg);
        lottieAnimationThanksView = mView.findViewById(R.id.lav_livevideo_praise_thanks);
        lottieAnimationThanksGroup = mView.findViewById(R.id.fl_livevideo_praise_thanks_group);
        lottieAnimationTeacherView = mView.findViewById(R.id.lav_livevideo_praise_teacher);
        lottieAnimationTeacherGroup = mView.findViewById(R.id.fl_livevideo_praise_teacher_group);
        teacherTipsView = mView.findViewById(R.id.tv_livevideo_praise_teacher_tips);
        contentGroup = mView.findViewById(R.id.rl_livevideo_praiselist_content);

        tvOnList = mView.findViewById(R.id.tv_livevideo_praiselist_tips);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvOnList.getLayoutParams();
        params.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 138));
        tvOnList.setLayoutParams(params);

        rvPraiselist = mView.findViewById(R.id.gv_livevideo_praiselist);
        rvPraiselist.addItemDecoration(new SpaceItemDecoration(SizeUtils.Dp2Px(mContext, 5)));
        rvPraiselist.setHasFixedSize(true);
        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_praise_student_list);
        rvPraiselist.setLayoutAnimation(animationController);
        rvPraiselist.scheduleLayoutAnimation();

        tvDanmaku =  mView.findViewById(R.id.tv_livevideo_praiselist_danmaku);
        btnLike =mView.findViewById(R.id.btn_livevideo_praise);
        tvNotes =  mView.findViewById(R.id.tv_livevideo_note);

        lottieAnimationBGView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                logger.i("lottieAnimationBGView: getMeasuredWidth() = " + lottieAnimationBGView.getMeasuredWidth());
                //计算列表的位置
                RelativeLayout.LayoutParams listParams = (RelativeLayout.LayoutParams) rvPraiselist
                        .getLayoutParams();
                if (isOnList) {
                    listParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 179));
                } else {
                    listParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 179 - 39));
                }
                if (listType == PRAISE_LIST_TYPE_Like) {
                    listParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 64));
                } else {
                    listParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 144));
                }
                listParams.leftMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 97));
                listParams.rightMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 88));
                rvPraiselist.setLayoutParams(listParams);

                //计算点赞区域的位置
                RelativeLayout.LayoutParams danmakuLayoutParams = (RelativeLayout.LayoutParams) tvDanmaku
                        .getLayoutParams();
                danmakuLayoutParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 271));
                danmakuLayoutParams.leftMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 83));
                danmakuLayoutParams.rightMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 168));
                tvDanmaku.setLayoutParams(danmakuLayoutParams);

                RelativeLayout.LayoutParams likeLayoutParams = (RelativeLayout.LayoutParams) btnLike
                        .getLayoutParams();
                likeLayoutParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 252));
                likeLayoutParams.leftMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 345));
                likeLayoutParams.rightMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 69));
                btnLike.setLayoutParams(likeLayoutParams);

                //计算备注的位置
                RelativeLayout.LayoutParams noteParams = (RelativeLayout.LayoutParams) tvNotes.getLayoutParams();
                noteParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 313));
                noteParams.leftMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 76));

                //计算老师点赞学生文字位置
                RelativeLayout.LayoutParams teacherTipsParams = (RelativeLayout.LayoutParams) teacherTipsView
                        .getLayoutParams();
                teacherTipsParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 236));
                teacherTipsParams.leftMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 109));
                teacherTipsParams.rightMargin = caculateHorizontalMargin(SizeUtils.Dp2Px(mContext, 105));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    lottieAnimationBGView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    lottieAnimationBGView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
//        preloadBitmap();
        return mView;
    }

    /**
     * 预加载一部分图片，避免动画太卡
     */
    private void preloadBitmap() {
        mBitmapCache = new LruCache<String, Bitmap>(10 * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        InputStream in = null;
        try {
            AssetManager assets = mContext.getAssets();
            String dir = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/images_advance";
            String[] list = assets.list(dir);
            for (int i = 0; i < list.length; i++) {
                in = mContext.getAssets().open(dir + File.separator + list[i]);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                mBitmapCache.put(list[i], bitmap);
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据效果图比例动态计算控件垂直方向边距
     *
     * @param rate
     * @return
     */
    private int caculateVerticalMargin(int rate) {
        int screenHeight = ScreenUtils.getScreenHeight();
        return (rate * screenHeight) / SizeUtils.Dp2Px(mContext, 375);
    }

    /**
     * 根据效果图比例动态计算控件水平方向边距
     *
     * @param rate
     * @return
     */
    private int caculateHorizontalMargin(int rate) {
        int measuredWidth = lottieAnimationBGView.getMeasuredWidth();
        return (rate * measuredWidth) / SizeUtils.Dp2Px(mContext, 500);
    }

    @Override
    public void initData() {
        //名字
        mName = mPresenter.getStuName();
        tvOnList.setText("恭喜 " + mName + " 同学金榜题名!");
        //名字缩写
        String abbName = mName;
        if (mName != null && mName.length() > 4) {
            abbName = mName.substring(0, 3) + "...";
        }
        likeCopywriting = new String[]{
                " 为你点赞，" + abbName + "学神~下次榜单再相见！",
                " 为你点赞，再接再厉哦，小学霸~",
                " 为你点赞，" + abbName + "好厉害，向你学习！",
                " 为你点赞，一起学习，一起进步",
                " 为你点赞，下次一定赶超你~",
                " 为你点赞，好羡慕能上榜~",
                " 为你点赞，" + abbName + "学神请接收我的膜拜",
                " 为你点赞，运气不错，额外获得<font color='#F13232'>1</font>颗赞哦~",
                " 为你点赞，运气爆棚，额外获得<font color='#F13232'>2</font>颗赞哦!"};

        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startBGAnimation();
            }
        }, 300);

        //播放声音
        if (mSoundPool == null)
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (soundPraiselistIn == 0) {
            soundPraiselistIn = mSoundPool.load(mContext, R.raw.praise_list, 1);
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    // TODO Auto-generated method stub
                    soundPool.play(soundPraiselistIn, 1, 1, 0, 0, 1);
                }
            });
        } else {
            mSoundPool.play(soundPraiselistIn, 1, 1, 0, 0, 1);
        }

        RCommonAdapter adapter = null;
        GridLayoutManager layoutManager = null;
        switch (listType) {
            case PRAISE_LIST_TYPE_EXECELLENT:
                tvNotes.setText("备注:全对或者订正到全对的同学可以上榜哦~");
                adapter = new RCommonAdapter(mContext, excellentListEntity.getStudentList());
                adapter.addItemViewDelegate(new ExcellentItem());
                layoutManager = new GridLayoutManager(mContext, 4);
                rvPraiselist.setLayoutManager(layoutManager);
                rvPraiselist.setAdapter(adapter);
                if (excellentListEntity.getPraiseStatus() != 0)
                    btnLike.setVisibility(View.INVISIBLE);

                if (excellentListEntity != null && excellentListEntity.getIsMy() == 1) {
                    tvOnList.setVisibility(View.VISIBLE);
                } else {
                    tvOnList.setVisibility(View.GONE);
                }
                break;
            case PRAISE_LIST_TYPE_Like:
                adapter = new RCommonAdapter(mContext, likeListEntity.getStudentList());
                adapter.addItemViewDelegate(new LikeItem());
                layoutManager = new GridLayoutManager(mContext, 3);
                rvPraiselist.setLayoutManager(layoutManager);
                rvPraiselist.setAdapter(adapter);
                if (likeListEntity != null && likeListEntity.getIsMy() == 1) {
                    tvOnList.setVisibility(View.VISIBLE);
                } else {
                    tvOnList.setVisibility(View.GONE);
                }
                tvDanmaku.setVisibility(View.GONE);
                btnLike.setVisibility(View.GONE);
                tvNotes.setVisibility(View.GONE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rvPraiselist.getLayoutParams();
                lp.setMargins(SizeUtils.Dp2Px(mContext, 20),
                        SizeUtils.Dp2Px(mContext, 53),
                        SizeUtils.Dp2Px(mContext, 20),
                        SizeUtils.Dp2Px(mContext, 24));
                break;
            case PRAISE_LIST_TYPE_PROGRESS:
                tvNotes.setText("备注:连续两次作业分数(百分制)有进步可以上榜哦~");
                adapter = new RCommonAdapter(mContext, progressListEntity.getProgressEntities());
                adapter.addItemViewDelegate(new ProgressItem());
                layoutManager = new GridLayoutManager(mContext, 3);
                rvPraiselist.setLayoutManager(layoutManager);
                rvPraiselist.setAdapter(adapter);
                if (progressListEntity.getPraiseStatus() != 0)
                    btnLike.setVisibility(View.INVISIBLE);

                if (progressListEntity != null && progressListEntity.getIsMy() == 1) {
                    tvOnList.setVisibility(View.VISIBLE);
                } else {
                    tvOnList.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
        //监听点赞按钮点击事件
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soundLike == 0) {
                    soundLike = mSoundPool.load(mContext, R.raw.thumbs_up, 1);
                    mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            // TODO Auto-generated method stub
                            soundPool.play(soundLike, 1, 1, 0, 0, 1);
                        }
                    });
                } else {
                    mSoundPool.play(soundLike, 1, 1, 0, 0, 1);
                }
                if (listType == PRAISE_LIST_TYPE_EXECELLENT)
                    mPresenter.getExcellentList(1);
                if (listType == PRAISE_LIST_TYPE_PROGRESS)
                    mPresenter.getProgressList(1);
                btnLike.setEnabled(false);
            }
        });
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
//            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) % 4 == 0) {
                outRect.left = 0;
            }
        }
    }

    /**
     * 计算点赞数量的规则
     */
    public int calculateLikeNum(LikeProbabilityEntity likeProbabilityEntity) {
        int likeNum = 1;
        int probability = likeProbabilityEntity.getProbability();
        Random random = new Random();
        int i;
        if (probability == 1) {
            //1：表示概率不加倍
            i = random.nextInt(9);
            if (i == 0) {
                likeNum = 2;
            } else {
                i = random.nextInt(19);
                if (i == 0)
                    likeNum = 3;
                else
                    likeNum = 1;
            }
        } else if (probability == 2) {
            //2：表示概率加倍
            i = random.nextInt(4);
            if (i == 0) {
                likeNum = 2;
            } else {
                i = random.nextInt(9);
                if (i == 0)
                    likeNum = 3;
                else
                    likeNum = 1;
            }
        }
        return likeNum;
    }

    /**
     * 开始整个背景动画
     */
    public void startBGAnimation() {
        logger.d("startBGAnimation");

        String advanceResPath = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/images_advance";
        String advanceJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/data.json";
        final LottieEffectInfo advanceEffectInfo = new LottieEffectInfo(advanceResPath, advanceJsonPath);

        String praiseResPath = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/images_praise";
        final LottieEffectInfo praiseEffectInfo = new LottieEffectInfo(praiseResPath, advanceJsonPath);

        String goodResPath = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/images_good";
        final LottieEffectInfo goodEffectInfo = new LottieEffectInfo(goodResPath, advanceJsonPath);

        final ArrayList<String> praiseDeleteRes = new ArrayList<>(3);
        praiseDeleteRes.add("img_11.png");
        praiseDeleteRes.add("img_12.png");

        final Map<String, String> praiseToAdvanceRes = new HashMap<>();
        praiseToAdvanceRes.put("img_6.png", "img_11.png");
        praiseToAdvanceRes.put("img_7.png", "img_12.png");
        praiseToAdvanceRes.put("img_8.png", "img_13.png");
        praiseToAdvanceRes.put("img_10.png", "img_15.png");
        praiseToAdvanceRes.put("img_13.png", "img_17.png");

        final Map<String, String> goodToAdvanceRes = new HashMap<>();
        goodToAdvanceRes.put("img_6.png", "img_11.png");
        goodToAdvanceRes.put("img_7.png", "img_13.png");
        goodToAdvanceRes.put("img_8.png", "img_12.png");
        goodToAdvanceRes.put("img_10.png", "img_15.png");

        lottieAnimationBGView.setAnimationFromJson(advanceEffectInfo.getJsonStrFromAssets(mContext), "praise_bg");
        lottieAnimationBGView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                //优秀榜
                if (listType == PRAISE_LIST_TYPE_EXECELLENT) {
                    if (goodToAdvanceRes.containsKey(fileName)) {
                        return goodEffectInfo.fetchBitmapFromAssets(lottieAnimationBGView, goodToAdvanceRes.get
                                        (fileName),
                                lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                                mContext);
                    }
                } else if (listType == PRAISE_LIST_TYPE_Like) {
                    if (praiseToAdvanceRes.containsKey(fileName)) {
                        return praiseEffectInfo.fetchBitmapFromAssets(lottieAnimationBGView, praiseToAdvanceRes.get
                                        (fileName),
                                lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                                mContext);
                    } else if (praiseDeleteRes.contains(fileName)) {
                        return null;
                    }
                }
                return advanceEffectInfo.fetchBitmapFromAssets(lottieAnimationBGView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        lottieAnimationBGView.setImageAssetDelegate(imageAssetDelegate);
        lottieAnimationBGView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                if (animatedFraction > 0.8) {
                    contentGroup.setVisibility(View.VISIBLE);
                }
            }
        });
        lottieAnimationBGView.addAnimatorListener(new BGAnimatorListener(ANIMATOR_TYPE_MAIN));
        lottieAnimationBGView.playAnimation();

        String loopResPath = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/images_advance";
        String loopJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "list_bg/data_loop.json";
        final LottieEffectInfo loopEffectInfo = new LottieEffectInfo(loopResPath, loopJsonPath);
        lottieAnimationLoopBGView.setAnimationFromJson(loopEffectInfo.getJsonStrFromAssets(mContext), "praise_loop_bg");
        lottieAnimationLoopBGView.useHardwareAcceleration(true);
        lottieAnimationLoopBGView.setRepeatCount(-1);
        lottieAnimationLoopBGView.setImageAssetDelegate(imageAssetDelegate);
        //截屏
        StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
        if (studyReportAction != null) {
            lottieAnimationLoopBGView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    logger.d("lottieAnimationLoopBGView:onAnimationStart");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    logger.d("lottieAnimationLoopBGView:onAnimationEnd");
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    lottieAnimationLoopBGView.removeAnimatorListener(this);
                    logger.d("lottieAnimationLoopBGView:onAnimationRepeat");
                    StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
                    if (studyReportAction != null && isOnList) {
                        if (listType == PRAISE_LIST_TYPE_EXECELLENT) {
                            studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_5, mView, false, false);
                        } else if (listType == PRAISE_LIST_TYPE_PROGRESS) {
                            studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_4, mView, false, false);
                        } else if (listType == PRAISE_LIST_TYPE_Like) {
                            studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_6, mView, false, false);
                        }
                    }
                }
            });
        }
    }

    /**
     * 谢谢点赞
     */
    private void startThanksBGAnimation() {
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "thanks/images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "thanks/data.json";
        final LottieEffectInfo lottieEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        lottieAnimationThanksView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
        lottieAnimationThanksView.useHardwareAcceleration(true);
        lottieAnimationThanksView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return lottieEffectInfo.fetchBitmapFromAssets(lottieAnimationThanksView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });

        lottieAnimationThanksView.addAnimatorListener(new BGAnimatorListener(ANIMATOR_TYPE_THANKS));
        lottieAnimationThanksView.playAnimation();
    }

    /**
     * 显示老师表扬横幅
     *
     * @param stuName
     * @param tecName
     */
    public void startScrollAnimation(String stuName, String tecName) {
        if (!isOnList)
            return;
        lottieAnimationTeacherGroup.setVisibility(View.VISIBLE);
        String student = stuName + "同学";
        String str1 = " 获得 ";
        String teacher = tecName + "老师";
        String str2 = " 的重点表扬,要努力继续上榜哦～";
        SpannableString spanText = new SpannableString(student + str1 + teacher + str2);
        spanText.setSpan(new AbsoluteSizeSpan(SizeUtils.Sp2Px(mContext, 18)), 0, student.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start = student.length() + str1.length();
        spanText.setSpan(new AbsoluteSizeSpan(SizeUtils.Sp2Px(mContext, 18)), start,
                start + teacher.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        teacherTipsView.setText(spanText);

        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "praise_teacher/images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "praise_teacher/data.json";
        final LottieEffectInfo lottieEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        lottieAnimationTeacherView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
        lottieAnimationTeacherView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return lottieEffectInfo.fetchBitmapFromAssets(lottieAnimationTeacherView, lottieImageAsset
                                .getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        });
        lottieAnimationTeacherView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                if (animatedFraction > 0.1 && teacherTipsView.getVisibility() == View.INVISIBLE) {
                    teacherTipsView.setVisibility(View.VISIBLE);
                }
                if (animatedFraction > 0.6) {
                    teacherTipsView.setVisibility(View.INVISIBLE);
                    lottieAnimationTeacherGroup.setVisibility(View.GONE);
                }
            }
        });
        lottieAnimationTeacherView.addAnimatorListener(new BGAnimatorListener(ANIMATOR_TYPE_TEACHER));
        lottieAnimationTeacherView.playAnimation();
    }

    /**
     * 动画监听不准确，屏幕上已经没有动画但是end方法过一段时间才能收到
     */
    class BGAnimatorListener implements Animator.AnimatorListener {

        private int mType;

        BGAnimatorListener(int type) {
            this.mType = type;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mType == ANIMATOR_TYPE_THANKS) {
                lottieAnimationThanksGroup.setVisibility(View.GONE);
            } else if (mType == ANIMATOR_TYPE_TEACHER) {
                lottieAnimationTeacherGroup.setVisibility(View.GONE);
            } else if (mType == ANIMATOR_TYPE_MAIN) {
                contentGroup.setVisibility(View.VISIBLE);
                lottieAnimationBGView.setVisibility(View.GONE);
                lottieAnimationLoopBGView.setVisibility(View.VISIBLE);
                //开启循环动画
                lottieAnimationLoopBGView.playAnimation();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

    }

    public void setLikeBtnEnabled(boolean enabled) {
        btnLike.setEnabled(enabled);
    }

    /**
     * 收到给我点赞的消息
     */
    public void receiveLikeNotice(ArrayList<String> nameList, LikeProbabilityEntity likeProbabilityEntity) {
        int nameListSize = this.nameList.size();
        int random;
        if (!isOnList)
            return;
        if (danmakuCount > this.nameList.size())
            danmakuCount = this.nameList.size();
        int totalNums = 0;
        for (int i = 0; i < nameList.size(); i++) {
            int likeNum = calculateLikeNum(likeProbabilityEntity);
            if (!nameList.get(i).equals(mName)) {
                //过滤掉自己和同名
                if (nameList.get(i).length() > 4) {
                    this.nameList.add(nameList.get(i).substring(0, 3) + "...");
                } else {
                    this.nameList.add(nameList.get(i));
                }
                if (likeNum == 1) {
                    random = new Random().nextInt(6);
                    this.likeCopywritingIndex.add(random);
                } else if (likeNum == 2) {
                    this.likeCopywritingIndex.add(7);
                } else if (likeNum == 3) {
                    this.likeCopywritingIndex.add(8);
                }
                this.numberList.add(likeNum);
            }
            totalNums += likeNum;
        }
        //计算点赞总数，发送至教师端
        mPresenter.sendLikeNum(totalNums);
        if (this.nameList.size() != 0 && this.nameList.size() > nameListSize)
            //如果给我点赞的同学的集合不为空，且数量增加，开启弹幕滚动
            startTimer();
    }

    class TanmakuTimerTask extends TimerTask {
        @Override
        public void run() {
            if (isStop)
                stopTimer();
            else {
                mWeakHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvDanmaku.next();
                        tvDanmaku.setText(Html.fromHtml(
                                "<font color='#F13232'>" + nameList.get(danmakuCount % nameList.size()) +
                                        "</font>" + likeCopywriting[likeCopywritingIndex.get(danmakuCount % nameList.size())]
                        ));
                        danmakuCount++;
                    }
                });
                if (nameList.size() == 1)
                    stopTimer();
            }
        }
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimer != null && isStop) {
            isStop = false;
            mTimer.schedule(new TanmakuTimerTask(), 0, 2000);
        }
    }

    private void stopTimer() {
        isStop = true;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void showLikeToast() {
        btnLike.setVisibility(View.INVISIBLE);
        lottieAnimationThanksGroup.setVisibility(View.VISIBLE);
        startThanksBGAnimation();
        mPresenter.sendLike();

        StableLogHashMap logHashMap = new StableLogHashMap("praisePraiseList");
        logHashMap.put("listtype", listType + "");
        logHashMap.put("stable", "2");
        logHashMap.put("expect", "1");
        logHashMap.put("sno", "5");
        logHashMap.put("ex", "Y");
        umsAgentDebugInter(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }

    /**
     * 优秀榜item
     */
    private class ExcellentItem implements RItemViewInterface<ExcellentListEntity.StudentEntity> {
        TextView tvName;
        TextView tvCounts;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_excellent;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            tvName = viewHolder.getView(R.id.tv_livevideo_praiselist_excellent_name);
            tvCounts = viewHolder.getView(R.id.tv_livevideo_praiselist_excellent_counts);
            tvCounts.setVisibility(View.GONE);
        }

        @Override
        public boolean isShowView(ExcellentListEntity.StudentEntity studentEntity, int i) {
            return true;
        }

        @Override
        public void convert(ViewHolder viewHolder, ExcellentListEntity.StudentEntity studentEntity, int i) {
            if (studentEntity != null) {
                String stuName = studentEntity.getStuName();
                if (!TextUtils.isEmpty(stuName)) {
                    if (stuName.length() >= 5) {
                        stuName = stuName.substring(0, 3) + "...";
                    }
                }
                tvName.setText(stuName);
            }
        }
    }

    /**
     * 点赞榜item
     */
    private class LikeItem implements RItemViewInterface<LikeListEntity.StudentEntity> {
        TextView tvName;
        TextView tvCounts;
        ImageView ivArrow;
        LinearLayout rootview;
        int tvNameWidth;
        int tvCountWidth;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_progress;
        }

        @Override
        public boolean isShowView(LikeListEntity.StudentEntity studentEntity, int i) {
            return true;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            rootview = (LinearLayout) viewHolder.getView(R.id.rl_livevideo_praiselist_progress_root);
            tvName = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_progress_name);
            tvCounts = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_progress_counts);
            ivArrow = (ImageView) viewHolder.getView(R.id.iv_livevideo_praiselist_progress_arrow);
            ivArrow.setImageResource(R.drawable.ic_livevideo_praiselist_praise);
            tvCountWidth = (int) Math.ceil(tvCounts.getPaint().measureText("999"));
            float nameWidth = tvName.getPaint().measureText("一二三...");
            tvNameWidth = (int) Math.ceil(nameWidth);
        }

        @Override
        public void convert(ViewHolder viewHolder, LikeListEntity.StudentEntity studentEntity, int i) {
            if ((i + 1) % 3 == 1) {
                rootview.setGravity(Gravity.LEFT);
            } else if ((i + 1) % 3 == 0) {
                rootview.setGravity(Gravity.RIGHT);
            } else {
                rootview.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            if (studentEntity != null) {
                String stuName = studentEntity.getStuName();
                if (!TextUtils.isEmpty(stuName)) {
                    if (stuName.length() >= 5) {
                        stuName = stuName.substring(0, 3) + "...";
                    }
                }
                tvName.setWidth(tvNameWidth);
                tvName.setText(stuName);

                tvCounts.setWidth(tvCountWidth);
                tvCounts.setText(String.valueOf(studentEntity.getStuPraiseNum()));

            }
        }
    }

    /**
     * 进步榜item
     */
    private class ProgressItem implements RItemViewInterface<ProgressListEntity.ProgressEntity> {
        TextView tvName;
        TextView tvCounts;
        ImageView ivArrow;
        LinearLayout rootview;
        int tvNameWidth;
        int tvCountWidth;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_progress;
        }

        @Override
        public boolean isShowView(ProgressListEntity.ProgressEntity progressEntity, int i) {
            return true;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            rootview = (LinearLayout) viewHolder.getView(R.id.rl_livevideo_praiselist_progress_root);
            tvName = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_progress_name);
            tvCounts = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_progress_counts);
            ivArrow = (ImageView) viewHolder.getView(R.id.iv_livevideo_praiselist_progress_arrow);
            tvCountWidth = (int) Math.ceil(tvCounts.getPaint().measureText("100分"));
            float nameWidth = tvName.getPaint().measureText("一二三...");
            tvNameWidth = (int) Math.ceil(nameWidth);
        }

        @Override
        public void convert(ViewHolder viewHolder, ProgressListEntity.ProgressEntity progressEntity, int i) {
            if ((i + 1) % 3 == 1) {
                rootview.setGravity(Gravity.LEFT);
            } else if ((i + 1) % 3 == 0) {
                rootview.setGravity(Gravity.RIGHT);
            } else {
                rootview.setGravity(Gravity.CENTER_HORIZONTAL);
            }

            if (progressEntity != null) {
                String stuName = progressEntity.getStuName();
                if (!TextUtils.isEmpty(stuName)) {
                    if (stuName.length() >= 5) {
                        stuName = stuName.substring(0, 3) + "...";
                    }
                }
                tvName.setWidth(tvNameWidth);
                tvName.setText(stuName);

                String score = progressEntity.getProgressScore() + "分";
                tvCounts.setWidth(tvCountWidth);
                tvCounts.setText(score);
            }
        }
    }

    public void stopDanmaku() {
        this.isStop = true;
    }

    public void releaseSoundPool() {
        if (mSoundPool != null)
            mSoundPool.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDanmaku();
        releaseSoundPool();
    }
}
