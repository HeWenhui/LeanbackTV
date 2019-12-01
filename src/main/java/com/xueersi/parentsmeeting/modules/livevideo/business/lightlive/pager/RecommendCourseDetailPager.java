package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseTeacherEntity;
import com.xueersi.parentsmeeting.modules.livevideo.utils.DrawUtil;
import com.xueersi.parentsmeeting.modules.livevideo.utils.SubjectUtil;
import com.xueersi.parentsmeeting.widget.VericalImageSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager
 * @ClassName: RecommendCourseDetailPager
 * @Description: 推荐课程
 * @Author: WangDe
 * @CreateDate: 2019/11/28 16:27
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/28 16:27
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RecommendCourseDetailPager extends BasePager {

    RecyclerView rvCourseDetail;
    ImageView ivClose;
    List<CourseEntity> mCourseEntities;
    private CourseDetailAdapter adapter;
    private CloseClickListener listener;

    public RecommendCourseDetailPager(Context context){
        super(context);
        mCourseEntities = new ArrayList<>();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_lightlive_recommend_course_detail, null);
        rvCourseDetail = mView.findViewById(R.id.rv_livevideo_lightlive_courses_detail);
        ivClose = mView.findViewById(R.id.iv_livevideo_lightlive_courses_close);
        rvCourseDetail.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        initListener();
        return mView;
    }

    @Override
    public void initData() {
        adapter = new CourseDetailAdapter(mCourseEntities);
        rvCourseDetail.setAdapter(adapter);

    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    public void updataView(List<CourseEntity> courseEntities){
        adapter.setData(courseEntities);
    }

    private class CourseDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<CourseEntity> mData;

        public CourseDetailAdapter(List<CourseEntity> mData) {
            this.mData = mData;
        }

        public void setData(List<CourseEntity> mData){
            this.mData = mData;
            notifyDataSetChanged();
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CourseDetailHolder(View.inflate(parent.getContext(), R.layout.item_recommend_course_card, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((CourseDetailHolder) holder).bindData(mData.get(position));
            ((CourseDetailHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到商城的订单详情页面
                    Bundle bundle = new Bundle();
                    bundle.putString("vCourseId", mData.get(position).getCourseId());
//                    bundle.putString("classId", mData.get(position).getClassID());
                    //采用ARouter来跳转
                    XueErSiRouter.startModule(mContext, "/xesmallCourseDetail/xrsmodule", bundle);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private class  CourseDetailHolder extends RecyclerView.ViewHolder{

        /** 课程名*/
        private TextView tvCourseName;
        /** 课程副标题*/
        private TextView tvSecondTitle;
        /** 课程难度*/
        private TextView tvDifficulty;
        /** 原价 */
        private TextView tvOriginalPrice;
        private TextView tvCurPrice;
        private View vMainTeacherContainer;
        private View vForeignTeacherContainer;
        private View vTutorTeacherContainer;
        /** 主讲老师名*/
        private TextView tvMainTeacherName;
        private TextView tvMainTeacherDesc;
        /** 主讲老师头像*/
        private ImageView ivMainTeacherHead;
        /** 外教老师头像*/
        private ImageView ivForeignTeacherHead;
        /** 外教老师名*/
        private TextView tvForeignTeacherName;
        /** 中教老师提示*/
        private TextView tvForeignTeacherDesc;
        /** 辅导老师提示*/
        private TextView tvTutorTeacherDesc;
        /** 辅导老师名*/
        private TextView tvTutorTeacherName;
        /** 辅导老师头像*/
        private ImageView ivTutorTeacherHead;
        /** 上课时间*/
        private TextView tvDate;
        private View vLine;
        /** 上课日期图标*/
        private Drawable imgShowTime;
        /** 是否限制课程名称最大行数*/
        private boolean nameMaxLineEnable;
        /** 课程名称最大行数*/
        private int nameMaxLine;
        private TextView tvStatus;
        private Context context;
        private CourseEntity entity;
        private final String COURSE_IS_FULL = "1";

        public CourseDetailHolder(View itemView) {
            super(itemView);
            this.context =mContext;
            tvCourseName = itemView.findViewById(R.id.tv_course_card_name);
            tvSecondTitle = itemView.findViewById(R.id.tv_course_card_second_title);

            tvDifficulty = itemView.findViewById(R.id.tv_course_card_difficult);
            tvDate = itemView.findViewById(R.id.tv_course_card_date);

            vMainTeacherContainer = itemView.findViewById(R.id.rl_course_card_main_teacher_container);
            ivMainTeacherHead = itemView.findViewById(R.id.iv_course_card_main_teacher_head);
            tvMainTeacherName = itemView.findViewById(R.id.tv_course_card_main_teacher_name);
            tvMainTeacherDesc = itemView.findViewById(R.id.tv_course_card_main_teacher_desc);

            vForeignTeacherContainer = itemView.findViewById(R.id.rl_course_card_foreign_teacher_container);
            ivForeignTeacherHead = itemView.findViewById(R.id.iv_course_card_foreign_teacher_head);
            tvForeignTeacherName = itemView.findViewById(R.id.tv_course_card_foreign_teacher_name);
            tvForeignTeacherDesc = itemView.findViewById(R.id.tv_course_card_foreign_teacher_desc);

            vTutorTeacherContainer = itemView.findViewById(R.id.rl_course_card_tutor_teacher_container);
            ivTutorTeacherHead = itemView.findViewById(R.id.iv_course_card_tutor_teacher_head);
            tvTutorTeacherName = itemView.findViewById(R.id.tv_course_card_tutor_teacher_name);
            tvTutorTeacherDesc = itemView.findViewById(R.id.tv_course_card_tutor_teacher_desc);

            vLine = itemView.findViewById(R.id.v_course_card_line);
            tvStatus = itemView.findViewById(R.id.tv_course_card_status);
            tvOriginalPrice = itemView.findViewById(R.id.tv_course_card_original_price);
            tvCurPrice = itemView.findViewById(R.id.tv_course_card_price);
        }

        public void bindData(CourseEntity entity){
            this.entity = entity;
            // 课程名
            setCourseName();
            // 上课时间
            setCourseDate();
            // 课程副标题
            setCourseSecondTitle();
            //课程难度
            setCourseDifficulty();
            // 老师信息
            setCourseTeacher();
            // 设置停售天数和报满状态
            setCourseStatus();
            // 设置课程原价
            setCourseOriginPrice();
            // 课程价格
            setCoursePrice();
        }
        /**
         * 课程名
         */
        private void setCourseName() {
            //设置名称和学科，学期
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            if (entity.isPreSale()) {
//            Drawable preSale = BusinessUtils.createDrawable("预售", getColor(R.color.COLOR_FF5E50), getColor(R.color.COLOR_FFFFFF));
                Drawable preSale = DrawUtil.create("预售", R.color.COLOR_FF5E50, R.color.COLOR_FFFFFF);
                VericalImageSpan imgSpan = new VericalImageSpan(preSale);
                SpannableString spannableString = new SpannableString("sl  ");
                spannableString.setSpan(imgSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append(spannableString);
            }

            if (entity.isGroupOn()) {
//            Drawable groupon = BusinessUtils.createDrawable("拼团", getColor(R.color.COLOR_FF5E50), getColor(R.color.COLOR_FFFFFF));
                Drawable groupon = DrawUtil.create("拼团", R.color.COLOR_FF5E50, R.color.COLOR_FFFFFF);
                VericalImageSpan span = new VericalImageSpan(groupon);
                SpannableString spannableString = new SpannableString("go  ");
                spannableString.setSpan(span, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append(spannableString);
            }
            if (entity.getSubJects() != null && entity.getSubJects().size() > 0){
                for (int i = 0; i < entity.getSubJects().size(); i++) {
                    String name = entity.getSubJects().get(i).getName();
                    if (!TextUtils.isEmpty(name)) {
//            Drawable subjectDrawable = BusinessUtils.createDrawable(entity.getSubjectName(), getColor(R.color.COLOR_5E617C), getColor(R.color.COLOR_FFFFFF));
                        Drawable subjectDrawable = DrawUtil.create(name, R.color.COLOR_5E617C, R.color.COLOR_FFFFFF);
                        VericalImageSpan imgSpan = new VericalImageSpan(subjectDrawable);
                        SpannableString spannableString = new SpannableString("xk ");
                        spannableString.setSpan(imgSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder.append(spannableString);
                    }
                }
            }

            if (!TextUtils.isEmpty(entity.getCourseName())) {
                stringBuilder.append(entity.getCourseName());
            }
            tvCourseName.setText(stringBuilder);

            if (nameMaxLineEnable && nameMaxLine > 0) {
                tvCourseName.setMaxLines(nameMaxLine);
                tvCourseName.setEllipsize(TextUtils.TruncateAt.END);
            }
        }


        private void setCourseSecondTitle() {
            if (TextUtils.isEmpty(entity.getSecondTitle())) {
                tvSecondTitle.setVisibility(View.GONE);
            } else {
                tvSecondTitle.setVisibility(View.VISIBLE);
                tvSecondTitle.setText(entity.getSecondTitle());
            }
        }

        /**
         * 课程上课时间
         */
        private void setCourseDate() {
            String time = entity.getLiveShowTime();
            if (TextUtils.isEmpty(time) || TextUtils.isEmpty(time.trim())) {
                time = entity.getChapterCount();
            } else {
                time = time.trim() + context.getString(R.string.text_str_dot) + entity.getChapterCount();
            }
            if (TextUtils.isEmpty(time)) {
                tvDate.setVisibility(View.GONE);
            } else {
                tvDate.setText(time);
                tvDate.setVisibility(View.VISIBLE);
            }
        }

        private void setCourseDifficulty() {
            if (entity.getCourseDifficulity() <= 0) {
                tvDifficulty.setVisibility(View.GONE);
            } else {
                tvDifficulty.setVisibility(View.VISIBLE);
                tvDifficulty.setText("难度" + entity.getCourseDifficulity() + "星");
            }
        }


        /**
         * 设置老师信息
         */
        private void setCourseTeacher() {

            // 主讲老师信息
            ArrayList<CourseTeacherEntity> mainTeacherEntities = entity.getLstMainTeacher();
            ArrayList<CourseTeacherEntity> foreignTeacherEntities = entity.getLstForeignTeacher();
            ArrayList<CourseTeacherEntity> coachTeacherEntities = entity.getLstCoachTeacher();
            boolean hasThree = (coachTeacherEntities != null && coachTeacherEntities.size() > 0)
                    && (mainTeacherEntities != null && mainTeacherEntities.size() > 0)
                    && (foreignTeacherEntities != null && foreignTeacherEntities.size() > 0);
            if (mainTeacherEntities != null && mainTeacherEntities.size() > 0) {
                vMainTeacherContainer.setVisibility(View.VISIBLE);
                CourseTeacherEntity courseMallTeacherEntity = mainTeacherEntities.get(0);
                String name = SubjectUtil.subName(courseMallTeacherEntity.getTeacherName(),hasThree);
                if (mainTeacherEntities.size() > 1) {
                    name += "等";
                }
                tvMainTeacherName.setText(name);
                tvMainTeacherDesc.setText(courseMallTeacherEntity.getTeacherHint());
                // 设置主教老师头像
                if (TextUtils.isEmpty(courseMallTeacherEntity.getTeacherImg())){
                    ivMainTeacherHead.setImageResource(R.drawable.bg_main_default_head_image);
                }else {
                    ImageLoader.with(context).load(courseMallTeacherEntity.getTeacherImg()).placeHolder(R.drawable
                            .bg_main_default_head_image)
                            .error(R.drawable.bg_main_default_head_image).into(ivMainTeacherHead);
                }


            } else {
                vMainTeacherContainer.setVisibility(View.GONE);
            }
            // 有外教老师
            if (foreignTeacherEntities != null && foreignTeacherEntities.size() > 0) {
                vForeignTeacherContainer.setVisibility(View.VISIBLE);
                CourseTeacherEntity courseMallTeacherEntity = foreignTeacherEntities.get(0);
                String name = SubjectUtil.subName(courseMallTeacherEntity.getTeacherName(),hasThree);
                if (foreignTeacherEntities.size() > 1) {
                    name += "等";
                }
                tvForeignTeacherName.setText(name);
                tvForeignTeacherDesc.setText(courseMallTeacherEntity.getTeacherHint());
                // 设置老师头像
                if (TextUtils.isEmpty(courseMallTeacherEntity.getTeacherImg())){
                    ivForeignTeacherHead.setImageResource(R.drawable.bg_foreign_default_head_image);
                }else {
                    ImageLoader.with(context).load(courseMallTeacherEntity.getTeacherImg()).placeHolder(R.drawable
                            .bg_foreign_default_head_image)
                            .error(R.drawable.bg_foreign_default_head_image).into(ivForeignTeacherHead);
                }

            } else {
                vForeignTeacherContainer.setVisibility(View.GONE);
            }

            // 辅导老师信息  报名已满的时候也不显示辅导信息
            if (coachTeacherEntities != null && coachTeacherEntities.size() > 0) {
                vTutorTeacherContainer.setVisibility(View.VISIBLE);
                CourseTeacherEntity courseMallTeacherEntity = coachTeacherEntities.get(0);
                String teacherName = entity.isExcTeacherCourse() ? "专属老师": SubjectUtil.subName(courseMallTeacherEntity.getTeacherName(),hasThree);
                tvTutorTeacherName.setText(teacherName);
                if (entity.isExcTeacherCourse()) {
                    tvTutorTeacherDesc.setText("全程陪伴");
                } else if (!TextUtils.isEmpty(entity.getRemainPeople()) && !"0".equals(entity.getRemainPeople()) && entity.getRemainPeople().length() < 6) {
                    // 剩余名额
                    tvTutorTeacherDesc.setText(courseMallTeacherEntity.getTeacherHint() + " 余" + entity.getRemainPeople() + "个名额");
                } else {
                    tvTutorTeacherDesc.setText(courseMallTeacherEntity.getTeacherHint());
                }
                // 设置辅导老师头像
                if (TextUtils.isEmpty(courseMallTeacherEntity.getTeacherImg())){
                    ivTutorTeacherHead.setImageResource(R.drawable.bg_tutor_default_head_imge);
                }else {
                    ImageLoader.with(context).load(courseMallTeacherEntity.getTeacherImg()).placeHolder(R.drawable
                            .bg_tutor_default_head_imge)
                            .error(R.drawable.bg_tutor_default_head_imge).into(ivTutorTeacherHead);
                }

            } else {
                vTutorTeacherContainer.setVisibility(View.GONE);
            }
        }

        private void setCourseStatus() {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String status = "";

            // 课程讲数
//        if (!TextUtils.isEmpty(entity.getChapterCount())) {
//            status = entity.getChapterCount();
//        }

            // 授课停售天数
            if (!TextUtils.isEmpty(entity.getDeadTime())) {
                status = entity.getDeadTime();
            }

            // 是否报满
            if (COURSE_IS_FULL.equals(entity.getIsFull())) {
                status = "已报满";
            }

            if (TextUtils.isEmpty(status)) {
                tvStatus.setVisibility(View.GONE);
            } else {
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(status);
            }
        }

        /**
         * 课程原价
         */
        private void setCourseOriginPrice() {
            SpannableStringBuilder total = new SpannableStringBuilder();
            if (entity.getCourseOrignPrice() > 0 && (entity.getCoursePrice() < entity.getCourseOrignPrice())) {
                SpannableString originalSpan = new SpannableString("¥" + entity.getCourseOrignPrice());
                // 删除线
                StrikethroughSpan deleteSpan = new StrikethroughSpan();
                originalSpan.setSpan(deleteSpan, 0, originalSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                total.append(originalSpan);
            }
            if (total.length() > 0) {
                tvOriginalPrice.setVisibility(View.VISIBLE);
                tvOriginalPrice.setText(total);
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
            }
        }

        private void setCoursePrice() {
            tvCurPrice.setText("¥" + entity.getCoursePrice());
        }
    }

    public void setCloseListener(CloseClickListener listener) {
        this.listener = listener;
    }

    public interface CloseClickListener{
        void onClick();
    }


}
