package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsAnswerResultLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.widget.VoteView;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 投票题结果页面
 *
 * @author wuqingfeng
 * @version 1.0, 2019/6/24
 */

public class VoteAnswerResultPager extends BasePager implements IArtsAnswerRsultDisplayer, View.OnClickListener {

    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "arts_answer_result_energy/";
    /**
     * 游戏试题类型
     */
    private static final int TYPE_GAME = 12;
    private final String TAG = "ArtsPSEAnswerResultPager";
    protected Logger logger = LoggerFactory.getLogger("ArtsPSEAnswerResultPager");

    /**
     * 强制提交 展示答题结果 延时自动关闭
     **/
    private final long AUTO_CLOSE_DELAY = 2000;
    /**
     * 关闭按钮 尺寸
     */
    private final int CLOSEBTN_HEIGHT = 35;
    private final int CLOSEBTN_WIDTH = 35;

    /**
     * 单选题 答案 展示item  距离顶部的  距离
     */
    private final int SINGLE_ANSWER_TOPMARGIN = 30;

    /**
     * 答案列表 开始展示的时间点
     */
    private static final float FRACTION_RECYCLERVIEW_IN = 0.27f;
    /**
     * 关闭按钮出现时间
     */
    private static final float FRACTION_SHOW_CLOSEBTN = 0.18f;
    private static final int SPAN_COUNT = 1;

    private LottieAnimationView animationView;
    private LottieAnimationView resultAnimeView;
    private RecyclerView recyclerView;
    private RelativeLayout rlAnswerRootLayout;
    private ImageView ivLookAnswer;
    private int mRecyclHeight;
    private AnswerResultEntity mData;
    private final String BG_COLOR = "#CC000000";
    /**
     * 当前答案状态
     */
    private int resultType;

    public static final int RESULT_TYPE_CORRECT = 2;
    public static final int RESULT_TYPE_PART_CORRECT = 1;
    public static final int RESULT_TYPE_ERRRO = 0;
    private AnswerResultStateListener mStateListener;

    /**
     * 奖励布局
     */
    LinearLayout llRewardInfo;
    VoteView voteView;
    /**
     * 金币数量
     */
    FangZhengCuYuanTextView tvGoldCount;
    /**
     * 能量值
     */
//    FangZhengCuYuanTextView tvEnergyCount;

    private TcpMessageReg tcpMessageReg;

    public VoteAnswerResultPager(Context context, AnswerResultEntity entity, AnswerResultStateListener stateListener) {
        super(context);
        mData = entity;
        this.mStateListener = stateListener;
        initData();
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_vote_anwserresult_pse, null);
        animationView = view.findViewById(R.id.lv_arts_answer_state_pse);
        resultAnimeView = view.findViewById(R.id.lv_arts_answer_result_pse);
        rlAnswerRootLayout = view.findViewById(R.id.rl_arts_pse_answer_result_root);
        ivLookAnswer = view.findViewById(R.id.iv_arts_answer_result_answer_btn);
        llRewardInfo = view.findViewById(R.id.ll_arts_answer_reslult_reward_info);
        voteView = (VoteView) view.findViewById(R.id.ll_vote);
//        tvEnergyCount = view.findViewById(R.id.tv_live_speech_result_myenergy);
        tvGoldCount = view.findViewById(R.id.tv_live_speech_result_mygold);

        ivLookAnswer.setOnClickListener(this);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0) {
                    showAnswerReuslt();
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

    @Override
    public void initData() {
        tcpMessageReg = ProxUtil.getProxUtil().get(mContext, TcpMessageReg.class);
        tcpMessageReg.registTcpMessageAction(new TcpMessageAction() {
            @Override
            public void onMessage(short type, int operation, String msg) {
                if (type == 10) {
                    String msgr = msg;
                }

            }

            @Override
            public short[] getMessageFilter() {
                return new short[]{10};
            }
        });
    }


    @Override
    public void showAnswerReuslt() {

//        resultType = mData.getIsRight();

        String lottieJsonPath = null;
        final LottieEffectInfo lottieEffectInfo;
//        logger.d("showAnswerReuslt:resultType=" + resultType);
        // 不是游戏题目
//        if (!isGameResult()) {
//            animationView.setVisibility(View.GONE);
//            animationView.setImageAssetDelegate(null);
//            animationView.removeAllAnimatorListeners();
//            animationView.removeAllUpdateListeners();
//            tvEnergyCount.setText("+"+mData.getEnergy());
//            tvGoldCount.setText("+"+mData.getGold());
        displayDetailUi();
        final ViewTreeObserver viewTreeObserver = resultAnimeView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = resultAnimeView.getMeasuredWidth();
                int height = resultAnimeView.getMaxHeight();
            }
        });
        return;
