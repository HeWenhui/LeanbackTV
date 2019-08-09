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
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PraiseMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalBarrageView;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;

/**
 * 初高中点赞互动
 */

public class PraiseInteractionPager extends BasePager implements VerticalBarrageView.OnBarrageScrollListener {
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/interaction/";

    //延迟开始冒泡
    private static final int MESSAGE_WHAT_DELAY_BUBBLE = 1;

    //礼物倒计时
    private static final int MESSAGE_WHAT_DELAY_GIFT = 2;

    //送出成功2秒消失
    private static final int MESSAGE_WHAT_DELAY_SEND_DISMISS = 3;

    //连续点击计时器
    private static final int MESSAGE_WHAT_DELAY_CONTINUE_PRAISE = 4;

    //每秒计数三次及时
    private static final int MESSAGE_WHAT_DELAY_PRAISE_COUNT = 5;

    //礼物倒计时


    //纳秒单位
    private static final long NAS = 1000000;

    private final PraiseInteractionBll mPraiseInteractionBll;

    private final LiveAndBackDebug liveAndBackDebug;

    private final LiveGetInfo mGetInfo;


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

    //连续点赞数字
    private TextView praiseNumView;
    //点赞总数
    private TextView praiseTotalNumView;

    private View specialGiftView;

    private TextView countDownView;

    private ImageView giftImg;

    private TextView goldCountView;

    private View giftSendView;
    private TextView giftSendText;
    private TextView deductGoldView;
    private TickerView giftSendCoin;

    //数学动画
    private LottieAnimationView mathLottileView;

    //物理动画l
    private LottieAnimationView physicalLottileView;

    //星星进场动画
    private LottieAnimationView starEnterLottileView;

    //弹幕滚动
    private VerticalBarrageView verticalBarrageView;

    //化学
    private ImageView chemistryView;

    //点赞数字移动动画
    private AnimatorSet animatorSet;

    //点赞总数
    private int praiseNumAmount = 0;

    //连续点赞次数
    private int continuePraiseNum = 0;

    //弹出送礼物窗口
    private int displayGiftNum = 0;

    private float translationY;

    //点赞按钮宽度
    private int btnWidth;
    private int btnMarginRight;
    private int btnMarginBottom;


    //是否是长按
    private boolean isLongPress = false;

    //特效礼物窗口倒计时
    private int countDownNum = 0;

    //当前礼物特效类型
    private int currentGiftType = 0;

    private TimeHandler timeHandler = new TimeHandler();

    //金币总数
    private int goldCount;

    //统计每次点赞的时间
    private List<Long> praiseTimeList = new ArrayList<>();

    //礼物数量统计
    StableLogHashMap giftHashMap = new StableLogHashMap("showeffectgiftviewcount");


    public PraiseInteractionPager(Context context, int goldCount, PraiseInteractionBll praiseInteractionBll, LiveAndBackDebug liveAndBackDebug, LiveGetInfo getInfo) {
        super(context);
        this.goldCount = goldCount;
        this.mGetInfo = getInfo;
        this.mPraiseInteractionBll = praiseInteractionBll;
        this.liveAndBackDebug = liveAndBackDebug;
        btnWidth = (int) context.getResources().getDimension(R.dimen.livevideo_praise_interac_praise_btn_width);
        btnMarginRight = (int) mContext.getResources().getDimension(R.dimen
                .livevideo_praise_interac_praise_btn_margin_right);
        btnMarginBottom = (int) mContext.getResources().getDimension(R.dimen
                .livevideo_praise_interac_praise_btn_margin_bottom);
        initData();
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_praise_interaction, null);

