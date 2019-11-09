package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultMember;
import com.xueersi.parentsmeeting.modules.livevideo.question.item.SpeechResultOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linyuqiang
 * 语音答题结果页
 */
public class SpeechResultPager extends LiveBasePager {
    private ViewGroup group;
    private ImageView ivSpeechResultTitle;
    private ImageView ivSpeechResultClose;
    private TextView tvSpeechResultScore;
    private ImageView civSpeechResultHead;
    /** 段位 */
    private ImageView ivUserSegment;
    private View vSpeechResultLine;
    private RecyclerView rvSpeechResultOther;
    /** 准确度 */
    private TextView tvSpeechResultAccuracyText;
    /** 流畅度 */
    private TextView tvSpeechResultFluencyText;
    /** 金币 */
    private TextView tvSpeechResultMyGold;
    /** 能量 */
    private TextView tvSpeechResultMyEnergy;
    /** 点赞 */
    private TextView tvSpeechResultMyPraise;
    private SpeechResultEntity speechResultEntity;
    private View rlSpeechResultContent;
    private static int[] titleres = {R.drawable.app_livevideo_enteampk_shellwindow_nicework3_img_nor1, R.drawable.app_livevideo_enteampk_shellwindow_tryharder2_img_nor,
            R.drawable.app_livevideo_enteampk_shellwindow_nicework3_img_nor, R.drawable.app_livevideo_enteampk_shellwindow_goodjob4_img_nor,
            R.drawable.app_livevideo_enteampk_shellwindow_fantastic5_img_nor};
    private LiveGetInfo liveGetInfo;
    /** 单人 */
    private boolean isSingle;

