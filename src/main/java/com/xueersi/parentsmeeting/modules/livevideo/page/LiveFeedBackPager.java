package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.EvaluateContent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.LiveTeacherFeedbackItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.RecyclerViewSpacesItemDecoration;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveFeedBackPager extends LiveBasePager {
    RelativeLayout bottomContent;
    /** 主讲布局 */
    RecyclerView rvFeedbackContent;
    RCommonAdapter contentAdapter;
    ImageView ivMainHeader;
    ImageView ivStatus1;
    ImageView ivStatus2;
    ImageView ivStatus3;
    TextView tvMainName;
    EditText etMainFeedback;


    /** 辅导布局 */
    RecyclerView rvTutorContent;
    RCommonAdapter contentTutorAdapter;
    ImageView ivTutorHeader;
    ImageView ivTutorStatus1;
    ImageView ivTutorStatus2;
    ImageView ivTutorStatus3;
    TextView tvTutorName;
    EditText etTutorFeedback;

    List<EvaluateContent> mainFeedbackList;

    List<EvaluateContent> tutorFeedbackList;

    /** 反馈内容 */
    FeedBackEntity mFeedbackEntity;
    /** 提交提示 */
    TextView tvSubmitHint;
    ImageView ivSubmit;
    ImageView ivClose;

    /** 不满意 */
    int FEED_TYPE_1 = 1;
    /** 有待提高 */
    int FEED_TYPE_2 = 2;
    /** 满意 */
    int FEED_TYPE_3 = 3;

    int mainType = 1;
    int tutorType = 1;

    public LiveFeedBackPager(Context context) {
        super(context);
    }

    public LiveFeedBackPager(Context context, boolean isNewView) {
        super(context, isNewView);

    }

    public LiveFeedBackPager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }

    public LiveFeedBackPager(Context context, FeedBackEntity feedBackEntity, RelativeLayout
            bottomContent) {
        super(context, feedBackEntity, true);
        this.bottomContent = bottomContent;
        setLayout(mView);
        mFeedbackEntity = feedBackEntity;
        setContentData();
    }

    private void setLayout(View view) {
        // 设置主视图参数
        RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(mainParam);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.layout_live_video_feed_back, bottomContent);
        rvFeedbackContent = mView.findViewById(R.id.rv_pager_live_teacher_feedback_content);
        rvTutorContent = mView.findViewById(R.id.rv_pager_live_teacher_feedback_tutor_content);
        ivMainHeader = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_head_image);
        tvMainName = mView.findViewById(R.id.tv_pager_live_teacher_feedback_main_name);
        ivStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_status_1);
        ivStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_status_2);
        ivStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_status_3);
        etMainFeedback = mView.findViewById(R.id.et_pager_live_teacher_feedback_main_input_text);


        ivTutorHeader = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_head_image);
        tvTutorName = mView.findViewById(R.id.tv_pager_live_teacher_feedback_tutor_name);
        ivTutorStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_status_1);
        ivTutorStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_status_2);
        ivTutorStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_status_3);
        etTutorFeedback = mView.findViewById(R.id.et_pager_live_teacher_feedback_tutor_input_text);
        tvSubmitHint = mView.findViewById(R.id.tv_pager_live_teacher_feedback_bottom_submit_hint);
        ivSubmit = mView.findViewById(R.id.iv_pager_live_teacher_feedback_submit);
        ivClose = mView.findViewById(R.id.iv_pager_live_teacher_feedback_close);
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        rvFeedbackContent.setLayoutManager(manager);
        rvTutorContent.setLayoutManager(manager);

        setRecyclerViewDecoration();
