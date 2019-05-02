package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FastScrollableRecyclerView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PraiseBtnAnimLayout;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.parentsmeeting.widget.praise.item.LivePraiseItem;
import com.xueersi.parentsmeeting.widget.praise.item.LivePraiseTitleItem;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 表扬榜1
 */
public class DarkPraisePager extends LiveBasePager {
    /**
     * 内容view
     */
    FastScrollableRecyclerView recyclerView;
    private RCommonAdapter contentAdapter;
    /**
     * 榜单数据
     */
    List<PraiseContentEntity> listContent;
    /**
     * 点赞
     */
    PraiseBtnAnimLayout imgBtnPractice;
    /**
     * 点赞动画
     */
    LottieAnimationView practiceView;
    TextView tvPracticeCount;
    int mCurrentNum;

    public DarkPraisePager(Context context) {
        super(context);
        listContent = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PraiseContentEntity entity = new PraiseContentEntity();
            if (i == 0) {
                entity.setItemSpan(4);
                entity.setName("课清全对");
                entity.setViewType(PraiseConfig.VIEW_TYPE_TITLE);
            } else {
                entity.setItemSpan(1);
                entity.setName("i" + i);
            }

            listContent.add(entity);
        }
        setContentData();
        setLayout(mView);
    }

    private void setLayout(View view) {
        int rightMargin = LiveVideoPoint.getInstance().getRightMargin();
        //设置主视图参数
        RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mainParam.rightMargin = rightMargin;
        view.setLayoutParams(mainParam);

    }

    public DarkPraisePager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    public DarkPraisePager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }

    public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {

            //return gridManager.getSpanCount();
            return listContent.get(position).getItemSpan();
        }
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_praise_list_china, null);
        recyclerView = mView.findViewById(R.id.rv_livevideo_praise_list_dark_content);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setSpanSizeLookup(new GridSpanSizeLookup());
        recyclerView.setLayoutManager(manager);
        imgBtnPractice = mView.findViewById(R.id.fl_page_livevideo_praise_list_dark_practice);
        practiceView = mView.findViewById(R.id.lav_livevideo_praise_list_dark_practice);
        tvPracticeCount = mView.findViewById(R.id.tv_page_livevideo_praise_list_dark_practice_count);
        return mView;
    }

    private void setContentData() {
        contentAdapter = new RCommonAdapter(mContext, listContent);
        contentAdapter.addItemViewDelegate(1, new LivePraiseItem());
        contentAdapter.addItemViewDelegate(4, new LivePraiseTitleItem());
        recyclerView.setAdapter(contentAdapter);
        setListener();
//        practiceView.playAnimation();

    }

    private void setListener() {
        // 点赞
        imgBtnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDatePraiseNum(1, true);
                practiceView.playAnimation();
            }
        });
    }

    /**
     * 更新点赞数
     *
     * @param praiseNum
     * @param withAnim  是否显示动画
     */
    private void upDatePraiseNum(long praiseNum, boolean withAnim) {
        if (imgBtnPractice != null && imgBtnPractice.getVisibility() == View.VISIBLE) {
            Loger.e("ArtsPraisePager", "======>upDatePraiseNum called:" + praiseNum + ":" + withAnim);


            tvPracticeCount.setText(mCurrentNum + "");
            mCurrentNum++;

        }
    }

}
