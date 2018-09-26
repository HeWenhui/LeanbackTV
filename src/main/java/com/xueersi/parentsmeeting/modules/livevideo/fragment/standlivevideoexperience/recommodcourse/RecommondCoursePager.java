package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class RecommondCoursePager extends BasePager {
    private final String TAG = getClass().getSimpleName();
    //动画持续时间
    private final int Duration = 500;
    /** 关闭按钮 */
    private ImageView ivClose;
    private ImageView ivBuy;
    //课程名称
    private ImageView tvCourseName;
    //课程价格
    private TextView tvCourseMoney;
    //老师头像
    private ImageView ivTeacherImg;
    //老师缩略图的头像
    private ImageView ivTeacherThumbnail;
    //展开后的推荐课程布局
    private ConstraintLayout wholeRecommondCourseLayout;
    //缩略图的推荐课程布局.
    private ConstraintLayout thumbnailRecommondCourseLayout;

    private ClickListener listener;

    public RecommondCoursePager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_stand_experience_recommond_course, null);
        ivClose = view.findViewById(R.id.iv_livevideo_stand_experience_recommond_course_close_btn);
        ivBuy = view.findViewById(R.id.iv_livevideo_stand_experience_recommond_course_buy_btn);
        tvCourseName = view.findViewById(R.id.tv_livevideo_stand_experience_recommond_course_course_name);
        tvCourseMoney = view.findViewById(R.id.tv_livevideo_stand_experience_recommond_course_course_money);
        ivTeacherImg = view.findViewById(R.id.iv_livevideo_stand_experience_recommond_course_teacher_icon);
        ivTeacherThumbnail = view.findViewById(R.id.iv_livevideo_stand_experience_recommod_course_teacher_thumbnail);
        wholeRecommondCourseLayout = view.findViewById(R.id.ctl_recommod_course);
        thumbnailRecommondCourseLayout = view.findViewById(R.id.ctl_recommod_course_thumbnail);
        return view;
    }

    @Override
    public void initData() {
        initAnimator();
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        hideWholeAnimation();
        showThumbnailAnimation();
        showWholeAnimation();
        hideThumbnailAnimation();
        showWholeSet = new AnimatorSet();
        hideWholeSet = new AnimatorSet();
        showWholeSet.playTogether(wholeShowAnimator, thumbnailHideAnimator);
        showWholeSet.setDuration(Duration);
        hideWholeSet.playTogether(wholeHideAnimator, thumbnailShowAnimator);
        hideWholeSet.setDuration(Duration);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholeRecommondCourseLayout.setEnabled(false);
                thumbnailRecommondCourseLayout.setEnabled(true);
                thumbnailRecommondCourseLayout.setVisibility(View.VISIBLE);
                hideWholeSet.start();
            }
        });
        ivBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickBuyCourse();
                }
            }
        });
        thumbnailRecommondCourseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholeRecommondCourseLayout.setEnabled(true);
                thumbnailRecommondCourseLayout.setEnabled(false);
                showWholeSet.start();
            }
        });
    }

    private ObjectAnimator wholeShowAnimator, wholeHideAnimator, thumbnailShowAnimator, thumbnailHideAnimator;
    private AnimatorSet showWholeSet, hideWholeSet;

    private void showWholeAnimation() {
        if (wholeShowAnimator == null) {
            wholeShowAnimator = ObjectAnimator.ofFloat(wholeRecommondCourseLayout, "alpha", 0f, 1f);
//            wholeShowAnimator.setDuration(Duration);
        }
//        wholeShowAnimator.start();
    }

    private void hideWholeAnimation() {
        if (wholeHideAnimator == null) {
            wholeHideAnimator = ObjectAnimator.ofFloat(wholeRecommondCourseLayout, "alpha", 1f, 0f);
//            wholeHideAnimator.setDuration(Duration);
        }
//        wholeHideAnimator.start();
    }

    private void showThumbnailAnimation() {
        if (thumbnailShowAnimator == null) {
            thumbnailShowAnimator = ObjectAnimator.ofFloat(thumbnailRecommondCourseLayout, "alpha", 0f, 1f);
//            thumbnailShowAnimator.setDuration(Duration);
        }
//        thumbnailShowAnimator.start();
    }

    private void hideThumbnailAnimation() {
        if (thumbnailHideAnimator == null) {
            thumbnailHideAnimator = ObjectAnimator.ofFloat(thumbnailRecommondCourseLayout, "alpha", 1f, 0f);
//            thumbnailHideAnimator.setDuration(Duration);
        }
//        thumbnailHideAnimator.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAnimator();
    }

    /**
     * 停止动画，销毁资源
     */
    private void stopAnimator() {
        if (hideWholeSet.isRunning()) {
            hideWholeSet.cancel();
        }
        if (showWholeSet.isRunning()) {
            showWholeSet.cancel();
        }
        hideWholeSet = null;
        showWholeSet = null;
        thumbnailHideAnimator = null;
        thumbnailShowAnimator = null;
        wholeShowAnimator = null;
        wholeHideAnimator = null;
    }

    public interface ClickListener {
//        void clickClose();

        void clickBuyCourse();
    }

    public void setClickListener(ClickListener clickListener) {
        this.listener = clickListener;
    }
}
