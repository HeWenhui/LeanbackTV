package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.util.LoginEnter;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.config.BurySourceIds;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.DiscountCouponDetailPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.DiscountCouponPager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.share.business.login.LoginActionEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: DiscountCouponBackBll
 * @Description: 轻直播回放优惠券
 * @Author: WangDe
 * @CreateDate: 2019/12/27 10:09
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/27 10:09
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DiscountCouponBackBll extends LiveBackBaseBll {

    /** 竖屏下视频和聊天中间布局*/
    private LinearLayout middleLayout;
    /** 优惠券缩率页面*/
    private DiscountCouponPager discountCouponPager;
    /** 优惠券*/
    private List<CouponEntity> couponEntities;
    private LightLiveHttpManager mHttpManager;
    private LightLiveHttpResponseParser mHttpResponseParser;
    private String liveId;
    private boolean hasInit;

    public DiscountCouponBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        discountCouponPager = new DiscountCouponPager(activity);
        mHttpManager = new LightLiveHttpManager(getmHttpManager());
        mHttpResponseParser = new LightLiveHttpResponseParser();
        couponEntities = new ArrayList<>();
        EventBusUtil.register(this);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        liveId = liveGetInfo.getId();
        getCouponList(true);
    }

    @Override
    public void initView() {
        middleLayout = getLiveViewAction().findViewById(R.id.ll_course_video_live_other_content);
        if (discountCouponPager != null && discountCouponPager.getRootView() != null && middleLayout != discountCouponPager.getRootView().getParent()){
            middleLayout.addView(discountCouponPager.getRootView(),0);
            ViewGroup.LayoutParams params = discountCouponPager.getRootView().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = SizeUtils.Dp2Px(mContext,55);
        }
        if(!mIsLand.get()){
            middleLayout.setVisibility(View.VISIBLE);
        }else {
            if (middleLayout != null){
                middleLayout.setVisibility(View.GONE);
            }
        }
        discountCouponPager.setData(couponEntities);
        initListener();
        super.initView();
    }

    private void initListener(){

        if(!mIsLand.get() && !hasInit){
            hasInit = true;
            discountCouponPager.setMoreClickListener(new DiscountCouponPager.MoreCouponClickListener() {
                @Override
                public void onClick() {
                    XESToastUtils.showToastAtCenter("优惠券仅在直播时发放哦~");
                }
            });
            middleLayout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
                @Override
                public void onChildViewAdded(View parent, View child) {
                    if (child != discountCouponPager.getRootView()){
                        discountCouponPager.setLineVisible(false);
                    }
                }

                @Override
                public void onChildViewRemoved(View parent, View child) {

                }
            });
        }

    }

    /** 获取优惠券列表*/
    private void getCouponList(final boolean isClear){

        mHttpManager.getCouponList(liveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                couponEntities = mHttpResponseParser.parserCouponList(responseEntity);
                if (discountCouponPager != null){
                    discountCouponPager.setData(couponEntities);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                if(isClear && middleLayout != null){
                    for (int i = 0; i < middleLayout.getChildCount(); i++) {
                        if (middleLayout.getChildAt(i) == discountCouponPager.getRootView()){
                            middleLayout.removeView(discountCouponPager.getRootView());
                        }
                    }
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                if(isClear && middleLayout != null){
                    for (int i = 0; i < middleLayout.getChildCount(); i++) {
                        if (middleLayout.getChildAt(i) == discountCouponPager.getRootView()){
                            middleLayout.removeView(discountCouponPager.getRootView());
                        }
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerResult(LoginActionEvent event) {
        if (event.isAlreadyLogin()){
            getCouponList(false);
        }
    }
}
