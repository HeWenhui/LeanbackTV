package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StartProgress;

public class SpeechResultJuniorPager extends LiveBasePager {
    private ViewGroup group;
    private SpeechResultEntity speechResultEntity;
    private StartProgress startProgress;

    public SpeechResultJuniorPager(Context context, ViewGroup group, SpeechResultEntity speechResultEntity) {
        super(context, false);
        this.group = group;
        this.speechResultEntity = speechResultEntity;
        mView = initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_speech_result_junior, group, false);
        startProgress = view.findViewById(R.id.sp_live_star_result);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        String[] split = speechResultEntity.content.split(" ");
        if (split.length == 1) {
            startProgress.setIsWord();
        }
        if (speechResultEntity.isAnswered) {
            startProgress.setAnswered();
        }
        startProgress.setSorce(speechResultEntity.score);
        startProgress.setStarCount(speechResultEntity.gold);
        startProgress.setAccuracy(speechResultEntity.accuracy);
        startProgress.setFluent(speechResultEntity.fluency);
        startProgress.setProgress(speechResultEntity.progress);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onPagerClose != null) {
                    onPagerClose.onClose(SpeechResultJuniorPager.this);
                }
            }
        }, 3000);
    }

    @Override
    public void initListener() {
        super.initListener();
        mView.findViewById(R.id.v_live_star_result_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPagerClose != null) {
                    onPagerClose.onClose(SpeechResultJuniorPager.this);
                }
            }
        });
    }
}
