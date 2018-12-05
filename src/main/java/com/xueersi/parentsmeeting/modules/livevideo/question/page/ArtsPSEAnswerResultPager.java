package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
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
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsAnswerResultLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsAnswerStateLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsAnswerStateNoEnergyLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ArtsAnswerTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文科小学英语答题结果页面
 *
 * @author chenkun
 * @version 1.0, 2018/7/26 下午1:59
 */

public class ArtsPSEAnswerResultPager extends BasePager implements IArtsAnswerRsultDisplayer, View.OnClickListener {

    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "arts_answer_result/";
    /** 游戏试题类型 */
    private static final int TYPE_GAME = 12;
    private final String TAG = "ArtsPSEAnswerResultPager";
    protected Logger logger = LoggerFactory.getLogger("ArtsPSEAnswerResultPager");

    /** 强制提交 展示答题结果 延时自动关闭 **/
    private final long AUTO_CLOSE_DELAY = 2000;
    /**
     * 关闭按钮 尺寸
     */
    private final int CLOSEBTN_HEIGHT = 37;
    private final int CLOSEBTN_WIDTH = 37;

    /**
     * 单选题 答案 展示item  距离顶部的  距离
     */
    private final int SINGLE_ANSWER_TOPMARGIN = 30;

    /**
     * 答案列表 开始展示的时间点
     */
    private static final float FRACTION_RECYCLERVIEW_IN = 0.27f;
    /** 关闭按钮出现时间 */
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
    /** 当前答案状态 */
    private int resultType;

    public static final int RESULT_TYPE_CORRECT = 2;
    public static final int RESULT_TYPE_PART_CORRECT = 1;
    public static final int RESULT_TYPE_ERRRO = 0;
    private AnswerResultStateListener mStateListener;