//        } else {
//            llRewardInfo.setVisibility(View.GONE);
//        }
//        if (resultType == RESULT_TYPE_CORRECT) {
//            String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_correct/images";
//            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_correct/data.json";
////            lottieResPath= LOTTIE_RES_ASSETS_ROOTDIR + "result_state_correct/images";
//            ArtsAnswerPartRightEnergyStateLottieEffectInfo effectInfo = new ArtsAnswerPartRightEnergyStateLottieEffectInfo(lottieResPath,
//                    lottieJsonPath, "img_20.png", "img_21.png");
//            effectInfo.setCoinStr("+" + mData.getGold());
//            effectInfo.setEnergyStr("+" + mData.getEnergy());
//            lottieEffectInfo = effectInfo;
//
//        } else if (resultType == RESULT_TYPE_PART_CORRECT) {
//            String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_part_correct/images";
//            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_part_correct/data.json";
//            ArtsAnswerPartRightEnergyStateLottieEffectInfo effectInfo = new ArtsAnswerPartRightEnergyStateLottieEffectInfo(lottieResPath,
//                    lottieJsonPath, "img_20.png", "img_21.png");
//            effectInfo.setCoinStr("+" + mData.getGold());
//            effectInfo.setEnergyStr("+" + mData.getEnergy());
//            lottieEffectInfo = effectInfo;
//
//        } else {
//            String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_error/images";
//            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_error/data.json";
//            ArtsAnswerErrorEnergyStateLottieEffectInfo effectInfo = new ArtsAnswerErrorEnergyStateLottieEffectInfo(lottieResPath,
//                    lottieJsonPath, "img_20.png", "img_21.png");
//            effectInfo.setCoinStr("+" + mData.getGold());
//            effectInfo.setEnergyStr("+" + mData.getEnergy());
//            lottieEffectInfo = effectInfo;
//        }
//        animationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
//        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
//            @Override
//            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
//                logger.d("showAnswerReuslt:FileName=" + lottieImageAsset.getFileName());
//                return lottieEffectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
//                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
//                        mContext);
//            }
//        });
//
//        animationView.playAnimation();
//        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                animationView.setVisibility(View.GONE);
//                animationView.setImageAssetDelegate(null);
//                animationView.removeAllAnimatorListeners();
//                animationView.removeAllUpdateListeners();
//                    //游戏答题结果 只展示金币UI
//                    if (getRootView() != null && getRootView().getParent() != null) {
//                        ((ViewGroup) getRootView().getParent()).removeView(getRootView());
//                    }
//                    if (mStateListener != null) {
//                        mStateListener.onCompeletShow();
//                    }
//
//            }
//        });

    }

    /**
     * 是否是新课件平台的 游戏答题结果
     */
    private boolean isGameResult() {
        return mData != null && mData.getType() == TYPE_GAME;
    }


    int latestMeasuerWidth;

    /**
     * 添加 关闭 按钮
     */
