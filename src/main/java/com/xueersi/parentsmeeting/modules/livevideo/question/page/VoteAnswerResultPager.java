package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsAnswerResultLottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.widget.VoteView;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 投票题结果页面
 *
 * @author wuqingfeng
 * @version 1.0, 2019/6/24
 */

public class VoteAnswerResultPager extends BasePager implements IArtsAnswerRsultDisplayer, View.OnClickListener {

    private final String TAG = "VoteAnswerResultPager";
    protected Logger logger = LoggerFactory.getLogger("VoteAnswerResultPager");

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
     * 答案列表 开始展示的时间点
     */
    private static final float FRACTION_RECYCLERVIEW_IN = 0.08f;
    /**
     * 关闭按钮出现时间
     */
    private static final float FRACTION_SHOW_CLOSEBTN = 0.05f;

    private LottieAnimationView animationView;
    private LottieAnimationView resultAnimeView;
    private RelativeLayout rlAnswerRootLayout;
    private ImageView ivLookAnswer;
    private String resultData;
    private JSONArray optionTitleArray;
    private int isforce = 1;
    private int gold;
    private Boolean isPlayBack;
    private String playBackVoteData;
    private final String BG_COLOR = "#CC000000";

    private AnswerResultStateListener mStateListener;

    /**
     * 奖励布局
     */
    LinearLayout llRewardInfo;
    ViewPager viewPager;
    VotePagerAdapter votePagerAdapter;
    private List<HashMap> mData;
    LinearLayout llLiveVideoVoteDian;
    ImageView ivLiveVideoVoteDianOne;
    ImageView ivLiveVideoVoteDianTwo;
    ImageView ivLiveVideoVoteLeft;
    ImageView ivLiveVideoVoteRight;
    /**
     * 金币数量
     */
    FangZhengCuYuanTextView tvGoldCount;

    private TcpMessageReg tcpMessageReg;
    private VoteTcpMessage voteTcpMessage;
    protected Handler handler = new Handler(Looper.getMainLooper());
    private Boolean closeVote = false;
    private int foldCount = 0;
    private int pattern = 0;
    VoteVideoSize voteVideoSize;

    public VoteAnswerResultPager(Context context, String result, int pattern, AnswerResultStateListener stateListener) {
        super(context);
        resultData = result;
        this.mStateListener = stateListener;
        this.pattern = pattern;
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
        tvGoldCount = view.findViewById(R.id.tv_live_speech_result_mygold);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        llLiveVideoVoteDian = (LinearLayout) view.findViewById(R.id.ll_livevideo_vote_dian);
        ivLiveVideoVoteDianOne = (ImageView) view.findViewById(R.id.iv_livevideo_vote_dian_one);
        ivLiveVideoVoteDianTwo = (ImageView) view.findViewById(R.id.iv_livevideo_vote_dian_two);
        ivLiveVideoVoteLeft = (ImageView) view.findViewById(R.id.iv_livevideo_vote_left);
        ivLiveVideoVoteRight = (ImageView) view.findViewById(R.id.iv_livevideo_vote_right);
        ivLiveVideoVoteLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        ivLiveVideoVoteRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        mData = new ArrayList<>();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int positon) {
                // 滑动结束
                changeView(positon);
            }

            @Override
            public void onPageScrolled(int positon, float positonOffset, int positonOffsetPx) {
                // 滑动过程
            }

