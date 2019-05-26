package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.LiveTeacherFeedbackItem;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.List;

public class LiveFeedBackPager extends LiveBasePager{
    RelativeLayout bottomContent;
    RecyclerView rvFeedbackContent;
    RCommonAdapter contentAdapter;

    List<String> mainFeedbackList;
    public LiveFeedBackPager(Context context) {
        super(context);
    }

    public LiveFeedBackPager(Context context, boolean isNewView) {
        super(context, isNewView);

    }

    public LiveFeedBackPager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }
    public LiveFeedBackPager(Context context,FeedBackEntity feedBackEntity, RelativeLayout
            bottomContent) {
        super(context, feedBackEntity, true);
        this.bottomContent = bottomContent;
        setLayout(mView);

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
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        rvFeedbackContent.setLayoutManager(manager);
        mainFeedbackList = new ArrayList<String>();
        mainFeedbackList.add("讲得太快了");
        mainFeedbackList.add("讲得太慢了");
        mainFeedbackList.add("内容讲错了");
        mainFeedbackList.add("闲话有点多");
        mainFeedbackList.add("只会读课件");
        mainFeedbackList.add("课堂闷无聊");
        return mView;
    }
    private void setContentData() {
        if (contentAdapter == null) {
            contentAdapter = new RCommonAdapter(mContext, mainFeedbackList);
            contentAdapter.addItemViewDelegate(1, new LiveTeacherFeedbackItem(mContext));
            rvFeedbackContent.setAdapter(contentAdapter);
        } else {
            contentAdapter.updateData(mainFeedbackList);
        }
    }
}
