package com.xueersi.parentsmeeting.modules.livevideo.praiselist.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll;

/**
 * 初高中点赞互动
 */

public class PraiseInteractionPager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/interaction/";

    //纳秒单位
    private static final long NAS = 1000000;

    //点赞按钮
    private ImageView praiseBtn;

    //呼吸光圈
    private View breathView;

    //点赞按钮冒星星
    private LottieAnimationView bubbleView;

    //星星重复播放
    private LottieAnimationView bubbleRepeatView;

    //点赞按钮按下动画
    private LottieAnimationView pressLottileView;

    //是否点击点赞按钮
    private boolean isClickPraiseBtn;

    //点赞数字
    private TextView praiseNumView;

    private int num;
    private float translationY;


    private int btnWidth;
    private int btnMarginRight;
    private int btnMarginBottom;

    private AnimatorSet animatorSet;

    //长按首次按下时间
    private long lastPraiseTime = 0;
    //是否是长按
    private boolean isLongPress = false;

    public PraiseInteractionPager(Context context, PraiseInteractionBll praiseInteractionBll) {
        super(context);
        btnWidth = (int) context.getResources().getDimension(R.dimen.livevideo_praise_interac_praise_btn_width);
        btnMarginRight = (int) mContext.getResources().getDimension(R.dimen
                .livevideo_praise_interac_praise_btn_margin_right);
        btnMarginBottom = (int) mContext.getResources().getDimension(R.dimen
                .livevideo_praise_interac_praise_btn_margin_bottom);
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_praise_interaction, null);
        praiseBtn = view.findViewById(R.id.iv_livevideo_praise_interac_praise_btn);
        praiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPraiseBtn();
            }
        });

        praiseBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        praiseBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastPraiseTime = System.nanoTime();
                        isLongPress = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        long interval = System.nanoTime() - lastPraiseTime;
                        //长按每秒三次点赞
                        if (interval > 1000 * NAS / 3) {
                            lastPraiseTime = System.nanoTime();
                            isLongPress = true;
                            onClickPraiseBtn();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return isLongPress;
            }
        });

        breathView = view.findViewById(R.id.iv_livevideo_praise_interac_breath);

        bubbleView = view.findViewById(R.id.lav_livevideo_praise_interac_bubble);
        bubbleRepeatView = view.findViewById(R.id.lav_livevideo_praise_interac_bubble_repeat);
        bubbleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = bubbleView.getWidth();
                if (width > 0) {
                    caculateBubblePosition();
                    bubbleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        pressLottileView = view.findViewById(R.id.iv_livevideo_praise_interac_press);
        pressLottileView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = pressLottileView.getWidth();
                System.out.println("width="+width);
                if (width > 0) {
                    caculatePressPosition();
                    pressLottileView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        praiseNumView = view.findViewById(R.id.lav_livevideo_praise_interac_num);
        praiseNumView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = praiseNumView.getWidth();
                if (width > 0) {
                    caculatePraiseNumPosition();
                    praiseNumView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

            }
        });
        translationY = praiseNumView.getTranslationY();

        initBubbleEnterAnimation();
        initPraiseNumDisplayAnimation();

        return view;
    }

    /**
     * 点击点赞按钮
     */
    private void onClickPraiseBtn() {
        isClickPraiseBtn = true;
        stopBubbleRepeatAnimation();
        startPraiseBtnPressAnimation();

        praiseNumView.setText("+" + String.valueOf(++num));
        praiseNumView.setVisibility(View.VISIBLE);
        startPraiseNumDisplayAnimation();

    }


    /**
     * 长按点赞按钮
     */
    private void onLongClickPraiseBtn() {
        stopBubbleRepeatAnimation();

    }

    private void initPraiseNumDisplayAnimation() {
        int distance = SizeUtils.Dp2Px(mContext, 10);
        animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(praiseNumView, "translationY", -distance);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(praiseNumView, "alpha", 0f, 1f);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                praiseNumView.setTranslationX(translationY);
                praiseNumView.setAlpha(0);
                praiseNumView.setVisibility(View.GONE);
            }
        });

        animatorSet.play(objectAnimator).with(alphaAnimator);
        animatorSet.setDuration(1000);

    }

    private void startPraiseNumDisplayAnimation() {
        if (animatorSet != null && !animatorSet.isRunning()) {
            animatorSet.start();
        }
    }


    /**
     * 按钮点赞动效
     */
    private void startPraiseBtnPressAnimation() {
        String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "press/images";
        String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "press/data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath);
        pressLottileView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext));
        pressLottileView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return bubbleEffectInfo.fetchBitmapFromAssets(pressLottileView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        pressLottileView.setImageAssetDelegate(imageAssetDelegate);
        pressLottileView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                float animatedFraction = animation.getAnimatedFraction();

            }
        });
        pressLottileView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                pressLottileView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pressLottileView.setVisibility(View.GONE);

            }
        });
        pressLottileView.playAnimation();

    }


    /**
     * 点赞按钮进场动画
     */
    public void startPraiseBtnEnterAnimation() {

        float translationX = praiseBtn.getTranslationX();
        float distance = getRightMargin() + btnWidth + btnMarginRight;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(praiseBtn, "translationX", distance, translationX);
        objectAnimator.setDuration(1000);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                logger.d("prasie btn enter anima end");
                //等待两秒没有点击事件冒星星
                praiseBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isClickPraiseBtn) {
                            bubbleView.playAnimation();
                            isClickPraiseBtn = false;
                        }
                    }
                }, 2000);
            }
        });
        objectAnimator.start();
    }

    /**
     * 开始点赞按钮呼吸动画
     */
    private void startBreathAnimation() {
        breathView.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator objectYAnimator = ObjectAnimator.ofFloat(breathView, "scaleY", 1f, 2f);
        objectYAnimator.setRepeatCount(-1);
        objectYAnimator.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator objectXAnimator = ObjectAnimator.ofFloat(breathView, "scaleX", 1f, 2f);
        objectXAnimator.setRepeatCount(-1);
        objectXAnimator.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(breathView, "alpha", 1f, 0f);
        alphaAnimator.setRepeatCount(-1);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);

        animatorSet.play(objectYAnimator).with(objectXAnimator).before(alphaAnimator);

        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    /**
     * 点赞按钮冒泡星星进场动画
     */
    private void initBubbleEnterAnimation() {
        String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "bubble_enter/images";
        String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "bubble_enter/data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath);
        bubbleView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext));
        bubbleView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return bubbleEffectInfo.fetchBitmapFromAssets(bubbleView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        bubbleView.setImageAssetDelegate(imageAssetDelegate);
        bubbleView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                float animatedFraction = animation.getAnimatedFraction();
                if (animatedFraction > 0.8) {
                    startBubbleRepeatAnimation();
                }

            }
        });
        bubbleView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bubbleView.setVisibility(View.INVISIBLE);

            }
        });

    }

    private void stopBubbleRepeatAnimation() {
        bubbleRepeatView.cancelAnimation();
        bubbleRepeatView.setVisibility(View.GONE);
    }


    /**
     * 点赞按钮连续冒泡星星动画
     */
    private void startBubbleRepeatAnimation() {
        String bubbleRepeatResPath = LOTTIE_RES_ASSETS_ROOTDIR + "bubble_repeat/images";
        String bubbleRepeatJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "bubble_repeat/data.json";
        final LottieEffectInfo bubbleRepeatEffectInfo = new LottieEffectInfo(bubbleRepeatResPath, bubbleRepeatJsonPath);
        bubbleRepeatView.setAnimationFromJson(bubbleRepeatEffectInfo.getJsonStrFromAssets(mContext));
        bubbleRepeatView.useHardwareAcceleration(true);
        bubbleRepeatView.setRepeatCount(-1);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return bubbleRepeatEffectInfo.fetchBitmapFromAssets(bubbleRepeatView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        bubbleRepeatView.setImageAssetDelegate(imageAssetDelegate);
        bubbleRepeatView.playAnimation();
    }

    private void caculatePraiseNumPosition() {
        int numMarginRight = btnMarginRight + btnWidth / 2 - praiseNumView.getWidth() / 2;

        RelativeLayout.LayoutParams breathParams = (RelativeLayout.LayoutParams) praiseNumView.getLayoutParams();
        breathParams.rightMargin = numMarginRight;

        praiseNumView.setLayoutParams(breathParams);
    }

    /**
     * 计算点赞按钮按下动效位置
     */
    private void caculatePressPosition() {

        int pressMarginRight = btnMarginRight - (pressLottileView.getWidth() - btnWidth) / 2;
        int pressMarginBottom = btnMarginBottom - (pressLottileView.getHeight() - btnWidth) / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pressLottileView.getLayoutParams();
        params.rightMargin = pressMarginRight;
        params.bottomMargin = pressMarginBottom;

        pressLottileView.setLayoutParams(params);

    }


    /**
     * 计算冒星星位置
     */
    private void caculateBubblePosition() {
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - bubbleView.getWidth() / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubbleView.getLayoutParams();
        params.rightMargin = Math.abs(bubbleMarginRight);

        bubbleView.setLayoutParams(params);
        bubbleRepeatView.setLayoutParams(params);


    }


    /**
     * 计算呼吸光效的位置
     */
    private void caculateBreathPosition() {
        int width = praiseBtn.getWidth();
        int height = praiseBtn.getHeight();
        int marginRight = (int) mContext.getResources().getDimension(R.dimen
                .livevideo_praise_interac_praise_btn_margin_right);
        int marginBottom = (int) mContext.getResources().getDimension(R.dimen
                .livevideo_praise_interac_praise_btn_margin_bottom);
        int breathMarginRight = marginRight - (breathView.getWidth() - praiseBtn.getWidth()) / 2;
        int breathMarginBottom = marginBottom - (breathView.getHeight() - praiseBtn.getHeight()) / 2;

        RelativeLayout.LayoutParams breathParams = (RelativeLayout.LayoutParams) breathView.getLayoutParams();
        breathParams.rightMargin = breathMarginRight;
        breathParams.bottomMargin = breathMarginBottom;

        breathView.setLayoutParams(breathParams);
    }


    @Override
    public void initData() {
    }

    private int getRightMargin() {
        return 600;
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