            @Override
            public void onPageScrollStateChanged(int positon) {

            }
        });

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

    private void changeView(int position) {
        switch (position) {
            case 0:
                ivLiveVideoVoteDianOne.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_dian_tu));
                ivLiveVideoVoteDianTwo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_dian_ao));
                ivLiveVideoVoteLeft.setEnabled(false);
                ivLiveVideoVoteLeft.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_left_diss));
                ivLiveVideoVoteRight.setEnabled(true);
                ivLiveVideoVoteRight.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_right_click));
                break;
            case 1:
                ivLiveVideoVoteDianOne.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_dian_ao));
                ivLiveVideoVoteDianTwo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_dian_tu));
                ivLiveVideoVoteLeft.setEnabled(true);
                ivLiveVideoVoteLeft.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_left_click));
                ivLiveVideoVoteRight.setEnabled(false);
                ivLiveVideoVoteRight.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_vote_right_diss));
                break;
        }
    }

    List<VoteView> voteViewList = new ArrayList<VoteView>();

    class VotePagerAdapter extends PagerAdapter {
        private List<HashMap> mData;

        public VotePagerAdapter(List<HashMap> list) {
            mData = list;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.fragment_vote, null);
       //     UmsAgentManager.umsAgentDebug(mContext,"vote_version_1","vote_version");
            VoteView voteView = view.findViewById(R.id.ll_vote);
            voteView.updateVote(mData.get(position), resultData);
            container.addView(view);
            voteViewList.add(voteView);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    class VoteVideoSize implements LiveVideoPoint.VideoSizeChange {

        @Override
        public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivLookAnswer.getLayoutParams();
            if (pattern == 1) {
                params.rightMargin = LiveVideoPoint.getInstance().getRightMargin() + SizeUtils.Dp2Px(mContext, 7);
                params.bottomMargin = getBottomMargin() + SizeUtils.Dp2Px(mContext, 7);
            } else {
                params.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x4 + SizeUtils.Dp2Px(mContext, 7);
                params.bottomMargin = SizeUtils.Dp2Px(mContext, 7);
            }
            ivLookAnswer.setLayoutParams(params);
        }
    }

    /**
     * 获取查看按钮底部距离
     */
    public int getBottomMargin() {
        return (LiveVideoPoint.getInstance().screenHeight - ((LiveVideoPoint.getInstance().x3 - LiveVideoPoint.getInstance().x2) * 9 / 16)) / 2;
    }

    @Override
    public void initData() {
        voteVideoSize = new VoteVideoSize();
        LiveVideoPoint.getInstance().addVideoSizeChange(mContext, voteVideoSize);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivLookAnswer.getLayoutParams();
        if (pattern == 1) {
            params.rightMargin = LiveVideoPoint.getInstance().getRightMargin() + SizeUtils.Dp2Px(mContext, 7);
            params.bottomMargin = getBottomMargin() + SizeUtils.Dp2Px(mContext, 7);
        } else {
            params.rightMargin = LiveVideoPoint.getInstance().screenWidth - LiveVideoPoint.getInstance().x4 + SizeUtils.Dp2Px(mContext, 7);
            params.bottomMargin = SizeUtils.Dp2Px(mContext, 7);
        }
        ivLookAnswer.setLayoutParams(params);
        try {
            JSONObject jsonObject = new JSONObject(resultData);
            JSONArray jsonArray = jsonObject.optJSONArray("answerData");
            if (jsonArray.length() > 0) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                resultData = jsonObject1.optString("useranswer");
            }
            optionTitleArray = jsonObject.optJSONArray("optionTitle");
            isforce = jsonObject.optInt("isForce");
            gold = jsonObject.optInt("gold");
            isPlayBack = jsonObject.optBoolean("isPlayBack");
            playBackVoteData = jsonObject.optString("data");
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(TAG, e);
        }
        if (!isPlayBack) {
            tcpMessageReg = ProxUtil.getProxUtil().get(mContext, TcpMessageReg.class);
            if (tcpMessageReg != null) {
                voteTcpMessage = new VoteTcpMessage();
            }
            tcpMessageReg.registTcpMessageAction(voteTcpMessage);
        }
    }


    /**
     * 如果是老师强制收题的话，直接展示结果页
     */
    @Override
    public void showAnswerReuslt() {
        if (isforce != 1 || isPlayBack) {
            forceSubmit();
        } else {
            displayDetailUi();
        }
    }

    int latestMeasuerWidth;
    final ImageView closeBtn = new ImageView(mContext);
    TextView tvClose;

    /**
     * 添加 关闭 按钮
     */
    private void addCloseBtn() {
        closeBtnAdded = true;
        if (isforce != 1 || isPlayBack) {
            closeVote = true;
            closeBtn.setImageResource(R.drawable.selector_live_enpk_shell_window_guanbi_btn);
        } else {
            closeBtn.setImageResource(R.drawable.selector_live_vote_shell_window_fold_btn);
        }
        closeBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeVote) {
                    closeReusltUi();
                    if (!isPlayBack)
                        mStateListener.onUpdateVoteFoldCount(String.valueOf(foldCount));
                } else {
                    closeAnswerResult();
                    foldCount++;
                }
            }
        });


        final ViewTreeObserver viewTreeObserver = resultAnimeView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (resultAnimeView.getMeasuredWidth() > 0 && latestMeasuerWidth != resultAnimeView.getMeasuredWidth
                        ()) {
                    latestMeasuerWidth = resultAnimeView.getMeasuredWidth();
                    int designWidth = SizeUtils.Dp2Px(resultAnimeView.getContext(), 640f);
                    int designHeight = SizeUtils.Dp2Px(resultAnimeView.getContext(), 360f);
                    float scaleX = (latestMeasuerWidth * 1.0f) / designWidth;
                    float scaleY = (resultAnimeView.getMeasuredHeight() * 1.0f) / designHeight;
                    float scale = Math.min(scaleX, scaleY);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) closeBtn.getLayoutParams();
                    if (params == null) {
                        params = new RelativeLayout.LayoutParams(SizeUtils.Dp2Px(mContext, CLOSEBTN_WIDTH), SizeUtils.Dp2Px(mContext, CLOSEBTN_HEIGHT));
                    }
                    int offset = (int) ((1.0f - scale) * SizeUtils.Dp2Px(resultAnimeView.getContext(), 35f));
                    params.rightMargin = (int) (SizeUtils.Dp2Px(resultAnimeView.getContext(), 125f) * scale) - offset;
                    params.topMargin = (int) (SizeUtils.Dp2Px(resultAnimeView.getContext(), 55f) / scale) + offset;
                    params.addRule(RelativeLayout.ALIGN_TOP, R.id.lv_arts_answer_result_pse);
                    params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.lv_arts_answer_result_pse);

                    if (closeBtn.getParent() == null) {
                        rlAnswerRootLayout.addView(closeBtn, params);
                        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(mContext, R
                                .anim.anim_livevideo_close_btn_in);
                        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.23f));
                        closeBtn.startAnimation(scaleAnimation);
                    } else {
                        LayoutParamsUtil.setViewLayoutParams(closeBtn, params);
                    }
                    //倒计时位置
                    tvClose = mView.findViewById(R.id.tv_arts_answer_result_pse_close);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvClose
                            .getLayoutParams();
                    layoutParams.rightMargin = params.rightMargin + SizeUtils.Dp2Px(resultAnimeView.getContext(), 37f);
                    layoutParams.topMargin = params.topMargin + SizeUtils.Dp2Px(resultAnimeView.getContext(), 2f);
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.lv_arts_answer_result_pse);
                    layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.lv_arts_answer_result_pse);
                    LayoutParamsUtil.setViewLayoutParams(tvClose, layoutParams);
                    if (isforce != 1 || isPlayBack) {
                        remindSubmit();
                    }
                }
            }
        });

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
     * 显示 答题结果面板
     */
    private void revealAnswerResult() {
        if (rlAnswerRootLayout.getVisibility() != View.VISIBLE) {
            ivLookAnswer.setVisibility(View.INVISIBLE);
            rlAnswerRootLayout.setVisibility(View.VISIBLE);
            mView.setBackgroundColor(Color.parseColor(BG_COLOR));
        }
    }

    /**
     * 隐藏 答题结果面板
     */
    private void closeAnswerResult() {
        ivLookAnswer.setVisibility(View.VISIBLE);
        rlAnswerRootLayout.setVisibility(View.INVISIBLE);
        mView.setBackgroundColor(Color.parseColor("#00000000"));
    }

    private boolean answerListShowing = false;
    private boolean closeBtnAdded = false;
    ArtsAnswerResultLottieEffectInfo effectInfo;

    /**
     * 展示答题详情
     */
    private void displayDetailUi() {
        String lottieResPath = "result_vote/images";
        String lottieJsonPath = "result_vote/data.json";

        effectInfo = new ArtsAnswerResultLottieEffectInfo(lottieResPath,
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
                    if (!TextUtils.isEmpty(resultData))
                        tvGoldCount.setText("+" + String.valueOf(gold));
                    initVoteList();
                }
                if (animation.getAnimatedFraction() >= FRACTION_SHOW_CLOSEBTN && !closeBtnAdded) {
                    addCloseBtn();
                }
            }
        });
    }

    /**
     * 初始化统计页面，多于6个选项分成两页
     */
    private void initVoteList() {
        try {
            if (optionTitleArray.length() < 7) {
                LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
                for (int i = 0; i < optionTitleArray.length(); i++) {
                    map.put(optionTitleArray.get(i).toString(), 0);
                }
                mData.clear();
                mData.add(map);
            } else {
                llLiveVideoVoteDian.setVisibility(View.VISIBLE);
                ivLiveVideoVoteLeft.setVisibility(View.VISIBLE);
                ivLiveVideoVoteRight.setVisibility(View.VISIBLE);
                LinkedHashMap<String, Integer> map1 = new LinkedHashMap<String, Integer>();
                LinkedHashMap<String, Integer> map2 = new LinkedHashMap<String, Integer>();
                for (int i = 0; i < optionTitleArray.length(); i++) {
                    if (i < 6) {
                        map1.put(optionTitleArray.get(i).toString(), 0);
                    } else {
                        map2.put(optionTitleArray.get(i).toString(), 0);
                    }

                }
                mData.clear();
                mData.add(map1);
                mData.add(map2);
            }
            votePagerAdapter = new VotePagerAdapter(mData);
            viewPager.setAdapter(votePagerAdapter);
            answerListShowing = true;
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }

    private void displayAnswerResult() {
        if (isforce == 1 && !isPlayBack) {
            resultAnimeView.updateBitmap("image_13", effectInfo.getBitMap(resultAnimeView));
        }
    }

    /**
     * 更新分页数据
     */
    private void showVoteList(String gold, LinkedHashMap<String, Integer> map) {
        if (isPlayBack) {
            llRewardInfo.setVisibility(View.VISIBLE);
            tvGoldCount.setText("+" + gold);
        }
        if (voteViewList.size() == 1) {
            voteViewList.get(0).updateVote(map, resultData);
        } else if (voteViewList.size() == 2) {
            voteViewList.get(0).updateVote(map, resultData);
            voteViewList.get(1).updateVote(map, resultData);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_arts_answer_result_answer_btn) {
            revealAnswerResult();
        }
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

    /**
     * 如果是老师强制收题的话，直接展示结果页动画
     */
    private void forceSubmit() {
        String lottieResPath = "result_vote/images";
        String lottieJsonPath = "result_vote/data.json";
        String[] targetFileNames = {"img_13.png"};
        final ArtsAnswerResultLottieEffectInfo effectInfo = new ArtsAnswerResultLottieEffectInfo(lottieResPath,
                lottieJsonPath);
        effectInfo.setTargetFileFilter(targetFileNames);
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
                    if (isPlayBack) {
                        initVoteList();
                        //回放直接显示数据
                        analysisVoteData(playBackVoteData);
                    } else {
                        if (!TextUtils.isEmpty(resultData))
                            tvGoldCount.setText("+" + String.valueOf(gold));
                        initVoteList();
                        //直接显示结果页先显示缓存的长连接数据，防止进入页面推送数据不及时
                        analysisVoteData(TcpConstants.VOTE_TYPE_DATA);
                    }
                }
                if (animation.getAnimatedFraction() >= FRACTION_SHOW_CLOSEBTN && !closeBtnAdded) {
                    addCloseBtn();
                }
            }
        });
    }

    @Override
    public void remindSubmit() {
        closeVote = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                revealAnswerResult();
                displayAnswerResult();
                closeBtn.setImageResource(R.drawable.selector_live_enpk_shell_window_guanbi_btn);
                tvClose.setVisibility(View.VISIBLE);
                int time = 3;
                if (isPlayBack) {
                    time = 5;
                }
                final AtomicInteger integer = new AtomicInteger(time);
                setCloseText(tvClose, integer);
                tvClose.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int count = integer.decrementAndGet();
                        if (count == 0) {
                            answerListShowing = false;
                            if (mStateListener != null) {
                                if (!isPlayBack)
                                    mStateListener.onUpdateVoteFoldCount(String.valueOf(foldCount));
                                mStateListener.onAutoClose(VoteAnswerResultPager.this);
                            } else {
                                ViewGroup group = (ViewGroup) mView.getParent();
                                group.removeView(mView);
                            }
                        } else {
                            setCloseText(tvClose, integer);
                            tvClose.postDelayed(this, 1000);
                        }
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tcpMessageReg != null) {
            if (voteTcpMessage != null) {
                tcpMessageReg.unregistTcpMessageAction(voteTcpMessage);
            }
        }
    }

    @Override
    public View getRootLayout() {
        return this.getRootView();
    }

    /**
     * 长连接推送投票数据
     */
    class VoteTcpMessage implements TcpMessageAction {
        @Override
        public void onMessage(short type, int operation, String msg) {
            if (type == TcpConstants.VOTE_TYPE) {
                analysisVoteData(msg);
            }
        }

        @Override
        public short[] getMessageFilter() {
            return new short[]{TcpConstants.VOTE_TYPE};
        }
    }

    /**
     * 解析长连接投票数据
     */
    private void analysisVoteData(String data) {
        try {
            if (!TextUtils.isEmpty(data)) {
                JSONObject jsonObject = new JSONObject(data);
                final int gold = jsonObject.optInt("gold");
                JSONObject voteObj = jsonObject.getJSONObject("vote");
                Iterator iterator = voteObj.keys();
                final LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    map.put(key, voteObj.getInt(key));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showVoteList(String.valueOf(gold), map);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }
}
