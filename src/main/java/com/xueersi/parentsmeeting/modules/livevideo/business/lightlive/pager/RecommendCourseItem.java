package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseTeacherEntity;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager
 * @ClassName: RecommendCourseItem
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/28 11:57
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/28 11:57
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RecommendCourseItem extends BasePager {

    /** 课程名称*/
    String title;
    /** 教师名称*/
    String teacherName;
    /** 老师头像*/
    String headImg;

    ImageView ivHead;
    TextView tvTitle;
    TextView tvName;
    /** 去课程详情页*/
    TextView tvApply;
    CourseEntity mCourseEntity;

    public RecommendCourseItem(Context context, CourseEntity courseEntity){
        super(context);
        mCourseEntity = courseEntity;
        initData();
        initListener();
    }
    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.item_lightlive_recommend_course,null);
        ivHead = mView.findViewById(R.id.iv_lightlive_recommend_head);
        tvTitle = mView.findViewById(R.id.tv_lightlive_recommend_title);
        tvName = mView.findViewById(R.id.tv_lightlive_recommend_name);
        tvApply = mView.findViewById(R.id.tv_lightlive_recommend_buy);
        return mView;
    }

    @Override
    public void initData() {
        tvTitle.setText(mCourseEntity.getCourseName());
        String type = "";
        if (mCourseEntity.getLstMainTeacher() != null && !mCourseEntity.getLstMainTeacher().isEmpty()){
            CourseTeacherEntity entity = mCourseEntity.getLstMainTeacher().get(0);
            type =   "授课: " + entity.getTeacherName();
            if(mCourseEntity.getLstMainTeacher().size() > 1){
                type += "等";
            }
        } else if (mCourseEntity.getLstForeignTeacher() != null && !mCourseEntity.getLstForeignTeacher().isEmpty()){
            CourseTeacherEntity entity = mCourseEntity.getLstForeignTeacher().get(0);
            type = "授课: " + entity.getTeacherName();
            if(mCourseEntity.getLstForeignTeacher().size() > 1){
                type += "等";
            }
        }
        tvName.setText(type);
//        tvName.setText("授课: test");
        headImg = mCourseEntity.getLstMainTeacher().get(0).getTeacherImg();
        int defaultHeadImg = R.drawable.bg_main_default_head_image;
        if (TextUtils.isEmpty(headImg)) {
            // 如果图片URL为空则直接加载默认图片，因为图片加载框架对空字符串的路径加载会加载到其它图片上，故这样解决
            ivHead.setImageResource(defaultHeadImg);

        } else {
            ImageLoader.with(ContextManager.getContext()).asCircle().load(headImg).error(defaultHeadImg)
                    .placeHolder(defaultHeadImg).into(ivHead);
        }
    }

    @Override
    public void initListener() {
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //跳转到商城的订单详情页面
            Bundle bundle = new Bundle();
            bundle.putString("vCourseId", mCourseEntity.getCourseId());
//            bundle.putString("classId", mCourseEntity.getClassID());
            //采用ARouter来跳转
            XueErSiRouter.startModule(mContext, "/xesmallCourseDetail/xrsmodule", bundle);
                XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_002),mCourseEntity.getCourseId());
            }
        });
    }
}
