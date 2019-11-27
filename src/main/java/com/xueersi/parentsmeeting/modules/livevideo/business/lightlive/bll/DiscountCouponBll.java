package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
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
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;

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
    private LinearLayout bottomLayout;
    /** 全屏布局*/
    private RelativeLayout contentLayout;
    private DiscountCouponPager discountCouponPager;
    private DiscountCouponDetailPager detailPager;
    private boolean isDetailShow;
    private List<CouponEntity> couponEntities;
    private LightLiveHttpManager mHttpManager;
    private LightLiveHttpResponseParser mHttpResponseParser;
    private boolean isNewData;

    public DiscountCouponBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        discountCouponPager = new DiscountCouponPager(activity);
        detailPager = new DiscountCouponDetailPager(activity);
        mHttpManager = new LightLiveHttpManager(getHttpManager());
        mHttpResponseParser = new LightLiveHttpResponseParser();
        couponEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CouponEntity e1 = new CouponEntity();
            e1.setFaceText(i+10+"");
            e1.setMoneyIcon("￥");
            e1.setName("高二行知奖学金（限寒春课程使用）");
            e1.setStatus(i%3);
            e1.setReduceText("满1000可用");
            e1.setValidDate("2019.10.1--2019.12.1");
            e1.setButtonText("立即领取");
            e1.setTitle("满1000减100");
            couponEntities.add(e1);
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mHttpManager.getCouponList(getInfo.getId(), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                String title = null;
                if (responseEntity != null) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    title = jsonObject.optString("title");
                }
                couponEntities = mHttpResponseParser.parserCouponList(responseEntity);
            }
        });
    }

    @Override
    public void initView() {
        bottomLayout = mContentView.findViewById(R.id.ll_course_video_live_other_content);
        contentLayout = mContentView.findViewById(R.id.rl_course_video_live_content);
        if (discountCouponPager != null && discountCouponPager.getRootView() != null && bottomLayout != discountCouponPager.getRootView().getParent()){
            bottomLayout.addView(discountCouponPager.getRootView(),0);
            ViewGroup.LayoutParams params = discountCouponPager.getRootView().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = SizeUtils.Dp2Px(mContext,55);
        }
        if(!mIsLand.get()){
            bottomLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.VISIBLE);
//            contentLayout.removeAllViews();
            isDetailShow = false;
        }else {
            if (bottomLayout != null){
                bottomLayout.setVisibility(View.GONE);
            }
            if (contentLayout != null){
                contentLayout.setVisibility(View.GONE);
            }
        }
        discountCouponPager.setData(couponEntities);
        initListener();
        super.initView();
    }

    private void initListener(){
        discountCouponPager.setMoreClickListener(new DiscountCouponPager.MoreCouponClickListener() {
            @Override
            public void onClick() {
                if (!isDetailShow){
                    contentLayout.addView(detailPager.getRootView());
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) detailPager.getRootView().getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.height = SizeUtils.Dp2Px(mContext,476);
                    detailPager.getRootView().setLayoutParams(params);
                    contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_80000000));
                    detailPager.updataView(couponEntities);
                }
                isDetailShow = true;
            }
        });

        detailPager.setCloseListener(new DiscountCouponDetailPager.CloseClickListener() {
            @Override
            public void onClick() {
                contentLayout.removeView(detailPager.getRootView());
                contentLayout.setBackground(mContext.getResources().getDrawable(R.color.COLOR_00000000));
                isDetailShow = false;
            }
        });

        detailPager.setCouponClickListener(new DiscountCouponDetailPager.GetCouponClickListener() {
            @Override
            public void onClick(String couponId) {
                mHttpManager.getCouponGet(couponId, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        if (jsonObject != null) {
                            String toast = jsonObject.optString("tip");
                            XESToastUtils.showToast(toast);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        XESToastUtils.showToast(msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        XESToastUtils.showToast(responseEntity.getErrorMsg());
                    }
                });

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