//    private void addCloseBtn() {
//        closeBtnAdded = true;
//        final ImageView closeBtn = new ImageView(mContext);
//        closeBtn.setImageResource(R.drawable.selector_live_enpk_shell_window_guanbi_btn);
//        closeBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mData.isVoice == 1) {
//                    if (mStateListener != null) {
//                        mStateListener.onAutoClose(VoteAnswerResultPager.this);
//                    }
//                } else {
//                    closeReusltUi();
//                }
//            }
//        });
//
//
//        final ViewTreeObserver viewTreeObserver = resultAnimeView.getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (resultAnimeView.getMeasuredWidth() > 0 && latestMeasuerWidth != resultAnimeView.getMeasuredWidth
//                        ()) {
//                    latestMeasuerWidth = resultAnimeView.getMeasuredWidth();
//                    int designWidth = SizeUtils.Dp2Px(resultAnimeView.getContext(), 640f);
//                    int designHeight = SizeUtils.Dp2Px(resultAnimeView.getContext(), 360f);
//                    float scaleX = (latestMeasuerWidth * 1.0f) / designWidth;
//                    float scaleY = (resultAnimeView.getMeasuredHeight() * 1.0f) / designHeight;
//                    float scale = Math.min(scaleX, scaleY);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) closeBtn.getLayoutParams();
//                    if(params == null){
//                        params = new RelativeLayout.LayoutParams(SizeUtils.Dp2Px(mContext, CLOSEBTN_WIDTH), SizeUtils.Dp2Px(mContext, CLOSEBTN_HEIGHT));
//                    }
//                    int offset = (int) ((1.0f - scale) * SizeUtils.Dp2Px(resultAnimeView.getContext(), 35f));
//                    params.rightMargin = (int) (SizeUtils.Dp2Px(resultAnimeView.getContext(), 120f) * scale) - offset;
//                    params.topMargin = (int) (SizeUtils.Dp2Px(resultAnimeView.getContext(), 45f) / scale) + offset;
//                    params.addRule(RelativeLayout.ALIGN_TOP, R.id.lv_arts_answer_result_pse);
//                    params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.lv_arts_answer_result_pse);
//
//                    if(closeBtn.getParent() == null){
//                        rlAnswerRootLayout.addView(closeBtn,params);
//                        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(mContext, R
//                                .anim.anim_livevideo_close_btn_in);
//                        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.23f));
//                        closeBtn.startAnimation(scaleAnimation);
//                    }else{
//                        LayoutParamsUtil.setViewLayoutParams(closeBtn, params);
//                    }
//                    //语音答题倒计时按钮位置
//                    if (mData.isVoice == 1) {
//                        TextView tvClose = mView.findViewById(R.id.tv_arts_answer_result_pse_close);
//                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvClose
//                                .getLayoutParams();
//                        layoutParams.bottomMargin = (int) (SizeUtils.Dp2Px(resultAnimeView.getContext(), 85f) / scale);
//                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.lv_arts_answer_result_pse);
//                        LayoutParamsUtil.setViewLayoutParams(tvClose, layoutParams);
//                    }
//                    setRewardInfoPosition(scaleY);
//                }
//            }
//        });
//
//        if (mData.isVoice == 1) {
//            final TextView tvClose = mView.findViewById(R.id.tv_arts_answer_result_pse_close);
//            tvClose.setVisibility(View.VISIBLE);
//            final AtomicInteger integer = new AtomicInteger(5);
//            setCloseText(tvClose, integer);
//            tvClose.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    int count = integer.decrementAndGet();
//                    if (count == 0) {
//                        answerListShowing = false;
//                        if (mStateListener != null) {
//                            mStateListener.onAutoClose(VoteAnswerResultPager.this);
//                        } else {
//                            ViewGroup group = (ViewGroup) mView.getParent();
//                            group.removeView(mView);
//                        }
//                    } else {
//                        setCloseText(tvClose, integer);
//                        tvClose.postDelayed(this, 1000);
//                    }
//                }
//            }, 1000);
//        }
//    }

    /**
     * 设置能量和金币位置
     */
    private void setRewardInfoPosition(float scale) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llRewardInfo.getLayoutParams();
        float scan = LiveVideoPoint.getInstance().screenHeight * SizeUtils.Dp2Px(mContext, 97) / SizeUtils.Dp2Px(mContext, 360);

        params.topMargin = (int) scan;

        llRewardInfo.setLayoutParams(params);

    }

    private void setCloseText(TextView textView, AtomicInteger integer) {
        textView.setText(integer + "s后关闭");
    }

    /**
     * 隐藏 答题结果
     */
    private void closeReusltUi() {
        if (mStateListener != null) {
            mStateListener.onCloseByUser();
        }
    }

    /**
     * 显示 答题结果
     */
    private void revealAnswerResult() {
        ivLookAnswer.setVisibility(View.INVISIBLE);
        rlAnswerRootLayout.setVisibility(View.VISIBLE);
        mView.setBackgroundColor(Color.parseColor(BG_COLOR));
    }

    private boolean answerListShowing = false;
    private boolean closeBtnAdded = false;

    /**
     * 展示答题详情
     */
    private void displayDetailUi() {
        String lottieResPath = "result_vote/images";
        String lottieJsonPath = "result_vote/data.json";

        final ArtsAnswerResultLottieEffectInfo effectInfo = new ArtsAnswerResultLottieEffectInfo(lottieResPath,
                lottieJsonPath);
        resultAnimeView.useHardwareAcceleration();
        resultAnimeView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
        resultAnimeView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(resultAnimeView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        resultAnimeView.playAnimation();
        resultAnimeView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() >= FRACTION_RECYCLERVIEW_IN && !answerListShowing) {
                    llRewardInfo.setVisibility(View.VISIBLE);
//                    showAnswerList();
                    showVoteList();
                }
                if (animation.getAnimatedFraction() >= FRACTION_SHOW_CLOSEBTN && !closeBtnAdded) {
//                    addCloseBtn();
                }
            }
        });
    }

    private void showVoteList() {
        answerListShowing = true;
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("A", 50);
        map.put("B", 80);
        map.put("C", 40);
        map.put("D", 20);
        map.put("E", 20);
        voteView.initVew(map);
    }

