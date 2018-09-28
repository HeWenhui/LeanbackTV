package com.xueersi.parentsmeeting.modules.livevideo.praiselist.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Message;
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

import java.math.BigDecimal;
import java.util.Random;

/**
 * 初高中点赞互动
 */

public class PraiseInteractionPager extends BasePager {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/interaction/";

    //延迟开始冒泡
    private static final int MESSAGE_WHAT_DELAY_BUBBLE = 1;

    //礼物倒计时
    private static final int MESSAGE_WHAT_DELAY_GIFT = 2;

    //送出成功2秒消失
    private static final int MESSAGE_WHAT_DELAY_SEND_DISMISS = 3;

    //礼物倒计时
    private static final int SPECIAL_GIFT_TYPE_MATH = 3;
    private static final int SPECIAL_GIFT_TYPE_PHYSICAL = 2;
    private static final int SPECIAL_GIFT_TYPE_CHEMISTRY = 1;

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

    private View specialGiftView;

    private TextView countDownView;

    private TextView goldView;

    private ImageView closeView;

    private ImageView giftImg;

    private View giftSendView;
    private TextView giftSendText;
    private TextView giftSendCoin;

    //数学动画
    private LottieAnimationView mathLottileView;

    //物理动画l
    private LottieAnimationView physicalLottileView;

    //化学
    private ImageView chemistryView;

    private int num = 0;
    private float translationY;


    private int btnWidth;
    private int btnMarginRight;
    private int btnMarginBottom;

    private AnimatorSet animatorSet;

    //长按首次按下时间
    private long lastPraiseTime = 0;
    //是否是长按
    private boolean isLongPress = false;

    private int countDownNum = 10;//倒计时

    private int currentGiftType = 0;

    private int coinAmount = 10;

