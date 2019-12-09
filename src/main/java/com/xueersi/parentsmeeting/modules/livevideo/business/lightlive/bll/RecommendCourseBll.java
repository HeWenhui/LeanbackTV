package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.RecommendCourseDetailPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.RecommendCoursePager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: RecommendCourseBll
 * @Description: 推荐课程
 * @Author: WangDe
 * @CreateDate: 2019/11/28 14:36
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/28 14:36
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RecommendCourseBll extends LiveBaseBll {

    RecommendCoursePager mCoursePager;
    RecommendCourseDetailPager mDetailPager;
    /** 全屏布局*/
    private RelativeLayout contentLayout;
    /** 竖屏下视频和聊天中间布局*/
    private LinearLayout middleLayout;
    private boolean isDetailShow;
    List<CourseEntity> courseEntities;
    private LightLiveHttpManager mHttpManager;
    private LightLiveHttpResponseParser mHttpResponseParser;

    public RecommendCourseBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        courseEntities = new ArrayList<>();
        mCoursePager = new RecommendCoursePager(context);
        mDetailPager = new RecommendCourseDetailPager(context);
        mHttpManager = new LightLiveHttpManager(getHttpManager());
        mHttpResponseParser = new LightLiveHttpResponseParser();
//        for (int i = 0; i < 10; i++) {
//            CourseEntity entity = new CourseEntity();
//            entity.setCourseName(i+"世界冠军王鹰豪-魔方课开课了"+i);
//            entity.setCourseId("787897");
//            entity.setChapterCount("2");
//            entity.setClassID("444"+i);
//            entity.setCoursePrice(500);
//            entity.setCourseOrignPrice(1000);
//            entity.setDeadTime("2019.12.2");
//            entity.setCourseDifficulity(i+1);
//            entity.setIsFull("0");
//            entity.setGroupon(false);
//            entity.setSubjectName("语文");
//            entity.setSecondTitle("清华大学");
//            entity.setLiveShowTime("三期：2月9日-2月12日 每天 13:00-14:00 · 共45讲");
//            entity.setRemainPeople(""+30+i);
//            ArrayList<CourseTeacherEntity> teacherEntities = new ArrayList<>();
//            CourseTeacherEntity entity1 = new CourseTeacherEntity("张三" + i);
//            entity1.setTeacherHint("dd");
//            teacherEntities.add(entity1);
//            entity.setLstMainTeacher(teacherEntities);
//            courseEntities.add(entity);
//        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        getCourseList();
    }

    @Override
    public void initView() {
        middleLayout = mContentView.findViewById(R.id.ll_course_video_live_other_content);
        contentLayout = mContentView.findViewById(R.id.rl_course_video_live_content);
        if (contentLayout != null){
            contentLayout.setClickable(false);
        }
        if (mCoursePager != null && mCoursePager.getRootView() != null && middleLayout != mCoursePager.getRootView().getParent()){
            middleLayout.addView(mCoursePager.getRootView(),middleLayout.getChildCount());
            ViewGroup.LayoutParams params = mCoursePager.getRootView().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = SizeUtils.Dp2Px(mContext,96);
        }
        if(!mIsLand.get()){
            middleLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);
            isDetailShow = false;
        }else {
            if (middleLayout != null){
                middleLayout.setVisibility(View.GONE);
            }
            if (contentLayout != null){
                contentLayout.setVisibility(View.GONE);
            }
        }
        mCoursePager.setData(courseEntities);
        initListener();
        super.initView();
    }

    private void initListener(){
        mCoursePager.setMoreClickListener(new RecommendCoursePager.MoreCouponClickListener() {
            @Override
            public void onClick() {
                if (!isDetailShow){
                    XrsBury.clickBury(mContext.getResources().getString(R.string.click_03_63_012));
                    contentLayout.addView(mDetailPager.getRootView());
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDetailPager.getRootView().getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.height = SizeUtils.Dp2Px(mContext,476);
                    mDetailPager.getRootView().setLayoutParams(params);
                    contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_80000000));
                    contentLayout.setClickable(true);
                    mDetailPager.updataView(courseEntities);
                    XrsBury.showBury(mContext.getResources().getString(R.string.show_03_63_009));
                }
                isDetailShow = true;
            }
        });
        mDetailPager.setCloseListener(new RecommendCourseDetailPager.CloseClickListener() {
            @Override
            public void onClick() {
                contentLayout.removeView(mDetailPager.getRootView());
                contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_00000000));
                contentLayout.setClickable(false);
                isDetailShow = false;
                XrsBury.clickBury(mContext.getResources().getString(R.string.click_03_63_008));
            }
        });
    }
    private void getCourseList(){
        mHttpManager.getCourseList(mGetInfo.getId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                courseEntities = mHttpResponseParser.parserCourseList(responseEntity);
                mCoursePager.setData(courseEntities);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