    public SpeechResultPager(Context context, ViewGroup group, SpeechResultEntity speechResultEntity, LiveGetInfo liveGetInfo) {
        super(context, false);
        this.group = group;
        this.speechResultEntity = speechResultEntity;
        isSingle = speechResultEntity.speechResultMembers.isEmpty();
        this.liveGetInfo = liveGetInfo;
        mView = initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_speech_result, group, false);
        ivSpeechResultTitle = view.findViewById(R.id.iv_live_speech_result_title);
        rlSpeechResultContent = view.findViewById(R.id.rl_live_speech_result_content);
        if (speechResultEntity.praise == -1) {
            ViewStub vs_live_speech_result_myenergy = view.findViewById(R.id.vs_live_speech_result_myenergy);
            View view1 = vs_live_speech_result_myenergy.inflate();
            if (!liveGetInfo.getSmallEnglish()) {
                view1.findViewById(R.id.iv_live_speech_result_myenergy).setVisibility(View.GONE);
                view1.findViewById(R.id.tv_live_speech_result_myenergy).setVisibility(View.GONE);
                View iv_live_speech_result_mygold = view1.findViewById(R.id.iv_live_speech_result_mygold);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) iv_live_speech_result_mygold.getLayoutParams();
                layoutParams.leftMargin = 0;
                iv_live_speech_result_mygold.setLayoutParams(layoutParams);
            }
            //单人的金币能量位置靠下
            if (isSingle) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view1.getLayoutParams();
                lp.topMargin = SizeUtils.Dp2Px(mContext, 0);
                view1.setLayoutParams(lp);
            }
        } else {
            ViewStub vs_live_speech_result_roleplay_myenergy = view.findViewById(R.id.vs_live_speech_result_roleplay_myenergy);
            View view1 = vs_live_speech_result_roleplay_myenergy.inflate();
            if (!liveGetInfo.getSmallEnglish()) {
                view1.findViewById(R.id.iv_live_speech_result_myenergy).setVisibility(View.GONE);
                view1.findViewById(R.id.tv_live_speech_result_myenergy).setVisibility(View.GONE);
                View iv_live_speech_result_mygold = view1.findViewById(R.id.iv_live_speech_result_mygold);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) iv_live_speech_result_mygold.getLayoutParams();
                layoutParams.leftMargin = 0;
                iv_live_speech_result_mygold.setLayoutParams(layoutParams);
            }
            tvSpeechResultMyPraise = view.findViewById(R.id.tv_live_speech_result_mypraise);
            //单人的金币能量位置靠下
            if (isSingle) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view1.getLayoutParams();
                lp.topMargin = SizeUtils.Dp2Px(mContext, 0);
                view1.setLayoutParams(lp);
            }
        }
        ivSpeechResultClose = view.findViewById(R.id.iv_live_speech_result_close);
        tvSpeechResultScore = view.findViewById(R.id.tv_live_speech_result_score);
        civSpeechResultHead = view.findViewById(R.id.civ_live_speech_result_head);
        ivUserSegment = view.findViewById(R.id.iv_live_speech_result_head_segment);
        vSpeechResultLine = view.findViewById(R.id.v_live_speech_result_line);
        rvSpeechResultOther = view.findViewById(R.id.rv_live_speech_result_other);
        tvSpeechResultAccuracyText = view.findViewById(R.id.tv_live_speech_result_accuracy_text);
        tvSpeechResultFluencyText = view.findViewById(R.id.tv_live_speech_result_fluency_text);
        tvSpeechResultMyGold = view.findViewById(R.id.tv_live_speech_result_mygold);
        tvSpeechResultMyEnergy = view.findViewById(R.id.tv_live_speech_result_myenergy);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        tvSpeechResultScore.setText(speechResultEntity.score + "分");
        tvSpeechResultAccuracyText.setText("" + speechResultEntity.accuracy);
        tvSpeechResultFluencyText.setText("" + speechResultEntity.fluency);
        tvSpeechResultMyGold.setText("+" + speechResultEntity.gold);
        tvSpeechResultMyEnergy.setText("+" + speechResultEntity.energy);
        if (tvSpeechResultMyPraise != null) {
            tvSpeechResultMyPraise.setText("" + speechResultEntity.praise);
        }
        ImageLoader.with(ContextManager.getContext()).load(speechResultEntity.headUrl).error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civSpeechResultHead);

        if (ProxUtil.getProxUtil().get(mContext, BetterMeContract.BetterMePresenter.class) != null) {
            StuSegmentEntity stuSegmentEntity = ProxUtil.getProxUtil().get(mContext, BetterMeContract
                    .BetterMePresenter.class).getStuSegmentEntity();
            if (stuSegmentEntity != null) {
                int segmentType = stuSegmentEntity.getSegmentType();
                int star = stuSegmentEntity.getStar();
                BetterMeUtil.addSegment(ivUserSegment, segmentType, star);
            }
        }

        //单人的
        if (isSingle) {
            rvSpeechResultOther.setVisibility(View.GONE);
            vSpeechResultLine.setVisibility(View.GONE);
            //单人的分数比布局的靠下一点
            RelativeLayout.LayoutParams contentLp = (RelativeLayout.LayoutParams) rlSpeechResultContent.getLayoutParams();
            contentLp.topMargin = SizeUtils.Dp2Px(mContext, 112);
            rlSpeechResultContent.setLayoutParams(contentLp);
        } else {
            //多人的
            ArrayList<SpeechResultMember> speechResultMembers = speechResultEntity.speechResultMembers;
            vSpeechResultLine.setVisibility(View.VISIBLE);
            rvSpeechResultOther.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            RCommonAdapter<SpeechResultMember> adapter = new RCommonAdapter<>(mContext, speechResultMembers);
            adapter.addItemViewDelegate(new SpeechResultOtherItem());
            rvSpeechResultOther.setAdapter(adapter);
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rv_live_speech_result_other.getLayoutParams();
//            lp.width = (int) (85 * speechResultMembers.size() * ScreenUtils.getScreenDensity());
//            rv_live_speech_result_other.setLayoutParams(lp);
        }
        int score = speechResultEntity.score;
        int progress;
        if (score < 40) {
            progress = 1;
        } else if (score < 60) {
            progress = 2;
        } else if (score < 75) {
            progress = 3;
        } else if (score < 90) {
            progress = 4;
        } else {
            progress = 5;
        }
        ivSpeechResultTitle.setImageResource(titleres[progress - 1]);
        final TextView textView = mView.findViewById(R.id.tv_arts_answer_result_pse_close);
        textView.setVisibility(View.VISIBLE);
        final AtomicInteger integer = new AtomicInteger(5);
        setCloseText(textView, integer);
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = integer.decrementAndGet();
                ViewGroup group = (ViewGroup) mView.getParent();
                if (count == 0) {
                    if (onPagerClose != null) {
                        onPagerClose.onClose(SpeechResultPager.this);
                    } else {
                        if (group != null) {
                            group.removeView(mView);
                        }
                    }
                } else {
                    if (group != null) {
                        setCloseText(textView, integer);
                        textView.postDelayed(this, 1000);
                    }
                }
            }
        }, 1000);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivSpeechResultClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPagerClose != null) {
                    onPagerClose.onClose(SpeechResultPager.this);
                } else {
                    if (group != null) {
                        group.removeView(mView);
                    }
                }
            }
        });
    }

    private void setCloseText(TextView textView, AtomicInteger integer) {
//        SpannableStringBuilder spannable = new SpannableStringBuilder(integer + "s后关闭");
//        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF7A1D")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(integer + "s后关闭");
    }

}