    private TimeHandler timeHandler = new TimeHandler();


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
        pressLottileView = view.findViewById(R.id.iv_livevideo_praise_interac_press);
        praiseBtn = view.findViewById(R.id.iv_livevideo_praise_interac_praise_btn);
        pressLottileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPraiseBtn();
            }
        });

        pressLottileView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        pressLottileView.setOnTouchListener(new View.OnTouchListener() {
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

        pressLottileView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = pressLottileView.getWidth();
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

        specialGiftView = view.findViewById(R.id.fl_livevideo_praise_interac_special_gift_group);
        specialGiftView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = specialGiftView.getWidth();
                if (width > 0) {
                    caculateSpecialGiftPosition();
                    specialGiftView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        countDownView = view.findViewById(R.id.tv_livevideo_praise_interac_special_gift_countdown);
        closeView = view.findViewById(R.id.iv_livevideo_praise_interac_special_gift_close);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeHandler.removeMessages(MESSAGE_WHAT_DELAY_GIFT);
                specialGiftView.setVisibility(View.GONE);
                pressLottileView.setEnabled(true);
            }
        });

        goldView = view.findViewById(R.id.tv_livevideo_praise_interac_special_gift_gold);
        giftImg = view.findViewById(R.id.lav_livevideo_praise_interac_special_gift_img);
        View sendView = view.findViewById(R.id.rl_livevideo_praise_interac_special_gift_send_group);
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGift();
            }
        });

        giftSendView = view.findViewById(R.id.rl_livevideo_praise_interac_gift_send_group);
        giftSendView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = giftSendView.getWidth();
                if (width > 0) {
                    caculateSendGiftPosition();
                    giftSendView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                giftSendView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        giftSendText = view.findViewById(R.id.tv_livevideo_praise_interac_gift_send_text);
        giftSendCoin = view.findViewById(R.id.tv_livevideo_praise_interac_gift_send_coin);


        mathLottileView = view.findViewById(R.id.lav_livevideo_praise_interac_math);

        physicalLottileView = view.findViewById(R.id.lav_livevideo_praise_interac_physical);

        chemistryView = view.findViewById(R.id.iv_livevideo_praise_interac_chemistry);

        initPraiseBtnPressAnimation();
        initBubbleEnterAnimation();
        initPraiseNumDisplayAnimation();
        initBubbleRepeatAnimation();
        initMathAnimation();
        initPhysicalAnimation();

        return view;
    }

    /**
     * 送出礼物
     */
    private void sendGift() {
        if (coinAmount - 5 > 0) {
            String giftType = "";
            if (currentGiftType == SPECIAL_GIFT_TYPE_PHYSICAL) {
                giftType = "成功送出“星空”";
                physicalLottileView.playAnimation();
            } else if (currentGiftType == SPECIAL_GIFT_TYPE_CHEMISTRY) {
                giftType = "成功送出“魔法水”";
                chemistryView.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) chemistryView.getBackground();
                animationDrawable.start();
                int numberOfFrames = animationDrawable.getNumberOfFrames();
                int duration = 0;
                for (int i = 0; i < numberOfFrames; i++) {
                    duration = duration + animationDrawable.getDuration(i);
                }
                chemistryView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chemistryView.setVisibility(View.GONE);
                    }
                }, duration);
            } else if (currentGiftType == SPECIAL_GIFT_TYPE_MATH) {
                mathLottileView.playAnimation();
                giftType = "成功送出“气球”";
            }
            giftSendText.setText(giftType);
            coinAmount = coinAmount - 5;
        } else {
            giftSendText.setText("金币不足");
        }
        giftSendCoin.setText(String.valueOf(coinAmount));
        giftSendView.setVisibility(View.VISIBLE);
        specialGiftView.setVisibility(View.GONE);
        pressLottileView.setEnabled(true);
        timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_SEND_DISMISS, 2000);
    }


    private class TimeHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == MESSAGE_WHAT_DELAY_BUBBLE) {
                if (specialGiftView.getVisibility() != View.VISIBLE) {
                    bubbleView.playAnimation();
                }
            } else if (what == MESSAGE_WHAT_DELAY_GIFT) {
                logger.d("countDownNum=" + countDownNum);
                countDownView.setText(countDownNum + "s关闭");
                if (countDownNum == 0) {
                    specialGiftView.setVisibility(View.GONE);
                    pressLottileView.setEnabled(true);
                    countDownNum = 10;
                    timeHandler.removeMessages(MESSAGE_WHAT_DELAY_GIFT);
                } else {
                    countDownNum--;
                    timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_GIFT, 1000);
                }
            } else if (what == MESSAGE_WHAT_DELAY_SEND_DISMISS) {
                giftSendView.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 点击点赞按钮
     */
    private void onClickPraiseBtn() {
        timeHandler.removeMessages(MESSAGE_WHAT_DELAY_BUBBLE);
        isClickPraiseBtn = true;

        //暂停冒泡动画
        bubbleView.cancelAnimation();
        bubbleView.setVisibility(View.GONE);
        bubbleRepeatView.cancelAnimation();
        bubbleRepeatView.setVisibility(View.GONE);

        pressLottileView.playAnimation();

        //保留2位小数
        num++;
        String strNum;
        if (num > 1000) {
            BigDecimal bigDecimal = new BigDecimal(num / 1000d);
            double doubleNum = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            strNum = String.valueOf(doubleNum) + "k";
        } else {
            strNum = String.valueOf(num);
        }
        praiseNumView.setText(strNum);
        startSpecailGiftAnimation();

        if (animatorSet.isRunning()) {
            animatorSet.cancel();
        }
        animatorSet.start();
    }

    /**
     * 判断是否开启l神秘礼物
     */
    private void startSpecailGiftAnimation() {
        //首次点赞5次出现礼物,20的整数倍并且从弹窗出现后10秒再出现礼物
        logger.d("praise num=" + num);
        if (num == 5 || (num % 20 == 0) && countDownNum == 10) {
            specialGiftView.setVisibility(View.VISIBLE);
            currentGiftType = getProbabilityNum();
            logger.d("probabilityNum=" + currentGiftType);
            if (currentGiftType == SPECIAL_GIFT_TYPE_CHEMISTRY) {
                //化学
                giftImg.setImageResource(R.drawable.livevideo_alert_chemistry_icon_normal);

            } else if (currentGiftType == SPECIAL_GIFT_TYPE_PHYSICAL) {
                //物理星空
                giftImg.setImageResource(R.drawable.livevideo_alert_physics_icon_normal);
            } else {
                //数学
                giftImg.setImageResource(R.drawable.livevideo_alert_math_icon_normal);
            }
            pressLottileView.setEnabled(false);
            timeHandler.sendEmptyMessage(MESSAGE_WHAT_DELAY_GIFT);
        }
    }

    /**
     * 特效礼物弹出概率
     * 20%，30%，50%,魔法水、星空、气球
     *
     * @return
     */
    private int getProbabilityNum() {
        int randomInt = new Random().nextInt(100) + 1;
        int num;
        if (randomInt <= 20) {
            num = 1;
        } else if (randomInt <= 50) {
            num = 2;
        } else {
            num = 3;
        }
        return num;
    }


    private void initPraiseNumDisplayAnimation() {
        int distance = SizeUtils.Dp2Px(mContext, 10);
        animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(praiseNumView, "translationY", -distance);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(praiseNumView, "alpha", 0f, 1f);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                praiseNumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                praiseNumView.setTranslationX(translationY);
                praiseNumView.setAlpha(0);
                praiseNumView.setVisibility(View.GONE);
                timeHandler.removeMessages(MESSAGE_WHAT_DELAY_BUBBLE);
                timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_BUBBLE, 2000);
            }
        });

        animatorSet.play(objectAnimator).with(alphaAnimator);
        animatorSet.setDuration(1000);

    }


    /**
     * 按钮点赞动效
     */
    private void initPraiseBtnPressAnimation() {
        String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "press/images";
        String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "press/data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath);
        pressLottileView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "press");
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
        pressLottileView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });

    }

    /**
     * 点赞按钮进场动画
     */
    public void startPraiseBtnEnterAnimation() {
        num = 0;
        float translationX = praiseBtn.getTranslationX();
        float distance = getRightMargin() + btnWidth + btnMarginRight;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(pressLottileView, "translationX", distance,
                translationX);
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
        bubbleView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "bubble_enter");
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
                    bubbleRepeatView.playAnimation();
                }

            }
        });
        bubbleView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                bubbleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bubbleView.setVisibility(View.INVISIBLE);
            }
        });

    }

    /**
     * 物理动画
     */
    private void initPhysicalAnimation() {
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "physical/images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "physical/data.json";
        final LottieEffectInfo repeatEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        physicalLottileView.setAnimationFromJson(repeatEffectInfo.getJsonStrFromAssets(mContext), "physical");
        physicalLottileView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return repeatEffectInfo.fetchBitmapFromAssets(physicalLottileView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        physicalLottileView.setImageAssetDelegate(imageAssetDelegate);
        physicalLottileView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                physicalLottileView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                physicalLottileView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 数学动画
     */
    private void initMathAnimation() {
        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "math/images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "math/data.json";
        final LottieEffectInfo effectInfo = new LottieEffectInfo(resPath, jsonPath);
        mathLottileView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), "math");
        mathLottileView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return effectInfo.fetchBitmapFromAssets(mathLottileView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        mathLottileView.setImageAssetDelegate(imageAssetDelegate);
        mathLottileView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mathLottileView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mathLottileView.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 点赞按钮连续冒泡星星动画
     */
    private void initBubbleRepeatAnimation() {
        String bubbleRepeatResPath = LOTTIE_RES_ASSETS_ROOTDIR + "bubble_repeat/images";
        String bubbleRepeatJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "bubble_repeat/data.json";
        final LottieEffectInfo bubbleRepeatEffectInfo = new LottieEffectInfo(bubbleRepeatResPath, bubbleRepeatJsonPath);
        bubbleRepeatView.setAnimationFromJson(bubbleRepeatEffectInfo.getJsonStrFromAssets(mContext), "bubble_repeat");
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
        bubbleRepeatView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                bubbleRepeatView.setVisibility(View.VISIBLE);
            }
        });
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

    private void caculateSpecialGiftPosition() {
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - specialGiftView.getWidth() / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) specialGiftView.getLayoutParams();
        params.rightMargin = Math.abs(bubbleMarginRight);

        specialGiftView.setLayoutParams(params);

    }

    private void caculateSendGiftPosition() {
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - giftSendView.getWidth() / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) giftSendView.getLayoutParams();
        params.rightMargin = Math.abs(bubbleMarginRight);

        giftSendView.setLayoutParams(params);

    }

    /**
     * 计算数学位置
     */
    private void caculateMathPosition() {
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - mathLottileView.getWidth() / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mathLottileView.getLayoutParams();
        params.rightMargin = Math.abs(bubbleMarginRight);

        mathLottileView.setLayoutParams(params);


    }

    /**
     * 计算物理位置
     */
    private void caculatePhysicalPosition() {
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - physicalLottileView.getWidth() / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) physicalLottileView.getLayoutParams();
        params.rightMargin = Math.abs(bubbleMarginRight);

        physicalLottileView.setLayoutParams(params);


    }

    /**
     * 计算化学位置
     */
    private void caculateChemistryPosition() {
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - physicalLottileView.getWidth() / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) physicalLottileView.getLayoutParams();
        params.rightMargin = Math.abs(bubbleMarginRight);

        physicalLottileView.setLayoutParams(params);


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
