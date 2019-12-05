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
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.DiscountCouponDetailPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.DiscountCouponPager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.share.business.login.LoginActionEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: DiscountCouponBll
 * @Description: 轻直播优惠券
 * @Author: WangDe
 * @CreateDate: 2019/11/25 19:05
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/25 19:05
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DiscountCouponBll extends LiveBaseBll {

    /** 竖屏下视频和聊天中间布局*/
    private LinearLayout middleLayout;
    /** 全屏布局*/
    private RelativeLayout contentLayout;
    /** 优惠券缩率页面*/
    private DiscountCouponPager discountCouponPager;
    /** 优惠券详细页面*/
    private DiscountCouponDetailPager detailPager;
    private boolean isDetailShow;
    /** 优惠券*/
    private List<CouponEntity> couponEntities;
    private LightLiveHttpManager mHttpManager;
    private LightLiveHttpResponseParser mHttpResponseParser;
    private String liveId;

    public DiscountCouponBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        discountCouponPager = new DiscountCouponPager(activity);
        detailPager = new DiscountCouponDetailPager(activity);
        mHttpManager = new LightLiveHttpManager(getHttpManager());
        mHttpResponseParser = new LightLiveHttpResponseParser();
        couponEntities = new ArrayList<>();
        EventBusUtil.register(this);

        //测试环境
//        for (int i = 0; i < 10; i++) {
//            CouponEntity e1 = new CouponEntity();
//            e1.setFaceText(i+10+"");
//            e1.setMoneyIcon("￥");
//            e1.setName("高二行知奖学金（限寒春课程使用）");
//            e1.setStatus(i%3);
//            e1.setReduceText("满1000可用");
//            e1.setValidDate("2019.10.1--2019.12.1");
//            e1.setButtonText("立即领取");
//            e1.setTitle("满1000减100");
//            couponEntities.add(e1);
//        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        liveId = getInfo.getId();
        getCouponList(true);
    }

    @Override
    public void initView() {
        middleLayout = mContentView.findViewById(R.id.ll_course_video_live_other_content);
        contentLayout = mContentView.findViewById(R.id.rl_course_video_live_content);
        if (contentLayout != null){
            contentLayout.setClickable(false);
        }
        if (discountCouponPager != null && discountCouponPager.getRootView() != null && middleLayout != discountCouponPager.getRootView().getParent()){
            middleLayout.addView(discountCouponPager.getRootView(),0);
            ViewGroup.LayoutParams params = discountCouponPager.getRootView().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = SizeUtils.Dp2Px(mContext,55);
        }
        if(!mIsLand.get()){
            middleLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);
//            contentLayout.removeAllViews();
            isDetailShow = false;
        }else {
            if (middleLayout != null){
                middleLayout.setVisibility(View.GONE);
            }
            if (contentLayout != null){
                contentLayout.setVisibility(View.GONE);
            }
        }
        discountCouponPager.setData(couponEntities);
        if (couponEntities != null && !couponEntities.isEmpty()){
            XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_003));
        }
        initListener();
        super.initView();
    }

    private void initListener(){
        discountCouponPager.setMoreClickListener(new DiscountCouponPager.MoreCouponClickListener() {
            @Override
            public void onClick() {
                if (!isDetailShow){
                    XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_005));
                    contentLayout.addView(detailPager.getRootView());
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) detailPager.getRootView().getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.height = SizeUtils.Dp2Px(mContext,476);
                    detailPager.getRootView().setLayoutParams(params);
                    contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_80000000));
                    contentLayout.setClickable(true);
                    detailPager.updataView(couponEntities);
                    XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_004));
                }
                isDetailShow = true;
            }
        });

        detailPager.setCloseListener(new DiscountCouponDetailPager.CloseClickListener() {
            @Override
            public void onClick() {
                contentLayout.removeView(detailPager.getRootView());
                contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_00000000));
                contentLayout.setClickable(false);
                isDetailShow = false;
                XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_006));
            }
        });

        detailPager.setCouponClickListener(new DiscountCouponDetailPager.GetCouponClickListener() {
            @Override
            public void onClick(final String couponId) {
                if (AppBll.getInstance().isAlreadyLogin()){
                    mHttpManager.getCouponGet(couponId, new HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                            if (jsonObject != null) {
                                int status = jsonObject.optInt("status");
                                String toast = jsonObject.optString("tip");
                                XESToastUtils.showToastAtCenter(toast);
                                XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_007),couponId, status== 1 ? 0:1);
                            }
                            getCouponList(false);
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            XESToastUtils.showToastAtCenter(msg);
                            XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_007),couponId,1);
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            XESToastUtils.showToastAtCenter(responseEntity.getErrorMsg());
                            XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_007),couponId,1);
                        }
                    });
                } else {
                    XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_005),"");
                    LoginEnter.openLogin(mContext,false,new Bundle());
                }
            }
        });
    }

    /** 获取优惠券列表*/
    private void getCouponList(final boolean isClear){

        mHttpManager.getCouponList(liveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                couponEntities = mHttpResponseParser.parserCouponList(responseEntity);
                if (discountCouponPager != null){
                    discountCouponPager.setData(couponEntities);
                    if (isClear && couponEntities != null && !couponEntities.isEmpty()){
                        XrsBury.showBury(mContext.getResources().getString(R.string.livevideo_show_03_32_003));
                    }
                }
                if (detailPager != null){
                    detailPager.updataView(couponEntities);
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

    @Override
    public void onDestroy() {
        EventBusUtil.unregister(toString());
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerResult(LoginActionEvent event) {
        if (event.isAlreadyLogin()){
            getCouponList(false);
        }
    }
}
