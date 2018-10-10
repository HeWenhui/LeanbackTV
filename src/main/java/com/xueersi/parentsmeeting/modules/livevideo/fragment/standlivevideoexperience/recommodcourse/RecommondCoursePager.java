package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RecommondCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoBannerBuyCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.MarqueeView;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.widget.AutoVerticalScrollTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RecommondCoursePager extends BasePager implements QuestionShowAction {
    private final String TAG = getClass().getSimpleName();
    //动画持续时间
    private final int Duration = 500;
    /** 关闭按钮 */
    private ImageView ivClose;
    private ImageView ivBuy;
    //课程名称
    private TextView tvCourseName;
    //课程价格
    private TextView tvCourseMoney;
    //老师头像
//    private ImageView ivTeacherImg;
    //老师缩略图的头像
//    private ImageView ivTeacherThumbnail;
    //展开后的推荐课程布局
    private ConstraintLayout wholeRecommondCourseLayout;
    //缩略图的推荐课程布局.
    private ConstraintLayout thumbnailRecommondCourseLayout;

    private ClickListener listener;
    //轮播消息View
    private MarqueeView mvBannerMessage;
    //展开的推荐课程是否处于显示状态
    private boolean isWholeShow = true;

    private boolean isBuyCourseSuccess = false;

    public RecommondCoursePager(Context context, boolean isBuyCourseSuccess) {
        super(context);
        this.isBuyCourseSuccess = isBuyCourseSuccess;
        if (isBuyCourseSuccess) {
            buyCourseSuccess();//如果购课已经成功，就隐藏这两个弹窗
        }
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
//        ivTeacherImg = view.findViewById(R.id.iv_livevideo_stand_experience_recommond_course_teacher_icon);
//        ivTeacherThumbnail = view.findViewById(R.id.iv_livevideo_stand_experience_recommod_course_teacher_thumbnail);
        wholeRecommondCourseLayout = view.findViewById(R.id.ctl_recommod_course);
        thumbnailRecommondCourseLayout = view.findViewById(R.id.ctl_recommod_course_thumbnail);
        mvBannerMessage = view.findViewById(R.id.mv_livevideo_stand_experience_banner_message);
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
                if (isWholeShow) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                            .stand_experience_1704002));
                    wholeRecommondCourseLayout.setClickable(false);
                    thumbnailRecommondCourseLayout.setClickable(true);
                    thumbnailRecommondCourseLayout.setVisibility(View.VISIBLE);
                    hideWholeSet.start();
                    isWholeShow = false;
                }
            }
        });
        ivBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWholeShow) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                            .stand_experience_1704001));
                    if (listener != null) {
                        listener.clickBuyCourse();
                    }
                }
            }
        });
//        ivBuy.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (isWholeShow) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            ivBuy.setAlpha(0.8f);
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            if (listener != null) {
//                                listener.clickBuyCourse();
//                            }
//                            ivBuy.setAlpha(1f);
//                            break;
//                    }
//                }
//                return true;
//            }
//        });
//        ivBuy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) {
//                    listener.clickBuyCourse();
//                }
//            }
//        });
        thumbnailRecommondCourseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWholeShow) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                            .stand_experience_1704003));
                    wholeRecommondCourseLayout.setClickable(true);
                    thumbnailRecommondCourseLayout.setClickable(false);
                    showWholeSet.start();
                    isWholeShow = true;
                }
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


    public void updateView(RecommondCourseEntity mRecommondCourseEntity) {
        logger.i(mRecommondCourseEntity.getCourseName() + " " + mRecommondCourseEntity.getCoursePrice());
        if (mRecommondCourseEntity != null) {
            if (!TextUtils.isEmpty(mRecommondCourseEntity.getCourseName())) {
                tvCourseName.setText(mRecommondCourseEntity.getCourseName());
            }
            if (!TextUtils.isEmpty(mRecommondCourseEntity.getCoursePrice())) {
                tvCourseMoney.setText("¥" + mRecommondCourseEntity.getCoursePrice());
            }
        }
    }


    //延迟2分钟发射
    private final int delayTime = 60 * 2 * 1000;

    /**
     * 设置滚动
     */
//    private Runnable messageRunnable;

//    public Runnable getBannerMessageRunnable(final Queue<VideoBannerBuyCourseEntity.BannerMessage> queMessage) {
//        this.queMessage = queMessage;
//        if (messageRunnable == null) {
//            messageRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    if (mView != null) {
//                        VideoBannerBuyCourseEntity.BannerMessage bannerMessage = queMessage.poll();
//                        SpannableString spannableString = new SpannableString("喇叭 欢迎" + bannerMessage.getUserName() +
//                                "加入" + bannerMessage.getCourseName());
//                        ImageSpan imageSpan = new ImageSpan(mContext.getResources().getDrawable(R.drawable
//                                .bg_livevideo_stand_experience_advertise_horn));
//                        spannableString.setSpan(imageSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//
//
////                        mvBannerMessage.next();
//                        mView.postDelayed(this, delayTime);
//                    }
//                }
//            };
//        }
//        return messageRunnable;
//    }
    public void startBanner(final Queue<VideoBannerBuyCourseEntity.BannerMessage> queMessage) {
        if (mvBannerMessage.getVisibility() == View.GONE) {
            mvBannerMessage.setVisibility(View.VISIBLE);
        }
        if (mView != null) {
            mvBannerMessage.startWithList(getBannerList(queMessage));
        }
    }

    private List<SpannableString> getBannerList(Queue<VideoBannerBuyCourseEntity.BannerMessage> queMessage) {
        //设置左边Left
        Drawable drawable = mContext.getResources().getDrawable(R.drawable
                .bg_livevideo_stand_experience_advertise_horn);
        drawable.setBounds(0, 0, 60, 48);
        mvBannerMessage.setLeftDrawable(drawable);

        List<SpannableString> list = new ArrayList<>();
        while (!queMessage.isEmpty()) {
            VideoBannerBuyCourseEntity.BannerMessage bannerMessage = queMessage.poll();
            SpannableString spannableString = new SpannableString(" 欢迎" + bannerMessage.getUserName() +
                    "加入" + bannerMessage.getCourseName());
            list.add(spannableString);
        }
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mView != null && messageRunnable != null) {
//            mView.removeCallbacks(messageRunnable);
//        }
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

    //购买课程成功
    public void buyCourseSuccess() {
        isBuyCourseSuccess = true;
        wholeRecommondCourseLayout.setVisibility(View.GONE);
        thumbnailRecommondCourseLayout.setVisibility(View.GONE);
    }

    /**
     * 在其他问题显示时隐藏该View
     *
     * @param isShow
     */
    @Override
    public void onQuestionShow(boolean isShow) {
        if (mView != null) {
            if (!isBuyCourseSuccess) {
                if (isShow) {
                    mView.setVisibility(View.GONE);
                } else {
                    mView.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    public interface ClickListener {
        void clickBuyCourse();
    }

    public void setClickListener(ClickListener clickListener) {
        this.listener = clickListener;
    }
}
