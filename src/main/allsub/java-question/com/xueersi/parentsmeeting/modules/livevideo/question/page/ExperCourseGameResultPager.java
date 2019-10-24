package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.item.ExperCourseResultAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

import java.util.List;

/**
 * Created by linyuqiang on 2019/4/15.  大题互动结果页
 */
public class ExperCourseGameResultPager extends LiveBasePager {
    private PrimaryScienceAnswerResultEntity entity;
    List<PrimaryScienceAnswerResultEntity.Answer> answerList;
    private LiveViewAction liveViewAction;
    private TextView tv_livevideo_expe_course_game_result;

    public ExperCourseGameResultPager(Context context, LiveViewAction liveViewAction, PrimaryScienceAnswerResultEntity entity) {
        super(context, false);
        this.liveViewAction = liveViewAction;
        mView = initView();
        this.entity = entity;
        answerList = entity.getAnswerList();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = liveViewAction.inflateView(R.layout.page_livevideo_exper_course_game_result);
        tv_livevideo_expe_course_game_result = mView.findViewById(R.id.tv_livevideo_expe_course_game_result);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
        int isRight = entity.getType();
        if (isRight == PrimaryScienceAnswerResultEntity.ABSLUTELY_WRONG) {
            tv_livevideo_expe_course_game_result.setText("下次记得提交哟");
            tv_livevideo_expe_course_game_result.setTextColor(0xff666666);
        } else if (isRight == PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT) {
            tv_livevideo_expe_course_game_result.setTextColor(0xff666666);
            SpannableStringBuilder total = new SpannableStringBuilder("奖励");
            SpannableString daySpan = new SpannableString(entity.getGold() + "");
            daySpan.setSpan(new ForegroundColorSpan(0xffFF5545), 0, (entity.getGold() + "").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            total.append(daySpan);
            total.append("金币");
            tv_livevideo_expe_course_game_result.setText(total);
        }
        LiveMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onPagerClose != null) {
                    onPagerClose.onClose(ExperCourseGameResultPager.this);
                }
            }
        }, 5000);
    }

    @Override
    public void initListener() {
        super.initListener();
    }
}
