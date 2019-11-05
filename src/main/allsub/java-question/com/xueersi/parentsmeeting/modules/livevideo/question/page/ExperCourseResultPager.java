package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.IArtsAnswerRsultDisplayer;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.item.ExperCourseResultAdapter;

import java.util.List;

/**
 * Created by linyuqiang on 2019/4/15.  大题互动结果页
 */
public class ExperCourseResultPager extends LiveBasePager implements IArtsAnswerRsultDisplayer {
    private PrimaryScienceAnswerResultEntity entity;
    List<PrimaryScienceAnswerResultEntity.Answer> answerList;
    /** 结果页上边金币标题 */
    private ImageView ivBigqueResultTitle;
    /** 结果页上边金币数量标题 */
    private TextView tvBigqueResultTitle;
    /** 结果页答题列表 */
    private RecyclerView rvBigqueResultList;
    /** 结果页关闭 */
    private ImageView ivBigqueResultClose;
    private LiveViewAction liveViewAction;
    private ImageView ivResultTitleLight;

    public ExperCourseResultPager(Context context, LiveViewAction liveViewAction, PrimaryScienceAnswerResultEntity entity) {
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
        if (liveViewAction != null) {
            mView = liveViewAction.inflateView(R.layout.page_livevideo_exper_course_result);
        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_exper_course_result, null);
        }
        ivBigqueResultTitle = mView.findViewById(R.id.iv_livevideo_bigque_result_title);
        tvBigqueResultTitle = mView.findViewById(R.id.tv_livevideo_bigque_result_title);
        rvBigqueResultList = mView.findViewById(R.id.rv_livevideo_bigque_result_list);
        ivBigqueResultClose = mView.findViewById(R.id.iv_livevideo_bigque_result_close);
        ivResultTitleLight = mView.findViewById(R.id.iv_livevideo_bigque_result_title_light);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rvBigqueResultList.setLayoutManager(layoutManager);
        rvBigqueResultList.setLayoutManager(new GridLayoutManager(mContext, 1, LinearLayoutManager.VERTICAL, false));
        rvBigqueResultList.addItemDecoration(new SpacesItemDecoration(SizeUtils.Dp2Px(mContext, 12)));
        return mView;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != 0)
                outRect.top = space;
        }
    }

    @Override
    public void initData() {
        super.initData();
        int isRight = entity.getType();
        if (isRight == PrimaryScienceAnswerResultEntity.ABSLUTELY_WRONG) {
            tvBigqueResultTitle.setText("很遗憾答错了");
            ivBigqueResultTitle.setImageResource(R.drawable.bg_livevideo_expercour_result_wrong_title);
            ivResultTitleLight.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title_light_grey);
        } else if (isRight == PrimaryScienceAnswerResultEntity.ABSLUTELY_RIGHT) {
            tvBigqueResultTitle.setText("恭喜你答对了    金币+" + entity.getGold());
            ivBigqueResultTitle.setImageResource(R.drawable.bg_livevideo_expercour_result_right_title);
            ivResultTitleLight.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title_light);
        } else if (isRight == PrimaryScienceAnswerResultEntity.PARTIALLY_RIGHT) {
            tvBigqueResultTitle.setText("部分正确    金币+" + entity.getGold());
            ivBigqueResultTitle.setImageResource(R.drawable.bg_livevideo_expercour_result_part_right_title);
            ivResultTitleLight.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title_light);
        }
        ExperCourseResultAdapter bigResultAdapter = new ExperCourseResultAdapter(answerList);
        rvBigqueResultList.setAdapter(bigResultAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivBigqueResultClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPagerClose != null) {
                    onPagerClose.onClose(ExperCourseResultPager.this);
                }
            }
        });
    }

    @Override
    public void showAnswerReuslt() {

    }

    @Override
    public void close() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (mView.getParent() != null) {
                    ((ViewGroup) mView.getParent()).removeView(mView);
                }
            }
        });
    }

    @Override
    public void remindSubmit() {

    }

    @Override
    public View getRootLayout() {
        return getRootView();
    }
}
