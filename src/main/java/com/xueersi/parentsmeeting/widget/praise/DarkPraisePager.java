package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
    /** 教师信息 */
    LinearLayout llTeacherContent;
    /** 教师信息 */
    ImageButton imgBtnClose;
    /** 标题 */
    ImageView ivTitle;
    /** 自定义标题 */
    TextView tvTitle;
    /** 副标题 */
    TextView tvSubTitle;
    /** 老师提示 */
    TextView tvTeacherTip;
    /** 老师批语 */
    TextView tvTeacherTalk;
    /** 老师头像 */
    ImageView ivTeacherHeadImage;
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
        RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

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
        mView = View.inflate(mContext, R.layout.page_livevideo_praise_list_wood, null);
        recyclerView = mView.findViewById(R.id.rv_livevideo_praise_list_content);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setSpanSizeLookup(new GridSpanSizeLookup());
        recyclerView.setLayoutManager(manager);
        imgBtnPractice = mView.findViewById(R.id.fl_page_livevideo_praise_list_practice);
        practiceView = mView.findViewById(R.id.lav_livevideo_praise_list_practice);
        tvPracticeCount = mView.findViewById(R.id.tv_page_livevideo_praise_list_practice_count);
        llTeacherContent = mView.findViewById(R.id.ll_page_livevideo_praise_list_teacher_content);
        imgBtnClose = mView.findViewById(R.id.btn_page_livevideo_praise_list_close);
        ivTitle  = mView.findViewById(R.id.iv_page_livevideo_praise_list_title);
        tvTitle  = mView.findViewById(R.id.tv_page_livevideo_praise_list_title);
        tvSubTitle = mView.findViewById(R.id.tv_page_livevideo_praise_list_sub_title);

        tvTeacherTip  = mView.findViewById(R.id.tv_page_livevideo_praise_list_teacher_tip);
        tvTeacherTalk  = mView.findViewById(R.id.tv_page_livevideo_praise_list_teacher_talk);
        ivTeacherHeadImage = mView.findViewById(R.id.iv_page_livevideo_praise_list_teacher_head_image);
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
        // 关闭
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llTeacherContent.getVisibility() == View.GONE) {
                    llTeacherContent.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.VISIBLE);
                    ivTitle.setVisibility(View.GONE);
                } else {
                    llTeacherContent.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.GONE);
                    ivTitle.setVisibility(View.VISIBLE);
                }
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