        starEnterLottileView = view.findViewById(R.id.lav_livevideo_praise_interac_star_enter);
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
                        isLongPress = false;
                        timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_PRAISE_COUNT, 333);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        timeHandler.removeMessages(MESSAGE_WHAT_DELAY_PRAISE_COUNT);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        timeHandler.removeMessages(MESSAGE_WHAT_DELAY_PRAISE_COUNT);
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

        praiseTotalNumView = view.findViewById(R.id.lav_livevideo_praise_interac_totalnum);
        praiseTotalNumView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = praiseTotalNumView.getWidth();
                if (width > 0) {
                    caculatePraiseTotalNumPosition();
                    praiseTotalNumView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
        View closeView = view.findViewById(R.id.ll_livevideo_praise_interac_special_gift_close);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specialGiftView.setVisibility(View.GONE);
                //用户手动关闭特效窗口，过两秒没有点击点赞按钮冒泡动画开始
                timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_BUBBLE, 2000);
            }
        });

        giftImg = view.findViewById(R.id.lav_livevideo_praise_interac_special_gift_img);
        goldCountView = view.findViewById(R.id.tv_livevideo_praise_interac_gold_amount);


        View sendView = view.findViewById(R.id.rl_livevideo_praise_interac_special_gift_send_group);
        deductGoldView = view.findViewById(R.id.tv_livevideo_praise_interac_special_gift_gold);
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGift();
            }
        });
        //成功送出
        giftSendView = view.findViewById(R.id.rl_livevideo_praise_interac_gift_send_group);
        giftSendView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = giftSendView.getWidth();
                if (width > 0) {
                    caculateSendGiftPosition();
                    giftSendView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        giftSendText = view.findViewById(R.id.tv_livevideo_praise_interac_gift_send_text);
        giftSendCoin = view.findViewById(R.id.tv_livevideo_praise_interac_gift_send_coin);
        giftSendCoin.setCharacterLists(TickerUtils.provideNumberList());
        giftSendCoin.setAnimationDuration(500);
        giftSendCoin.setAnimationInterpolator(new OvershootInterpolator());
        giftSendCoin.setGravity(Gravity.CENTER);

        mathLottileView = view.findViewById(R.id.lav_livevideo_praise_interac_math);

        physicalLottileView = view.findViewById(R.id.lav_livevideo_praise_interac_physical);

        chemistryView = view.findViewById(R.id.iv_livevideo_praise_interac_chemistry);

        verticalBarrageView = view.findViewById(R.id.vbv_livevideo_praise_barrageView);
        verticalBarrageView.setListener(this);
        verticalBarrageView.start();

        initPraiseBtnPressAnimation();
        initBubbleEnterAnimation();
        initPraiseNumDisplayAnimation();
        initMathAnimation();
        initPhysicalAnimation();

        return view;
    }

    @Override
    public void initData() {
        goldCountView.setText("金币余额:  " + goldCount);
        giftSendCoin.setText(String.valueOf(goldCount));
    }

    /**
     * 送出礼物
     */
    private void sendGift() {
        specialGiftView.setVisibility(View.GONE);
        int deductNum = (int) deductGoldView.getTag();
        if (goldCount - deductNum >= 0) {
            HttpCallBack httpCallBack = new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    int type = jsonObject.optInt("type");
                    int gold = jsonObject.optInt("gold");

                    mPraiseInteractionBll.sendPrivateMessage(PraiseMessageEntity.TYPE_SPECIAL_GIFT, type);

                    //插入我送出的特效礼物队列一条数据l
                    mPraiseInteractionBll.insertMySpecialGift(type);

                    String giftType = "";
                    if (type == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
                        giftType = "成功送出“星空”";
                    } else if (type == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
                        giftType = "成功送出“魔法水”";
                    } else if (type == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
                        giftType = "成功送出“气球”";
                    }
                    giftSendText.setText(giftType);
                    goldCount = goldCount - gold;
                    giftSendCoin.setText(String.valueOf(goldCount));
                    giftSendView.setVisibility(View.VISIBLE);

                    timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_SEND_DISMISS, 2000);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    giftSendText.setText("发送失败");
                    giftSendView.setVisibility(View.VISIBLE);
                    timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_SEND_DISMISS, 2000);

                }
            };
            mPraiseInteractionBll.sendGiftDeductGold(currentGiftType, httpCallBack);
        } else {
            giftSendText.setText("金币不足");
            giftSendView.setVisibility(View.VISIBLE);
            timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_SEND_DISMISS, 2000);
        }

    }


    private void startHidePraiseBtnAniamtion() {
        float translationX = praiseBtn.getTranslationX();
        float distance = getRightMargin() + btnWidth + btnMarginRight;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(pressLottileView, "translationX", distance);
        objectAnimator.setDuration(1000);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        objectAnimator.start();
    }


    /**
     * 关闭点赞
     */
    public void closePraise() {
        String eventId = "livescience_likeclick";
        StableLogHashMap logHashMap = new StableLogHashMap("likeclickcount");
        logHashMap.put("count", String.valueOf(praiseNumAmount));
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());

        liveAndBackDebug.umsAgentDebugPv(eventId, giftHashMap.getData());

        startHidePraiseBtnAniamtion();
        praiseTimeList.clear();
        timeHandler.removeMessages(0);
        bubbleRepeatView.setVisibility(View.GONE);
        bubbleView.setVisibility(View.GONE);
        specialGiftView.setVisibility(View.GONE);
        verticalBarrageView.stop();
    }

    @Override
    public void onBarrageScrollItem(PraiseMessageEntity praiseMessageEntity) {
        int messageType = praiseMessageEntity.getMessageType();
        if (messageType == PraiseMessageEntity.TYPE_SPECIAL_GIFT) {
            int giftType = praiseMessageEntity.getGiftType();
            if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
                physicalLottileView.playAnimation();
            } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
                startChemistrySpecailGiftAnimation();
            } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
                mathLottileView.playAnimation();
            }

        }

    }

    public void setGoldNum(int goldNum) {
        this.goldCount = goldNum;
    }

    /**
     * 开启点赞
     */
    public void openPraise() {
        giftHashMap.getData().clear();
        giftHashMap.put("type0", String.valueOf(0));
        giftHashMap.put("type1", String.valueOf(0));
        giftHashMap.put("type2", String.valueOf(0));
        praiseNumAmount = 0;
        continuePraiseNum = 0;
        displayGiftNum = 0;
        countDownNum = 0;
        startEnterStarAnimation();
    }


    private class TimeHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == MESSAGE_WHAT_DELAY_BUBBLE) {
                if (specialGiftView.getVisibility() != View.VISIBLE && !bubbleView.isAnimating()) {
                    bubbleView.playAnimation();
                }
            } else if (what == MESSAGE_WHAT_DELAY_GIFT) {
                logger.d("countDownNum=" + countDownNum);
                countDownView.setText(countDownNum + "s关闭");
                if (countDownNum == 0) {
                    specialGiftView.setVisibility(View.GONE);
                    timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_BUBBLE, 2000);
                    timeHandler.removeMessages(MESSAGE_WHAT_DELAY_GIFT);
                } else {
                    countDownNum--;
                    timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_GIFT, 1000);
                }
            } else if (what == MESSAGE_WHAT_DELAY_SEND_DISMISS) {
                giftSendView.setVisibility(View.GONE);
            } else if (what == MESSAGE_WHAT_DELAY_CONTINUE_PRAISE) {
                praiseNumView.setVisibility(View.GONE);
                praiseTotalNumView.setVisibility(View.VISIBLE);
                if (praiseTimeList.size() > 1) {
                    long lastTime = praiseTimeList.get(praiseTimeList.size() - 1);
                    long firstTime = praiseTimeList.get(0);
                    int time = 1000;
                    int praiseAutoBarrageTime = mGetInfo.getPraiseAutoBarrageTime();
                    if (praiseAutoBarrageTime > 0) {
                        time = praiseAutoBarrageTime * 1000;
                    }
                    if (lastTime - firstTime > time) {
                        caculatePraiseTotalNumPosition();
                        praiseTotalNumView.setText(getDisplayNum(praiseNumAmount));
                        continuePraiseNum = 0;
                        praiseTimeList.clear();
                        mPraiseInteractionBll.pushMyPraise(praiseNumAmount);
                    }
                }

            } else if (what == MESSAGE_WHAT_DELAY_PRAISE_COUNT) {
                onClickPraiseBtn();
                timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_PRAISE_COUNT, 333);
            }
        }
    }


    /**
     * 点击点赞按钮
     */
    private void onClickPraiseBtn() {
        timeHandler.removeMessages(MESSAGE_WHAT_DELAY_BUBBLE);
        timeHandler.removeMessages(MESSAGE_WHAT_DELAY_CONTINUE_PRAISE);
        //暂停冒泡动画
        bubbleView.cancelAnimation();
        bubbleView.setVisibility(View.GONE);
        bubbleRepeatView.cancelAnimation();
        bubbleRepeatView.setVisibility(View.GONE);
        praiseTotalNumView.setVisibility(View.GONE);

        praiseNumAmount++;
        //如果正在倒计时不计入显示礼物条件
        if (countDownNum <= 0) {
            displayGiftNum++;
        } else {
            displayGiftNum = 0;
        }

        judgeDisplayNumAnimation();

        //播放按下动画
        pressLottileView.cancelAnimation();
        pressLottileView.playAnimation();

        displaySpecailGiftAnimation();

        timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_BUBBLE, 2000);
        timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_CONTINUE_PRAISE, 1000);
        logger.d("praiseNumAmount=" + praiseNumAmount
                + ",continuePraiseNum=" + continuePraiseNum
                + ",displayGiftNum=" + displayGiftNum);
    }

    /**
     * 判断是否显示数字动画
     */
    private void judgeDisplayNumAnimation() {
        long currentPraiseTime = SystemClock.uptimeMillis();
        //首次点赞
        if (praiseTimeList.isEmpty()) {
            continuePraiseNum++;
            //播放动画
            playPraiseNumAnimation();
        } else {
            long lastPraiseTime = praiseTimeList.get(praiseTimeList.size() - 1);
            long interval = currentPraiseTime - lastPraiseTime;
            //大于1秒不算连续点赞l
            if (interval > 1000) {
                continuePraiseNum = 0;
                praiseTimeList.clear();
                continuePraiseNum++;
                //播放动画
                playPraiseNumAnimation();
            } else {
                continuePraiseNum++;
                //连续点赞大于5秒直接弹出
                int time = 5000;
                if (mGetInfo.getPraiseAutoCutTime() > 0) {
                    time = mGetInfo.getPraiseAutoCutTime() * 1000;
                }
                if (currentPraiseTime - praiseTimeList.get(0) > time) {
                    //push总数
                    praiseTimeList.clear();
                    caculatePraiseTotalNumPosition();
                    praiseTotalNumView.setText(getDisplayNum(praiseNumAmount));
                    mPraiseInteractionBll.pushMyPraise(praiseNumAmount);
                }
                //圆圈内直接计数
                if (interval < 500) {
                    praiseNumView.setVisibility(View.VISIBLE);

                } else {
                    //计数动画
                    playPraiseNumAnimation();
                }

            }
        }
        praiseTimeList.add(currentPraiseTime);
        praiseNumView.setText(getDisplayNum(continuePraiseNum));
        praiseTotalNumView.setText(getDisplayNum(praiseNumAmount));
    }

    /**
     * 保留2位小数
     *
     * @param num
     * @return
     */
    private String getDisplayNum(int num) {
        String strNum;
        if (num > 1000) {
            BigDecimal bigDecimal = new BigDecimal(num / 1000d);
            double doubleNum = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            strNum = String.valueOf(doubleNum) + "k";
        } else {
            strNum = String.valueOf(num);
        }
        return strNum;
    }


    private void playPraiseNumAnimation() {
        if (animatorSet.isRunning()) {
            animatorSet.cancel();
        }
        animatorSet.start();
    }


    public void appendBarraige(PraiseMessageEntity data) {
        if (verticalBarrageView != null) {
            verticalBarrageView.appendBarrages(data);
        }
    }

    public void appendBarraiges(List<PraiseMessageEntity> data) {
        if (verticalBarrageView != null) {
            verticalBarrageView.addBarrages(data);
        }
    }


    /**
     * 开启化学特效礼物动画
     */
    private void startChemistrySpecailGiftAnimation() {
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
    }


    /**
     * 判断是否开启l神秘礼物
     */
    private void displaySpecailGiftAnimation() {
        //首次点赞5次出现礼物,20的整数倍并且从弹窗出现后10秒再出现礼物
        if (praiseNumAmount == 5 || (displayGiftNum > 0 && (displayGiftNum % 20 == 0))) {
            displayGiftNum = 0;
            countDownNum = 10;
            starDisplayGiftAnimation();
            goldCountView.setText("金币余额:  " + goldCount);
            currentGiftType = getProbabilityNum() - 1;
            ArrayList praiseGift = mGetInfo.getPraiseGift();
            if (currentGiftType >= 0 && currentGiftType < praiseGift.size()) {
                int num = mGetInfo.getPraiseGift().get(currentGiftType);
                deductGoldView.setTag(num);
                deductGoldView.setText(String.valueOf(num));
            } else {
                deductGoldView.setTag(10);
                deductGoldView.setText("10");
            }
            logger.d("special gift type=" + currentGiftType);
            Map<String, String> data = giftHashMap.getData();
            if (currentGiftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
                //化学
                giftImg.setImageResource(R.drawable.livevideo_alert_chemistry_icon_normal);
                if (data.containsKey("type2")) {
                    String chemistry = data.get("type2");
                    int chemistryInt = Integer.valueOf(chemistry);
                    giftHashMap.put("type2", String.valueOf(++chemistryInt));
                } else {
                    giftHashMap.put("type2", String.valueOf(1));
                }

            } else if (currentGiftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
                if (data.containsKey("type1")) {
                    String pysical = data.get("type1");
                    int pysicalInt = Integer.valueOf(pysical);
                    giftHashMap.put("type1", String.valueOf(++pysicalInt));
                } else {
                    giftHashMap.put("type1", String.valueOf(1));
                }
                //物理星空
                giftImg.setImageResource(R.drawable.livevideo_alert_physics_icon_normal);
            } else {
                //数学
                giftImg.setImageResource(R.drawable.livevideo_alert_math_icon_normal);
                if (data.containsKey("type0")) {
                    String math = data.get("type0");
                    int mathInt = Integer.valueOf(math);
                    giftHashMap.put("type0", String.valueOf(++mathInt));
                } else {
                    giftHashMap.put("type0", String.valueOf(1));
                }
            }
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
        ArrayList<Double> praiseGiftRate = mGetInfo.getPraiseGiftRate();
        int rate0 = 20;
        int rate1 = 30;
        if (praiseGiftRate.size() > 2) {
            rate0 = (int) (praiseGiftRate.get(0) * 100);
            rate1 = (int) (praiseGiftRate.get(1) * 100);
        }
        int randomInt = new Random().nextInt(100) + 1;
        int num;
        if (randomInt <= rate0) {
            num = 1;
        } else if (randomInt <= (rate0 + rate1)) {
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

            }
        });

        animatorSet.play(objectAnimator).with(alphaAnimator);
        animatorSet.setDuration(500);

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
     * 星星飘落
     */
    public void startEnterStarAnimation() {

        String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "star_enter/images";
        String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "star_enter/data.json";
        final LottieEffectInfo repeatEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        starEnterLottileView.setAnimationFromJson(repeatEffectInfo.getJsonStrFromAssets(mContext), "star_enter");
        starEnterLottileView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                return repeatEffectInfo.fetchBitmapFromAssets(starEnterLottileView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
            }
        };
        starEnterLottileView.setImageAssetDelegate(imageAssetDelegate);
        starEnterLottileView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                starEnterLottileView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                starEnterLottileView.setVisibility(View.VISIBLE);
                startPraiseBtnEnterAnimation();
            }
        });
        starEnterLottileView.playAnimation();
    }

    /**
     * 送礼物弹窗动画
     */
    private void starDisplayGiftAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(specialGiftView, "alpha", 0,
                1);
        objectAnimator.setDuration(500);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                specialGiftView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        objectAnimator.start();
    }


    /**
     * 点赞按钮进场动画
     */
    private void startPraiseBtnEnterAnimation() {
        float translationX = praiseBtn.getTranslationX();
        float distance = getRightMargin() + btnWidth + btnMarginRight;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(pressLottileView, "translationX", distance,
                translationX);
        objectAnimator.setDuration(1000);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                pressLottileView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                logger.d("prasie btn enter anima end");
                //等待两秒没有点击事件冒星星
                timeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_DELAY_BUBBLE, 2000);
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
                float animatedFraction = animation.getAnimatedFraction();
                if (animatedFraction > 0.8) {

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
                initBubbleRepeatAnimation();

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
                bubbleView.setVisibility(View.GONE);
                bubbleRepeatView.setVisibility(View.VISIBLE);

            }
        });
        bubbleRepeatView.playAnimation();
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
     * 动态计算总数的位置
     */
    private void caculatePraiseTotalNumPosition() {
        int fontWidth = (int) praiseTotalNumView.getPaint().measureText(String.valueOf(praiseNumAmount));
        int numMarginRight = btnMarginRight + btnWidth / 2 - fontWidth / 2;

        RelativeLayout.LayoutParams breathParams = (RelativeLayout.LayoutParams) praiseTotalNumView.getLayoutParams();
        breathParams.rightMargin = numMarginRight;

        praiseTotalNumView.setLayoutParams(breathParams);
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
        int bubbleMarginRight = btnMarginRight + btnWidth / 2 - giftSendView.getWidth();

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


    private int getRightMargin() {
        return LiveVideoPoint.getInstance().getRightMargin();
    }
}
