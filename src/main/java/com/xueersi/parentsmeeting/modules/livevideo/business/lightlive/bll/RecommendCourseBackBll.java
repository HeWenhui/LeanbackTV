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
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.RecommendCourseDetailPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.RecommendCoursePager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: RecommendCourseBackBll
 * @Description: 轻直播回放推荐课程
 * @Author: WangDe
 * @CreateDate: 2019/12/27 11:08
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/27 11:08
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RecommendCourseBackBll extends LiveBackBaseBll {

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

    public RecommendCourseBackBll(Activity activity, LiveBackBll liveBackBll){
        super(activity,liveBackBll);
        courseEntities = new ArrayList<>();
        mCoursePager = new RecommendCoursePager(activity,true);
        mDetailPager = new RecommendCourseDetailPager(activity,true);
        mHttpManager = new LightLiveHttpManager(getmHttpManager());
        mHttpResponseParser = new LightLiveHttpResponseParser();
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        getCourseList();
    }

    @Override
    public void initView() {
        middleLayout = getLiveViewAction().findViewById(R.id.ll_course_video_live_other_content);
        contentLayout = getLiveViewAction().findViewById(R.id.rl_course_video_live_content);
        if (contentLayout != null){
            contentLayout.setClickable(false);
        }
        if (mCoursePager != null && mCoursePager.getRootView() != null && middleLayout != mCoursePager.getRootView().getParent()){
            middleLayout.addView(mCoursePager.getRootView(),middleLayout.getChildCount());
            ViewGroup.LayoutParams params = mCoursePager.getRootView().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = SizeUtils.Dp2Px(mContext,112);
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
                    LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_008));
                    contentLayout.addView(mDetailPager.getRootView());
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDetailPager.getRootView().getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.height = SizeUtils.Dp2Px(mContext,476);
                    mDetailPager.getRootView().setLayoutParams(params);
                    contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_80000000));
                    contentLayout.setClickable(true);
                    mDetailPager.updataView(courseEntities);
                    LightLiveBury.showBury(mContext.getResources().getString(R.string.show_03_84_004));
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
                LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_011));
            }
        });
    }

    private void getCourseList(){
        mHttpManager.getCourseList(liveGetInfo.getId(), new HttpCallBack(false) {
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
