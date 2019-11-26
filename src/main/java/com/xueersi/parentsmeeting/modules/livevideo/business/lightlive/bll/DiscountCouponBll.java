package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.DiscountCouponPager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

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
    private List<String> mData;
    private DiscountCouponPager discountCouponPager;

    public DiscountCouponBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        discountCouponPager = new DiscountCouponPager(activity);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
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
        }else {
            if (bottomLayout != null){
                bottomLayout.setVisibility(View.GONE);
            }
        }
        mData = new ArrayList<String>();
        mData.add("满1000减50");
        mData.add("满100减10");
        mData.add("满1000减100");
        discountCouponPager.setData(mData);
        super.initView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
