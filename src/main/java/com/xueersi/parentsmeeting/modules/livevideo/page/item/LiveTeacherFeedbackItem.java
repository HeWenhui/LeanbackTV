package com.xueersi.parentsmeeting.modules.livevideo.page.item;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.EvaluateContent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveHeadView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

/**
 * 站立直播系统消息
 *
 * @author linyuqiang
 * @date 2018/5/10
 */
public class LiveTeacherFeedbackItem implements RItemViewInterface<EvaluateContent> {
    int redColor;
    int greyColor;
    TextView tvFeedBack;
    LiveFeedBackPager.FeedbackSelectInterface feedbackSelect;
    boolean isMain;
    public LiveTeacherFeedbackItem(Context mContext,LiveFeedBackPager.FeedbackSelectInterface feedbackSelect,boolean isMain,int redColor,int greyColor) {
        this.feedbackSelect = feedbackSelect;
        this.isMain = isMain;
        this.redColor = redColor;
        this.greyColor = greyColor;
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.item_live_teacher_feedback;
    }

    @Override
    public boolean isShowView(EvaluateContent item, int position) {
        return true;
    }

    @Override
    public void initView(ViewHolder holder, int position) {
        tvFeedBack = holder.getView(R.id.tv_item_live_teacher_feedback_text);
    }

    @Override
    public void convert(ViewHolder holder, final EvaluateContent evaluateContent, int position) {
        tvFeedBack.setText(evaluateContent.getText());
        if(evaluateContent.isSelectFlag()){
            tvFeedBack.setTextColor(redColor);
            tvFeedBack.setBackgroundResource(R.drawable.bg_corners_stroke_fb5e50_solid_transparent_radius_16);
        } else {
            tvFeedBack.setTextColor(greyColor);
            tvFeedBack.setBackgroundResource(R.drawable.bg_corners_stroke_ff5e5e7f_solid_transparent_radius_16);

        }
        tvFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedbackSelect.onSelect(evaluateContent.getText(),isMain);
            }
        });
    }
}