//        mainFeedbackList = new ArrayList<String>();
//        mainFeedbackList.add("讲得太快了");
//        mainFeedbackList.add("讲得太慢了");
//        mainFeedbackList.add("内容讲错了");
//        mainFeedbackList.add("闲话有点多");
//        mainFeedbackList.add("只会读课件");
//        mainFeedbackList.add("课堂闷无聊");

        return mView;
    }

    public void setRecyclerViewDecoration() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 0);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 0);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, SizeUtils.Dp2Px(mContext, 20));//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 0);//右间距
        rvFeedbackContent.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        rvTutorContent.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }


    @Override
    public void initData() {
        super.initData();
        setFeedStyleList(FEED_TYPE_1);

        tvMainName.setText(mFeedbackEntity.getMainName());
        ImageLoader.with(mContext).load(mFeedbackEntity.getMainHeadImage()).
                error(R.drawable.icon_livevideo_praiselist_team_head_default).into(ivMainHeader);
        if (mFeedbackEntity.isHaveInput()) {
            etMainFeedback.setVisibility(View.VISIBLE);
        } else {
            etMainFeedback.setVisibility(View.GONE);

        }
        if (mFeedbackEntity.isHaveTutor()) {
            tvTutorName.setText(mFeedbackEntity.getTutorName());
            ImageLoader.with(mContext).load(mFeedbackEntity.getTutorHeadImage()).
                    error(R.drawable.icon_livevideo_praiselist_team_head_default).into(ivTutorHeader);
            if (mFeedbackEntity.isHaveInput()) {
                etTutorFeedback.setVisibility(View.VISIBLE);
            } else {
                etTutorFeedback.setVisibility(View.GONE);
            }
        }
        // 设置反馈内容
        setContentData();
    }


    /**
     * 设置反馈内容
     */
    private void setContentData() {
        if (mainFeedbackList != null && mainFeedbackList.size() > 0) {
            rvFeedbackContent.setVisibility(View.VISIBLE);
            if (contentAdapter == null) {
                contentAdapter = new RCommonAdapter(mContext, mainFeedbackList);
                contentAdapter.addItemViewDelegate(1, new LiveTeacherFeedbackItem(mContext));
                rvFeedbackContent.setAdapter(contentAdapter);
            } else {
                contentAdapter.updateData(mainFeedbackList);
            }
        } else {
            rvFeedbackContent.setVisibility(View.GONE);

        }
        if (tutorFeedbackList != null && tutorFeedbackList.size() > 0) {
            if (contentTutorAdapter == null) {
                contentTutorAdapter = new RCommonAdapter(mContext, tutorFeedbackList);
                contentTutorAdapter.addItemViewDelegate(1, new LiveTeacherFeedbackItem(mContext));
                rvTutorContent.setAdapter(contentTutorAdapter);
            } else {
                contentTutorAdapter.updateData(tutorFeedbackList);
            }
            rvTutorContent.setVisibility(View.VISIBLE);

        } else {
            rvTutorContent.setVisibility(View.GONE);
        }
    }

    private void setClickListener() {
        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeedbackEntity.isHaveInput()) {

                }
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyleData(FEED_TYPE_1, true);
            }
        });
        ivStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyleData(FEED_TYPE_2, true);

            }
        });

        ivStatus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyleData(FEED_TYPE_3, true);

            }
        });
        ivTutorStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyleData(FEED_TYPE_1, false);

            }
        });
        ivTutorStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyleData(FEED_TYPE_2, false);

            }
        });
        ivTutorStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyleData(FEED_TYPE_3, false);

            }
        });
    }

    /**
     * 选中态设置
     *
     * @param style
     * @param isMain
     */
    private void setStyleData(int style, boolean isMain) {
        if (isMain) {
            mainType = style;
            if (FEED_TYPE_1 == style) {
                ivStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_selected);
                ivStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
            } else if (FEED_TYPE_1 == style) {
                ivStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_selected);
                ivStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
            } else {
                ivStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_slected);
            }
        } else {
            tutorType = style;
            if (FEED_TYPE_1 == style) {
                ivTutorStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_selected);
                ivTutorStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivTutorStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
            } else if (FEED_TYPE_1 == style) {
                ivTutorStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivTutorStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_selected);
                ivTutorStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
            } else {
                ivTutorStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivTutorStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivTutorStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_slected);
            }
        }
        setFeedStyleList(style);
    }

    /**
     * 选中内容设置
     *
     * @param style
     */
    private void setFeedStyleList(int style) {
        if (mFeedbackEntity.getMainContentList() != null && mFeedbackEntity.getMainContentList().size() >= style) {
            mainFeedbackList = mFeedbackEntity.getMainContentList().get(style - 1);
        }
        if (mFeedbackEntity.getTutorContentList() != null && mFeedbackEntity.getTutorContentList().size() >= style) {
            mainFeedbackList = mFeedbackEntity.getTutorContentList().get(style - 1);
        }
        setContentData();
    }

}