    public ArtsPSEAnswerResultPager(Context context, AnswerResultEntity entity, AnswerResultStateListener stateListener) {
        super(context);
        mData = entity;
        this.mStateListener = stateListener;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_arts_anwserresult_pse, null);
        animationView = view.findViewById(R.id.lv_arts_answer_state_pse);
        resultAnimeView = view.findViewById(R.id.lv_arts_answer_result_pse);
        rlAnswerRootLayout = view.findViewById(R.id.rl_arts_pse_answer_result_root);
        ivLookAnswer = view.findViewById(R.id.iv_arts_answer_result_answer_btn);
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

    }


    @Override
    public void showAnswerReuslt() {

        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_public";
        resultType = mData.getIsRight();

        String lottieJsonPath = null;
        String titleFilePath = null;
        String titleBgPath = null;
        final LottieEffectInfo lottieEffectInfo;
        logger.d("showAnswerReuslt:resultType=" + resultType);
        if (resultType == RESULT_TYPE_CORRECT) {
            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_correct/huo.json";
            titleFilePath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_correct/images/img_17.png";
            titleBgPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_correct/images/img_18.png";

            ArtsAnswerStateLottieEffectInfo effectInfo = new ArtsAnswerStateLottieEffectInfo(lottieResPath,
                    lottieJsonPath, "img_15.png", "img_16.png", "img_17.png", "img_18.png");
            effectInfo.setTilteFilePath(titleFilePath);
            effectInfo.setTitleBgFilePath(titleBgPath);
            effectInfo.setCoinStr("+" + mData.getGold());
            effectInfo.setEnergyStr("+" + mData.getEnergy());
            lottieEffectInfo = effectInfo;

        } else if (resultType == RESULT_TYPE_PART_CORRECT) {
            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_part_correct/data.json";
            titleFilePath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_part_correct/images/img_17.png";
            titleBgPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_part_correct/images/img_18.png";

            ArtsAnswerStateLottieEffectInfo effectInfo = new ArtsAnswerStateLottieEffectInfo(lottieResPath,
                    lottieJsonPath, "img_15.png", "img_16.png", "img_17.png", "img_18.png");
            effectInfo.setTilteFilePath(titleFilePath);
            effectInfo.setTitleBgFilePath(titleBgPath);
            effectInfo.setCoinStr("+" + mData.getGold());
            effectInfo.setEnergyStr("+" + mData.getEnergy());
            lottieEffectInfo = effectInfo;

        } else {
            String imgDir = "arts_answer_result/result_state_error/images";
            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_state_error/data.json";
            ArtsAnswerStateNoEnergyLottieEffectInfo effectInfo = new ArtsAnswerStateNoEnergyLottieEffectInfo(imgDir,
                    lottieJsonPath, "img_14.png");
            effectInfo.setEnergyStr("+" + mData.getEnergy());
            lottieEffectInfo = effectInfo;
        }

        animationView.setAnimationFromJson(lottieEffectInfo.getJsonStrFromAssets(mContext));
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                logger.d("showAnswerReuslt:FileName=" + lottieImageAsset.getFileName());
                return lottieEffectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });

        animationView.playAnimation();
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationView.setVisibility(View.GONE);
                animationView.setImageAssetDelegate(null);
                animationView.removeAllAnimatorListeners();
                animationView.removeAllUpdateListeners();
                if (isGameResult()) {
                    //游戏答题结果 只展示金币UI
                    if (getRootView() != null && getRootView().getParent() != null) {
                        ((ViewGroup) getRootView().getParent()).removeView(getRootView());
                    }
                    mStateListener.onCompeletShow();
                } else {
                    displayDetailUi();
                }
            }
        });

    }

    /** 是否是新课件平台的 游戏答题结果 */
    private boolean isGameResult() {
        return mData != null && mData.getType() == TYPE_GAME;
    }


    /**
     * 添加 关闭 按钮
     */
    private void addCloseBtn() {
        closeBtnAdded = true;
        ImageView closeBtn = new ImageView(mContext);
        if (mData.isVoice == 1) {
            closeBtn.setImageResource(R.drawable.selector_live_enpk_shell_window_guanbi_btn);
        } else {
            closeBtn.setImageResource(R.drawable.selector_live_answer_result_close);
        }
        closeBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int hieght = SizeUtils.Dp2Px(mContext, CLOSEBTN_HEIGHT);
        int width = SizeUtils.Dp2Px(mContext, CLOSEBTN_WIDTH);

        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int screenWidth = Math.min(point.x, point.y);
        int screenHeight = Math.max(point.x, point.y);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, hieght);
        layoutParams.topMargin = (int) (screenWidth * 0.122f);
        layoutParams.rightMargin = (int) (screenHeight * 0.180f);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlAnswerRootLayout.addView(closeBtn, layoutParams);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.e("========> close Btn called:");
                if (mData.isVoice == 1) {
                    if (mStateListener != null) {
                        mStateListener.onAutoClose(ArtsPSEAnswerResultPager.this);
                    }
                }else {
                    hideAnswerReuslt();
                }
            }
        });
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_close_btn_in);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.23f));
        closeBtn.startAnimation(scaleAnimation);
        if (mData.isVoice == 1){
            final TextView textView = mView.findViewById(R.id.tv_arts_answer_result_pse_close);
            textView.setVisibility(View.VISIBLE);
            final AtomicInteger integer = new AtomicInteger(5);
            setCloseText(textView, integer);
            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int count = integer.decrementAndGet();
                    if (count == 0) {
                        answerListShowing = false;
                        if (mStateListener != null) {
                            mStateListener.onAutoClose(ArtsPSEAnswerResultPager.this);
                        } else {
                            ViewGroup group = (ViewGroup) mView.getParent();
                            group.removeView(mView);
                        }
                    } else {
                        setCloseText(textView, integer);
                        textView.postDelayed(this, 1000);
                    }
                }
            }, 1000);
        }
    }

    private void setCloseText(TextView textView, AtomicInteger integer) {
//        SpannableStringBuilder spannable = new SpannableStringBuilder(integer + "s后关闭");
//        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF7A1D")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(integer + "s后关闭");
    }

    /**
     * 隐藏 答题结果
     */
    private void hideAnswerReuslt() {
        ivLookAnswer.setVisibility(View.VISIBLE);
        rlAnswerRootLayout.setVisibility(View.INVISIBLE);
        mView.setBackgroundColor(Color.TRANSPARENT);
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
        // addCloseBtn();
        String lottieResPath = null;
        String lottieJsonPath = null;

        if (resultType == RESULT_TYPE_PART_CORRECT) {

            lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_part_correct/images";
            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_part_correct/data.json";

        } else if (resultType == RESULT_TYPE_CORRECT) {
            lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_correct/images";
            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_correct/data.json";

        } else if (resultType == RESULT_TYPE_ERRRO) {
            lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_error/images";
            lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "result_error/data.json";
        }

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
                    if (1 == mData.isVoice) {
                        showAnswer();
                    } else {
                        showAnswerList();
                    }
                }
                if (animation.getAnimatedFraction() >= FRACTION_SHOW_CLOSEBTN && !closeBtnAdded) {
                    addCloseBtn();
                }
            }
        });
    }

    private void showAnswer() {
        answerListShowing = true;
        ViewStub vs_arts_answer_result_voice = rlAnswerRootLayout.findViewById(R.id.vs_arts_answer_result_voice);
        logger.d("showAnswer:vs_arts_answer_result_voice=" + vs_arts_answer_result_voice);
        if (vs_arts_answer_result_voice == null) {
            return;
        }
        View view = vs_arts_answer_result_voice.inflate();
        TextView tv_arts_answer_result_voice_my = view.findViewById(R.id.tv_arts_answer_result_voice_my);
        TextView tv_arts_answer_result_voice_right = view.findViewById(R.id.tv_arts_answer_result_voice_right);
//        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim
//                .anim_livevido_arts_answer_result_alpha_in);
//        view.startAnimation(alphaAnimation);
        List<AnswerResultEntity.Answer> answerList = mData.getAnswerList();
        if (answerList.size() == 1) {
            AnswerResultEntity.Answer answer = answerList.get(0);
            String myAnswer = "";
            if (AnswerResultEntity.TEST_TYPE_2 == answer.getTestType()) {
                List<String> choiceList = answer.getChoiceList();
                if (choiceList != null) {
                    for (int i = 0; i < choiceList.size(); i++) {
                        myAnswer += choiceList.get(i);
                    }
                }
            } else {
                tv_arts_answer_result_voice_my.setVisibility(View.GONE);
            }
            SpannableString spannableStringBuilder = new SpannableString("你的答案：" + myAnswer);
            if (answer.getIsRight() == 0) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFFE65453), 5, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFF77AF1F), 5, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv_arts_answer_result_voice_my.setText(spannableStringBuilder);
            String rightAnswer = "";
            List<String> rightAnswers = answer.getRightAnswers();
            if (rightAnswers != null) {
                for (int i = 0; i < rightAnswers.size(); i++) {
                    rightAnswer += rightAnswers.get(i);
                }
            }
            spannableStringBuilder = new SpannableString("正确答案：" + rightAnswer);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFF77AF1F), 5, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_arts_answer_result_voice_right.setText(spannableStringBuilder);
        }
        mStateListener.onCompeletShow();
    }

    private void showAnswerList() {
        answerListShowing = true;
        logger.e("=====>showAnswerList called");
        recyclerView = mView.findViewById(R.id.rcl_arts_answer_result_detail);
        recyclerView.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(mContext, R.anim
                .anim_livevido_arts_answer_result_alpha_in);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.VERTICAL,
                false));
        final AnswerResultAdapter mAdapter = new AnswerResultAdapter(mData.getAnswerList());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {


            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                logger.e("=======> getItemOffsets:" + recyclerView.computeVerticalScrollRange());
                if (mAdapter.getItemCount() > 1) {
                    int itemPosition = parent.getChildAdapterPosition(view);
                    int left = 0;
                    int right = 0;
                    int top = 0;
                    int bottom = 0;
                    if (itemPosition >= SPAN_COUNT) {
                        top = SizeUtils.Dp2Px(mContext, 8);
                    }
                    outRect.set(left, top, right, bottom);
                } else {

                    int widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
                    int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    view.measure(widthSpec, heightSpec);
                    int topMargin = (recyclerView.getLayoutParams().height - view.getMeasuredHeight()) / 2;
                    outRect.set(0, topMargin < 0 ? 0 : topMargin, 0, 0);
                }
            }
        });
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int realY = Math.min(point.x, point.y);
        params.topMargin = (int) (realY * 0.30);
        recyclerView.setLayoutParams(params);
        recyclerView.startAnimation(alphaAnimation);
        mStateListener.onCompeletShow();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_arts_answer_result_answer_btn) {
            revealAnswerResult();
        }

    }

    private static class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvStanderAnswer;
        private ArtsAnswerTextView tvAnswer;
        private TextView tvStanderAnswerBelow;
        private TextView tvIndex;

        private final int STATE_CODE_RIGHT = 2;
        private final int STATE_CODE_PARTRIGHT = 1;
        private final int STATE_CODE_WRONG = 0;


        public ItemHolder(View itemView) {
            super(itemView);
            tvStanderAnswer = itemView.findViewById(R.id.tv_arts_answer_result_stander_answer);
            tvAnswer = itemView.findViewById(R.id.tv_arts_answer_result_answer);
            tvStanderAnswerBelow = itemView.findViewById(R.id.tv_arts_answer_result_stander_answer_below);
            tvIndex = itemView.findViewById(R.id.tv_arts_answer_result_answer_index);
        }

        public void bindData(AnswerResultEntity.Answer answer, int position) {

            if (tvIndex != null) {
                tvIndex.setText((position + 1) + "");
            }

            if (isSelect(answer)) {
                tvStanderAnswer.setVisibility(answer.getIsRight() == STATE_CODE_RIGHT ? View.GONE : View.VISIBLE);
                tvStanderAnswer.setText(listToStr(answer.getRightAnswers(), null));
                tvStanderAnswerBelow.setVisibility(View.GONE);
                tvAnswer.setTextWithIcon("你的答案:" + listToStr(answer.getChoiceList(), null));
            } else {
                tvStanderAnswer.setVisibility(View.GONE);
                tvAnswer.setTextWithIcon(listToStr(answer.getBlankList(), "、"));
                tvStanderAnswerBelow.setVisibility(answer.getIsRight() == STATE_CODE_RIGHT ? View.GONE : View.VISIBLE);
                tvStanderAnswerBelow.setText(listToStr(answer.getRightAnswers(), "、"));
            }

            int iconResId = 0;
            if (answer.getIsRight() == STATE_CODE_RIGHT) {
                iconResId = R.drawable.livevideo_pse_answer_correct;
            } else if (answer.getIsRight() == STATE_CODE_PARTRIGHT) {
                iconResId = R.drawable.livevideo_pse_answer_partcorrect;
            } else if (answer.getIsRight() == STATE_CODE_WRONG) {
                iconResId = R.drawable.livevideo_pse_answer_error;
            }

            if (iconResId != 0) {
                tvAnswer.setIconResId(iconResId);
            }
        }


        private boolean isSelect(AnswerResultEntity.Answer data) {

            return data.getTestType() == 2;
        }


        /**
         * @param data
         * @param splitStr 分割字符串
         * @return
         */
        private String listToStr(List<String> data, String splitStr) {
            StringBuilder stringBuilder = new StringBuilder();
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    if (i < (data.size() - 1) && splitStr != null) {
                        stringBuilder.append(data.get(i)).append(splitStr);
                    } else {
                        stringBuilder.append(data.get(i));
                    }
                }
            }

            return stringBuilder.toString();
        }
    }


    private static class AnswerResultAdapter extends RecyclerView.Adapter {

        final int ITEM_TYPE_SINGLE = 1;
        final int ITEM_TYPE_MULTI = 2;
        List<AnswerResultEntity.Answer> answerList;

        public AnswerResultAdapter(List<AnswerResultEntity.Answer> data) {
            answerList = data;
        }


        @Override
        public int getItemViewType(int position) {
            return getItemCount() > 1 ? ITEM_TYPE_MULTI : ITEM_TYPE_SINGLE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (ITEM_TYPE_SINGLE == viewType) {
                return new ItemHolder(View.inflate(parent.getContext(), R.layout
                        .item_arts_pse_answerresult_single, null));
            } else {
                return new ItemHolder(View.inflate(parent.getContext(), R.layout
                        .item_arts_pse_answerresult_multi, null));
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemHolder) holder).bindData(answerList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return answerList == null ? 0 : answerList.size();
        }

    }


    @Override
    public void close() {

        answerListShowing = false;
        mView.post(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) mView.getParent()).removeView(mView);
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
