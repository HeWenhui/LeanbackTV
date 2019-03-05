package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultMember;
import com.xueersi.parentsmeeting.modules.livevideo.question.item.SpeechResultOtherItem;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linyuqiang
 * 语音答题结果页
 */
public class SpeechResultPager extends LiveBasePager {
    private ViewGroup group;
    private ImageView iv_live_speech_result_title;
    private ImageView iv_live_speech_result_close;
    private TextView tv_live_speech_result_score;
    private ImageView civ_live_speech_result_head;
    private View v_live_speech_result_line;
    private RecyclerView rv_live_speech_result_other;
    private TextView tv_live_speech_result_accuracy_text;
    private TextView tv_live_speech_result_fluency_text;
    private TextView tv_live_speech_result_mygold;
    private TextView tv_live_speech_result_myenergy;
    private TextView tv_live_speech_result_mypraise;
    private SpeechResultEntity speechResultEntity;
    private static int[] titleres = {R.drawable.app_livevideo_enteampk_shellwindow_nicework3_img_nor1, R.drawable.app_livevideo_enteampk_shellwindow_tryharder2_img_nor,
            R.drawable.app_livevideo_enteampk_shellwindow_nicework3_img_nor, R.drawable.app_livevideo_enteampk_shellwindow_goodjob4_img_nor,
            R.drawable.app_livevideo_enteampk_shellwindow_fantastic5_img_nor};
    private LiveGetInfo liveGetInfo;

    public SpeechResultPager(Context context, ViewGroup group, SpeechResultEntity speechResultEntity, LiveGetInfo liveGetInfo) {
        super(context, false);
        this.group = group;
        this.speechResultEntity = speechResultEntity;
        this.liveGetInfo = liveGetInfo;
        mView = initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_speech_result, group, false);
        iv_live_speech_result_title = view.findViewById(R.id.iv_live_speech_result_title);
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
            tv_live_speech_result_mypraise = view.findViewById(R.id.tv_live_speech_result_mypraise);
        }
        iv_live_speech_result_close = view.findViewById(R.id.iv_live_speech_result_close);
        tv_live_speech_result_score = view.findViewById(R.id.tv_live_speech_result_score);
        civ_live_speech_result_head = view.findViewById(R.id.civ_live_speech_result_head);
        v_live_speech_result_line = view.findViewById(R.id.v_live_speech_result_line);
        rv_live_speech_result_other = view.findViewById(R.id.rv_live_speech_result_other);
        tv_live_speech_result_accuracy_text = view.findViewById(R.id.tv_live_speech_result_accuracy_text);
        tv_live_speech_result_fluency_text = view.findViewById(R.id.tv_live_speech_result_fluency_text);
        tv_live_speech_result_mygold = view.findViewById(R.id.tv_live_speech_result_mygold);
        tv_live_speech_result_myenergy = view.findViewById(R.id.tv_live_speech_result_myenergy);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        tv_live_speech_result_score.setText(speechResultEntity.score + "分");
        tv_live_speech_result_accuracy_text.setText("" + speechResultEntity.accuracy);
        tv_live_speech_result_fluency_text.setText("" + speechResultEntity.fluency);
        tv_live_speech_result_mygold.setText("+" + speechResultEntity.gold);
        tv_live_speech_result_myenergy.setText("+" + speechResultEntity.energy);
        if (tv_live_speech_result_mypraise != null) {
            tv_live_speech_result_mypraise.setText("" + speechResultEntity.praise);
        }
        ImageLoader.with(mContext).load(speechResultEntity.headUrl).error(R.drawable.app_livevideo_enteampk_boy_bg_img_nor).into(civ_live_speech_result_head);
        ArrayList<SpeechResultMember> speechResultMembers = speechResultEntity.speechResultMembers;
        if (speechResultMembers.isEmpty()) {
            rv_live_speech_result_other.setVisibility(View.GONE);
            v_live_speech_result_line.setVisibility(View.GONE);
        } else {
            v_live_speech_result_line.setVisibility(View.VISIBLE);
            rv_live_speech_result_other.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            RCommonAdapter<SpeechResultMember> adapter = new RCommonAdapter<>(mContext, speechResultMembers);
            adapter.addItemViewDelegate(new SpeechResultOtherItem());
            rv_live_speech_result_other.setAdapter(adapter);
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
        iv_live_speech_result_title.setImageResource(titleres[progress - 1]);
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
        iv_live_speech_result_close.setOnClickListener(new View.OnClickListener() {
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