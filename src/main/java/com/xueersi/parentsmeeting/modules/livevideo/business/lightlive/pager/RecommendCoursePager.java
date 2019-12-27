package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;

import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager
 * @ClassName: RecommendCoursePager
 * @Description: 轻直播推荐课程
 * @Author: WangDe
 * @CreateDate: 2019/11/27 21:54
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/27 21:54
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RecommendCoursePager extends BasePager {

    List<CourseEntity> courseEntities;
    TextView tvCount;
    TextView tvMore;
    ViewFlipper vfCourses;
    MoreCouponClickListener listener;
    private final int ANIMATION_TIME = 20 * 1000;
    private boolean isPlayback;

    public RecommendCoursePager(Context context,boolean isPlayback){
        super(context);
        this.isPlayback = isPlayback;
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_lightlive_recommend_course,null);
        tvCount = mView.findViewById(R.id.tv_lightlive_recommend_count);
        tvMore = mView.findViewById(R.id.tv_lightlive_recommend_more);
        vfCourses = mView.findViewById(R.id.vf_lightlive_recommend_courses);
        mView.setVisibility(View.GONE);
        initListener();
        return mView;
    }

    @Override
    public void initData() {
        tvCount.setText(courseEntities.size()+"门推荐课程");
        for (CourseEntity entity : courseEntities) {
            vfCourses.addView(new RecommendCourseItem(mContext,entity).getRootView());
        }
        vfCourses.setFlipInterval(ANIMATION_TIME);
        vfCourses.setInAnimation(mContext,R.anim.anim_slide_in_top_blur);
        vfCourses.setOutAnimation(mContext,R.anim.anim_slide_out_bottom_blur);
        vfCourses.startFlipping();
    }

    public void setData(List<CourseEntity> courseEntities){
        this.courseEntities = courseEntities;
        if (courseEntities != null && !courseEntities.isEmpty()){
            initData();
            mView.setVisibility(View.VISIBLE);
            if(!isPlayback){
                XrsBury.showBury(mContext.getResources().getString(R.string.show_03_63_010));
            }
        }else {
            mView.setVisibility(View.GONE);
        }

    }

    @Override
    public void initListener() {
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vfCourses.stopFlipping();
    }

    public void setMoreClickListener(MoreCouponClickListener listener) {
        this.listener = listener;
    }

    public interface MoreCouponClickListener {
        void onClick();
    }
}
