package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by ZhangYuansun on 2019/3/6
 *
 * 小学理科 互动题结果页
 */
public class PrimaryScienceAnserResultPager extends LiveBasePager {
    int type;
    public PrimaryScienceAnserResultPager(Context context,int type) {
        super(context);
        this.type= type;
        initData();
    }

    LottieAnimationView lavRight;
    LottieAnimationView lavGameRight;
    LottieAnimationView lavActiveRight;
    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_primaryscience_anwserresult, null);
        lavRight = mView.findViewById(R.id.lav_livevideo_primaryscience_anwserrsult_right);
        lavGameRight = mView.findViewById(R.id.lav_livevideo_primaryscience_anwserrsult_game_right);
        lavActiveRight = mView.findViewById(R.id.lav_livevideo_primaryscience_anwserrsult_active_right);

        return mView;
    }

    @Override
    public void initData() {
    lavRight.playAnimation();
        if(type==2){
            lavGameRight.setVisibility(View.VISIBLE);
            lavGameRight.playAnimation();
        }
        if(type==3){
            lavActiveRight.setVisibility(View.VISIBLE);
            lavActiveRight.playAnimation();
        }
    }

    @Override
    public void initListener() {

    }
}
