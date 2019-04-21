package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.item.BigResultAdapter;

import java.util.ArrayList;

public class BigResultPager extends LiveBasePager {
    private ArrayList<BigResultEntity> bigResultEntities = new ArrayList<>();
    private RecyclerView rv_livevideo_bigque_result_list;
    ImageView iv_livevideo_bigque_result_close;
    private ViewGroup group;

    public BigResultPager(Context context, ViewGroup group) {
        super(context, false);
        this.group = group;
        mView = initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_bigques_result, group, false);
        rv_livevideo_bigque_result_list = mView.findViewById(R.id.rv_livevideo_bigque_result_list);
        iv_livevideo_bigque_result_close = mView.findViewById(R.id.iv_livevideo_bigque_result_close);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_livevideo_bigque_result_list.setLayoutManager(layoutManager);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
        for (int i = 0; i < 10; i++) {
            BigResultEntity bigResultEntity = new BigResultEntity();
            bigResultEntity.standAnswer = "A";
            bigResultEntity.youAnswer = "B";
            if (i % 2 == 0) {
                bigResultEntity.rightType = LiveQueConfig.DOTTYPE_RESULT_RIGHT;
            } else {
                bigResultEntity.rightType = LiveQueConfig.DOTTYPE_RESULT_WRONG;
            }
            bigResultEntities.add(bigResultEntity);
        }
        BigResultAdapter bigResultAdapter = new BigResultAdapter(bigResultEntities);
        rv_livevideo_bigque_result_list.setAdapter(bigResultAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        iv_livevideo_bigque_result_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPagerClose != null) {
                    onPagerClose.onClose(BigResultPager.this);
                }
            }
        });
    }
}
