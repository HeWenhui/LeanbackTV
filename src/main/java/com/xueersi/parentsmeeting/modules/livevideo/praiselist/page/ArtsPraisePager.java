package com.xueersi.parentsmeeting.modules.livevideo.praiselist.page;

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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsPraiseLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsRraiseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.ArtsPraiseListBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PraiseBtnAnimLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PraiseView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PriaseRecyclerView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;

import java.math.BigDecimal;
import java.util.List;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2018/7/12 下午2:32
 */

public class ArtsPraisePager extends BasePager {

    private static final String TAG = "ArtsPraisePager";
    private static final int SPAN_COUNT = 3;
    private static final float RECYCLVIEW_ENTER_FRACTION = 0.47f;

    private static final int TEN_THOUSAND = 10000;
    private static final int HUNDRED = 100;

    /**
     * 背景音乐 音量
     */
    private static final float MUSIC_VOLUME_RATIO_BG = 0.3f;
    /**
     * 前景音效 音量
     */
    private static final float MUSIC_VOLUME_RATIO_FRONT = 0.8f;

    /**
     * 显示榜单后  关闭按钮延时显示
     */
    private static final long DELAY_SHOW_CLOSEBTN_MS = 10000;

    /**
     * 鼓励榜 *
     */
    private static final int PRAISE_TYPE_PRAISE = 1;

    /**
     * 敦促榜
     */
    private static final int PRAISE_TYPE_ENCOURAGE = 2;
    private static final float SCALE_ANIM_FACTOR = 0.23f;

    private static final String LOTTIE_RES_ASSEST_ROOTDIR = "arts_praise_list/";
    private final ArtsPraiseListBll mPraiseListBll;
    private LottieAnimationView enterAnimationView;
    private PriaseRecyclerView recyclerView;
    private ArtsPraiseLottieEffectInfo effectInfo;
    private boolean loopanimstarted;
    private PraiseView praiseView;
    private FrameLayout praiseLayout;
    private PraiseListAdapter adapter;
    private SoundPoolHelper soundPoolHelper;
    private int mPraiseType;
    private ImageView ivCloseBtn;

    private int[] soundResArray = {
            R.raw.like_btn_click,
            R.raw.praise_show,
            R.raw.encourage_show,
            R.raw.close_btn_click,
            R.raw.encourage_show,
            R.raw.teacher_saying_show
    };
    private TextView tvMsg;
    private TextView tvTeacherName;
    private LinearLayout titleContainer;
    private ImageView ivTeahcerHead;
    private RelativeLayout praiseMsgLayout;
    private ArtsPraiseLottieEffectInfo loopEffectInfo;
    private LottieAnimationView loopAnimationView;
    private TextView tvPraiseNum;
    private PraiseBtnAnimLayout praiseBtnAnimLayout;
    private ArtsRraiseEntity mData;

    /**
     * 当前总点赞数
     */
    private long mCurrentNum;
    private TeamMemberGridlayoutManager prasieLayoutManager;
    private float mLayoutScale = 1.0f;
    private int mAddNum;


    public ArtsPraisePager(Context context, ArtsPraiseListBll artsPraiseListBll, ArtsRraiseEntity data) {
        super(context);
        mPraiseListBll = artsPraiseListBll;
        mData = data;
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_arts_praiselist, null);
        enterAnimationView = view.findViewById(R.id.lt_livevideo_arts_praiselist);
        //设置硬件加速
        enterAnimationView.useHardwareAcceleration();
        tvPraiseNum = view.findViewById(R.id.tv_livevideo_arts_prasise_praisenum);