//    private void showAnswerList() {
//        answerListShowing = true;
//        recyclerView = mView.findViewById(R.id.rcl_arts_answer_result_detail);
//        recyclerView.setVisibility(View.VISIBLE);
//        //动态设置宽度 适配虚拟按键
//        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (resultAnimeView.getMeasuredWidth() > 0) {
//                    int expectedWidth = (int) (resultAnimeView.getMeasuredWidth() * 0.54);
//                    if (recyclerView.getMeasuredWidth() != expectedWidth) {
//                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView
//                                .getLayoutParams();
//                        params.width = expectedWidth;
//                        LayoutParamsUtil.setViewLayoutParams(recyclerView, params);
//                    }
//                }
//            }
//        });
//        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim
//                .anim_livevido_arts_answer_result_alpha_in);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.VERTICAL,
//                false));
//        final RCommonAdapter<AnswerResultEntity.Answer > mAdapter = new RCommonAdapter(mContext,mData.getAnswerList());
//        mAdapter.addItemViewDelegate(1,new ItemHolder());
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//
//
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                logger.e("=======> getItemOffsets:" + recyclerView.computeVerticalScrollRange());
//                if (mAdapter.getItemCount() > 1) {
//                    int itemPosition = parent.getChildAdapterPosition(view);
//                    int left = 0;
//                    int right = 0;
//                    int top = 0;
//                    int bottom = 0;
//                    if (itemPosition >= SPAN_COUNT) {
//                        top = SizeUtils.Dp2Px(mContext, 8);
//                    }
//                    outRect.set(left, top, right, bottom);
//                } else {
//
//                    int widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.getMeasuredWidth(), View
//                            .MeasureSpec.EXACTLY);
//                    int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    view.measure(widthSpec, heightSpec);
//                    int topMargin = (recyclerView.getLayoutParams().height - view.getMeasuredHeight()) / 2;
//                    outRect.set(0, topMargin < 0 ? 0 : topMargin, 0, 0);
//                }
//            }
//        });
//        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
//       // Point point = new Point();
//       // ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
//        // int realY = Math.min(point.x, point.y);
//      //  params.topMargin = (int) (realY * 0.30);
//      //  recyclerView.setLayoutParams(params);
//        recyclerView.startAnimation(alphaAnimation);
//        if (mStateListener != null) {
//
//            mStateListener.onCompeletShow();
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_arts_answer_result_answer_btn) {
            revealAnswerResult();
        }
    }

    private int getColor(int corlorId) {
        return mContext.getResources().getColor(corlorId);
    }

    @Override
    public void close() {

        answerListShowing = false;
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (mView.getParent() != null) {
                    ((ViewGroup) mView.getParent()).removeView(mView);
                }
            }
        });
    }


    /**
     * 自动关闭统计面板
     *
     * @param timeDelay 延时时间
     */
    private void autoClose(long timeDelay) {

    }


    @Override
    public void remindSubmit() {

    }

    @Override
    public View getRootLayout() {
        return this.getRootView();
    }


}