        soundPoolHelper = new SoundPoolHelper(mContext, 5, AudioManager.STREAM_MUSIC);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0) {
                    disPlayPraisUI();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        return view;
    }


    private void disPlayPraisUI() {
        if (mData != null) {
            if (mData.getRankType() == PRAISE_TYPE_PRAISE) {
                mPraiseType = PRAISE_TYPE_PRAISE;
                showEnterAnim();
                mView.setBackgroundResource(R.drawable.livevideo_arts_praise_praise_bg);
            } else if (mData.getRankType() == PRAISE_TYPE_ENCOURAGE) {
                mPraiseType = PRAISE_TYPE_ENCOURAGE;
                showEnterAnim();
                mView.setBackgroundResource(R.drawable.livevideo_arts_praise_encourage_bg);
            }
            mPraiseListBll.upLoadLog("ArtsPraise","ArtsPraisePager_disPlayPraiseUI_type_"+mPraiseType);
        }
    }


    private void showPraiseList() {

        if (recyclerView == null) {

            mPraiseListBll.upLoadLog("ArtsPraise","ArtsPraisePager_showPraiseList");

            recyclerView = mView.findViewById(R.id.rcl_livevideo_arts_praiselist);
            recyclerView.setVisibility(View.VISIBLE);
            Point point = new Point();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
            int realY = Math.min(point.x, point.y);
            int screenHeight = Math.max(point.x,point.y);
            int screenWidth = Math.min(point.x,point.y);

            float screenRatio = screenHeight/(screenWidth*1.0f);
            float standerRation = 16.0f/9.0f;

            // 全面屏
            if(screenRatio > standerRation){
                mLayoutScale = screenHeight / (screenWidth*standerRation);
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
            int newWithd = (int) (layoutParams.width * mLayoutScale);
            layoutParams.width = newWithd;
            layoutParams.topMargin = (int) (realY * 0.33);
            Loger.e("ArtsPraisePager","====> rootView measueredWith="+mView.getMeasuredWidth());
            layoutParams.leftMargin = (int) (mView.getMeasuredWidth() * 0.245f *mLayoutScale);
            recyclerView.setLayoutParams(layoutParams);
            prasieLayoutManager = new TeamMemberGridlayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(prasieLayoutManager);
            adapter = new PraiseListAdapter(mData.getRankEntities());
            recyclerView.setAdapter(adapter);

           /* GridLayoutAnimationController animationController = (GridLayoutAnimationController) AnimationUtils.
                    loadLayoutAnimation(mContext, R.anim.anim_livevido_arts_praiselist);
            recyclerView.setLayoutAnimation(animationController);
            recyclerView.scheduleLayoutAnimation(); // 无淡入效果
            */

            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int itemPosition = parent.getChildAdapterPosition(view);
                    int left = 0;
                    int right = 0;
                    int top = 0;
                    int bottom = 0;
                    if (itemPosition >= SPAN_COUNT) {
                        top = SizeUtils.Dp2Px(mContext, 12);
                    }
                    outRect.set(left, top, right, bottom);
                }
            });


            initPraiseBtn(realY);

            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (recyclerView.getMeasuredWidth() > 0) {
                        scrollToTargetPosition(recyclerView);
                        //显示 老师鼓励语 动画
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showTeacherSaying();
                            }
                        }, 2500);
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                            recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });

            initCloseBnt();
            //处理全面屏
            recyclerView.setScaleX(mLayoutScale);
            recyclerView.setScaleY(mLayoutScale);
        }
    }


    /**
     * 展示老师鼓励文案
     */
    private void showTeacherSaying() {

        praiseMsgLayout = mView.findViewById(R.id.rl_lievideo_arts_praise_msg);
        praiseMsgLayout.setVisibility(View.VISIBLE);

        praiseMsgLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_artspraise_praise_text);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.4f));

        titleContainer = praiseMsgLayout.findViewById(R.id.ll_livevideo_arts_praise_teacherhead_container);
        ivTeahcerHead = praiseMsgLayout.findViewById(R.id.iv_livevideo_arts_praise_teacherhead);
        tvTeacherName = praiseMsgLayout.findViewById(R.id.tv_livevideo_arts_praise_teachername);
        tvMsg = praiseMsgLayout.findViewById(R.id.tv_livevideo_arts_praise_praisemsg);
        if (PRAISE_TYPE_PRAISE == mPraiseType) {
            titleContainer.setBackgroundResource(R.drawable.livevideo_artspraise_teacherhead_praise_bg);
            tvMsg.setBackgroundResource(R.drawable.livevideo_artspraise_praise_pop_bg);
            tvMsg.setTextColor(Color.parseColor("#FD6368"));
        } else if (PRAISE_TYPE_ENCOURAGE == mPraiseType) {
            titleContainer.setBackgroundResource(R.drawable.livevideo_artspraise_teacherhead_encourage_bg);
            tvMsg.setBackgroundResource(R.drawable.livevideo_artspraise_encourage_pop_bg);
            tvMsg.setTextColor(Color.parseColor("#2B74A9"));
        }
        String teacherName = TextUtils.isEmpty(mData.getCounselorName()) ? "" : mData.getCounselorName() + "老师对你说:";
        String pariseWord = TextUtils.isEmpty(mData.getWord()) ? "" : mData.getWord();
        tvTeacherName.setText(teacherName);
        tvMsg.setText(pariseWord);
        titleContainer.startAnimation(scaleAnimation);
        mPraiseListBll.upLoadLog("ArtsPraise",
                "ArtsPraisePager_showTeacherSaying_teacherName_"+teacherName+"_praiseWord_"
                        +pariseWord+"_counselorAvatar_"+mData.getCounselorAvatar());
        ImageLoader.with(ivTeahcerHead.getContext()).load(mData.getCounselorAvatar()).asBitmap
                (new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap headBitmap = DrawableHelper.drawable2bitmap(drawable);
                        Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap
                                .getHeight()) / 2);
                        ivTeahcerHead.setImageBitmap(resultBitmap);
                    }

                    @Override
                    public void onFail() {
                        ivTeahcerHead.setImageResource(R.drawable.livevideo_arts_praise_default_head);
                    }
                });


        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPraiseText();
            }

        }, 700);

        playTeacherSayingSound();

    }


    private void showPraiseText() {
        mPraiseListBll.upLoadLog("ArtsPraise","ArtsPraisePager_showPraiseText");
        tvMsg.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(mContext, R.anim
                .anim_livevido_teampk_aq_award);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.4f));
        tvMsg.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hidePraiseText();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 隐藏 老师鼓励语
     */
    private void hidePraiseText() {

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (praiseMsgLayout != null) {
                    praiseMsgLayout.setVisibility(View.GONE);
                    mPraiseListBll.upLoadLog("ArtsPraise","ArtsPraisePager_hidePraiseText");
                }
            }
        }, 5000);

    }


    private Runnable closePagerTask = new Runnable() {
        @Override
        public void run() {
            closePager();
        }
    };

    private Runnable showCloseBntTask = new Runnable() {
        @Override
        public void run() {

            ivCloseBtn = mView.findViewById(R.id.iv_livevideo_arts_prasie_close);
            ivCloseBtn.setVisibility(View.VISIBLE);

            ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                    loadAnimation(mContext, R.anim.anim_livevido_artspraise_praise_text);
            scaleAnimation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
            ivCloseBtn.startAnimation(scaleAnimation);

            ivCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playClostBtnSound();
                    // 音效播放完毕 关闭按钮
                    mView.removeCallbacks(closePagerTask);
                    mView.postDelayed(closePagerTask, 500);
                }
            });
        }
    };

    private void initCloseBnt() {

        mView.postDelayed(showCloseBntTask, DELAY_SHOW_CLOSEBTN_MS);

    }


    private void initPraiseBtn(int realY) {
        praiseLayout = mView.findViewById(R.id.fl_livevideo_arts_praise_layout);
        praiseLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) praiseLayout.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = (int) (realY * 0.135);
        if(mView.getMeasuredWidth() > 0){
            params.width = (int) (mView.getMeasuredWidth() * 0.23f *mLayoutScale);
        }
        praiseLayout.setLayoutParams(params);
        praiseView = praiseLayout.findViewById(R.id.prsv_livevideo_arts_praise);

        mView.findViewById(R.id.iv_livevideo_arts_praise_praise_btn_bg)
                .setBackgroundResource(mPraiseType == PRAISE_TYPE_PRAISE ? R.drawable.livevideo_arts_praise_praise_btn :
                        R.drawable.livevideo_arts_praise_encourage_btn);

        Loger.e("ArtsPraisePager", "======>initPraiseBtn called:" + mCurrentNum);
        upDatePraiseNum(mCurrentNum, false);

        praiseLayout.findViewById(R.id.fl_livevideo_arts_praise_scaleanimlayout).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                // step 1 playmusic
                playPraiseBtnClickSound();
                // step 2
                if (mPraiseListBll != null) {
                    mPraiseListBll.sendPraiseNotice();
                }
                mTotalNum++;
                mCurrentNum++;
                upDatePraiseNum(mCurrentNum, true);
            }
        });

        // 点赞按钮 进场动画
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_artspraise_praise_text);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(SCALE_ANIM_FACTOR));
        praiseBtnAnimLayout = praiseLayout.findViewById(R.id.fl_livevideo_arts_praise_scaleanimlayout);
        praiseBtnAnimLayout.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                praiseBtnAnimLayout.startAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void playPraiseBtnClickSound() {
        soundPoolHelper.playMusic(R.raw.like_btn_click, MUSIC_VOLUME_RATIO_FRONT, false);
    }


    /**
     * 播放表扬音效
     */
    private void playPraiseShowMusic() {
        int soundResId = mPraiseType == PRAISE_TYPE_PRAISE ? R.raw.praise_show : R.raw.encourage_show;
        soundPoolHelper.playMusic(soundResId, MUSIC_VOLUME_RATIO_FRONT, false);

    }


    /**
     * 老师鼓励语 音效
     */
    private void playTeacherSayingSound() {

        soundPoolHelper.playMusic(R.raw.teacher_saying_show, MUSIC_VOLUME_RATIO_FRONT, false);

    }


    private void playClostBtnSound() {

        soundPoolHelper.playMusic(R.raw.close_btn_click, MUSIC_VOLUME_RATIO_FRONT, false);

    }


    /**
     * 滑动到目标位置
     *
     * @param recyclerView
     */
    private void scrollToTargetPosition(final PriaseRecyclerView recyclerView) {

        if (recyclerView.canScrollVertically(1)) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(getScrollToPosition());
                }
            }, 1500);
        }

    }

    /**
     * 滑动到目标位置
     *
     * @return
     */
    private int getScrollToPosition() {
        int postion = getUserPraiseListIndex();
        int firstVisiablePosition = prasieLayoutManager.findFirstVisibleItemPosition();
        if (postion < 0) {
            postion = adapter.getItemCount() - 1;
        }else{
            int lastVisiablePosition = prasieLayoutManager.findLastCompletelyVisibleItemPosition();
            int visibaleItemCount = lastVisiablePosition - firstVisiablePosition;
            //不在首屏 列表中
            if(postion > lastVisiablePosition){
                postion += 2 *SPAN_COUNT;
            }else{
                postion = lastVisiablePosition +(2 - (lastVisiablePosition-postion)%SPAN_COUNT) * SPAN_COUNT;
            }
            if(postion < 0){
                postion = 0;
            }
            if(postion >adapter.getItemCount() -1 ){
                postion = adapter.getItemCount() -1;
            }
        }
        return postion;
    }

    /**
     * 获取当前用户在表扬榜中的  位置
     *
     * @return 不在列表中返回 -1
     */
    private int getUserPraiseListIndex() {
        int result = -1;
        if (mData != null && mData.getRankEntities() != null) {
            for (int i = 0; i < mData.getRankEntities().size(); i++) {
                if (mData.getRankEntities().get(i).getInList() == 1) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }


    @Override
    public void initData() {
    }


    /**
     * 更新 当前点赞数 任务
     */
     private Runnable upDatePraiseNumTask = new Runnable() {
         @Override
         public void run() {
             long newCurrentNum = mAddNum+mCurrentNum;
             if(newCurrentNum < mTotalNum ){
                 upDatePraiseNum(newCurrentNum,true);
                 mView.postDelayed(this,UPDATE_PRAISE_NUM_DELAY);
             }else{
                 newCurrentNum = newCurrentNum>mTotalNum?mTotalNum:newCurrentNum;
                 upDatePraiseNum(newCurrentNum,true);
             }
         }
     };


     private long mTotalNum;
     /**自动更新点赞数时间间隔*/
     private static final long UPDATE_PRAISE_NUM_DELAY = 500;
     /**递增次数*/
     private static final int ADD_TIMES = (int) (2000 /  UPDATE_PRAISE_NUM_DELAY);
    /**
     * 更新当前点赞数
     * @param praiseNum 当前点赞总数
     */
     public void upDatePraiseNum(long praiseNum){
        // Loger.e("ArtsPraisePager","======>upDatePraiseNum:"+praiseNum+":"+mCurrentNum+":"+mTotalNum);
         if(mCurrentNum >0){
             if(praiseNum > mTotalNum && praiseNum > 0){
                 // step 1 cancle task
                 mView.removeCallbacks(upDatePraiseNumTask);
                 // step 2 jump to last totalNum;
                 upDatePraiseNum(mTotalNum,false);
                 // step 3 resart Task
                 mTotalNum = praiseNum;
                 mAddNum = (int) Math.ceil((mTotalNum - mCurrentNum)*1.0d/ADD_TIMES*1.0d);
                 if(mAddNum > 0){
                     mView.post(upDatePraiseNumTask);
                 }
             }
         }else{
             //首次初始化
             mCurrentNum = praiseNum;
             mTotalNum = praiseNum;
             upDatePraiseNum(mCurrentNum, false);
             //Loger.e("ArtsPraisePager","======>upDatePraiseNum 222222222:"+praiseNum+":"+mCurrentNum+":"+mTotalNum);
         }
     }

    /**
     * 更新点赞数
     * @param praiseNum
     * @param withAnim  是否显示动画
     */
    private void upDatePraiseNum(long praiseNum, boolean withAnim) {
        if (praiseLayout != null && praiseLayout.getVisibility() == View.VISIBLE ) {
            Loger.e("ArtsPraisePager", "======>upDatePraiseNum called:" + praiseNum + ":" + withAnim);
            if (withAnim) {
                praiseView.addHeart();
            }
            StringBuilder sb = new StringBuilder();
            if (praiseNum > TEN_THOUSAND) {
                if (praiseNum % TEN_THOUSAND >= HUNDRED) {
                    BigDecimal bigDecimal = new BigDecimal(praiseNum / 10000.0f);
                    sb.append(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append("万");
                } else {
                    sb.append(praiseNum / TEN_THOUSAND).append("万");
                }
            } else {
                sb.append(praiseNum);
            }
            tvPraiseNum.setText(sb.toString());
            mCurrentNum = praiseNum;
            Loger.e("ArtsPraisePager", "======>upDatePraiseNum setText:"+mCurrentNum);

        }
    }


    /**
     * 展示进场动效
     *
     */
    private void showEnterAnim() {
        String imgPath = null;
        if (PRAISE_TYPE_PRAISE == mPraiseType) {
            imgPath = LOTTIE_RES_ASSEST_ROOTDIR + "praise/images";
            String jsonFilePath = LOTTIE_RES_ASSEST_ROOTDIR + "praise/enter.json";
            effectInfo = new ArtsPraiseLottieEffectInfo(imgPath, jsonFilePath, "img_0.png");
        } else if (PRAISE_TYPE_ENCOURAGE == mPraiseType) {
            imgPath = LOTTIE_RES_ASSEST_ROOTDIR + "encourage/images";
            String jsonFilePath = LOTTIE_RES_ASSEST_ROOTDIR + "encourage/enter.json";
            effectInfo = new ArtsPraiseLottieEffectInfo(imgPath, jsonFilePath, "img_0.png");
        }
        mPraiseListBll.upLoadLog("ArtsPraise","ArtsPraisePager_showEnterAnim_rankTitel_"+mData.getRankTitle());

        effectInfo.setTitle(mData.getRankTitle());
        enterAnimationView.setImageAssetsFolder(imgPath);
        enterAnimationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
        enterAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(enterAnimationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });

        enterAnimationView.playAnimation();
        enterAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showLoopAnim();
            }
        });


        enterAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() >= RECYCLVIEW_ENTER_FRACTION) {
                    showPraiseList();
                }
            }
        });

        playPraiseShowMusic();
    }

    /**
     * 展示 动效
     *
     */
    private void showLoopAnim() {

        if (!loopanimstarted) {
            loopanimstarted = true;
            loopAnimationView = mView.findViewById(R.id.lt_livevideo_arts_praise_loop);
            loopAnimationView.useHardwareAcceleration();
            String imgPath = null;
            loopEffectInfo = null;
            if (PRAISE_TYPE_PRAISE == mPraiseType) {
                imgPath = LOTTIE_RES_ASSEST_ROOTDIR + "praise/images";
                String jsonFilePath = LOTTIE_RES_ASSEST_ROOTDIR + "praise/loop.json";
                loopEffectInfo = new ArtsPraiseLottieEffectInfo(imgPath, jsonFilePath, "img_0.png");
            } else if (PRAISE_TYPE_ENCOURAGE == mPraiseType) {
                imgPath = LOTTIE_RES_ASSEST_ROOTDIR + "encourage/images";
                String jsonFilePath = LOTTIE_RES_ASSEST_ROOTDIR + "encourage/loop.json";
                loopEffectInfo = new ArtsPraiseLottieEffectInfo(imgPath, jsonFilePath, "img_0.png");
            }
            loopEffectInfo.setTitle(mData.getRankTitle());
            loopAnimationView.setAnimationFromJson(loopEffectInfo.getJsonStrFromAssets(mContext));
            final ArtsPraiseLottieEffectInfo finalLoopEffectInfo = loopEffectInfo;
            loopAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return loopEffectInfo.fetchBitmapFromAssets(loopAnimationView, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            mContext);
                }
            });

            loopAnimationView.setRepeatCount(-1);
            loopAnimationView.playAnimation();
            loopAnimationView.setVisibility(View.INVISIBLE);

            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loopAnimationView.setVisibility(View.VISIBLE);
                    enterAnimationView.removeAllAnimatorListeners();
                    enterAnimationView.cancelAnimation();
                    ((ViewGroup) mView).removeView(enterAnimationView);
                }
            }, 500);
        }
    }


    static class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_livevideo_arts_praiselist_name);
        }

        public void bindData(ArtsRraiseEntity.RankEntity data) {
            tvName.setTextColor(data.getInList() == 1 ? Color.parseColor("#FFBC2D") : Color.parseColor("#707070"));
            tvName.setText(data.getRealName());
        }
    }

    static class PraiseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ArtsRraiseEntity.RankEntity> reankData;

        public PraiseListAdapter(List<ArtsRraiseEntity.RankEntity> data) {
            reankData = data;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_live_arts_priase, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemHolder) holder).bindData(reankData.get(position));
        }

        @Override
        public int getItemCount() {
            return reankData == null ? 0 : reankData.size();
        }
    }


    private void closePager() {

        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }
        mView.removeCallbacks(showCloseBntTask);
        mView.removeCallbacks(closePagerTask);
        mView.removeCallbacks(upDatePraiseNumTask);

        if (mPraiseListBll != null) {
            mPraiseListBll.closePager();
        }
        mPraiseListBll.upLoadLog("ArtsPraise","ArtsPraisePager_closePager");
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
            if (soundPoolHelper != null) {
                for (int i = 0; i < soundResArray.length; i++) {
                    soundPoolHelper.setVolume(soundResArray[i], MUSIC_VOLUME_RATIO_FRONT);
                }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        closePager();
    }
}
